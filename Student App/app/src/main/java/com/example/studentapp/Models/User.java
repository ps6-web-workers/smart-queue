package com.example.studentapp.Models;

public class User {
    private String loggin;
    private int abonnement;
    private String firstName;
    private String lastName;

    public User(String loggin, int abonnement, String firstName, String lastName) {
        this.loggin = loggin;
        this.abonnement = abonnement;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getLoggin() {
        return loggin;
    }

    public int getAbonnement() {
        return abonnement;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setAbonnement(int abonnement) {
        this.abonnement = abonnement;
    }

    @Override
    public String toString() {
        return "User{" +
                "loggin='" + loggin + '\'' +
                ", abonnement=" + abonnement +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
