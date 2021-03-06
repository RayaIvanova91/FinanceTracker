package com.financetracker;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan("com.financetracker")
public class SpringWebConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/uploaded/**").addResourceLocations("file:///C:\\uploaded\\");
		registry.addResourceHandler("/images/**").addResourceLocations("/static/images/");
		registry.addResourceHandler("resetPassword/images/**").addResourceLocations("/static/images/");
		registry.addResourceHandler("/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/accounts/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/accounts/js/**").addResourceLocations("/static/js/");
		
		registry.addResourceHandler("/accounts/acc/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/accounts/acc/js/**").addResourceLocations("/static/js/");
		
		registry.addResourceHandler("/transactions/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/transactions/js/**").addResourceLocations("/static/js/");
		
		registry.addResourceHandler("/transactions/add/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/transactions/add/js/**").addResourceLocations("/static/js/");

		registry.addResourceHandler("/transactions/edit/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/transactions/edit/js/**").addResourceLocations("/static/js/");

		registry.addResourceHandler("/resetPassword/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/resetPassword/js/**").addResourceLocations("/static/js/");
		
		registry.addResourceHandler("/plannedTransactions/css/**").addResourceLocations("/static/css/");
		registry.addResourceHandler("/plannedTransactions/js/**").addResourceLocations("/static/js/");

		
		registry.addResourceHandler("/js/**").addResourceLocations("/static/js/");
		registry.addResourceHandler("/pdfs/**").addResourceLocations("/static/pdf/");
		registry.addResourceHandler("/fonts/**").addResourceLocations("/static/fonts/font-awesome-4.7.0/css");
		registry.addResourceHandler("/bootstrap/**").addResourceLocations("/static/vendor/bootstrap/css");
	}

	@Bean
	public InternalResourceViewResolver getInternalResourceViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewClass(JstlView.class);
		resolver.setPrefix("/WEB-INF/views/jsp/");
		resolver.setSuffix(".jsp");

		return resolver;
	}

	// localization configuration
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}

	@Bean
	public LocaleResolver localeResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor changeInterceptor = new LocaleChangeInterceptor();
		changeInterceptor.setParamName("language");
		registry.addInterceptor(changeInterceptor);
	}

}
