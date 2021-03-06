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
package org.riotfamily.media.setup;

import org.riotfamily.media.model.RiotFile;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class RiotFileFactoryBean extends AbstractFactoryBean<RiotFile> {
	
	private Resource resource;
	
	public RiotFileFactoryBean() {
	}

	public RiotFileFactoryBean(Resource resource) {
		this.resource = resource;
	}
	
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	@Override
	public Class<RiotFile> getObjectType() {
		return RiotFile.class;
	}
	
	@Override
	protected RiotFile createInstance() throws Exception {
		return new RiotFile(resource.getFile());
	}
}
