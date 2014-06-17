package org.imsglobal.basiclti.provider.admintool;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.StringUtils;
import org.imsglobal.basiclti.consumersecret.api.ConsumerSecretService;
import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.JsonView;
/**
 * Controller that handles all the html and json rendering fot the admin.jsp page.
 * @author Roland Groen (roland@edia.nl)
 *
 */
@Controller
public class OauthAdmin {
	@Autowired
	protected ConsumerSecretService consumerSecretService;

	@Autowired
	protected JsonView jsonView;

	@RequestMapping(value = "/addsecret.html", method = RequestMethod.POST)
	public String addSecret(@RequestParam String consumerKey, @RequestParam String consumerSecret) {
		consumerSecretService.setConsumerSecret(consumerKey, consumerSecret);
		return "redirect:admin.html";
	}

	@RequestMapping(value = "/admin.html")
	public String admin(ModelMap modelMap) {
		modelMap.addAttribute("secrets", consumerSecretService.searchOauthConsumerSecrets());
		return "admin";
	}

	private String[][] createData(List<OauthConsumerSecret> secrets) {
		String[][] aaData = new String[secrets.size()][2];
		int index = 0;
		for (OauthConsumerSecret oauthConsumerSecret : secrets) {
			aaData[index][0] = oauthConsumerSecret.getConsumerKey();
			aaData[index][1] = oauthConsumerSecret.getConsumerSecret();
			index++;
		}
		return aaData;
	}

	@RequestMapping(value = "/deletesecret.html", method = RequestMethod.POST)
	public String deleteSecret(@RequestParam String consumerKey) {
		consumerSecretService.setConsumerSecret(consumerKey, null);
		return "redirect:index.html";
	}

	@RequestMapping(value = "/secrets.json", method = RequestMethod.GET)
	public ModelAndView secrets(ModelMap modelMap) {
		List<OauthConsumerSecret> secrets = consumerSecretService.searchOauthConsumerSecrets();
		String[][] aaData = createData(secrets);
		modelMap.addAttribute("aaData", aaData);
		return new ModelAndView(jsonView, modelMap);
	}

	@RequestMapping(value = "/secrets.json", method = RequestMethod.POST)
	public ModelAndView secrets(ModelMap modelMap, @RequestParam String value, @RequestParam String target, @RequestParam String key,
	        @RequestParam String secret,
	        HttpServletRequest request,
	        HttpServletResponse response) throws IOException {
		String newKeyValue = null;
		String newSecretValue = null;
		if (StringUtils.equalsIgnoreCase(target, "key")) {
			if (!StringUtils.equals(key, value)) {
				if (StringUtils.isNotEmpty(value)) {
					consumerSecretService.setConsumerSecret(key, null);
					consumerSecretService.setConsumerSecret(value, secret);
				} else {
					consumerSecretService.setConsumerSecret(key, value);
				}
			}
			newKeyValue = value;
			newSecretValue = secret;
		} else if (StringUtils.equalsIgnoreCase(target, "secret")) {
			consumerSecretService.setConsumerSecret(key, value);
			newKeyValue = key;
			newSecretValue = value;
		}
		List<OauthConsumerSecret> secrets = new ArrayList<OauthConsumerSecret>(1);
		if (StringUtils.isNotEmpty(newKeyValue) && StringUtils.isNotEmpty(newSecretValue)) {
			OauthConsumerSecret oauthConsumerSecret = new OauthConsumerSecret();
			oauthConsumerSecret.setConsumerKey(newKeyValue);
			oauthConsumerSecret.setConsumerSecret(newSecretValue);
			secrets.add(oauthConsumerSecret);
		}
		modelMap.addAttribute("aaData", createData(secrets));
		return new ModelAndView(jsonView, modelMap);
	}
}
