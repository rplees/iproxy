package com.rplees.iproxy.utils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.WeakHashMap;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.proto.Proto;
import com.rplees.iproxy.proto.Proto.HttpProto;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

/**
 * @author rplees
 * @email rplees.i.ly@gmail.com
 * @version 1.0  
 * @created May 6, 2023 12:35:16 PM
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Logs {
	private static Map<String, Logger> cache = new WeakHashMap();
	public static final Charset charset = Charset.defaultCharset();
	public static final String DEFAULT_LOG_PATH = "logger";
	public static final String SUFFIX = ".log";
	public static final Object lock = new Object();
	
	public static Logger logger(String logPath, Proto local) {
		final String startWith = "http://";
		final int startWithLength = startWith.length();
		
		String name = "UNKNOWN";
		if(local instanceof HttpProto) {
			name = ((HttpProto) local).uri();
		}
		
		if(name.startsWith(startWith)) {
			name = name.substring(startWithLength);
		}
		
		int pathEndPos = name.indexOf('?');
		if (pathEndPos > -1) {
			name = name.substring(0, pathEndPos);
		}
		
		String host = local.host();
		if(host.startsWith(startWith)) {
			host = host.substring(startWithLength);
		}
		
		if(name.startsWith(host)) {
			name = name.substring(host.length());
		}
		
		return getLogger(logPath + "/" + host, name);
	}
	
	public static Logger getLogger(String path, String taskName) {
		Logger logger = cache.get(taskName);
		if (logger != null) {
			return logger;
		}
		
		synchronized (lock) {
			logger = cache.get(taskName);
			
			if(logger != null) {
				return logger;
			}
			
			logger = build(path, taskName);
			cache.put(taskName, logger);
		}
		
		return logger;
	}

	private static Logger build(String path, String taskName) {
		ILoggerFactory factory = LoggerFactory.getILoggerFactory();
		if(factory instanceof LoggerContext) {
			LoggerContext context = (LoggerContext) factory;
			RollingFileAppender appender = getLogbackAppender(context, path, taskName);
			ch.qos.logback.classic.Logger logger = context.getLogger(taskName);
			logger.addAppender(appender);
			logger.setAdditive(false);
			logger.setLevel(Level.ALL);
			return logger;
		}
		
		Logger logger = factory.getLogger(taskName);
		logger.warn("找不到具体对应的日志.");
		return logger;
	}
	
	public static RollingFileAppender<?> getLogbackAppender(LoggerContext context, String path, String taskName) {
		
		RollingFileAppender appender = new RollingFileAppender();
		appender.setName(taskName);
		appender.setContext(context);
		appender.setFile(new File(path, taskName + SUFFIX).getAbsolutePath());

		// 设置日志文件输出格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
        encoder.setCharset(charset);
        encoder.start();

		// 设置日志记录器的滚动策略
		TimeBasedRollingPolicy<?> policy = new TimeBasedRollingPolicy<>();
		policy.setFileNamePattern(path + taskName + ".%d{yyyy-MM-dd}" + SUFFIX);
		policy.setParent(appender);
		policy.setContext(context);
		policy.start();

		appender.setRollingPolicy(policy);
		appender.setEncoder(encoder);
		appender.start();
		return appender;
	}
}
