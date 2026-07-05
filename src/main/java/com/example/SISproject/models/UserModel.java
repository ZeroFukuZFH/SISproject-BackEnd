package com.example.SISproject.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private int id;
    private String name;
    private String email;
    private String password;
    private String activityStatus;
}