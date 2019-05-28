package com.windstream.voip.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableTransactionManagement
@EnableHystrixDashboard
@EnableCircuitBreaker
@EnableFeignClients(basePackages = "com.windstream.voip.proxy")
@EnableScheduling
@EnableCaching
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
public class VoIPConfig extends WebMvcConfigurationSupport {



	@Bean(name = "voip-db")
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource configureVoIPDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "bw-db")
	@ConfigurationProperties(prefix = "bw.datasource")
	public DataSource configureBWDataSource() {
		return DataSourceBuilder.create().build();
	}


	@Bean(name="voip-jdbc-template")
	@Primary
	public JdbcTemplate configureVoIPJdbcTemplate(@Qualifier("voip-db")DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}


	@Bean(name="bw-jdbc-template")
	public JdbcTemplate configureBwJdbcTemplate(@Qualifier("bw-db")DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public Docket enableSwaggerForV1() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("v1").select()
				.apis(RequestHandlerSelectors.basePackage("com.windstream.voip.controller")).paths(PathSelectors.regex("(/v1).+")).build()
				.apiInfo(swaggerConfigMetaData());
	}
	
	@Bean
	public Docket enableSwaggerForV2() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("v2").select()
				.apis(RequestHandlerSelectors.basePackage("com.windstream.voip.controller")).paths(PathSelectors.regex("(/v2).+")).build()
				.apiInfo(swaggerConfigMetaData());
	}

	// Adding swagger UI resources to fix swagger UI issue.
	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

	}

	@Override
	protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(false);
		configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}

	@Override
	protected void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.setUseSuffixPatternMatch(false); // avoiding url path extension translation.
	}

	private ApiInfo swaggerConfigMetaData() {
		return new ApiInfoBuilder().title("VoIP Service").description("\"VoIP Service description\"").version("1.0.0")
				.license("Apache License Version 2.0").licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
				.contact(new Contact("WOL Dev", "https://www.windstreamonline.com/", "wcit-wol@windstream.com"))
				.build();
	}


	@Bean
	LockProvider lockProvider(@Qualifier("voip-jdbc-template")JdbcTemplate template) {
		return new JdbcTemplateLockProvider(template);
	}
}
