package com.example.demo;

import def.dom.HTMLCollectionOf;
import def.dom.HTMLDivElement;
import jsweet.util.StringTypes;

import static def.dom.Globals.document;

public class Sweet {
	
	public static void main(String[] args) {
		HTMLCollectionOf<HTMLDivElement> nodeList = document.getElementsByTagName(StringTypes.div);
		for (HTMLDivElement element : nodeList) {
			element.innerText = "Hello again in vanilla JS";
		}
	}
}
