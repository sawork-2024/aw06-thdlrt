## 设计思路

- 整体架构图
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515013145298.png" alt="image-20240515013145298" style="zoom:50%;" />

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
package com.example.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

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
  port: 18080
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false

```

#### router（Gateway）的配置

API Gateway是微服务架构中的入口，负责请求的路由、过滤和负载均衡等功能。

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

```java
package com.example.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RouterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RouterApplication.class, args);
    }
}
```

在`application.yml`中配置API Gateway的路由规则：

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
            - Path=/productsService/**
        - id: orders-service
          uri: lb://orders-service
          predicates:
            - Path=/ordersService/**

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

在`pos-product`模块下创建一个Spring Boot应用，并添加`@EnableEurekaClient`注解：

```java
package com.example.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
```

在`application.yml`中配置产品管理服务：

```yml
spring:
  application:
    name: product-service
eureka:
  client:
    service-url:
      defaultZone: http://localhost:18080/eureka/
```

#### 功能实现

代码结构

```css
pos-order
└── src
    └── main
        └── java
            └── orders
                ├── cli
                ├── controller
                ├── db
                ├── map
                ├── model
                ├── service
                └── OrdersApplication.java

```

- cli: 命令行接口相关代码
- controller: 控制器层代码，处理HTTP请求
- db: 数据库访问层代码
- map: 映射层代码
- model: 数据模型代码
- service: 服务层代码，包含业务逻辑

`OrdersApplication.java` 是订单管理服务的主应用类，包含Spring Boot的启动代码。

- product的实现方式类似，不再赘述

### 断路器的使用

在本实验中，使用Resilience4j来实现断路器功能，并应用于订单管理服务和产品管理服务。

- 在`pos-orders`和`pos-products`模块的`pom.xml`文件中引入Resilience4j的依赖

- 在`application.yml`中配置Resilience4j断路器的参数，例如在`pos-orders`模块中
- 在订单管理服务的服务层代码中应用断路器，在`OrderService`类中：使用了`@CircuitBreaker`注解来标注需要应用断路器的方法`createOrder`。当该方法的调用失败率超过配置的阈值时，断路器会打开，并调用降级方法`fallbackCreateOrder`。
  - 在产品管理服务中同样应用断路器
- 当某个服务出现故障时，断路器能够及时切断与该服务的连接，并提供降级处理，从而避免更多请求失败对系统造成的影响。

### 运行与测试

- 运行状态：
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515020046556.png" alt="image-20240515020046556" style="zoom:50%;" />

#### 压力测试

- 水平扩展之前：两个工作服务节点各一个
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515021649012.png" alt="image-20240515021649012" style="zoom: 50%;" />

- 水平扩展之后：两个工作服务节点各两个
  - <img src="https://thdlrt.oss-cn-beijing.aliyuncs.com/image-20240515021744746.png" alt="image-20240515021744746" style="zoom:50%;" />

- 可见服务性能有一定的提升