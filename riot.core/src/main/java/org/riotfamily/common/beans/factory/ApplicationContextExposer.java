package org.riotfamily.common.beans.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Bean that exposes the current application context the the 
 * <code>ExternalAppliationContextBeanImporter</code>.
 * </p>
 * @since 9.1.0.OSGI
 * @author Lars Behnke [lbehnke at bdal dot de]
 */
public class ApplicationContextExposer implements ApplicationContextAware, BeanNameAware {

	private String beanName;
	
	private static Map<String, ApplicationContext> contexts = new HashMap<String, ApplicationContext>();
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		contexts.put(beanName, applicationContext);
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}
	
	public static Object getBean(String contextName, String beanName) {
		ApplicationContext ctx = getContext(contextName);
		Object bean = ctx.getBean(beanName);
		if (bean == null) {
			throw new BeanCreationException("External application context '" + contextName + 
					"' does not expose an bean named '" + beanName + "'.");
		}
		return bean;
	}
	
	public static <T> Collection<T> listBeansOfType(String contextName, Class<T> clazz) {
		ApplicationContext ctx = getContext(contextName);
		return SpringUtils.listBeansOfType(ctx, clazz);
	}
	
	public static <T> Collection<T> listOrderedBeansIncludingAncestors(String contextName, Class<T> clazz) {
		ApplicationContext ctx = getContext(contextName);
		return SpringUtils.orderedBeansIncludingAncestors(ctx, clazz);
	}
	
	
	private static ApplicationContext getContext(String contextName) {
		ApplicationContext ctx = contexts.get(contextName);
		if (ctx == null) {
			throw new BeanCreationException("Could not access external application context '" + contextName + 
					"'. Please check whether an ApplicationContextExposer bean has been configured.");
		}
		return ctx;
	}

}
