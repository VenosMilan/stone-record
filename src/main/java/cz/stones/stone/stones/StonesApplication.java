package cz.stones.stone.stones;

import javax.sql.DataSource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties;
import org.springframework.context.annotation.Bean;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.spring.annotation.EnableVaadin;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import cz.stones.stone.stones.dao.StoneDao;

@SpringBootApplication
@Theme(value = "stones-app", variant = Lumo.DARK)
@EnableVaadin(value = "cz.stones.stone")
public class StonesApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(StonesApplication.class, args);
	}

	@Bean
	SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer(
			DataSource dataSource, SqlInitializationProperties properties, StoneDao repository) {
		return new SqlDataSourceScriptDatabaseInitializer(dataSource, properties) {
			@Override
			public boolean initializeDatabase() {
				if (repository.count() == 0L) {
                    return super.initializeDatabase();
                }
				return false;
			}
		};
	}
}
