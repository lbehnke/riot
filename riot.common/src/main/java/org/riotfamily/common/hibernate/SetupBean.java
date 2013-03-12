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
package org.riotfamily.common.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SetupBean extends AbstractConditionalSetupBean {

	private List<?> objects;
	
	public SetupBean(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setObjects(List<?> objects) {
		this.objects = objects;
	}

	@Override
	protected void doSetup(Session session) {
		for (Object object : objects) {
			session.save(object);
		}
	}
	
}
