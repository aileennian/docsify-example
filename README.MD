## 项目介绍

 - 介绍

   按github的标准及现在开源社区的推荐，MD形式组织的文档，简洁高效，还可以进行版本管理。

   但在浏览与大文档组织中，需要一个好的工具来来满足层次化的文档管理，提升用户体验。

   该文档的组织用到的docsify插件，这个插件是基于npm工具。

- 工具安装

 1、npm工具

 访问nodejs官网，安装nodejs即有npm。

 第三方介绍，点 (https://www.jianshu.com/p/1ab15a647e83)
    
 2、安装插件
    
```
npm i docsify docsify--cli -g --registry=https://registry.npm.taobao.org
```

# 文档访问

- windwos环境: cmd进入控制台
```
run.cmd
```
- linux环境
```
run.sh
```
- 浏览器访问

注意看run命令之后的控制台输出的端口，默认都是3000

浏览器输入 http://localhost:3000


# 文档编写

- 目录管理

在files文件夹下编写好一个markdown文档之后，需要将文件加入到_siderbar.md文件中，才可看到好看的格式。为了减轻整理工作，可按如下自动生成文件列表。文件名为菜单名，每一个文件的第一级与第二级标题自动成为菜单与子菜单：

```
cd auto
mvn test -Dauto-sidebar
```

对于文件本身的分类等深度的应用，后续文档支持。


