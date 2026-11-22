package inventorysystem.model;

import java.time.LocalDate;

public class Customer extends Person {
    private LocalDate registeredDate;

    public Customer() { }

    public Customer(int id, String name, String phone, String email, String address, LocalDate registeredDate) {
        super(id, name, phone, email, address);
        this.registeredDate = registeredDate;
    }

    public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }

    @Override
    public String displayInfo() {
        return "Customer #" + getId() + "\n"
                + "Name: " + getName() + "\n"
                + "Phone: " + orDash(getPhone()) + "\n"
                + "Email: " + orDash(getEmail()) + "\n"
                + "Address: " + orDash(getAddress()) + "\n"
                + "Registered: " + (registeredDate == null ? "-" : registeredDate);
    }
}
