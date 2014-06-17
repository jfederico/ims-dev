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

import java.util.List;

import org.imsglobal.basiclti.consumersecret.api.ConsumerSecretService;
import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Test for HibernateConsumerSecretServiceImpl
 * 
 * @author Roland Groen (roland@edia.nl)
 *
 */
@ContextConfiguration(locations =  {"classpath*:org/imsglobal/basiclti/consumersecret/impl/hibernate-services.xml", "classpath*:org/imsglobal/basiclti/consumersecret/impl/hibernate-test.xml"})
@TransactionConfiguration(defaultRollback=true)
public class HibernateConsumerSecretServiceImplTest extends AbstractJUnit38SpringContextTests {
	@Autowired
	ConsumerSecretService hibernateConsumerSecretService;
	
	@Override
	protected void setUp() throws Exception {
	    super.setUp();
	    // Clean up before each run..
	    List<OauthConsumerSecret> items = hibernateConsumerSecretService.searchOauthConsumerSecrets();
	    for (OauthConsumerSecret oauthConsumerSecret : items) {
	    	hibernateConsumerSecretService.setConsumerSecret(oauthConsumerSecret.getConsumerKey(), null);
        }
	}
	
	public void testGetConsumerSecret() {
		hibernateConsumerSecretService.setConsumerSecret("test", "sec");
		assertEquals("sec", hibernateConsumerSecretService.getConsumerSecret("test"));
	}

	public void testSearchOauthConsumerSecrets() {
		assertEquals(0, hibernateConsumerSecretService.searchOauthConsumerSecrets().size());
		for (int i = 0; i < 100; i++) {
			hibernateConsumerSecretService.setConsumerSecret("test", "secret-"+i);
        }
		assertEquals(1, hibernateConsumerSecretService.searchOauthConsumerSecrets().size());
		for (int i = 0; i < 100; i++) {
			hibernateConsumerSecretService.setConsumerSecret("test-"+i, "secret-"+i);
		}
		assertEquals(101, hibernateConsumerSecretService.searchOauthConsumerSecrets().size());
		assertNotNull(hibernateConsumerSecretService.getConsumerSecret("test"));
		hibernateConsumerSecretService.setConsumerSecret("test", null);
		hibernateConsumerSecretService.setConsumerSecret("test", null);
		assertNull(hibernateConsumerSecretService.getConsumerSecret("test"));
		assertEquals(100, hibernateConsumerSecretService.searchOauthConsumerSecrets().size());
		assertEquals("secret-99", hibernateConsumerSecretService.getConsumerSecret("test-99"));
	}

	public void testSetConsumerSecret() {
		hibernateConsumerSecretService.setConsumerSecret("test", "sec2");
		assertEquals("sec2", hibernateConsumerSecretService.getConsumerSecret("test"));
		hibernateConsumerSecretService.setConsumerSecret("test", "");
		assertNull(hibernateConsumerSecretService.getConsumerSecret("test"));
		hibernateConsumerSecretService.setConsumerSecret("test", null);
		assertNull(hibernateConsumerSecretService.getConsumerSecret("test"));
	}
}
