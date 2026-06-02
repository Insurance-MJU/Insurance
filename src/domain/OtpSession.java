package domain;

public class OtpSession {
    private final String sessionId;
    private final String expiresAt;

    public OtpSession(String sessionId, String expiresAt) {
        this.sessionId = sessionId;
        this.expiresAt = expiresAt;
    }

    public String getSessionId() { return sessionId; }
    public String getExpiresAt() { return expiresAt; }
}
