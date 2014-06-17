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

import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

/**
 * Test for PropertiesConsumerSecretServiceImpl
 *   
 * @author Roland Groen (roland@edia.nl)
 *
 */
@ContextConfiguration(locations = "classpath*:org/imsglobal/basiclti/consumersecret/impl/properties-services.xml")
public class PropertiesConsumerSecretServiceImplTest extends AbstractJUnit38SpringContextTests {

	@Autowired
	PropertiesConsumerSecretServiceImpl propertiesConsumerSecretServiceImpl;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		propertiesConsumerSecretServiceImpl.setLocation(new ClassPathResource("test_secrets.properties", getClass()));
	}

	public void testGetConsumerSecret() {
		assertEquals("secret", propertiesConsumerSecretServiceImpl.getConsumerSecret("one"));
	}

	public void testSearchOauthConsumerSecrets() {
		assertEquals(4, propertiesConsumerSecretServiceImpl.searchOauthConsumerSecrets().size());
		for (OauthConsumerSecret sec : propertiesConsumerSecretServiceImpl.searchOauthConsumerSecrets()) {
			if ("one".equals(sec.getConsumerKey())) {
				assertEquals("secret", sec.getConsumerSecret());
			}
	        
        }
	}

	public void testSetConsumerSecret() {
		try {
			propertiesConsumerSecretServiceImpl.setConsumerSecret("one", "two");
			fail("UnsupportedOperationException");
		} catch (Exception e) {
		}

	}

}
