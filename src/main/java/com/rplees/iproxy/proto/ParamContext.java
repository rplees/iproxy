package com.rplees.iproxy.proto;

public interface ParamContext {
	public ProtoRouter router();
	
	public class DefaultParamContext implements ParamContext {
		ProtoRouter router;
		
		public ProtoRouter router() {
			return router;
		}
		
		public static DefaultParamContext from(ProtoRouter router) {
			DefaultParamContext context = new DefaultParamContext();
			context.router = router;
			return context;
		}
	}
}
