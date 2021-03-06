## Hibernate中的对象有三种状态:
* 1.`TransientObject` 瞬时(临时)对象   可以通过`new`、从`Persistent`到`Transient`的`Delete`、从`Detached`到`Transient`的`Delete`
* 2.``PersistentObject`` 持久化对象  从数据库查出来的`get`、`load`、`find`、`Iterator`方法，从`Transient` 到`Persistent`的`save`、`saveorupdate`，还有从`Detached` 到`Persistent` 的`update`、`saveorupdate`
* 3.`DetachedObject` 离线对象  只能出现在从Persistent 到Detached 的evict、close、clear

## 对象加载方法：
* `get()`:不论`lazy`如何取值，总是会试图加载初始化好的实例
*  `load()`:如果`lazy`取值为`false`，会加载到初始化好 的实例

## Hibernate修改对象的三个方法：
* `update`：只对处于持久状态的对象进行操作
	无返回值：`void`
	仅仅只是要求执行update语句的方法
	使用环境：
		1.程序在第一个`session`中加载对象
		2.该对象被传递到表现层
		3.对象发生了一些改动
		4.该对象被返回到业务逻辑层
		5.程序调用第二个`session`的`update()`方法持久这些改动

* `saveOrUpdate`：
	无返回值：`void`
	会根据执行时，对象的状态决定是执行insert语句还是执行`update`语句
	* 游离状态的数据执行`update()`
	* 临时状态的数据执行`save()`
* `merge`：根据当前对象主键是否存在执行新增或者更新操作，主键存在但是没有字段进行更改则不执行任何操作
	有返回值
	执行对象的update语句，在执行前后，不会对操作对象的状态进行改变，并返回一个执行对象的持久状态的对象。

## `set` 属性
 * `cascade`级联控制操作属性 `none`（默认）、`save-update`、`delete`、`all`，一般只使用`save-update`
* `inverse`为反向控制属性：`false`（默认）表示由一方控制，`true`表示由多方自己控制关联关系
## `order-by`属性用于指定关联集合中多方对象的排序要求
 即查询sql语句中的`order-by`子句，`order-by`属性要使用表中的列名，而不是类的属性名
## lazy属性，用于延迟加载
* 类级别加载策略：在类中进行设置
	* `true`(默认) 延迟加载  
	* `false` 立即加载  
	多对一延迟加载策略：
* 一对多和多对多关联的查询策略：`<set>`中设置
	*	`true`(默认)	延迟加载
	*	`false`	立即加载
	*	`extra`	加强延迟加载
* 多对一关联的查询策略：`<many-to-one>`中可以设置
	*	`proxy`(默认)	延迟加载
	*	`no-proxy`	无代理延迟加载
	*	`false`	立即加载

## Hibernate 二级缓存
1.  Hibernater 的二级缓存是一个插件，下面是几种常用的缓存插件：
	* `EhCache`：可作为进程范围的缓存，存放数据的物理介质可以是内存或硬盘，对Hibernate的查询缓存提供了支持。

	*	`OSCache`：可作为进程范围的缓存，存放数据的物理介质可以是内存或硬盘，提供了丰富的缓存数据过期策略，对Hibernate的查询缓存提供了支持。

	*	`SwarmCache`：可作为群集范围内的缓存，但不支持Hibernate的查询缓存。

	*	`JBossCache`：可作为群集范围内的缓存，支持事务型并发访问策略，对Hibernate的查询缓存提供了支持

2.  什么样的数据适合存放到第二级缓存中？ 
	*	1) 很少被修改的数据 
	*	2) 不是很重要的数据，允许出现偶尔并发的数据 
	*	3) 不会被并发访问的数据 
	*	4) 参考数据,指的是供应用参考的常量数据，它的实例数目有限，它的实例会被许多其他类的实例引用，实例极少或者从来不会被修改。

3. 不适合存放到第二级缓存的数据？ 
	* 1) 经常被修改的数据 
	*	2) 财务数据，绝对不允许出现并发 
	*	3) 与其他应用共享的数据。

## Hibernate优缺点
*	(1)  对象/关系数据库映射(ORM)
*	(2)  透明持久化(persistent)
*	(3)	 事务Transaction(org.hibernate.Transaction)
*	(4)  它没有侵入性，即所谓的轻量级框架
*	(5)  移植性会很好
*	(6)  缓存机制，提供一级缓存和二级缓存
*	(7)  简洁的HQL编程

### session 线程非安全（持久化管理器），SessionFactory才是线程安全

## EJB3中定义的四种`Entity`状态：
*	新实体（`new`）；
*	持久化实体（`managed`）；
*	分离的实体（`detached`）；
*	删除的实体（`removed`）。

## `open session in view`
* 通常`Open Session in View`模式相对来说是个不错的解决方案。事务在服务层结束，但关联的`Hibernate Session`保持打开状态，直到视图生成完成为止。这样及早的释放了数据库锁和连接，并且视图中可以通过`lazy load`来加载。
*	Spring支持这种即开即用的模式，通过`org.springframework.orm.hibernate.support.OpenSessionInViewFilter`(可以和任何web层技术一起使用)或者`OpenSessionInViewInterceptor`(和Spring的Web MVC框架一起使用)。
* Spring的这两种实现,他们支持两种操作方式:
	* 单一会话模式<br>
		通过在请求范围的`Session`上来操作事务，单个`Hibernate Session`将用于整个`HTTP`请求。默认情况下是单一会话方式，它是`Open Session in View`比较有效的版本。请求范围内的`Hibernate Session`视为第一级缓存，整个请求内只载入每个持久性对象一次。主要的缺点是：所有的`Session`管理的对象都必须是唯一的，这样视图从`HttpSession`中重新`attach`一个对象，可能导致`Hibernate`重复对象的异常。
	* 延迟关闭模式<br>
	每个事务将和平常一样使用其自身的`Session`,但这些`Session`中的每一个都在事务完成后保持开启，在视图生成后关闭。这样可以通过使用新的`Hibernate Session`来避免重复对象的问题，所有`Session`都在事务完成后保持开启，在他们每个上允许`lazy load`。但是，如果单个持久性对象涉及到多个事务，可能导致问题。`Hibernate`需要一个持久化对象与一个`Hibernate Session`关联，而不是同时与多个。这种情况下应该使用单一会话模式。

### lazyinitialzationException处理方式有：
* 让`session`会话的范围无限大
* 通过`lazy`属性的设置 <br> 
	一次性查出所有需要的值
* `open session in view` 模式<br>但是因为需要配置`filter`，只能应用在`web project`中


### fetch  hibernate中的抓取策略  
*	单端代理的批量抓取 `<many-to-one>`中进行设置  
	* `select`(默认)<br>
	`select`  单独另外发送一条sql语句抓取当前对象关联实体或集合  执行结果是两条语句
	* `join`<br>`join`  通过`select`语句使用外连接来加载其关联实体或集合，此时`lazy`会失效  执行结果是一条语句

*	集合代理的批量抓取
	*	select
	*	join
	*	subselect

## hibernate检索策略
*	立即检索策略：<br>
		`session`的`get()`方法
	* 优点：频繁使用的关联对象能够被加载到缓存中
	* 缺点：占用内存，`select`语句过多
*	延迟检索策略<br>
		`session`的`load()`方法
	*	优点：由程序决定加载哪些类和内容，避免大量无用的sql语句和内存消耗
	*	缺点：session关闭后就无法访问关联对象
*	左外连接检索策略  <br>采用左外连接检索，能够使用sql的外连接查询，将需要加载的关联对象加载到内存中
		`<set>`或`<many-to-one>`中的`fetch`设置为`join`
	*	优点：
		1. 对应用程序完全透明，不管对象处于持久化状态还是游离状态，应用程序都可以方便的从一个对象导航到与他关联的对象。<br>
		2. 使用外连接，select语句数目少
	*	缺点：
		1. 可能会加载应用程序不需要访问的对象，白白浪费许多内存空间
		2.	复杂的数据库表连接也会影响检索功能<br>
`batch-size`  用于指定关联查询数量，以减少批量检索的数据数目
		