/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsm;

import model.Component;

/**
 *
 * @author Mahmoud
 */
public class IntentSpoofing {
    
    private Component sComponent;
    private Component rComponent;
    private Component xComponent;   
    
    public IntentSpoofing(Component s, Component r, Component x){
        this.sComponent = s;
        this.rComponent = r;
        this.xComponent = x;        
    }
    @Override
    public boolean equals(Object o){
        if (!(o instanceof IntentSpoofing))
            return false;
        
        IntentSpoofing that = (IntentSpoofing) o;
        if (this.sComponent.equals(that.sComponent) && this.rComponent.equals(that.rComponent) && this.xComponent==that.xComponent){
            return true;
        }        
        return false;        
    }
    @Override
    public String toString(){
        String msg = this.sComponent.getName()+" -> "+this.rComponent.getName()+"(X:"+this.xComponent.getName()+")\n" ;
        return msg;
 
    }

    public Component getsComponent() {
        return sComponent;
    }

    public void setsComponent(Component sComponent) {
        this.sComponent = sComponent;
    }

    public Component getrComponent() {
        return rComponent;
    }

    public void setrComponent(Component rComponent) {
        this.rComponent = rComponent;
    }

    public Component getxComponent() {
        return xComponent;
    }

    public void setxComponent(Component xComponent) {
        this.xComponent = xComponent;
    }
    
    
}
