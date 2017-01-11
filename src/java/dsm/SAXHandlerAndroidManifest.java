/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import static dsm.LPDetermination.PrmResourceMap;
import static dsm.LPDetermination.apps;
import static dsm.LPDetermination.componentsMap;
import static dsm.LPDetermination.init;
import static dsm.LPDetermination.intents;
import static dsm.LPDetermination.sysPackageName;
import static dsm.XmlParserUsingSAX.appId;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import model.Intent;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import model.Application;
import model.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import static utils.WebServicesUtils.ANDROID_FRAMEWORK_MANIFEST_PATH;

/**
 *
 * @author Mahmoud
 */
class SAXHandlerAndroidManifest extends SimpleSAXHandler {
//    private static int permissionCnt=0;
    public static void main(String[] args){
//        System.out.println("SAXHandlerAndroidManifest ...");
        try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            File xmlFile = new File(ANDROID_FRAMEWORK_MANIFEST_PATH);            
            SimpleSAXHandler handler = new SAXHandlerAndroidManifest();
            InputStream is = new FileInputStream(xmlFile);
            saxParser.parse(is, handler);        
            System.out.println(componentsMap.size());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //********************* Handle the xml file
    
    private static String systemServiceComp = "SystemService";
    private String currQName ;
    private String groupName;
    private Component comp;
    private Application sysApp;
//    private Map<String, List<String>> groupPrmMap;

        public SAXHandlerAndroidManifest() {
//            init();
//            groupPrmMap = new HashMap<>();
            sysApp = new Application(appId++);
            sysApp.setName(sysPackageName);
            sysApp.setPackageName(sysPackageName);
            apps.put(sysApp.getName(), sysApp);
            
            //add a SystemService as a service component to the System app
            Component sysServiceComp = new Component(sysApp.getPackageName());
            sysServiceComp.setName(systemServiceComp);
            sysServiceComp.setExported("true");
            sysServiceComp.setType("service");
            componentsMap.put(sysServiceComp.getComponentId(), sysServiceComp);
            
    }
    

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currQName = qName;
        switch (currQName){
            case "protected-broadcast"://add it as an implicit intent from the system
                Intent i = new Intent();
                i.setAction(attributes.getValue("android:name"));
                i.setSender(sysPackageName+"."+systemServiceComp);                                
                i.setSysIntent(true);
                intents.add(i);
                break;
//            case "permission-group": //add it as a component name
//                groupName = attributes.getValue("android:name").replace("android.permission-group.", "");                
//                comp = new Component(sysApp.getPackageName());
//                comp.setName(groupName);
//                comp.setType("resource");
//                comp.setExported("true");
//                List<String> l = new ArrayList<>();
//                componentsMap.put(groupName, comp);
//                //groupPrmMap.put(groupName, l);
//                break;
            case "permission"://add it to the required permission in the current component
                //get the permission group
                String discoveredIn="P";
                groupName = attributes.getValue("android:permissionGroup");
                String permission = attributes.getValue("android:name").replace("android.permission.", "");
                if(groupName ==null){ //new group
                    groupName=permission;
                }else{
                    groupName=groupName.replace("android.permission-group.", "");
                    discoveredIn="G";
                }
                 Integer key = LPDetermination.getComponentKeybyName(sysApp.getPackageName()+"."+groupName);
                if(key==null){ //new permissio group
                    //l2 = new ArrayList<>();
                    comp = new Component(sysApp.getPackageName());
                    comp.setName(groupName);
                    comp.setType("resource");
                    comp.setExported("true");
                    comp.setDiscoveredIn(discoveredIn);
                    componentsMap.put(comp.getComponentId(), comp);
                }
//                else{
//                    System.out.print("system component already exists: "+groupName+" "+key);
//                }
//                l2.add(permission);
                comp.getRequiredPermissions().add(permission);
                //groupPrmMap.get(groupName).add(permission);
                PrmResourceMap.put(permission, comp.getName());
                break;
        }
    }
        @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    }
    
    public void endDocument() throws SAXException {
//        System.out.println(apps);
//        System.out.println(componentsMap);
//        System.out.println(intents);
    }
}
