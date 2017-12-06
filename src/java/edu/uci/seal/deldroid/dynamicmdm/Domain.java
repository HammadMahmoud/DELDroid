/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.dynamicmdm;

/**
 *
 * @author Mahmoud
 */
public class Domain {
    
    public int [][] dsm;
    public int [] columnsID;
    
    public Domain(int[][] dsm){
        if (dsm==null || dsm.length==0){
            throw new IllegalStateException("Invalid DSM. DSM cannot be null or empty");
        }
        this.dsm = dsm;
        this.columnsID = new int[this.dsm[0].length];
    }
    public Domain(int[][] dsm, int[] columnsID){
        if (dsm==null || dsm.length==0){
            throw new IllegalStateException("Invalid DSM. DSM cannot be null or empty");
        }
        this.dsm = dsm;
        this.columnsID = columnsID;
    }

    public void setDsm(int[][] dsm) {
        this.dsm = dsm;
    }
    @Override
    public String toString(){
        if (this.dsm == null){
            return "";
        }
        if (this.dsm.length == 0){
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();        
        for (int row=0; row<dsm.length; row++){
            sb.append(dsm[row][0]);
            for (int col=1; col<dsm[0].length; col++){
                sb.append(",").append(dsm[row][col]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
}
