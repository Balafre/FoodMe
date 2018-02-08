package com.foodme.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "daily_interval")
public class DailyInterval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false, columnDefinition = "BIGINT")
    private Long id;

    @Column(length = 10)
    private String fromTime;

    @Column(length = 10)
    private String tillTime;

    public DailyInterval(){}

    public DailyInterval(String fromTime, String tillTime) {
        this.fromTime = fromTime;
        this.tillTime = tillTime;
    }
}

