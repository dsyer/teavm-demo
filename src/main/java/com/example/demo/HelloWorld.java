package com.example.demo;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ViolationMessage;

public class HelloWorld implements Exports {

	private am.ik.yavi.core.Validator<Foo> bundle;

	public static void main(String[] args) {
		export(new HelloWorld());
	}

	HelloWorld() {
		this.bundle = ValidatorBuilder.<Foo>of(Foo.class).constraintOnTarget(foo -> StringUtils.hasText(foo.getValue()),
				"value", ViolationMessage.of("foo.value.notEmpty", "Foo has empty value")).build();
	}

	@Override
	public String hello(String... args) {
		return Arrays.asList(args).stream().collect(Collectors.joining(" "));
	}

	@Override
	public void greet() {
		System.out.println(hello("Hello", "World"));
	}

	@Override
	public boolean validate(String value) {
		return bundle.validate(new Foo(value)).isEmpty();
	}

	@JSBody(params = "arg", script = "main.exports = arg;")
	public static native void export(Exports arg);
}

interface Exports extends JSObject {
	void greet();

	String hello(String... args);

	boolean validate(String value);
}

class Foo {
	private String value;

	public Foo(String string) {
		value = string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}