package ru.monitor.MiningClient.model;


import java.time.LocalDateTime;

public class Miner {

    private Long id;

    private String minerName;

    private String parameters;

    private LocalDateTime createdTime;
    private LocalDateTime lastUpdatedTime;

    private Worker worker;

    private LocalDateTime lastSelfCheck;

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
}
