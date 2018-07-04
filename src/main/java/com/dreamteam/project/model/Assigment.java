package com.dreamteam.project.model;

import javax.persistence.OneToOne;

public class Assigment {
    private Long assigmentId;
    @OneToOne
    private User user;
    @OneToOne
    private Role role;
    @OneToOne
    private Project project;
}
