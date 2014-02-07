/**
 * Copyright (C) 2012 philnate (http://github.com/philnate/textmanager)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
		p.put("webapp.resource.loader.path", new File(webapp, "html").toString());
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
