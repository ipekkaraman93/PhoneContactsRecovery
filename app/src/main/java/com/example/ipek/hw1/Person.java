package com.example.ipek.hw1;

public class Person {
    private String  name;
    private String  phoneNumber;

    public Person(String name, String phoneNumber) {
        super();
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }
    public String getPhoneNumber() {

        return phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }
}

