package org.cri.deliveryrecord;


import android.text.format.Time;

public class Guest {
    int _id;
    String _name;
    String _delivery_time;
    public Guest(){   }
    public Guest(int id, String name, String _deliveryTime){
        this._id = id;
        this._name = name;
        this._delivery_time = _deliveryTime;
    }

    @Override
    public String toString() {

        return ""+_id;
    }

    public Guest(int id, String _deliveryTime){
        this._id = id;
        this._delivery_time = _deliveryTime;
    }


    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getDeliveryTime(){
        return this._delivery_time;
    }

    public void setDeliveryTime(String delivery_time){
        this._delivery_time = delivery_time;
    }
}
