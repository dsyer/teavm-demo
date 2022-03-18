# Helo World with TeaVM and Spring Boot

[TeaVM](https://teavm.org) is a Java to JavaScript transpiler. The goal of this project is to show how you can write code in Java and get the transpiled version of it to run in the browser and in Node.js. We are looking for a nice developer experience for sharing code on the server and in the client application, maybe one day validation logic from `@Valid`.

We are not looking for a way to create a GUI in the browser using Java - TeaVM supports this use case at a very low level, which is good, but not a goal for this research. [GWT](https://github.com/gwtproject/gwt) has to transpile Java to JavaScript as well, but it has much bigger goals, and a whole XML configuration system that feels like a step to far for our modest goals. The same is true of `Vaadin`. Both projects are focused on the goal of building a GUI for the browser without using the browser. We want to try and build a JavaScript library that can be used in the browser, but also potentially in Node.js. There might even be a way to transpile again, for example to WASM.

## Spring Boot

Spring Boot will be used as a vehicle for hosting the generated JavaScript, but any web server can do that just as well. To get started we create a [new web application](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.6.4&packaging=jar&jvmVersion=11&groupId=com.example&artifactId=demo&name=demo&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.demo&dependencies=webflux). Unpack it andopen it up in an IDE. There is a `DemoApplication` that you can run and see the app starting:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.4)

2022-03-18 09:12:14.122  INFO 2982482 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication using Java 11.0.14 on tower with PID 2982482 (/home/dsyer/dev/scratch/jsweet-demo/target/classes started by dsyer in /home/dsyer/dev/scratch/jsweet-demo)
2022-03-18 09:12:14.125  INFO 2982482 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to 1 default profile: "default"
2022-03-18 09:12:15.081  INFO 2982482 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2022-03-18 09:12:15.088  INFO 2982482 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 1.29 seconds (JVM running for 1.584)
```

It doesn't do anything else yet.

## Logic in Java

Suppose we have some utility code that we want to share. Here it is in Java in skeleton form:

```java
public class HelloWorld {

	public String hello(String... args) {
		return Arrays.asList(args).stream().collect(Collectors.joining(" "));
	}

	public void greet() {
		System.out.println(hello("Hello", "World"));
	}

}
```

There is a simple `void->void` method and another that transforms input strings into a single value. To use this code in Java is trivial - you just create an intance of `HelloWorld` and call the methods. We intentionally kept it very simple - just some logic and a bit of printing to console, no Spring, and stick to primitive-ish types, like `String` in the public API.

## Transpiling with TeaVM

TeaVM has an API you could use to transpile that Java, and it also has plugins that you can use at build time. We can add the plugin to our Maven build:

```xml
<plugin>
	<?m2e execute onConfiguration,onIncremental?>
	<groupId>org.teavm</groupId>
	<artifactId>teavm-maven-plugin</artifactId>
	<version>${teavm.version}</version>
	<executions>
		<execution>
			<id>teavm-client</id>
			<goals>
				<goal>compile</goal>
			</goals>
			<configuration>
				<targetDirectory>${project.build.directory}/classes/static</targetDirectory>
				<mainClass>com.example.demo.HelloWorld</mainClass>
			</configuration>
		</execution>
	</executions>
</plugin>
```

It will fail for now because `HelloWorld` has no `main()` method. TeaVM only works to transpile `main()` methods, but we can work with that. Let's implement a `main()` method and see what happens:

```java
public class HelloWorld {

	public static void main(String[] args) {
		HelloWorld hello = new HelloWorld();
		hello.greet();
		System.out.println(hello.hello("Goodbye"));
	}

	...
}
```

If you compile it and run it you know it will print some messages:

```
$ java -cp target/classes/ com.example.demo.HelloWorld
Hello World
Goodbye
```

What about the generated JavaScript? Well it already works in the browser:

```html
<html>

<body>
	<h2>Hello</h2>
	<script src="./classes.js"></script>
</body>

</html>
```

If you put that in `src/main/resources/static/index.html` and run the Spring Boot application you will find that a function `main()` has been defined in the window globals. You can call it to see the messages printed to the console in the browser:

```javascript
> main()
Hello World
Goodbye
< undefined
```

That is kind of awesome. The generated JavaScript is about 50kB. It looks something like this:

```javascript
var main;
(function () { 
	// ... a big function body that assigns the value of main
})();
```

## Exporting a Public API

TeaVM lets us append some JavaScript to the generated function above. So we can use that to export an API. To do that we need a marker interface with our target methods:

```java
import org.teavm.jso.JSObject;

interface Exports extends JSObject {
	void greet();
	String hello(String... args);
}
```

we need `HelloWorld` to implement this interface and also export it using an annotation from TeaVM. We add the `implements` keyword and a new static method, which is declared `native` so it can't be called from Java, but TeaVM will provide an implementation in the JavaScript:

```java
public class HelloWorld implements Exports {

	...

	@JSBody(params = "arg", script = "main.exports = arg;")
	public static native void export(Exports arg);
}
```

The `main()` method is going to change to call that native method:

```java
public static void main(String[] args) {
	export(new HelloWorld());
}
```

When we do that and look at the generated JavaScript we will find that `main` is now not just a function, but also has `exports`. Using the same HTML in the browser:

```javascript
> main()
undefined
> main.exports.greet()
Hello World
> main.exports.hello(["Hello", "World"])
'Hello World'
```

## Create a JavaScript Library

### CommonJS

Now we can turn that JavaScript into a library. We could manually edit it and wrap the generated code with something that exposes the public API in a JavaScript friendly way. This would work with CommonJS in Node.js or in the browser:

```javascript
(function() {
	// generated code goes here
	main();
	if (typeof module !== 'undefined' && module.exports) {
		// CommonJS
		module.exports.hello = (...args) => main.exports.hello(args);
		module.exports.greet = () => main.exports.greet();
	} else {
		// Browser globals
		window.hello = (...args) => main.exports.hello(args);
		window.greet = () => main.exports.greet();
	}
})();
```

With the same `index.html` as before we could load that JavaScript and just do this in the console:

```javascript
> greet()
Hello World
> hello("Hello", "World")
'Hello World'
```

with Node.js:

```javascript
> var main = require('./target/classes/static/classes.js')
> main.greet()
Hello World
> main.hello("Hello", "World")
'Hello World'
```

### ES6

This would work for ES6 also in Node.js and in the browser:

```javascript
let main = {exports:{}};
(function(module) {
	// generated code goes here
	main();
	module.exports.hello = (...args) => main.exports.hello(args);
	module.exports.greet = () => main.exports.greet();
})(main);

let hello = (...args) => main.exports.hello(args);
let greet = () => main.exports.greet();

export {greet, hello};
export default greet;
```

In Node.js we have to call the file `*.js`:

```javascript
> var main = await import('./target/classes/static/classes.mjs')
undefined
> main.greet()
Hello World
undefined
> main.hello("Hello", "World")
'Hello World'
```

We would have to change the `index.html` to use the new module:

```html
<html>

<body>
	<h2>Hello</h2>
	<script type="module">
		import { greet, hello } from './classes.js';
		greet();
		console.log(hello("Hello", "World"));
	</script>
</body>

</html>
```

> NOTE: Spring Boot doesn't serve `*.mjs` with the correct mime type by default, so we need to use `.js` as a file extension for the module script. It's a hassle because Node.js likes `.mjs`.

Loading this page the console will print "Hello World" twice. We can't call those functions directly from the console because they are in a module.

## Generic Wrapper

We can choose either the CommonJS or the ES6 route and write some code that uses the generated JavaScript from TeaVM. In ES6:

```javascript
let main = {exports:{}};
var script;

if (typeof fetch === 'undefined') {
  let dir = await import('path').then(path => path.dirname(import.meta.url).replace('file:/', ''));
  script = await import('fs').then(fs => fs.readFileSync(dir + '/classes.js', {encoding: 'utf8'}));
} else {
  script = await fetch('./classes.js').then(response => response.text());
}

Function('return function(module, exports) {\n' +
  script
  + '\nmain(); module.exports = main.exports;}')()(main, main.exports);

let hello = (...args) => main.exports.hello(args);
let greet = () => main.exports.greet();

export {greet, hello};
export default greet;
```

We can use that module in Node.js and in the browser as above.