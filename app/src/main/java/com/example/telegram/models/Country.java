package com.example.telegram.models;

public class Country {
    int id;
    String country_flag, country_name, country_code;

    public Country(int id, String country_flag, String country_name, String country_code) {
        this.id = id;
        this.country_flag = country_flag;
        this.country_name = country_name;
        this.country_code = country_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry_flag() {
        return country_flag;
    }

    public void setCountry_flag(String country_flag) {
        this.country_flag = country_flag;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }
}
