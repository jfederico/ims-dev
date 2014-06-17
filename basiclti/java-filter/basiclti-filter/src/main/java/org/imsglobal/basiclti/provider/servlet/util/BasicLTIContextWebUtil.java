package org.imsglobal.basiclti.provider.servlet.util;

/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2011 IMS GLobal Learning Consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License. 
 *
 **********************************************************************************/
import javax.servlet.http.HttpSession;

import org.imsglobal.basiclti.provider.api.BasicLtiContext;

/**
 * Helper class that fetches the {@link BasicLtiContext} from the session.
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class BasicLTIContextWebUtil {
	/**
	 * Gets the {@link BasicLtiContext}, if set.
	 * @param session
	 * @return the {@link BasicLtiContext} available in the sessio, null if not set.
	 */
	public static BasicLtiContext getBasicLtiContext(HttpSession session) {
		return (BasicLtiContext) session.getAttribute(getSessionAttributeName());
	}

	protected static String getSessionAttributeName() {
	    return "session-" + BasicLtiContext.class.getName();
    }

	/**
	 * Sets the {@link BasicLtiContext} in the session, set to null to delete
	 * @param session
	 * @param basicLtiContext
	 */
	public static void setBasicLtiContext(HttpSession session, BasicLtiContext basicLtiContext) {
		if (basicLtiContext != null) {
			session.setAttribute(getSessionAttributeName(), basicLtiContext);
		} else {
			session.removeAttribute(getSessionAttributeName());
		}
	}
}
