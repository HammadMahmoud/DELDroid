/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

/**
 *
 * @author Mahmoud
 */
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
    
}
