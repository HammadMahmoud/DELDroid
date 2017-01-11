/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;
import db.DataManager;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.StandardOpenOption;
import utils.WebServicesUtils;
import static utils.WebServicesUtils.ArchExtractor_FILES_PATH;
import static utils.WebServicesUtils.startProcess;
import static utils.WebServicesUtils.DELDROID_SCRIPTS_PATH;
/**
 *
 * @author Mahmoud Hammad
 */
public class DELDroid {
    public static void main(String[] args){
//        for (int i=76;i<=90;i++){
//        if (i!=76 && i!=85 && i!=87){
            LPDetermination.BUNDLE_NO = 84;
            //Runs the static analysis tools
            runStaticTools();        

            //Generates the LP architecture and analyzes it
//            LPDetermination.main(args);
//
            //Enforces the LP architecture
    //        LPEnforcement.pushRules(); 
//        }
//        }
    }
    
    private static void moveAppsArchs() {
        try{
        File dir = new File("/Volumes/Android/lpdroid_models/models/inf221_dataset/arch/");
        File toDir = new File(ArchExtractor_FILES_PATH);
        
        File[] files = dir.listFiles();
        for(int i = 0; i < files.length; i++) {
            if (files[i].getAbsolutePath().endsWith(".xml")){
                //moves the files 
                File afile =new File(files[i].getAbsolutePath());
                File bfile = new File(toDir+File.separator+files[i].getName());
                boolean b = afile.renameTo(bfile);
                
                System.out.println(b+" afile:"+afile.getAbsolutePath()+" bfile:"+bfile.getAbsolutePath());
            }
         }  
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    private static void runStaticTools(){
        try{
        Path path = Paths.get(WebServicesUtils.PATHS_FILE);
        Files.write(path, (getPathsFileString()).getBytes(), StandardOpenOption.APPEND);
        
        DataManager.deleteBundle(LPDetermination.BUNDLE_NO, true);                

        startProcess("python",DELDROID_SCRIPTS_PATH+"modelExtractor.py");   
        System.out.println("ArchExtractor has extracted the models.");
        
        moveAppsArchs();
        
//        startProcess("python",DELDROID_SCRIPTS_PATH+"runDare.py");   
//        System.out.println("Dare has retargeted the apps.");
//        
//        startProcess("python",DELDROID_SCRIPTS_PATH+"runIC3.py");   
//        System.out.println("IC3 has extracted the models. "+DELDROID_SCRIPTS_PATH+"runIC3.py");
//        
//        DataManager.createBundle(LPDetermination.BUNDLE_NO);        

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    private static String getPathsFileString(){
        String str = "COVERT_PATH=/Users/Mahmoud/Documents/eclipseWorkspace/covert/runCovert.sh\n"+
        "IC3_DIR=/Volumes/Android/lpdroid_models/scripts/ic3-0.2.0/\n"+
        "APK_REPO=/Volumes/Android/lpdroid_models/inf221_dataset/S"+LPDetermination.BUNDLE_NO+"/apks/\n"+
        "MODEL_REPO=/Volumes/Android/lpdroid_models/models/inf221_dataset/\n"+
        "DATA_STORAGE=/Volumes/Android/lpdroid_models/logs/inf221_dataset/\n";
        
        return str;

    }
    
}
