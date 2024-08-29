package com.hp.biz.logger.function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.collect.Lists;
import com.hp.biz.logger.BizLoggerProperties;
import com.hp.biz.logger.annotation.BizDiffField;
import com.hp.biz.logger.annotation.BizLoggerComponent;
import com.hp.biz.logger.annotation.BizLoggerFunction;
import com.hp.biz.logger.expression.BizLoggerExpressionEvaluator;
import com.hp.biz.logger.factory.BizLoggerEvaluationContextFactory;
import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizDiffObjectWrapper;
import com.hp.common.base.utils.ParameterHelper;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.access.Accessor;
import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.TypeAwareAccessor;
import de.danielbechler.diff.comparison.ComparisonService;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.differ.Differ;
import de.danielbechler.diff.differ.DifferDispatcher;
import de.danielbechler.diff.identity.EqualsIdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategyResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.selector.CollectionItemElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Classes;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.AnnotatedElement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@BizLoggerComponent("javaObjectDiffBasedBizDifferComponent")
public class JavaObjectDiffBasedBizDifferComponent implements IBizDifferComponent {

    protected final BizLoggerProperties.DiffProperties diffProperties;
    protected final BeanResolver beanResolver;
    protected final ObjectProvider<JavaObjectDiffBasedTypeComparisonConfigurer> comparisonConfigurers;
    protected BizLoggerExpressionEvaluator bizLoggerExpressionEvaluator;

    public JavaObjectDiffBasedBizDifferComponent(
            BizLoggerProperties bizLoggerProperties,
            BeanFactory beanFactory,
            ObjectProvider<JavaObjectDiffBasedTypeComparisonConfigurer> comparisonConfigurers
    ) {
        this.diffProperties = bizLoggerProperties.getDiffConfig();
        this.beanResolver = new BeanFactoryResolver(beanFactory);
        this.comparisonConfigurers = comparisonConfigurers;
    }

    @BizLoggerFunction("BIZ_DIFFER")
    @Override
    public Collection<BizDiffDTO> diff(@Nullable BizDiffObjectWrapper diffObjectWrapper) {
        if (Objects.isNull(diffObjectWrapper)) {
            return Collections.emptyList();
        }
        final Object before = diffObjectWrapper.getInitValue();
        final Object after = diffObjectWrapper.getChangedValue();
        final DiffNode rootNode = getObjectDiffer().compare(after, before);
        if (!rootNode.hasChanges()) {
            return Collections.emptyList();
        }
        return createDiffLogs(before, after, rootNode);
    }

    @NonNull
    protected ObjectDiffer getObjectDiffer() {
        final ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding();
        final ObjectDifferBuilder register = objectDifferBuilder
                .differs()
                // Array differ capability. It's basically a rip-off of the CollectionDiffer.
                .register((differDispatcher, nodeQueryService) -> new ArrayDiffer(differDispatcher, (ComparisonService) objectDifferBuilder.comparison(), objectDifferBuilder.identity()));

        comparisonConfigurers.orderedStream()
                .forEach(conf-> ParameterHelper.Collections.ofNullable(conf.useEquals())
                        .ifPresent(classes-> classes.forEach(klass-> register.comparison().ofType(klass).toUseEqualsMethod()))
                );

        return register.build();
    }

    @NonNull
    protected Collection<BizDiffDTO> createDiffLogs(Object before, Object after, DiffNode rootNode) {
        final List<BizDiffDTO> diffs = Lists.newArrayList();
        rootNode.visitChildren((diffNode, visit) -> {
            // Do not process root node or nodes without changes.
            if (!diffNode.hasChanges()) {
                return;
            }
            if (diffNode.hasChildren()) {
                // Processing Array or Collection explicitly.
                if (isArrayOrCollection(diffNode)) {
                    createForArrayOrCollection(before, after, diffNode).ifPresent(diffs::add);
                }
                return;
            }
            if (isArrayOrCollectionRecursively(diffNode.getParentNode())) {
                visit.dontGoDeeper();
                return;
            }
            createForDiffNode(before, after, diffNode).ifPresent(diffs::add);
        });
        return diffs;
    }

    protected boolean isArrayOrCollectionRecursively(DiffNode diffNode) {
        DiffNode node = diffNode;
        while (node != null) {
            if (isArrayOrCollection(node)) {
                return true;
            }
            node = node.getParentNode();
        }
        return false;
    }

    protected Optional<BizDiffDTO> createForArrayOrCollection(Object before, Object after, DiffNode diffNode) {
        final List<BizDiffDTO> childDiffs = Lists.newArrayListWithCapacity(diffNode.childCount());
        diffNode.visitChildren((childNode, childVisit) -> {
            if (!Classes.isSimpleType(childNode.getValueType())) {
                childVisit.dontGoDeeper();
            }
            createForDiffNode(before, after, childNode).ifPresent(childDiffs::add);
        });
        if (CollUtil.isEmpty(childDiffs)) {
            return Optional.empty();
        }
        final List<BizDiffDTO.DiffAction> childActions = childDiffs.stream()
                .flatMap(diffDTO -> diffDTO.getActions().stream())
                .filter(diffDTO -> diffDTO.getState().meaningful())
                .toList();
        return createForDiffNode(before, after, diffNode)
                .map(i -> {
                    i.setActions(groupingActions(childActions));
                    return i;
                });
    }

    // Users can rewrite this method to avoid grouping.
    protected List<BizDiffDTO.DiffAction> groupingActions(List<BizDiffDTO.DiffAction> actions) {
        return actions.stream()
                .collect(Collectors.groupingBy(BizDiffDTO.DiffAction::getState))
                .entrySet()
                .stream()
                .map(entry ->
                        new BizDiffDTO.DiffAction(
                                entry.getKey(),
                                groupingActionValues(entry.getValue(), BizDiffDTO.DiffAction::getBefore),
                                groupingActionValues(entry.getValue(), BizDiffDTO.DiffAction::getAfter)
                        )
                )
                .toList();
    }

    // Group elements with the same state
    protected String groupingActionValues(List<BizDiffDTO.DiffAction> diffActions, Function<BizDiffDTO.DiffAction, String> mapper) {
        final String values = diffActions.stream()
                .map(mapper)
                .filter(StrUtil::isNotEmpty)
                .collect(Collectors.joining(StrUtil.COMMA));
        return StrUtil.isEmpty(values) ? null : values;
    }

    protected boolean isArrayOrCollection(DiffNode diffNode) {
        return !diffNode.getValueType().isPrimitive() && (diffNode.getValueType().isArray()) || Collection.class.isAssignableFrom(diffNode.getValueType());
    }

    protected Optional<BizDiffDTO> createForDiffNode(Object before, Object after, DiffNode diffNode) {
        final BizDiffDTO diffDTO = new BizDiffDTO(diffNode.getPropertyName(), this.getTraversalName(diffNode));

        final BizDiffField bizDiffField = diffNode.getFieldAnnotation(BizDiffField.class);

        final BizDiffDTO.DiffAction action = new BizDiffDTO.DiffAction(
                diffNode.getState().name(),
                Optional.ofNullable(diffNode.canonicalGet(before)).map(Objects::toString).orElse(null),
                Optional.ofNullable(diffNode.canonicalGet(after)).map(Objects::toString).orElse(null)
        );
        if (Objects.nonNull(bizDiffField) && StrUtil.isNotEmpty(bizDiffField.value())) {
            action.setParsedBefore(Optional.ofNullable(getExpressionValue(bizDiffField, before, diffNode)).map(Object::toString).orElse(null));
            action.setParsedAfter(Optional.ofNullable(getExpressionValue(bizDiffField, after, diffNode)).map(Object::toString).orElse(null));
        }
        diffDTO.setActions(Collections.singletonList(action));

        if (Objects.isNull(bizDiffField) || !bizDiffField.ignored()) {
            return Optional.of(diffDTO);
        }
        return Optional.empty();
    }

    protected Object getExpressionValue(BizDiffField bizDiffField, Object object, DiffNode diffNode) {
        final String expression = bizDiffField.value();
        if (StrUtil.isEmpty(expression)) {
            return object;
        }

        final Class<?> targetClass = object.getClass();
        final AnnotatedElement field = ClassUtil.getDeclaredField(targetClass, diffNode.getPropertyName());
        final EvaluationContext evaluationContext = BizLoggerEvaluationContextFactory.createStandardEvaluationContext(object, beanResolver);

        return lazyLoadExpressionEvaluator().getValue(expression, targetClass, field, evaluationContext);
    }

    // To avoid the circular reference issue.
    protected BizLoggerExpressionEvaluator lazyLoadExpressionEvaluator() {
        if (this.bizLoggerExpressionEvaluator != null) {
            return this.bizLoggerExpressionEvaluator;
        }
        final IBizLoggerFunctionResolver bizLoggerFunctionResolver = SpringUtil.getBean(IBizLoggerFunctionResolver.class);
        this.bizLoggerExpressionEvaluator = new BizLoggerExpressionEvaluator(
                bizLoggerFunctionResolver,
                new SpelExpressionParser()
        );
        return this.bizLoggerExpressionEvaluator;
    }

    @NonNull
    protected String getTraversalName(@NonNull DiffNode node) {
        String name = Optional.ofNullable(node.getFieldAnnotation(BizDiffField.class))
                .map(BizDiffField::alias)
                .orElse(node.getPropertyName());
        DiffNode parent = node.getParentNode();
        while (parent != null) {
            if (parent.isRootNode()) {
                parent = parent.getParentNode();
                continue;
            }
            String finalName = name;
            DiffNode finalParent = parent;
            name = Optional.ofNullable(parent.getFieldAnnotation(BizDiffField.class))
                    .map(annotation -> annotation.alias().concat(diffProperties.getWords().getOf()).concat(finalName))
                    .orElse(finalParent.getPropertyName().concat(diffProperties.getWords().getOf()).concat(finalName));
            parent = parent.getParentNode();
        }
        return name;
    }

    protected static class ArrayDiffer implements Differ {
        private final DifferDispatcher differDispatcher;
        private final ComparisonStrategyResolver comparisonStrategyResolver;
        private final IdentityStrategyResolver identityStrategyResolver;

        public ArrayDiffer(final DifferDispatcher differDispatcher,
                           final ComparisonStrategyResolver comparisonStrategyResolver,
                           final IdentityStrategyResolver identityStrategyResolver) {
            Assert.notNull(differDispatcher, "differDispatcher");
            this.differDispatcher = differDispatcher;

            Assert.notNull(comparisonStrategyResolver, "comparisonStrategyResolver");
            this.comparisonStrategyResolver = comparisonStrategyResolver;

            Assert.notNull(identityStrategyResolver, "identityStrategyResolver");
            this.identityStrategyResolver = identityStrategyResolver;
        }

        @Override
        public boolean accepts(Class<?> type) {
            return !type.isPrimitive() && type.isArray();
        }

        @Override
        public DiffNode compare(DiffNode parentNode, Instances arrayInstances) {
            final DiffNode arrayNode = newNode(parentNode, arrayInstances);
            final IdentityStrategy identityStrategy = identityStrategyResolver.resolveIdentityStrategy(arrayNode);
            if (identityStrategy != null) {
                arrayNode.setChildIdentityStrategy(identityStrategy);
            }
            if (arrayInstances.hasBeenAdded()) {
                final Collection<?> addedItems = arrayAsCollection(arrayInstances.getWorking());
                compareItems(arrayNode, arrayInstances, addedItems, identityStrategy);
                arrayNode.setState(DiffNode.State.ADDED);
            } else if (arrayInstances.hasBeenRemoved()) {
                final Collection<?> removedItems = arrayAsCollection(arrayInstances.getBase());
                compareItems(arrayNode, arrayInstances, removedItems, identityStrategy);
                arrayNode.setState(DiffNode.State.REMOVED);
            } else if (arrayInstances.areSame()) {
                arrayNode.setState(DiffNode.State.UNTOUCHED);
            } else {
                final ComparisonStrategy comparisonStrategy = comparisonStrategyResolver.resolveComparisonStrategy(arrayNode);
                if (comparisonStrategy == null) {
                    compareInternally(arrayNode, arrayInstances, identityStrategy);
                } else {
                    compareUsingComparisonStrategy(arrayNode, arrayInstances, comparisonStrategy);
                }
            }
            return arrayNode;
        }

        private static Collection<?> arrayAsCollection(Object object) {
            return object == null ? Lists.newArrayList() : Lists.newArrayList((Object[]) object);
        }

        private static DiffNode newNode(DiffNode parentNode, Instances arrayInstances) {
            final Accessor accessor = arrayInstances.getSourceAccessor();
            final Class<?> type = arrayInstances.getType();
            return new DiffNode(parentNode, accessor, type);
        }

        private void compareItems(final DiffNode arrayNode,
                                  final Instances arrayInstances,
                                  final Iterable<?> items,
                                  final IdentityStrategy identityStrategy) {
            for (final Object item : items) {
                final Accessor itemAccessor = new ArrayItemAccessor(item, identityStrategy);
                differDispatcher.dispatch(arrayNode, arrayInstances, itemAccessor);
            }
        }

        private void compareInternally(final DiffNode arrayNode,
                                       final Instances arrayInstances,
                                       final IdentityStrategy identityStrategy) {
            final Collection<?> working = arrayAsCollection(arrayInstances.getWorking());
            final Collection<?> base = arrayAsCollection(arrayInstances.getBase());

            final Iterable<?> added = new LinkedList<Object>(working);
            final Iterable<?> removed = new LinkedList<Object>(base);
            final Iterable<?> known = new LinkedList<Object>(base);

            remove(added, base, identityStrategy);
            remove(removed, working, identityStrategy);
            remove(known, added, identityStrategy);
            remove(known, removed, identityStrategy);

            compareItems(arrayNode, arrayInstances, added, identityStrategy);
            compareItems(arrayNode, arrayInstances, removed, identityStrategy);
            compareItems(arrayNode, arrayInstances, known, identityStrategy);
        }

        private static void compareUsingComparisonStrategy(final DiffNode arrayNode,
                                                           final Instances arrayInstances,
                                                           final ComparisonStrategy comparisonStrategy) {
            comparisonStrategy.compare(arrayNode,
                    arrayInstances.getType(),
                    arrayInstances.getWorking(Collection.class),
                    arrayInstances.getBase(Collection.class));
        }

        private void remove(final Iterable<?> from, final Iterable<?> these, final IdentityStrategy identityStrategy) {
            final Iterator<?> iterator = from.iterator();
            while (iterator.hasNext()) {
                final Object item = iterator.next();
                if (contains(these, item, identityStrategy)) {
                    iterator.remove();
                }
            }
        }

        private boolean contains(final Iterable<?> haystack, final Object needle, final IdentityStrategy identityStrategy) {
            for (final Object item : haystack) {
                if (identityStrategy.equals(needle, item)) {
                    return true;
                }
            }
            return false;
        }
    }

    protected static class ArrayItemAccessor implements TypeAwareAccessor, Accessor {
        private final Object referenceItem;
        private final IdentityStrategy identityStrategy;

        /**
         * Default implementation uses IdentityService.EQUALS_IDENTITY_STRATEGY.
         */
        public ArrayItemAccessor(final Object referenceItem) {
            this(referenceItem, EqualsIdentityStrategy.getInstance());
        }

        /**
         * Allows for custom IdentityStrategy.
         */
        public ArrayItemAccessor(final Object referenceItem,
                                 final IdentityStrategy identityStrategy) {
            Assert.notNull(identityStrategy, "identityStrategy");
            this.referenceItem = referenceItem;
            this.identityStrategy = identityStrategy;
        }

        public Class<?> getType() {
            return referenceItem != null ? referenceItem.getClass() : null;
        }

        @Override
        public String toString() {
            return "Array item " + getElementSelector();
        }

        public ElementSelector getElementSelector() {
            final CollectionItemElementSelector selector = new CollectionItemElementSelector(referenceItem);
            return identityStrategy == null ? selector : selector.copyWithIdentityStrategy(identityStrategy);
        }

        public Object get(final Object target) {
            final Collection<?> targetCollection = arrayAsCollection(target);
            if (targetCollection == null) {
                return null;
            }
            for (final Object item : targetCollection) {
                if (item != null && identityStrategy.equals(item, referenceItem)) {
                    return item;
                }
            }
            return null;
        }

        public void set(final Object target, final Object value) {
            final Collection<Object> targetCollection = arrayAsCollection(target);
            if (targetCollection == null) {
                return;
            }
            final Object previous = get(target);
            if (previous != null) {
                unset(target);
            }
            targetCollection.add(value);
        }

        private static Collection<Object> arrayAsCollection(final Object object) {
            if (object == null) {
                return null;
            } else if (object.getClass().isArray()) {
                return Lists.newArrayList((Object[]) object);
            }
            throw new IllegalArgumentException(object.getClass().toString());
        }

        public void unset(final Object target) {
            final Collection<?> targetCollection = arrayAsCollection(target);
            if (targetCollection == null) {
                return;
            }
            final Iterator<?> iterator = targetCollection.iterator();
            while (iterator.hasNext()) {
                final Object item = iterator.next();
                if (item != null && identityStrategy.equals(item, referenceItem)) {
                    iterator.remove();
                    break;
                }
            }
        }
    }


    public interface JavaObjectDiffBasedTypeComparisonConfigurer {

        Collection<Class<?>> useEquals();
    }

}
