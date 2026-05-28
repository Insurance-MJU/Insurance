import infra.AppContext;
import infra.config.AppConfig;
import infra.config.PropertiesConfigLoader;

public class Main {
    public static void main(String[] args) {

        AppConfig config = new AppConfig(new PropertiesConfigLoader("application.properties"));
        AppContext context = AppContext.initialize(config);
        context.startWeb();

    }
}
