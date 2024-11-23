package com.oasisnourish.models.products;

public class ProductImage {

    private int id;
    private String url;
    private int order;
    private int productId;

    public ProductImage() {
        this(0, "", 0, 0);
    }

    public ProductImage(int id, String url, int order, int productId) {
        this.id = id;
        this.url = url;
        this.order = order;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

}
