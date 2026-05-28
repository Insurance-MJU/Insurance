package controller.cli;

import domain.common.User;
import java.util.Scanner;

public class Context {
    private static final Context INSTANCE = new Context();
    private User currentUser;
    private Scanner scanner = new Scanner(System.in);

    private Context() {}

    public static Context getInstance() { return INSTANCE; }

    public void login(User user) { this.currentUser = user; }
    public void logout() { this.currentUser = null; }
    public boolean isLoggedIn() { return currentUser != null; }
    public User getCurrentUser() { return currentUser; }
    public Scanner scanner() { return scanner; }
    public void setScanner(Scanner s) { this.scanner = s; }
}
