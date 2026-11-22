package inventorysystem.model;

/**
 * Abstract base class for people the store deals with.
 * Demonstrates ABSTRACTION and INHERITANCE: Customer and Supplier extend it.
 */
public abstract class Person {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String address;

    protected Person() { }

    protected Person(int id, String name, String phone, String email, String address) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    // Encapsulation: private fields, public getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    /**
     * Each subclass builds its own description (POLYMORPHISM): calling
     * displayInfo() on a Person reference runs the Customer or Supplier version.
     */
    public abstract String displayInfo();

    /** Helper for subclasses: shows "-" for an empty optional value. */
    protected static String orDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    @Override
    public String toString() {
        return name;
    }
}
