package org.imsglobal.basiclti.consumersecret.model;
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

/**
 * Representation of a OAuth consumer key/secret pair.
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class OauthConsumerSecret {
	String consumerKey;
	String consumerSecret;
	public OauthConsumerSecret(){}
	public OauthConsumerSecret(String consumerKey, String consumerSecret) {
	    super();
	    this.consumerKey = consumerKey;
	    this.consumerSecret = consumerSecret;
    }
	public String getConsumerKey() {
    	return consumerKey;
    }
	public String getConsumerSecret() {
    	return consumerSecret;
    }
	public void setConsumerKey(String consumerKey) {
    	this.consumerKey = consumerKey;
    }
	public void setConsumerSecret(String consumerSecret) {
    	this.consumerSecret = consumerSecret;
    }
}
