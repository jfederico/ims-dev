package test.org.imsglobal.cc.CCParser;

/**********************************************************************************
 * $URL: http://ims-dev.googlecode.com/svn/trunk/cc/IMS_CCParser_v1p0/src/test/java/test/org/imsglobal/cc/CCParser/TestParser.java $
 * $Id: TestParser.java 227 2011-01-08 18:26:55Z drchuck $
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

public class TestParser extends TestCase {

  private static String CC_1="/ccvtd0001v1p16.zip";
  private static String CC_2="/ccvtd0002v1p12.zip";
  private static String CC_3="/ccvtd0003v1p13.zip";
  private static String CC_4="/ccvtd0004v1p09.zip";
  private static String CC_5="/ccvtd0005v1p07.zip";
  private static String CC_6="/ccvtd0006v1p14.zip";
  private static String CC_7="/ccvtd0007v1p14.zip";
  private static String CC_8="/ccvtd0008v1p15.zip";
  private static String CC_9="/ccvtd0009v1p08.zip";
  private static String CC_10="/ccvtd0010v1p12.zip";
  private static String CC_11="/ccvtd0011v1p07.zip";
  private static String CC_12="/ccvtd0012v1p08.zip";
  private static String CC_13="/ccvtd0013v1p08.zip";
  private static String CC_14="/ccvtd0014v1p08.zip";
  private static String CC_15="/ccvtd0015v1p07.zip";
  private static String CC_16="/ccvtd0016v1p12.zip";
  private static String CC_17="/ccvtd0017v1p06.zip";
  
  private static String[] CARTS={CC_1,CC_2, CC_3, CC_4, CC_5, CC_6, CC_7, CC_8, CC_9, CC_10,
                                 CC_11, CC_12, CC_13, CC_14, CC_15, CC_16,CC_17 };
  
  private static final String UNZIP=System.getProperty("java.io.tmpdir");
  
  public void
  setUp() {
    
  }
  
  public void
  testLoading() {
    for (int x=0; x< CARTS.length; x++) {
      URL temp=this.getClass().getResource(CARTS[x]);
      try {
        System.err.println("investigating: "+CARTS[x]);
        File cc = new File(temp.toURI());
        assertNotNull(temp);
        assertNotNull(cc);
        CartridgeLoader cl=ZipLoader.getUtilities(cc, UNZIP);
        Parser parser=Parser.createCartridgeParser(cl);
        DefaultHandler dh = new DefaultHandler();
        parser.parse(dh);
      }
      catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
        fail();
      }
    }
  }
  
  public void 
  cleanup() {
    for (String target: CARTS) {
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
