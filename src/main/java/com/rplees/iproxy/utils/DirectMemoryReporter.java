package com.rplees.iproxy.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rplees.iproxy.remote.handler.ChannelBridgeCollector;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.internal.PlatformDependent;

public class DirectMemoryReporter {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final int k = 1024;

    public void startReport() {
    	ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(() -> {
			doReport();
			ChannelBridgeCollector.instance().stat();
		}, 5, 5, TimeUnit.SECONDS);
    }

    private void doReport() {
    	log.info("netty_direct_memory:{}KB", PlatformDependent.usedDirectMemory() / k);
    }
}
