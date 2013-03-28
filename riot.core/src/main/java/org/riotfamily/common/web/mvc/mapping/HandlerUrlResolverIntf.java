package org.riotfamily.common.web.mvc.mapping;

public interface HandlerUrlResolverIntf {

	public String getUrlForHandler(Class<?> handlerClass, Object... vars);
	public String getUrlForHandler(String handlerName, Object... vars);
}
