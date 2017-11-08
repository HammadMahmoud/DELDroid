/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Mahmoud
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id","actions", "categories", "data", "pathData"})
public class IntentFilter {
    @XmlElement(name = "ifID")
        int id;
        @XmlElementWrapper(name = "actions")
        @XmlElement(name = "action")
    	List<String> actions;
        @XmlElementWrapper(name = "categories")
        @XmlElement(name = "category")
        List<String> categories;
        @XmlElementWrapper(name = "data")
        @XmlElement(name = "data_item")
	List<Data> data;	
        @XmlElement(name = "dataPath")
	String pathData;
        @XmlTransient
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
