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

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext)
	    throws ServletException {

	AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
	context.setConfigLocation("me.philnate.textmanager.web.config");

	FilterRegistration.Dynamic characterEncodingFilter = servletContext
		.addFilter("characterEncodingFilter",
			new CharacterEncodingFilter());
	characterEncodingFilter.addMappingForUrlPatterns(
		EnumSet.allOf(DispatcherType.class), true, "/*");
	characterEncodingFilter.setInitParameter("encoding", "UTF-8");
	characterEncodingFilter.setInitParameter("forceEncoding", "true");

	servletContext.addListener(new ContextLoaderListener(context));
	servletContext.setInitParameter("defaultHtmlEscape", "true");

	DispatcherServlet servlet = new DispatcherServlet();
	// no explicit configuration reference here: everything is configured in
	// the root container for simplicity
	servlet.setContextConfigLocation("");

	ServletRegistration.Dynamic appServlet = servletContext.addServlet(
		"appServlet", servlet);
	appServlet.setLoadOnStartup(1);
	appServlet.setAsyncSupported(true);

	Set<String> mappingConflicts = appServlet.addMapping("/");
	if (!mappingConflicts.isEmpty()) {
	    throw new IllegalStateException(
		    "'appServlet' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
	}
    }
}