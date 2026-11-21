package inventorysystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int id;
    private Integer customerId;      // null = walk-in customer
    private String customerName;
    private LocalDateTime saleDate;
    private final List<SaleDetail> details = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName == null ? "Walk-in" : customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public List<SaleDetail> getDetails() { return details; }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail d : details) {
            total = total.add(d.getSubtotal());
        }
        return total;
    }
}
