package org.freightfox.dynamicpdfgenerator.dto;

import lombok.Data;

@Data
public class Item {
    private String name;
    private String quantity;
    private double rate;
    private double amount;
}
