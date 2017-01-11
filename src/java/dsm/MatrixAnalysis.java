/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mahmoud
 */
public class MatrixAnalysis {
    public static void main(String[] args){
        int [][] dsm ={ {0,1,0,0,0},
                        {0,0,0,1,0},
                        {0,0,0,0,0},
                        {0,0,0,0,0},
                        {0,0,0,0,0}};
//        int [][] dsm = {{1,2,3},{4,5,6},{7,8,9}};
//        printArray(getRow(dsm, 2, 0, 1));
//        Set<PrivEscalationInstance>  s = privilegeEscalationAnalysis(dsm, 3);
//        for (PrivEscalationInstance p : s){
//            System.out.println(p.getSenderDsmIdx()+" "+p.getReceiverDsmIdx()+" "+p.getResourceDsmIdx());
//        }
//        emptyColumns(dsm, 2);
        printArray(getRow(dsm, 0, 0, dsm.length-1));
        legitimateHas(dsm, 3);
        printArray(getRow(dsm, 0, 0, dsm.length-1));
        
        
    }
    public static void legitimateHas(int[][] dsm, int resourceIdx){
        for (int col=resourceIdx; col<dsm.length; col++){//look at the used permissions
            for (int row=0; row<resourceIdx; row++){ //find a ocmponent that actually uses it
                if(col==row){
                    continue;
                }
                if(checkUse(dsm[row][col])){//component row uses permission col
                    System.out.println("component "+row+" uses permission "+ col);
                    //find the components that communicate with component row
                    for(int caller=0; caller<resourceIdx; caller++){
                        if(row!=caller && dsm[caller][row]!=0){//there is a communication between comp r and comp row
                            System.out.println("Component ID: "+caller+" communicates with component ID: "+row);
                            if(!checkUse(dsm[caller][col])){
                                dsm[caller][col]=2;
                                System.out.println("dsm["+caller+"]["+col+"]=2");
                            }
                        }
                    }
                    
                }
            }
        }
    }
    
    public static boolean[] emptyColumns(int[][] dsm, int resourceIdx){
        boolean[] emptyColumns = new boolean[dsm.length-resourceIdx];
        for (int col=resourceIdx; col<dsm.length; col++){
            boolean emptyCol=true;
            for (int row=0; row<resourceIdx; row++){
                if (dsm[row][col]!=0){
                    emptyCol=false;
                    break; //this column contains relationships
                }                    
            }
            if(emptyCol){
                emptyColumns[col-resourceIdx]=true;
            }
        }
        return emptyColumns;
    }
    public static Set<PrivEscalationInstance> privilegeEscalationAnalysis(int[][] dsm, int resourceIdx){
        
//        System.out.println("resourceIdx: "+resourceIdx);
        Set<PrivEscalationInstance> instances = new HashSet<>();
        PrivEscalationInstance instance = null;
        for (int x=0; x<resourceIdx-1; x++){ //note that component (resourceIdx-1) is the SystemService component
            for (int y=0; y<resourceIdx-1 ; y++){
                if (x!=y){
                if (dsm[x][y]!=0){ //exists a communication between x and y components
                    int[] xPrms = getRow(dsm, x, resourceIdx, dsm.length-1 ); //permissions for component x
                    int[] yPrms = getRow(dsm, y, resourceIdx, dsm.length-1 ); //permissions for component y
                    
                    for (int i=0; i<xPrms.length;i++){
                        if (checkUse(yPrms[i]) && //component x uses permission i
                            !checkEnforce(yPrms[i])  &&  //component x does not enforce permission p
                            (!checkHas(xPrms[i]) && 
                             !checkUse(xPrms[i]))   //component y does not have permission p
                                ){                                                         
                            //privilage escalation
                            instance = new PrivEscalationInstance(x, y, (i+resourceIdx));
                            instances.add(instance);
//                            System.out.println("Component "+x+" is vulnerable to privilege escalation attack by component "+y+" that can have access to permission "+(i+resourceIdx));
                        }
                    }
                }
            }
            }
        }
        return instances;
    }
    private static boolean checkUse(int prmCode){
        return (prmCode==1 || prmCode==3 || prmCode==6 || prmCode==8);
    }    
    private static boolean checkHas(int prmCode){
        return (prmCode==2 || prmCode==7);
    }
    private static boolean checkEnforce(int prmCode){
        return (prmCode>=5);
    }
    
    private static int[] getRow(int[][] matrix, int row, int startCol, int endCol){
        int[] r = new int[endCol-startCol+1];
        if (startCol>endCol || matrix.length<row){
            throw new IllegalStateException("Illegal aruments");  
        }
        int idx=0;
        for (int j=0; j<(endCol-startCol+1); j++){
            r[idx++]=matrix[row][startCol+j];
        }
        return r;
    }
    private static void printArray(int[] arr){
        for (int i=0; i<arr.length; i++){
        System.out.print(arr[i]+" ");
    }
        System.out.println();
    }
}
