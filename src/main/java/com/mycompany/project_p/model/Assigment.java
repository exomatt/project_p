package com.mycompany.project_p.model;

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
