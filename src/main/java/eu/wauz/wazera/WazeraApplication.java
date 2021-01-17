package eu.wauz.wazera;

import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@SpringBootApplication
@ComponentScan(basePackages = {"eu.wauz"})
@EntityScan(basePackages = {"eu.wauz"})
public class WazeraApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(WazeraApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WazeraApplication.class);
	}
	
	@Bean
	public ServletRegistrationBean<Servlet> servletRegistrationBean() {
		FacesServlet facesServlet = new FacesServlet();
		ServletRegistrationBean<Servlet> servletRegistrationBean = new ServletRegistrationBean<Servlet>(facesServlet, "*.xhtml");
		return servletRegistrationBean;
	}
	
	@Bean
	public ErrorPageRegistrar errorPageRegistrar(){
	    return registry -> {
	    	registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/docs.xhtml"));
	    };
	}
	
    @Bean
    @Autowired
	public DataSource wazeraDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setUrl("jdbc:mariadb://localhost:3306/wazera");
		return dataSource;
	}
    
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        hibernateJpaVendorAdapter.setDatabase(Database.H2);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        return hibernateJpaVendorAdapter;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
    	LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
    	lef.setDataSource(dataSource);
    	lef.setJpaVendorAdapter(jpaVendorAdapter);
    	lef.setPackagesToScan("eu.wauz");
    	return lef;
    }

}
