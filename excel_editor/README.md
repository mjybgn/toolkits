# 使用说明
1.提供EditorAllMain用于excel->lua格式转换，将“资源路径”目录下的所有xlxs格式的文件转换为lua格式，并存放到“资源解析输出路径”下；
2.提供EditorMain用于单个excel->lua的转换；
3.提供ImportResource用于将“公共资源表的目录路径”下的xlxs文件上传到目录下的“资源路径”；
详细配置-->(/config/config.xml)。

注意事项 excel->lua适配表：
1.header（表头）为4行n列；（若想扩展更多可以修改com/ming/module/excelParse/ParseContent下的接口实现类）
待优化（TODO）：可以使用正则表达式适配多种表头，扫描识别表格式。