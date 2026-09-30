package com.pos.model;

public class Product {
    private int productId;
    private String barcode;
    private String productName;
    private int categoryId;
    private int supplierId;
    private int piecesPerBulk;
    private double pricePerPiece;
    private double pricePerBulk;
    private String status;

    public Product() {}

    public Product(int productId, String barcode, String productName, int categoryId, 
                   int supplierId, int piecesPerBulk, double pricePerPiece, double pricePerBulk, String status) {
        this.productId = productId;
        this.barcode = barcode;
        this.productName = productName;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.piecesPerBulk = piecesPerBulk;
        this.pricePerPiece = pricePerPiece;
        this.pricePerBulk = pricePerBulk;
        this.status = status;
    }

    // Encapsulation (Getters and Setters)
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public int getPiecesPerBulk() { return piecesPerBulk; }
    public void setPiecesPerBulk(int piecesPerBulk) { this.piecesPerBulk = piecesPerBulk; }

    public double getPricePerPiece() { return pricePerPiece; }
    public void setPricePerPiece(double pricePerPiece) { this.pricePerPiece = pricePerPiece; }

    public double getPricePerBulk() { return pricePerBulk; }
    public void setPricePerBulk(double pricePerBulk) { this.pricePerBulk = pricePerBulk; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}