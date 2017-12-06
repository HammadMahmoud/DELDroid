/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.dynamicmdm;

import edu.uci.seal.deldroid.dynamicmdm.AnalysisLookup.DomainType;
import edu.uci.seal.deldroid.lp.IllustrativeExample;

/**
 *
 * @author Mahmoud
 */
public class TestDynamicMDM {
    public static void main(String[] args){
        
        IllustrativeExample.main(null);
        
        AnalysisLookup lookup = new AnalysisLookup();

        SystemArchitecture system = new SystemArchitecture();
        Domain commDomain = getDomain(6);        //6 components
        Domain prmDomain = getDomain(3);     //3 permissions
        Domain dataDomain = getDomain(2);         //2 CPs
                
        system.addDomain(DomainType.EXPLICIT, commDomain);
        system.addDomain(DomainType.IMPLICIT, commDomain);
        system.addDomain(DomainType.EMPTY_PI , commDomain);
        
        system.addDomain(DomainType.DATA_ACCESS , dataDomain);
        system.addDomain(DomainType.DATA_MANIPULATION , dataDomain);

        system.addDomain(DomainType.CP_READ_PRM , prmDomain);
        system.addDomain(DomainType.CP_WRITE_PRM , prmDomain);
        system.addDomain(DomainType.ENFORCEMENT , getEnforcement());
        system.addDomain(DomainType.GRANTED , getGranted());
        system.addDomain(DomainType.USAGE , getUsage());
                
        System.out.println(system);
        
        DmdmAnalysis analysis = new DmdmAnalysis(lookup, system);        
        analysis.doAnalysis();
    }
    
    private static Domain getDomain(int comps){
        int[] columnsId = new int []{1000001, 1000002, 1000003, 1000004, 1000005};
        int[][] dsm = new int [comps][comps];
        Domain dummyDomain = new Domain(dsm);
        for (int i=0; i<dsm.length; i++){
            for (int col=0; col<dsm[0].length; col++){
                dsm[i][col] = 1;
            }
        }
        
        dummyDomain.columnsID = columnsId;
        return dummyDomain;
    }
    
    private static Domain getPrmDomain(int[][] dsm, int[] columnsId){
        Domain domain = new Domain(dsm);
        domain.columnsID = columnsId;
        return domain;
        
    }
    private static Domain getUsage(){
        int[] columnsId = new int []{1000006, 1000007, 1000008};
        int[][] dsm = new int[][]{
            {1,0,0},
            {0,1,0},
            {0,0,1},
            {0,0,0},
            {0,0,0},
            {0,0,0}
        };
        return getPrmDomain(dsm, columnsId);
    }
    private static Domain getGranted(){
        int[] columnsId = new int []{1000006, 1000007, 1000008};
        int[][] dsm = new int[][]{
            {1,0,0},
            {0,1,0},
            {0,0,1},
            {0,0,0},
            {0,0,0},
            {0,0,0}
        };        
        return getPrmDomain(dsm, columnsId);
    }
    private static Domain getEnforcement(){
        int[] columnsId = new int []{1000006, 1000007, 1000008};
        int[][] dsm = new int[][]{
            {1,0,0},
            {0,0,0},
            {0,1,0},
            {0,0,0},
            {0,0,1},
            {0,0,0}
        };        
        return getPrmDomain(dsm, columnsId);
    }
    
}
