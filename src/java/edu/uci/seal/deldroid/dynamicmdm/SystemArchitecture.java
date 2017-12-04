/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.dynamicmdm;

import edu.uci.seal.deldroid.dynamicmdm.AnalysisLookup.DomainType;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Mahmoud
 */

public class SystemArchitecture {
    
    Map<DomainType, Domain> dynamicMdm;
    
    
    public SystemArchitecture(){
        this.dynamicMdm = new HashMap<>();
    }
    
    public void addDomain(DomainType domainType, Domain domain){
        this.dynamicMdm.put(domainType, domain);
        
    }
    
    public void addApp(){
        
    }
    public void updateApp(){
        
    }    
    public void removeApp(){
        
    }    
    public void grantPermission(){
        
    }
    public void revokePermission(){
        
    }
    public void addCommunication(){
        
    }
    public void printMe(){
    }
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Entry<DomainType, Domain> e : this.dynamicMdm.entrySet()){
            sb.append(e.getKey().getName());
            sb.append("\n");
            sb.append(e.getValue());
            sb.append("\n");
        }                
        return sb.toString();
    }
}
