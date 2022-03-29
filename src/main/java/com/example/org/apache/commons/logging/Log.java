package com.example.org.apache.commons.logging;

public interface Log {

	boolean isFatalEnabled();
	boolean isErrorEnabled();
	boolean isWarnEnabled();
	boolean isInfoEnabled();
	boolean isDebugEnabled();
	boolean isTraceEnabled();
	void fatal(Object message);

	void fatal(Object message, Throwable t);
	void error(Object message);
	void error(Object message, Throwable t);
	void warn(Object message);
	void warn(Object message, Throwable t);
	void info(Object message);
	void info(Object message, Throwable t);
	void debug(Object message);
	void debug(Object message, Throwable t);
	void trace(Object message);
	void trace(Object message, Throwable t);
}
