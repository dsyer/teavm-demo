package com.example.demo;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSObjects;

import am.ik.yavi.builder.ValidatorBuilder;
import am.ik.yavi.core.ViolationMessage;

public class HelloWorld implements Exports {

	private am.ik.yavi.core.Validator<String> bundle;

	public static void main(String[] args) throws Exception {
		export(new HelloWorld());
		// System.out.println(Foo.class.getConstructor(String.class).newInstance("foo"));
		LogFactory.getLog(HelloWorld.class).info("Logging: " + new Foo("foo"));
	}

	HelloWorld() {
		this.bundle = ValidatorBuilder.of(String.class).constraintOnTarget(foo -> StringUtils.hasText(foo),
				"value", ViolationMessage.of("value.notEmpty", "Empty value")).build();
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
	public boolean validate(JSMapLike<JSObject> map) {
		String value = JSObjects.toString(map.get("value"));
		try {
			return bundle.validate(value).isEmpty();
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	@JSBody(params = "arg", script = "main.exports = arg;")
	public static native void export(Exports arg);
}

interface Exports extends JSObject {
	void greet();

	String hello(String... args);

	boolean validate(JSMapLike<JSObject> map);
}

class Foo {
	private String value;

	public Foo() {}

	public Foo(String string) {
		value = string;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Foo: value=" + value;
	}
}