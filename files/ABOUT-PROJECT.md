
## Feign接口由服务端来完成

- 1、定义Feign接口
```
@FeignClient(value = ServerContant.PROVIDER_ACCOUNT)
@Headers({"Accept: application/json", "Content-Type: application/json"})
@RequestMapping("/api/account")
public interface AccountApi {    
    @RequestMapping(value = "/debit/", method = RequestMethod.POST)
    R<Integer> debit(@RequestParam("userId") String userId, @RequestParam("money") BigDecimal money);
}
```

- 2、Controller代码--服务提供方
```
@RestController
public class AccountApiImpl implements AccountApi {
    private final AccountService accountService;
    @Override
    public R<Integer> debit(String userId, BigDecimal money) {
        Integer ret = accountService.debit(userId,money);
        return R.OK(ret);
    }
}
```

- 3、服务消费方

```
 // -- 接口调用
public class OrderServerImpl ...{
    @Autowired
    private StorageApi storageApi;
    
    public R<String> purchase(BusinessDTO businessDTO) {
        //..
        storageApi.deduct(businessDTO.getCommodityCode(), businessDTO.getCount());
        //..
    } 
 }
 
 
// 注册feign
@Configuration
@EnableFeignClients(basePackages = {
         "com.syx.nian.demo.ali.account.feign*"
        ,"com.syx.nian.demo.ali.storage.feign"
})

public class FeignApiAutoConfig {

}

```

## Nacos配置中心

见nacos文档

## 文档工程-docsify

见 [首页](../README.md)



## 注解方式实现API版本管理

(app和web端可用，目前feign-client端还需要改造）

    ```
    @RestController
    @ApiVersion(1) 
    @RequestMapping("/api/{version}/echo")
    public class TestVersion {
    
        //访问方式：http://.../api/v1/
        @GetMapping("/")
        public String test01(@PathVariable String version) {
            return "test01 : " + version;
        }
        
        //访问方式： http://.../api/v2/
        @ApiVersion(2)
        @GetMapping("/")
        public String test02(@PathVariable String version) {
            return "test01 : " + version;
        }
    
     //...
     }
     
     #配置代码
     public class WebMvcConfiguration extends WebMvcConfigurationSupport implements InitializingBean, DisposableBean {
        @Override
        protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {return new CustomRequestMappingHandlerMapping();}
    }
     
    ```

## IDE工具相关

- 双环境-解决maven org.eclipse.m2e:lifecycle-mapping 在Idea中无法使用的处理方式
```
// 需要git和mvn环境
 $ git clone https://github.com/mfriedenhagen/dummy-lifecycle-mapping-plugin.git
 $ cd dummy-lifecycle-mapping-plugin
 $ mvn install
 // 用于有私服的方式
 // mvn deploy -DaltDeploymentRepository=REPOID::default::PLUGIN_REPO_URL.
 
```


## 组件版本
 - 关键组件版本
  - Springboot：2.2.2.RELEASE
  - SpringCloud: Hoxton.SR1(2.2.1.RELEASE) -——nacos需要
  - Nacos-client：1.1.4
  - spring-cloud-alibaba-nacos: 2.2.0.RELEASE
  - seata-all: 1.0.0
  - httpclient.version: 4.5.2
  - mysql：慎用V8版本
  - druiddruid:1.1.21

