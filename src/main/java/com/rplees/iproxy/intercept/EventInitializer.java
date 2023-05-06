package com.rplees.iproxy.intercept;

import com.rplees.iproxy.intercept.pipeline.EventPipeline;

public interface EventInitializer {
	void init(EventPipeline pipeline);
	
	public class DefaultEventInitializer implements EventInitializer {
		@Override
		public void init(EventPipeline pipeline) {
		}
	}
}
