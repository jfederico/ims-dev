package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestAssessment.java $
 * $Id: TestAssessment.java 227 2011-01-08 18:26:55Z drchuck $
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

import org.imsglobal.cc.AssessmentHandler;
import org.imsglobal.cc.DefaultHandler;
import org.imsglobal.cc.Parser;
import org.imsglobal.cc.QTIItem;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jmock.Expectations;
import org.jmock.Mockery;


import junit.framework.TestCase;

public class TestAssessment extends TestCase {
  
  /*
    Test class for manifest files (only). 
    Manifest as per imsmanifest.xml
  
   */
  
  private TestLoader utils;
  private Parser parser;
  
  private static final Namespace QTI_NS=Namespace.getNamespace("qti","http://www.imsglobal.org/xsd/ims_qtiasiv1p2");
  
  public void
  setUp() throws Exception {
    utils=new TestLoader();
    parser=Parser.createCartridgeParser(utils);
  }
  
  public void
  testAssessmentParse() {
    DefaultHandler handler = new TestHandler();
    Mockery context = new Mockery();
    
    final AssessmentHandler hx=context.mock(AssessmentHandler.class);
    
    context.checking(new Expectations() {{
      oneOf(hx).startAssessment("assessment.xml", false);
      oneOf(hx).setAssessmentDetails("QDB_1", "Pretest");
      oneOf(hx).setAssessmentXml(with(any(Element.class)));
      oneOf(hx).startQTIMetadata();
      oneOf(hx).addQTIMetadataField("cc_profile", "cc.exam.v0p1");
      oneOf(hx).addQTIMetadataField("qmd_assessmenttype", "Examintaion");
      exactly(2).of(hx).addAssessmentItem(with(any(QTIItem.class)));
      exactly(1).of(hx).addQTIMetadataXml(with(any(Element.class)));
      oneOf(hx).endQTIMetadata();
      oneOf(hx).endAssessment();
    }});
    
    ((TestHandler)handler).setAssessmentHandler(hx);
    
    try {
      parser.parse(handler);
      context.assertIsSatisfied();
      checkItem(((TestHandler)handler).getItem());
      checkAssessment(((TestHandler)handler).getAssessment());
    } catch (Exception pe) {
      System.err.println(pe.getMessage());
      pe.printStackTrace();
      fail();
    }
  }
  
  public void
  checkAssessment(Element the_element) {
    Element assessment=the_element.getChild("assessment",QTI_NS);
    assertNotNull(assessment);
    Element section=assessment.getChild("section",QTI_NS);
    assertNotNull(assessment);
    assertEquals(section.getChildren("item", QTI_NS).size(), 2);
    Element item1=(Element)section.getChildren("item", QTI_NS).get(0);
    Element item2=(Element)section.getChildren("item", QTI_NS).get(1);
    assertEquals(item1.getAttributeValue("ident"),"QUE_104045");
    assertEquals(item1.getChildren("itemfeedback", QTI_NS).size(),2);
    assertEquals(item2.getAttributeValue("ident"),"QUE_102010");
    assertEquals(item2.getChildren("itemfeedback", QTI_NS).size(),1);
  }
  
  public void
  checkItem(QTIItem the_item) {
    assertEquals(the_item.getIdent(), "QUE_104045");
    assertEquals(the_item.getTitle(), "Question 01");
    assertNotNull(the_item.getItem_metadata());
    assertNotNull(the_item.getPresentation());
    assertEquals(the_item.getItem_feedbacks().size(), 2);
    assertEquals(the_item.getResp_processings().size(), 1);
  }
  
  /*
   * <item ident="QUE_104045" title="Question 01">
        <!-- start multiplechoice -->
        <itemmetadata>
          <qtimetadata>
            <qtimetadatafield>
              <fieldlabel>cc_profile</fieldlabel>
              <fieldentry>cc.multiple_choice.v0p1</fieldentry>
            </qtimetadatafield>
            <qtimetadatafield>
              <fieldlabel>cc_question_category</fieldlabel>
              <fieldentry>Chapter 01</fieldentry>
            </qtimetadatafield>
          </qtimetadata>
        </itemmetadata>
        <presentation>
          <material>
            <mattext texttype="text/html">Which of the following best defines psychology?</mattext>
          </material>
          <response_lid ident="QUE_104045_RL" rcardinality="Single">
            <render_choice>
              <response_label ident="QUE_104045_A1">
                <material>
                  <mattext texttype="text/html">the scientific study of behavior and mental processes</mattext>
                </material>
              </response_label>
              <response_label ident="QUE_104045_A2">
                <material>
                  <mattext texttype="text/html">the science of mental and emotional disorder</mattext>
                </material>
              </response_label>
            </render_choice>
          </response_lid>
        </presentation>
        <resprocessing>
          <outcomes>
              <decvar minvalue="0" maxvalue="100" varname="SCORE" vartype="Decimal"/>
          </outcomes>
          <respcondition continue="Yes">
            <!-- note: general feedback is provided unconditionally -->
            <conditionvar>
              <other/>
            </conditionvar>
            <displayfeedback feedbacktype="Response" linkrefid="general_fb" />
          </respcondition>
          <respcondition title="Correct">
            <conditionvar>
              <varequal respident="QUE_104045_RL">QUE_104045_A1</varequal>
            </conditionvar>
            <setvar action="Set" varname="SCORE">100</setvar>
            <displayfeedback feedbacktype="Response" linkrefid="QUE_104045_A1_fb" />
            <displayfeedback feedbacktype="Response" linkrefid="correct_fb" />
          </respcondition>
        </resprocessing>
        <itemfeedback ident="QUE_104045_A1_fb">
          <flow_mat>
            <material>
              <mattext texttype="text/html"><![CDATA[Feedback for 1]]></mattext>
            </material>
          </flow_mat>
        </itemfeedback>
        <itemfeedback ident="QUE_104045_A2_fb">
          <flow_mat>
            <material>
              <mattext texttype="text/html"><![CDATA[Feedback for 2]]></mattext>
            </material>
          </flow_mat>
        </itemfeedback>
      </item>
   */
  
  
}
