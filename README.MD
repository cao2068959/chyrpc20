## 前言

这是什么?  一个轻量级的RPC框架

为什么造这个轮子? ***~~因为我太闲了~~*** 

## 项目架构
 注册中心 : zookeeper  (使用curator 操作包)
 
 通信框架 : netty  (4.1.25版本)
 
 序列化 :  kryo  
 
[foryou]: readmeImg/qy.png



## 快速体验

### spring boot 的模式
如果项目使用 spring boot 那么还提供了 spring-boot-starter 来支持 全注解的模式
传送门

[chy-rpc-spring-boot-starter](https://github.com/cao2068959/rpc-starter "")


### 运行环境
JDK 8

一个可以访问到的 zookeeper

### 安装项目

下载本仓库后,直接直接 执行 `maven install` 后 引用项目坐标
```xml
<dependency>
    <groupId>chy.frame.rpc</groupId>
    <artifactId>chyrpc2.0</artifactId>
    <version>2.0</version>
</dependency>
```

### 提供者配置

```java
 //创建上下文对象
 ChyRpcApplication chyRpcApplication = new ChyRpcApplication();
 //设置提供者暴露出去的端口,这里netty 将在这个端口上创建服务端,等待消费者调用
 chyRpcApplication.setPort(7778);
 //设置消费者能访问到的本机的ip地址
 chyRpcApplication.setIp("127.0.0.1");
 //设在 zookeeper 所在地址,如果是集群用逗号分隔
 chyRpcApplication.setZookeeperAddress("127.0.0.1:2181");
 //把需要远程调用的方法给注册进入 上下文对象中,注册进入的服务名称叫做userSName
 chyRpcApplication.register("userSName", new UserServiceImp());

```

### 消费者配置
```java
 //创建上下文对象
ChyRpcApplication chyRpcApplication = new ChyRpcApplication();
 //设在 zookeeper 所在地址,如果是集群用逗号分隔
chyRpcApplication.setZookeeperAddress("127.0.0.1:2181");
//这里会去zookeeper 根据服务的名称(第一个参数)寻找是否有提供者 提供了对应的服务,然后生成代理对象
//这里 UserService 是上面 UserServiceImp 的接口
//如果注册了 多个同样名称的服务,那么这里会使用轮询机制
UserService service = chyRpcApplication.getService("userSName",UserService.class);
//这里没有指定服务的名称,默认会使用入参类的全路径,这里就是 chy.test.UserService
UserService service2 = chyRpcApplication.getService(UserService.class);
//执行,这里会远程调用到 提供者,由提供者执行后返回对应的结果
service.getUser(10086)

```

在调用时,返回值和入参都可以是实体对象.这里会使用 `kryo` 进行序列化,所以 所有需要全程传输的对象都必须要要有一个 `无参构造器`

### 不使用接口的方式
在上面的模式中 提供者 注册的 `UserServiceImp` 是 `UserService` 接口的实现,而消费这 使用 `UserService` 去获取代理对象

而这里 `提供者` 可以不使用任何接口实现的形式,仅仅就是一个普通的 业务类
 
但是 `消费者` 在获取代理对象的时候,必须使用接口的形式,这个接口 里面的方法名 参数 返回值 必须要和 提供者注册的一致

提供者
```
public class NoInterfaceImg {

    public String exec(String s){
        return "没有接口的实现类:"+s;
    }

}

```

```java
chyRpcApplication.register("noInterface", new NoInterfaceImg());
```

消费者
```java
public interface INoInterface {

     String exec(String s);
}
```
```java
INoInterface iNoInterface = chyRpcApplication.getService("noInterface",INoInterface.class);
iNoInterface.exec("没有接口呀");
```
