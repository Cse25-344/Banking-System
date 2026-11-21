package banking;

public class IndividualCustomer extends Customer {
    private final String firstName;
    private final String surname;
    private final boolean employed;
    private final String employerName;
    private final String employerAddress;

    public IndividualCustomer(String id, String address, String firstName, String surname,
                              boolean employed, String employerName, String employerAddress) {
        super(id, address);
        if (firstName == null || firstName.isBlank() || surname == null || surname.isBlank())
            throw new IllegalArgumentException("First name and surname are required");
        this.firstName = firstName;
        this.surname = surname;
        this.employed = employed;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override public String getDisplayName() { return firstName + " " + surname; }
    public boolean isEmployed() { return employed; }
    public boolean hasEmployerDetails() {
        return employerName != null && !employerName.isBlank()
                && employerAddress != null && !employerAddress.isBlank();
    }
    public String getEmployerName() { return employerName; }
    public String getEmployerAddress() { return employerAddress; }
}
