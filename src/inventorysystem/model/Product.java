package inventorysystem.model;

import java.math.BigDecimal;

public class Product {
    private int id;
    private String code;
    private String name;
    private int categoryId;
    private String categoryName;
    private int supplierId;
    private String supplierName;
    private BigDecimal price = BigDecimal.ZERO;
    private int stockQuantity;
    private int minStockLevel = 10;

    public Product() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    /**
     * Changes the stock by the given amount (negative = sold, positive = restocked).
     * Refuses to let the stock go below zero.
     */
    public void updateStock(int change) {
        int newQuantity = stockQuantity + change;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Not enough stock: only " + stockQuantity + " left.");
        }
        stockQuantity = newQuantity;
    }

    public boolean isLowStock() { return stockQuantity <= minStockLevel; }

    /** OUT, LOW or OK - used by the Inventory screen. */
    public String getStockStatus() {
        if (stockQuantity == 0) return "OUT OF STOCK";
        return isLowStock() ? "LOW STOCK" : "OK";
    }

    @Override
    public String toString() { return code + " - " + name; }
}
