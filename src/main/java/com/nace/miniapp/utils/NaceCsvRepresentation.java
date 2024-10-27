package com.nace.miniapp.utils;


import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NaceCsvRepresentation {

    @CsvBindByName(column = "Order")
    private long order;

    @CsvBindByName(column = "Level")
    private int level;

    @CsvBindByName(column = "Code")
    private String code;

    @CsvBindByName(column = "Parent")
    private String parent;

    @CsvBindByName(column = "Description")
    private String description;

    @CsvBindByName(column = "This item includes")
    private String including;

    @CsvBindByName(column = "This item also includes")
    private String includingMore;

    @CsvBindByName(column = "Rulings")
    private String rulings;

    @CsvBindByName(column = "This item excludes")
    private String excluding;

    @CsvBindByName(column = "Reference to ISIC Rev. 4")
    private String reference;

}
