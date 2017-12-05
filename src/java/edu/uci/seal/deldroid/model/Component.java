/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.model;

import edu.uci.seal.deldroid.lp.LPDetermination;
import static edu.uci.seal.deldroid.lp.LPDetermination.apps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import static edu.uci.seal.deldroid.utils.WebServicesUtils.UNKNOW_PACKAGE;

/**
 *
 * @author Mahmoud
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"componentId","dsmIdx", "compName","fullName", "packageName","type", "exported", "requiredPrmToAccess", 
                      "providerReadPermission", "providerWritePermission", "providerAuthority",
                      "requiredPermissions", "actuallyUsedPermissions", "intentFilters"})
public class Component implements Comparable{
    @XmlTransient
    private static Set<Integer> ids=new HashSet<>();
    @XmlTransient
    private static int SEQUENCE_ID=1000000;
//    private int id;
    private int componentId;//this is the IC3 component id
    private int dsmIdx;
    private String compName;
//    @XmlTransient
    private String fullName;
    private String type;
    private String exported;    //T true or F false
    @XmlElement(name = "requiredPrmToAccess")
    private String requiredPrmToAccess; //Other component needs only this permission to communicate with this Component
    @XmlElement(name = "providerReadPermission")
    private String providerReadPermission;
    @XmlElement(name = "providerWritePermission")
    private String providerWritePermission;
    @XmlElement(name = "providerAuthority")
    private String providerAuthority;
    
    @XmlTransient
    private boolean systemComponent = false;
    

    
    @XmlElement(name =  "appPackageName")
    private String packageName;
    @XmlTransient
    private String discoveredIn; //S: static, IS: from the intents as sender component, IR from the intents as a receiver component, G: group permission, P: permission, A: ArchExtractor        
    
    @XmlElementWrapper(name = "compRequiredPermissions")
    @XmlElement(name = "compRequiredPermission")
    private List<String> requiredPermissions; // This list contains permissions from the Manifest and permissions checked at runtime

    @XmlElementWrapper(name = "compActuallyUsedPermissions")
    @XmlElement(name = "compActuallyUsedPermissionsn")
    private ArrayList<String> actuallyUsedPermissions = new ArrayList<String>();    

    @XmlElementWrapper(name = "intentFilters")
    @XmlElement(name = "intentFilter")
    ArrayList<IntentFilter> intentFilters = new ArrayList<IntentFilter>();
    
    @XmlTransient
    private List<String> appUsesPermissions; //for the use on the LP enforcement only
    @XmlTransient
    private Set<String> ifActions; // Intent Filter actions, for the use on the LP enforcement only

    public Component(String packageName){
//        int componentId;
//        if(UNKNOW_PACKAGE.equals(packageName)){
//            componentId=-1;
//        }else{
//            componentId=compId++;
//        }        
        init(packageName, -2);
    }
    public Component(String packageName, int componentId){
        
//        if(id!=-1){ this.id=compId++;}else{
//            this.id=id;
//        }        
//        compId = Math.max(compId, ++componentId);
        init(packageName, componentId);
    }
    private void init(String packageName, int component_id){
        
        if (LPDetermination.sysPackageName.equals(packageName)){
            this.systemComponent = true;
        }else{
            this.systemComponent = false;
        }
        if(UNKNOW_PACKAGE.equals(packageName)){
            componentId=-1;
        }else if (component_id==-2){
            this.componentId=SEQUENCE_ID++;
        }else{
            this.componentId=component_id;
        }
        this.type="";
        this.packageName = packageName;
        discoveredIn = "S"; //default
        this.requiredPermissions = new ArrayList<>();
        this.actuallyUsedPermissions = new ArrayList<>();
        this.intentFilters = new ArrayList<>();
        this.appUsesPermissions = new ArrayList<>();
        this.ifActions = new HashSet<>();
        
//        this.providerAuthority = "";
//        this.providerReadPermission = "";
//        this.providerWritePermission = "";
        
//        checkId(this.id, packageName);        
    }
//    private void checkId(int id, String packageName){        
//        if (id!=-1 && !ids.add(id)){
//            throw new IllegalStateException("Component Id ("+id+") already inserted in package "+packageName);            
//        }
//            else{
//            System.out.println("Ok compId:"+id);
//        }        
//    }

    public Set<String> getIfActions() {
        return ifActions;
    }

    public void setIfActions(Set<String> ifActions) {
        this.ifActions = ifActions;
    }

    public List<String> getAppUsesPermissions() {
        return appUsesPermissions;
    }

    public void setAppUsesPermissions(List<String> appUsesPermissions) {
        this.appUsesPermissions = appUsesPermissions;
    }

    public boolean isSystemComponent() {
        return systemComponent;
    }

    public void setSystemComponent(boolean systemComponent) {
        this.systemComponent = systemComponent;
    }

    
    
    public String getProviderAuthority() {
        return providerAuthority;
    }

    public void setProviderAuthority(String providerAuthority) {
        this.providerAuthority = providerAuthority;
    }

    public String getProviderReadPermission() {
        return providerReadPermission;
    }

    public void setProviderReadPermission(String providerReadPermission) {
        this.providerReadPermission = providerReadPermission;
    }

    public String getProviderWritePermission() {
        return providerWritePermission;
    }

    public void setProviderWritePermission(String providerWritePermission) {
        this.providerWritePermission = providerWritePermission;
    }

    
    public int getDsmIdx() {
        return dsmIdx;
    }

    public void setDsmIdx(int dsmIdx) {
        this.dsmIdx = dsmIdx;
    }

    
    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }


    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    
    public String getExported() {
        return exported;
    }

    public void setExported(String exported) {
        String s = String.valueOf(exported.toUpperCase().charAt(0)); //T or F        
        this.exported = s;
    }

    
    public ArrayList<String> getActuallyUsedPermissions() {
        return actuallyUsedPermissions;
    }

    public void setActuallyUsedPermissions(ArrayList<String> actuallyUsedPermissions) {
        this.actuallyUsedPermissions = actuallyUsedPermissions;
    }

    public ArrayList<IntentFilter> getIntentFilters() {
        return intentFilters;
    }

    public void setIntentFilters(ArrayList<IntentFilter> intentFilters) {
        this.intentFilters = intentFilters;
    }

    public String getDiscoveredIn() {
        return discoveredIn;
    }

    public void setDiscoveredIn(String discoveredIn) {
        this.discoveredIn = discoveredIn;
    }


    public String getRequiredPrmToAccess() {        
        return requiredPrmToAccess;
    }

    //OP needs to check only this one
    public void setRequiredPrmToAccess(String requiredPrmToAccess) {
        if ("null".equals(requiredPrmToAccess) || "<NULL>".equals(requiredPrmToAccess)){
            return;
        }
        this.requiredPrmToAccess = requiredPrmToAccess;
    }
    public List<String> getRequiredPermissions() {
        return requiredPermissions;
    }

    public void setRequiredPermissions(List<String> requiredPermissions) {
        this.requiredPermissions = requiredPermissions;
        if (this.requiredPrmToAccess!=null && !this.requiredPermissions.contains(this.requiredPrmToAccess))
            this.requiredPermissions.add(this.requiredPrmToAccess);
    }

//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        if (this.id==-1)
//            this.id=compId++;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.compName = this.fullName.replace(this.packageName+".", "");
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    
    @Override
    public String toString(){
        String sep=LPDetermination.sep;
//        String formattedCompName = this.fullName;
        String formattedCompName = this.fullName.replace(packageName, "");
        if(formattedCompName.startsWith("."))
            formattedCompName=formattedCompName.substring(1);
        Application app = apps.get(this.packageName);
        
        String requiredPrms= ((this.requiredPrmToAccess==null)?"":this.requiredPrmToAccess+"/")+
                (this.requiredPermissions.isEmpty()?"":(requiredPermissions.toString().replace(",", ";")));
        
        
        if (app==null){
        return  this.packageName+sep
//                +this.discoveredIn+sep
                +this.type+sep+this.exported+sep+
                this.intentFilters.toString().replace(",", ";")+sep+
                ""+sep+ //permissions required to access this app
                requiredPrms +sep+   //A caller to this component should have requiredPrmToAccess permission
                ""+sep+
                this.actuallyUsedPermissions.toString().replace(",", ";")+sep+
                this.actuallyUsedPermissions.toString().replace(",", ";")+sep+
                (this.providerReadPermission==null?"":this.providerReadPermission)+sep+
                (this.providerWritePermission==null?"":this.providerWritePermission)+sep+
                (this.providerAuthority==null?"":this.providerAuthority)+sep+
                formattedCompName +sep+
                this.componentId+sep+
                this.dsmIdx;
                
        }else{
        return  this.packageName+sep
//                +this.discoveredIn+sep
                +this.type+sep+this.exported+sep+
                this.intentFilters.toString().replace(",", ";")+sep+
                app.getAppRequiredPermissions().toString().replace(",", ";")+sep+ //permissions required to access this app
                requiredPrms +sep+    //A caller to this component should have requiredPrmToAccess permission
                app.getAppUsesPermissions().toString().replace(",", ";")+sep+
                this.actuallyUsedPermissions.toString().replace(",", ";")+sep+
                (this.providerReadPermission==null?"":this.providerReadPermission)+sep+
                (this.providerWritePermission==null?"":this.providerWritePermission)+sep+
                (this.providerAuthority==null?"":this.providerAuthority)+sep+
                formattedCompName+sep+
                this.componentId+sep+
                this.dsmIdx;
        }
                    
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Component)) return false;
        Component c = (Component) o;
        if(this.componentId>=0){
            return this.componentId==c.componentId;
        }
        String compName=this.fullName.replace(this.packageName+".", "");
        String cCompName= c.fullName.replace(c.packageName+".", "");
        return this.componentId==c.componentId && this.packageName.equalsIgnoreCase(c.packageName) && compName.equalsIgnoreCase(cCompName);
    }
    
    public boolean nameEquals(Object o){
        if (!(o instanceof Component)) return false;
        Component c = (Component) o;
        String compName=this.fullName.replace(this.packageName+".", "");
        String cCompName= c.fullName.replace(c.packageName+".", "");
        return this.packageName.equalsIgnoreCase(c.packageName) && compName.equalsIgnoreCase(cCompName);
    }

    @Override
    public int hashCode(){
            return this.fullName.hashCode()*this.packageName.hashCode()*17;
    }

    @Override
    public int compareTo(Object o) {
        Component c = (Component) o;
        if(!this.packageName.equalsIgnoreCase(c.packageName)){
            //System package is greater than any other packages, so it will be listed at the end of the DSM
            if (LPDetermination.sysPackageName.equals(this.packageName))
                return 1;
            if (LPDetermination.sysPackageName.equals(c.packageName))
                return -1;
            return this.packageName.compareTo(c.packageName);
        }
        float f= this.componentId - c.componentId;
        return (int) Math.signum(f);
    }


}
