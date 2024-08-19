package com.dsl.ftg.tests;

import java.nio.file.Paths;

import org.junit.Test;
import org.junit.jupiter.api.Disabled;

/**
 * 
 * @author Deiv
 */
public class testPath {

	@Test
	@Disabled
	public void pathTest() {
		System.out.println(Paths.get(System.getProperty("user.home").toString()));
	}
}
