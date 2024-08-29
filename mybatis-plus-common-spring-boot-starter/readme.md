# ReadMe

DDD style encapsulation.

## Note

### 1. TypeHandler

Use `TypeHandlerAdapter` instead of `TypeHandler` directly for much easier conversion.

---

### 2. Wrapper in Query Or Save

Use `QueryHelper` or `UpdaterHelper`, they use `TypeHandlerAdapter` and fallback conversion logic to convert
parameters before interacting with the data source.

---

### 3. Mapper

Use `BaseRepository` instead of `BaseMapper` directly for more functionalities.

Use `OrmOperations` to perform save and update.

---

### 4. Model

If the project is designed in the DDD style, like the COLA framework.

Entities or aggregate roots should implement `BaseMbpAggregate` instead of `Model`.

---

### 5. Pagination

Use `PageHelper` with `PageRequestWrapper` and `PageResponse` instead `IPage` for a more generic design.
