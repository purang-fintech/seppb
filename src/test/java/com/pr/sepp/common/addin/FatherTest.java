package com.pr.sepp.common.addin;

public class FatherTest {
	private String name;

	public FatherTest() {
		System.out.println("--父类的无参构造函数--");
	}

	public FatherTest(String name) {
		this.name = name;
		System.out.println("--父类的有参构造函数--" + this.name);
	}

	static {
		System.out.println("--父类的静态代码块--");
	}
	{
		System.out.println("--父类的非静态代码块--");
	}

	public void speak() {
		System.out.println("--父类的方法--");
	}

	// 加入一个main程序后
	public static void main(String[] args) {
		System.out.println("--父类主程序--");
		FatherTest father1 = new FatherTest();
		FatherTest father = new FatherTest("父亲的名字");
		father1.speak();
		father.speak();
	}
}
