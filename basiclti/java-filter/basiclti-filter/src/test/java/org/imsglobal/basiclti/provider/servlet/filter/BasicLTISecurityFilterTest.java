package org.imsglobal.basiclti.provider.servlet.filter;

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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * Test for the filter, making use of the {@link ServletRunner} class from httpunit.
 * @author Roland Groen (roland@edia.nl)
 *
 */
@ContextConfiguration(locations = { "classpath*:org/imsglobal/basiclti/consumersecret/impl/hibernate-services.xml",
        "classpath*:org/imsglobal/basiclti/consumersecret/impl/hibernate-test.xml" })
public class BasicLTISecurityFilterTest extends AbstractJUnit38SpringContextTests {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDoFilter() throws Exception {
		ServletRunner runner = new ServletRunner(BasicLTISecurityFilterTest.class.getResourceAsStream("web.xml"), "/admin");
		ServletUnitClient client = runner.newClient();
		try {
			client.getResponse("http://localhost/admin/test");
			fail("Expecting HttpException");
		} catch (HttpException e) {
			assertTrue(true);
		}

	}

	public void testDoFilterHMACSHA1() throws Exception {
		ServletRunner runner = new ServletRunner(BasicLTISecurityFilterTest.class.getResourceAsStream("web.xml"), "/admin");
		ServletUnitClient client = runner.newClient();
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, "one", "secret", serviceProvider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lti_message_type", "basic-lti-launch-request");
		map.put("roles", "Teacher");
		map.put("resource_link_id", "LINK_ID");
		map.put("user_id", "USER_ID");
		map.put("context_id", "CONTEXT_ID");

		map.put(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
		OAuthMessage newRequestMessage = accessor.newRequestMessage("GET", "http://localhost/admin/test", map.entrySet());
		GetMethodWebRequest method = new GetMethodWebRequest("http://localhost/admin/test");

		List<Entry<String, String>> parameters = newRequestMessage.getParameters();
		for (Entry<String, String> entry : parameters) {
			method.setParameter(entry.getKey(), entry.getValue());
		}
		WebResponse response = client.getResponse(method);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getInputStream()));
		try {
			String readLine = reader.readLine();
			assertEquals("OK!", readLine);
		} finally {
			reader.close();
		}
	}

	public void testDoFilterPlain() throws Exception {
		ServletRunner runner = new ServletRunner(BasicLTISecurityFilterTest.class.getResourceAsStream("web.xml"), "/admin");
		ServletUnitClient client = runner.newClient();
		OAuthServiceProvider serviceProvider = new OAuthServiceProvider(null, null, null);
		OAuthConsumer consumer = new OAuthConsumer(null, "one", "secret", serviceProvider);
		OAuthAccessor accessor = new OAuthAccessor(consumer);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lti_message_type", "basic-lti-launch-request");
		map.put("roles", "Teacher");
		map.put("resource_link_id", "LINK_ID");
		map.put("user_id", "USER_ID");
		map.put("context_id", "CONTEXT_ID");

		//		map.put(OAuth.OAUTH_SIGNATURE_METHOD, "PLAINTEXT");
		OAuthMessage newRequestMessage = accessor.newRequestMessage("GET", "http://localhost/admin/test", map.entrySet());
		GetMethodWebRequest method = new GetMethodWebRequest("http://localhost/admin/test");

		List<Entry<String, String>> parameters = newRequestMessage.getParameters();
		for (Entry<String, String> entry : parameters) {
			method.setParameter(entry.getKey(), entry.getValue());
		}
		WebResponse response = client.getResponse(method);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getInputStream()));
		try {
			String readLine = reader.readLine();
			assertEquals("OK!", readLine);
		} finally {
			reader.close();
		}
	}
}
