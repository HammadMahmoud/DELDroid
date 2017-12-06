/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import static edu.uci.seal.deldroid.lp.LPDetermination.PrmResourceMap;
import static edu.uci.seal.deldroid.lp.LPDetermination.apps;
import static edu.uci.seal.deldroid.lp.LPDetermination.componentsMap;
import static edu.uci.seal.deldroid.lp.LPDetermination.allUsedPermissions;
import static edu.uci.seal.deldroid.lp.LPDetermination.intents;
import static edu.uci.seal.deldroid.lp.LPDetermination.permissions;
import static edu.uci.seal.deldroid.lp.LPDetermination.permissionsProtectionLevel;
import static edu.uci.seal.deldroid.lp.LPDetermination.sysPackageName;
import static edu.uci.seal.deldroid.lp.XmlParserUsingSAX.appId;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import edu.uci.seal.deldroid.model.Intent;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import edu.uci.seal.deldroid.model.Application;
import edu.uci.seal.deldroid.model.Component;
import edu.uci.seal.deldroid.model.Permission;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.ANDROID_FRAMEWORK_MANIFEST_PATH;
import java.util.Collection;
import java.util.Set;

/**
 *
 * @author Mahmoud
 */
class SAXHandlerAndroidManifest extends SimpleSAXHandler {
//    private static int permissionCnt=0;

    public static void main(String[] args) {
//        System.out.println("SAXHandlerAndroidManifest ...");
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            File xmlFile = new File(ANDROID_FRAMEWORK_MANIFEST_PATH);
            SimpleSAXHandler handler = new SAXHandlerAndroidManifest();
            InputStream is = new FileInputStream(xmlFile);
            saxParser.parse(is, handler);
            System.out.println(componentsMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //********************* Handle the xml file
    private static String systemServiceComp = "SystemService";
    private String currQName;
    private String groupName;
    private Component comp;
    private Permission currPermission;
    private Application sysApp;
//    private Map<String, List<String>> groupPrmMap;

    public SAXHandlerAndroidManifest() {
//            init();
//            groupPrmMap = new HashMap<>();
        sysApp = new Application(appId++);
        sysApp.setName(sysPackageName);
        sysApp.setPackageName(sysPackageName);
        apps.put(sysPackageName, sysApp);

        //add a SystemService as a service component to the System app
        Component sysServiceComp = new Component(sysApp.getPackageName());
        sysServiceComp.setFullName(systemServiceComp);
        sysServiceComp.setExported("true");
        sysServiceComp.setType("service");
        sysApp.getComponents().add(sysServiceComp);
        componentsMap.put(sysServiceComp.getComponentId(), sysServiceComp);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currQName = qName;
        switch (currQName) {
            case "protected-broadcast"://add it as an implicit intent from the system
                Intent i = new Intent();
                i.setAction(attributes.getValue("android:name"));
                i.setSender(sysPackageName + "." + systemServiceComp);
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
                String discoveredIn = "P";
                groupName = attributes.getValue("android:permissionGroup");
                String permission = attributes.getValue("android:name").replace("android.permission.", "");
                String protectionLevelStr = attributes.getValue("android:protectionLevel");
                if (protectionLevelStr == null || protectionLevelStr.isEmpty()) {
                    protectionLevelStr = "normal";
                }
                char protectionLevel = protectionLevelStr.trim().charAt(0);
                //add only the permissions that are needed for the apps in this system
                permissionsProtectionLevel.put(permission, protectionLevelStr);
                if (allUsedPermissions.contains(permission + "/n")) {
//                    System.out.println("USED: " + permission + "/" + protectionLevelStr);
                    if (groupName == null) { //new group
                        groupName = permission;
                    } else {
                        groupName = groupName.replace("android.permission-group.", "");
                        discoveredIn = "G";                        
                    }
                    PrmResourceMap.put(permission, groupName);

//                    Integer key = LPDetermination.getComponentKeybyName(sysApp.getPackageName() + "." + groupName);
                    Integer key = LPDetermination.getComponentKeybyName(sysApp.getPackageName() + "." + permission);                    
                    if (key == null) { //new permission group
                        //l2 = new ArrayList<>();
                        comp = new Component(sysApp.getPackageName());
                        comp.setFullName(groupName);
                        comp.setType("resource");
                        comp.setExported("true");
                        comp.setDiscoveredIn(discoveredIn);
                        componentsMap.put(comp.getComponentId(), comp);
                        sysApp.getComponents().add(comp);
                        //Mahmoud: create new permission
                        Permission prm = new Permission(null, sysApp.getPackageName(), permission, protectionLevel, true, groupName, groupName);
                        int prmIdx = permissions.indexOf(prm);
//                        System.out.println(prmIdx + " " + permission + "/" + protectionLevelStr + " " + protectionLevel);
                        if (prmIdx < 0) {
//                            System.out.println("permission NOT found "+prm+" protectionLevel:"+protectionLevel);
                            permissions.add(prm);
                        } else {
                            Permission p = permissions.get(prmIdx);
                            p.setProtectionLevel(protectionLevel);
                        }
                    }/* else {
                        System.out.print("system component already exists: " + groupName + " " + permission + " " + key);
                    }*/
//                l2.add(permission);
                    comp.getRequiredPermissions().add(permission);
                    //groupPrmMap.get(groupName).add(permission);                    

                }
//                else {
//                    System.out.println("NOT USED: " + permission + "/" + protectionLevelStr);
//                }

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
