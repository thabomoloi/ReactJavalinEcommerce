package com.oasisnourish.models.products;

import java.util.Objects;

public class ProductImage {

    private int id;
    private String url;
    private int number;
    private int productId;

    public ProductImage() {
        this(0, "", 0, 0);
    }

    public ProductImage(int id, String url, int number, int productId) {
        this.id = id;
        this.url = url;
        this.number = number;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductImage image = (ProductImage) obj;
        return id == image.id
                && Objects.equals(url, image.url)
                && number == image.number
                && productId == image.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, number, productId);
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
