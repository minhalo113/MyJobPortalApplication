package com.example.myjobportalapplication.data_Model;

public class applicantData {
    String applicantName;
    String applicantAge;

    public applicantData(){

    }

    public applicantData(String applicantName, String applicantAge) {
        this.applicantName = applicantName;
        this.applicantAge = applicantAge;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantAge() {
        return applicantAge;
    }

    public void setApplicantAge(String applicantAge) {
        this.applicantAge = applicantAge;
    }
}
