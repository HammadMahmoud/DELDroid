/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import db.DataManager;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import static utils.WebServicesUtils.EXPERIMENT_RESULTS_FILES_PATH;

/**
 *
 * @author Mahmoud
 */
public class RunExperiment {

    private static final int REPETITION = 33;
    private static final int FROM_BUNDLE_NO = 1;
    private static final int TO_BUNDLE_NO = 10;

    public static void main(String[] args) {
        runAllBundles();
    }

    private static void deleteBundleData(int bundleNo) throws Exception{
        throw new Exception("Are you sure you want to delete all the data related to bundle "+bundleNo+" from the MySQL dabase?"
                + "if yes, then, comment this statement :-) ");
//        DataManager.deleteBundle(bundleNo, false);
    }

    private static void runAllBundles() {
        try {
            int finishedBundle=1;
            Files.write(Paths.get(EXPERIMENT_RESULTS_FILES_PATH), LPDetermination.EXP_HEADER.getBytes(), StandardOpenOption.APPEND);
            for (int bundleNo = FROM_BUNDLE_NO; bundleNo <= TO_BUNDLE_NO; bundleNo++) {
                LPDetermination.BUNDLE_NO = bundleNo;
                DataManager.updateAppsBundle(bundleNo);
                for (int i = 0; i < REPETITION; i++) {
                    LPDetermination.main(new String[0]);
                }
                if (REPETITION>1){
                    Files.write(Paths.get(EXPERIMENT_RESULTS_FILES_PATH), ("\nBundle "+finishedBundle).getBytes(), StandardOpenOption.APPEND);
                }
                finishedBundle++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RunExperiment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(RunExperiment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
