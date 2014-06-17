package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestPack2.java $
 * $Id: TestPack2.java 227 2011-01-08 18:26:55Z drchuck $
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
import java.net.URL;

import org.imsglobal.cc.CartridgeLoader;
import org.imsglobal.cc.DefaultHandler;
import org.imsglobal.cc.Parser;
import org.imsglobal.cc.ZipLoader;


import junit.framework.TestCase;

public class TestPack2 extends TestCase {

  /*
   * PJN NOTE:
   * Due to bugs in the IMS test packages, whilst all of these packages fail (as they should) they are, in many cases, failing
   * for the wrong reasons. Note that this parser is NON VALIDATING.
   */
  
  private static final String CC_1="/ccetd0001v1p04.zip";
  private static final String CC_2="/ccetd0002v1p04.zip";
  private static final String CC_3="/ccetd0003v1p04.zip";
  private static final String CC_4="/ccetd0004v1p04.zip";
  private static final String CC_5="/ccetd0005v1p04.zip";
  private static final String CC_6="/ccetd0006v1p04.zip";
  private static final String CC_7="/ccetd0007v1p04.zip";
  private static final String CC_8="/ccetd0008v1p04.zip";
  private static final String CC_9="/ccetd0009v1p04.zip";
  private static final String CC_10="/ccetd0010v1p04.zip";
  private static final String CC_11="/ccetd0011v1p04.zip";
  private static final String CC_12="/ccetd0012v1p04.zip";
  private static final String CC_13="/ccetd0013v1p04.zip";
  private static final String CC_14="/ccetd0014v1p04.zip";
  private static final String CC_15="/ccetd0015v1p04.zip";
  private static final String CC_16="/ccetd0016v1p04.zip";
  private static final String CC_17="/ccetd0017v1p04.zip";
  private static final String CC_18="/ccetd0018v1p04.zip";
  private static final String CC_19="/ccetd0019v1p04.zip";
  private static final String CC_20="/ccetd0020v1p04.zip";
  private static final String CC_21="/ccetd0021v1p04.zip";
  private static final String CC_22="/ccetd0022v1p04.zip";
  private static final String CC_23="/ccetd0023v1p04.zip";
  private static final String CC_24="/ccetd0024v1p04.zip";
  private static final String CC_25="/ccetd0025v1p04.zip";
  private static final String CC_26="/ccetd0026v1p04.zip";
  private static final String CC_27="/ccetd0027v1p04.zip";
  private static final String CC_28="/ccetd0028v1p04.zip";
  private static final String CC_29="/ccetd0029v1p04.zip";  
  private static final String CC_30="/ccetd0030v1p04.zip";
  private static final String CC_31="/ccetd0031v1p04.zip";
  private static final String CC_32="/ccetd0032v1p04.zip";
  private static final String CC_33="/ccetd0033v1p04.zip";
  private static final String CC_34="/ccetd0034v1p04.zip";
  private static final String CC_35="/ccetd0035v1p04.zip";
  
  private static final String[] BROKEN_CARTS={CC_1, CC_2, CC_3, CC_4, CC_5, CC_6, CC_7, CC_8, CC_9, CC_10,
                                              CC_11, CC_12, CC_13, CC_14, CC_15, CC_16, CC_17, CC_18, CC_19, CC_20,
                                              CC_21, CC_22, CC_23, CC_24, CC_25, CC_26, CC_27, CC_28, CC_29, CC_30,
                                              CC_31, CC_32, CC_33, CC_34, CC_35};
  
  private static final String UNZIP= System.getProperty("java.io.tmpdir");
  
  public void
  setUp() {
   
  }
  
  public void
  testLoading() {
    for (int x=0; x< BROKEN_CARTS.length; x++) {
      URL temp=this.getClass().getResource(BROKEN_CARTS[x]);
      try {
        System.err.println("investigating: "+BROKEN_CARTS[x]);
        File cc = new File(temp.toURI());
        assertNotNull(temp);
        assertNotNull(cc);
        CartridgeLoader cl=ZipLoader.getUtilities(cc,UNZIP);
        Parser parser=Parser.createCartridgeParser(cl);
        DefaultHandler dh = new DefaultHandler();
        parser.parse(dh);
        fail();
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
      } 
    }
    cleanup();
  }
  
  public void 
  cleanup() {
    for (String target: BROKEN_CARTS) {
      File dir=new File(UNZIP, target);
      clean(dir);
    }
  }


  public void
  clean(File the_file) {
    if (the_file.exists()) {
      if (the_file.isDirectory()) {
        File[] files=the_file.listFiles();
        for (File temp: files) {
          clean(temp);
        }
      }
      the_file.delete();
    }
  }
  
}
