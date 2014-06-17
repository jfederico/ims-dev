package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestManifest.java $
 * $Id: TestManifest.java 227 2011-01-08 18:26:55Z drchuck $
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

import org.imsglobal.cc.AuthorizationHandler;
import org.imsglobal.cc.DefaultHandler;
import org.imsglobal.cc.Handler;
import org.imsglobal.cc.MetadataHandler;
import org.imsglobal.cc.Parser;
import org.jdom.Element;
import org.jmock.Expectations;
import org.jmock.Mockery;


import junit.framework.TestCase;

public class TestManifest extends TestCase {
  
  /*
    Test class for manifest files (only). 
    Manifest as per imsmanifest.xml
  
   */
  
  private TestLoader utils;
  private Parser parser;
  
  public void
  setUp() throws Exception {
    utils=new TestLoader();
    parser=Parser.createCartridgeParser(utils);
  }
  
  public void
  testManifestParse() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    
    final Handler hx=context.mock(Handler.class);
    
    context.checking(new Expectations() {{
      oneOf (hx).startManifest();
      oneOf (hx).setManifestXml(with(any(Element.class))); 
      oneOf (hx).startCCFolder("I_00000");
      oneOf (hx).startCCItem(  "I_00001", "Learning Objectives");
      oneOf (hx).startCCFolder("I_00002");
      oneOf (hx).startCCItem("I_00003", "Pretest");
      oneOf (hx).startCCItem("I_00005", "Wikipedia - Psychology");
      oneOf (hx).startCCItem("I_00006", "Psychology of Faces");
      exactly(4).of(hx).setCCItemXml(with(any(Element.class)));
      oneOf (hx).startResource("I_00001_R", false);
      oneOf (hx).addFile("I_00001_R/Learning_Objectives.html");
      oneOf (hx).startResource("I_00003_R", false);
      oneOf (hx).addFile("assessment.xml");
      oneOf (hx).startResource("I_00005_R", false);
      oneOf (hx).addFile("weblink.xml");
      oneOf (hx).startResource("I_00006_R",false);
      oneOf (hx).addFile("discussion.xml");
      oneOf (hx).startResource("I_00006_Media", false);
      oneOf (hx).addFile("I_00006_Media/angry_person.jpg");
      oneOf (hx).startResource("I_media_R", false);
      oneOf (hx).addFile("I_media_R/smiling_dog.jpg");
      oneOf (hx).startDependency("I_00006_R", "I_00006_Media");
      oneOf (hx).startDependency("I_00006_R", "I_media_R");
      //question bank
      oneOf (hx).startResource("I_00004_R", true); //this is a protected resource
      oneOf (hx).addFile("question_bank.xml");
      exactly(2).of(hx).endDependency();
      exactly(7).of(hx).setResourceXml(with(any(Element.class)));
      exactly(7).of(hx).endResource();
      exactly(4).of(hx).endCCItem();
      exactly(2).of(hx).endCCFolder();
      oneOf (hx).endManifest();
    }});
    ((TestHandler)handler).seHandler(hx);
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
  
  public void
  testAuhthorization() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    final AuthorizationHandler auth=context.mock(AuthorizationHandler.class);
    context.checking(new Expectations() {{
      oneOf (auth).startAuthorization(false, true, true);
      oneOf (auth).setAuthorizationService("76543", "http://ccdemo.pearsoncmg.com/wps/cc_auth/cgi-bin/cc_auth.asmx");
      oneOf (auth).setAuthorizationServiceXml(with(any(Element.class)));
      oneOf (auth).endAuthorization();
    }});
    ((TestHandler)handler).setAuth(auth);
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
  
  public void
  testMetadata() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    final MetadataHandler md=context.mock(MetadataHandler.class);
    context.checking(new Expectations() {{
      oneOf (md).startManifestMetadata("IMS Common Cartridge", "1.0.0");
      oneOf (md).setManifestMetadataXml(with(any(Element.class)));
      oneOf (md).endManifestMetadata();
      oneOf (md).setResourceMetadataXml(with(any(Element.class)));  
    }});
    ((TestHandler)handler).setMd(md);
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
  
}
