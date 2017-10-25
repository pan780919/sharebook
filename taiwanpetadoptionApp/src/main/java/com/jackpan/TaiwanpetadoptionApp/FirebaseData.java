package com.jackpan.TaiwanpetadoptionApp;

import android.util.Log;

import java.text.Normalizer;
import java.util.List;

/**
 * Created by HYXEN20141227 on 2016/12/21.
 */

public class FirebaseData {
    public  String id;
    public  String tittle;
    public  String message;
    public  String pic;
    public  String pic2;
    public  String pic3;
//    public  String pic4;
//    public  String pic5;
    public String name;
    public String date;
    public String url;
//    public String url2;
//    public String url3;
    public String adds;
//    public  Double lat;
//    public  Double lon;
    public  int   like = 1;
    public  String tomsg;
    public  int   view =1;
//    public  String mood;
    public  String cat;
    public  String phone;
    public  String price;
    public  String isbn;
//    List<ForMsg> ForMsg;


    public int getView() {
        return view;
    }

    public int getLike() {
        return like;
    }

    private static final String TAG = "FirebaseData";

    public void setId(String id) {
        this.id = id;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getPic() {
        return pic;
    }

    public String getTittle() {
        return tittle;
    }


}

