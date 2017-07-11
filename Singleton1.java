/*
	懒汉式单例
*/
public class Singleton1{
	private static Singleton1 singleton = null;
	private Singleton1(){}
	public synchronized Singleton1 getInstance(){
		if(singleton == null){
			singleton = new Singleton1();
		}
		return singleton;
	}
}
/*
 * 饥汉式单例
*/
class Singleton2{
	private static volatile Singleton2 singleton = new Singleton2();
	private Singleton2(){}
	public Singleton2 getInstance(){
		return singleton;
	}
}
/*
 * 双重检验锁实现惰性单例模式
 * 第一重判断只在单例未实现时判断，并且假设多个线程通过了第一个if，这时候他们进入synchronized关键字控制的同步代码块中，只有一个线程
 * 独占，这时候他判断如果singleton还是null的话就初始化，然后该线程退出了同步代码块，刚才同时跟之前线程一起等待的线程使用权的线程们，
 * 再次检查第二重判断的时候singleton就不为null了，此时会直接返回，这样就保证了线程安全，至此过程结束
 * 
 */
class Singleton3{
	private static volatile Singleton3 singleton = null;
	private Singleton3() {}
	public static Singleton3 getInstance(){
		//
		if (null == singleton) {
			synchronized (Singleton3.class) {
				if (null == singleton) {
					singleton = new Singleton3();
				}
			}
		}
		return singleton;
	}
	
}
/*
 * 静态内部类实现惰性单例
 * 有类加载过程jvm控制线程安全
 * 调用return Inner.getSingleton时，singleton在第一次调用时实例化
 * 具体细节需要了解内部类
 */
class Singleton4{
	private static class Inner{
		//final可有可无
		private static final Singleton4 singeton = new Singleton4();
	}
	private Singleton4(){};
	public Singleton4 getSingleton(){
		return Inner.singeton;
	}
}
/*
 * 枚举实现单例
 * 涉及到类的序列化和防止反射攻击
 * 枚举实现的方式是最容易也是最佳的
 * 具体细节需要深入了解java枚举
 */
enum Singleton{
	INSTANCE;
}
