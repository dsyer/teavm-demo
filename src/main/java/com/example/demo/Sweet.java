package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sweet {

	private static List<String> values = Arrays.asList("Hello", "World");

	public static void main(String[] args) {
		System.out.println(new Sweet().hello(args));
	}
	
	public String hello(String... args) {
		List<String> values = new ArrayList<>(Sweet.values);
		values.addAll(Arrays.asList(args));
		return values.stream().collect(Collectors.joining(" "));
	}
}
