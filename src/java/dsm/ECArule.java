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
public class ECArule {
    String senderPkg;
    String senderClass;
    String receiverPkg;
    String receiverClass;
    String action;
    
    public ECArule(String senderPkg, String senderClass, String receiverPkg, String receiverClass, String action){
        this.senderPkg = senderPkg;
        this.senderClass = senderClass;
        this.receiverPkg=receiverPkg;
        this.receiverClass=receiverClass;
        this.action=action;
    }
    @Override
    public String toString(){
        String sep=LPDetermination.sep;
        return this.senderPkg+sep+this.senderClass+sep+this.receiverPkg+sep+this.receiverClass+sep+this.action;        
    }
    
    
}
