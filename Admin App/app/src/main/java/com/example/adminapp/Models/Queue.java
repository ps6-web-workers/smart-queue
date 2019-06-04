package com.example.adminapp.Models;

import java.util.Collection;

public class Queue {
    private int id;
    private String name;
    private Collection<Ticket> tickets;

    public Queue(int id, String name, Collection tickets) {
        this.id = id;
        this.name = name;
        this.tickets = tickets;
    }

    public Collection getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        return "Queue " + id + " {" +
                "name='" + name + '\'' +
                ", tickets=" + tickets.toString() +
                '}';
    }
}
