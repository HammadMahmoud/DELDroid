/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import dsm.ECArule.EcaRuleAction;

/**
 *
 * @author Mahmoud
 */
public class ECAServiceRule {
    private String requester;
    private String service;
    private EcaRuleAction ecaRuleAction;
    
    public ECAServiceRule(String requester, String service, EcaRuleAction ruleAction){
        this.requester=requester;
        this.service=service;
        this.ecaRuleAction = ruleAction;
    }
    @Override
    public String toString(){
        return this.ecaRuleAction.getIndex()+LPDetermination.sep+requester+LPDetermination.sep+service;
    }
    @Override
    public boolean equals(Object o){
        if (! (o instanceof ECAServiceRule))
            return false;
        ECAServiceRule e = (ECAServiceRule) o;
        return this.ecaRuleAction.equals(e.ecaRuleAction) && this.requester.equals(e.requester) && this.service.equals(e.service);
    }
    
}
