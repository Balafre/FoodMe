package com.foodme.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "default_option")
public class DefaultOption extends BaseOption {
}
