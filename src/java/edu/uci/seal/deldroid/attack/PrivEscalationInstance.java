/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.attack;

import edu.uci.seal.deldroid.lp.LPDetermination;
import edu.uci.seal.deldroid.model.Component;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mahmoud
 */
@XmlAccessorType(XmlAccessType.PROPERTY)

@XmlType(propOrder = {"malApp","malComp", "malCompId", "malCompDsmIdx", "vulApp", "vulComp", "vulCompId", "vulCompDsmIdx", "resourceDsmIdx", "resource"})

public class PrivEscalationInstance {
    
    private int malCompDsmIdx;    //sender
    private int vulCompDsmIdx;    //receiver
    private int resourceDsmIdx;
    private boolean iac;
    
    private Component malComp;
    private Component vulComp;

    public PrivEscalationInstance(int senderDsmIdx, int receiverDsmIdx, int resourceDsmIdx) {
        this.malCompDsmIdx = senderDsmIdx;
        this.vulCompDsmIdx = receiverDsmIdx;
        this.resourceDsmIdx = resourceDsmIdx;
        
        this.malComp = LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(senderDsmIdx));
        this.vulComp = LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(receiverDsmIdx));
        this.iac = !(this.malComp.getPackageName().equals(this.vulComp.getPackageName()));
    }

    public int getMalCompDsmIdx() {
        return malCompDsmIdx;
    }

    public void setMalCompDsmIdx(int malCompDsmIdx) {
        this.malCompDsmIdx = malCompDsmIdx;
    }

    
    public int getVulCompDsmIdx() {
        return vulCompDsmIdx;
    }

    public void setVulCompDsmIdx(int vulCompDsmIdx) {
        this.vulCompDsmIdx = vulCompDsmIdx;
    }

    public int getResourceDsmIdx() {
        return resourceDsmIdx;
    }

    public void setResourceDsmIdx(int resourceDsmIdx) {
        this.resourceDsmIdx = resourceDsmIdx;
    }
    
    @Override
    public boolean equals(Object o){
        if (!(o instanceof PrivEscalationInstance)){
            return false;
        }
        PrivEscalationInstance p = (PrivEscalationInstance) o;
        return (this.vulCompDsmIdx==p.vulCompDsmIdx) && (this.malCompDsmIdx==p.malCompDsmIdx) && (this.resourceDsmIdx==p.resourceDsmIdx);
    }
    
    @XmlElement(name = "malApp")
    public String getMalApp(){
        return this.malComp.getPackageName();
    }
    @XmlElement(name = "malComp")
    public String getMalComp(){
        return this.malComp.getFullName();
    }
    @XmlElement(name = "malCompId")
    public int getMalCompId(){
        return this.malComp.getComponentId();
    }
    @XmlElement(name = "vulApp")
    public String getVulApp(){
        return this.vulComp.getPackageName();
    }
    @XmlElement(name = "vulComp")
    public String getVulComp(){
        return this.vulComp.getFullName();
    }
    @XmlElement(name = "vulCompId")
    public int getVulCompId(){
        return this.vulComp.getComponentId();
    }
    @XmlElement(name = "resource")
    public String getResource(){
        return LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(this.resourceDsmIdx)).getFullName();                
    }

    @XmlTransient
    public boolean isIac() {
        return iac;
    }

    public void setIac(boolean iac) {
        this.iac = iac;
    }
    
    @Override
    public String toString(){
        String msg = "Component [" + this.malCompDsmIdx + "] "
                        + this.malComp.getFullName()
                        + " --> [" + this.vulCompDsmIdx + "] "
                        + this.vulComp.getFullName()
                        + " ON permission [" + this.resourceDsmIdx + "] "
                        + this.getResource()                        
                        + "\n";
        return msg;
    }
}
