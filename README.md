![0509DA06](https://github.com/sawork-2024/aw06-thdlrt/assets/102659095/74e3ef5a-f8fc-458f-98cd-e0432d8a4c8c)## 总体设计思路

- 整体架构图
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240605000701209.png" alt="image-20240605000701209" style="zoom:20%;" />
- 总共拆分为5个模块：gateway、discover、product、order、model
  
#### Eureka配置与使用

- 项目中的Eureka订阅结构
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515013355428.png" alt="image-20240515013355428" style="zoom:50%;" />

Eureka Server是一个服务注册中心，所有的微服务都会在这里注册，并且可以相互发现。首先需要创建一个Eureka Server应用，并进行配置。

在`pom.xml`中添加Eureka Server的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

之后在`pos-eureka`模块下创建一个Spring Boot应用，并添加`@EnableEurekaServer`注解：

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

在`application.yml`中进行Eureka Server的相关配置：

```yml
server:
  port: 8080
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false

```

#### Gateway的配置

API Gateway是微服务架构中的入口，负责请求的路由、过滤和负载均衡等功能。负责将请求分配到不同的模块来进行处理。

在`pom.xml`中添加API Gateway的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

在`pos-router`模块下创建一个Spring Boot应用，并添加`@EnableEurekaClient`注解：
之后在`application.yml`中配置API Gateway的路由规则：分别将请求分配到product、order、model三个不同的模块

```yml
spring:
  application:
    name: gateway-services
  cloud:
    gateway:
      routes:
        - id: products-service
          uri: lb://products-service
          predicates:
            - Path=/Product/**
        - id: orders-service
          uri: lb://orders-service
          predicates:
            - Path=/Order/**
        - id: models-service
          uri: lb://models-service
          predicates:
            - Path=/products/**

```

#### 服务功能（order和product）的配置

分别实现产品管理服务和订单管理服务，首先将它们注册到Eureka Server。

在`pom.xml`中添加Eureka Client的依赖：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
在`pos-product`模块下创建一个Spring Boot应用，并添加`@EnableEurekaClient`注解，注册为eureka客户端
在`application.yml`中配置产品管理服务：指明eureka节点的地址

```yml
spring:
  application:
    name: product-service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8080/eureka/
```
#### 数据模块model
- 本模块中定义了项目中使用的Item、Order、Product等数据类型
- h2数据库在本模块中，由本模块对外提供关于product的操作（增删查询）。
- product和order模块都是通过FeignClients使用http访问model模块来获取相关数据的（这样也避免对统一数据类型的重复定义）

- Feign示例

 ```java
@FeignClient(name = "models-service")
public interface ProductClient {

    @GetMapping("/products")
    List<Product> getProducts();

    @GetMapping("/products/{id}")
    Product getProductById(@PathVariable("id") Long id);

    @PutMapping("/products/{id}")
    Product updateProduct(@PathVariable("id") Long id, @RequestBody Product product);

    @GetMapping("/products/search/{name}")
    List<Product> searchProductByName(@PathVariable("name") String name);
}
```

### 断路器的使用

在本实验中，使用Resilience4j来实现断路器功能，并应用于订单管理服务和产品管理服务。

- 在`pos-orders`和`pos-products`模块的`pom.xml`文件中引入Resilience4j的依赖

- 在`application.yml`中配置Resilience4j断路器的参数，例如在`pos-orders`模块中
- 在订单管理服务的服务层代码中应用断路器，在`OrderService`类中：使用了`@CircuitBreaker`注解来标注需要应用断路器的方法`createOrder`。当该方法的调用失败率超过配置的阈值时，断路器会打开，并调用降级方法`fallbackCreateOrder`。
  - 在产品管理服务中同样应用断路器
- 当某个服务出现故障时，断路器能够及时切断与该服务的连接，并提供降级处理，从而避免更多请求失败对系统造成的影响。

### 运行与测试

- 运行状态：
  - ![image](https://github.com/sawork-2024/aw06-thdlrt/assets/102659095/d0cecd0a-f8ce-44bb-810d-d38096123a69)
  - 所有模块都正确连接道路eureka

#### 压力测试

- 水平扩展之前：两个工作服务节点各一个
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515021649012.png" alt="image-20240515021649012" style="zoom: 50%;" />

- 水平扩展之后：两个工作服务节点各两个
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515021744746.png" alt="image-20240515021744746" style="zoom:50%;" />

- 可见服务性能有一定的提升
