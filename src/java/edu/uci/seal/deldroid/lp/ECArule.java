/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

/**
 *
 * @author Mahmoud
 */
public class ECArule {
        public static enum EcaRuleAction {

        PREVENT(0),
        ALLOW(1);
        private int idx;
        EcaRuleAction(int idx) {
            this.idx = idx;
        }
        public int getIndex() {
            return this.idx;
        }
}

    String senderPkg;
    String senderClass;
    String receiverPkg;
    String receiverClass;
    String action;
    EcaRuleAction ecaRuleAction; //0:prevent, 1:allow
    
    public ECArule(String senderPkg, String senderClass, String receiverPkg, String receiverClass, String action, EcaRuleAction ruleAction){
        this.senderPkg = senderPkg;
        this.senderClass = senderClass;
        this.receiverPkg=receiverPkg;
        this.receiverClass=receiverClass;
        this.action=action;
        this.ecaRuleAction = ruleAction;
    }
    @Override
    public String toString(){
        String sep=LPDetermination.sep;
        return this.ecaRuleAction.getIndex()+sep+this.senderPkg+sep+this.senderClass+sep+this.receiverPkg+sep+this.receiverClass+sep+this.action;
    }
    @Override
    public boolean equals(Object o){
        if (!( o instanceof ECArule)){
            return false;
        }
        ECArule that = (ECArule) o;
        if (this.senderClass==null && this.receiverPkg!=null && this.receiverClass == null){
                return  this.ecaRuleAction.equals(that.ecaRuleAction) && 
                        this.senderPkg.equals(that.senderPkg) &&
                        this.receiverPkg.equals(that.receiverPkg)
                ;
        }
        if (this.receiverPkg!=null && this.receiverClass == null){
                return this.ecaRuleAction.equals(that.ecaRuleAction) && this.senderPkg.equals(that.senderPkg) &&
                this.senderClass.equals(that.senderClass) &&
                this.receiverPkg.equals(that.receiverPkg)
                ;
        }
        if (this.receiverPkg!=null && this.receiverClass != null){
                return this.ecaRuleAction.equals(that.ecaRuleAction) && this.senderPkg.equals(that.senderPkg) &&
                this.senderClass.equals(that.senderClass) &&
                this.receiverPkg.equals(that.receiverPkg) &&
                this.receiverClass.equals(that.receiverClass);
        }
        if (this.action != null){
                return this.ecaRuleAction.equals(that.ecaRuleAction) && this.senderPkg.equals(that.senderPkg) &&
                this.senderClass.equals(that.senderClass) &&
                this.action.equals(that.action);            
        }
        return false;
    }
    
    
}
