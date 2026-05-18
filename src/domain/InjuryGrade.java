package domain;

public enum InjuryGrade {
    GRADE_1(1),   GRADE_2(2),   GRADE_3(3),   GRADE_4(4),   GRADE_5(5),
    GRADE_6(6),   GRADE_7(7),   GRADE_8(8),   GRADE_9(9),   GRADE_10(10),
    GRADE_11(11), GRADE_12(12), GRADE_13(13), GRADE_14(14);

    private final int grade;

    InjuryGrade(int grade) { this.grade = grade; }

    public int getGrade()    { return grade; }
    public String getLabel() { return grade + "급"; }

    public static InjuryGrade fromGrade(int grade) {
        for (InjuryGrade g : values()) {
            if (g.grade == grade) return g;
        }
        return null;
    }

    public static int maxGrade() { return 14; }
}
