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

    public enum Role {
        POLICYHOLDER, // 계약자
        INSURED,      // 피보험자
        BENEFICIARY,  // 수익자
        CLAIMANT,     // 청구인
        THIRD_PARTY   // 제3자 (사고 상대방)
    }

    // SSN(YYMMDD-G******) 에서 만 나이 계산
    public static int calcAge(String ssn) {
        try {
            String[] parts = ssn.split("-");
            if (parts.length < 2 || parts[0].length() < 2) return -1;
            int yy = Integer.parseInt(parts[0].substring(0, 2));
            char g = parts[1].charAt(0);
            int birthYear = (g == '1' || g == '2') ? 1900 + yy : 2000 + yy;
            return java.time.LocalDate.now().getYear() - birthYear;
        } catch (Exception e) {
            return -1;
        }
    }

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
