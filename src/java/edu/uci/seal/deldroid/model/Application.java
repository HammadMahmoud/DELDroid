/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dsm.LPDetermination;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import utils.WebServicesUtils;

/**
 *
 * @author Mahmoud
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"appId", "name", "packageName", "versionCode", "versionName", "appcategory",
                      "appUsesPermissions", "appActuallyUsesPermissions", "appRequiredPermissions", 
                      "appDefinedPermissions", "components"})

public class Application {

    int appId;
    String name;
    String packageName;
    String versionCode;
    String versionName;
    String appcategory; //app category as it appears in the app store, for example: travel, game, etc
    @XmlElementWrapper(name = "appUsesPermissions")
    @XmlElement(name = "appUsesPermission")
    List<String> appUsesPermissions;

    @XmlElementWrapper(name = "appActuallyUsesPermissions")
    @XmlElement(name = "appActuallyUsesPermission")
    List<String> appActuallyUsesPermissions;

    @XmlElementWrapper(name = "appRequiredPermissions")
    @XmlElement(name = "appRequiredPermission")
    List<String> appRequiredPermissions;

    @XmlElementWrapper(name = "appDefinedPermissions")
    @XmlElement(name = "appDefinedPermission")
    Set<String> appDefinedPermissions;

    @XmlElementWrapper(name = "components")
    @XmlElement(name = "component")
    Set<Component> components;

    public Application(){
        this.appcategory = "Default";
        
    }
    public Application(int id) {
        this.appId = id;
        appUsesPermissions = new ArrayList<>();
        appActuallyUsesPermissions = new ArrayList<>();
        appRequiredPermissions = new ArrayList<>();
        appDefinedPermissions = new HashSet<>();
        this.components = new HashSet<>();
        this.appcategory = "Default";

    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public Set<Component> getComponents() {
        return components;
    }

    public void setComponents(Set<Component> components) {
        this.components = components;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getAppcategory() {
        return appcategory;
    }

    public void setAppcategory(String appcategory) {
        this.appcategory = appcategory;
    }

    public List<String> getAppUsesPermissions() {
        return appUsesPermissions;
    }

    public void setAppUsesPermissions(List<String> appUsesPermissions) {
        this.appUsesPermissions = appUsesPermissions;
    }

    public List<String> getAppActuallyUsesPermissions() {
        return appActuallyUsesPermissions;
    }

    public void setAppActuallyUsesPermissions(List<String> appActuallyUsesPermissions) {
        this.appActuallyUsesPermissions = appActuallyUsesPermissions;
    }

    public List<String> getAppRequiredPermissions() {
        return appRequiredPermissions;
    }

    public void setAppRequiredPermissions(List<String> appRequiredPermissions) {
        this.appRequiredPermissions = appRequiredPermissions;
    }

    public Set<String> getAppDefinedPermissions() {
        return appDefinedPermissions;
    }

    public void setAppDefinedPermissions(Set<String> appDefinedPermissions) {
        this.appDefinedPermissions = appDefinedPermissions;
    }

    @Override
    public String toString() {
        String sep = LPDetermination.sep;
        return this.packageName + sep + this.name + sep + this.versionCode + sep + this.versionName + sep
                + this.appUsesPermissions.toString().replace(",", ";") + sep
                + this.appActuallyUsesPermissions.toString().replace(",", ";") + sep
                + this.appDefinedPermissions.toString().replace(",", ";") + sep
                + this.appRequiredPermissions.toString().replace(",", ";") + sep
                + this.appUsesPermissions.size() + sep
                + this.appActuallyUsesPermissions.size();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Application)) {
            return false;
        }
        Application that = (Application) o;

        return this.packageName.equals(that.packageName);
    }

    public void toXml(String filename) throws JAXBException{
        WebServicesUtils.toXML(filename, this);

    }
}
