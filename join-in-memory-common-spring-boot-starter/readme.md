# ReadMe

Single table query is the key. Join actions are most likely to be avoided by the proper design of the project.

## Usage

### 1. Defined a custom root annotation

Annotate the annotation with the meta-annotation `@JoinInMemory`

- The `keyFromSourceData` attribute must be overridden to provide access for clients to use the root annotation.
- The `keyFromJoinData` represents which field from the fetched data should be used when joining. Like the outer key in any relational database.
- The `loader` represents the method that fetches the join data.
  - Note that the loader method also supports passing custom parameters which are extracted from the `custom attributes` defined in the root annotation.
  - By `Custom attributes`, it means any attributes that are not defined in the `@JoinInMemory`
- The `joinDataConverter` means converting the match data before setting it to the field,
- The `runLevel` means the executing order in both parallel or serial executors.

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JoinInMemory(
        keyFromSourceData = "",
        keyFromJoinData = "id",
        loader = "@joinRepository.findAllById(#root)",
        joinDataConverter = "#root.name"
)
public @interface JoinUsernameOnUserId {

    @AliasFor(annotation = JoinInMemory.class, value="keyFromSourceData")
    @Language("SpEL")
    String value();

    @AliasFor(annotation = JoinInMemory.class, value = "runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
```

---

### 2. Annotate the source class

Like so

```java
@Data
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.PARALLEL,
        fieldProcessPolicy = JoinFieldProcessPolicy.GROUPED
)
public class JoinTester {
    
    String createdBy;

    @JoinUsernameOnUserId(value = "createdBy")
    String creator;

    String updatedBy;

    @JoinUsernameOnUserId(value = "updatedBy")
    String updater;

    Long removedBy;

    @JoinUsernameOnUserId(value = "removedBy")
    String remover;

    public JoinTester(String createdBy, String updatedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
```

--- 

### 3. Call join

```java
private final JoinService joinSerivce;

joinService.joinInMemory(tester);
```
