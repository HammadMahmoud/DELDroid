/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.db;

import edu.uci.seal.deldroid.lp.LPDetermination;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import edu.uci.seal.deldroid.model.Application;
import edu.uci.seal.deldroid.model.Component;
import com.mysql.jdbc.Driver;
import static edu.uci.seal.deldroid.lp.LPDetermination.componentsMap;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.uci.seal.deldroid.model.Data;
import edu.uci.seal.deldroid.model.Intent;
import edu.uci.seal.deldroid.model.IntentFilter;
import android.content.UriMatcher;
import static edu.uci.seal.deldroid.lp.LPDetermination.apps;
import static edu.uci.seal.deldroid.lp.LPDetermination.authorityPermissionsMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.cpAuthorityPermissionsFile;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.resourceSysServiceFile;

/**
 *
 * @author Mahmoud
 */
public class DataManager {
    private static MyDBConnection mdbc;
    private static java.sql.Statement stmt;
    private static Connection conn;
    public static int MIN_COMP_ID=0;
    public static int explicitIntentsCnt;
    public static int implicitIntentsCnt;
        
    
    public static void main(String[] args) {                
        
        deleteBundle(50, false);
        /*
        LPDetermination.init();
        
        prepare();
        //Add IC3 apps
        int bundleNo=1;
        addIC3Applications(LPDetermination.apps, bundleNo);
        addIC3AppUsesPermissions(LPDetermination.apps, bundleNo);
//        System.out.println(LPDetermination.apps);
        
        //Add IC3 components
        addIC3Components(LPDetermination.componentsMap, bundleNo);
        addProvidersPermissions(LPDetermination.componentsMap, bundleNo);
        
        //Add IntentFilters to their components
        addIntentFilters(LPDetermination.componentsMap, LPDetermination.iFiltersMap, bundleNo);
        
//        System.out.println(LPDetermination.componentsMap);
        
        //Add Intents
        addExplicitIntents(LPDetermination.intents, bundleNo);        
//        System.out.println(LPDetermination.intents);
        //Add Intents
        addImplicitIntents(LPDetermination.intents, bundleNo);
//        System.out.println(LPDetermination.intents);
                */
    }
    
    public static void prepare(){
        MIN_COMP_ID=0;
        explicitIntentsCnt=0;
        implicitIntentsCnt=0;
        
        try{
            if (mdbc==null){
                mdbc = new MyDBConnection();
            mdbc.init();
            }
            conn = mdbc.getMyConnection();
            stmt = conn.createStatement();        
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void tearDown(){
        try{
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static ResultSet executeQuery(String query){
        ResultSet result=null;
        try{
        if (mdbc==null){
            mdbc = new MyDBConnection();
            mdbc.init();            
            conn = mdbc.getMyConnection();
            stmt = conn.createStatement();
        }
            result = stmt.executeQuery(query);            
//            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;        
    }

        private static int executeUpdate(String statement){
        int result=0;
        try{
        if (mdbc==null){
            mdbc = new MyDBConnection();
            mdbc.init();            
            conn = mdbc.getMyConnection();
            stmt = conn.createStatement();
        }
            result = stmt.executeUpdate(statement);       
//            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;        
    }

        public static void updateAppsBundle(int bundleNo){
        try{
        int updatedRecords = executeUpdate("update applications set bundle=" +bundleNo+
                                        " where id in (" +
                                        " select id from apps_bundle" +
                                        " where bundle="+bundleNo+");" );
        System.out.println("Bundle "+bundleNo+": "+updatedRecords+" records updated.");
        }catch(Exception e){
            e.printStackTrace();
        }        
    }

       public static void createBundle(int bundleNo){
        try{
        
            int insertApps = executeUpdate("update applications set dataset=\"S"+bundleNo+"\", ds_id="+bundleNo+", bundle="+bundleNo+" where dataset is null;");
            System.out.println("Bundle "+bundleNo+": "+insertApps+" records inserted in the applications table.");
            int insertRecords = executeUpdate("insert into apps_bundle select id,app,version,dataset,ds_id,bundle from applications where bundle="+bundleNo+";" );
            System.out.println("Bundle "+bundleNo+": "+insertRecords+" records inserted in the apps_bundle table.");
        }catch(Exception e){
            e.printStackTrace();
        }        
    }

        public static void updateAppsBundleByName(int bundleNo){
        try{
        int updatedRecords = executeUpdate("update applications set bundle=" +bundleNo+
                                        " where app in (" +
                                        " select app from apps_bundle" +
                                        " where bundle="+bundleNo+");" );
        System.out.println("Bundle "+bundleNo+": "+updatedRecords+" records updated.");
        }catch(Exception e){
            e.printStackTrace();
        }        
    }

        public static void deleteBundle(int bundleNo, boolean completeDelete){
        try{
            System.out.println("Delete all data related to bundle "+bundleNo);
            String[] commands = {
                "delete from IFCategories where filter_id in (select f.id from IntentFilters f,components comp, classes c, apps_bundle a where f.component_id=comp.id and a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from IFActions where filter_id in (select f.id from IntentFilters f,components comp, classes c, apps_bundle a where f.component_id=comp.id and a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from IFData where filter_id in (select f.id from IntentFilters f,components comp, classes c, apps_bundle a where f.component_id=comp.id and a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from IntentFilters where component_id in (select comp.id from components comp, classes c, apps_bundle a where a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from Aliases where component_id in (select comp.id from components comp, classes c, apps_bundle a where a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from ExitPointComponents  where component_id in (select comp.id from components comp, classes c, apps_bundle a where a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from PAuthorities where provider_id in (select p.id from providers p, components comp, classes c, apps_bundle a where p.component_id=comp.id and a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from Providers where component_id in (select comp.id from components comp, classes c, apps_bundle a where a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from ComponentExtras where component_id in (select comp.id from components comp, classes c, apps_bundle a where a.id=c.app_id and comp.class_id=c.id and a.bundle="+bundleNo+");",
                "delete from Components where class_id in (select c.id from classes c, apps_bundle a where a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from ICategories where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IActions where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IMimeTypes where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IExtras where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IPackages where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IClasses where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IData where intent_id in (select i.id from Intents i, ExitPoints x, classes c, apps_bundle a where i.exit_id=x.id and x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from Intents where exit_id in (select x.id from ExitPoints x, classes c, apps_bundle a where x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from IntentPermissions where exit_id in (select x.id from ExitPoints x, classes c, apps_bundle a where x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from Uris where exit_id in (select x.id from ExitPoints x, classes c, apps_bundle a where x.class_id=c.id and a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from ExitPoints where class_id in (select c.id from classes c, apps_bundle a where a.id=c.app_id and a.bundle="+bundleNo+");",
                "delete from Classes where app_id in (select a.id from apps_bundle a where a.bundle="+bundleNo+");",
                "delete from UsesPermissions where app_id in (select a.id from apps_bundle a where a.bundle="+bundleNo+");",
                "delete from Applications where id in (select a.id from apps_bundle a where a.bundle="+bundleNo+");",
                "delete from actionStrings where st like \"s"+bundleNo+"%\";"
                
                };

            for (int i=0; i<commands.length; i++){
                executeUpdate(commands[i]);
            }
            if(completeDelete){
                int n = executeUpdate("delete from apps_bundle where bundle="+bundleNo+";");
                System.out.println(n+ " records deleted");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    public static void addIC3Applications(Map<String, Application> apps, int bundleNo){
        try{
        ResultSet appRecords = executeQuery("select id, app, version, label from applications where bundle="+bundleNo);
        while (appRecords.next()){
            Application app = new Application(appRecords.getInt("id"));
            app.setPackageName(appRecords.getString("app"));
            String appLabel = appRecords.getString("label");            
                    
            if (appLabel == null || appLabel.isEmpty()){
                app.setName(app.getPackageName());
            }else{
                app.setName(appLabel);
            }
            
            app.setVersionCode(appRecords.getString("version"));
            apps.put(app.getPackageName(), app);
        }
        }catch(Exception e){
            e.printStackTrace();
        }        
    }

    public static void addIC3AppUsesPermissions(Map<String, Application> apps, int bundleNo){
        try{
        ResultSet records = executeQuery("select up.uses_permission, a.id app_id, a.app, replace(ps.st,\"android.permission.\",\"\") st from usesPermissions up, permissionStrings ps, applications a where up.uses_permission=ps.id and a.id=up.app_id and a.bundle="+bundleNo);
        while (records.next()){
            apps.get(records.getString("app")).getAppUsesPermissions().add(records.getString("st"));
        }
        }catch(Exception e){
            e.printStackTrace();
        }                
    }
    private static void calcMinCompId(int bundleNo){
//        MIN_COMP_ID=0; 
//        try{
//            ResultSet result = executeQuery("select min(comp.id) minCompId from components comp, classes c, applications a where comp.class_id = c.id and c.app_id=a.id and a.bundle="+bundleNo);
//            result.next();
//            MIN_COMP_ID = result.getInt("minCompId");
//        }catch(Exception e){
//            e.printStackTrace();
//        }        
    }
    public static void addIC3Components(Map<Integer, Component> componentsMap, int bundleNo){
        calcMinCompId(bundleNo);
        try{
        ResultSet records = executeQuery("select comp.id, comp.id comp_id, a.app packageName, comp.kind , c.`class` name, comp.exported, "+
                    "getPermission(comp.`permission`) requiredPrm\n" +
                    "from components comp, classes c, applications a\n" +
                    "where comp.class_id=c.id and c.app_id=a.id " +
                    "and c.class not like 'null_type'" +
                    //"and c.class not like 'com.twitter.sdk.android.%' and c.class not like '%com.digits.sdk.android.%' and c.class not like '%com.squareup.picasso%' " + //for the demo only
                    "and a.bundle="+bundleNo+"  order by a.id, comp.id;");
        while (records.next()){
            String compName = records.getString("name");
            Integer ic3ComptId = records.getInt("comp_id");
            if (componentsMap.containsKey(ic3ComptId)){
                System.out.println("component "+compName+" already exists");
                System.out.println(componentsMap.get(ic3ComptId));
                throw new Exception("duplicate components. Component name ["+compName+"]");
            }
            
            Component comp = new Component(records.getString("packageName"), ic3ComptId);
            comp.setType(getType(records.getString("kind")));
            comp.setFullName(compName);            
            if (records.getString("exported").equals("1"))
                comp.setExported("T");
            else
                comp.setExported("F");
            comp.setRequiredPrmToAccess(records.getString("requiredPrm"));
//            comp.getRequiredPermissions().add(records.getString("requiredPrm"));
//            componentsMap.put(comp.getName(), comp);
            componentsMap.put(ic3ComptId, comp);
        }
        }catch(Exception e){
            e.printStackTrace();
        }               
    }
        public static void addProvidersPermissions(Map<Integer, Component> componentsMap, int bundleNo)  {
        try{
        ResultSet records = executeQuery("select c.`class` comp_name, comp.id comp_id, p.grant_uri_permissions, p.read_permission, p.write_permission, pa.id provider_authority_id, pa.authority\n" +
                                        " from providers p, components comp, classes c, applications a, PAuthorities pa\n" +
                                        " where c.id=comp.class_id and p.component_id=comp.id and c.app_id=a.id and p.id=pa.id and a.bundle="+bundleNo);
        while (records.next()){
//            String compName=records.getString("comp_name");
            Integer ic3CompId=records.getInt("comp_id");
//            Component comp = componentsMap.get(compName);
            Component comp = componentsMap.get(ic3CompId);
            if (comp==null){
                System.out.println("Warning: component ID "+ic3CompId+" is null");
                continue;
            }
            
            comp.setProviderReadPermission(records.getString("read_permission"));
            comp.setProviderWritePermission(records.getString("write_permission"));
            comp.setProviderAuthority(records.getString("authority"));
            
//            List requiredPrms = comp.getRequiredPermissions();
//            if (requiredPrms!=null){
//                if (readPrm!=null){
//                    if (!requiredPrms.contains(readPrm)){
//                        comp.getRequiredPermissions().add(readPrm);
//                    }
//                }
//                String writePrm = records.getString("write_permission");
//                if (writePrm!=null){
//                    if (!requiredPrms.contains(writePrm)){
//                        comp.getRequiredPermissions().add(writePrm);
//                    }
//                }
//            }
         }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addIntentFilters(Map<Integer, Component> componentsMap, Map<Integer, IntentFilter> iFiltersMap, int bundleNo)  {
        Integer ic3CompId=null;
        try{
        ResultSet records = executeQuery("select ifilter.id filter_id, comp.id - "+MIN_COMP_ID+" comp_id, "
                + "comp.class_id, comp.kind, comp.exported, comp.`permission`, c.app_id, c.`class` comp_name\n" +
        "    from IntentFilters ifilter, components comp, classes c, applications a\n" +
        "    where ifilter.component_id=comp.id and comp.class_id=c.id and c.app_id=a.id and a.bundle="+bundleNo );
        
        while (records.next()){
            IntentFilter filter = new IntentFilter();
            filter.setId(records.getInt("filter_id"));
            iFiltersMap.put(filter.getId(), filter);
            String compName=records.getString("comp_name");
            ic3CompId=records.getInt("comp_id");
//            componentsMap.get(compName).getIntentFilters().add(filter);
            if (componentsMap.containsKey(ic3CompId)){
                componentsMap.get(ic3CompId).getIntentFilters().add(filter);
            }else{
//                System.out.println("addIntentFilters: Component ID "+ic3CompId+" is not exists.");
            }
        }
        }
        catch(Exception e){
//            System.out.println("ic3CompId "+ic3CompId+" not exists");
            e.printStackTrace();
        }
        //add actions, categories, data to their intentFilters
        addIntentFilterActions(iFiltersMap);
        addIntentFilterCategories(iFiltersMap);
        addIntentFilterData(iFiltersMap);
    }
    
    public static void addIntentFilterActions(Map<Integer, IntentFilter> iFiltersMap){
        String action;
        Integer cnt=0;
        try{
        ResultSet records = executeQuery("select ifa.filter_id, ifa.`action` action_id, a.st action \n" +
                                        " from IFActions ifa, ActionStrings a\n" +
                                        " where ifa.`action`=a.id;\n" );
        while (records.next()){
            action = records.getString("action");
            Integer id = records.getInt("filter_id");
            if (iFiltersMap.containsKey(id)){
                iFiltersMap.get(id).getActions().add(action);
                if (LPDetermination.actionCompCntMap.containsKey(action)){
                    cnt=LPDetermination.actionCompCntMap.get(action);
                }
                LPDetermination.actionCompCntMap.put(action, ++cnt);                
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }        
        
    }
    public static void addIntentFilterCategories(Map<Integer, IntentFilter> iFiltersMap){
        try{
        ResultSet records = executeQuery("select ifc.filter_id, cs.st category\n" +
                                        " from IFCategories ifc, CategoryStrings cs\n" +
                                        " where ifc.category=cs.id;" );
        while (records.next()){
            Integer id = records.getInt("filter_id");
            if (iFiltersMap.containsKey(id)){
                iFiltersMap.get(id).getCategories().add(records.getString("category"));
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }                
    }

    public static void addIntentFilterData(Map<Integer, IntentFilter> iFiltersMap){
        try{
        ResultSet records = executeQuery("select filter_id, scheme, type, host, path, port, subtype from IFData;" );
        while (records.next()){
                                    //String scheme, String mimeType, String host, String pathpattern, String port, String subtype
            Data data = new Data(records.getString("scheme"), records.getString("type"), records.getString("host"), 
                                records.getString("path"), records.getString("port"), records.getString("subtype"));
            
            Integer id = records.getInt("filter_id");
            if (iFiltersMap.containsKey(id)){
                iFiltersMap.get(id).getData().add(data);
            
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }        
        
    }

    private static String getType(String kind){
        switch(kind){
            case "a":
                return "activity";
            case "s":
                return "service";
            case "p":
                return "provider";
            case "r":
                return "receiver";
        }
        return kind;
    }
    
    public static void addContentProviderAccess(Map<Integer, Integer> compContentProviderMap, int bundleNo){
        try{
//            System.out.println("addContentProviderAccess ....");
        ResultSet records = executeQuery("select sender_component_id, uri, authority  from accesstocontentprovider where authority is not null and bundle="+bundleNo );
        Integer senderDsmIdx=null;
        String receiverUri=null;
        String receiverAuthority=null;
        while (records.next()){
            int sender_comp_id=records.getInt("sender_component_id");
            senderDsmIdx = componentsMap.get(sender_comp_id).getDsmIdx();
            receiverUri = records.getString("uri");
            receiverAuthority = records.getString("authority");
            compsAuthorityPrms(receiverAuthority, sender_comp_id);
            
            Component receiverCP = getCPComponentDsmIdx(receiverUri);

            if (receiverCP!=null){
                LPDetermination.DATA_ACCESS_REDUCDANT++;
//                LPDetermination.interAppInstance(LPDetermination.interAppVar.LP_DOMAIN1_CP , records.getInt("sender_component_id"), receiverCP.getComponentId());
                compContentProviderMap.put(senderDsmIdx, receiverCP.getDsmIdx());
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }        
    }

    private static void compsAuthorityPrms(String authority, Integer compId){
        Set<String> prms = authorityPermissionsMap.get(authority);        
        if(prms != null){            
            Component c = componentsMap.get(compId);
            Application app = apps.get(c.getPackageName());
            List appPrms = app.getAppUsesPermissions();
            if (appPrms !=null){
                for (String prm : prms){                    
                    if(appPrms.contains(prm)){
                        c.getActuallyUsedPermissions().add(prm);
                        System.out.println("Component "+c.getFullName()+" uses "+prm+" from the authority "+authority+" "+c.getActuallyUsedPermissions());
                    }
                }
            }
        }
    }
    
    private static Component getCPComponentDsmIdx(String uri){
        if (uri==null || uri.isEmpty())
            return null;
        for (Component c : LPDetermination.componentsMap.values()){
            if ("provider".equals(c.getType()) && c.getProviderAuthority()!=null && uri.contains(c.getProviderAuthority())){
                    return c;
            }
        }
        return null;
    }
        
    public static void addExplicitIntents(List<Intent> intents, int bundleNo){
        try{
        ResultSet records = executeQuery("select intent_id, implicit, sender_app, sender_app_id, "
                + "sender_class, sender_component_id, receiver_kind, receiver_package, receiver_class "
                + "from ExplicitCommunication where bundle="+bundleNo);
        
        while (records.next()){
            explicitIntentsCnt++;
            String receiverType =  records.getString("receiver_kind");
            String receiverClass = records.getString("receiver_class");
            if (! receiverClass.trim().isEmpty()){
                receiverType = getType(receiverType);
                Intent intent = new Intent(records.getInt("intent_id"), records.getInt("sender_component_id"),records.getString("sender_app"),
                        records.getString("sender_class"),receiverClass,receiverType, null, records.getString("receiver_package"));            
                intents.add(intent);
            }
            
        }
        
//        System.out.println("===================================");
//        for (Intent i : intents){
//            if (i.getReceiver()!=null){
//                System.out.println(i.getSender()+" "+i.getSenderComponentId()+" "+i.getReceiver());
//            }
//        }
//        System.out.println("===================================");
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    
    private static Map<Integer, List<String>> loadIntentCategories(){
        Map<Integer, List<String>> categories = new HashMap<>();
        //add intent categories
        ResultSet records=null;
        records = executeQuery("select ic.intent_id, cs.st category from ICategories ic, CategoryStrings cs "
                + "where ic.category=cs.id;" );
            try{
        while (records.next()){
                Integer intentId = records.getInt("intent_id");
                if (categories.containsKey(intentId)){
                    categories.get(intentId).add(records.getString("category"));            
                }else{
                    List<String> list = new ArrayList<>();
                    list.add(records.getString("category"));
                    categories.put(intentId, list);
                }
        }        
            }catch(Exception e){
                e.printStackTrace();
            }
            
            return categories;
    }
    private static Map<Integer, Data> loadIntentData(){
        Map<Integer, Data> intentDataMap = new HashMap<>();
        try{
                ResultSet intentData = executeQuery("select intent_id, scheme, ssp, uri, path, query, authority from IData, uriData "
                        + "where IData.`data`=uriData.id;");
                while (intentData.next()){
                    Data data= new Data();
                    Integer intentId = intentData.getInt("intent_id");
                    data.setScheme(intentData.getString("scheme"));
                    data.setSsp(intentData.getString("ssp"));
                    data.setPath(intentData.getString("path"));
                    data.setAuthority(intentData.getString("authority"));
                    data.setQuery(intentData.getString("query"));
                    data.setUri(intentData.getString("uri"));
                    
                    intentDataMap.put(intentId, data);
                }            
        }catch(Exception e){
            e.printStackTrace();
        }
        return intentDataMap;
    }
    public static void addImplicitIntents(List<Intent> intents, int bundleNo){
        Map<Integer, List<String>> intentCategories = loadIntentCategories();
        Map<Integer, Data> intentDataMap = loadIntentData();

        try{
        ResultSet records = executeQuery("select distinct intent_id, sender_app_id, sender_app, sender_class, receiver_kind, action, sender_component_id "
                + "from implicitcommunication where bundle="+bundleNo );
        while (records.next()){
            implicitIntentsCnt++;
            String receiverType =  records.getString("receiver_kind");
            int intentId = records.getInt("intent_id");
            receiverType = getType(receiverType);
            Intent intent = new Intent(intentId, records.getInt("sender_component_id"),
                    records.getString("sender_app"),
                    records.getString("sender_class"),null,receiverType, records.getString("action"), null);
            intent.setReceiverType(records.getString("receiver_kind"));
                //Add Intent categories
            if (intentCategories.get(intentId)!=null){
                intent.setCategories(intentCategories.get(intentId));
            }
            if(intentDataMap.get(intentId)!=null){
                //Add Intent data
                intent.setData(intentDataMap.get(intentId));
            }
            
                
                boolean b = intents.add(intent);
            }
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
 
}
