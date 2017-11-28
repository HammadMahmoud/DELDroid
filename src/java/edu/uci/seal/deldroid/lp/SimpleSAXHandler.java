/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import static edu.uci.seal.deldroid.lp.LPDetermination.apps;
import static edu.uci.seal.deldroid.lp.LPDetermination.componentsMap;
import static edu.uci.seal.deldroid.lp.LPDetermination.intents;
import static edu.uci.seal.deldroid.lp.LPDetermination.missingComps;
import static edu.uci.seal.deldroid.lp.XmlParserUsingSAX.appId;
import edu.uci.seal.deldroid.model.Intent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import edu.uci.seal.deldroid.model.Application;
import edu.uci.seal.deldroid.model.Component;
import edu.uci.seal.deldroid.model.Data;
import edu.uci.seal.deldroid.model.IntentFilter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.UNKNOW_PACKAGE;

/**
 *
 * @author Mahmoud
 *
 * This class parses the XML file and store the data in a data structure (apps,
 * components, actionComponentMap, intents)
 */
class SimpleSAXHandler extends DefaultHandler {

//    private Map<String, Component> componentsMap;
//    private Map<String, List<String>> actionComponentMap;
//    private Map<String, Application> apps;
//    private List<Intent> intents;
//    private Set<Component> missingComps;
    
    static boolean ADD_INTENT=false;  //Add ArchExtractor intents
    static boolean ADD_COMPONENTS=false; //Add ArchExtractor components
    private String currQName;
    private Application currApp;
    private Component currComp;
    private IntentFilter currIfilter;
    private Data currData;
    private Intent currIntent;
    private Component currMissingReceiverComp;

    private boolean moreAppAttributes = true;
    private boolean inComponents = false;
    private boolean inFilter = false;
    private boolean inData = false;
    private boolean inIntents = false;        

//    private Set<String> handledActions; //actions that are sent from this app

    public SimpleSAXHandler() {
//        componentsMap = new HashMap<>();
//        intents = new ArrayList<>();
//        actionComponentMap = new HashMap<>();
////        handledActions = new HashSet<>();
        currApp = new Application(appId++);
    }

//    SimpleSAXHandler(Map<String, Application> apps, Map<String, Component> componentsMap, Map<String, String> actionComponentMap, 
//            List<Intent> intents, Set<Component> missingComps) {
//        this.apps = apps;
//        this.componentsMap = componentsMap;
//        this.actionComponentMap = new HashMap<>();
//        this.intents = intents;
//        this.missingComps = missingComps;
//        
////        handledActions = new HashSet<>();
//        currApp = new Application(appId++);
//    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currQName = qName;
        switch (qName) {

            case "components":
                moreAppAttributes = false;
                inComponents = true;
                break;
            case "Component":                
                currComp = new Component(currApp.getPackageName());
                break;
            case "IntentFilters":
                inFilter = true;
                break;
            case "filter":
                currIfilter = new IntentFilter();
                break;
            case "data":
                inData = true;
                break;
            case "dataElement":
                currData = new Data();
                break;
            case "intents":
                inIntents = true;
                break;
            case "Intent":
                currIntent = new Intent();
                currIntent.setPackageName(currApp.getPackageName());
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        Integer key=null;
        switch (qName) {
            case "components":
                inComponents = false;
                break;
            case "Component":
//                if (componentsMap.containsKey(currComp.getName())){
                key=LPDetermination.getComponentKeybyName(currComp);
                if(key!=null){
                    currComp.setComponentId(Integer.valueOf(key));
//                    System.out.println("currComp "+currComp);
                  componentsMap.get(key).setActuallyUsedPermissions(currComp.getActuallyUsedPermissions());
                  componentsMap.get(key).setRequiredPermissions(currComp.getRequiredPermissions());                  
                }else{
                    currComp.setDiscoveredIn("A"); //ArchExtractor
//                    componentsMap.put(currComp.getName(), currComp);
                    if(ADD_COMPONENTS){
                        componentsMap.put(currComp.getComponentId(), currComp);
                    }
                }                
                currComp = null;
                break;
            case "IntentFilters":
                inFilter = false;
                break;
            case "filter":
                currComp.getIntentFilters().add(currIfilter);
                break;
            case "data":
                inData = false;
                break;
            case "dataElement":
                currIfilter.getData().add(currData);
                break;
            case "intents":
                inIntents = false;
                break;
            case "Intent":
                if(ic3NoIntentForApp(currApp)){
                    System.out.println(currIntent);
                    intents.add(currIntent);
                }
                else if (( currIntent.getReceiver()!=null && !currIntent.getReceiver().isEmpty())){//take the explicit Intents only from archExtractor
                    if (ADD_INTENT){
                        intents.add(currIntent);
                    }
//                System.out.println(currIntent.getSender()+" "+currIntent.getSenderComponentId()+" "+currIntent.getReceiver());
                }
                break;
            case "appRequiredPermissions":
                        if (apps.containsKey(currApp.getPackageName())){
                            Application a = apps.get(currApp.getPackageName());
                            a.setAppActuallyUsesPermissions(currApp.getAppActuallyUsesPermissions());
                            a.setAppDefinedPermissions(currApp.getAppDefinedPermissions());
                            a.setAppRequiredPermissions(currApp.getAppRequiredPermissions());
                            if (a.getName() == null || a.getName().isEmpty()){
                                a.setName(currApp.getName());
                            }
                            
                        }else{
                            apps.put(currApp.getPackageName(), currApp);
                        }
                        break;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        String value = (new String(ch, start, length)).trim();
        if (value != null && value.length() > 0) {
            //------------------- App Attributes --------------------
            if (moreAppAttributes) {
                switch (currQName) {
                    case "packageName":
                        currApp.setPackageName(value);                                                
                        break;
                    case "name":
                        currApp.setName(value);
                        break;
                    case "versionCode":
                        currApp.setVersionCode(value);
                        break;
                    case "versionName":
                        currApp.setVersionName(value);
                        break;
                    case "appLabel":
                        currApp.setName(value);
                        break;
                    case "appUsesPermission":
                        currApp.getAppUsesPermissions().add(value);
                        break;
                    case "appActuallyUsesPermission":
                        currApp.getAppActuallyUsesPermissions().add(value);
//                        System.out.println(currApp.getPackageName() +" "+value);
                        break;
                    case "appDefnedPermission":
                        currApp.getAppDefinedPermissions().add(value);
                        break;
                    case "appRequiredPermission":
                        currApp.getAppRequiredPermissions().add(value);
                        break;
//                    case "appRequiredPermissions":
//                        if (apps.containsKey(currApp.getPackageName())){
//                            System.out.println("App exists .......................");
//                            Application a = apps.get(currApp.getPackageName());
//                            a.setAppActuallyUsesPermissions(currApp.getAppActuallyUsesPermissions());
//                            a.setAppDefinedPermissions(currApp.getAppDefinedPermissions());
//                            a.setAppRequiredPermissions(currApp.getAppRequiredPermissions());
//                        }else{
//                            System.out.println("App not exists .......................");
//                            apps.put(currApp.getPackageName(), currApp);
//                        }
//                        break;
                }
            } //------------------- COMPONENT --------------------
            else if (inComponents) {
                switch (currQName) {
                    case "type":
                        currComp.setType(value);
                        break;
                    case "name":
                          currComp.setFullName(componentFullName(currApp.getPackageName(), value));
                        break;
                    case "exported":
                        currComp.setExported(value);
                        break;
                    case "requiredPermission":
                        currComp.getRequiredPermissions().add(value);                        
                        if (currComp.getRequiredPrmToAccess()!=null){
                            currComp.setRequiredPrmToAccess(value);
                        }
                        break;
                    case "actuallyUsedPermission":
                        currComp.getActuallyUsedPermissions().add(value);
                        break;
                }                
                //------------------- INTENT FILTER --------------------
                if (inFilter) {
                    switch (currQName) {
                        case "action":
                            currIfilter.getActions().add(value);
                            break;
                        case "category":
                            currIfilter.getCategories().add(value);
                            break;
                        case "pathData":
                            currIfilter.setPathData(value);
                            break;

                    }
                    //------------------- DATA  --------------------
                    if (inData) {
                        switch (currQName) {
                            case "scheme":
                                currData.setScheme(value);
                                break;
                            case "mimeType":
                                currData.setMimeType(value);
                                break;
                            case "host":
                                currData.setHost(value);
                                break;
                            case "pathPattern":
                                currData.setPath(value);
                                break;
                        }
                    }
                }
            }
            if (inIntents){
                switch (currQName) {
                    case "sender":
                        currIntent.setSender(value);
                        break;
                    case "component":                        
                        currIntent.setReceiver(value);                        
                        break;
                    case "action":
                        currIntent.setAction(value.replace("\"",""));
                        break;
                }
                
            }
            /*if (inIntents) {
                //------------------- INTENT --------------------    
                switch (currQName) {
                    case "sender":
                        value = componentFullName(currApp.getPackageName(), value);
//                        
//                         The sender component is not exists, either dynamically loaded or dynamically registered
//                                                
//                            String shortNameS = currApp.getPackageName()+pkgCompSep+value.replace(currApp.getPackageName()+".", "");
//                            String compNameS=currApp.getPackageName()+pkgCompSep+value;
//                            if(componentsMap.containsKey(compNameS)){
//                                compNameS=compNameS;
//                            }
//                            else if(componentsMap.containsKey(shortNameS)){
//                                compNameS=shortNameS;
//                            }
                        
//                            else { //component not found
                        if (!componentsMap.containsKey(value)){
                                Component c = new Component(currApp.getPackageName());                            
                                c.setName(value);
                                c.setExported("true");
                                c.setDiscoveredIn("IS");
                                componentsMap.put(c.getName(), c);
                                c=null;
                            }
                        currIntent.setSender(value);
                        break;
                    case "component":
                        boolean localComp=true;                        
                        String pkgName = currApp.getPackageName();
                        String compNameR = componentFullName(pkgName, value);
                        
                        
                        if (componentsMap.containsKey(compNameR)){
                            System.out.println(compNameR+" ****************");
                        }else{
                        if(!value.startsWith(pkgName)){
                            pkgName=UNKNOW_PACKAGE;
                            compNameR = value;
                            localComp=false;
                        }
                                }
                        
                        if(!componentsMap.containsKey(compNameR)){
                        
                            currMissingReceiverComp = new Component(pkgName);
                            currMissingReceiverComp.setName(compNameR);
                            currMissingReceiverComp.setDiscoveredIn("IR");
                            currMissingReceiverComp.setExported("true");
                            if(localComp){
                                componentsMap.put(currMissingReceiverComp.getName(), currMissingReceiverComp);
                            }else{
                                missingComps.add(currMissingReceiverComp);
                            }
                        }
                        currIntent.setReceiver(compNameR);
                            
                        
                        
//                        value = sanitizeComponentName(value);
//                            String pkgName = currApp.getPackageName();
//                            if (!value.startsWith(currApp.getPackageName())){
//                                pkgName=UNKNOW_PACKAGE;
//                            }                            
//
//                            String shortNameR = pkgName+pkgCompSep+value.replace(currApp.getPackageName()+".", "");
//                            String compNameR=pkgName+pkgCompSep+value;
//                            if(componentsMap.containsKey(compNameR)){
//                                compNameR=compNameR;
//                            }
//                            else if(componentsMap.containsKey(shortNameR)){
//                                compNameR=shortNameR;
//                            }
//                            else { //component not found
//                        if(!componentsMap.containsKey(compNameR)){
//                            
//                            compNameR=componentFullName(pkgName, value);
//                            currMissingReceiverComp = new Component(pkgName);
//                            currMissingReceiverComp.setName(compNameR);
//                            currMissingReceiverComp.setDiscoveredIn("IR");
//                            currMissingReceiverComp.setExported("true");
//                            if(currMissingReceiverComp.getId()!=-1){
//                                componentsMap.put(currMissingReceiverComp.getName(), currMissingReceiverComp);
//                            }else{//add it to the missingComps set hopefully we can find this component on another app
//                                missingComps.add(currMissingReceiverComp);
//                            }
////                            currMissingReceiverComp=null;
//                        }
//                        currIntent.setReceiver(compNameR);
                        break;
                    case "action":
                        currIntent.setAction(value.replace("\"", ""));
                        break;
                    case "dataType":
                        currIntent.setDataType(value);
                        break;
                    case "scheme":
                        currIntent.setScheme(value);
                        break;
                    case "extra":
                        currIntent.setExtra(value);
                        break;
                    case "sensitiveData":
                        currIntent.setSensitiveData(value);
                        break;
                    case "category":
                        currIntent.addCategory(value);
                        break;
                    case "consumerMethod":
                        if (currMissingReceiverComp!=null){                            
                            if(value.contains("Activity"))
                                currMissingReceiverComp.setType("activity");
                            else if(value.contains("Service"))
                                currMissingReceiverComp.setType("service");
                            else if(value.contains("Broadcast"))
                                currMissingReceiverComp.setType("receiver");
                            currMissingReceiverComp=null;
                        }
                    break;
                        
                }
            }*/
        }
    }

    public void endDocument() throws SAXException {
        /*
         Component sys = getSysComp();
         componentsMap.put("System", sys);
         int n = componentsMap.size();
         //        orderedComps = new Component[n];
        
         System.out.println("End of the document, there are: " + componentsMap.size() + " components");
         System.out.println("\n-----------------------------");        
         componentsMap.entrySet().stream().forEach((entry) -> {
         Component c = entry.getValue();
         //            orderedComps[c.getId()] =  c;
         System.out.println(c);            
         });
         System.out.println("there are: " + intents.size() + " intents");
         System.out.println("\n-----------------------------");
        
        
         int [][] dsm = new int[n][n];        
        
         intents.stream().forEach((i) -> {
         System.out.println(i);
         int senderIdx = componentsMap.get(i.getSender()).getId();
         if (i.getReceiver()!=null){ //explicit intent
         int receiverIdx = componentsMap.get(i.getReceiver()).getId();
         dsm[senderIdx][receiverIdx] = 1;
         }else{ //implicit intent
         String action = i.getAction();
         List candidateReceivers = actionComponentMap.get(action);
         if (candidateReceivers != null){
         handledActions.add(action);
         candidateReceivers.stream().forEach((r) -> {
         if (r != null){
         int rIdx = componentsMap.get(r).getId();
         dsm[senderIdx][rIdx] += 2;
         }
         });
         }                
         }
         });
         // add the system actions
         actionComponentMap.entrySet().stream().forEach((entry) -> {
         String action = entry.getKey();
         if (!handledActions.contains(action)){
         List candidateReceivers = actionComponentMap.get(action);
         if (candidateReceivers != null){
         candidateReceivers.stream().forEach((r) -> {
         if (r != null){
         int rIdx = componentsMap.get(r).getId();
         dsm[sys.getId()][rIdx] = 2;
         }
         });
         }                
         }
         });
        
         System.out.println("\n-----------------------------DSM starts");
         System.out.println("\n0: No communication, 1: Explicit, 2: Implicit");
         //        printDSM(dsm, n);
         System.out.println("\n-----------------------------DSM ends");
         System.out.println(actionComponentMap);
        
        
         */

    }

    private String componentFullName(String pkgName, String compName) {
        String result = compName.replace("\"", "");
        if (compName.contains("\"")) {
            int n = compName.indexOf("\"");
            result= compName.substring(n + 1).replace("\"", "");
        }
        if(result.startsWith(pkgName))
            return result;
        if(!result.startsWith("."))
            result="."+result;
        
        return pkgName+result;
    }
//   private void printDSM(int[][] dsm, int n)
//   {
//       System.out.print(" ,");
//       for(int h = 0; h < n; h++){ //print the header
//           System.out.print(orderedComps[h].getName()+",");
//       }
//       System.out.println();
//   for(int i = 0; i < n; i++)
//   {
//      for(int j = 0; j < n; j++)
//      {
//         if (j==0){
//             System.out.print(orderedComps[i].getName()+",");
//         }
////         if (i==j){
////            System.out.print('x'+",");
////         } else{
//            System.out.print(dsm[i][j]+",");
////         }
//      }
//      System.out.println();
//      
//   }
//    }

//    private void addActionCompMapEntry(String action, String compName) {
//        if (actionComponentMap.containsKey(action)) {
//            actionComponentMap.get(action).add(compName);
//        } else {
//            List l = new ArrayList<String>();
//            l.add(compName);
//            actionComponentMap.put(action, l);
//        }
//    }

    private boolean ic3NoIntentForApp(Application currApp) {
        //tofo: this method should read from IC3 database
        /*Query
        select a.id,a.name from exitpoints x, classes c, applications a where x.class_id=c.id and c.app_id=a.id and a.id=1450;
        */
        if ("s73.com.moez.QKSMS".equalsIgnoreCase(currApp.getPackageName())){
//            System.out.println("IC3 couldn't extract the Intents for "+currApp.getName()+" so DELDroid will rely on the extracted INtents from ArchExtractor.");
            return true;
        }
        return false;
    }

}
