/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.attack;

import edu.uci.seal.deldroid.attack.UnauthorizedIntentReceipt;
import edu.uci.seal.deldroid.attack.PrivEscalationInstance;
import edu.uci.seal.deldroid.attack.IntentSpoofing;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import edu.uci.seal.deldroid.utils.WebServicesUtils;

/**
 *
 * @author Mahmoud
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"privilegeEscalationInstances","intentSpoofingInstances", "unauthorizedIntentReceiptInstances"})

public class AnalysisResult {
    @XmlElementWrapper(name = "privilegeEscalationInstances")
    @XmlElement(name = "privilegeEscalationInstance")
    private Set<PrivEscalationInstance> privilegeEscalationInstances;

    @XmlElementWrapper(name = "intentSpoofingInstances")
    @XmlElement(name = "intentSpoofingInstance")
    private Set<IntentSpoofing> intentSpoofingInstances;

    @XmlElementWrapper(name = "unauthorizedIntentReceiptInstances")
    @XmlElement(name = "unauthorizedIntentReceiptInstance")
    private Set<UnauthorizedIntentReceipt> unauthorizedIntentReceiptInstances;
    

    public AnalysisResult(Set<PrivEscalationInstance> privilegeEscalationInstances, 
            Set<IntentSpoofing> intentSpoofingInstances,
            Set<UnauthorizedIntentReceipt> unauthorizedIntentReceiptInstances
            ){
        this.privilegeEscalationInstances = privilegeEscalationInstances;
        this.intentSpoofingInstances = intentSpoofingInstances;
        this.unauthorizedIntentReceiptInstances = unauthorizedIntentReceiptInstances;
        
    }
    public AnalysisResult(){
        
    }
    
    public void toXml(String fileName) throws JAXBException{
        WebServicesUtils.toXML(fileName, this);
    }


    
    
}
