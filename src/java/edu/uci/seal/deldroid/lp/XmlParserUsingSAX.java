/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import static edu.uci.seal.deldroid.lp.LPDetermination.componentsMap;
import edu.uci.seal.deldroid.model.Intent;
import edu.uci.seal.deldroid.model.Application;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import edu.uci.seal.deldroid.model.Component;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.ANDROID_FRAMEWORK_MANIFEST_PATH;

/**
 *
 * @author Mahmoud
 */
class XmlParserUsingSAX implements XmlParser {

    static int appId = 0;
//    boolean foundSystem = false;

    public XmlParserUsingSAX() {

    }

    private File[] putSystemFirst(File[] listOfFiles) {
//        for (int i = 0; i < listOfFiles.length; i++) {
//            if (listOfFiles[i].getName().equals("system.xml")) {
//                foundSystem = true;
//                if (i == 0) {
//                    return listOfFiles;
//                }
//
//                //swap elements
//                File temp = listOfFiles[0];
//                listOfFiles[0] = listOfFiles[i];
//                listOfFiles[i] = temp;
//                return listOfFiles;
//            }
//        }
        return listOfFiles;
    }

    @Override
    public void parse(String xmlFilesPath) {
        //parse xml file using SAX parser
        //adds a system as app with id 0
//        Application sys = systemAsApp();
//        apps.put(sys.getPackageName(), sys);
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            SimpleSAXHandler handler = null;
            InputStream is=null;
            
//            //add the system components 
//            File sysXml = new File(ANDROID_FRAMEWORK_MANIFEST_PATH);            
//            handler = new SAXHandlerAndroidManifest();
//            is = new FileInputStream(sysXml);
//            saxParser.parse(is, handler);        
            
            //Add all apps in the DSM folder
            File DSMfolder = new File(xmlFilesPath);
            File[] listOfFiles = putSystemFirst(DSMfolder.listFiles());
            File xmlFile;
            for (int i = 0; i < listOfFiles.length; i++) {                        
                xmlFile = listOfFiles[i];
                
                if (xmlFile.getAbsolutePath().endsWith(".xml")) {
//                    if (!xmlFile.getName().equals("com.appsbar.SushiRecipes103614.xml"))
//                        continue;
                    try {
                        String path = xmlFile.getAbsolutePath();
                        int x = path.lastIndexOf(File.separator);
                        String packageName=path.substring(x+1).replace(".xml", "");                        
                        if (LPDetermination.apps.containsKey(packageName)){
                            System.out.println("Processing file: " + xmlFile.getAbsolutePath()+" packageName:"+packageName);
                            handler = new SimpleSAXHandler();
                            is = new FileInputStream(xmlFile);
                            saxParser.parse(is, handler);
    //                        if (i == 0 && foundSystem) {
    //                            addSysPrmsResources();
    //                        }
                        }
                    } catch (Exception e) {
                        System.out.println("============ ERROR =================");
                        System.out.println("===== " + xmlFile.getAbsolutePath() + " ==========");
                        e.printStackTrace();
                        System.out.println("====================================");
                        System.exit(0);
                    }
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//    private void addSysPrmsResources() {
//        for (Component c : componentsMap.values()) {
//            for (String p : c.getRequiredPermissions()) { //required permissions to access this resource
//                PrmResourceMap.put(p, c.getName());
//            }
//        }
//    }
}
