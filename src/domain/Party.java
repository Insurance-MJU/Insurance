package domain;

import java.io.Serializable;
import java.util.Date;

public class Party implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public void setAddress(String v) { this.address = v; }
    public void setBirthDate(Date v) { this.birthDate = v; }
    public void setName(String v) { this.name = v; }
    public void setPartyId(String v) { this.partyId = v; }
    public void setPhone(String v) { this.phone = v; }
    public void setRole(Role v) { this.role = v; }
    public void setSsn(String v) { this.ssn = v; }
}
