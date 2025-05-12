package at.open.letto.plugin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.jar.Manifest;

@SpringBootApplication
		(scanBasePackages = {
				"at.open.letto.plugin",
				"at.letto.basespringboot.security",
				"at.letto.basespringboot.config",
				"at.letto.basespringboot.controller",
				"at.letto.databaseclient"
		})
public class PluginApplication {

	public static ConfigurableApplicationContext context;
	public static Manifest manifest;

	public static void main(String[] args) {
		SpringApplication.run(PluginApplication.class, args);
		try {
			manifest = new Manifest(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
		} catch (Exception ex) {}
	}

}
