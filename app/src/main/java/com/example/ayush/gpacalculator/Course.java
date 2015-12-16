package com.example.ayush.gpacalculator;

/**
 * Created by Ayush on 11/20/2015.
 */


import java.io.Serializable;


public class Course implements Serializable {
    private final String TAG = "Course";


    private String courseCode;
    private double gpaWeight;
    private double gpaValue;
    private boolean creditNoCredit;

    public Course(String courseCode, double gpaWeight, double gpaValue, boolean creditNoCredit) {
        this.courseCode = courseCode;
        this.gpaWeight = gpaWeight;
        this.gpaValue = gpaValue;
        this.creditNoCredit = creditNoCredit;

    }

    public boolean isCreditNoCredit() {
        return creditNoCredit;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public double getGpaWeight() {
        return gpaWeight;
    }

    public double getGpaValue() {
        return gpaValue;
    }
}
