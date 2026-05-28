import infra.AppContext;
import infra.config.AppConfig;
import infra.config.PropertiesConfigLoader;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0] : "web";

        AppConfig config = new AppConfig(new PropertiesConfigLoader("application.properties"));
        AppContext context = AppContext.initialize(config);

        if ("cli".equals(mode)) {
            context.startCli();
        } else {
            context.startWeb();
        }
    }
}
