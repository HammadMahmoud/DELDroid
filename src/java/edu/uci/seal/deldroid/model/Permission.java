/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.model;

import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Mahmoud
 */
public class Permission {
    String definedByPkg;
    private Integer prmId;
    private String name;
    char protectionLevel;    
    boolean systemPrm;
    String permissionGroupName;
    String resourceName; //for example, SEND_SMS permission belong to SMS resource
    @XmlTransient
    private static int SEQUENCE_ID=2000000;
    
    public Permission(String definedByPkg, String name, char protectionLevel, boolean isSystemPrm, String permissionGroupName, String resourceName){
        this.prmId = SEQUENCE_ID++;
        this.definedByPkg = definedByPkg;
        this.name = name;
        this.protectionLevel = protectionLevel;
        this.systemPrm = isSystemPrm;
        this.permissionGroupName = permissionGroupName;
        this.resourceName = resourceName;
    }
    public Permission(){
        this.prmId = SEQUENCE_ID++;
    }
    public Permission(Integer prmId){
        this.prmId = prmId;        
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
        sb = sb.append(this.name).append("/").append(this.protectionLevel);
        return sb.toString();
    }
    
}
