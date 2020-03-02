## 综合介绍

- 访问官网：[seata.io](http://seata.io)

- 安装配置（待续）

# 项目实例

## 需求描述

用户购买商品的请求，完成如下操作流程：
- 1、创建用户订单
- 2、扣减商品库存
- 3、扣除用户帐户金额

## 系统设计

- 分服架构说明

  一共提供三种服务
  - 1、主体服务：发起采购（@GlobalTransactional）
  - 2、分支服务：订单服务-创建订单(订单DB)
  - 3、分支服务：库存服务-扣减商品库存（库存DB)
  - 4、分支服务：帐户服务-扣除帐户金额（帐户DB)
 
## 实现步骤

项目位置：aszsst-spring-cloud-example

###  1、创建SpringCloud工程
- SpringBoot + Nacos + Mybatis + Seata + MySql + maven
  - 关键组件版本
    - Springboot：2.2.2.RELEASE
    - SpringCloud: Hoxton.SR1(2.2.1.RELEASE) -——nacos需要
    - Nacos-client：1.1.4
    - spring-cloud-alibaba-nacos: 2.2.0.RELEASE
    - seata-all: 1.0.0
    - httpclient.version: 4.5.2
    - mysql：慎用V8版本
    - druiddruid:1.1.21
  - 环境依赖
    - Maven、JDK
    - Mysql：慎用V8版本，如果是v8，jdbc-driver需要更换
    - nacos-server
    - seata-server

### 2、服务安装
 - 下载：(https://github.com/seata/seata/releases)[https://github.com/seata/seata/releases]
 - 解压，修改配置 conf/file.conf
 ```
 conf/file.conf：vgroup_mapping.**fsp_tx_group** = "default"
 ```
 如果其他方式存储、配置、注册那需要修改的地方比较多。demo时不需要
 - 双击seata-server启动 （linux下 sh seata-server.sh）
 
### 3、工程引入seata步骤
- vim pom.xm
  - 引入seata-all即可（**不要引入seata-spring-boot-starter**)
  - org.apache.httpcomponents:httpclient
- bootstrap.properties
```
spring.cloud.alibaba.seata.tx-service-group=fsp_tx_group
spring.cloud.alibaba.seata.application-id=${spring.application.name}
spring.cloud.alibaba.seata.enabled=true
```
第一个是必须的，后面两个为了程序可配置性也稳定本人增加的属性
- 客户端 file.conf和registry.conf文件
- 注意
  - 复制到resources目录下，内容与服务端的配配置有点小不同
  - file.conf中："fsp_tx_group"一定要和服务端一致
  - registry.conf：注册与配置类型也要服务端一致，这里是file

- 数据源-增加DataSourceProxy代理层
```
    @Bean("dataSourceProxy")
    @Autowired
    @Order(10)
    public DataSourceProxy buildDataSourceProxy(@Qualifier("originDataSource") DataSource dataSource) {
         DataSourceProxy proxy = new DataSourceProxy(dataSource);
         return proxy;
    }
    
     @Autowired
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceProxy")  DataSourceProxy dataSourceProxy) throws Exception {
        //..
        sqlSessionFactory.setDataSource(dataSourceProxy);
        //...
    }
```
- seata自动注册配置代码
```
    @Bean
    @ConditionalOnMissingBean({GlobalTransactionScanner.class})
    public GlobalTransactionScanner globalTransactionScanner() {
        String appId =  "bootstrap.propereties中设置的seata.application-id值";
        String txServiceGroup = "bootstrap.propereties设置的seata.tx-service-group值”;
        GlobalTransactionScanner transactionScanner = new GlobalTransactionScanner(appId, txServiceGroup);
        //seataProperties.setEnabled(true);
        return transactionScanner;
    }
```

- 全局事务发起
```
    @Override
    @GlobalTransactional(name = "${spring.cloud.alibaba.seata.tx-service-group}",timeoutMills = 300000,rollbackFor = Exception.class) //此注解开启全局事务
    public R<String> purchase(BusinessDTO businessDTO) {
        //....
        //创建订单
       create(businessDTO.getUserId(), businessDTO.getCommodityCode(),businessDTO.getCount(),orderMoney);
        if (i==0){
            throw  new BusinessException("添加订单不成功！");
        }
        //远程方法 扣减库存
        storageApi.deduct(businessDTO.getCommodityCode(), businessDTO.getCount());
        
        ////远程方法 扣减账户余额  可在accountServiceImpl中模拟异常
        accountApi.debit(businessDTO.getUserId(), orderMoney);
        return R.OK();
    }
```
  - 只需增加"@GlobalTransactional"即可。
  - 引入nacos服务引用和EUREKA一样，用feign-client即可

### 4、运行
- 启动nacos-server -- 配置工程已OK，详细配置文档后面可给出
- 启动seata-server
- 实始化数据库, 将init/demo.sql执行 (数据库IP、库名、用户、帐号需要修改）
- 启动服务
  - 帐户服务：account工程：AccountApplication
  - 库存服务：storage工程：StorageApplication
  - 订单服务：orsder工程：OrderApplication, 主方法我直接写在了订单服务器

- 测试
  - postman
```
    POST 方式 http://localhost:8081/business/purchase/?userId=1&commodityCode=C201901140001&count=6&amount=10&name=333
```
  - 测试-curl方式
```
curl -H "Content-Type:application/json" -X POST -d '{"userId":"1","commodityCode":"C201901140001","name":"---","count":2,"amount":"100"}' localhost:8104/business/dubbo/buy
```

# 常见问题

## 内存溢出

   seata-server运行一段地间如出现内存溢出，可修改seata-server.cmd的启动参数

```
%JAVACMD% %JAVA_OPTS% -server -Xmx4096m -Xms4096m -Xmn2048m -Xss1024k -XX:SurvivorRatio=10 -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=512m -XX:MaxDirectMemorySize=2024m -XX:-OmitStackTraceInFastThrow -XX:-UseAdaptiveSizePolicy -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%BASEDIR%"/logs/java_heapdump.hprof -XX:+DisableExplicitGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=75 -Xloggc:"%BASEDIR%"/logs/seata_gc.log -verbose:gc -Dio.netty.leakDetectionLevel=advanced -classpath %CLASSPATH% -Dapp.name="seata-server" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" io.seata.server.Server %CMD_LINE_ARGS%

```

  关于内存的相关问题，待续