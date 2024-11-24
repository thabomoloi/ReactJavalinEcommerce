package com.oasisnourish.dto.products;

public class ProductInputDto {

    private int id;
    private String name;
    private String description;
    private String ribbon;
    private String code;

    public ProductInputDto() {

    }

    public ProductInputDto(int id, String code, String name, String description, String ribbon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ribbon = ribbon;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRibbon() {
        return ribbon;
    }

    public void setRibbon(String ribbon) {
        this.ribbon = ribbon;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
