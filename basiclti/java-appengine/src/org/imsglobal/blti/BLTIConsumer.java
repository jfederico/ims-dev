/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/basiclti/java-appengine/src/org/imsglobal/blti/BLTIConsumer.java $
 * $Id: BLTIConsumer.java 231 2011-01-12 00:39:57Z drchuck $
 **********************************************************************************
 * Copyright (c) 2008 IMS GLobal Learning Consortium
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

/*  Note:  This code was initially developed by 
    Chuck Wight of the University of Utah and practicezone.org 
@author Chuck Wight
*/

package org.imsglobal.blti;

import java.util.Random;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Cached;

@Cached
public class BLTIConsumer {
	@Id String oauth_consumer_key;
	String secret;
	
	BLTIConsumer() {}

	BLTIConsumer(String oauth_consumer_key) {
		Random random =  new Random();
        long r1 = random.nextLong();
        long r2 = random.nextLong();
        String hash1 = Long.toHexString(r1);
        String hash2 = Long.toHexString(r2);
        this.secret = hash1 + hash2;
        this.oauth_consumer_key = oauth_consumer_key;
    }
	
	static void create(String oauth_consumer_key) {
		ObjectifyService.register(BLTIConsumer.class);
		ObjectifyService.begin().put(new BLTIConsumer(oauth_consumer_key));
	}
	
	static void delete(String oauth_consumer_key) {
		ObjectifyService.register(BLTIConsumer.class);
		ObjectifyService.begin().delete(new Key<BLTIConsumer>(BLTIConsumer.class,oauth_consumer_key));
	}
	
	static String getSecret(String oauth_consumer_key) {
		ObjectifyService.register(BLTIConsumer.class);
		BLTIConsumer c = ObjectifyService.begin().find(BLTIConsumer.class,oauth_consumer_key);
		return (c==null?null:c.secret);
	}
}
