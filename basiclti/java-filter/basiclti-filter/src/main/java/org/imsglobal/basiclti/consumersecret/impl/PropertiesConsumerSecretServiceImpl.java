package org.imsglobal.basiclti.consumersecret.impl;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.imsglobal.basiclti.consumersecret.api.ConsumerSecretService;
import org.imsglobal.basiclti.consumersecret.api.UnexpectedConsumerServiceExcption;
import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;
import org.springframework.core.io.support.PropertiesLoaderSupport;
/**
 * Property file based implementation of the {@link ConsumerSecretService}, making use of the spring {@link PropertiesLoaderSupport} to
 * provide the properties. Please review the documentation of the {@link PropertiesLoaderSupport} class on how to set the property file.
 * 
 * The property file is treated as read only.
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
public class PropertiesConsumerSecretServiceImpl extends PropertiesLoaderSupport implements ConsumerSecretService {

	public String getConsumerSecret(String consumerKey) {
		Properties properties = getPropertiesInternal();
		return properties.getProperty(consumerKey);
	}

	protected Properties getPropertiesInternal() {
		try {
			return mergeProperties();
		} catch (IOException e) {
			throw new UnexpectedConsumerServiceExcption(e);
		}
	}

	public List<OauthConsumerSecret> searchOauthConsumerSecrets() {
		Properties properties = getPropertiesInternal();
		List<OauthConsumerSecret> rv = new ArrayList<OauthConsumerSecret>(properties.size());
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String consumerSecret = ObjectUtils.toString(entry.getValue());
			String consumerKey = ObjectUtils.toString(entry.getKey());
			if (StringUtils.isNotEmpty(consumerKey) && StringUtils.isNotEmpty(consumerSecret)) {
				rv.add(new OauthConsumerSecret(consumerKey, consumerSecret));
			}
		}
		return rv;
	}

	public void setConsumerSecret(String consumerKey, String consumerSecret) {
		throw new UnsupportedOperationException("This method is not supported by the PropertiesConsumerSecretServiceImpl");
	}
}
