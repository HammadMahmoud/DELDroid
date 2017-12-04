/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import edu.uci.seal.deldroid.attack.UnauthorizedIntentReceipt;
import edu.uci.seal.deldroid.attack.PrivEscalationInstance;
import edu.uci.seal.deldroid.attack.IntentSpoofing;
import edu.uci.seal.deldroid.db.DataManager;
import static edu.uci.seal.deldroid.db.DataManager.*;
import edu.uci.seal.deldroid.lp.ECArule.EcaRuleAction;
import static edu.uci.seal.deldroid.lp.XmlParserUsingSAX.appId;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import static jdk.nashorn.internal.objects.NativeString.match;
import edu.uci.seal.deldroid.model.Application;
import edu.uci.seal.deldroid.model.Component;
import edu.uci.seal.deldroid.model.Data;
import edu.uci.seal.deldroid.model.Intent;
import edu.uci.seal.deldroid.model.IntentFilter;
import edu.uci.seal.deldroid.model.Permission;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.ANDROID_FRAMEWORK_MANIFEST_PATH;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.DSM_FILES_PATH;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.ArchExtractor_FILES_PATH;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.EXPERIMENT_RESULTS_FILES_PATH;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.resourceSysServiceFile;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.LP_RULES_PATH;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.cpAuthorityPermissionsFile;

/**
 *
 * @author Mahmoud
 */
public class LPDetermination {

    static int BUNDLE_NO = 5;

    static boolean TESTING_EXPERIMENT = false;
    static boolean SHORT_NAME=true;
    static boolean PRINT_DSM_INFO=true;
    static boolean PERFORM_DATA_TEST = true; //includes the Data test in the Intent resolution
    static boolean ONLY_IAC_VUL = true; //consider only inter app vulnerabilities
    static boolean GENERATE_VISUALIZATION_FILES = true;

    //check if this communication is exist in the LP architecture
    static int CHECK_SENDER = 16656;
    static int CHECK_RECEIVER = 16737;

    
    static PrintWriter iac;
    static Set<String> uniqueIAC; //each element is senderComponentID,receiverComponentId

    public static Map<Integer, Permission> permissionsMap;
    public static Map<String, Application> apps;
    public static Set<String> allUsedPermissions;
    public static Map<Integer, Component> componentsMap; //map componentId to its component
//    static Map<String, String> actionComponentMap;
    static Set<Component> missingComps;
    public static List<Intent> intents;
    public static Map<Integer, IntentFilter> iFiltersMap;
    public static String sysPackageName = "System";
    static List<Intent> unhandledIntents;
//    static Component[] orderedComps;
    static int[][] dsm;
    static int[][] opDSM;
    static Map<String, String> PrmResourceMap; //map the permission to a resource name which is a permission group an dtreated here as system component
    static Map<String, Set<String>> resourceSysServiceMap; //map the resource name (permission group) to the system service that requires this permission
    static Set<ECArule> ECArules;
    static Set<ECAServiceRule> ECAServiceRules;
    public static Map<Integer, Integer> dsmIdxComponentIdMap; //map a dsm index to component id
    static Map<Integer, Integer> compContentProviderMap; //map a component (dsm index) to the content provider (dsm index) that it accesses
    public static Map<String, Integer> actionCompCntMap;
    static int resourceStartIdx = Integer.MAX_VALUE; //the index of the first resource
    static Set<PrivEscalationInstance> opPrivInstances;
    static Set<PrivEscalationInstance> lpPrivInstances;
    
    static Set<UnauthorizedIntentReceipt> lp_uir;
    static Set<IntentSpoofing> lp_is;
//    static int lp_unauthorizedIntentReceipts = 0;
//    static int lp_intentSpoofing = 0;

    static Set<UnauthorizedIntentReceipt> op_uir;
    static Set<IntentSpoofing> op_is;
//    static int op_unauthorizedIntentReceipts = 0;
//    static int op_intentSpoofing = 0;
    
    static Set<String> contrainedContextServices; //set of the contained resources
    
    static int lpDependenciesCnt = 0;
    static int lppermissionGrantedDomainCnt=0;
    static int lpECArulesAllowedCommunication=0;
    static int opDependenciesCnt = 0;    
    static int compsCnt = 0; //components domain
    static int permissionsCnt = 0; //all permissions, used and unused
    static Set<String> utilizedPermissions; //list of the utilized permissions. Permission Granted Domain
    static int compsDependencies = 0;  //LP: number of dependencies in the components domain
    static int opCompsDependencies = 0;  //OP: number of dependencies in the components domain
    static int HandledExplicitIntentsCnt = 0;
    static int HandledImplicitIntentsCnt = 0;
    static int OP_compsAccessCP = 0; //number of components that can access content providers from external appsInAttacks
    static int unusedSystemIntentsCnt = 0; //number of protected broadcast Intents that the systems send but not used for this Android system
    static int usedSystemIntentsCnt = 0; //number of protected broadcast Intents that can be received by the modeled Android system
    static DecimalFormat dff = new DecimalFormat("######.####");
    public static int DATA_ACCESS_REDUCDANT = 0; //number of component-to-contentProvider access including the redundant access, i.e. more than one access to the same CP from teh same compoennt
    public static String sep = ","; //separator
    static String rulesPath = LP_RULES_PATH + "rules.txt";
    static String resourcesPath = LP_RULES_PATH + "contrainedServices.txt";    
    static String lpPath = LP_RULES_PATH + "lp.csv";
    static String iccAttacksPath = LP_RULES_PATH + "iccAtacks.txt";
    public static Map<String,Set<String>> authorityPermissionsMap;

    //inter-app (IA) statistics    
    static int[] interAppStatistics;

    private static void addAllUsedPermissions() {
        for (Application app : apps.values()){
            for (String p : app.getAppUsesPermissions()){
                allUsedPermissions.add(p);
            }
        }        
    }

    public static enum interAppVar {

        LP_DOMAIN1_EX(0), LP_DOMAIN1_IM(1), LP_DOMAIN1_CP(2),
        //LP_PRIV(3),
        OP_DOMAIN1_EX(4), OP_DOMAIN1_CP(5),
        //OP_PRIV(6),
        
        //Order the IAC based on their severity level (lower is better):
        //Passive vulnerability type: the Sender(S) is vulnerable since it uses permission(s). Unauthorized Intent Receipt. R is the potential malicious.
        //Active vulnerability type: Receiver(R) is vulnerable since it uses permission(S). Intent Spoofing. S is a potential malicious.
        //each IAC is counted once on the more sever level. 
        //For example, if both S and R have permission in implicit IAC, we counted it in the IA_LP_L6_R_PRM

        //IAC through Explicit Intent
        IA_LP_L1_NO_PRM(7), //severity 1: explicit IAC where neither Sender(S) nor Receiver(R) uses permission(s)
        IA_LP_L2_S_PRM(8), //severity 2: explicit IAC where S uses permission(S). Passive
        IA_LP_L3_R_PRM(9), //severity 3: explicit IAC where R uses permission(S). Active
        //IAC through Implicit Intent
        IA_LP_L4_NO_PRM(10), //severity 4: implicit IAC where neither S nor R use permission(s)
        IA_LP_L5_S_PRM(11), //severity 5: implicit IAC where S uses permission(S). Passive
        IA_LP_L6_R_PRM(12), //severity 6: implicit IAC where R uses permission(S). Active
        //IAC through Implicit Intent, where the R is set to exported=false. Although this does not make the component private since there is an IF, it conveys the intention of the developer for making that component private.
        IA_LP_L7_PRIVATE_NO_PRM(13), //severity 7: implicit IAC where neither S nor R use permission(s) and R's exported=false
        IA_LP_L8_PRIVATE_R_PRM(14) //severity 8: implicit IAC where R uses permission(S) and its exported=false. Active
        ;
        private int idx;

        interAppVar(int idx) {
            this.idx = idx;
        }

        public int getIndex() {
            return this.idx;
        }
        public static int getMaxIndex(){
            return 14+1;
        }
    };

    static final String EXP_HEADER = "\nBundle,Apps,Components,Explicit Intents,Implicit Intents,Intents,"
            + "Intent Filters,"
            + "OP-CP access,LP-CP all access,LP-CP unique access,"
            + "OP Domain1,LP Domain1, Reduction Domain1%,"
            + "OP Domain2,LP Domain2, Reduction Domain2%,"
            + "IA-OP Domain1,CP-IA-OP Domain1,IA-OP-all Domain1,"
            + "Total IA-LP Domain1,IAC Reduction Domain1%,"
            + "Explicit IA-LP Domain1,Implicit IA-LP Domain1,CP IA-LP Domain1,"
            + "L1,L2,L3,L4,L5,L6,L7,L8,"
            + "OP Priv. Esc. Instances,LP Priv. Esc. Instances,Priv. Esc. Reduction%,"
            + "IA-OP Priv,IA-LP Priv,IA-Priv. Esc. Reduction%,"
            + "IA-OP Intent Spoofing,IA-OP IS Apps,IA-OP Unauthorized Intent Receipt,IA-OP UIR Apps,IA-OP ICC Apps,"
            + "IA-LP Intent Spoofing,IA-LP IS Apps,IA-LP Unauthorized Intent Receipt,IA-LP UIR Apps,IA-LP ICC Apps,"
            + "Max ICC-ECA,All Generated ICC-ECA,ECA Reduction%,LP ICC-ECA for Allowed Communications,"
            + "Max Resources-ECA,Generated Resources-ECA,ECA Reduction%,"            
            + "OP-DSM time(S),OP Analysis time(S),"
            + "LP-DSM time(S),LP Analysis time(S),ECA rules time(S),"
            + "Determination time (S) (2nd-3rd-4th phases)\n";

    public static void main(String[] srgs) {
        try {
//        String pkg = "edu.uci.seal.myapplication.xml";
            Date date1 = new Date();
            Date dateDsm1 = new Date();
            Date dateOpDsm1 = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yy_H-mm");
//        String outputPath = DSM_FILES_PATH + "B"+BUNDLE_NO+"_DSM_" + formatter.format(date1) + ".txt";
            String outputPath = DSM_FILES_PATH + "B" + BUNDLE_NO + "_" + date1.getTime() + ".txt";
            if (SHORT_NAME) {
//                outputPath = DSM_FILES_PATH + "system_info_analysis.txt";
                outputPath = DSM_FILES_PATH + "B" + BUNDLE_NO+".txt";
            }
            if (TESTING_EXPERIMENT) {
                outputPath = DSM_FILES_PATH + "system_info_analysis.txt";
            }
            PrintWriter outputWriter = new PrintWriter(outputPath, "UTF-8");
            iac = new PrintWriter(DSM_FILES_PATH + "iac.txt", "UTF-8");
            PrintWriter lpFile = new PrintWriter(lpPath, "UTF-8");
            PrintWriter rulesFile = new PrintWriter(rulesPath, "UTF-8");
            PrintWriter iccAttacksFile = new PrintWriter(iccAttacksPath, "UTF-8");
            PrintWriter resourcesFile = new PrintWriter(resourcesPath, "UTF-8");
            
            

            outputWriter.write("*************************************************************\n");
            outputWriter.write("Bundle(" + BUNDLE_NO + "): Start generating DSM @ " + formatter.format(date1) + "\n");
            outputWriter.write("*************************************************************\n");

            init();

            ///////////////// Extract filterData from IC3
            readAuthorityPermissionsMap();
            DataManager.prepare();
            //Add IC3 appsInAttacks
            addIC3Applications(LPDetermination.apps, BUNDLE_NO);
            addIC3AppUsesPermissions(LPDetermination.apps, BUNDLE_NO);
            addAllUsedPermissions();
            //Domain 1: IC3 components
            addIC3Components(LPDetermination.componentsMap, BUNDLE_NO);
            addProvidersPermissions(LPDetermination.componentsMap, BUNDLE_NO);
            //Add IntentFilters to their components
            addIntentFilters(LPDetermination.componentsMap, iFiltersMap, BUNDLE_NO);
            compsCnt = componentsMap.size();

            //Domain 2: system permissions
            extractSystemInfo();
            permissionsCnt = componentsMap.size() - compsCnt;
            int systemProtectedIntents = intents.size();
//
            //Add Explicit Intents
            addExplicitIntents(LPDetermination.intents, BUNDLE_NO);
//        System.out.println("IC3 explicit Intents are: "+(intents.size()-systemProtectedIntents));
//            int ic3ExplicitIntents = (intents.size() - systemProtectedIntents);
            //Add Implicit Intents
            addImplicitIntents(LPDetermination.intents, BUNDLE_NO);
//            int ic3ImplicitIntents = intents.size() - systemProtectedIntents - ic3ExplicitIntents;            
//        System.out.println("IC3 implicit Intents are: "+ic3ImplicitIntents);
            //////////////////////////////////////// ArchExtractor
            extractInfo(ArchExtractor_FILES_PATH, outputWriter); //extract components information from ArchExtractor
//            int archExtractorIntents = intents.size() - systemProtectedIntents - ic3ExplicitIntents - ic3ImplicitIntents;
//        System.out.println("ArchExtractor Intents are: "+archExtractorIntents);
//            compsCnt = componentsMap.size() - permissionsCnt;
//        System.out.println("Components : " + compsCnt+" - "+permissionsCnt);
            compsCnt = componentsMap.size() - permissionsCnt;
            addMissingCompsToComps();//components that are discovered from the intents
//        System.out.println("components after missing: " + compsCnt);

            ////////////////////////////////////////
            updateComponentsDsmIdx();//this is a very important step
            DataManager.addContentProviderAccess(compContentProviderMap, BUNDLE_NO);
            generateDSM(); //calculate the component communications from intents
            compsDependencies = lpDependenciesCnt; //before adding Comp-Service dependincies
            compToResourceDSM();//component to system's resource communication
//        System.out.println("Unhandled Intents:"+unhandledIntents.size());
            Date dateDsm2 = new Date();

            generateOpDSM(); //over-privilaged DSM
            Date dateOpDsm2 = new Date();

            Date generateRulesD1 = new Date();
//            generateECARules(dsm); //ICC-ECA
            efficientlyGenerateECARules(dsm);
            lpECArulesAllowedCommunication = ECArules.size();
            ecaRulesPreventApps(dsm);
            Date generateRulesD2 = new Date();

            Date lpPrivAnalysisD1 = new Date();
            lp_uir = new HashSet<>();
            lp_is = new HashSet<>();
            lpPrivInstances = new HashSet<>();
            MatrixAnalysis.securityAnalysis(dsm, resourceStartIdx,lp_uir, lp_is, lpPrivInstances);
            Date lpPrivAnalysisD2 = new Date();
            printPrivEscalationInstance(lpPrivInstances, "LP Privilege Analysis results", outputWriter);
            

            Date opPrivAnalysisD1 = new Date();
            op_uir = new HashSet<>();
            op_is = new HashSet<>();
            opPrivInstances = new HashSet<>();
            MatrixAnalysis.securityAnalysis(opDSM, resourceStartIdx,op_uir, op_is, opPrivInstances);
//            printPrivEscalationInstance(opPrivInstances, "OP Privilege Analysis results", outputWriter);
            Date opPrivAnalysisD2 = new Date();

            Date date2 = new Date();
            //icc attacks
            Set<String> op_uir_apps = uirApps(op_uir);
            Set<String> op_is_apps = isApps(op_is);
            Set<String> op_pe_apps = peApps(opPrivInstances);
            Set<String> op_icc_apps = new HashSet(op_uir_apps);
            op_icc_apps.addAll(op_is_apps);
            op_icc_apps.addAll(op_pe_apps);
            
            Set<String> lp_uir_apps = uirApps(lp_uir);
            Set<String> lp_is_apps = isApps(lp_is);
            Set<String> lp_pe_apps = peApps(lpPrivInstances);
            Set<String> lp_icc_apps = new HashSet();
            lp_icc_apps.addAll(lp_uir_apps);
            lp_icc_apps.addAll(lp_is_apps);
            lp_icc_apps.addAll(lp_pe_apps);

            
            long elapsedTime = (date2.getTime() - date1.getTime());
            long dsmTime = (dateDsm2.getTime() - dateDsm1.getTime());
            long opDsmTime = (dateOpDsm2.getTime() - dateOpDsm1.getTime());
            long generateRulesTime = (generateRulesD2.getTime() - generateRulesD1.getTime());
            long lpPrivAnalysisTime = (lpPrivAnalysisD2.getTime() - lpPrivAnalysisD1.getTime());
            long opPrivAnalysisTime = (opPrivAnalysisD2.getTime() - opPrivAnalysisD1.getTime());

        //calculate the empty columns before printing the DSM
            //if lpEmptyColumns[i]==true then, column i in the lp-dsm is empty
            boolean[] lpEmptyColumns = MatrixAnalysis.emptyColumns(dsm, resourceStartIdx);
            boolean[] opEmptyColumns = MatrixAnalysis.emptyColumns(opDSM, resourceStartIdx);
            
            

            if (PRINT_DSM_INFO){
                printApps(outputWriter);
                printDSM(outputWriter, dsm, "LP DSM", lpEmptyColumns, lpFile);
//            printDSM(outputWriter, opDSM, "OP DSM", opEmptyColumns, null);
            printComponents(outputWriter, lpEmptyColumns, opEmptyColumns);
            printIntents(outputWriter);
            printUnhandledIntents(outputWriter);            
            printECARules(outputWriter, rulesFile);                  
            printContrainedResources(resourcesFile);
            }
            
            //Number of elements
            outputWriter.write("Apps:" + (apps.size() - 1) + "\n"); //exclude the system app        
            String actuallyUsedPrmsInBundle = percentOfActuallyUsedPrmsInBundle();
            outputWriter.write("Permission Granted Domain:" + utilizedPermissions.size() + "\n");// ", Percent of actually used permissions in all appsInAttacks: "+actuallyUsedPrmsInBundle+"\n");
            outputWriter.write("Components:" + compsCnt + "\n");
            outputWriter.write("Intents:" + " Explicit(" + Math.max(DataManager.explicitIntentsCnt, HandledExplicitIntentsCnt)
                    + ") Implicit(" + (DataManager.implicitIntentsCnt + usedSystemIntentsCnt) + ")\n");
            outputWriter.write("Handled Intents:" + (HandledExplicitIntentsCnt + HandledImplicitIntentsCnt) + " Explicit(" + HandledExplicitIntentsCnt + ") Implicit(" + HandledImplicitIntentsCnt + ")\n");
            outputWriter.write("IntentFilters:" + iFiltersMap.size() + "\n");
            //Time
            outputWriter.write("Design time starts @ " + formatter.format(date1) + " and ends @ " + formatter.format(date2) + " .Total time (" + +elapsedTime + ") milliseconds\n");
            outputWriter.write("LP-DSM generating time (" + dsmTime + ") milliseconds\n");
            outputWriter.write("OP-DSM generating time (" + opDsmTime + ") milliseconds\n");
            outputWriter.write("ECA Rules generating time (" + generateRulesTime + ") milliseconds\n");
            outputWriter.write("LP Privilege escalation analysis time(" + lpPrivAnalysisTime + ") milliseconds\n");
            outputWriter.write("OP Privilege escalation analysis time(" + opPrivAnalysisTime + ") milliseconds\n");
//        double savedAnalysisTimePrcnt = ((opPrivAnalysisTime - lpPrivAnalysisTime) / (double) opPrivAnalysisTime) * 100;
//        outputWriter.write("Saved privilege escalation analysis time(" + dff.format (savedAnalysisTimePrcnt) + ") milliseconds\n");

            //Dependencies        
            outputWriter.write("LP-DSM component-component communications (domain 1) (" + compsDependencies + ") \n");
            outputWriter.write("LP-DSM component-resource dependencies (domain 2) (" + (lpDependenciesCnt - compsDependencies) + ") \n");
            outputWriter.write("LP-DSM all entries (domain 1 and domain 2) (" + lpDependenciesCnt + ") \n");

            outputWriter.write("OP-DSM component-component communications (domain 1) (" + opCompsDependencies + ") \n");
            outputWriter.write("OP-DSM component-resource dependencies (domain 2) (" + (opDependenciesCnt - opCompsDependencies) + ") \n");
            outputWriter.write("OP-DSM all entries (domain 1 and domain 2) (" + opDependenciesCnt + ") \n");

            int reduced = (opDependenciesCnt - lpDependenciesCnt);
            double reducedPrcnt = (reduced / (double) opDependenciesCnt) * 100;
            outputWriter.write("Number of reduced dependencies (" + reduced + ") reduction percent (" + dff.format(reducedPrcnt) + ") \n");

            int reducedDomain1 = (opCompsDependencies - compsDependencies);
            double reducedDomain1Prcnt = (reducedDomain1 / (double) opCompsDependencies) * 100;
            outputWriter.write("Number of reduced dependencies (" + reducedDomain1 + ") reduction percent (" + dff.format(reducedDomain1Prcnt) + ") \n");

            int opDomain2 = (opDependenciesCnt - opCompsDependencies);
            int lpDomain2 = (lpDependenciesCnt - compsDependencies);
            int reducedDomain2 = (opDomain2 - lpDomain2);
            double reducedDomain2Prcnt = (reducedDomain2 / (double) opDomain2) * 100;
            outputWriter.write("Number of reduced dependencies (" + reducedDomain2 + ") reduction percent (" + dff.format(reducedDomain2Prcnt) + ") \n");

            //ECA rules
            int maxCommunicationRules = (compsCnt * compsCnt);// - compsDependencies;
            outputWriter.write("Max number of ICC-ECA rules {comps*comps} (" + maxCommunicationRules + ") \n");
            outputWriter.write("Number of created ICC-ECA rules for the allowed communications (" + lpECArulesAllowedCommunication + ") \n");            
            outputWriter.write("Number of created ICC-ECA rules (" + ECArules.size() + ") \n");
            int ecaRedection = (maxCommunicationRules - ECArules.size());
            double ecaRedectionPrcnt = (ecaRedection / (double) maxCommunicationRules) * 100;
//            outputWriter.write("Number of reduced ICC-ECA rules  (" + ecaRedection + ") reduction percent (" + dff.format(ecaRedectionPrcnt) + ") \n");
            outputWriter.write("ICC-ECA reduction (" + dff.format(ecaRedectionPrcnt) + ") \n");
            int numberOfResources = utilizedPermissions.size(); //Permission Granted Domain
            int maxResourcesRules = calculateMaxResourcesRules();
            double ecaResourcesRedectionPrcnt = ((maxResourcesRules - ECAServiceRules.size()) / (double) maxResourcesRules) * 100;            
            outputWriter.write("Max number of Resource-ECA rules (" + maxResourcesRules + ") \n");
            outputWriter.write("Number of created Resource-ECA rules (" + ECAServiceRules.size() +" comparing to "+lpDomain2+ " lppermissionGrantedDomainCnt:"+lppermissionGrantedDomainCnt+") \n");
            outputWriter.write("Resource-ECA reduction (" + dff.format(ecaResourcesRedectionPrcnt) + ") \n");
            outputWriter.write("************************ INTER APP STATISTICS **************************\n");
            outputWriter.write("Number of Explicit Inter-App Communications in LP-Domain1 (" + interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()] + ")\n");
            outputWriter.write("Number of Implicit Inter-App Communications in LP-Domain1 (" + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()] + ")\n");
            outputWriter.write("Number of CP access Inter-App Communications in LP-Domain1 (" + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()] + ")\n");
            outputWriter.write("Number of Inter-App Communications in LP-Domain1 (" + (interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()]
                    + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()] + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()]) + ")\n");

            outputWriter.write("Number of allowed Inter-App Communications in OP-Domain1 (" + interAppStatistics[interAppVar.OP_DOMAIN1_EX.getIndex()] + ")\n");
            outputWriter.write("Number of CP access Inter-App Communications in OP-Domain1 (" + interAppStatistics[interAppVar.OP_DOMAIN1_CP.getIndex()] + ")\n");
            outputWriter.write("*************************************************************\n");
            
//            System.out.println("Number of Inter-App Priv-Escalation instances in LP (" + interAppStatistics[interAppVar.LP_PRIV.getIndex()] + ")\n");
            //  ICC attacks
            
            outputWriter.write("OP ICC Attacks\n");
            outputWriter.write("Privilege Escalation attacks:"+opPrivInstances.size()+" in "+op_pe_apps.size()+" apps\n");
            outputWriter.write("Unauthorized Intent receipt ICC attacks:"+op_uir.size()+" in "+op_uir_apps.size()+" apps\n");
            outputWriter.write("Intent spoofing ICC attacks:"+op_is.size()+" in "+op_is_apps.size()+"\n");            
            outputWriter.write("OP ICC Attacks Apps "+op_icc_apps.size() +"\n");
            

            outputWriter.write("LP ICC Attacks\n");
            outputWriter.write("Privilege Escalation attacks:"+lpPrivInstances.size()+" in "+lp_pe_apps.size()+" apps\n");
            outputWriter.write("Unauthorized Intent receipt ICC attacks:"+lp_uir.size()+" in "+lp_uir_apps.size()+" apps\n");
            outputWriter.write("Intent spoofing ICC attacks:"+lp_is.size()+" in "+lp_is_apps.size()+" apps\n");
            outputWriter.write("LP ICC Attacks Apps "+lp_icc_apps.size() +"\n");

            outputWriter.write("*************************************************************\n");
            
            int IAreducedDomain1 = (interAppStatistics[interAppVar.OP_DOMAIN1_EX.getIndex()] + interAppStatistics[interAppVar.OP_DOMAIN1_CP.getIndex()])
                    - (interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()] + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()] + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()]);
            double IAreducedDomain1Prcnt = (IAreducedDomain1 / (double) (interAppStatistics[interAppVar.OP_DOMAIN1_EX.getIndex()] + interAppStatistics[interAppVar.OP_DOMAIN1_CP.getIndex()])) * 100;

            int IAprivReduced = opPrivInstances.size() - lpPrivInstances.size();
            double IAprivReducedPrcnt = (IAprivReduced / (double) ( opPrivInstances.size())) * 100;
            double privReductionPrcnt = ((opPrivInstances.size() - lpPrivInstances.size()) / (double) (opPrivInstances.size())) * 100;

            String summary = "\nBundle " + BUNDLE_NO + sep + (apps.size() - 1) + sep + compsCnt + sep
                    + HandledExplicitIntentsCnt + sep + HandledImplicitIntentsCnt + sep + (HandledExplicitIntentsCnt + HandledImplicitIntentsCnt) + sep
                    + iFiltersMap.size() + sep
                    + OP_compsAccessCP + sep + DATA_ACCESS_REDUCDANT + sep + compContentProviderMap.size() + sep
                    + opCompsDependencies + sep + compsDependencies + sep + dff.format(reducedDomain1Prcnt) + sep
                    + opDomain2 + sep + lpDomain2 + sep + dff.format(reducedDomain2Prcnt) + sep
                    + interAppStatistics[interAppVar.OP_DOMAIN1_EX.getIndex()] + sep
                    + interAppStatistics[interAppVar.OP_DOMAIN1_CP.getIndex()] + sep
                    + (interAppStatistics[interAppVar.OP_DOMAIN1_EX.getIndex()] + interAppStatistics[interAppVar.OP_DOMAIN1_CP.getIndex()]) + sep
                    + (interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()] + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()] + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()]) + sep
                    + dff.format(IAreducedDomain1Prcnt) + sep
                    + interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()] + sep
                    + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()] + sep
                    + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()] + sep
                    //Ranking IAC
                    + interAppStatistics[interAppVar.IA_LP_L1_NO_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L2_S_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L3_R_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L4_NO_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L5_S_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L6_R_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L7_PRIVATE_NO_PRM.getIndex()] + sep
                    + interAppStatistics[interAppVar.IA_LP_L8_PRIVATE_R_PRM.getIndex()] + sep
                    + opPrivInstances.size() + sep + lpPrivInstances.size() + sep + dff.format(privReductionPrcnt) + sep
                    //IA Priv. Esc. Reduction
                    + opPrivInstances.size() + sep
                    + lpPrivInstances.size() + sep + dff.format(IAprivReducedPrcnt) + sep
                    //ICC Attacks
                    + op_is.size()+sep+op_is_apps.size()+sep+op_uir.size()+sep+op_uir_apps.size()+sep+op_icc_apps.size()+sep
                    + lp_is.size()+sep+lp_is_apps.size()+sep+lp_uir.size()+sep+lp_uir_apps.size()+sep+lp_icc_apps.size()+sep
                    //ICC-ECA rules
                    + maxCommunicationRules + sep + ECArules.size() + sep + dff.format(ecaRedectionPrcnt) + sep
                    +lpECArulesAllowedCommunication+sep
                    //Resources ECA rules
                    + maxResourcesRules + sep + ECAServiceRules.size() + sep + ecaResourcesRedectionPrcnt + sep                    
                    //Time
                    + dff.format((double) opDsmTime/1000) + sep + dff.format((double) opPrivAnalysisTime/1000) + sep
                    + dff.format((double) dsmTime/1000) + sep + dff.format((double) lpPrivAnalysisTime/1000) + sep + 
                            dff.format((double) generateRulesTime/1000) + sep
                    + dff.format((double) elapsedTime/1000);

            outputWriter.write(EXP_HEADER);
            outputWriter.write(summary);
            outputWriter.write("\n*************************************************************\n");
            
            if (PRINT_DSM_INFO){
                printIccAttacks(iccAttacksFile, op_icc_apps, lp_icc_apps);
            }

            
            lpFile.close();
            rulesFile.close();
            iccAttacksFile.close();
            outputWriter.close();
            resourcesFile.close();
            iac.close();

            //write the results in the experiment file
            Path path = Paths.get(EXPERIMENT_RESULTS_FILES_PATH);
            if (! Files.exists(path)){
                Files.write(path, (EXP_HEADER).getBytes(), StandardOpenOption.CREATE);
            }
            Files.write(path, (summary).getBytes(), StandardOpenOption.APPEND);
            
//            System.out.println("Number Explicit LP-IAC:" + interAppStatistics[interAppVar.LP_DOMAIN1_EX.getIndex()]);
//            System.out.println("Number Implicit LP-IAC:" + interAppStatistics[interAppVar.LP_DOMAIN1_IM.getIndex()]);
//            System.out.println("Number CP access LP-IAC:" + interAppStatistics[interAppVar.LP_DOMAIN1_CP.getIndex()]);
//            System.out.println("The system has generated the DSM. You can see the output in " + outputPath + " file");
//        System.out.println("The generated DSM is ("+sep+") separated");
//        System.out.println("actionCompCntMap\n" + actionCompCntMap);
            
            checkComm(CHECK_SENDER, CHECK_RECEIVER);
            
            //************** Visualization tool
            if (GENERATE_VISUALIZATION_FILES){
                VisualizationFiles.printApps(apps);
                VisualizationFiles.printAnalysisResult(lp_uir, lp_is, lpPrivInstances);
                VisualizationFiles.printMDM(dsm, dsmIdxComponentIdMap, componentsMap, resourceStartIdx);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static int calculateMaxResourcesRules(){
        int maxRules = 0;
        for (Application app : apps.values() ){
            maxRules = maxRules+ (app.getComponents().size() * app.getAppUsesPermissions().size());
        }
        
        return maxRules;
    }
    
    private static Set<String> uirApps(Set<UnauthorizedIntentReceipt> uirs){
        Set<String> appsInAttacks = new HashSet<>();
        for (UnauthorizedIntentReceipt u: uirs){
            String app1 = u.getVulComponent().getPackageName();
            String app2 = u.getMalComponent().getPackageName();
            appsInAttacks.add(app1);
            appsInAttacks.add(app2);
        }
        return appsInAttacks;
    }

    private static Set<String> isApps(Set<IntentSpoofing> iss){
        Set<String> appsInAttackes = new HashSet<>();
        for (IntentSpoofing u: iss){
            String app1 = u.getMalComponent().getPackageName();
            String app2 = u.getVulComponent().getPackageName();
            appsInAttackes.add(app1);
            appsInAttackes.add(app2);
        }
        return appsInAttackes;
        
    }
    private static Set<String> peApps(Set<PrivEscalationInstance> instances){
        Set<String> appsInAttackes = new HashSet<>();
        for (PrivEscalationInstance instance : instances){
            String app1 = instance.getMalApp();
            String app2 = instance.getVulApp();
            appsInAttackes.add(app1);
            appsInAttackes.add(app2);
        }
        
        return appsInAttackes;
        
    }
    
    
    private static void checkComm(int senderCompId, int receiverCompId){
//       if (senderCompId>0 && receiverCompId>0){
//         int sender = componentsMap.get(senderCompId).getDsmIdx();
//         int receiver = componentsMap.get(receiverCompId).getDsmIdx();
//         if (sender<dsm.length && receiver<dsm.length){
//            System.out.println("dsm["+sender+"]["+receiver+"]="+ dsm[sender][receiver]);
//         }
//        }
    }

    public static void init() {
        resourceStartIdx = Integer.MAX_VALUE;
        XmlParserUsingSAX.appId = 0;
        lpDependenciesCnt = 0;
        opDependenciesCnt = 0;
        compsCnt = 0;
        permissionsCnt = 0;
        compsDependencies = 0;
        opCompsDependencies = 0;
        HandledExplicitIntentsCnt = 0;
        HandledImplicitIntentsCnt = 0;
        unusedSystemIntentsCnt = 0;
        lppermissionGrantedDomainCnt=0;
        DataManager.MIN_COMP_ID = 0;
        DataManager.explicitIntentsCnt = 0;
        DataManager.implicitIntentsCnt = 0;
        lpECArulesAllowedCommunication=0;

     OP_compsAccessCP = 0; //number of components that can access content providers from external appsInAttacks
     usedSystemIntentsCnt = 0; //number of protected broadcast Intents that can be received by the modeled Android system
     dff = new DecimalFormat("######.####");
     DATA_ACCESS_REDUCDANT = 0; //number of component-to-contentProvider access including the redundant access, i.e. more than one access to the same CP from teh same compoennt
     sep = ","; //separator
     rulesPath = LP_RULES_PATH + "rules.txt";
     resourcesPath = LP_RULES_PATH + "contrainedServices.txt";    
     lpPath = LP_RULES_PATH + "lp.csv";
     iccAttacksPath = LP_RULES_PATH + "iccAtacks.txt";

     permissionsMap = new HashMap<>();
        dsm = null;
        opDSM = null;
        iFiltersMap = new HashMap<>();
        apps = new HashMap<>();
        componentsMap = new HashMap<>();
//        actionComponentMap = new HashMap<>();
        intents = new ArrayList<>();
        unhandledIntents = new ArrayList<>();
        missingComps = new HashSet<>();
        PrmResourceMap = new HashMap<>();
        ECArules = new HashSet<>();
        ECAServiceRules = new HashSet<>();
        dsmIdxComponentIdMap = new HashMap<>();
        resourceSysServiceMap = new HashMap<>();
        populateResourceSysServiceMap();
        actionCompCntMap = new HashMap<>();
        compContentProviderMap = new HashMap<>();

        interAppStatistics = new int[interAppVar.getMaxIndex()];
        uniqueIAC = new HashSet<>();
        OP_compsAccessCP = 0;
        DATA_ACCESS_REDUCDANT = 0;
        opPrivInstances = new HashSet<>();
        lpPrivInstances = new HashSet<>();
        authorityPermissionsMap = new HashMap<>();
        utilizedPermissions = new HashSet<>();
        op_uir = new HashSet<>();
        op_is = new HashSet<>();

        lp_uir = new HashSet<>();
        lp_is = new HashSet<>();
        contrainedContextServices = new HashSet<>();
        allUsedPermissions = new HashSet<>();

    }

    private static String percentOfActuallyUsedPrmsInBundle() {
        //returns the average number of used permissions in all appsInAttacks
        double totalAppsPercentages = 0.0;
        for (Application a : apps.values()) {
            if (!sysPackageName.equals(a.getPackageName())) {
                if (!a.getAppUsesPermissions().isEmpty()) {
                    totalAppsPercentages += a.getAppActuallyUsesPermissions().size() / (double) a.getAppUsesPermissions().size();
                }
            }
        }
        double answer = totalAppsPercentages / (double) (apps.size() - 1);
        return dff.format(answer);
    }

    public static void populateResourceSysServiceMap() {
        try {
            Path resourceSysServiceFilePath = Paths.get(resourceSysServiceFile);
            Files.lines(resourceSysServiceFilePath).forEach((line) -> {
                String[] arr = line.split(";");
                if (arr != null && arr.length == 2 && arr[0] != null) {
                    if (resourceSysServiceMap.containsKey(arr[0])) {
                        resourceSysServiceMap.get(arr[0]).add(arr[1]);
                    } else {
                        Set<String> l = new HashSet<>();
                        l.add(arr[1]);
                        resourceSysServiceMap.put(arr[0], l);
                    }
                }
                arr = null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractSystemInfo() {
        //XmlParser parser = new XmlParserUsingDOM();
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            //add the system components 
            File sysXml = new File(ANDROID_FRAMEWORK_MANIFEST_PATH);
            SimpleSAXHandler handler = new SAXHandlerAndroidManifest();
            InputStream is = new FileInputStream(sysXml);
            saxParser.parse(is, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void extractInfo(String dir, PrintWriter writer) {
//        String pkgPath = MODEL_REPOSITORY_PATH+pkg;
        //DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //Date date1 = new Date();
//        outputWriter.write("/*************************************************************\n");
//        outputWriter.write("xml files in " + dir+"\n");
//        outputWriter.write("Start processing xml file @ " + dateFormat.format(date1)+"\n");

        XmlParser parser = new XmlParserUsingSAX();
        parser.parse(dir);
//        Date date2 = new Date();
//        long elapsedTime = (date2.getTime() - date1.getTime());
//        outputWriter.write("End processing xml file @ " + dateFormat.format(date2)+"\n");
//        outputWriter.write("Time elapsed :" + elapsedTime + " milliseconds\n");
//        outputWriter.write("*************************************************************\n\n");
    }

    private static void printApps(PrintWriter writer) {
        writer.write("=============== APPS (" + apps.size() + ") ==============\n");
        apps.values().stream().forEach((app) -> {
            writer.write(app.toString() + "\n");
        });
    }

    private static void printComponents(PrintWriter writer, boolean[] lpEmptyColumns, boolean[] opEmptyColumns) {

        writer.write("=============== Components  (" + compsCnt + ") ==============\n");
        String header = "Package Name,Type,Exported,"
                + "(IF)/(D)|scheme|mimeType|host|path|port|subType|uri/Path/[actions]/[categories],"
                + "[App required prms],[required prms],[uses prms],[actually uses prms],Provider Read Prm,"
                + "Provider Write Prm,Authority,Name,CmponentId,ID,";

        writer.write(header);
        //print the component in ascending order of their DSM index
        //print only the resources that appear either in the LP or OP architecture
        for (int dsmIdx = 0; dsmIdx < dsmIdxComponentIdMap.size(); dsmIdx++) {
            if (dsmIdx >= resourceStartIdx) {
                if (lpEmptyColumns[dsmIdx - resourceStartIdx] || opEmptyColumns[dsmIdx - resourceStartIdx]) {
                    continue;
                }
            }
            Component comp = componentsMap.get(dsmIdxComponentIdMap.get(dsmIdx));
            writer.write(comp.toString() + "\n");
        }
//        outputWriter.write("=============== Missing Components  (" + missingComps.size() + ") ==============\n");
//        missingComps.stream().forEach((m) -> {
//            outputWriter.write(m.toString() + "\n");
//        });
    }

    private static void printIntents(PrintWriter writer) {
        writer.write("=============== INTENTS (" + intents.size() + ")  ==============\n");
        intents.stream().forEach((i) -> {
            if (i.isSysIntent()) {
                usedSystemIntentsCnt++;
            }else{
//                outputWriter.write(i.toString()+"\n");
            }
        });
    }

    private static void printUnhandledIntents(PrintWriter writer) {
        writer.write("=============== UNHANDLED INTENTS (" + unhandledIntents.size() + ")  ==============\n");
        unhandledIntents.stream().forEach((u) -> {
            writer.write(u.toString() + "\n");
        });
    }

    public static void updateComponentsDsmIdx() {
        /*
         update the component's dsmIdx attribute
         insert entries in dsmIdxComponentIdMap
         */
        int idx = 0;
        TreeSet<Component> tree = new TreeSet<>();
        for (Component c : componentsMap.values()) {
            tree.add(c);
        }
        int currIdx = 0;
        Component c = null;
        for (Component t : tree) {
//            t.setDsmIdx(idx++);
            currIdx = idx++;
            c = componentsMap.get(t.getComponentId());
            c.setDsmIdx(currIdx);
            dsmIdxComponentIdMap.put(currIdx, t.getComponentId());
            if ("resource".equals(c.getType())) {
                resourceStartIdx = Math.min(resourceStartIdx, c.getDsmIdx());
            }
        }
        tree = null;
//        System.out.println("dsmIdxComponentIdMap:"+dsmIdxComponentIdMap);                

    }

    static void generateDSM() {
//        System.out.println("dsmIdxComponentIdMap contains " + dsmIdxComponentIdMap.size() + " elements");
        int n = componentsMap.size();
        dsm = new int[n][n];
        for (Intent i : intents) {
                Integer senderComponentId=null;

            if (i.getSenderComponentId() > 0) { //intent from IC3
                senderComponentId = i.getSenderComponentId();
            } else {
                senderComponentId = getComponentKeybyName(i.getSender());
            }

            if (senderComponentId == null) {
                //try the component name without the anonymous (inner) class,
                //the inner class might be a listener class 
                int dollarSignIdx = i.getSender().indexOf('$');
                if (dollarSignIdx > 0) {
                    senderComponentId = getComponentKeybyName(i.getSender().substring(0, dollarSignIdx));

                }
                if (senderComponentId == null) {
//                    System.out.println("Warning: componentsMap does not contain the sender "+i.getSender());
                    if (!i.isSysIntent()) {
                        unhandledIntents.add(i);
                    }
                    continue;
//                    System.exit(0);
                }

            }
            Integer senderIdx = componentsMap.get(senderComponentId).getDsmIdx();

//            int senderIdx = componentsMap.get(i.getSender()).getComponentId();
            if (i.getReceiver() != null) { //explicit intent
//                int receiverIdx = componentsMap.get(i.getReceiver()).getComponentId();
                Integer receiverComponentId = getComponentKeybyName(i.getReceiver());
                if (receiverComponentId == null) {
                    //try the component name without the anonymous (inner) class,
                    //the inner class might be a listener class 
                    int dollarSignIdx = i.getReceiver().indexOf('$');
                    if (dollarSignIdx > 0) {
                        receiverComponentId = getComponentKeybyName(i.getReceiver().substring(0, dollarSignIdx));
                    }
                    if (receiverComponentId == null) {
                        if (i.getReceiver().contains("(.*)")) { //special case
                            if (!i.isSysIntent()) {
                                unhandledIntents.add(i);
                            }
                            continue;
                        }
//                    System.out.println("Warning: componentsMap does not contain the receiver "+i.getReceiver());
                        if (!i.isSysIntent()) {
                            unhandledIntents.add(i);
                        }
//                    System.exit(0);
                        continue;
                    }
                }
                Integer receiverIdx = componentsMap.get(receiverComponentId).getDsmIdx();
                if (dsm[senderIdx][receiverIdx] == 0) {
                    lpDependenciesCnt++;
                }
                dsm[senderIdx][receiverIdx] = 1;
                HandledExplicitIntentsCnt++;
                interAppInstance(interAppVar.LP_DOMAIN1_EX, senderComponentId, receiverComponentId);

            } else { //implicit intent

                String action = i.getAction();
                Set<Integer> candidateReceivers = doIntentResolution(i);
                if (candidateReceivers != null && candidateReceivers.size() > 0) {
                    for (Integer candidateComponentId : candidateReceivers) {
                        if (candidateComponentId != null) {
//                            int rIdx = componentsMap.get(s).getComponentId();
//                            Integer rIdx = getComponentKeybyName(s); 
                            Integer candidateIdx = componentsMap.get(candidateComponentId).getDsmIdx();;
                            int dsmVal = 2; //implicit communication
                            try {
                                if (dsm[senderIdx][candidateIdx] == 0) {
                                    lpDependenciesCnt++;
                                    interAppInstance(interAppVar.LP_DOMAIN1_IM, senderComponentId, candidateComponentId);
                                }
                                if (dsm[senderIdx][candidateIdx] == 1) {
                                    dsmVal = 3; //explicit & impicit communication
                                }
                                dsm[senderIdx][candidateIdx] = dsmVal;

                            } catch (Exception e) {
//                                System.out.println("receiver:" + candidateComponentId);
//                                System.out.println("*** length=" + n + " dsm[" + senderIdx + "][" + candidateIdx + "]");
                                e.printStackTrace();
                                System.exit(0);
                            }

                        }
                    };
                    if (!candidateReceivers.isEmpty()) {
                        HandledImplicitIntentsCnt++;
                    }

                } else { //unhandled intent
                    if (!i.isSysIntent()) {
                        unhandledIntents.add(i);
                    }
                }
            }
        };
        // add a component accessing a content provider
        for (Entry e : compContentProviderMap.entrySet()) {
            dsm[(Integer) e.getKey()][(Integer) e.getValue()] = 5;

        }
    }

    public static boolean interAppInstance(interAppVar interApp, Integer senderComponentId, Integer receiverComponentId) {
        if (sysPackageName.equals(componentsMap.get(senderComponentId).getPackageName())
                || sysPackageName.equals(componentsMap.get(receiverComponentId).getPackageName())) {
            return false;
        }
        if (!componentsMap.get(senderComponentId).getPackageName().equals(
                componentsMap.get(receiverComponentId).getPackageName())) {
            String iacInstance = interApp.getIndex() + "," + senderComponentId + "," + receiverComponentId;
            if (!uniqueIAC.contains(iacInstance)) {
//                if (!(interAppVar.LP_PRIV.equals(interApp) || interAppVar.OP_PRIV.equals(interApp))){
                    uniqueIAC.add(iacInstance);
//                }
                interAppStatistics[interApp.getIndex()] += 1;
                if (interAppVar.LP_DOMAIN1_EX.equals(interApp) || interAppVar.LP_DOMAIN1_IM.equals(interApp)) {
                    checkIACseverity(interApp, senderComponentId, receiverComponentId);
                }
            }
            return true;
        }

        return false;
    }

    private static void checkIACseverity(interAppVar interApp, Integer senderId, Integer receiverId) {
        Component s = componentsMap.get(senderId);
        Component r = componentsMap.get(receiverId);
        boolean sUsesPrm = s.getActuallyUsedPermissions().isEmpty();
        boolean rUsesPrm = r.getActuallyUsedPermissions().isEmpty();
        boolean rExported = "T".equalsIgnoreCase(r.getExported().trim());

        if (interAppVar.LP_DOMAIN1_EX.equals(interApp)) {
            if (!sUsesPrm && !rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L1_NO_PRM.getIndex()] += 1;
            } else if (rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L3_R_PRM.getIndex()] += 1;
            } else if (sUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L2_S_PRM.getIndex()] += 1;
            }
        } else if (interAppVar.LP_DOMAIN1_IM.equals(interApp) && rExported) {
            if (!sUsesPrm && !rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L4_NO_PRM.getIndex()] += 1;
            } else if (rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L6_R_PRM.getIndex()] += 1;
            } else if (sUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L5_S_PRM.getIndex()] += 1;
            }
        } else if (interAppVar.LP_DOMAIN1_IM.equals(interApp) && !rExported) {
            if (!sUsesPrm && !rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L7_PRIVATE_NO_PRM.getIndex()] += 1;
            } else if (rUsesPrm) {
                interAppStatistics[interAppVar.IA_LP_L8_PRIVATE_R_PRM.getIndex()] += 1;
            }
        }
    }

    private static void generateOpDSM() {
        int n = componentsMap.size();
        opDSM = new int[n][n];
        //orderedComps is an array of Component objects where each component placed in an index that is equal to its ID
//        for (int i=0; i<n;i++){            
        for (Component s : componentsMap.values()) {
//            Integer rComponentId = dsmIdxComponentIdMap.get(i);
//            Component s = componentsMap.get(rComponentId);
//            if ("resource".equals(s.getType()))

            if (sysPackageName.equals(s.getPackageName()) || "provider".equals(s.getType())) {
                continue;
            }
            for (Component r : componentsMap.values()) {
//                Component r = orderedComps[j];
                if ("resource".equals(r.getType())) {
                    //check if Component R can access resource C
                    //R can access C if R has at least one of the required permissions by C
                    List<String> appUsesPermissions = apps.get(s.getPackageName()).getAppUsesPermissions(); //the R's app has those permissions
                    for (String prm : r.getRequiredPermissions()) {
                        if (appUsesPermissions.contains(prm)) {
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1;
                            opDependenciesCnt++;
                            break;
                        }
                    }
                    //The default for opDSM[i][j] is 0, so no need to add any code here
                } else {
                    //check if Component S can interact with Component R

                    //components in the same app can communicate with one another
                    if (s.getPackageName().equals(r.getPackageName())) {
//                        if(opDSM[s.getDsmIdx()][r.getDsmIdx()] == 0){
                        if (r.getIntentFilters().isEmpty()){
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1; //only explicit
                        }else{
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 3; //explicit and implicit
                        }
//                        interAppInstance(interAppVar.OP_DOMAIN1, s.getComponentId(), r.getComponentId());
                        opDependenciesCnt++;
                        opCompsDependencies++;
//                        }
                    } else if ("F".equals(r.getExported())) {
                        //component s cannot communicate with a private component
                        opDSM[s.getDsmIdx()][r.getDsmIdx()] = 0;
                    } else if ("receiver".equals(r.getType()) && r.getFullName().contains("$")) {
                        /**
                         * Component S can communicate with a dynamically
                         * registered receiver regardless to the permission
                         * enforced by its app
                         */
                        if (r.getIntentFilters().isEmpty()){
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1;
                        }else{
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 3;
                        }
                        opDependenciesCnt++;
                        opCompsDependencies++;
                        interAppInstance(interAppVar.OP_DOMAIN1_EX, s.getComponentId(), r.getComponentId());
                    } else {
                        List sAppUsesPrms = apps.get(s.getPackageName()).getAppUsesPermissions();
                        if (r.getRequiredPrmToAccess() == null
                                || (sAppUsesPrms != null && (sAppUsesPrms.contains(r.getRequiredPrmToAccess())
                                || sAppUsesPrms.containsAll(apps.get(r.getPackageName()).getAppRequiredPermissions())))) {
                            if ("provider".equals(r.getType())) {
                                //component s can access the content provider if s has the required read and/or write permission                                

                                int accessNumber = 0;
                                //4: can read, 5: can write, 9: can read and write
                                //but since I am not diffrenciating between read and write in the DSM, I will keep it just 5
                                //which means a component can access a content provider

                                //check read privilege
                                if (r.getProviderReadPermission() == null
                                        || (sAppUsesPrms != null && sAppUsesPrms.contains(r.getProviderReadPermission()))) {
//                                    accessNumber = 4;
                                    accessNumber = 5;
                                    interAppInstance(interAppVar.OP_DOMAIN1_CP, s.getComponentId(), r.getComponentId());
                                } else {
//                                    System.out.println("component " + s.getName() + " cannot read filterData from " + r.getName()
//                                            + " due to lack of the required read permission " + r.getProviderReadPermission());

                                }
                                //check write privilege
                                if (accessNumber != 5) {//if we don't care about read or write, this condition is fine, otherwise we need to remove it)
                                    if (r.getProviderWritePermission() == null
                                            || (sAppUsesPrms != null && sAppUsesPrms.contains(r.getProviderWritePermission()))) {
//                                    accessNumber = accessNumber + 5;
                                        accessNumber = 5;
                                        interAppInstance(interAppVar.OP_DOMAIN1_CP, s.getComponentId(), r.getComponentId());
                                    } else {
//                                    System.out.println("component " + s.getName() + " cannot write filterData on " + r.getName()
//                                            + " due to lack of the required write permission " + r.getProviderWritePermission());

                                    }
                                }
                                opDSM[s.getDsmIdx()][r.getDsmIdx()] = accessNumber;
                                //this dependency is not counted in the component dependencies
                                if (accessNumber != 0) {
                                    OP_compsAccessCP++;
//                                    interAppInstance(interAppVar.OP_DOMAIN1, s.getComponentId(), r.getComponentId());                                    
                                }
                            } else {
                                if (r.getIntentFilters().isEmpty()){
                                    opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1;
                                }else{
                                    opDSM[s.getDsmIdx()][r.getDsmIdx()] = 3;
                                }
                                interAppInstance(interAppVar.OP_DOMAIN1_EX, s.getComponentId(), r.getComponentId());
                                opDependenciesCnt++;
                                opCompsDependencies++;
//                        //exported component in a different app
//                        if (r.getRequiredPermissions().isEmpty()) {
////                            if(opDSM[s.getDsmIdx()][r.getDsmIdx()] == 0){
//                                opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1;
//                                opDependenciesCnt++;
//                                opCompsDependencies++;
////                            }
//                        } 
//                        else {
//                            String requiredPrm = r.getRequiredPrmToAccess();
//                            if (appsInAttacks.get(s.getPackageName()).getAppUsesPermissions().contains(requiredPrm)) {
//                                //The app has the permission to communicate with this component
////                                if(opDSM[s.getDsmIdx()][r.getDsmIdx()] == 0){
//                                    opDSM[s.getDsmIdx()][r.getDsmIdx()] = 1;
//                                    opCompsDependencies++;
//                                    opDependenciesCnt++;
////                                }
//                            } else {
//                                opDSM[s.getDsmIdx()][r.getDsmIdx()] = 0;
//                            }
//                        }
                            }

                        } else {
                            opDSM[s.getDsmIdx()][r.getDsmIdx()] = 0;
//                            System.out.println("component " + s.getName() + " cannot communicate with " + r.getName() + " due to lack of required permission " + r.getRequiredPrmToAccess());
                        }

                    }

                }

            }
        }
    }

    private static Set<Integer> doIntentResolution(Intent i) {        
        //returns a set of components' names that can handle this intent
        Set<Integer> s = new HashSet<>();
        for (Entry<Integer, Component> e : componentsMap.entrySet()) {
            List<IntentFilter> filters = ((Component) e.getValue()).getIntentFilters();
            for (IntentFilter f : filters) {
                if (matches(e.getValue().getType(), f, i)) {
                    s.add(e.getKey());
                }
            }
        }
        return s;
    }

    private static boolean matchReceiverType(String compType, String receiverType) {
        //compType: activity, service, provider, receiver
        //receiverType: a, s, p, r
        return compType.substring(0, 1).equalsIgnoreCase(receiverType.substring(0, 1));
    }

    private static boolean matches(String compType, IntentFilter f, Intent i) {
        //more information about how to match IntentFilter and Intent can be found at https://developer.android.com/guide/components/intents-filters.html#Resolution
        //an Intent has action, filterData, and one or more category    
        //check action, filterData, category

        if (i.getReceiverType() != null && compType != null) {
            if (!matchReceiverType(compType, i.getReceiverType())) {
                return false; //this component cannot receive this Intent
            }
        }

        //Action test. For the system intent we check only the action
        if (i.isSysIntent() && f.getActions().contains(i.getAction())) {
            return true;
        }
        //Action test
        if (!f.getActions().contains(i.getAction())) {
            return false;
        }

        //For an intent to pass the category test, each category in the Intent must match a category in the filter. 
        //The reverse is not necessary
        if (i.getCategories() != null && i.getCategories().size() > 0) {
            List<String> filterCats = f.getCategories();
            for (String cat : i.getCategories()) {
                if (!filterCats.contains(cat)) { // a category in the intent is not handled by this component
                    return false;
                }
            }
        }

        //Data test: we check the MimeType, Scheme, host, and port
        if (LPDetermination.PERFORM_DATA_TEST) {
            for (Data filterData : f.getData()) {
                //check each filterData in the filter with the Data attributes in the Intent
                Data intentData = i.getData();
                if (intentData != null) {
                    if (filterData.getMimeType() != null) {
                        if (!filterData.getMimeType().equalsIgnoreCase(intentData.getMimeType())) {
                            return false; //MimeType violation
                        }
                    }
                    if (filterData.getScheme() != null) {
                        if (!filterData.getScheme().equalsIgnoreCase(intentData.getScheme())) {
                            return false;//scheme violation
                        }
                        if (filterData.getHost() != null) {
                            if (!filterData.getHost().equalsIgnoreCase(intentData.getHost())) {
                                return false; //host violation
                            }
                            if (filterData.getPort() != null) {
                                if (!filterData.getPort().equalsIgnoreCase(intentData.getPort())) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        /*if (i.getScheme() != null || i.getDataType() != null) {
         Data d = new Data();
         d.setScheme(i.getScheme());
         d.setMimeType(i.getDataType());
         d.setHost(i.getHost());
         d.setPathPattern(i.getPath());
         d.setPort(i.getPort());
         if (!f.getData().contains(d)) { //Data match in the Data.equals method
         return false;
         }
         }*/
        return true;
    }

//    private static void orderComponents() {
//        orderedComps = new Component[componentsMap.size()];
//        componentsMap.entrySet().stream().forEach((entry) -> {
//            Component r = (Component) entry.getValue();
//            if(orderedComps[r.getComponentId()]!=null){
//            System.out.println("redundant Component: Map:"+entry.getKey()+" Array:"+orderedComps[r.getComponentId()].getName()
//                    +" "+orderedComps[r.getComponentId()].hashCode()+" "+r.hashCode()
//                    +"\nMap: "+r
//                    +"\nArr:"+orderedComps[r.getComponentId()]);            
//        }
//            orderedComps[r.getComponentId()] = r;
//        });
//    }
    private static void printDSM(PrintWriter writer, int[][] matrix, String label, boolean[] emptyColumns, PrintWriter lpFile) {
        writer.write("\n=================== " + label + " ===================\n");
        String codes = "Comp-Comp relationship (1st domain)> EMPTY: No communication, 1: Explicit, 2: Implicit, 3: Explicit & Implicit, 5: comp to CP\n"
                + "Comp-Prm  relationship (2nd domain)> EMPTY: comp does not have this permission, 1: (u; h), 2: (h), 5: (e), "
                + "6: (u; h; e), 7:(h; e) \n\n";
        writer.write(codes);
//        if(lpFile!=null){lpFile.write(codes);}
        int n = matrix.length;
        //print the header
        String header = "Package Name,Type,Exported,"
                + "(IF)/(D)|scheme|mimeType|host|path|port|subType|uri/Path/[actions]/[categories],"
                + "[App required prms],[required prms],[uses prms],[actually uses prms],Provider Read Prm,"
                + "Provider Write Prm,Authority,Name,CompID,ID,";

        writer.write(header);
        if (lpFile != null) {
            lpFile.write(header);
        }
        for (int h = 0; h < n; h++) {
            if (h >= resourceStartIdx) {
                if (emptyColumns[h - resourceStartIdx]) {
                    continue;
                } else {
                    String resourceName = componentsMap.get(dsmIdxComponentIdMap.get(h)).getFullName();
                    writer.write(h + " (" + resourceName + ")" + sep);
                    if (lpFile != null) {
                        lpFile.write(" (" + resourceName + ")" + sep);
                    }

                }
            } else {
                writer.write(h + sep);
                if (lpFile != null) {
                    lpFile.write(h + sep);
                }
            }
        }
        writer.write("\n");
        if (lpFile != null) {
            lpFile.write("\n");
        }
        for (int i = 0; i < n; i++) {
            if (i >= resourceStartIdx) {
                break;  //do not print the third quadrant (resource-comp) and thr fourth quadrant (resource-resource)
            }
            for (int j = 0; j < n; j++) {
                //if this resource column is empty, don't print it
                if (j >= resourceStartIdx) {
                    if (emptyColumns[j - resourceStartIdx]) {
                        continue; //skip this column
                    }
                }

                if (j == 0) {
                    Integer componentId = dsmIdxComponentIdMap.get(i);
                    String compInfo = componentsMap.get(componentId) + sep;
                    writer.write(compInfo);
                    if (lpFile != null) {
                        lpFile.write(compInfo);
                    }
                }
//         if (i==j){
//            System.out.print('x'+sep);
//         } else{
                if (matrix[i][j] == 0) {
                    writer.write("" + sep);
                    if (lpFile != null) {
                        lpFile.write("" + sep);
                    }
                } else {
                    writer.write(matrix[i][j] + sep);
                    if (lpFile != null) {
                        lpFile.write(matrix[i][j] + sep);
                    }
                }
//         }
            }
            writer.write("\n");
            if (lpFile != null) {
                lpFile.write("\n");
            }
        }
        writer.write("\n=================== " + label + " ===================\n");
    }

//        private static void printOpDSM(PrintWriter outputWriter) {
//        outputWriter.write("\n=================== DSM ===================\n");
//        outputWriter.write("\nEMPTY: No communication, 1: Explicit, 2: Implicit\n");
//        int n = opDSM.length;
//        //print the header
//        outputWriter.write("Package Name;Discovered In;Type;Exported;Intent Filter(Data(scheme|mimeType)/Path/[actions][]categories);"
//                + "[App required prms];[required prms];[uses prms];[actually uses prms];Name;ID;");
//        for (int h = 0; h < n; h++) {
//            outputWriter.write(h + sep);
//        }
//        outputWriter.write("\n");
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                if (j == 0) {
//                    outputWriter.write(orderedComps[i] + sep);
//                }
////         if (i==j){
////            System.out.print('x'+sep);
////         } else{
//                if (opDSM[i][j] == 0) {
//                    outputWriter.write("" + sep);
//                } else {
//                    outputWriter.write(opDSM[i][j] + sep);
//                }
////         }
//            }
//            outputWriter.write("\n");
//        }
//        outputWriter.write("\n=================== DSM ===================\n");
//    }
    public static void compToResourceDSM() {
        String resourceName = null;
        try {
            for (Component c : componentsMap.values()) {
                if (!c.getType().equals("resource")) {

                    //code 1: use and has permision
                    List<String> u = c.getActuallyUsedPermissions();
                    if (u != null && u.size() > 0) {
                        for (String prm : u) {
                            resourceName = PrmResourceMap.get(prm);
                            utilizedPermissions.add(resourceName);
                            //add a communication between sender(r.getName) and receiver (resourceName)
                            int senderId = c.getDsmIdx();
                            Integer receiverComponentId = getComponentKeybyName(resourceName);
//                            int receiverId = componentsMap.get(resourceName).getComponentId();
                            int receiverId = componentsMap.get(receiverComponentId).getDsmIdx();
                            if (dsm[senderId][receiverId] == 0) {
                                lpDependenciesCnt++;
                            }
                            if (dsm[senderId][receiverId] != 1) {
                                dsm[senderId][receiverId] += 1; //use and has
//                                System.out.println("Sender:"+c.getName()+" permission:"+resourceName+" "+receiverId);
                            }

                        }
                    }
                    //code 5: enforced permission
                    List<String> r = c.getRequiredPermissions(); //those are the enforced permissions
                    if (r != null && r.size() > 0) {
                        for (String p : r) {
                            resourceName = PrmResourceMap.get(p);
                            if (resourceName == null) {
                                continue;
                            }
                            //add a communication between sender(r.getName) and receiver (resourceName)                            
                            int senderId = c.getDsmIdx();
                            Integer receiverComponentId = getComponentKeybyName(resourceName);
//                            int receiverId = componentsMap.get(resourceName).getComponentId();
                            int receiverId = componentsMap.get(receiverComponentId).getDsmIdx();
                            if (dsm[senderId][receiverId] == 0) {
                                lpDependenciesCnt++;
                            }
                            if (dsm[senderId][receiverId] < 5) {
                                dsm[senderId][receiverId] += 5;
                            } //enforced permission
                        }
                    }
                }
            }

            //code 2: has permission. If a component C1 communicates with another component R that uses permission P and C1's app has that pemrission, then C1 has P permission.
            for (int i = 0; i < dsm.length; i++) {
                for (int j = 0; j < dsm.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    if (dsm[i][j] != 0) {
                        Component sender = componentsMap.get(dsmIdxComponentIdMap.get(i));
                        if (sysPackageName.equals(sender.getPackageName())) //system is the sender
                        {
                            continue;
                        }
                        Component receiver = componentsMap.get(dsmIdxComponentIdMap.get(j));
                        if (sysPackageName.equals(receiver.getPackageName())) //system is the receiver
                        {
                            continue;
                        }
                        //sender is a component and the receiver is a component
                        List<String> l = receiver.getActuallyUsedPermissions();
                        if (!l.isEmpty()) {
                            for (String p : l) {
                                resourceName = PrmResourceMap.get(p);
                                //there is a legitimate reason for sender to have permission p
                                if (apps.get(sender.getPackageName()) != null
                                        && apps.get(sender.getPackageName()).getAppUsesPermissions().contains(p)) {
                                    int senderId = sender.getDsmIdx();
                                    Integer receiverComponentId = getComponentKeybyName(resourceName);
                                    int receiverId = componentsMap.get(receiverComponentId).getDsmIdx();
                                    if (dsm[senderId][receiverId] == 0) {
                                        lpDependenciesCnt++;
                                    }
                                    if (dsm[senderId][receiverId] == 0 || dsm[senderId][receiverId] == 5) {
                                        dsm[senderId][receiverId] += 2; //has
                                    }
                                }

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("component: " + resourceName);
            e.printStackTrace();
        }
        
    }

    private static void addMissingCompsToComps() {
        Integer key = null;
        Integer cnt = 0;
        for (Component m : missingComps) {
//            if(!componentsMap.containsKey(m.getName())){
            key = LPDetermination.getComponentKeybyName(m);
            if (key != null) {
//            Component r = getCompFromMap(m.getName());
//            if (r==null){
//                System.out.println("Missing component " + m.getName());
                m.setComponentId(componentsMap.size());
                componentsMap.put(m.getComponentId(), m);
                for (IntentFilter i : m.getIntentFilters()) {
                    for (String action : i.getActions()) {
                        if (LPDetermination.actionCompCntMap.containsKey(action)) {
                            cnt = LPDetermination.actionCompCntMap.get(action);
                        }
                        LPDetermination.actionCompCntMap.put(action, ++cnt);
                    }
                }
            } else {
//                System.out.println("FOUND Missing component " + m.getName());
            }
        };
    }

    private static Component getCompFromMap(String P_compName) {
        Component c = null;
        String eCompName = null;
        String pkg = null;
        for (Entry e : componentsMap.entrySet()) {
            c = (Component) e.getValue();
            pkg = c.getPackageName();
            eCompName = c.getFullName();
            eCompName = eCompName.replace(pkg, "");
            if (eCompName.startsWith(".")) {
                eCompName = eCompName.substring(1);
            }

            P_compName = P_compName.replace(pkg, "");
            if (P_compName.startsWith(".")) {
                P_compName = P_compName.substring(1);
            }

            if (P_compName.equals(eCompName)) {
                return c;
            }
        }
        return c;
    }

    private static void addComponentsToApps() {
        componentsMap.values().stream().forEach((c) -> {
            apps.get(c.getPackageName()).getComponents().add(c);
        });

    }

    static Set<String> getServicesAccessedByResource(String resource) {
        //EFFECTS: takes a permission group name (called here resource) and 
        //         returns the service name that can be obtained using this permission
        if (resourceSysServiceMap.containsKey(resource)) {
            return resourceSysServiceMap.get(resource);
        }
        return null;
    }

    static boolean isResourcePrmsInAppPrms(List<String> appUsesPermissions, List<String> resourcePermissions) {
        //EFFECTS: check if any permission in the resourcePermissions list, permissions belong to a permission-group, 
        //          is in the appUsesPermissions list, list of permissions that an app requists
        if (appUsesPermissions == null || resourcePermissions == null) {
            return false;
        }
        for (String p : resourcePermissions) {
            if (appUsesPermissions.contains(p)) {
                return true;
            }
        }
        return false;
    }

    
    private static void ecaRulesPreventApps(int[][] dsm){
        boolean appDependencyFound = false; //True: if any component from app1 communicate with any component from app2, false, otherwise
        int dependency = -1;
        for (Application sApp : apps.values()) {
            if (sysPackageName.equals(sApp.getPackageName())){
                continue;
            }
            appDependencyFound=false;
            for (Application rApp : apps.values()){
                if (sysPackageName.equals(rApp.getPackageName()) || sApp.equals(rApp)){
                    continue;
                }
                for (Component sComp : sApp.getComponents())   {
                    for (Component rComp : rApp.getComponents()){
                        dependency=dsm[sComp.getDsmIdx()][rComp.getDsmIdx()];
                        if (dsm[sComp.getDsmIdx()][rComp.getDsmIdx()] != 0){
                            appDependencyFound=true;
                            break;
                        }
                    }
                    if(appDependencyFound){
                            break;
                    }
                }
                if (appDependencyFound==false){ //there is no communication between sApp and rApp                    
                    ECArule rule = new ECArule(sApp.getPackageName(), null, rApp.getPackageName(), null, null, EcaRuleAction.PREVENT);
                    ECArules.add(rule);
                }
            }
            
        }
    }
            
    
        private static void efficientlyGenerateECARules(int[][] dsm) {
            
        addComponentsToApps();
        Set<ECArule> sRules = new HashSet<>();
        componentsMap.values().stream().forEach((s) -> {
            int sIdx = s.getDsmIdx();            

            if (sysPackageName.equals(s.getPackageName())) {//system is the sender
                //do nothing
            } else {
                for (Application app : apps.values()) {
                    boolean dependencyFound = false;
                    boolean sysApp = false;
                    sRules.clear();
                    for (Component r : app.getComponents()) {
                        int rIdx = r.getDsmIdx();
//                        if (dsm[sIdx][rIdx] == 0) {
                            if (sysPackageName.equals(app.getPackageName())) {
                                
                                //the receiver is a system component
                                //create a Resource-ECA rule
                                sysApp = true;
                                
                                if (dsm[sIdx][rIdx] != 0) {
                                    lppermissionGrantedDomainCnt++;
                                }
                                
                                Set<String> l = getServicesAccessedByResource(r.getFullName());
                                if (l != null){
                                    contrainedContextServices.addAll(l);
                                if (dsm[sIdx][rIdx] != 0) {
                                //check if the s's app has a permission from the r's required permission
                                boolean appHasResourcePrm = isResourcePrmsInAppPrms(apps.get(s.getPackageName()).getAppUsesPermissions(), r.getRequiredPermissions());
                                if (appHasResourcePrm) {
                                    for (String service : l) {
                                        ECAServiceRule rule = new ECAServiceRule(s.getFullName(), service, EcaRuleAction.ALLOW );
                                        ECAServiceRules.add(rule);
                                    }
                                }
                                }
                                }
                                if (l==null && dsm[sIdx][rIdx] != 0){
                                    ECAServiceRule rule = new ECAServiceRule(s.getFullName(), r.getFullName(),EcaRuleAction.ALLOW);
                                    ECAServiceRules.add(rule);
//                                    System.out.println("No resources are protected with this permission "+r.getName());
                                }
                            } 
                            else {
                                if (dsm[sIdx][rIdx] != 0) {
                                    ECArule rule = new ECArule(s.getPackageName(), s.getFullName(), app.getPackageName(), r.getFullName(), null, EcaRuleAction.ALLOW);                                    
                                    ECArules.add(rule);
                                    dependencyFound=true;
                                }else{
                                    dependencyFound=false; //component s does not communicate with component app.r
                                }
                        } 
                    }
                    
                    if (dependencyFound==false){ //component s does not communicate with ALL components in the application app
//                        ECArule rule = new ECArule(s.getPackageName(), s.getName(), app.getPackageName(), null, null, EcaRuleAction.PREVENT);
//                        ECArules.add(rule);

                    }
                }
            }
        });
    }
        
    private static void generateECARules(int[][] dsm) {
        addComponentsToApps();
        Set<ECArule> sRules = new HashSet<>();
        componentsMap.values().stream().forEach((s) -> {
            int sIdx = s.getDsmIdx();

            if (sysPackageName.equals(s.getPackageName())) {//system is the sender
                //do nothing
            } else {
                for (Application app : apps.values()) {
                    boolean dependencyFound = false;
                    boolean sysApp = false;
                    sRules.clear();
                    for (Component r : app.getComponents()) {
                        int rIdx = r.getDsmIdx();
                        if (dsm[sIdx][rIdx] == 0) {
                            if (sysPackageName.equals(app.getPackageName())) {
                                //the receiver is a system component
                                //create a Resource-ECA rule
                                sysApp = true;
                                Set<String> l = getServicesAccessedByResource(r.getFullName());                                
                                //check if the s's app has a permission from the r's required permission
                                boolean appHasResourcePrm = isResourcePrmsInAppPrms(apps.get(s.getPackageName()).getAppUsesPermissions(), r.getRequiredPermissions());
                                if (appHasResourcePrm && l != null) {
                                    contrainedContextServices.addAll(l);
                                    for (String service : l) {
                                        ECAServiceRule rule = new ECAServiceRule(s.getFullName(), service,EcaRuleAction.PREVENT);
                                        ECAServiceRules.add(rule);
                                    }
                                }
                            } 
                            else {
                                sysApp = false;
                                ECArule rule = new ECArule(s.getPackageName(), s.getFullName(), app.getPackageName(), r.getFullName(), null, EcaRuleAction.PREVENT);
                                sRules.add(rule);
                                //add ECA rule with sender and action if there is only one component that can handle that action
                                for (IntentFilter f : r.getIntentFilters()) {
                                    for (String action : f.getActions()) {
                                        Integer cnt = actionCompCntMap.get(action);
                                        if (!android.content.Intent.ACTION_MAIN.equals(action)) {
                                            if (cnt != null && (cnt == 1 || !"activity".equals(r.getType()))) {
                                                rule = new ECArule(s.getPackageName(), s.getFullName(), null, null, action, EcaRuleAction.PREVENT);

//                                            boolean b = sRules.add(rule);
//                                            System.out.println(b+"( action "+action+" "+rule);
                                                sRules.add(rule);
//                                                ECArules.add(rule);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            dependencyFound = true;
                            //System.out.println("Dependency found DSM[" + sIdx + "][" + rIdx + "]=" + dsm[sIdx][rIdx]);
                        }
                    }
                    if (!sysApp) {
                        if (dependencyFound) {
                            ECArules.addAll(sRules);
                            sRules.clear();
                            dependencyFound = false;
                        } else {//component S doesn't depend on app
                            ECArule rule = new ECArule(s.getPackageName(), s.getFullName(), app.getPackageName(), null, null, EcaRuleAction.PREVENT);
                            ECArules.add(rule);
                        }
                    }
                }
            }
        });
    }

    private static void printIccAttacks(PrintWriter iccFile, Set<String> op_apps, Set<String> lp_apps) {
        iccFile.write("\n=================== LP ICC Attacks Apps ===================\n");
        for (String app : lp_apps){
            iccFile.write(app +"\n");            
        }
        iccFile.write("\n=================== OP ICC Attacks Apps ===================\n");
        for (String app : op_apps){
            iccFile.write(app+"\n");            
        }
        iccFile.write("\n=================== LP ICC Attacks ===================\n");
        iccFile.write("\n=================== Unauthorized Intent Receipt ===================\n");
        for (UnauthorizedIntentReceipt u : lp_uir){
            iccFile.write(u.toString());            
        }
        iccFile.write("\n=================== Intent Spoofing ===================\n");
        for (IntentSpoofing i : lp_is){
            iccFile.write(i.toString());
        }
        
        iccFile.write("\n=================== OP ICC Attacks ===================\n");
        iccFile.write("\n=================== Unauthorized Intent Receipt ===================\n");
        for (UnauthorizedIntentReceipt u : op_uir){
            iccFile.write(u.toString());            
        }
        iccFile.write("\n=================== Intent Spoofing ===================\n");
        for (IntentSpoofing i : op_is){
            iccFile.write(i.toString());
        }
        
    }        

    
    private static void printContrainedResources(PrintWriter resourcesFile) {
        for (String service : contrainedContextServices){
            resourcesFile.write(service+"\n");
        }
    }
    private static void printECARules(PrintWriter writer, PrintWriter rulesFile) {
        writer.write("\n=================== ICC-ECA rules ===================\n");
        writer.write("senderPkg;senderClass;receiverPkg;receiverClass;action\n");
        if (rulesFile != null) {
            rulesFile.write("#senderPkg;senderClass;receiverPkg;receiverClass;action\n");
        }
        for (ECArule rule : ECArules) {
//            writer.write(rule.toString() + "\n");
            if (rulesFile != null) {
                rulesFile.write(rule.toString() + "\n");
            }
        }
        writer.write("\n=================== ICC-ECA rules ===================\n");
        writer.write("\n=================== Resource-ECA rules ===================\n");
        writer.write("#requester;service\n");
//        outputWriter.write(resourceSysServiceMap.toString());
        for (ECAServiceRule rule : ECAServiceRules) {
//            writer.write(rule.toString() + "\n");
            if (rulesFile != null) {
                rulesFile.write(rule.toString() + "\n");
            }
        }
        writer.write("\n=================== Resource-ECA rules ===================\n");
    }

    public static Integer getComponentKeybyName(Component c) {
        //Search for a component in componentMap that has the same name, if found, returns the key
        for (Entry e : componentsMap.entrySet()) {
            if (((Component) e.getValue()).nameEquals(c)) {
                return (Integer) e.getKey();
            }
        }
        return null;
    }

    public static Integer getComponentKeybyName(String componentFullName) {
        //Search for a component in componentMap that has the same name, if found, returns the key
        for (Entry e : componentsMap.entrySet()) {
            Component c = (Component) e.getValue();
            if (c.getFullName().equalsIgnoreCase(componentFullName)) {
                return c.getComponentId();
            }
            String cFullName = c.getPackageName() + "." + c.getFullName().replace(c.getPackageName() + ".", "");
            if (cFullName.equalsIgnoreCase(componentFullName)) {
                return (Integer) e.getKey();
            }
        }
        return null;
    }

    private static void printPrivEscalationInstance(Set<PrivEscalationInstance> instances, String label, PrintWriter writer) {
        writer.write("\n=================== START:" + label + " ===================\n");
        if (instances == null || instances.isEmpty()) {
            writer.write("This Android system is not vulnerable to privilage escalation attack.");
            writer.write("\n=================== END:" + label + " ===================\n");
            return;
        }
        for (PrivEscalationInstance i : instances) {
            //check if this is an inter-app attack
//            Integer senderComponentId = dsmIdxComponentIdMap.get(i.getMalCompDsmIdx());
//            Integer receiverComponentId = dsmIdxComponentIdMap.get(i.getVulCompDsmIdx());
//            boolean interAppPrivEsc = interAppInstance(interApp, senderComponentId, receiverComponentId);
            if (i.isIac() ) { //print only the Inter-App privilage escalation
                writer.write(i.toString());
//                writer.write("Component [" + i.getMalCompDsmIdx() + "] "
//                        + componentsMap.get(senderComponentId).getName()
//                        + " --> [" + i.getVulCompDsmIdx() + "] "
//                        + componentsMap.get(receiverComponentId).getName()
//                        + " ON permission [" + i.getResourceDsmIdx() + "] "
//                        + componentsMap.get(dsmIdxComponentIdMap.get(i.getResourceDsmIdx())).getName()
//                        + "\n");
            }
        }
        writer.write("\n=================== END:" + label + " ===================\n");
    }
    
            public static void readAuthorityPermissionsMap() {
            
        try {
            Path cpAuthorityPermissionsFilePath = Paths.get(cpAuthorityPermissionsFile);
            Files.lines(cpAuthorityPermissionsFilePath).forEach((line) -> {
                if (! line.startsWith("#")){
                String[] arr = line.split(",");
                if (arr != null && arr.length >1) {
                    String authority = arr[0];
                    Set<String> permissions = new HashSet<>();
                    for (int i=1; i<arr.length;i++ ){
                        
                        permissions.add(arr[i].replace("android.permission.", ""));
                    }
                    authorityPermissionsMap.put(authority, permissions);
                }                
                arr = null;
            }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("authorityPermissionsMap: "+authorityPermissionsMap);
    }
    public static Set<String> protectedIntentActions(){
        Set<String> protectedActions = new HashSet<>();
        for (Intent i : intents){
            if (i.isSysIntent()){
                protectedActions.add(i.getAction());                
            }
        }
        return protectedActions;
    }

}
