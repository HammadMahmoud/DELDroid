/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Mahmoud
 */
public class WebServicesUtils {

    public static final String ANDROID_HOME = "/Users/Mahmoud/Tools/android-sdk-macosx/";

    public static final String UNKNOW_PACKAGE = "UnknownPkg";

    //DELDroid-related paths
//    public static final String DELDROID_ROOT = "/Users/Mahmoud/Documents/PhD_projects/DELDroid/eval_10bundles_30apps_33times/";
    public static final String DELDROID_ROOT = "/Users/Mahmoud/Documents/PhD_projects/DELDroid/deldroid_root/";
//    public static final String DELDROID_ROOT = "/Volumes/Android/lpdroid_models/";
    public static final String LP_RULES_PATH = DELDROID_ROOT + "DELDroid_output/";
    public static final String DSM_FILES_PATH = LP_RULES_PATH;
    public static final String EXPERIMENT_RESULTS_FILES_PATH = DELDROID_ROOT + "results.csv";
    public static final String ArchExtractor_FILES_PATH = DELDROID_ROOT+"all_arch_models/";
//    public static final String ArchExtractor_FILES_PATH = DELDROID_ROOT + "jss_models/arch/";
    public static final String VISUALIZATION_FILES_PATH = DELDROID_ROOT + "DELDroid_output/visualization/";

    public static final String DELDROID_SCRIPTS_PATH = DELDROID_ROOT + "scripts/";
    public static final String PATHS_FILE = DELDROID_SCRIPTS_PATH + "paths.txt";
    public static final String resourceSysServiceFile = DELDROID_ROOT + "services/permission_SysServiceMap.txt";
    public static final String cpAuthorityPermissionsFile = DELDROID_ROOT + "services/cpAccessPermission.txt";
    public static final String ANDROID_FRAMEWORK_MANIFEST_PATH = DELDROID_ROOT+"services/Framework_AndroidManifest.xml";

    public static void initalize() {

    }

    public static void startProcess(String... command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        Process p;
        try {
            p = pb.start();
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toXML(String fileName, Object obj) {
        try{
        File file = new File(VISUALIZATION_FILES_PATH, fileName + ".xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(obj, file);
        }
        catch (javax.xml.bind.JAXBException ex){
            ex.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
