package com.example.myjobportalapplication.data_Model;

import java.util.HashMap;
import java.util.Map;

public class Data {
    String title;
    String skills;
    String description;
    String salary;
    String id;
    String date;
    String recruiterID;
    public Data(){

    }

    public Data(String title, String skills, String description, String salary, String id, String date, String recruiterID) {
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.salary = salary;
        this.id = id;
        this.date = date;
        this.recruiterID = recruiterID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public String getRecruiterID() {
        return recruiterID;
    }
    public void setRecruiterID(String recruiterID) {
        this.recruiterID = recruiterID;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", this.date);
        result.put("description", this.description);
        result.put("id", this.id);
        result.put("recruiterID", this.recruiterID);
        result.put("salary", this.salary);
        result.put("skills", this.skills);
        result.put("title", this.title);

        return result;
    }
}
