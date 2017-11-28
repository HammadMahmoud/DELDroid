/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.seal.deldroid.lp;

import android.content.UriMatcher;
import android.net.Uri;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mahmoud
 */
public class Test {
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    
    public static void main(String[] args){
        Pattern p = Pattern.compile("PushServiceStart(.*)");
        Matcher m = p.matcher("PushServiceStart125455");
        System.out.println(m.matches());
        
        int componentId = 1;
        sURIMatcher.addURI("contacts", null,componentId);
        Uri uri = Uri.parse("content://contacts/phones");
        int matchedId = sURIMatcher.match(uri);
        System.out.println(matchedId);
        
    }
            
    
}
