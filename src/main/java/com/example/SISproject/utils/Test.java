package com.example.SISproject.utils;

public class Test {
    public static void main(String[] args) {
        Database database = new Database();
        System.out.println(database.getUrl());
        System.out.println(database.getUsername());
        System.out.println(database.getPassword());
    } // IT WORKS!!!
}
