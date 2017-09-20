package com.innocv.helloworld.model;

import com.google.gson.annotations.SerializedName;

/**
 *
 * User bean
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class User {

    @SerializedName("id")
    int _Id;

    @SerializedName("name")
    String _Name;

    @SerializedName("birthdate")
    String _Birthdate;

    public User(int _Id, String _Name, String _Birthdate) {
        this._Id = _Id;
        this._Name = _Name;
        this._Birthdate = _Birthdate;
    }

    public int getId() {
        return _Id;
    }

    public void setId(int Id) {
        _Id = Id;
    }

    public String getName() {
        return _Name;
    }

    public void setName(String Name) {
        _Name = Name;
    }

    public String getBirthdate() {
        return _Birthdate;
    }

    public void setBirthdate(String Birthdate) {
        _Birthdate = Birthdate;
    }
}
