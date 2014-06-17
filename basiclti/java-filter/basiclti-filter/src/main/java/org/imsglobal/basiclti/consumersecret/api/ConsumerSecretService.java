package org.imsglobal.basiclti.consumersecret.api;
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
import java.util.List;

import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;

/**
 * 
 * This service definition handles the persistence of the consumer secret. 
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
public interface ConsumerSecretService {
	
	/**
	 * Gets a consumer secret, returns null if none found.
	 * @param consumerKey
	 * @return a secret if one is stored, null if the key does not have a secret assiciated.
	 */
	String getConsumerSecret(String consumerKey);

	/**
	 * Fetches all the available secrets.
	 * @return
	 */
	List<OauthConsumerSecret> searchOauthConsumerSecrets();

	/**
	 * Stores a consumer secret. Also supports the implicit deletion by setting the secret to null or an empty string.
	 * @param consumerKey, the key, may never be null.
	 * @param consumerSecret, the secret, can be null or empty string in order to delete the key/secret association.
	 */
	void setConsumerSecret(String consumerKey, String consumerSecret);
}
