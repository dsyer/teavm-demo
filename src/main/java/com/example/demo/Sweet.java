package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sweet {

	private static List<String> values = Arrays.asList("Hello", "World");
	
	public String hello() {
		return values.stream().collect(Collectors.joining(" "));
	}
}
