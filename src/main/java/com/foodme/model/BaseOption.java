package com.foodme.model;

import com.foodme.enumeration.OptionType;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Data
@MappedSuperclass
public class BaseOption extends BaseEntity {

    @Length(max = 50)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private OptionType optionType;
}
