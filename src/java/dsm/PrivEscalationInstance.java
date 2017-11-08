/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mahmoud
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"senderDsmIdx","senderComponent","receiverDsmIdx","receiverComponent", "resourceDsmIdx","resource"})

public class PrivEscalationInstance {
    private int senderDsmIdx;
    private int receiverDsmIdx;
    private int resourceDsmIdx;

    public PrivEscalationInstance(int senderDsmIdx, int receiverDsmIdx, int resourceDsmIdx) {
        this.senderDsmIdx = senderDsmIdx;
        this.receiverDsmIdx = receiverDsmIdx;
        this.resourceDsmIdx = resourceDsmIdx;
    }

    public int getSenderDsmIdx() {
        return senderDsmIdx;
    }

    public void setSenderDsmIdx(int senderDsmIdx) {
        this.senderDsmIdx = senderDsmIdx;
    }

    public int getReceiverDsmIdx() {
        return receiverDsmIdx;
    }

    public void setReceiverDsmIdx(int receiverDsmIdx) {
        this.receiverDsmIdx = receiverDsmIdx;
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
        return (this.receiverDsmIdx==p.receiverDsmIdx) && (this.senderDsmIdx==p.senderDsmIdx) && (this.resourceDsmIdx==p.resourceDsmIdx);
    }
    
    @XmlElement(name = "senderComponent")
    public String getSenderComponent(){
        return LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(this.senderDsmIdx)).getName();
    }
    @XmlElement(name = "receiverComponent")
    public String getReceiverComponent(){
        return LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(this.receiverDsmIdx)).getName();        
    }
    @XmlElement(name = "resource")
    public String getResource(){
        return LPDetermination.componentsMap.get(LPDetermination.dsmIdxComponentIdMap.get(this.resourceDsmIdx)).getName();                
    }
    
}
