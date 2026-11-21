package inventorysystem.model;

public class Supplier extends Person {
    private String companyName;

    public Supplier() { }

    public Supplier(int id, String companyName, String contactName, String phone, String email, String address) {
        super(id, contactName, phone, email, address);
        this.companyName = companyName;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    @Override
    public String getRole() { return "Supplier"; }

    /** Overrides Person.toString so combo boxes show the company name. */
    @Override
    public String toString() { return companyName; }
}
