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
    public String displayInfo() {
        return "Supplier #" + getId() + "\n"
                + "Company: " + companyName + "\n"
                + "Contact person: " + getName() + "\n"
                + "Phone: " + orDash(getPhone()) + "\n"
                + "Email: " + orDash(getEmail()) + "\n"
                + "Address: " + orDash(getAddress());
    }

    /** Overrides Person.toString so combo boxes show the company name. */
    @Override
    public String toString() { return companyName; }
}
