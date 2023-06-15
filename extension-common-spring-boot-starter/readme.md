## 业务扩展点

- 主要逻辑就是通过spring管理的bean中找到使用了Extension注解的Bean，根据BizId关联Bean，通过统一的执行器获取具体bean调用具体方法- 