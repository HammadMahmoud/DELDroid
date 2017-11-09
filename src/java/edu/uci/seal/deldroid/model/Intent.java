/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import dsm.LPDetermination;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mahmoud
 */
public class Intent {
    private static int LAST_INTENT_ID;
    
    
    private int intentId;
    private int senderComponentId;
    private String packageName;
    private String sender;
    private String receiverPackage;
    private String receiver; //receiver class
    private String receiverType;
    private String action;
    private String sensitiveData;
    private List<String> categories;
    private boolean sysIntent;
    private String extra;
    
    private Data data;

//    //we don't need these instance variables, they have been replaced by Data instance variable
//    private String dataType; //mimeType
//    //URI: <scheme>://<host>:<port>/<path>
//    private String uri;
//    private String scheme;
//    private String host;
//    private String port;
//    private String path;    
//    private String ssp; //URI without the scheme part
//    private String authority;
//    private String query;
    
    

    
    public Intent() {
        this.intentId=++LAST_INTENT_ID;            
        this.categories=new ArrayList<>();
        this.sysIntent=false;
        this.senderComponentId=-1;
        this.sysIntent=false;
    }
    
    //create explicit intent
    public Intent(int intentId, int senderComponentId, String packageName, String sender, String receiver, String receiverType, String action, String receiverPackage){
        LAST_INTENT_ID = Math.max(LAST_INTENT_ID, intentId);
        this.senderComponentId=senderComponentId;
        this.categories=new ArrayList<>();
        this.sysIntent=false;

        this.intentId=intentId;
        this.packageName=packageName;
        this.sender=sender;
        this.receiver=receiver;
        this.receiverType=receiverType;
        this.action = action;
        this.receiverPackage=receiverPackage;
        
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getReceiverPackage() {
        return receiverPackage;
    }

    public void setReceiverPackage(String receiverPackage) {
        this.receiverPackage = receiverPackage;
    }

    public int getSenderComponentId() {
        return senderComponentId;
    }

    public void setSenderComponentId(int senderComponentId) {
        this.senderComponentId = senderComponentId;
    }

    public static int getLAST_INTENT_ID() {
        return LAST_INTENT_ID;
    }

    public static void setLAST_INTENT_ID(int LAST_INTENT_ID) {
        Intent.LAST_INTENT_ID = LAST_INTENT_ID;
    }

//    public String getSsp() {
//        return ssp;
//    }
//
//    public void setSsp(String ssp) {
//        this.ssp = ssp;
//    }
//
//    public String getAuthority() {
//        return authority;
//    }
//
//    public String getQuery() {
//        return query;
//    }
//
//    public void setQuery(String query) {
//        this.query = query;
//    }
//
//    public void setAuthority(String authority) {
//        this.authority = authority;
//    }

//    public String getUri() {
//        return uri;
//    }

//    public void setUri(String uri) {
//        try{
//        if (uri==null) return;
//        this.uri = uri;
//        //URI: <scheme>://<host>:<port>/<path>
//        String[] arr = uri.split(":");
//        this.scheme=arr[0];
//        // those elements independent but have a linear dependencies
//        if (this.scheme==null) return;
//        
//        if (arr.length>1){
//            this.host=arr[1];
//        }
//        
//        if (this.host==null) return;
//        
//        if (arr.length>2){
//            String r = arr[2];
//            int idx = arr[2].indexOf("/");
//            if (idx!=-1){
//                this.port=arr[2].substring(0, idx);
//                this.path=arr[2].substring(idx+1);
//            }
//        }
//        }catch(Exception e){
//            System.out.println("******** "+uri);
//            e.printStackTrace();
//            System.exit(0);
//        }        
//    }

//    public String getHost() {
//        return host;
//    }
//
//    public void setHost(String host) {
//        this.host = host;
//    }
//
//    public String getPort() {
//        return port;
//    }
//
//    public void setPort(String port) {
//        this.port = port;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
    
//    public String getDataType() {
//        return dataType;
//    }
//
//    public void setDataType(String dataType) {
//        this.dataType = dataType;
//    }
//
//    public String getScheme() {
//        return scheme;
//    }
//
//    public void setScheme(String scheme) {
//        this.scheme = scheme;
//    }
    

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public int getIntentId() {
        return intentId;
    }

    public void setIntentId(int intentId) {
        this.intentId = intentId;
    }

    
    public boolean isSysIntent() {
        return sysIntent;
    }

    public void setSysIntent(boolean sysIntent) {
        this.sysIntent = sysIntent;
    }
    

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

//
    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getSensitiveData() {
        return sensitiveData;
    }

    public void setSensitiveData(String sensitiveData) {
        this.sensitiveData = sensitiveData;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        
        this.categories = categories;
    }
    public void addCategory(String category){
        this.categories.add(category);
    }
    
    @Override
    public String toString(){
        String sep=LPDetermination.sep;
        if (this.receiver != null)
            return this.sender+sep+this.receiver;
        else
            return this.sender+sep+this.action+sep+this.categories.toString().replace(",", ";")+sep+this.data;
    }
    
    
}
