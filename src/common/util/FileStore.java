package common.util;

import java.io.*;
import java.util.*;

public class FileStore {
    private static final String DATA_DIR = "data";

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> load(String filename) {
        File file = new File(DATA_DIR, filename);
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            System.err.println("[로드 실패] " + filename + ": " + e.getMessage());
            return null;
        }
    }

    public static <T extends Serializable> void save(String filename, List<T> data) {
        new File(DATA_DIR).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(new File(DATA_DIR, filename)))) {
            oos.writeObject(new ArrayList<>(data));
        } catch (IOException e) {
            System.err.println("[저장 실패] " + filename + ": " + e.getMessage());
        }
    }
}
