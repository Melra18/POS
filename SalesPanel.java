/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.pos.ui;

import com.pos.config.ConnectDb;
import com.pos.model.Product;
import com.pos.model.ProductSorter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SalesPanel extends javax.swing.JPanel {

    private static final String[] SORT_OPTIONS = {
        "Default", "Price: Low to High", "Price: High to Low", "Name: A-Z", "Stock: Low to High"
    };

    private Dashboard parentDashboard;
    private ProductPanel filterPanel;
    private JComboBox<String> cbSort;

    // ArrayList: lahat ng products sa memory
    private final List<Product> allProducts = new ArrayList<>();
    private int lastCategoryId = 0;
    private String lastSearch = "";

    public SalesPanel(Dashboard parentDashboard) {
        initComponents();
        this.parentDashboard = parentDashboard;
        pnlProductList = new ScrollablePanel();
        jScrollPane1.setViewportView(pnlProductList);
        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        pnlProductList.setLayout(new WrapLayout(WrapLayout.CENTER, 12, 12));
        
        
        pnlProductList.setBackground(Color.WHITE);

        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.getViewport().setBackground(Color.WHITE);

        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        jScrollPane1.getVerticalScrollBar().setBlockIncrement(80);

this.setBackground(Color.WHITE);  
        filterPanel = new ProductPanel();
        filterPanel.setFilterListener((categoryId, searchText) -> {
            lastCategoryId = categoryId;
            lastSearch = (searchText == null) ? "" : searchText;
            applyFilters();
        });

        cbSort = new JComboBox<>(SORT_OPTIONS);
        cbSort.addActionListener(e -> applyFilters());

        JLabel lblSort = new JLabel("Sort:");
        lblSort.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 18));
        sortPanel.setBackground(Color.WHITE);
        sortPanel.add(lblSort);
        sortPanel.add(cbSort);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(filterPanel, BorderLayout.CENTER);
        northPanel.add(sortPanel, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(northPanel, BorderLayout.NORTH);
        this.add(jScrollPane1, BorderLayout.CENTER);

        loadFromDatabase();
        applyFilters();
    }

    public void refreshProducts() {
        loadFromDatabase();
        filterPanel.reloadCategories();
        applyFilters();
    }

    private void loadFromDatabase() {
        allProducts.clear();
        String sql = "SELECT product_id, product_name, unit_price, stock_quantity, "
                   + "category_id, supplier_id, image_path FROM products WHERE is_archived = 0";

        try (Connection conn = ConnectDb.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                try {
                    allProducts.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("category_id"),
                        rs.getInt("supplier_id"),
                        rs.getString("image_path")
                    ));
                } catch (IllegalArgumentException bad) {
                    System.err.println("Skipped invalid product row: " + bad.getMessage());
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    // Filter (linear search) -> Sort (insertion sort) -> Render
    private void applyFilters() {
        List<Product> view = new ArrayList<>();
        String keyword = lastSearch.trim().toLowerCase();

        for (Product p : allProducts) {
            boolean categoryOk = (lastCategoryId == 0) || (p.getCategoryId() == lastCategoryId);
            boolean nameOk = keyword.isEmpty() || p.getProductName().toLowerCase().contains(keyword);
            if (categoryOk && nameOk) {
                view.add(p);
            }
        }

        sortView(view);

        pnlProductList.removeAll();
        for (Product p : view) {
            pnlProductList.add(createProductCard(p));
        }
        pnlProductList.revalidate();
        pnlProductList.repaint();
    }

    private void sortView(List<Product> view) {
        switch (cbSort.getSelectedIndex()) {
            case 1:
                ProductSorter.insertionSort(view, Comparator.comparingDouble(Product::getUnitPrice));
                break;
            case 2:
                ProductSorter.insertionSort(view, Comparator.comparingDouble(Product::getUnitPrice).reversed());
                break;
            case 3:
                ProductSorter.insertionSort(view, (a, b) -> a.getProductName().compareToIgnoreCase(b.getProductName()));
                break;
            case 4:
                ProductSorter.insertionSort(view, Comparator.comparingInt(Product::getStockQuantity));
                break;
            default:
                break;
        }
    }

    private static class ScrollablePanel extends JPanel implements Scrollable {
    @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
    @Override public int getScrollableUnitIncrement(Rectangle r, int o, int d) { return 16; }
    @Override public int getScrollableBlockIncrement(Rectangle r, int o, int d) { return 80; }
    @Override public boolean getScrollableTracksViewportWidth() { return true; }
    @Override public boolean getScrollableTracksViewportHeight() { return false; }
}
    
    
    private void setCardBorder(JPanel card, Color color) {
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(9, 9, 9, 9)
        ));
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        setCardBorder(card, new Color(220, 220, 220));
        card.setPreferredSize(new Dimension(200, 205));
        card.setMinimumSize(new Dimension(200, 205));

        JLabel iconLabel = new JLabel("🧴", SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(245, 245, 250));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setPreferredSize(new Dimension(178, 90));
        iconLabel.setMinimumSize(new Dimension(178, 90));

        JLabel nameLabel = new JLabel(
            "<html><center>" + product.getProductName() + "</center></html>", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(new Color(30, 30, 30));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        JLabel priceLabel = new JLabel("₱" + df.format(product.getUnitPrice()), SwingConstants.CENTER);
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        priceLabel.setForeground(new Color(0, 102, 204));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel stockLabel = new JLabel(
            product.isInStock() ? "Stock: " + product.getStockQuantity() : "Out of Stock",
            SwingConstants.CENTER);
        stockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stockLabel.setForeground(product.isInStock() ? new Color(100, 100, 100) : Color.RED);
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(priceLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(stockLabel);

        if (product.isInStock()) {
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    parentDashboard.addProductToCart(product);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    setCardBorder(card, new Color(0, 102, 204));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    setCardBorder(card, new Color(220, 220, 220));
                }
            });
        }
        return card;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        pnlProductList = new javax.swing.JPanel();

        setBackground(new java.awt.Color(255, 255, 255));

        jScrollPane1.setViewportView(pnlProductList);

        pnlProductList.setBackground(new java.awt.Color(255, 255, 255));
        pnlProductList.setForeground(new java.awt.Color(255, 255, 255));
        pnlProductList.setAutoscrolls(true);
        jScrollPane1.setViewportView(pnlProductList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 966, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 839, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlProductList;
    // End of variables declaration//GEN-END:variables
}
