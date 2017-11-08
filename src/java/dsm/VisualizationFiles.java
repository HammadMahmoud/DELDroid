/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import static dsm.LPDetermination.resourceStartIdx;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import model.Application;
import model.Component;
import model.Data;
import model.IntentFilter;
import utils.WebServicesUtils;

/**
 *
 * @author Mahmoud 11/6/2017
 */
public class VisualizationFiles {
    public static int systemNo = LPDetermination.BUNDLE_NO;
    /*
     This class generates the required input files to the DELDroid visualization tool.
     Input files are: MDM.csv, apps.xml, analysisResult.xml
     */

    public static void printApps(Map<String, Application> apps) throws JAXBException {
        for (Map.Entry<String, Application> e : apps.entrySet()) {
            e.getValue().toXml("app-" + e.getKey());
        }

    }

    public static void printAnalysisResult(Set<UnauthorizedIntentReceipt> uirs, Set<IntentSpoofing> iss, Set<PrivEscalationInstance> pes) {
        try {
            AnalysisResult result = new AnalysisResult(pes, iss, uirs);
            result.toXml("analysisResults-"+systemNo);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    public static void printMDM(int[][] mdm, Map<Integer, Integer> dsmIdxComponentIdMap, Map<Integer, Component> componentsMap, int resourceStartIdx) {
        
        try {
            //generate explicit, implicit,granted,used, and enforced domains
            String explicit = "domain-explicit-communication-"+systemNo+".csv";
            String implicit = "domain-implicit-communication-"+systemNo+".csv";
            String granted = "domain-permission-granted-"+systemNo+".csv";
            String usage = "domain-permission-usage-"+systemNo+".csv";
            String enforcement = "domain-permission-enforcement-"+systemNo+".csv";

            PrintWriter explicitDomainWriter = new PrintWriter(WebServicesUtils.VISUALIZATION_FILES_PATH + explicit, "UTF-8");
            PrintWriter implicitDomainWriter = new PrintWriter(WebServicesUtils.VISUALIZATION_FILES_PATH + implicit, "UTF-8");
            PrintWriter grantedDomainWriter = new PrintWriter(WebServicesUtils.VISUALIZATION_FILES_PATH + granted, "UTF-8");
            PrintWriter usageDomainWriter = new PrintWriter(WebServicesUtils.VISUALIZATION_FILES_PATH + usage, "UTF-8");
            PrintWriter enforcementDomainWriter = new PrintWriter(WebServicesUtils.VISUALIZATION_FILES_PATH + enforcement, "UTF-8");

            String sep = ",";
            StringBuilder communicationHeader = new StringBuilder("Package,Component,ID,");
            StringBuilder permissionHeader = new StringBuilder("Package,Component,ID,");
            //header
            for (int i = 0; i < dsmIdxComponentIdMap.size(); i++) {
                if (i < resourceStartIdx) {
                    communicationHeader = communicationHeader.append(i + sep);
                } else {
                    Component comp = componentsMap.get(dsmIdxComponentIdMap.get(i));                    
                    permissionHeader = permissionHeader.append("(" + i + ") " + comp.getName() + sep);
                }
            }
            explicitDomainWriter.write(communicationHeader.toString() + "\n");
            implicitDomainWriter.write(communicationHeader.toString() + "\n");
            grantedDomainWriter.write(permissionHeader.toString() + "\n");
            usageDomainWriter.write(permissionHeader.toString() + "\n");
            enforcementDomainWriter.write(permissionHeader.toString() + "\n");

            //data    
            for (int row = 0; row < resourceStartIdx; row++) {
                for (int col = 0; col < dsmIdxComponentIdMap.size(); col++) {                    
                        if (col == 0) {
                            Component comp = componentsMap.get(dsmIdxComponentIdMap.get(row));                            
                            String compInfo = comp.getPackageName() + sep + comp.getName() + sep + row;
                            explicitDomainWriter.write(compInfo);
                            implicitDomainWriter.write(compInfo);
                            usageDomainWriter.write(compInfo);
                            grantedDomainWriter.write(compInfo);
                            enforcementDomainWriter.write(compInfo);
                        }
                        if (col < resourceStartIdx) {
                        //print communication domains values
                        explicitDomainWriter.write(sep + explicit(mdm[row][col]));
                        implicitDomainWriter.write(sep + implicit(mdm[row][col]));
                        }else{
                        //print permission domain
                        usageDomainWriter.write(sep + usage(mdm[row][col]));
                        grantedDomainWriter.write(sep + granted(mdm[row][col]));
                        enforcementDomainWriter.write(sep + enforcement(mdm[row][col]));
                    } 

                }
                explicitDomainWriter.write("\n");
                implicitDomainWriter.write("\n");
                grantedDomainWriter.write("\n");
                usageDomainWriter.write("\n");
                enforcementDomainWriter.write("\n");

            }

            explicitDomainWriter.close();
            implicitDomainWriter.close();
            grantedDomainWriter.close();
            usageDomainWriter.close();
            enforcementDomainWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isResource(String type) {
        return "resource".equals(type);
    }

    public static int explicit(int value) {
        if (value == 1 || value == 3) {
            return 1;
        }
        return 0;
    }

    public static int implicit(int value) {
        if (value == 2 || value == 3) {
            return 1;
        }
        return 0;
    }

    public static int usage(int value) {
        if (value == 1 || value == 3 || value == 6 || value == 8) {
            return 1;
        }
        return 0;
    }

    public static int granted(int value) {
        if (value == 1 || value == 2 || value == 3 || value == 7 || value == 8) {
            return 1;
        }
        return 0;
    }

    public static int enforcement(int value) {
        if (value >= 5) {
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) {
        createSelfDocumentedXMLs();
    }

    public static void createSelfDocumentedXMLs() {
        try {
            Application app = new Application(1);
            app.setName("app's name");
            app.setPackageName("package.name");
            app.setVersionCode("app's version code");
            app.setVersionName("app's version name");
            app.setAppcategory("app's category");
            List<String> permissions = new ArrayList<>();
            permissions.add("permission1");
            permissions.add("permission2");
            app.setAppUsesPermissions(new ArrayList(permissions));
            permissions.remove(1);
            app.setAppActuallyUsesPermissions(new ArrayList(permissions));
            Set<String> set = new HashSet<>();
            set.add("custom permission defined by this app");
            app.setAppDefinedPermissions(set);
            permissions.clear();
            permissions.add("required permission to access this app");
            app.setAppRequiredPermissions(new ArrayList(permissions));

            Component comp1 = new Component(app.getPackageName());
            comp1.setComponentId(100);
            comp1.setDsmIdx(1);
            comp1.setPackageName("the poackage name of its hosting application");
            comp1.setName("name of this component");
            comp1.setType("type of the component. VALUES: activity, service, receiver, or provider");
            comp1.setExported("indicates if the component is exported or not. VALUES: T or F");
            comp1.setRequiredPrmToAccess("The required permission to access this component, i.e., other components must have this permission to be able to communicate with this component.");
            comp1.setProviderReadPermission("if the component is a ContentProvider, then this is the required pemrission to read data from this ContentProvider.");
            comp1.setProviderAuthority("if this component is a ContentProvider, then this is the URI for this content provider");
            comp1.setProviderWritePermission("if the component is a ContentProvider, then this is the required pemrission to modify the data contained in this ContentProvider.");
            comp1.setRequiredPermissions(new ArrayList<String>() {
                {
                    add("a list of enforced permissions by this component.");
                }
            });
            comp1.setActuallyUsedPermissions(new ArrayList<String>() {
                {
                    add("a list of the actually used permissions by this component");
                }
            });

            IntentFilter filter = new IntentFilter();
            filter.setId(1);
            filter.setActions(new ArrayList<String>() {
                {
                    add("list of actions this Intent Filter can handle.");
                }
            });
            filter.setCategories(new ArrayList<String>() {
                {
                    add("list of Intent categories this Intent Filter can handle.");
                }
            });
            filter.setPathData("data path that should match in the Intent, if specified");

            Data data = new Data();
            data.setScheme("The scheme part of the URI");
            data.setMimeType("the MIME media type");
            data.setHost("The host part of the URI");
            data.setPath("The port part of the URI authority");
            filter.setData(new ArrayList<Data>() {
                {
                    add(data);
                }
            });
            comp1.setIntentFilters(new ArrayList<IntentFilter>() {
                {
                    add(filter);
                }
            });

            app.setComponents(new HashSet<Component>() {
                {
                    add(comp1);
                }
            });

            HashMap<String, Application> apps = new HashMap<>();
            apps.put(app.getPackageName(), app);
            printApps(apps);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

}
