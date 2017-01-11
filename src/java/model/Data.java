/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Mahmoud
 */
public class Data {
    //Those instance variables are shared between IF and Intent
	private String scheme;
	private String mimeType;
	private String host;
        private String port;
        private String subtype;

        //instances variables assigned to Intents only
    private String uri;
    private String path;    
    private String ssp; //URI without the scheme part
    private String authority;
    private String query;
        
        
        
        
    @Override
    public String toString(){
        String sep="|";
        return "(D)"+sep+this.scheme+sep+this.mimeType+sep+this.host+sep+this.path+sep+this.port+sep+
                this.subtype+sep+uri+sep;
        //(D)|scheme|mimeType|host|path|port|subType|uri
    }
        
    public Data(){
        
    }
    public Data(String scheme, String mimeType){
        this.scheme = scheme;
        this.mimeType = mimeType;
    }
    public Data(String scheme, String mimeType, String host, String pathpattern, String port, String subtype){
        this.scheme = scheme;
        this.mimeType = mimeType;
        this.host=host;
        this.path=pathpattern;
        this.port=port;
        this.subtype=subtype;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        try{
        if (uri==null) return;
        this.uri = uri;
        //URI: <scheme>://<host>:<port>/<path>
        String[] arr = uri.split(":");
        this.scheme=arr[0];
        // those elements independent but have a linear dependencies
        if (this.scheme==null) return;
        
        if (arr.length>1){
            this.host=arr[1];
        }
        
        if (this.host==null) return;
        
        if (arr.length>2){
            String r = arr[2];
            int idx = arr[2].indexOf("/");
            if (idx!=-1){
                this.port=arr[2].substring(0, idx);
                this.path=arr[2].substring(idx+1);
            }
        }
        }catch(Exception e){
            System.out.println("******** "+uri);
            e.printStackTrace();
            System.exit(0);
        }        
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSsp() {
        return ssp;
    }

    public void setSsp(String ssp) {
        this.ssp = ssp;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
        
    
    
    @Override
    public boolean equals(Object o){
        if (!(o instanceof Data))
            return false;
        Data d = (Data) o;
        if (this.scheme ==null && d.scheme!= null) return false;
        if (this.mimeType ==null && d.mimeType!= null) return false;
        return this.scheme.equals(d.scheme) && this.mimeType.equals(d.mimeType);
    }
        
        
        public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
        
}
