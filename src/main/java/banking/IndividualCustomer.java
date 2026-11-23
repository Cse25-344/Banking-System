
package banking;

public class IndividualCustomer extends Customer {

    private String firstName;
    private String surname;
    private boolean employed;
    private String employerName;
    private String employerAddress;

    public IndividualCustomer(
            String id,
            String address,
            String firstName,
            String surname,
            boolean employed,
            String employerName,
            String employerAddress) {

        super(id, address);

        this.firstName = firstName;
        this.surname = surname;
        this.employed = employed;
        this.employerName = employerName;
        this.employerAddress = employerAddress;
    }

    @Override
    public String getName() {
        return firstName + " " + surname;
    }

    public boolean isEmployed() {
        return employed;
    }

    public String getEmployerName() {
        return employerName;
    }

    public String getEmployerAddress() {
        return employerAddress;
    }
}
