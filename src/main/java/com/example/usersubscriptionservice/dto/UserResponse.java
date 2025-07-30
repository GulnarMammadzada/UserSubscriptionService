package com.example.usersubscriptionservice.dto;

import lombok.Data;

@Data
class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;

    public UserResponse() {
    }
}
