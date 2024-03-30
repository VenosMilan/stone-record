package cz.stones.stone.stones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

@SpringBootApplication
@Theme(value = "stones-app", variant = Lumo.DARK)
@EnableVaadin(value = "cz.stones.stone")
public class StonesApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(StonesApplication.class, args);
	}

}
