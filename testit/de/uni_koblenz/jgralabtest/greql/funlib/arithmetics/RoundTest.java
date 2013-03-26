package de.uni_koblenz.jgralabtest.greql.funlib.arithmetics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uni_koblenz.jgralab.greql.funlib.FunLib;

public class RoundTest extends ArithmeticTest {

	@Test
	public void testInt() {
		for (int i = 0; i < intValues.length; i++) {
			long expected = intValues[i];
			Object result = FunLib.apply("round", intValues[i]);
			assertEquals(expected, result);
		}
	}

	@Test
	public void testLong() {
		for (int i = 0; i < longValues.length; i++) {
			long expected = longValues[i];
			Object result = FunLib.apply("round", longValues[i]);
			assertEquals(expected, result);
		}
	}

	@Test
	public void testDouble() {
		for (int i = 0; i < doubleValues.length; i++) {
			long expected = Math.round(doubleValues[i]);
			Object result = FunLib.apply("round", doubleValues[i]);
			assertEquals(expected, result);
		}
	}
}
