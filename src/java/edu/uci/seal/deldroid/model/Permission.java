/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.model;

import edu.uci.seal.deldroid.lp.LPDetermination;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Mahmoud
 */
public class Permission implements Comparable{
    String definedByPkg;
    private Integer prmId;
    private String name;
    char protectionLevel;    
    boolean systemPrm;
    String permissionGroupName;
    String resourceName; //for example, SEND_SMS permission belong to SMS resource
    boolean customPermission;
    private int prmDomainIdx;
    @XmlTransient
    private static int SEQUENCE_ID=2000000;
    
    public Permission(Integer prmId, String definedByPkg, String name, char protectionLevel, boolean isSystemPrm, String permissionGroupName, String resourceName){
        if (prmId == null){
            this.prmId = SEQUENCE_ID++;
        }else{
            this.prmId = prmId;
        }
        this.systemPrm = isSystemPrm;
        if (isSystemPrm){
            this.definedByPkg = LPDetermination.sysPackageName;
        }else{
            this.definedByPkg = definedByPkg;
        }
        this.name = name;
        this.protectionLevel = protectionLevel;
        
        this.permissionGroupName = permissionGroupName;
        this.resourceName = resourceName;
        this.customPermission = false;
    }
//    public Permission(){
//        this.prmId = SEQUENCE_ID++;
//    }
//    public Permission(Integer prmId){
//        this.prmId = prmId;        
//    }

    public int getPrmDomainIdx() {
        return prmDomainIdx;
    }

    public void setPrmDomainIdx(int prmDomainIdx) {
        this.prmDomainIdx = prmDomainIdx;
    }

    public boolean isCustomPermission() {
        return customPermission;
    }

    public void setCustomPermission(boolean customPermission) {
        this.customPermission = customPermission;
    }

    
    public Integer getPrmId() {
        return prmId;
    }

    public void setPrmId(Integer prmId) {
        this.prmId = prmId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(char protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public String getDefinedByPkg() {
        return definedByPkg;
    }

    public void setDefinedByPkg(String definedByPkg) {
        this.definedByPkg = definedByPkg;
    }

    public boolean isSystemPrm() {
        return systemPrm;
    }

    public void setSystemPrm(boolean systemPrm) {
        this.systemPrm = systemPrm;
    }

    public String getPermissionGroupName() {
        return permissionGroupName;
    }

    public void setPermissionGroupName(String permissionGroupName) {
        this.permissionGroupName = permissionGroupName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb = sb.append(this.definedByPkg).append("-").append(this.name).append("/").append(this.protectionLevel);
        return this.prmDomainIdx+">"+sb.toString();
    }
    @Override
    public boolean equals(Object obj){
        if (! (obj instanceof Permission)){
            return false;
        }
        Permission that = (Permission) obj;
        if (that.getName()==null || that.definedByPkg==null){
            return false;
        }
        return this.name.equalsIgnoreCase(that.getName()) && this.definedByPkg.equalsIgnoreCase(that.getDefinedByPkg());
    }
    @Override
    public int compareTo(Object o) {
        Permission p = (Permission) o;
        if( ! this.definedByPkg.equalsIgnoreCase(p.definedByPkg)){
            //System package is greater than any other packages, so it will be listed at the end of the DSM
            if (LPDetermination.sysPackageName.equals(this.definedByPkg))
                return 1;
            if (LPDetermination.sysPackageName.equals(p.definedByPkg))
                return -1;
            return this.definedByPkg.compareTo(p.definedByPkg);
        }
        
        return this.name.compareTo(p.name);
    }    
}
