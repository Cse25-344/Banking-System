
package model;

public class CompanyCustomer extends Customer {

    private String companyName;
    private String registrationNumber;

    public CompanyCustomer(
            String id,
            String address,
            String companyName,
            String registrationNumber) {

        super(id, address);

        this.companyName = companyName;
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String getName() {
        return companyName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}
