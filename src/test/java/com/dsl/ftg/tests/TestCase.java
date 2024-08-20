package com.dsl.ftg.tests;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.jupiter.api.Disabled;

/**
 * 
 * @author Deiv
 */
public class TestCase {

	static Map<String, Integer> mapTest = new HashMap<>();
	
	List<String> sl = List.of("test", "anotherTest", "test", "otherTest");
	
	@Test
	@Disabled
	public void pathTest() {
		Path path = Paths.get(System.getProperty("user.home"), "Projects", "workspace-1", "FileTreeGenerator", "test.txt");
		Path anotherPath = Paths.get(System.getProperty("user.home"), "Projects");
	}
	
	@Test
	@Disabled
	public void mapIntegrityTest() {
		for(String str : sl) {
			if(!mapTest.containsKey(str)) {
				mapTest.put(str, 1);
				System.out.println(mapTest);
				continue;
			}
			mapTest.computeIfPresent(str, (key, value) -> value += 1);
			System.out.println(mapTest);
		}
	}
}
