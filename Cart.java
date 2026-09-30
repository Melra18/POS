package com.pos.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {

    private static final double VAT_RATE = 0.12;

    // Keeps products in the order they were added
    private final Map<Integer, CartItem> items = new LinkedHashMap<>();

    // Used for Ctrl + Z
    private final Deque<Integer> undoStack = new ArrayDeque<>();

    // Default: no discount
    private Discount discount = new NoDiscount();

    /**
     * Add one quantity of a product to the cart.
     */
    public boolean add(Product product) {

        if (product == null) {
            return false;
        }

        int productId = product.getProductId();

        CartItem existingItem = items.get(productId);

        int currentQuantity = 0;

        if (existingItem != null) {
            currentQuantity = existingItem.getQuantity();
        }

        // Don't allow the cart quantity to exceed available stock
        if (currentQuantity + 1 > product.getStockQuantity()) {
            return false;
        }

        // Product is not yet in cart
        if (existingItem == null) {

            CartItem newItem = new CartItem(product, 1);

            items.put(productId, newItem);

        } else {

            // Product already exists, increase quantity
            existingItem.setQuantity(currentQuantity + 1);
        }

        // Remember this action for Ctrl + Z
        undoStack.push(productId);

        return true;
    }

    /**
     * Undo the most recent ADD action.
     */
    public boolean undoLast() {

        if (undoStack.isEmpty()) {
            return false;
        }

        int productId = undoStack.pop();

        CartItem item = items.get(productId);

        // Safety check
        if (item == null) {
            return false;
        }

        if (item.getQuantity() <= 1) {

            // Remove product completely
            items.remove(productId);

        } else {

            // Decrease quantity by 1
            item.setQuantity(item.getQuantity() - 1);
        }

        return true;
    }

    /**
     * Remove an item based on its row index in the cart table.
     */
    public boolean remove(int index) {

        if (index < 0 || index >= items.size()) {
            return false;
        }

        Integer[] productIds = items.keySet().toArray(new Integer[0]);

        int productId = productIds[index];

        items.remove(productId);

        // Remove this product from undo history
        undoStack.removeIf(id -> id.equals(productId));

        return true;
    }

    /**
     * Completely empty the cart.
     */
    public void clear() {

        items.clear();
        undoStack.clear();

        // Reset discount
        discount = new NoDiscount();
    }

    /**
     * Set the current discount.
     */
    public void setDiscount(Discount discount) {

        if (discount == null) {
            this.discount = new NoDiscount();
        } else {
            this.discount = discount;
        }
    }

    /**
     * Get current discount.
     */
    public Discount getDiscount() {
        return discount;
    }

    /**
     * Get all cart items.
     */
    public Collection<CartItem> getItems() {
        return items.values();
    }

    /**
     * Check if cart is empty.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Calculate subtotal.
     */
    public double getSubtotal() {

        double subtotal = 0;

        for (CartItem item : items.values()) {
            subtotal += item.getSubtotal();
        }

        return subtotal;
    }

    /**
     * Calculate discount amount.
     */
    public double getDiscountAmount() {

        return discount.compute(getSubtotal());
    }

    /**
     * Calculate VAT.
     */
    public double getVat() {

        return getSubtotal() * VAT_RATE;
    }

    /**
     * Calculate final total.
     */
    public double getTotal() {

        return getSubtotal()
                - getDiscountAmount()
                + getVat();
    }
}