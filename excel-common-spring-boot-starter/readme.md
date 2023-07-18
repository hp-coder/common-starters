# Excel通用模块

`基于 EasyExcel 的通用excel模块`

## 注意

这个模块主要是说明逻辑的前提下, 尽可能包括所有场景, 并非完全支持所有场景, 有问题一起研究讨论即可.

## 说明

- 注解 + AOP方式增强并简化导出导入逻辑
- 在此基础上使用配置增强接口，增加对导出导入合并行，以及校验数据的自定义处理

## 注解

- @DynamicSelectData
  - 使用下拉列表的列数据校验注解
- @ExcelMerge
  - 合并行注解，根据上下行数据判断是否合并
- @ExcelSelect
  - 导出excel带下拉列表或级联列表注解，提供数据源
- @RequestExcel
  - 导入excel数据注解，自动处理数据并封装到API方法参数中
- @ResponseExcel
  - 导出excel数据注解，自动处理数据并根据API返回值类型处理生成excel并导出
- @Sheet
  - 导出excel时指定sheet名称忽略列等信息
