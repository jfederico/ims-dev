package org.imsglobal.cc;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/main/java/org/imsglobal/cc/PrintHandler.java $
 * $Id: PrintHandler.java 227 2011-01-08 18:26:55Z drchuck $
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

import org.jdom.Element;

/* PJN NOTE:
 * This class is an example of what an implementer might want to do as regards overloading DefaultHandler.
 * In this case, messages are written to the screen. If a method in default handler is not overridden, then it does
 * nothing.
 */

public class PrintHandler   implements AssessmentHandler, DiscussionHandler, AuthorizationHandler,
                                       MetadataHandler, LearningApplicationResourceHandler, QuestionBankHandler,
                                       WebContentHandler, WebLinkHandler{


  
  public void setAssessmentDetails(String the_ident, String the_title) {
    System.err.println("assessment ident: "+the_ident +" title: "+the_title);
  }

  public void endCCFolder() {
    System.err.println("cc folder ends");
  }

  public void endCCItem() {
    System.err.println("cc item ends");
  }

  public void startCCFolder(String the_id) {
    System.err.println("cc folder "+the_id+" begins");
  }

  public void startCCItem(String the_id, String the_title) {
    System.err.println("cc item "+the_id+" begins");
    System.err.println("title: "+the_title);
  }

  public void setCCItemXml(Element the_xml) {
    System.err.println("item xml: "+the_xml);
  }

  public void addAttachment(String attachment_path) {
    System.err.println("adding an attachment: "+attachment_path);
  }

  public void endDiscussion() {
    System.err.println("end discussion");
  }

  public void startManifest() {
    System.err.println("start manifest");
  }

  public void setManifestXml(Element the_xml) {
    System.err.println("manifest xml: "+the_xml);
  }

  public void endManifest() {
    System.err.println("end manifest");
  }

  public void startDiscussion(String topic_name, String text_type, String text, boolean isProtected) {
    System.err.println("start a discussion: "+topic_name);
    System.err.println("text type: "+text_type);
    System.err.println("text: "+text); 
    System.err.println("protected: "+isProtected);
  }

  public void endWebLink() {
    System.err.println("end weblink");
  }

  public void startWebLink(String the_title, String the_url, String the_target, String the_window_features, boolean isProtected) {
    System.err.println("start weblink: "+the_title);
    System.err.println("link to: "+the_url);
    System.err.println("target window: "+the_target);
    System.err.println("window features: "+the_window_features);
    System.err.println("protected: "+isProtected);
  }
 
  public void setWebLinkXml(Element the_link) {
    System.err.println("weblink xml: "+the_link);
  }

  public void addFile(String the_file_id) {
    System.err.println("adding file: "+the_file_id);
  }

  public void endWebContent() {
    System.err.println("ending webcontent");
  }

  public void startWebContent(String entry_point, boolean isProtected) {
    System.err.println("start web content");
    System.err.println("protected: "+isProtected);
    if (entry_point!=null) {
     System.err.println("entry point is: "+entry_point);
    }
  }

  public void endLearningApplicationResource() {
    System.err.println("end learning application resource");
  }

  public void startLearningApplicationResource(String entry_point, boolean isProtected) {
    System.err.println("start learning application resource");
    System.err.println("protected: "+isProtected);
    if (entry_point!=null) {
      System.err.println("entry point is: "+entry_point);
    }
  }

  public void endAssessment() {
    System.err.println("end assessment");    
  }

  public void setAssessmentXml(Element xml) {
    System.err.println("assessment xml: "+xml);
  }

  public void startAssessment(String the_file_name, boolean isProtected) {
    System.err.println("start assessment contained in: "+the_file_name);
    System.err.println("protected: "+isProtected);
  }

  public void endQuestionBank() {
    System.err.println("end question bank");
  }

  public void setQuestionBankXml(Element the_xml) {
    System.err.println("question bank xml: "+the_xml);
  }

  public void startQuestionBank(String the_file_name, boolean isProtected) {
    System.err.println("start question bank in: "+the_file_name);
    System.err.println("protected: "+isProtected);
  }

  public void setAuthorizationServiceXml(Element the_node) {
    System.err.println(the_node);
  }

  public void setAuthorizationService(String cartridgeId, String webservice_url) {
    System.err.println("adding auth service for "+cartridgeId+" @ "+webservice_url);
  }

  public void endAuthorization() {
    System.err.println("end of authorizations");
  }

  public void startAuthorization(boolean isCartridgeScope, boolean isResourceScope, boolean isImportScope) {
    System.err.println("start of authorizations");
    System.err.println("protect all: "+isCartridgeScope);
    System.err.println("protect resources: "+isResourceScope);
    System.err.println("protect import: "+isImportScope);
  }

  public void endManifestMetadata() {
    System.err.println("end of manifest metadata");
  }

  public void startManifestMetadata(String schema, String schema_version) {
    System.err.println("start manifest metadata");
    System.err.println("schema: "+schema);
    System.err.println("schema_version: "+schema_version);
  }
 
  public void setPresentationXml(Element the_xml) {
    System.err.println("QTI presentation xml: "+the_xml);
  }

  public void setQTICommentXml(Element the_xml) {
    System.err.println("QTI comment xml: "+the_xml);
  }

  public void setSection(String ident, String title) {
    System.err.println("set section ident: "+ident);
    System.err.println("set section title: "+title);
  }

  public void setSectionXml(Element the_xml) {
    System.err.println("set Section Xml: "+the_xml);
  }

  public void endQTIMetadata() {
    System.err.println("end of QTI metadata");
  }

  public void setManifestMetadataXml(Element the_md) {
    System.err.println("manifest md xml: "+the_md);    
  }

  public void setResourceMetadataXml(Element the_md) {
    System.err.println("resource md xml: "+the_md); 
  }

  public void addQTIMetadataField(String label, String entry) {
    System.err.println("QTI md label: "+label);
    System.err.println("QTI md entry: "+entry);
}

  public void setQTIComment(String the_comment) {
    System.err.println("QTI comment: "+the_comment);
  }

  public void endDependency() {
    System.err.println("end dependency");
  }

  public void startDependency(String source, String target) {
    System.err.println("start dependency- resource : "+source+" is dependent upon: "+target);
  }

  public void startResource(String id, boolean isProtected) {
    System.err.println("start resource: "+id+ " protected: "+isProtected);
  }

  public void setResourceXml(Element the_xml) {
    System.err.println("resource xml: "+the_xml);
  }

  public void endResource() {
    System.err.println("end resource"); 
  }

  public void addAssessmentItem(QTIItem the_item) {
    System.err.println("add QTI assessment item: "+the_item.toString());
    
  }

  public void addQTIMetadataXml(Element the_md) {
    System.err.println("add QTI metadata xml: "+the_md);
    
  }

  public void startQTIMetadata() {
    System.err.println("start QTI metadata");
  }

  public void setDiscussionXml(Element the_element) {
    System.err.println("set discussion xml: "+the_element); 
  }

  public void addQuestionBankItem(QTIItem the_item) {
    System.err.println("add QTI QB item: "+the_item.toString()); 
  }

  public void setQuestionBankDetails(String the_ident) {
    System.err.println("set qti qb details: "+the_ident);  
  }
}

