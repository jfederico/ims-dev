package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestQuestionBank.java $
 * $Id: TestQuestionBank.java 227 2011-01-08 18:26:55Z drchuck $
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
import org.imsglobal.cc.QTIItem;
import org.imsglobal.cc.QuestionBankHandler;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jmock.Expectations;
import org.jmock.Mockery;


import junit.framework.TestCase;

public class TestQuestionBank extends TestCase {
  private TestLoader utils;
  private Parser parser;
  
  private static final Namespace QTI_NS=Namespace.getNamespace("qti","http://www.imsglobal.org/xsd/ims_qtiasiv1p2");
  
  public void
  setUp() throws Exception {
    utils=new TestLoader();
    parser=Parser.createCartridgeParser(utils);
  }
  
  public void
  testQBParse() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    
    final QuestionBankHandler hx=context.mock(QuestionBankHandler.class);
    
    context.checking(new Expectations() {{
      oneOf(hx).startQuestionBank("question_bank.xml", true);
      oneOf(hx).setQuestionBankDetails("QB_I_00004_R");
      exactly(14).of(hx).addQuestionBankItem(with(any(QTIItem.class)));
      oneOf(hx).setQuestionBankXml(with(any(Element.class)));
      oneOf(hx).endQuestionBank();
    }});
    
    ((TestHandler)handler).setQb(hx);
    
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
      Element qb=((TestHandler)handler).getQB().getChild("objectbank",QTI_NS);
      assertEquals(qb.getChildren("item",QTI_NS).size(),14);
      assertEquals(qb.getAttributeValue("ident"),"QB_I_00004_R");
      Element child1=(Element)qb.getChildren("item",QTI_NS).get(0);
      Element child14=(Element)qb.getChildren("item",QTI_NS).get(13);
      assertEquals(child1.getAttributeValue("ident"),"QUE_104065");
      assertEquals(child1.getChildren("itemfeedback",QTI_NS).size(), 7);
      assertEquals(child1.getChild("resprocessing",QTI_NS).getChildren().size(),6);
      assertEquals(child14.getAttributeValue("ident"),"QUE_104081");
      assertEquals(child14.getChildren("itemfeedback",QTI_NS).size(), 3);
      assertEquals(child14.getChild("resprocessing",QTI_NS).getChildren().size(),2);
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
}
