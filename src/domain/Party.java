package domain;

import java.util.Date;

public class Party {
    private String address;
    private Date birthDate;
    private String name;
    private String partyId;
    private String phone;
    private Role role;
    private String ssn;

    public enum Role {}

    public String getAddress() { return address; }
    public Date getBirthDate() { return birthDate; }
    public String getName() { return name; }
    public String getPartyId() { return partyId; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public String getSsn() { return ssn; }
}
