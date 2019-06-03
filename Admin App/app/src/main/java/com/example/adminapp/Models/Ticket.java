package com.example.studentapp.Models;

public class Ticket {
    private int ticketId;
    private String userLogin;
    private String userFirstName;
    private String userLastName;

    public Ticket(int ticketId, String userLogin, String userFirstName, String userLastName) {
        this.ticketId = ticketId;
        this.userLogin = userLogin;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
    }

    public int getTicketId() {
        return ticketId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    @Override
    public String toString() {
        return "Ticket " + ticketId + " {" +
                "userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                '}';
    }
}
