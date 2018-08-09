package ru.monitor.MiningClient.model;
import java.time.LocalDateTime;

public class MonitoringInfo {


    private Long id;
    private Miner miner;
    private LocalDateTime createdTime;
    private InfoType type;
    private String message;

    public MonitoringInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Miner getMiner() {
        return miner;
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public InfoType getType() {
        return type;
    }

    public void setType(InfoType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
