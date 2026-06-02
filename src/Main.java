import infra.AppContext;
import infra.config.AppConfig;
import infra.config.PropertiesConfigLoader;

public class Main {
    public static void main(String[] args) {
        AppConfig config = new AppConfig(new PropertiesConfigLoader("application.properties"));
        AppContext context = AppContext.initialize(config);
        if (args.length > 0 && "web".equalsIgnoreCase(args[0])) {
            context.startWeb();
        } else {
            context.startCli();
        }
    }
}
