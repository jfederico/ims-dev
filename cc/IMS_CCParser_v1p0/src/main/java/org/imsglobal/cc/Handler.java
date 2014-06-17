package org.imsglobal.cc;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/main/java/org/imsglobal/cc/Handler.java $
 * $Id: Handler.java 227 2011-01-08 18:26:55Z drchuck $
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

public interface Handler {
  
  public void startManifest();
  
  public void setManifestXml(Element the_xml);

  public void startCCFolder(String the_id);
  
  public void endCCFolder();
  
  public void startCCItem(String the_id, String the_title);
  
  public void setCCItemXml(Element the_xml);
  
  public void endCCItem();
  
  public void startDependency(String source, String target);
  
  public void endDependency();
  
  public void startResource(String id, boolean isProtected);
  
  public void setResourceXml(Element the_xml);
  
  public void addFile(String file_id);
  
  public void endResource();
  
  public void endManifest();
  
}
