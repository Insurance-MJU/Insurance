import infra.AppContext;
import infra.Context;

public class Main {
    public static void main(String[] args) {
        AppContext appContext = AppContext.initialize();
        appContext.getLoginController().run();

        if (Context.getInstance().isLoggedIn()) {
            appContext.getMainMenuController().run();
        }
    }
}
