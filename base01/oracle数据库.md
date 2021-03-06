
#### Oracle物理结构：(控制文件)、(数据文件)、(日志文件)。
#### Oracle 逻辑结构：(表空间)、(段)、(区)、(块)。
#### Oracle 内存结构： (系统全局区SGA)、(程序全局去PGA)、(用户全局区UGA)。
#### Oracle进程结构：(后台进程)、(服务器进程)、  (用户进程)。



### 一、建表空间语句（不建议进行操作）
*	oracle中对数据库默认进行事务管理，所以对数据库进行增、删、改操作确认无误后，执行commit命令进行提交，否则一直该数据库或表将一直处于被占用状态，其他用户无法进行查询以外的操作。
```sql
		-- 创建表空间
		create tablespace user1_tbs 
		datafile 'E:/user1_tbs.dbf' 
		size 100M
		;
		
		-- 创建用户，并赋予表空间
		--CREATE USER 用户名 
		--IDENTIFIED BY 密码
		
		create user user1 
		identified by ok 
		default tablespace user1_tbs;

		-- 给用户授予权限
		--GRANT   权限1，权限2...
		--ON    对象类型   对象名
		--TO     用户1，用户2...
		--(WITH GRANT OPTION) ;
		grant connect to user1;
		grant resource to user1;
```
### 二、建库建表语句
```sql
	-- 创建表及其主外键关系
	create table s(
		sNo char(2) primary key, -- 主键关系
		sName varchar2(8) not null,
		city varchar2(20) not null
	);
	create table p(
		pNo char(2) primary key,-- 主键关系
		pName varchar2(8) not null,
		kind number(1) not null
	);
	create table sp(
		sNo char(2) references s (sNo), -- 外键关系
		pNo char(2) references p (pNo), -- 外键关系
		qty number(7) not null
	);
	-- 复合主键
	alter table sp 
	add constraints PK_SP primary key (sNo,pNo);
```
### 三、SQL编程及高级查询
*	sql分类：
		数据定义语言(Data Definition Language, DDL)
			create   alter  drop
		数据操纵语言(Data Manipulation Language, DML)
			insert  delete  select   update
		事务控制语言(Transaction Control Language, TCL)
			commit  savepoint  rollback
		数据控制语言(Data Control Language, DCL)
			grant  revoke



	to_date('2000-12-12 17:34:12' ,'yyyy-mm-dd hh24:mi:ss') 字符串转化为日期
	to_char(sysdate,'yyyy-mm-dd hh24:mi:ss')根据实际需要规定格式，
*	联合查询	
```sql
	select * from (
		select age 年龄 from agetable where age >=...
		-- union 并集，去除重复行
		-- union all 并集，不去除重复行
		-- minus 减集
		-- intersect 交集
		select age from agetable where age <=...
		)
	where
	group by
	having
	order by
```
*	分页查询<br>
		`oracle`伪列：伪列可以从表中查询，但不能插入，更新和删除他们的值
		常用的伪列：
			`rowid`  表中行的存储位置，该数据可以唯一的标识数据库中的一行，可以使用rowid快速定位表中的一行，不常用
			`rownum`  查询返回的结果集中行的序号，可以用它来限制查询返回的行数（在没有使用别名的情况下，`rownum`列必须从1开始）常用！分页必须掌握
```sql			
	select * from		--外层：使用中间层定义的rownum别名
	(
		select rownum r,t.*		--中间层：定义rownum别名
		from(
				select * 
				from scott.emp		--符合业务要求的带有排序的
				order by sal desc
			) t
		)
	where r >= 2 and r <= 5

		--pageNum  页数
		--pageSize  每页显示的行数
		--where r > (pageNum-1)*pageSize and r<=pageNum*pageSize(oracle分页)
		--limit (pageNum-1)*pageSize，pageSize(mysql分页)
```
## PL/SQL编程
```sql
--if条件分支  if...then...elsif...then...end if;
-- Created on 2017/3/17 by AD-YOUNG 
declare				--声明部分，声明变量等等
  v_tax number;
  v_sal scott.emp.sal%type;--引用所在列数据格式对变量声明
  v_name scott.emp.ename%type :='KING';--赋值使用符号“ := ” 
  v_taxpoint number;
  v_dif number;
begin				--语义正文
  select sal into v_sal
  from scott.emp
  where ename = v_name;
  v_dif := v_sal - 3500;
	  --开始if分支
  if v_dif < 0
	then v_tax := 0;--then表示if条件达成时的执行语句
		--多重if使用elsif关键字
	  elsif v_dif <= 1500
		then v_tax := v_dif * 0.03;
	  elsif v_dif <= 4500
		then v_tax := 45 + (v_dif - 1500) * 0.1;
	  end if;--结束if分支
	  dbms_output.put_line(v_name||'应该缴纳的税金是：'||v_tax);--字符串的连接拼接使用“||”
	end;				--结束
		

	--case条件分支示例
	declare 
	  v_name scott.emp.ename%TYPE := 'SCOTT';
	  v_sal scott.emp.sal%TYPE;
	  c_begin_money constant integer := 3500;
	  v_money number(7,2);
	  v_pay number(7,2);
	begin
	  -- 查询KING的工资
	  select sal into v_sal
	  from scott.emp
	  where ename = v_name;
	  -- 计算缴税工资
	  v_money := v_sal - c_begin_money;
	  -- 使用case语句计算所交税金
	  --case分支  case when...then...esle...end case;
	  case 
		when v_money <= 1500
		  then v_pay:= v_money * 0.03 - 0;
		when v_money <= 4500
		  then v_pay:= v_money * 0.1 - 105;
		when v_money <= 9000
		  then v_pay:= v_money * 0.2 - 555;
		when v_money <= 35000
		  then v_pay:= v_money * 0.25 - 1005;
		when v_money <= 55000
		  then v_pay:= v_money * 0.3 - 2755;
		when v_money <= 80000
		  then v_pay:= v_money * 0.35 - 5505;
		else
		  v_pay:= v_money * 0.45 - 13505;
	  end case;
	  --case分支结束
	  -- 输出缴税金额
	  dbms_output.put_line(v_name||'工资：'||v_sal);
	  dbms_output.put_line('起征金额：'||c_begin_money);
	  dbms_output.put_line('缴税金额：'||v_money);
	  dbms_output.put_line('应缴税金：'||v_pay);
	end;
	--用户自定义异常示例
	declare 
	  v_emp_name scott.emp.ename%TYPE := 'SCOTT';
	  v_dept_name scott.dept.dname%TYPE;
	  v_emp_sal scott.emp.sal%TYPE;
	  v_salgrade_grade scott.salgrade.grade%TYPE;
	  e_low_money exception; -- 用户自定义异常
	begin
	  select d.dname,e.sal,s.grade
	  into  v_dept_name,v_emp_sal,v_salgrade_grade
	  from scott.emp e 
	  inner join scott.dept d 
	  on e.deptno = d.deptno 
	  inner join scott.salgrade s
	  on e.sal >= s.losal and e.sal <= s.hisal
	  where e.ename = v_emp_name;
	  /*from scott.emp e , scott.dept d, scott.salgrade s
	  where e.deptno = d.deptno 
	  and e.sal >= s.losal and e.sal <= s.hisal
	  and e.ename = v_emp_name;*/
	  if v_salgrade_grade < 5 
		then
		raise  e_low_money;  -- 使用raise触发用户自定义异常
	  end if;
	  dbms_output.put_line(v_emp_name||'部门名称：'||v_dept_name);
	  dbms_output.put_line(v_emp_name||'薪水：'||v_emp_sal);
	  dbms_output.put_line(v_emp_name||'工资级别：'||v_salgrade_grade);
	exception
	  when NO_DATA_FOUND then 
		dbms_output.put_line('没有数据符合条件');
	  when TOO_MANY_ROWS then
		dbms_output.put_line('超过一行的结果集');
	  when e_low_money then
		dbms_output.put_line('工资等级不超过5的太低了！');
	end;
```
### 游标：<br>用来处理使用`select`语句从数据库中检索到的多行记录的工具
*	显式游标
	*	返回记录时，使用显式游标逐行读取
*	隐式游标
	*	PL/SQL自动为隐式DML语句创建隐式游标，包含一条返回记录
	*	属性：
		1.	`%found`  用于检验游标是否成功，通常在`fetch`语句中使用，当游标按照条件查询到一条记录时，返回`true`
		2.	`%isopen`  判断游标是否处于打开状态，试图打开一个已经打开或者已经关闭的游标，将会出现错误
		3.	`%notfound`  与`%found`作用相反，当按照条件无法查询到记录时，返回`true`
		4.	`%rowcount`  循环执行游标读取数据时，返回检索出的记录数据的行数
	*	语法
		1.	声明游标
		```sql
			CURSOR cursor_name [ ( parameter [ , parameter]……)]
			[ RETURN return_type ] IS selectsql
				CURSOR：用于声明一个游标
				parameter：可选参数，用于指定参数类型、模式等
				return：可选，指定游标的返回类型
				selectsql:需要处理的select语句，不能含INTO子句
		```
		2.	打开游标
		```sql
		open cursor_name
		```
		3.	提取游标
		```
				fetch cursor_name into variable_list
					variable_list必须与游标提取的结果集类型相同，cursor_name%rowtype
			关闭游标
				close cursor    显式游标使用完后必须关闭
			--隐式游标，配合for...in...loop语句使用
			begin
			  for v_row in (select e.ename,d.dname,e.sal,s.grade 
				from scott.emp e ,scott.dept d, scott.salgrade s
				where e.deptno = d.deptno 
				and e.sal >= s.losal and e.sal <= s.hisal 
				and d.dname = 'SALES') loop
					   dbms_output.put_line('姓名：'||v_row.ename
								||'  部门名称：'||v_row.dname 
								||'  薪资：'||v_row.sal
								||'  工资等级：'||v_row.grade);
				   end loop;  
				end;
		```		
###	子程序  存储过程
```sql
--创建存储过
create [or replace] procedure  --创建存储过程，可指定运行过程需传递的参数
<procedure_name> [(<parameter list>)]
	is|as  
	<local variable declaration>
	begin
		<executable statements>  --包括在存储过程中要执行的语句
			[exception  --处理异常
			<exception handlers>]
	end;
								
				--创建存储过程proc_add_dept，向mytable中插入数据
	create or replace procedure proc_add_dept(
				--参数列表
		deptno number,
		dname varchar2,
		loc varchar2
	) 
	is
	--声明
		begin
		  insert into mytable values (deptno,dname,loc);
		  commit;
		end proc_add_dept;

  -- 调用存储过程
		exec[ute] procedure_name(parameter_list);
```	
*	参数传递的三种方式：
	*	按位置传递参数：
	*	按名称传递参数：常用，不易出错
	*	混合方式传递参数：当其中一个参数按名称传递后，后面的参数必须同样按照名称传递
* 传递参数三种模式：
	*	in  用于接收调用程序的值，且是默认的参数模式
	*	out  用于向调用程序返回值
	*	in out  用于接收调用程序的值，并向调用程序返回更新的值
### 其他操作				
* 将权限分配给其他用户
```
SQL> GRANT EXECUTE ON find_emp TO MARTIN;
SQL> GRANT EXECUTE ON swap TO PUBLIC;
```			
*	删除存储过程
```
drop procedure procedure_name
```
### 存储过程编写规范
1.	存储过程	中不可以直接使用DDL语句，可以通过动态SQL实现。但是不建议频繁的使用DDL语句
2.	存储过程必须有相应的出错处理功能
3.	存储过程变量使用`%type`或`%rowtype`类型
4.	必须在存储过程中做异常捕获，并将异常信息通过`os_Msg`变量输出
5.	`-1~-19999`的异常为oracle定义的异常代码
6.	存储过程必须包含两个输出参数分别用于标识过程的执行状态及过程提示信息
7.	`when others`必须放置在异常处理代码的最后面作为缺省处理器处理没有显式处理的异常
### 数据字典<br>
数据字典是Oracle存放有关数据库信息的地方，其用途是用来描述数据的。比如一个表的创建者信息，创建时间信息，所属表空间信息，用户访问权限信息等。数据库数据字典是一组表和视图结构。它们存放在SYSTEM表空间中.当用户在对数据库中的数据进行操作时遇到困难就可以访问数据字典来查看详细的信息。用户可以用SQL语句访问数据库数据字典。

* `user_objects`是oracle字典表的试图，他包含了通过DDL建立的所有对象
* `user_source`  数据字典视图包含存储过程的代码文本
* `dba_objects`  所有用户对象的基本信息
* `dba_tables`  所有用户的所有表信息
* `dba_views`  所有用户的所有视图信息
* `dba_sequences`  所有用户的所有序列信息
### 视图
* `user`视图以`user`为前缀，用来记录用户对象的信息
- `V$`视图：以`v$`为前缀，用来记录与数据库相关的性能统计状态信息
- `gv$`视图：以`gv$`为前缀，用来记录分布式环境下所有实例的动态信息


判断值是否为空，使用 is null

**where进行筛选时可以使用表的别名不可以使用列的别名，where字句中必须使用完整列名**
### 函数
* `NVL`函数表达式可以是数字型，字符型和日期型；
* `ROUND`和`RUNC`函数用于数字型或日期型
- 分析函数
- 单行函数
- 分组函数
- 汇总函数

### 在oracle中，对用户拥有的所用数据库对象统称为`模式`

### oracle索引的分类及使用
oracle中创建索引的语句：位图索引关键字	BITMAP  唯一索引关键字UNIQUE  基于函数的索引要有函数

### oracle中基于存储过程的数据泵导入导出命令是exp/imp


### oracle中分区表的优缺点：
- 优点
1. 改善查询性能：对分区对象的查询可以仅搜索自己关心的分区，提高检索速度。（变相的索引）
2. 增强可用性：如果表的某个分区出现故障，表在其他分区的数据仍然可用；
3. 维护方便：如果表的某个分区出现故障，需要修复数据，只修复该分区即可；
4. 均衡I/O：可以把不同的分区映射到磁盘以平衡I/O，改善整个系统性能。
- 缺点： 
#### 分区表相关：已经存在的表没有方法可以直接转化为分区表。不过 Oracle 提供了在线重定义表的功能。

#### oracle中导出实用程序exp 导入实用程序imp
