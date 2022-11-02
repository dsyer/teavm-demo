package com.example.demo;

import org.teavm.interop.Export;
import org.teavm.interop.Import;

public class HelloWorld {

	public static void main(String[] args) throws Exception {
	}

	@Export(name = "echo")
	public static int echo() {
		return get();
	}

	@Import(module = "env", name = "get")
	public static native int get();
}