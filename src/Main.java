import infra.Context;
import ui.LoginController;
import ui.MainMenuController;

public class Main {
    public static void main(String[] args) {
        new LoginController().run();

        if (Context.getInstance().isLoggedIn()) {
            new MainMenuController().run();
        }
    }
}
