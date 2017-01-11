/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mahmoud
 */
public class IntentFilter {
        int id;
    	List<String> actions;
	List<Data> data;
	List<String> categories;
	String pathData;
        public static String sep = "/";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
        
        
        
@Override
public String toString(){
    
    return "(IF)"+sep+this.data+sep+pathData+sep+this.actions.toString().replace(",", ";")+sep+
            this.categories.toString().replace(",", ";");
    //(IF)/Data/Path/[actions]/[categories]
    //Data: (D)|scheme|mimeType|host|path|port|subType|uri
    //(IF)/(D)|scheme|mimeType|host|path|port|subType|uri/Path/[actions]/[categories]
}
        
        public IntentFilter(){
            actions = new ArrayList<>();
            data = new ArrayList<>();
            categories = new ArrayList<>();
            pathData = "";            
        }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getPathData() {
        return pathData;
    }

    public void setPathData(String pathData) {
        this.pathData = pathData;
    }

    
}
