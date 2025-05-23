package com.example.linkup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id;

    @Column(name = "username", unique = true)
    String username;

    @Column(name = "password")
    String password;

    @Column(name = "full_name")
    String full_name;

    @Column(name = "created_time")
    Date created_time;

    @Column(name = "updated_time")
    Date updated_time;

    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    Profiles profile;
}
