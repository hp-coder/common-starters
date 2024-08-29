# ReadMe

## Usage

### 1. Create an Annotation annotated with the @LazyLoader

Attributes defined in the annotation are considered as the parameters that will be passed to the loader method.

The parameter placeholder should be written in the format that starts with a '#' and appends with the corresponding attribute name.

- like: #ids - String ids();

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@LazyLoader("@productRepository.findAllById(#ids)")
public @interface LazyLoadProductsByProductIds {

    @Language("SpEL")
    String ids();
}
```

---

### 2. Annotate field attributes with the annotation defined previously

The `productIds` is inspected as a SpEL expression, which means that the Expression resolution process is to look for any
field named `productIds` or the getter method of that field and pass its value to the loader method.

```java
@Data
public class CreateOrderContext implements CreateOrderContextApi {

    private Long id;

    private CreateOrderCommand command;

    @LazyLoadProductsByProductIds(ids = "productIds")
    private List<Product> products;

    public Collection<Long> getProductIds() {
        return command.getGoods()
                .stream()
                .map(Goods::getProductId)
                .collect(Collectors.toList());
    }
}
```

---

### 3. Create a proxy instance of the instance

The `LazyLoaderProxyFactory` is an auto-register Spring Bean that could be easily injected into any other Spring Bean, 
such as classes annotated with `@Service`

```java
private final LazyLoaderProxyFactory lazyLoaderProxyFactory;

final CreateOrderContext proxiedContext=this.lazyLoaderProxyFactory.createFor(createOrderContext);
```

---

### 4. Calling the getter method of that annotated field

If configured properly, calling the getter method of that field should perform an execution of that loader method defined
in the `@LazyLoader`.

```java
final List<Product> products = proxiedContext.getProducts();
```



