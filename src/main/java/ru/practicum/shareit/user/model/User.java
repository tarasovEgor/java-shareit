package ru.practicum.shareit.user.model;

import lombok.Data;

@Data
public class User {
    private Long id = 0L;
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
