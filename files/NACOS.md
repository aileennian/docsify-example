
## 工具选型
- 注册中心
  - https://www.cnblogs.com/zhucww/p/11532770.html
  - https://blog.csdn.net/chang_li/article/details/89480351

- 配置中心
  - https://www.jianshu.com/p/afd7776a64c6
  - https://www.jianshu.com/p/2f0ae9c7f2e1

# NACOS SERVER安装配置

- 官网: https://nacos.io/zh-cn/
- 工程: https://github.com/alibaba/nacos
- 默认端口：8848
- 默认有一个本地数据库：
- 安装配置

```
$ wget https://github.com/alibaba/nacos/releases/download/1.1.4/nacos-server-1.1.4.tar.gz
$ tar zxvf nacos-server-1.1.4 
//(Demo是可不需要配置外置数据库，内置库名为dery）
$ cd nacos
$ ./startup.sh -m standalone #. 可换成sh 或 bash 
```

- 检查启动情况

```
$ tail ../nacos/logs/start.out
$ ps -ef|grep nacos
$ nestat -an|grep 8848
```

- 关闭服务命令

```
$ ./shutdown.sh  关闭
```

- 可视化界面
  - URL: http://ip...:8848/nacos/#/login
  - 默认帐号/密码：nacos/1qaz@67YU

# Restful API

 - 服务注册

```
$ curl -X POST 'http://127.0.0.1:8848/nacos/v1/ns/instance?serviceName=nacos.naming.serviceName&ip=20.18.7.10&port=8080'
```


- 服务发现

```
$ curl -X GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=nacos.naming.serviceName'
```

-  发布配置

```
$ curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test&content=HelloWorld"
```

-  获取配置

```
$ curl -X GET "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test"
```



# 工程接入(待续)

待续