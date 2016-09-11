package edu.iit.arajago6hawk.carebnb;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by suraj on 9/11/2016.
 */
public class Relief implements Serializable {
    String name;
    Integer quantity;
    Location location;

    Relief(String name, Integer quantity, Location location){
        this.name = name;
        this.quantity = quantity;
        this.location = location;
    }
}