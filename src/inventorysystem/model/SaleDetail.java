package inventorysystem.model;

import java.math.BigDecimal;

/** One product line inside a sale (also used as a shopping-cart line). */
public class SaleDetail {
    private int productId;
    private String productCode;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public SaleDetail() { }

    public SaleDetail(int productId, String productCode, String productName, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getProductId() { return productId; }
    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
