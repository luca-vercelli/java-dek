package org.jd.core.v1.util;

/**
 * A couple of objects of two different types A and B
 * 
 * @author luca vercelli 2021
 *
 * @param <A>
 * @param <B>
 */
public class Couple<A, B> {

	public A a;
	public B b;

	public Couple() {
	}

	public Couple(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public String toString() {
		return "(" + (a == null ? "null" : a.toString()) + "," + (b == null ? "null" : b.toString()) + ")";
	}

}
