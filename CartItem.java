package com.pos.model;

public class CartItem {
    private int productId;
    private String productName;
    private String unitType; // "Piece" or "Bulk"
    private int quantity;
    private double unitPrice;
    private int piecesPerBulk;

    public CartItem(int productId, String productName, String unitType, int quantity, double unitPrice, int piecesPerBulk) {
        this.productId = productId;
        this.productName = productName;
        this.unitType = unitType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.piecesPerBulk = piecesPerBulk;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getUnitType() { return unitType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
    
    // Calculates total subtotal for this cart row
    public double getSubtotal() {
        return this.quantity * this.unitPrice;
    }

    // Calculates total individual pieces to deduct from inventory
    public int getTotalPiecesToDeduct() {
        if ("Bulk".equalsIgnoreCase(unitType)) {
            return this.quantity * this.piecesPerBulk;
        }
        return this.quantity;
    }
}