/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.dynamicmdm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mahmoud
 */
public class AnalysisLookup {
    
    public Map<SecurityAnalysis, Set<DomainType>> securityAnalysisDomainsMap;
    public Map<DomainType, Set<SecurityAnalysis>> domainSecurityAnalysisMap;
    
    public AnalysisLookup(){
        this.securityAnalysisDomainsMap = new HashMap<>();
        this.domainSecurityAnalysisMap = new HashMap<>();
        
        //add the lookup table information
        addEntry(SecurityAnalysis.PE, DomainType.EXPLICIT);
        addEntry(SecurityAnalysis.PE, DomainType.IMPLICIT);
        addEntry(SecurityAnalysis.PE, DomainType.GRANTED);        
        addEntry(SecurityAnalysis.PE, DomainType.USAGE );
        addEntry(SecurityAnalysis.PE, DomainType.ENFORCEMENT );
        
        addEntry(SecurityAnalysis.IS, DomainType.EXPLICIT );
        addEntry(SecurityAnalysis.IS, DomainType.IMPLICIT );
        
        addEntry(SecurityAnalysis.UIR, DomainType.IMPLICIT );
        
        addEntry(SecurityAnalysis.ICP, DomainType.EXPLICIT);
        addEntry(SecurityAnalysis.ICP, DomainType.IMPLICIT);
        addEntry(SecurityAnalysis.ICP, DomainType.GRANTED);        
        addEntry(SecurityAnalysis.ICP, DomainType.ENFORCEMENT );
        
        addEntry(SecurityAnalysis.PDL, DomainType.DATA_ACCESS);
        addEntry(SecurityAnalysis.PDL, DomainType.CP_READ_PRM);
        
        addEntry(SecurityAnalysis.CP, DomainType.DATA_MANIPULATION);
        addEntry(SecurityAnalysis.CP, DomainType.CP_WRITE_PRM);
        
    }    
    private void addEntry(SecurityAnalysis analysis, DomainType domain){
        Set<DomainType> domains = new HashSet<>();
        
        if (this.securityAnalysisDomainsMap.containsKey(analysis)){
            domains = this.securityAnalysisDomainsMap.get(analysis);
        }else{
            this.securityAnalysisDomainsMap.put(analysis, domains);
        }
        domains.add(domain);
        
        
        Set<SecurityAnalysis> analysisSet = new HashSet<>();
        if (this.domainSecurityAnalysisMap.containsKey(domain)){
            analysisSet = this.domainSecurityAnalysisMap.get(domain);
        }else{
            this.domainSecurityAnalysisMap.put(domain, analysisSet);
        }
        analysisSet.add(analysis);        
    }
            
    /*
            SecurityAnalysis
    */
    public static enum SecurityAnalysis{
        PE (1, "Privilege Escalation"),
        UIR (2, "Unauthorized Intent Receipt"),
        IS (3, "Intent Spoofing"),
        ICP(4, "Identical Custom Permission"),
        UPE (5, "Unsafe Pending Intent"),
        PDL (6, "Passive Data Leak"),
        CP (7, "Content Pollution")        
        ;
        
        private int id;
        private String name;
        SecurityAnalysis(int id, String name){
            this.id = id;
            this.name = name;
        }
        public int getId(){
            return this.id;
        }
        public String getName(){
            return this.name;
        }
    }
    /*
        DomainType
    */
    public static enum DomainType{
        //communication domains
        EXPLICIT(1, "Explicit Communication Domain"),
        IMPLICIT(2, "Implicit Communication Domain"),
        EMPTY_PI(3, "Empty PendingIntent Domain"),
        DATA_ACCESS(4, "Data Access Domain"),
        DATA_MANIPULATION(5, "Data Manipulation Domain"),
        //permission domains
        GRANTED(21, "Permission Granted Domain"),       
        USAGE(22, "Permission Usage Domain"),
        ENFORCEMENT(23, "Permission Enforcement Domain"),
        CP_READ_PRM (24, "CP Required Read Permission Domain"), //required permission to read data from a content provider (CP)
        CP_WRITE_PRM(25, "CP Required Write Permission Domain") //required permission to write to a CP
        ;
        private Integer id;
        private String name;
        
        DomainType(Integer id, String name){
            this.id = id;
            this.name = name;
        }
        public Integer getDomainId(){
            return this.id;
        }

        public String getName() {
            return this.name;
        }
    }
    /*
        Operations
    */
    public static enum Operation{
        ADD_APP(1),
        REMOVE_APP(2),
        UPDATE_APP(3),
        GRANT_PERMISSION(4),
        REVOKE_PERMISSION(5),
        ADD_COMMUNICATION(6),
        ;
                
        private int id;
        Operation(int id){
            this.id = id;        
        }
        public int getOpId(){
            return this.id;
        }    
    }
    
    
}
