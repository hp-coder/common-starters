package com.luban.common.base.utils;


import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author hp 2023/4/13
 */

public interface ParamUtils {

    final class Likes {
        private Likes() {
        }

        public static String likeRight(String value) {
            return value + "%";
        }

        public static String likeLeft(String value) {
            return "%" + value;
        }

        public static String likeAll(String value) {
            return "%" + value + "%";
        }
    }

    final class Strings<T extends String> {
        private static final Strings<?> EMPTY = new Strings<>(null);

        private final T value;

        private Strings(T value) {
            this.value = value;
        }

        public static <T extends String> Strings<T> ofNullable(T value) {
            return (StringUtils.hasText(value)) ? of(value) : ((Strings) empty());
        }

        public static <T extends String> Strings<T> of(T value) {
            return new Strings(value);
        }

        public <U extends String> Strings map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper);
            if (!isPresent()) {
                return empty();
            } else {
                return Strings.ofNullable(mapper.apply(value));
            }
        }

        public Strings<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate);
            if (!isPresent()) {
                return this;
            } else {
                return predicate.test(value) ? this : empty();
            }
        }

        public void ifPresent(Consumer<? super T> action) {
            if (StringUtils.hasText(value)) {
                action.accept(value);
            }
        }

        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            if (StringUtils.hasText(value)) {
                action.accept(value);
            } else {
                emptyAction.run();
            }
        }

        public T orElse(T other) {
            return StringUtils.hasText(other) ? other : value;
        }

        public boolean isPresent() {
            return StringUtils.hasText(value);
        }

        public T get() {
            if (value == null) {
                throw new NoSuchElementException("No value present");
            }
            return value;
        }

        public static Strings empty() {
            return EMPTY;
        }
    }

    final class Collections<T extends Collection> {
        private static final Collections<?> EMPTY = new Collections<>(null);

        private final T value;

        private Collections(T value) {
            this.value = value;
        }

        public static <T extends Collection> Collections<T> ofNullable(T value) {
            return (CollectionUtils.isEmpty(value)) ? ((Collections) empty()) : of(value);
        }

        public static <T extends Collection> Collections<T> of(T value) {
            return new Collections(value);
        }

        public <U extends Collection> Collections map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper);
            if (!isPresent()) {
                return empty();
            } else {
                return Collections.ofNullable(mapper.apply(value));
            }
        }

        public Collections<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate);
            if (!isPresent()) {
                return this;
            } else {
                return predicate.test(value) ? this : empty();
            }
        }

        public void ifPresent(Consumer<? super T> action) {
            if (!CollectionUtils.isEmpty(value)) {
                action.accept(value);
            }
        }

        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            if (CollectionUtils.isEmpty(value)) {
                emptyAction.run();
            } else {
                action.accept(value);
            }
        }

        public T orElse(T other) {
            return CollectionUtils.isEmpty(other) ? value : other;
        }

        public boolean isPresent() {
            return !CollectionUtils.isEmpty(value);
        }

        public T get() {
            if (value == null) {
                throw new NoSuchElementException("No value present");
            }
            return value;
        }

        public static Collections empty() {
            return EMPTY;
        }
    }

    final class Maps<T extends Map> {

        private static final Maps<?> EMPTY = new Maps<>(null);

        private final T value;

        private Maps(T value) {
            this.value = value;
        }

        public static <T extends Map> Maps<T> ofNullable(T value) {
            return (CollectionUtils.isEmpty(value)) ? ((Maps) empty()) : of(value);
        }

        public static <T extends Map> Maps<T> of(T value) {
            return new Maps(value);
        }

        public <U extends Map> Maps map(Function<? super T, ? extends U> mapper) {
            Objects.requireNonNull(mapper);
            if (!isPresent()) {
                return empty();
            } else {
                return Maps.ofNullable(mapper.apply(value));
            }
        }

        public Maps<T> filter(Predicate<? super T> predicate) {
            Objects.requireNonNull(predicate);
            if (!isPresent()) {
                return this;
            } else {
                return predicate.test(value) ? this : empty();
            }
        }

        public void ifPresent(Consumer<? super T> action) {
            if (!CollectionUtils.isEmpty(value)) {
                action.accept(value);
            }
        }

        public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
            if (CollectionUtils.isEmpty(value)) {
                emptyAction.run();
            } else {
                action.accept(value);
            }
        }

        public T orElse(T other) {
            return CollectionUtils.isEmpty(other) ? value : other;
        }

        public boolean isPresent() {
            return !CollectionUtils.isEmpty(value);
        }

        public T get() {
            if (value == null) {
                throw new NoSuchElementException("No value present");
            }
            return value;
        }

        public static Maps empty() {
            return EMPTY;
        }
    }
}
