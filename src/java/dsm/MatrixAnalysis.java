/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import static dsm.LPDetermination.componentsMap;
import static dsm.LPDetermination.dsmIdxComponentIdMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import model.Application;
import model.Component;
import model.IntentFilter;

/**
 *
 * @author Mahmoud
 */
public class MatrixAnalysis {

    public static void main(String[] args) {
        int[][] dsm = {{0, 1, 0, 0, 0},
        {0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0}};
//        int [][] dsm = {{1,2,3},{4,5,6},{7,8,9}};
//        printArray(getRow(dsm, 2, 0, 1));
//        Set<PrivEscalationInstance>  s = securityAnalysis(dsm, 3);
//        for (PrivEscalationInstance p : s){
//            System.out.println(p.getSenderDsmIdx()+" "+p.getReceiverDsmIdx()+" "+p.getResourceDsmIdx());
//        }
//        emptyColumns(dsm, 2);
        printArray(getRow(dsm, 0, 0, dsm.length - 1));
        legitimateHas(dsm, 3);
        printArray(getRow(dsm, 0, 0, dsm.length - 1));

    }

    public static void legitimateHas(int[][] dsm, int resourceIdx) {
        for (int col = resourceIdx; col < dsm.length; col++) {//look at the used permissions
            for (int row = 0; row < resourceIdx; row++) { //find a ocmponent that actually uses it
                if (col == row) {
                    continue;
                }
                if (checkUse(dsm[row][col])) {//component row uses permission col
                    System.out.println("component " + row + " uses permission " + col);
                    //find the components that communicate with component row
                    for (int caller = 0; caller < resourceIdx; caller++) {
                        if (row != caller && dsm[caller][row] != 0) {//there is a communication between comp r and comp row
                            System.out.println("Component ID: " + caller + " communicates with component ID: " + row);
                            if (!checkUse(dsm[caller][col])) {
                                dsm[caller][col] = 2;
                                System.out.println("dsm[" + caller + "][" + col + "]=2");
                            }
                        }
                    }

                }
            }
        }
    }

    public static boolean[] emptyColumns(int[][] dsm, int resourceIdx) {
        boolean[] emptyColumns = new boolean[dsm.length - resourceIdx];
        for (int col = resourceIdx; col < dsm.length; col++) {
            boolean emptyCol = true;
            for (int row = 0; row < resourceIdx; row++) {
                if (dsm[row][col] != 0) {
                    emptyCol = false;
                    break; //this column contains relationships
                }
            }
            if (emptyCol) {
                emptyColumns[col - resourceIdx] = true;
            }
        }
        return emptyColumns;
    }

    public static Set<PrivEscalationInstance> securityAnalysis(int[][] dsm, int resourceIdx,
            Set<UnauthorizedIntentReceipt> uir, Set<IntentSpoofing> is) {
        //Privilege escalation analysis
        //unauthorized Intent receipt
        //Intent spoofing

//        System.out.println("resourceIdx: "+resourceIdx);
        Set<PrivEscalationInstance> instances = new HashSet<>();
        boolean instanceAdded = false;
        PrivEscalationInstance instance = null;
        for (int x = 0; x < resourceIdx - 1; x++) { //note that component (resourceIdx-1) is the SystemService component
            for (int y = 0; y < resourceIdx - 1; y++) {
                instanceAdded = false;
                if (x != y) {
                    if (dsm[x][y] != 0) { //exists a communication between x and y components
                        int[] xPrms = getRow(dsm, x, resourceIdx, dsm.length - 1); //permissions for component x
                        int[] yPrms = getRow(dsm, y, resourceIdx, dsm.length - 1); //permissions for component y

                        for (int i = 0; i < xPrms.length; i++) {
                            if (checkUse(yPrms[i]) && //component x uses permission i
                                    !checkEnforce(yPrms[i]) && //component x does not enforce permission p
                                    (!checkHas(xPrms[i])
                                    && !checkUse(xPrms[i])) //component y does not have permission p
                                    ) {
                                //privilage escalation
                                instance = new PrivEscalationInstance(x, y, (i + resourceIdx));
                                instances.add(instance);
                                instanceAdded = true;
//                            System.out.println("Component "+x+" is vulnerable to privilege escalation attack by component "+y+" that can have access to permission "+(i+resourceIdx));
                            }
                        }
                        //check unauthorized Intent receipt and Intent spoofing
                        if (!instanceAdded) {
//                        if (dsm[x][y]==2 || dsm[x][y]==3) { //implicit communication only
                            if (dsm[x][y] != 0) {
                                iccAttackAnalysis(x, y, dsm, uir, is);
                            }
                        }

                    }
                }
            }
        }
        return instances;
    }

    private static void iccAttackAnalysis(int senderDsmIdx, int receiverDsmIdx, int[][] dsm,
            Set<UnauthorizedIntentReceipt> uir, Set<IntentSpoofing> is) {

        Integer senderComponentId = dsmIdxComponentIdMap.get(senderDsmIdx);
        Integer receiverComponentId = dsmIdxComponentIdMap.get(receiverDsmIdx);
        if (dsm[senderDsmIdx][receiverDsmIdx] == 0 || dsm[senderDsmIdx][receiverDsmIdx] == 5) {
            return;
        }
        Component senderComponent = componentsMap.get(senderComponentId);
        Component receiverComponent = componentsMap.get(receiverComponentId);

        if (!senderComponent.getPackageName().equals(receiverComponent.getPackageName())) { //IAC communication

            //check if Intent spoofing
            Application receiverApp = LPDetermination.apps.get(receiverComponent.getPackageName());
            for (Component x : receiverApp.getComponents()) {
                int comm = dsm[x.getDsmIdx()][receiverDsmIdx];
                if (receiverDsmIdx != x.getDsmIdx()) {
                    if ((senderComponent.getType().equals(x.getType())) && (comm == 2 || comm == 3)) {
                        //                    LPDetermination.lp_intentSpoofing++;
                        IntentSpoofing intentSpoofing = new IntentSpoofing(senderComponent, receiverComponent, x);
                        is.add(intentSpoofing);
                        return;
                    }
                }
            }

            if (dsm[senderDsmIdx][receiverDsmIdx] == 2 || dsm[senderDsmIdx][receiverDsmIdx] == 3) { //Implicit Intent
                if ("activity".equals(receiverComponent.getType())) {
                    ArrayList<IntentFilter> filters = receiverComponent.getIntentFilters();
                    if (filters == null || filters.isEmpty()) {
                        return;
                    }

                    if (onlyMainAction(filters)) {//if this activity declares only the MAIN action, then this component is less likely to be a malicious component
                        return;
                    }
                }

                //Unauthorized Intent receipt: check if there is a communication between x and another component from x's app
                Application senderApp = LPDetermination.apps.get(senderComponent.getPackageName());
                for (Component x : senderApp.getComponents()) {
                    if (senderDsmIdx != x.getDsmIdx()) {
                        int comm = dsm[senderDsmIdx][x.getDsmIdx()];
                        if ((receiverComponent.getType().equals(x.getType())) && (comm == 2 || comm == 3)) {
                            //unauthorized intent receipt
                            //                    System.out.println("There is a communication between "+
                            //                            sender and receiver which they belong to different apps and 
                            //                                    there is a communication between sender and x);
                            //                    LPDetermination.lp_unauthorizedIntentReceipts ++;
                            UnauthorizedIntentReceipt u = new UnauthorizedIntentReceipt(senderComponent, receiverComponent, x);
                            uir.add(u);
                            return;
                        }
                    }
                }
            }
        }
    }

    private static boolean onlyMainAction(List<IntentFilter> filters) {
        for (IntentFilter filter : filters) {
            for (String action : filter.getActions()) {
                if (action != null && !action.isEmpty() && !"android.intent.action.MAIN".equals(action)) {
                    return false;
                }
            }
        }
        return true;

    }

    private static boolean checkUse(int prmCode) {
        return (prmCode == 1 || prmCode == 3 || prmCode == 6 || prmCode == 8);
    }

    private static boolean checkHas(int prmCode) {
        return (prmCode == 2 || prmCode == 7);
    }

    private static boolean checkEnforce(int prmCode) {
        return (prmCode >= 5);
    }

    private static int[] getRow(int[][] matrix, int row, int startCol, int endCol) {
        int[] r = new int[endCol - startCol + 1];
        if (startCol > endCol || matrix.length < row) {
            throw new IllegalStateException("Illegal aruments");
        }
        int idx = 0;
        for (int j = 0; j < (endCol - startCol + 1); j++) {
            r[idx++] = matrix[row][startCol + j];
        }
        return r;
    }

    private static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
}
