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
package me.philnate.textmanager.web;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import me.philnate.textmanager.web.config.RootConfig;
import me.philnate.textmanager.web.config.WebMvcConfig;

import static me.philnate.textmanager.web.config.RootConfig.PROFILE_PRODUCTION;

/**
 * Created by pknobel on 2/11/14.
 */
public class Starter {
	private static final int DEFAULT_PORT = 8082;
	private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

	public static void main(String[] args) throws Exception {
		new Starter(getPort(args));

		// DispatcherServlet servlet = new DispatcherServlet();
		// // no explicit configuration reference here: everything is configured in
		// // the root container for simplicity
		// servlet.setContextConfigLocation("");
		//
		// ServletRegistration.Dynamic appServlet = servletContext.addServlet("appServlet", servlet);
		// appServlet.setLoadOnStartup(1);
		// appServlet.setAsyncSupported(true);
		//
		// Set<String> mappingConflicts = appServlet.addMapping("/");
		// if (!mappingConflicts.isEmpty()) {
		// throw new IllegalStateException("'appServlet' cannot be mapped to '/' under Tomcat versions <= 7.0.14");
		// }
	}

	private WebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		// set the Production profile to be active
		context.getEnvironment().setDefaultProfiles(PROFILE_PRODUCTION);
		context.register(RootConfig.class, WebMvcConfig.class);
		// context.setConfigLocation(RootConfig.class.getPackage().toString());
		return context;
	}

	private ServletContextHandler getServletContextHandler(WebApplicationContext context) throws IOException {
		ServletContextHandler servletContext = new ServletContextHandler();
		// FilterRegistration.Dynamic characterEncodingFilter = servletContext.addFilter("characterEncodingFilter",
		// new CharacterEncodingFilter());
		// characterEncodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		// characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		// characterEncodingFilter.setInitParameter("forceEncoding", "true");
		servletContext.addServlet(new ServletHolder("default", new DispatcherServlet(context)), "/*");
		servletContext.addEventListener(new ContextLoaderListener(context));
		servletContext.setResourceBase(new FileSystemResource(new File("./")).toString());
		// servletContext.addListener(new ContextLoaderListener(context));
		// servletContext.setInitParameter("defaultHtmlEscape", "true");
		return servletContext;
	}

	private Starter(int port) throws Exception {
		Server server = new Server(8082);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}

	private static int getPort(String[] args) {
		if (args.length > 0) {
			try {
				return Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				LOGGER.info("Could not parse port falling back to default", e);
			}
		}
		return DEFAULT_PORT;
	}
}
