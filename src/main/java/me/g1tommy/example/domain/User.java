package me.g1tommy.example.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
public class User {

    @Id
    private Long id;

    @Column
    private String username;

    @Column
    private String firstname;

    @Column
    private String lastname;

}
