package com.example;

public class Test
{
	@org.junit.Test
	public void test()
	{
		Foo r = Bar.of("abc");
		Foo z = Baz.of(123);
		A a = B.of(A.of());
	}
}
