package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestHandler.java $
 * $Id: TestHandler.java 227 2011-01-08 18:26:55Z drchuck $
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
import org.imsglobal.cc.AuthorizationHandler;
import org.imsglobal.cc.DefaultHandler;
import org.imsglobal.cc.DiscussionHandler;
import org.imsglobal.cc.Handler;
import org.imsglobal.cc.LearningApplicationResourceHandler;
import org.imsglobal.cc.MetadataHandler;
import org.imsglobal.cc.QTIItem;
import org.imsglobal.cc.QuestionBankHandler;
import org.imsglobal.cc.WebContentHandler;
import org.imsglobal.cc.WebLinkHandler;
import org.jdom.Element;
import org.jmock.Expectations;
import org.jmock.Mockery;


public class TestHandler extends DefaultHandler implements AssessmentHandler, DiscussionHandler, AuthorizationHandler,
MetadataHandler, LearningApplicationResourceHandler, QuestionBankHandler,
WebContentHandler, WebLinkHandler {

  private QTIItem item;
  private Element dt;
  private Element weblink;
  private Element question_bank;
  private Element assessment;
  
  public void addQTIMetadataXml(Element the_md) {
   ass.addQTIMetadataXml(the_md);
  }

  public void setAssessmentDetails(String the_ident, String the_title) {
    ass.setAssessmentDetails(the_ident, the_title);
  }

  private AssessmentHandler ass;
  private DiscussionHandler dis;
  private AuthorizationHandler auth;
  private LearningApplicationResourceHandler lar;
  private MetadataHandler md;
  private QuestionBankHandler qb;
  private WebContentHandler wc;
  private WebLinkHandler wl;
  private Handler handler;
  
  public TestHandler() {
    item=null;
    dt=null;
    weblink=null;
    question_bank=null;
    assessment=null;
    Mockery context=new Mockery();
    ass=context.mock(AssessmentHandler.class);
    md=context.mock(MetadataHandler.class);
    auth=context.mock(AuthorizationHandler.class);
    dis=context.mock(DiscussionHandler.class);
    lar=context.mock(LearningApplicationResourceHandler.class);
    qb=context.mock(QuestionBankHandler.class);
    wc=context.mock(WebContentHandler.class);
    wl=context.mock(WebLinkHandler.class);
    handler=context.mock(Handler.class);
    context.checking(new Expectations() {{
      ignoring(ass);
      ignoring(md);
      ignoring(auth);
      ignoring(dis);
      ignoring(lar);
      ignoring(qb);
      ignoring(wc);
      ignoring(wl);
      ignoring(handler);
    }});
  }
  
  public void
  setAssessmentHandler(AssessmentHandler the_ass) {
    ass=the_ass;
  }
  
  public void setDis(DiscussionHandler dis) {
    this.dis = dis;
  }

  public void setAuth(AuthorizationHandler auth) {
    this.auth = auth;
  }

  public void setLar(LearningApplicationResourceHandler lar) {
    this.lar = lar;
  }

  public void setMd(MetadataHandler md) {
    this.md = md;
  }

  public void setQb(QuestionBankHandler qb) {
    this.qb = qb;
  }

  public void setWc(WebContentHandler wc) {
    this.wc = wc;
  }

  public void setWl(WebLinkHandler wl) {
    this.wl = wl;
  }

  public void seHandler(Handler wl) {
    this.handler = wl;
  }
  
  public void endAssessment() {
    ass.endAssessment();
  }

  public void setAssessmentXml(Element the_xml) {
    ass.setAssessmentXml(the_xml);
    assessment=the_xml;
  }

  public void setPresentationXml(Element the_xml) {
    ass.setPresentationXml(the_xml);
  }

  public void setQTIComment(String the_comment) {
    ass.setQTIComment(the_comment);
  }

  public void setQTICommentXml(Element the_xml) {
    ass.setQTICommentXml(the_xml);
  }

  public void setSection(String ident, String title) {
    ass.setSection(ident, title);
  }

  public void setSectionXml(Element the_xml) {
    ass.setSectionXml(the_xml);
  }

  public void startAssessment(String the_file_name, boolean isProtected) {
    ass.startAssessment(the_file_name, isProtected);
  }


  public void addQTIMetadataField(String label, String entry) {
    ass.addQTIMetadataField(label, entry);
  }

  public void endQTIMetadata() {
    ass.endQTIMetadata();

  }

  public void startQTIMetadata() {
    ass.startQTIMetadata();
  }

  public void endCCFolder() {
    handler.endCCFolder();
  }

  public void endCCItem() {
    handler.endCCItem();
  }

  public void endDependency() {
    handler.endDependency();
  }

  public void endManifest() {
    handler.endManifest();
  }

  public void endResource() {
    handler.endResource();
  }

  public void setCCItemXml(Element the_xml) {
    handler.setCCItemXml(the_xml);
  }

  public void setManifestXml(Element the_xml) {
    handler.setManifestXml(the_xml);
  }

  public void setResourceXml(Element the_xml) {
    handler.setResourceXml(the_xml);
  }

  public void startCCFolder(String the_id) {
    handler.startCCFolder(the_id);
  }

  public void startCCItem(String the_id, String the_title) {
    handler.startCCItem(the_id, the_title);
  }

  public void startDependency(String source, String target) {
    handler.startDependency(source, target);
  }

  public void startManifest() {
    handler.startManifest();
  }

  public void startResource(String id, boolean isProtected) {
    handler.startResource(id, isProtected);
  }

  public void addAttachment(String attachment_path) {
    dis.addAttachment(attachment_path);
  }

  public void endDiscussion() {
    dis.endDiscussion();
  }

  public void startDiscussion(String topic_name, String text_type, String text,
      boolean isProtected) {
    dis.startDiscussion(topic_name, text_type, text, isProtected);
  }

  public void setAuthorizationService(String cartridgeId, String webservice_url) {
    auth.setAuthorizationService(cartridgeId, webservice_url);
  }

  public void setAuthorizationServiceXml(Element the_node) {
    auth.setAuthorizationServiceXml(the_node);
  }

  public void endAuthorization() {
    auth.endAuthorization();
  }

  public void startAuthorization(boolean isCartridgeScope,
      boolean isResourceScope, boolean isImportScope) {
    auth.startAuthorization(isCartridgeScope, isResourceScope, isImportScope);
  }

  public void setManifestMetadataXml(Element the_md) {
    md.setManifestMetadataXml(the_md);
  }

  public void endManifestMetadata() {
    md.endManifestMetadata();
  }

  public void setResourceMetadataXml(Element the_md) {
    md.setResourceMetadataXml(the_md);
  }

  public void startManifestMetadata(String schema, String schema_version) {
    md.startManifestMetadata(schema, schema_version);
  }

  public void addFile(String the_file) {
    handler.addFile(the_file);
  }

  public void endLearningApplicationResource() {
    lar.endLearningApplicationResource();
  }

  public void startLearningApplicationResource(String entry_point,
      boolean isProtected) {
    lar.startLearningApplicationResource(entry_point, isProtected);
  }

  public void endQuestionBank() {
    qb.endQuestionBank();
  }

  public void setQuestionBankXml(Element the_xml) {
    qb.setQuestionBankXml(the_xml);
    question_bank=the_xml;
  }

  public void startQuestionBank(String the_file_name, boolean isProtected) {
    qb.startQuestionBank(the_file_name, isProtected);
  }

  public void endWebContent() {
    wc.endWebContent();
  }

  public void startWebContent(String entry_point, boolean isProtected) {
    wc.startWebContent(entry_point, isProtected);
  }

  public void endWebLink() {
    wl.endWebLink();
  }

  public void setWebLinkXml(Element the_xml) {
    wl.setWebLinkXml(the_xml);
    weblink=the_xml;
  }

  public void startWebLink(String the_title, String the_url, String the_target,
      String the_window_features, boolean isProtected) {
    wl.startWebLink(the_title, the_url, the_target, the_window_features, isProtected);
  }

  public void addAssessmentItem(QTIItem the_item) {
    ass.addAssessmentItem(the_item);
    if (item==null) {
      item=the_item;
    }
  }

  public void addQuestionBankItem(QTIItem the_item) {
    qb.addQuestionBankItem(the_item);
    if (item==null) {
      item=the_item;
    }
  }
  
  public void setQuestionBankDetails(String the_ident) {
    qb.setQuestionBankDetails(the_ident);
  }
  
  public void setDiscussionXml(Element the_element) {
    dis.setDiscussionXml(the_element);
    dt=the_element;
  }

  public Element
  getDiscussion() {
    return dt;
  }
  
  public QTIItem
  getItem() {
    return item;
  }
  
  public Element
  getWeblink() {
    return weblink;
  }
  
  public Element
  getQB() {
    return question_bank;
  }
  
  public Element
  getAssessment() {
    return assessment;
  }
  
}
