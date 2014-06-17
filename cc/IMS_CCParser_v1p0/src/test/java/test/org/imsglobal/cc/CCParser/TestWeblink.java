package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestWeblink.java $
 * $Id: TestWeblink.java 227 2011-01-08 18:26:55Z drchuck $
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

import org.imsglobal.cc.DefaultHandler;
import org.imsglobal.cc.Parser;
import org.imsglobal.cc.WebLinkHandler;
import org.jdom.Element;
import org.jmock.Expectations;
import org.jmock.Mockery;


import junit.framework.TestCase;

public class TestWeblink extends TestCase {

  private TestLoader utils;
  private Parser parser;
  
  public void
  setUp() throws Exception {
    utils=new TestLoader();
    parser=Parser.createCartridgeParser(utils);
  }
  
/*  
  <wl:webLink xmlns:wl="http://www.imsglobal.org/xsd/imswl_v1p0">
    <title>Wikipedia - Psychology</title>
    <url href="http://en.wikipedia.org/wiki/Psychology" target="_self" windowFeatures="width=100, height=100"/>
  </wl:webLink>
*/  
  
  public void
  testWeblinkParse() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    
    final WebLinkHandler hx=context.mock(WebLinkHandler.class);
    
    context.checking(new Expectations() {{

      oneOf(hx).startWebLink("Wikipedia - Psychology", 
                             "http://en.wikipedia.org/wiki/Psychology", 
                             "_self", 
                             "width=100, height=100", 
                             false);
      oneOf(hx).setWebLinkXml(with(any(Element.class)));
      oneOf(hx).endWebLink();
      
    }});
    
    ((TestHandler)handler).setWl(hx);
    
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
      Element wl=((TestHandler)handler).getWeblink();
      assertEquals(wl.getChildText("title"),"Wikipedia - Psychology");
      assertEquals(wl.getChildren().size(), 2);
      assertEquals(wl.getChild("url").getAttributeValue("target"),"_self");
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
  
}
