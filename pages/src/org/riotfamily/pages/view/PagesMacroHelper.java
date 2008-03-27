/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.pages.cache.PageCacheUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageNode;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.6
 */
public class PagesMacroHelper {

	private PageDao pageDao;

	private PageResolver pageResolver;

	private PathCompleter pathCompleter;
	
	private HttpServletRequest request;

	
	public PagesMacroHelper(PageDao pageDao, PageResolver pageResolver,
			PathCompleter pathCompleter, HttpServletRequest request) {
		
		this.pageDao = pageDao;
		this.pageResolver = pageResolver;
		this.pathCompleter = pathCompleter;
		this.request = request;
	}
	
	
	public List getTopLevelPages(SiteFacade facade) {
		PageNode rootNode = pageDao.getRootNode();
		PageCacheUtils.addNodeTag(request, rootNode);
		return getVisiblePages(rootNode.getChildPages(facade.getSite()),
				EditModeUtils.isEditMode());
	}

	public Page getPageForUrl(String url, SiteFacade facade) {
		return pageResolver.resolvePage(url, request.getContextPath(),
				facade.getSite(), pathCompleter);
	}
	
	public Page getPageForHandler(String handlerName, SiteFacade facade) {
		return pageDao.findPageForHandler(handlerName, facade.getSite());
	}

	public List getPagesForHandler(String handlerName, SiteFacade facade) {
		return pageDao.findPagesForHandler(handlerName, facade.getSite());
	}

	private List getVisiblePages(List pages, boolean preview) {
		ArrayList result = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.isVisible(preview)) {
				result.add(page);
			}
		}
		return result;
	}

}
