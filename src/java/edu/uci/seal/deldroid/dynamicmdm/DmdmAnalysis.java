/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.dynamicmdm;

import edu.uci.seal.deldroid.attack.IntentSpoofing;
import edu.uci.seal.deldroid.attack.UnauthorizedIntentReceipt;
import edu.uci.seal.deldroid.dynamicmdm.AnalysisLookup.DomainType;
import edu.uci.seal.deldroid.lp.LPDetermination;
import static edu.uci.seal.deldroid.lp.LPDetermination.componentsMap;
import static edu.uci.seal.deldroid.lp.LPDetermination.dsmIdxComponentIdMap;
import edu.uci.seal.deldroid.model.Application;
import edu.uci.seal.deldroid.model.Component;
import edu.uci.seal.deldroid.model.IntentFilter;
import edu.uci.seal.deldroid.model.Permission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mahmoud
 */
public class DmdmAnalysis {

    private AnalysisLookup lookup;
    private SystemArchitecture system;

    private final Domain usage;
    private final Domain granted;
    private final Domain enforcement;
    private final Domain explicit;
    private final Domain implicit;

    private final Set<String> protectedActions;

    public DmdmAnalysis(AnalysisLookup lookup, SystemArchitecture system) {
        this.lookup = lookup;
        this.system = system;
        this.protectedActions = LPDetermination.protectedIntentActions();

        this.usage = system.dynamicMdm.get(DomainType.USAGE);
        this.granted = system.dynamicMdm.get(DomainType.GRANTED);
        this.enforcement = system.dynamicMdm.get(DomainType.ENFORCEMENT);
        this.explicit = system.dynamicMdm.get(DomainType.EXPLICIT);
        this.implicit = system.dynamicMdm.get(DomainType.IMPLICIT);
    }

    public void doAnalysis() {
        //do analysis for the whole system. Good for the first time
        for (int row = 0; row < explicit.dsm.length; row++) {
            for (int col = 0; col < explicit.dsm.length; col++) {
                if (row != col && (explicit.dsm[row][col] != 0 || implicit.dsm[row][col] != 0)) {
                    Integer senderComponentId = dsmIdxComponentIdMap.get(row);
                    Integer receiverComponentId = dsmIdxComponentIdMap.get(col);
                    Component senderComponent = componentsMap.get(senderComponentId);
                    Component receiverComponent = componentsMap.get(receiverComponentId);
                    if (!senderComponent.isSystemComponent() && !receiverComponent.isSystemComponent()) {
                        if (!senderComponent.getPackageName().equals(receiverComponent.getPackageName())) {
//                            peAnalysis(row, col);
                            icpAnalysis(senderComponent, receiverComponent);
//                            isAnalysis(senderComponent, receiverComponent);
//                            if (implicit.dsm[row][col] != 0) {
//                                uirAnalysis(senderComponent, receiverComponent);
//                            }                            
                        }
                    }
                }
            }
        }
    }

    public void doIncrementalAnalysis() {

    }

    public void peAnalysis(int senderDsmIdx, int receiverDsmIdx) {
        //Analysis rule: (communicatee(cm; cv) \/ communicatei(cm; cv)) /\ p usedcv /\ p ! grantedcm ^ p ! enforcedcv

        //check used permission
        for (int p = 0; p < usage.dsm[0].length; p++) {
            if (usage.dsm[receiverDsmIdx][p] != 0) { //the receiver component uses permission p
                if (enforcement.dsm[receiverDsmIdx][p] == 0) { //permission p is not enforced by the receiver component
                    if (granted.dsm[senderDsmIdx][p] == 0) { //permission p is not granted to the sender component
                        //privilege escalation
                        System.out.println("Privilege escalation attack: " + senderDsmIdx + " -> " + receiverDsmIdx + " ON " + p);
                    }
                }
            }
        }
    }

    public void uirAnalysis(Component senderComponent, Component receiverComponent) {
        if (senderComponent.getPackageName().equals(receiverComponent.getPackageName())) {
            return;
        }

        try {
            if ("activity".equals(receiverComponent.getType())) {
                ArrayList<IntentFilter> filters = receiverComponent.getIntentFilters();
                if (filters == null || filters.isEmpty()) {
                    return;
                }

                if (onlyMainAction(filters)) {//if this activity declares only the MAIN action, then this component is less likely to be a malicious component
                    return;
                }
            }

            //Analysis rule: communicatei(cv; cm) ^ (appcv != appcm) ^ 9 communicatei(cv; cx) ^ (appcv = appcx )
            int senderDsmIdx = senderComponent.getDsmIdx();
            int receiverDsmIdx = receiverComponent.getDsmIdx();
            if (implicit.dsm[senderDsmIdx][receiverDsmIdx] == 0) {
                return;
            }
            Application senderApp = LPDetermination.apps.get(senderComponent.getPackageName());
            for (Component x : senderApp.getComponents()) {
                if (senderDsmIdx != x.getDsmIdx()) {
                    int comm = implicit.dsm[senderDsmIdx][x.getDsmIdx()];
                    if ((receiverComponent.getType().equals(x.getType())) && comm != 0) {
                            //unauthorized intent receipt
                        //                    System.out.println("There is a communication between "+
                        //                            sender and receiver which they belong to different apps and 
                        //                                    there is a communication between sender and x);
                        //                    LPDetermination.lp_unauthorizedIntentReceipts ++;

                        //check if the malicious component and the potential component have the same intent filter so by sending an implicit Intent from teh vulnerable component, sender, a malicious component can receive it
                        if (matchedFilter(receiverComponent, x)) {
                            UnauthorizedIntentReceipt u = new UnauthorizedIntentReceipt(senderComponent, receiverComponent, x);
                            System.out.println(u);
                        }

                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Info: " + senderComponent + " " + receiverComponent);
            e.printStackTrace();
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

    private boolean matchedFilter(Component mal, Component pot) {
        boolean found = false;
        List<IntentFilter> malFilters = mal.getIntentFilters();
        List<IntentFilter> potFilters = pot.getIntentFilters();

        if (malFilters == null || potFilters == null || malFilters.isEmpty() || potFilters.isEmpty()) {
            return false;
        }
        //compare only the actions
        Set<String> malActions = new HashSet<>();
        for (IntentFilter filter : malFilters) {
            malActions.addAll(filter.getActions());
        }
        for (IntentFilter filter : potFilters) {
            for (String action : filter.getActions()) {
                if (!protectedActions.contains(action) && malActions.contains(action)) {
//                    System.out.println("matched action: "+action);
                    return true;
                }
            }
        }

        return found;

    }

    public void isAnalysis(Component senderComponent, Component receiverComponent) {
        int senderDsmIdx = senderComponent.getDsmIdx();
        int receiverDsmIdx = receiverComponent.getDsmIdx();

        Application receiverApp = LPDetermination.apps.get(receiverComponent.getPackageName());
        for (Component x : receiverApp.getComponents()) {
            if (receiverDsmIdx != x.getDsmIdx()) {
                if (senderComponent.getType().equals(x.getType())) {
                    IntentSpoofing intentSpoofing = new IntentSpoofing(senderComponent, receiverComponent, x);
                    System.out.println(intentSpoofing);
                    return;
                }
            }
        }
    }

    public void icpAnalysis(Component senderComponent, Component receiverComponent) {
        int senderDsmIdx = senderComponent.getDsmIdx();
        int receiverDsmIdx = receiverComponent.getDsmIdx();
        
        for(int p=0; p < enforcement.dsm[0].length; p++){
            if (enforcement.dsm[receiverDsmIdx][p]==1 && granted.dsm[senderDsmIdx][p]==0){ //p is enforced by the receiver and i snot granted to the sender 
                System.out.println("Expected ICP: "+senderDsmIdx+" -> "+receiverDsmIdx+" ["+p+"]");
                int enforcedPrmId = enforcement.columnsID[p];
                Permission enforcedPrm = LPDetermination.permissionsMap.get(enforcedPrmId);
                
                String permissionName = enforcedPrm.getName();
                Set<String> receiverAppDefinedPrms = LPDetermination.apps.get(receiverComponent.getPackageName()).getAppDefinedPermissions();
                for (String prm : receiverAppDefinedPrms){
                    String prmName = prm.split("/")[0];
                    String prmLevel = prm.split("/")[0];
                    if (permissionName.equals(prmName)){
                        System.out.println("Actual ICP: "+senderDsmIdx+" -> "+receiverDsmIdx+" ["+p+"]");
                    }
                }
            }
        }
    }

    public void pdlAnalysis() {

    }

    public void cpAnalysis() {

    }

}
