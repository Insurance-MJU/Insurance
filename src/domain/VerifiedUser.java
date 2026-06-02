package domain;

public class VerifiedUser {
    private final String name;
    private final String ssn;
    private final String phone;

    public VerifiedUser(String name, String ssn, String phone) {
        this.name = name;
        this.ssn = ssn;
        this.phone = phone;
    }

    public String getName()  { return name; }
    public String getSsn()   { return ssn; }
    public String getPhone() { return phone; }
}
