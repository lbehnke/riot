/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.web.mvc.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.common.beans.factory.ApplicationContextExposer;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.HandlerMapping;

/**
 * Class that performs URL lookups for handlers mapped by a 
 * {@link ReverseHandlerMapping} in an OSGi environment.
 * The bean collaborates with <code>ApplicationContextExposer</code>.
 * Mind the order of <contextNames> since it affects the context precedence.
 * 
 * @author Lars Behnke
 * @since 9.1.0.OSGI
 */
public class GlobalHandlerUrlResolver implements HandlerUrlResolverIntf {

	private String[] contextNames;

	private List<ReverseHandlerMapping> reverseMappings;

	@Required
	public void setContextNames(String[] contextNames) {
		this.contextNames = contextNames;
	}

	protected synchronized List<ReverseHandlerMapping> getMappings() {
		if (reverseMappings == null) {
			
			Collection<HandlerMapping> mappings = new ArrayList<HandlerMapping>();
			Collection<ReverseHandlerMappingAdapter> adapters = new ArrayList<ReverseHandlerMappingAdapter>();
			for (String contextName : contextNames) {
				mappings.addAll(ApplicationContextExposer.listBeansOfType(contextName, HandlerMapping.class));
				adapters.addAll(ApplicationContextExposer.listOrderedBeansIncludingAncestors(contextName, ReverseHandlerMappingAdapter.class));
			}
			reverseMappings = Generics.newArrayList();
			for (HandlerMapping mapping : mappings) {
				if (mapping instanceof ReverseHandlerMapping) {
					reverseMappings.add((ReverseHandlerMapping) mapping);
				}
				else {
					for (ReverseHandlerMappingAdapter adapter : adapters) {
						if (adapter.supports(mapping)) {
							reverseMappings.add(adapter.adapt(mapping));
							break;
						}
					}
				}
			}
		}
		return reverseMappings;
	}
	
	public String getUrlForHandler(Class<?> handlerClass, Object... vars) {
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerClass, vars);
			if (url != null) {
				return url;
			}
		}
		return null;
	}
	
	public String getUrlForHandler(String handlerName, Object... vars) {
		for (ReverseHandlerMapping mapping : getMappings()) {
			String url = mapping.getUrlForHandler(handlerName, vars);
			if (url != null) {
				return url;
			}
		}
		return null;
	}

	
}
