package com.example.expensemanager;

public class Model {
    String date, cost, description, sub_category, main_category, secret_uuid;

    public Model(){
    }

    public Model(String date, String cost, String description, String sub_category, String main_category, String secret_uuid) {
        this.date = date;
        this.cost = cost;
        this.description = description;
        this.sub_category = sub_category;
        this.main_category = main_category;
        this.secret_uuid = secret_uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getMain_category() {
        return main_category;
    }

    public void setMain_category(String main_category) {
        this.main_category = main_category;
    }

    public String getSecret_uuid() {
        return secret_uuid;
    }

    public void setSecret_uuid(String secret_uuid) {
        this.secret_uuid = secret_uuid;
    }
}
