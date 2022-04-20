# tiny-tomcat
## 仿照tomcat实现的web服务器，基于NIO。



### web容器相关

- [x] 生命周期管控，以Lifecycle为顶级接口**：简易实现**

- [x] 打破双亲委派，实现web程序类加载隔离**：简易实现**
- [x] 实现Engine，Host，Context，Wrapper四种web容器，逐级处理request请求**：简易实现**
- [x] Connector连接器，接收socket，轮询事件**：简易实现**
- [x] 实现DefaultServlet，加载静态资源**：简易实现**
- [x] 实现Http长连接**：简易实现**
- [ ] comet模式**：未实现**



### servlet相关

- [x] 实现过滤器**：简易实现**
- [ ] 实现八大监听器
  - [x] ServletContextAttributeListener：context属性监听器**：简易实现**
  - [x] ServletContextListener：context初始化和毁灭监听器**：简易实现**
  - [x] ServletRequestAttributeListener：request属性监听器**：简易实现**
  - [x] ServletRequestListener：request初始化监听器和毁灭监听器**：简易实现**
  - [x] HttpSessionAttributeListener：session属性监听器**：简易实现**
  - [x] HttpSessionBindingListener：session绑定属性监听器**：简易实现**
  - [x] HttpSessionListener：session初始化监听器**：简易实现**
  - [ ] HttpSessionActivationListener：session序列化和反序列化监听器<span color="red">**：未实现**</span>
- [ ] 实现servlet
  - [x] 单例servlet：request公用一个servlet-**简易实现**
  - [x] loadOnStartUp
  - [ ] SingleThreadModel：request请求使用不同的servlet-**未实现**
  - [ ] 异步servlet**：未实现**

- [x] session管理：**简易实现**



## 整体流程

<img src="/img/整体流程.png"/>



目前程序只是初步实现，还有很多缺点，不足的地方，代码很多地方也不太优雅。希望后续可以继续优化代码，补足其中的缺陷。

