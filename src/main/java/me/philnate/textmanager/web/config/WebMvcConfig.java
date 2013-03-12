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

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    private static final String MESSAGE_SOURCE = "/WEB-INF/i18n/messages";
    private static final String TILES = "/WEB-INF/tiles/tiles.xml";
    private static final String VIEWS = "/WEB-INF/views/**/views.xml";

    private static final String RESOURCES_HANDLER = "/resources/";
    private static final String RESOURCES_LOCATION = RESOURCES_HANDLER + "**";

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
	RequestMappingHandlerMapping requestMappingHandlerMapping = super
		.requestMappingHandlerMapping();
	requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
	requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
	return requestMappingHandlerMapping;
    }

    @Bean(name = "messageSource")
    public MessageSource configureMessageSource() {
	ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	messageSource.setBasename(MESSAGE_SOURCE);
	messageSource.setCacheSeconds(5);
	return messageSource;
    }

    @Bean
    public TilesViewResolver configureTilesViewResolver() {
	return new TilesViewResolver();
    }

    @Bean
    public TilesConfigurer configureTilesConfigurer() {
	TilesConfigurer configurer = new TilesConfigurer();
	configurer.setDefinitions(new String[] { TILES, VIEWS });
	return configurer;
    }

    @Override
    public Validator getValidator() {
	LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
	validator.setValidationMessageSource(configureMessageSource());
	return validator;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	registry.addResourceHandler(RESOURCES_HANDLER).addResourceLocations(
		RESOURCES_LOCATION);
    }

    @Override
    public void configureDefaultServletHandling(
	    DefaultServletHandlerConfigurer configurer) {
	configurer.enable();
    }
}