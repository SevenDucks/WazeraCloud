package eu.wauz.wazera;

import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@SpringBootApplication
@ComponentScan(basePackages = {"eu.wauz"})
@EntityScan(basePackages = {"eu.wauz"})
public class WazeraApplication extends SpringBootServletInitializer {
	
	public static final String APP_ROOT = "~/wazera/";
	
	public static final boolean EMBED_DB = true;
	
	@Autowired
	private BuildProperties buildProperties;

	public static void main(String[] args) {
		SpringApplication.run(WazeraApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WazeraApplication.class);
	}
	
	@Bean
	public ServletContextInitializer servletContextInitializer() {
		return new ServletContextInitializer() {
			
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException {
				System.out.println("O~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-O");
				System.out.println(" _    _                                           ");
				System.out.println("| |  | | WazeraCloud v" + buildProperties.getVersion());
				System.out.println("| |  | | __ _ _   _ _____ __ ___   ___  _ __  ___ ");
				System.out.println("| |/\\| |/ _` | | | |_  / '_ ` _ \\ / _ \\| '_ \\/ __|");
				System.out.println("\\  /\\  / (_| | |_| |/ /| | | | | | (_) | | | \\__ \\");
				System.out.println(" \\/  \\/ \\__,_|\\__,_/___|_| |_| |_|\\___/|_| |_|___/");
				System.out.println("");
				System.out.println("O-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~O");
				servletContext.setInitParameter("primefaces.THEME", "wazera-purple");
				servletContext.setInitParameter("primefaces.FONT_AWESOME", "true");
				servletContext.setInitParameter("primefaces.MOVE_SCRIPTS_TO_BOTTOM", "true");
			}
			
		};
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
    	if(EMBED_DB) {
    		DriverManagerDataSource dataSource = new DriverManagerDataSource();
    		dataSource.setUsername("root");
    		dataSource.setDriverClassName("org.h2.Driver");
    		dataSource.setUrl("jdbc:h2:file:" + APP_ROOT + "Wazera");
    		return dataSource;
    	}
    	else {
    		BasicDataSource dataSource = new BasicDataSource();
    		dataSource.setUsername("root");
    		dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
    		dataSource.setUrl("jdbc:mariadb://localhost:3306/wazera");
    		return dataSource;
    	}
	}
    
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        if(EMBED_DB) {
        	hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        }
        else {
        	hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        }
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
