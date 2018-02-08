package com.foodme.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "opening_hours")
public class OpeningHours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "open_24_7")
    private boolean open24_7;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "opened_daily_timeframe_id")
    private DailyInterval openedDaily;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "opening_hours_id")
    private List<DailySchedule> dailyScheduleList;
}
