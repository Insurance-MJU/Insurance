package domain;

import domain.common.Money;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class CreditInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class AccidentRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String date;
        private final String description;
        private final Money amount;

        public AccidentRecord(String date, String description, Money amount) {
            this.date = date;
            this.description = description;
            this.amount = amount;
        }

        public String getDate()        { return date; }
        public String getDescription() { return description; }
        public Money getAmount()       { return amount; }
    }

    private String applicantName;
    private String ssn;
    private String carNumber;
    private List<AccidentRecord> accidentHistory;
    private int drivingExperienceYears;
    private String creditGrade;
    private String fraudHistory;

    // ── 비즈니스 메서드 ───────────────────────────────────────
    public int getAccidentCount() {
        return (accidentHistory != null) ? accidentHistory.size() : 0;
    }

    public boolean hasAccidentHistory() {
        return getAccidentCount() > 0;
    }

    public boolean isNewDriver() {
        return drivingExperienceYears == 0;
    }

    // ── Setters (외부 시스템 클라이언트 전용) ────────────────────
    public void setApplicantName(String v)                 { this.applicantName = v; }
    public void setSsn(String v)                           { this.ssn = v; }
    public void setCarNumber(String v)                     { this.carNumber = v; }
    public void setAccidentHistory(List<AccidentRecord> v) { this.accidentHistory = v; }
    public void setDrivingExperienceYears(int v)           { this.drivingExperienceYears = v; }
    public void setCreditGrade(String v)                   { this.creditGrade = v; }
    public void setFraudHistory(String v)                  { this.fraudHistory = v; }

    // ── Getters ───────────────────────────────────────────────
    public String getApplicantName()              { return applicantName; }
    public String getSsn()                        { return ssn; }
    public String getCarNumber()                  { return carNumber; }
    public List<AccidentRecord> getAccidentHistory() {
        return accidentHistory != null ? accidentHistory : Collections.emptyList();
    }
    public int getDrivingExperienceYears()        { return drivingExperienceYears; }
    public String getCreditGrade()                { return creditGrade; }
    public String getFraudHistory()               { return fraudHistory; }

}
