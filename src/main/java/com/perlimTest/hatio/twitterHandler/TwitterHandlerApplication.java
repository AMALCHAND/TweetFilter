package com.perlimTest.hatio.twitterHandler;

import java.util.EnumSet;

import javax.faces.webapp.FacesServlet;
import javax.servlet.DispatcherType;

import org.ocpsoft.rewrite.servlet.RewriteFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
@SpringBootApplication
/*
 * @author amalchand
 * @since Dec 04,2020
 */
public class TwitterHandlerApplication extends SpringBootServletInitializer
{

	public static void main(String[] args)
	{

		SpringApplication.run(TwitterHandlerApplication.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{

		final SpringApplicationBuilder appBuilder = application.sources(TwitterHandlerApplication.class);
		return appBuilder;
	}

	@Bean
	public ServletRegistrationBean<FacesServlet> servletRegistrationBean()
	{

		FacesServlet servlet = new FacesServlet();
		return new ServletRegistrationBean<FacesServlet>(servlet, "*.xhtml");
	}

	@Bean
	public FilterRegistrationBean<RewriteFilter> rewriteFilter()
	{
		final FilterRegistrationBean<RewriteFilter> rwFilter = new FilterRegistrationBean<RewriteFilter>(
				new RewriteFilter());
		rwFilter.setDispatcherTypes(
				EnumSet.of(DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.ERROR));
		rwFilter.addUrlPatterns("/*");
		return rwFilter;
	}
}
