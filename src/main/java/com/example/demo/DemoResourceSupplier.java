package com.example.demo;

import org.teavm.classlib.ResourceSupplier;
import org.teavm.classlib.ResourceSupplierContext;

public class DemoResourceSupplier implements ResourceSupplier {

	@Override
	public String[] supplyResources(ResourceSupplierContext context) {
		return new String[] {"application.properties", "org/hibernate/validator/ValidationMessages.properties"};
	}
	
}
