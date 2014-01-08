/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
 *
 *   This file is part of textManager.
 *
 *   textManager is free software: you can redistribute it and/or modify it under the terms of the
 *   GNU General Public License as published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   textManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *   See the GNU General Public License for more details. You should have received a copy of the GNU
 *   General Public License along with textManager. If not, see <http://www.gnu.org/licenses/>.
 */
package me.philnate.textmanager.web.config;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.velocity.VelocityConfig;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.github.cherimojava.data.spring.EntityConverter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	private static final File webapp = new File(SystemUtils.getUserDir(), "webapp");

	@Autowired
	EntityConverter converter;

	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping requestMappingHandlerMapping = super.requestMappingHandlerMapping();
		requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
		requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
		return requestMappingHandlerMapping;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resource/**").addResourceLocations("file:" + webapp.toString() + "/resources/");
	}

	@Override
	protected void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		// addDefaultHttpMessageConverters(converters);
		converters.add(converter);
	}

	@Bean
	public VelocityConfig velocityConfig() {
		Properties p = new Properties();
		p.put("resource.loader", "webapp");
		p.put("webapp.resource.loader.path", new File(webapp, "WEB-INF/html").toString());
		p.put("webapp.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		VelocityConfigurer vc = new VelocityConfigurer();
		vc.setVelocityEngine(new VelocityEngine(p));
		return vc;
	}

	@Bean
	public VelocityViewResolver velocityViewResolver() {
		VelocityViewResolver vvw = new VelocityViewResolver();
		vvw.setPrefix("");
		vvw.setSuffix(".html");
		vvw.setCache(false);
		return vvw;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
}
