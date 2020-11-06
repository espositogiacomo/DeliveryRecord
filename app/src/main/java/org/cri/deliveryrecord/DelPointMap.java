package org.cri.deliveryrecord;


import android.text.format.Time;

public class DelPointMap {
    int _id;
    String _deliveryPoint;
    String _note;
    public DelPointMap(){   }
    public DelPointMap(int id, String deliveryPoint, String note){
        this._id = id;
        this._deliveryPoint = deliveryPoint;
        this._note = note;
    }

    public DelPointMap(int id, String deliveryPoint){
        this._id = id;
        this._deliveryPoint = deliveryPoint;
    }


    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getDeliveryPoint(){
        return this._deliveryPoint;
    }

    public void setDeliveryPoint(String deliveryPoint){
        this._deliveryPoint = deliveryPoint;
    }

    public String getNote(){
        return this._note;
    }

    public void setNote(String note){
        this._note = note;
    }
}
