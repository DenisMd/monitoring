package ru.monitor.Mining.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "miner")
public class Miner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "miner_name")
    private String minerName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String parameters;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "last_updated_time")
    private LocalDateTime lastUpdatedTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "worker_id", referencedColumnName = "id")
    @JsonIgnore
    private Worker worker;

    @Column(name = "last_self_check")
    private LocalDateTime lastSelfCheck;

    @Column(name = "active")
    private boolean active;

    public Miner() {
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLastSelfCheck() {
        return lastSelfCheck;
    }

    public void setLastSelfCheck(LocalDateTime lastSelfCheck) {
        this.lastSelfCheck = lastSelfCheck;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMinerName() {
        return minerName;
    }

    public void setMinerName(String minerName) {
        this.minerName = minerName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String printSign() {
        return "⛏ " + worker.getUserName() + "("+getMinerName()+")";
    }
}
