/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import android.content.Intent;
import static edu.uci.seal.deldroid.lp.LPDetermination.ECAServiceRules;
import static edu.uci.seal.deldroid.lp.LPDetermination.apps;
import static edu.uci.seal.deldroid.lp.LPDetermination.extractSystemInfo;
import static edu.uci.seal.deldroid.lp.LPDetermination.getComponentKeybyName;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.ANDROID_HOME;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.startProcess;
import java.util.stream.Stream;
import edu.uci.seal.deldroid.model.Component;
import edu.uci.seal.deldroid.model.IntentFilter;

/**
 *
 * @author Mahmoud Hammad
 */
public class LPEnforcement {

    //Nexus5: /storage/emulated/0/Download/rules.txt, 00dd7864613ea9d4
    //Emulator: /sdcard/Download/rules.txt, emulator-5554
//    static String PATH_ON_DEVICE = "/sdcard/Download/rules.txt";
    static String PATH_ON_DEVICE = "/storage/08F6-0D03/Download/rules.txt"; //emulator
    static String DEVICE = "emulator-5554";
//    static String PATH_ON_DEVICE = "/storage/emulated/0/Download/rules.txt"; //Nexus 5
//    static String DEVICE = "00dd7864613ea9d4";
    static int FIRST_COMP_IDX = -1; // index of the first component in the dsm 
    static int FIRST_RES_IDX = -1; //index of the first permission
    static int COMP_CNT = 0; //number of components in the system
    static int RES_CNT = 0; //number of resources in the dsm
    static int FIRST_COMP_ROW = 0; // index of the first row that contains component information
    static int SYS_IDX = -1; //inde of the first system component
    static Map<Integer, Component> components;
    static Map<Integer, String> resources;
    static int[][] dsm;
    static List<String> rules;    
    static Map<String, Integer> actionCntMap;
    
    public static void main(String[] args) {
        //read the DSM and generate new rules
        adaptArch();
        //Enforces the new LP architecture
        pushRules();
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static void adaptArch() {
        int length = -1;
        components = new HashMap<>();
        resources = new HashMap<>();
        rules = new ArrayList<>();
        actionCntMap = new HashMap<>();

        String line;
        try (Stream<String> stream = Files.lines(Paths.get(LPDetermination.lpPath))) {
            Iterator<String> iterator = stream.iterator();
            int row = -1;
            while (iterator.hasNext()) {
                row++;
                line = iterator.next();

                if (line.startsWith("Comp-") || line.isEmpty()) {
                    continue;
                }
                String[] arr = line.split(LPDetermination.sep, -1);
                if (line.startsWith("Package Name")) {
                    FIRST_COMP_ROW = row + 1;
                    readHeader(arr);
                    length = arr.length;
                    dsm = new int[COMP_CNT][COMP_CNT + RES_CNT + 1];
                    continue;
                }
                readDsmLine(arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("LENGTH:" + length + " FIRST_COMP_IDX:" + FIRST_COMP_IDX + " FIRST_RES_IDX:" + FIRST_RES_IDX + " COMP_CNT:" + COMP_CNT
                + " RES_CNT:" + RES_CNT + " FIRST_COMP_ROW:" + FIRST_COMP_ROW + " SYS_IDX" + SYS_IDX);
        System.out.println("dsm rows:" + dsm.length + " col:" + dsm[0].length);
//        System.out.println("-----------");
//        for (Component c : components.values()) {
//            System.out.println(c.getDsmIdx() + " " + c.getName()+" "+c.getAppUsesPermissions());
//        }
//        System.out.println(resources);

        generateECArules();
        printRules();
    }
    
    private static void printRules(){
        try{
                PrintWriter rulesFile = new PrintWriter(LPDetermination.rulesPath, "UTF-8");
        for (String r : rules) {
            System.out.println(r);
            rulesFile.write(r+"\n");
        }
        rulesFile.close();
        }catch(Exception e){
        e.printStackTrace();
    }
        
    }

    private static void generateECArules() {
        LPDetermination.init();
        extractSystemInfo();
        LPDetermination.populateResourceSysServiceMap();
//        System.out.println(LPDetermination.componentsMap);

        Component sender;
        Component receiver;
        String eca;
        for (int i = 0; i < dsm.length; i++) {
            for (int j = 0; j < dsm[0].length - 1; j++) {
//                System.out.print(dsm[i][j]+",");
                if (i != j && i != SYS_IDX && j != SYS_IDX && dsm[i][j] == 0) {
//                    if (dsm[i][j] == 0) {
                    sender = components.get(i);
                    if (j < COMP_CNT) {//ICC-ECA rule                            
                        receiver = components.get(j);
                        eca = sender.getPackageName()+LPDetermination.sep+sender.getPackageName() + "." + sender.getFullName() + LPDetermination.sep + 
                              receiver.getPackageName()+LPDetermination.sep+receiver.getPackageName() + "." + receiver.getFullName() + LPDetermination.sep + "";
                        rules.add(eca);
                        Set<String> ifActions = receiver.getIfActions();
                        if(ifActions != null && !ifActions.isEmpty()){
                            for (String a : ifActions){
                                if(!Intent.ACTION_MAIN.equals(a)){
                                if(actionCntMap.get(a) == 1 || !"activity".equals(receiver.getType())){
                                    eca = sender.getPackageName()+LPDetermination.sep+sender.getPackageName() + "." + sender.getFullName() + LPDetermination.sep + 
                              ""+LPDetermination.sep + "" + LPDetermination.sep + a;
                                    rules.add(eca);
                                }
                                }
                            }
                        }
//                            System.out.print(i + "" + j + ",");
                    } else { //Resource_ECA rule
//                            System.out.print(i + "|" + j + ",");
                        Integer rId = getComponentKeybyName(resources.get(j));
                        Component r = LPDetermination.componentsMap.get(rId);
                        //check if the s's app has a permission from the r's required permission
                        boolean appHasResourcePrm = LPDetermination.isResourcePrmsInAppPrms(sender.getAppUsesPermissions(), r.getRequiredPermissions());
                        if (appHasResourcePrm) {
                            Set<String> services = LPDetermination.getServicesAccessedByResource(r.getFullName());
//                               System.out.println("-----> "+sender.getName()+">"+resources.get(j)+" "+l+" "+appHasResourcePrm);
//                                   System.out.println("Prevent component sender:"+s.getName()+" from accessing receiver:"+r.getName()+" system services:"+l);
                            if(services!=null){
                            for (String service : services) {
                                eca = sender.getPackageName() + "." + sender.getFullName() + LPDetermination.sep + service;
                                rules.add(eca);
                            }
                            }
                        }
                        //eca = sender.getPackageName()+"."+sender.getName()+LPDetermination.sep+res;
                    }
//                    } else {
////                        System.out.print(",");
//                    }
                }
//                    else{
////                    System.out.print(",");
//                }

            }
//            System.out.print("\n");
        }
    }

    private static void readHeader(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals("ID")) {
                FIRST_COMP_IDX = i + 1;
                continue;
            }
            if (FIRST_COMP_IDX > 0) {
                if (arr[i].contains(" (")) {
                    if (FIRST_RES_IDX < 0) {
                        FIRST_RES_IDX = i;
                    }
                    String[] r = arr[i].split("\\(");
                    resources.put(i - FIRST_COMP_IDX, r[1].replace(")", ""));
                    RES_CNT++;
                }
            }
        }
        COMP_CNT = FIRST_RES_IDX - FIRST_COMP_IDX;
//        RES_CNT = arr.length - FIRST_RES_IDX - 1;
    }
    private static Set<String> actionsInIntentFilter(String ifiltersStr){
        //ifiltersStr: (IF)/(D)|scheme|mimeType|host|path|port|subType|uri/Path/[actions]/[categories];(IF)...
        Set<String> actions = new HashSet<>();
        String[] iFilters = ifiltersStr.split("(IF)");
        for (int i=0; i<iFilters.length; i++){
            String[] iFilter = iFilters[i].split(IntentFilter.sep);
        if(iFilter.length >= 2){
            String filterActionsStr = iFilter[3].replace("[","").replace("]", "").trim();
            if(filterActionsStr!=null && !filterActionsStr.isEmpty()){
                String[] iAction = filterActionsStr.split(";");
                for (String a : Arrays.asList(iAction)){
                    boolean b =  actions.add(a);
                    if(b){
                    if(actionCntMap.containsKey(a)){
                        actionCntMap.put(a, actionCntMap.get(a)+1);
                            }else{
                        actionCntMap.put(a, 1);
                    }
                    }
                        }
            }
            }
        }
        return actions;
    }
    private static void readDsmLine(String[] arr) {
        //0-(FIRST_COMP_IDX-1): Comp info., FIRST_COMP_IDX-(FIRST_RES_IDX-1): domain 1, FIRST_RES_IDX-end: domain 2
//        System.out.println(arr[FIRST_COMP_IDX - 1] + ")" + arr[0] + " (" + arr[arr.length - 2] + ") " + arr.length);
        Component comp = new Component(null);
        Integer compId = Integer.valueOf(arr[FIRST_COMP_IDX - 1]);
        comp.setComponentId(compId);
        comp.setDsmIdx(compId);
        comp.setPackageName(arr[0]);
        String[] prms = arr[6].replace("[", "").replace("]", "").replace(" ", "").split(";");
        List<String> appUsesPrms = Arrays.asList(prms);
        comp.setAppUsesPermissions(appUsesPrms);
        Set<String> filterActions = actionsInIntentFilter(arr[3]);        
        comp.setIfActions(filterActions);
        
        if (SYS_IDX < 0 && LPDetermination.sysPackageName.equals(comp.getPackageName())) {
            SYS_IDX = compId;
        }
        comp.setType(arr[1]);
        comp.setFullName(arr[FIRST_COMP_IDX - 2]);
//        System.out.println("->"+comp.getName()+" "+filterActions);
        components.put(comp.getDsmIdx(), comp);
        for (int i = 0; i < (COMP_CNT + RES_CNT); i++) {
            dsm[compId][i] = (arr[i + FIRST_COMP_IDX].isEmpty()) ? 0 : Integer.valueOf(arr[i + FIRST_COMP_IDX]);
        }
    }

    public static void pushRules() {
//        System.out.println(ANDROID_HOME + "platform-tools/adb -s " + DEVICE + " push" + " " + LPDetermination.rulesPath + " " + PATH_ON_DEVICE);
//        System.out.println(ANDROID_HOME + "platform-tools/adb shell am startservice -n edu.uci.seal.adaptdroid/.AdaptArchS");

        //push the file
        startProcess(ANDROID_HOME + "platform-tools/adb", "-s", DEVICE, "push", LPDetermination.rulesPath, PATH_ON_DEVICE);
        //start a service to adapt the architecture            
        startProcess(ANDROID_HOME + "platform-tools/adb", "shell", "am", "startservice", "-n", "edu.uci.seal.adaptdroid/.AdaptArchS");
        System.out.println("The LP architecture pushed to the Android device.");
    }
}
