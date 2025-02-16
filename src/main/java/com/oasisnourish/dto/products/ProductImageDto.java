package com.oasisnourish.dto.products;

public class ProductImageDto {

    private int id;
    private String url;
    private int number;
    private int productId;

    public ProductImageDto() {
    }

    public ProductImageDto(int id, String url, int number, int productId) {
        this.id = id;
        this.url = url;
        this.number = number;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
