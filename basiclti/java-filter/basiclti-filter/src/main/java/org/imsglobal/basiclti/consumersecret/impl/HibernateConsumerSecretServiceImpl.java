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


import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.imsglobal.basiclti.consumersecret.api.ConsumerSecretService;
import org.imsglobal.basiclti.consumersecret.model.OauthConsumerSecret;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate based service implementation of the {@link ConsumerSecretService}, based on the {@link HibernateDaoSupport}. To use this 
 * service, you need to take the following steps:
 * <p>
 * 1. Import the services definition 
 * <pre>&lt;import resource="classpath*:org/imsglobal/basiclti/consumersecret/impl/hibernate-services.xml" /&gt;</pre>
 * </p>
 * <p>
 * 2. Make sure the placeholder properties are set, an example properties using Derby would be:
 * <pre>&lt;bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		&lt;property name="properties">
			&lt;props>
				&lt;prop key="hibernate.hbm2ddl.auto">update&lt;/prop>
				&lt;prop key="hibernate.dialect">org.hibernate.dialect.DerbyDialect&lt;/prop>
				&lt;prop key="jdbc.url">jdbc:derby:db-basiclti-filter;create=true&lt;/prop>
				&lt;prop key="jdbc.driverClassName">org.apache.derby.jdbc.EmbeddedDriver&lt;/prop>
				&lt;prop key="jdbc.username">&lt;/prop>
				&lt;prop key="jdbc.password">&lt;/prop>
			&lt;/props>
		&lt;/property>
	&lt;/bean></pre>
 *	</p>
 * <p>
 * 2. 
 * </p>
 * @author Roland Groen (roland@edia.nl)
 *
 */
@Transactional
public class HibernateConsumerSecretServiceImpl extends HibernateDaoSupport implements ConsumerSecretService {

	@Transactional
	public String getConsumerSecret(String consumerKey) {
		OauthConsumerSecret oauthConsumerSecret = (OauthConsumerSecret)getHibernateTemplate().get(OauthConsumerSecret.class, consumerKey);
		if (oauthConsumerSecret != null) {return oauthConsumerSecret.getConsumerSecret();}
		return null;
	}
	
	@Transactional(readOnly=true)
	@SuppressWarnings("unchecked")
    public List<OauthConsumerSecret> searchOauthConsumerSecrets() {
		return (List<OauthConsumerSecret>)getHibernateTemplate().findByCriteria(DetachedCriteria.forClass(OauthConsumerSecret.class).addOrder(Order.asc("consumerKey")));
	}
	
	@Transactional
	public void setConsumerSecret(String consumerKey, String consumerSecret) {
		if (StringUtils.isEmpty(consumerKey)) {
			return;
		}
		if (StringUtils.isEmpty(consumerSecret)) {
			OauthConsumerSecret oauthConsumerSecret = (OauthConsumerSecret)getHibernateTemplate().get(OauthConsumerSecret.class, consumerKey);
			if (oauthConsumerSecret != null) {
				getHibernateTemplate().delete(oauthConsumerSecret);
			}
			return;
		}
		OauthConsumerSecret oauthConsumerSecret = (OauthConsumerSecret)getHibernateTemplate().get(OauthConsumerSecret.class, consumerKey);
		if (oauthConsumerSecret != null) {
			oauthConsumerSecret.setConsumerSecret(consumerSecret);
			getHibernateTemplate().update(oauthConsumerSecret);
		} else {
			oauthConsumerSecret = new OauthConsumerSecret();
			oauthConsumerSecret.setConsumerKey(consumerKey);
			oauthConsumerSecret.setConsumerSecret(consumerSecret);
			getHibernateTemplate().save(oauthConsumerSecret);
			
		}

	}

}
