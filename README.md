# RPC项目总体流程

![rpc-process1](https://lty-image-bed.oss-cn-shenzhen.aliyuncs.com/blog/rpc-process1.png)

# RPC项目树

## 模块树

```
├── rpc-consumer
│   ├── rpc-consumer-demo          -> 未接入spring：consumer测试类
│   └── rpc-consumer-spring        -> 接入spring：consumer本地服务接口
├── rpc-core                       -> rpc核心实现逻辑模块
├── rpc-interface                  -> 远程服务接口
├── rpc-provider
│   ├── rpc-provider-demo          -> 未接入spring：provider测试类
│   ├── rpc-provider-goods         -> 接入spring：provider远程服务
│   ├── rpc-provider-pay           -> 接入spring：provider远程服务
│   └── rpc-provider-user          -> 接入spring：provider远程服务
├── rpc-spring-starter             -> spring-starter接入类
└── simple-rpc                     -> 简易rpc
```

## 核心模块树

```
├── annotations                    -> 项目注解包
├── cache                          -> 项目全局缓存
├── client                         -> 客户端相关类（请求处理、启动加载）
├── common                         -> 通用模块
├── config                         -> 项目配置（服务端、客户端属性配置）
├── constants                      -> 项目常量
├── dispatcher                     -> 服务端请求解耦
├── event                          -> 事件监听机制
├── exception                      -> 全局异常
├── filter                         -> 责任链模式过滤请求
├── proxy                          -> 动态代理
├── registry                       -> 注册中心
├── router                         -> 路由选择负载均衡
├── serialize                      -> 序列化与反序列化
├── server                         -> 服务端相关类（请求处理、启动加载）
├── service                        -> 测试服务接口
├── spi                            -> SPI自定义加载类
└── utils                          -> 项目工具包
```

# RPC项目测试

## 普通测试

1.   进入rpc-provider/rpc-provider-demo模块下，运行ProviderDemo主方法

     <img src="https://lty-image-bed.oss-cn-shenzhen.aliyuncs.com/blog/image-20221214155224257.png" alt="image-20221214155224257" style="zoom:50%;" />

2.   进入rpc-consumer/rpc-consumer-demo模块下，运行ConsumerDemo主方法

     <img src="https://lty-image-bed.oss-cn-shenzhen.aliyuncs.com/blog/image-20221214155301390.png" alt="image-20221214155301390" style="zoom:50%;" />

## 接入Springboot测试

1.   进入rpc-provider模块下，分别运行rpc-provider-goods、rpc-provider-pay、rpc-provider-user三个服务启动类

     <img src="https://lty-image-bed.oss-cn-shenzhen.aliyuncs.com/blog/image-20221214155517934.png" alt="image-20221214155517934" style="zoom: 50%;" />

     2.   进入rpc-consumer/rpc-consumer-spring模块下，运行服务启动类

          <img src="https://lty-image-bed.oss-cn-shenzhen.aliyuncs.com/blog/image-20221214155636798.png" alt="image-20221214155636798" style="zoom:50%;" />

     3.   Consumer默认端口为8081，在浏览器中输入 http://localhost:8081/api-test/do-test 进行远程服务调用基本测试

## 自定义配置

在项目模块的resouces文件下，有 `irpc.properties` 文件，用于配置Consumer（服务消费者）与Provider（服务提供者）的基本属性

1.   Consumer基本配置

     ```properties
     # 应用名
     irpc.applicationName=irpc-consumer
     # 注册中心地址
     irpc.registerAddr=localhost:2181
     # 注册中心类型
     irpc.registerType=zookeeper
     # 动态代理类型
     irpc.proxyType=javassist
     # 路由策略类型
     irpc.router=rotate
     # 序列化类型
     irpc.clientSerialize=jdk
     # 请求超时时间
     irpc.client.default.timeout=3000
     # 最大发送数据包
     irpc.client.max.data.size=4096
     ```

2.   Provider基本配置

     ```properties
     # 服务提供者端口号
     irpc.serverPort=9021
     # 服务提供者名称
     irpc.applicationName=good-provider
     # 注册中心地址
     irpc.registerAddr=localhost:2181
     # 注册中心类型
     irpc.registerType=zookeeper
     # 序列化类型
     irpc.serverSerialize=fastJson
     # 服务端异步处理队列大小
     irpc.server.queue.size=513
     # 服务端线程池大小
     irpc.server.biz.thread.nums=257
     # 服务端最大连接数
     irpc.server.max.connection=100
     # 服务端可接收数据包最大值
     irpc.server.max.data.size=4096
     ```

