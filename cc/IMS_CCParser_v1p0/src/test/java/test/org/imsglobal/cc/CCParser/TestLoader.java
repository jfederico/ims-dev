package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestLoader.java $
 * $Id: TestLoader.java 227 2011-01-08 18:26:55Z drchuck $
 **********************************************************************************
 *
 * Copyright (c) 2010 IMS GLobal Learning Consortium
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.imsglobal.cc.CartridgeLoader;


public class TestLoader implements CartridgeLoader {

  private static final String MANIFEST="/imsmanifest.xml";
  private static final String QTI_ASSESSMENT="/assessment.xml";
  private static final String QTI_QB="/qb.xml";
  private static final String DISCUSSION="/dt.xml";
  private static final String WEBLINK="/wl.xml";
  
  private static final String MANIFEST_STRING="imsmanifest.xml";
  private static final String QTI_ASS_STRING="assessment.xml";
  private static final String QTI_QB_STRING="question_bank.xml";
  private static final String DISCUSSION_STRING="discussion.xml";
  private static final String WEBLINK_STRING="weblink.xml";
  
  private static final Map<String, String> content;
  
  static {
    content=new HashMap<String, String>();
    content.put(MANIFEST_STRING, MANIFEST);
    content.put(QTI_ASS_STRING, QTI_ASSESSMENT);
    content.put(QTI_QB_STRING, QTI_QB);
    content.put(DISCUSSION_STRING, DISCUSSION);
    content.put(WEBLINK_STRING, WEBLINK);
  }
  
  public InputStream getFile(String the_target) throws FileNotFoundException, IOException {
    File target;
    try {
      if (content.containsKey(the_target)) {
        URL temp=this.getClass().getResource(content.get(the_target));
        target=new File(temp.toURI());
      } else {
      throw new FileNotFoundException();
      }
    } catch (Exception e) {
      throw new IOException(e.getMessage());
    }
    return new FileInputStream(target);
  }
  
  
  

}
