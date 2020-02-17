package com.pr.sepp.common.addin;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MathTest {
	@Test
	public void transfer(){
		float a = 1.1f;
		float b = 1.8f;

		assertTrue((int) a == (int) b);
	}
}
