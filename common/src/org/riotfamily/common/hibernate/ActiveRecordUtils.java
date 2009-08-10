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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.hibernate;

import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.Hibernate;

public final class ActiveRecordUtils {
	
	private ActiveRecordUtils() {
	}

	public static Serializable getId(ActiveRecord record) {
		return ActiveRecord.getSessionFactory()
				.getClassMetadata(Hibernate.getClass(record))
				.getIdentifier(record, EntityMode.POJO);
	}
	
	public static Serializable getIdAndSaveIfNecessary(ActiveRecord record) {
		Serializable id = getId(record);
		if (id == null) {
			record.save();
		}
		return getId(record);
	}
	
	public static ActiveRecord load(Class<? extends ActiveRecord> type, 
			Serializable id) {
		
		return ActiveRecord.load(type, id);
	}
	
}
