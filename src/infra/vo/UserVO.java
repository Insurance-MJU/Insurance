package infra.vo;

public class UserVO {
    public final String userId;
    public final String password;
    public final String name;
    public final String role;
    public final String ssn;

    public UserVO(String userId, String password, String name, String role, String ssn) {
        this.userId   = userId;
        this.password = password;
        this.name     = name;
        this.role     = role;
        this.ssn      = ssn;
    }
}
