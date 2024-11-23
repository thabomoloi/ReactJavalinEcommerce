package com.oasisnourish.models.products;

import java.util.Objects;

public class Product {

    private int id;
    private String name;
    private String description;
    private String ribbon;
    private String code;

    public Product() {
        this(0, "0000", "", "", "");
    }

    public Product(int id, String code, String name, String description, String ribbon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ribbon = ribbon;
        this.code = code;
    }

     @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Product product = (Product) obj;
        return id == product.id
                && Objects.equals(name, product.name) 
                && Objects.equals(code, product.code)
                && Objects.equals(description, product.description)
                && Objects.equals(ribbon, ribbon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, name, description, ribbon);
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
