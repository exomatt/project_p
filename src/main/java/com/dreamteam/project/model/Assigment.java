package com.dreamteam.project.model;

import javax.persistence.*;

@Entity
@Table(name = "ASSIGMENT")
public class Assigment {
    @Id
    @SequenceGenerator(name = "assigment_seq", sequenceName = "assigment_id_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "assigment_seq")
    @Column(name = "ASSIGMENT_ID", nullable = false)
    private Long assigmentId;
    @OneToOne
    @JoinColumn(name = "USER_ID")
    private User user;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    public Assigment (){}
    public Assigment(User user, Role role, Project project) {
        this.user = user;
        this.role = role;
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public Project getProject() {
        return project;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "Assigment{" +
                "user=" + user +
                ", role=" + role +
                '}';
    }
}
