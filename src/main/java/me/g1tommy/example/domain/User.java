package me.g1tommy.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
@AllArgsConstructor
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
