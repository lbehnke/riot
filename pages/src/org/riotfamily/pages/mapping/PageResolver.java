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
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.Site;
import org.springframework.util.StringUtils;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PageResolver {
	
	public static final String SITE_ATTRIBUTE = PageResolver.class.getName() + ".site";

	public static final String PAGE_ATTRIBUTE = PageResolver.class.getName() + ".page";

	private static final Object NOT_FOUND = new Object();
	
	private RiotLog log = RiotLog.get(PageResolver.class);

	private SitemapSchema sitemapSchema;
	
	
	public PageResolver(SitemapSchema sitemapSchema) {
		this.sitemapSchema = sitemapSchema;
	}

	/**
	 * Returns the first Site that matches the given request. The PathCompleter
	 * is used to strip the servlet mapping from the request URI.
	 * @return The first matching Site, or <code>null</code> if no match is found
	 */
	public Site getSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		if (site == null) {
			site = resolveSite(request);
			exposeSite((Site) site, request);
		}
		if (site == null || site == NOT_FOUND) {
			return null;
		}
		Site result = (Site) site;
		result.refreshIfDetached();
		return result; 
	}

	protected void exposeSite(Site site, HttpServletRequest request) {
		expose(site, request, SITE_ATTRIBUTE);
	}
	
	/**
	 * Returns the Page for the given request.
	 */
	public Page getPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		if (page == null) {
			page = resolvePage(request);
			exposePage((Page) page, request);
		}
		if (page == null || page == NOT_FOUND) {
			return null;
		}
		Page result = (Page) page;
		result.refreshIfDetached();
		return result;
	}
	
	protected void exposePage(Page page, HttpServletRequest request) {
		expose(page, request, PAGE_ATTRIBUTE);
	}
	
	/**
	 * Returns the previously resolved Page for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getPage(HttpServletRequest)} has been invoked before. 
	 */
	public static Page getResolvedPage(HttpServletRequest request) {
		Object page = request.getAttribute(PAGE_ATTRIBUTE);
		return page != NOT_FOUND ? (Page) page : null;
	}
	
	/**
	 * Returns the previously resolved Site for the given request.
	 * <p>
	 * <strong>Note:</strong> This method does not perform any lookups itself.
	 * Only use this method if you are sure that 
	 * {@link #getSite(HttpServletRequest)} has been invoked before. 
	 */
	public static Site getResolvedSite(HttpServletRequest request) {
		Object site = request.getAttribute(SITE_ATTRIBUTE);
		return site != NOT_FOUND ? (Site) site : null; 
	}

	/**
	 * Returns the Page which is requestable at the given URL. This may return
	 * <code>null</code> in case the given parameters do not match a page.
	 * 
	 * @param url url of the requestable page
	 * @param contextPath of the application in order to strip it
	 * @param fallbackSite in case the site can't be looked up, this site will
	 * 			be used to find the page
	 * @param pathCompleter in order to strip the servlet mapping
	 * @return the page matching the parameters or null if no page was found
	 */
	public Page resolvePage(String url, String contextPath, Site fallbackSite) {
		
		String host = ServletUtils.getHost(url);
		Site site = Site.loadByHostName(host);
		if (site == null) {
			log.debug("Could not find site for url '" + url + "'. Using fallback.");
			site = fallbackSite;
		}
		
		String path = ServletUtils.getPath(url);

		// Strip the contextPath if known
		if (StringUtils.startsWithIgnoreCase(path, contextPath)) {
			path = path.substring(contextPath.length());
		}
		if (path == null) {
			log.warn("The path is null. Can't continue.");
			return null;
		}
		path = getLookupPath(path);
		

		Page page = Page.loadBySiteAndPath(site, path);
		if (page == null) {
			log.debug("Haven't found a page for '" + site + path + "'. Trying to find a page through an alias.");
			PageAlias alias = PageAlias.loadBySiteAndPath(site, path);
			if (alias != null) {
				page = alias.getPage();
			}
		}
		
		log.debug("Page: " + page);

		return page;
	}	

	
	private Site resolveSite(HttpServletRequest request) {
		String hostName = request.getServerName();
		return Site.loadByHostName(hostName);
	}

	private Page resolvePage(HttpServletRequest request) {
		Site site = getSite(request);
		if (site == null) {
			return null;
		}
		String path = ServletUtils.getPathWithinApplication(request);
		String lookupPath = getLookupPath(path);
		Page page = Page.loadBySiteAndPath(site, lookupPath);
		if (page == null || !page.isRequestable() 
				|| !sitemapSchema.suffixMatches(page, path)) {
			
			return null;
		}
		return page;
	}
	
	public String getLookupPath(HttpServletRequest request) {
		return getLookupPath(ServletUtils.getPathWithinApplication(request));
	}
	
	public String getLookupPath(String path) {
		return FormatUtils.stripExtension(path);
	}
	
	private void expose(Object object, HttpServletRequest request,
			String attributeName) {
		
		if (object == null) {
			object = NOT_FOUND;
			log.debug("Exposing 'NOT_FOUND' as '" + attributeName + "'");
		}
		else {
			log.debug("Exposing '" + object + "' as '" + attributeName + "'");
		}
		request.setAttribute(attributeName, object);
	}
	
	/**
	 * Resets all internally used attributes.
	 * @param request
	 */
	public static void resetAttributes(HttpServletRequest request) {
		request.removeAttribute(SITE_ATTRIBUTE);
		request.removeAttribute(PAGE_ATTRIBUTE);
	}
}
