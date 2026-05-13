package infra.util;

import java.io.File;
import java.util.Scanner;

public class DocumentUploadHelper {
    private static final String[] ALLOWED_EXT = {"pdf", "docx", "xlsx", "hwpx"};

    private DocumentUploadHelper() {}

    /**
     * 파일 경로를 입력받아 존재 여부와 확장자를 검증한다.
     * 파일이 없으면 경고 후 재입력을 유도하고, 사용자가 건너뛰면 null을 반환한다.
     *
     * @return 유효한 절대 경로 문자열, 또는 null(건너뜀)
     */
    public static String inputFilePath(Scanner sc, String docName) {
        while (true) {
            System.out.printf("   파일 경로 (예: C:\\Documents\\%s.pdf): ", docName);
            String path = sc.nextLine().trim();

            if (path.isEmpty()) {
                System.out.print("   건너뛰시겠습니까? (Y/N): ");
                if (sc.nextLine().trim().equalsIgnoreCase("Y")) return null;
                continue;
            }

            // A1: 확장자 검사
            if (!isValidExt(path)) {
                System.out.println("   [경고] 파일 형식이 올바르지 않습니다. pdf, docx, xlsx, hwpx만 허용됩니다.");
                continue;
            }

            // E1: 파일 존재 검사
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("   [경고] 파일을 찾을 수 없습니다: " + path);
                System.out.print("   다시 입력하시겠습니까? (Y/N): ");
                if (sc.nextLine().trim().equalsIgnoreCase("Y")) continue;
                return null;
            }

            return file.getAbsolutePath();
        }
    }

    public static boolean isValidExt(String path) {
        if (path == null || !path.contains(".")) return false;
        String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
        for (String a : ALLOWED_EXT) if (a.equals(ext)) return true;
        return false;
    }
}
