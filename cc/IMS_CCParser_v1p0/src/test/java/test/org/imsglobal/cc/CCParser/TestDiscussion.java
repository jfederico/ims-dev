package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestDiscussion.java $
 * $Id: TestDiscussion.java 227 2011-01-08 18:26:55Z drchuck $
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
import org.imsglobal.cc.DiscussionHandler;
import org.imsglobal.cc.Parser;
import org.jdom.Element;
import org.jmock.Expectations;
import org.jmock.Mockery;


import junit.framework.TestCase;

public class TestDiscussion extends TestCase {

  private TestLoader utils;
  private Parser parser;
  
  public void
  setUp() throws Exception {
    utils=new TestLoader();
    parser=Parser.createCartridgeParser(utils);
  }
  
  /*
  <dt:topic xmlns:dt="http://www.imsglobal.org/xsd/imsdt_v1p0">
    <title>The Psychology of Faces</title>
    <text texttype="text/html">Is recognition of human emotional states learned or innate? Might there be a culture in the world that expresses joy through scowling and fear through laughter? Does your dog smile when he's happy? &lt;br/> &lt;img src="$IMS-CC-FILEBASE$../I_media_R/smiling_dog.jpg"/></text>
    <attachments>
        <attachment href="../I_00006_Media/angry_person.jpg"/>
    </attachments>
   </dt:topic>
   */
  
  public void
  testDiscussionParse() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    
    final DiscussionHandler hx=context.mock(DiscussionHandler.class);
    
    context.checking(new Expectations() {{
      oneOf(hx).startDiscussion("The Psychology of Faces", 
                                "text/html", 
                                "Is recognition of human emotional states learned or innate? Might there be a culture in the world that expresses joy through scowling and fear through laughter? Does your dog smile when he's happy? <br/> <img src=\"$IMS-CC-FILEBASE$../I_media_R/smiling_dog.jpg\"/>", 
                                false);
      oneOf(hx).addAttachment("../I_00006_Media/angry_person.jpg");
      oneOf(hx).setDiscussionXml(with(any(Element.class)));
      oneOf(hx).endDiscussion();
    }});
    
    ((TestHandler)handler).setDis(hx);
    
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
      Element dt=((TestHandler)handler).getDiscussion();
      assertEquals(dt.getChildText("title"), "The Psychology of Faces");
      assertEquals(dt.getChildText("text"),"Is recognition of human emotional states learned or innate? Might there be a culture in the world that expresses joy through scowling and fear through laughter? Does your dog smile when he's happy? <br/> <img src=\"$IMS-CC-FILEBASE$../I_media_R/smiling_dog.jpg\"/>");
      assertEquals(dt.getChildren().size(), 3);
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
}
