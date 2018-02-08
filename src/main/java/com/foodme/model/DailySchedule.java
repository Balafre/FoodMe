package com.foodme.model;

import com.foodme.enumeration.DayOfWeek;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "daily_schedule")
public class DailySchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "day_of_week")
    @Enumerated(value = EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<DailyInterval> timeFrames = new ArrayList<>();

    public DailySchedule() {
    }

    public DailySchedule(DayOfWeek dayOfWeek, List<DailyInterval> timeFrames) {
        this.dayOfWeek = dayOfWeek;
        this.timeFrames.addAll(timeFrames);
    }

    public DailySchedule(DayOfWeek dayOfWeek, DailyInterval timeFrame) {
        this.dayOfWeek = dayOfWeek;
        this.timeFrames.add(timeFrame);
    }


}
