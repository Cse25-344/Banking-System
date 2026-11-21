package banking;

public class CompanyCustomer extends Customer {
    private final String companyName;
    private final String registrationNumber;

    public CompanyCustomer(String id, String address, String companyName, String registrationNumber) {
        super(id, address);
        if (companyName == null || companyName.isBlank() || registrationNumber == null || registrationNumber.isBlank())
            throw new IllegalArgumentException("Company name and registration number are required");
        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
    }

    @Override public String getDisplayName() { return companyName; }
    public String getRegistrationNumber() { return registrationNumber; }
}
