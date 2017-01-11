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
public class ECAServiceRule {
    private String requester;
    private String service;
    
    public ECAServiceRule(String requester, String service){
        this.requester=requester;
        this.service=service;
    }
    @Override
    public String toString(){
        return requester+LPDetermination.sep+service;
    }
    @Override
    public boolean equals(Object o){
        if (! (o instanceof ECAServiceRule))
            return false;
        ECAServiceRule e = (ECAServiceRule) o;
        return this.requester.equals(e.requester) && this.service.equals(e.service);
    }
    
}
