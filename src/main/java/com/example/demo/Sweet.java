package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

public class Sweet implements Exported {

	private static List<String> values = Arrays.asList("Hello", "World");

	public static void main(String[] args) {
		export(new Sweet());
	}

	@Override
	public String hello(String... args) {
		List<String> values = new ArrayList<>(Sweet.values);
		values.addAll(Arrays.asList(args));
		return values.stream().collect(Collectors.joining(" "));
	}

	@Override
	public void greet() {
		System.out.println(hello());
	}

	@JSBody(params = "arg", script = "main.exported = arg;")
	public static native void export(Exported arg);

}

interface Exported extends JSObject {
	void greet();
	String hello(String... args);
}
