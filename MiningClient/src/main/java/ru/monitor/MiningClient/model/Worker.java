package ru.monitor.MiningClient.model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Worker {

    private Long id;

    private String hostName;

    private String userName;

    private LocalDateTime createdTime;

    private LocalDateTime lastUpdatedTime;

    private Long telegramChatId;

    private List<Miner> miners = new ArrayList<>();

    public Worker() {
    }


    public List<Miner> getMiners() {
        return miners;
    }

    public void setMiners(List<Miner> miners) {
        this.miners = miners;
    }

    public Long getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(Long telegramChatId) {
        this.telegramChatId = telegramChatId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
