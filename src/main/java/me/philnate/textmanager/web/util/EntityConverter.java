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
package me.philnate.textmanager.web.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.mongodb.json.JSONWriter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.github.cherimojava.data.mongo.entity.Entity;
import com.github.cherimojava.data.mongo.entity.EntityFactory;
import com.github.cherimojava.data.mongo.io.EntityEncoder;
import com.google.common.base.Charsets;

//TODO this probably could move into cherimodata spring support

/**
 * Converts an JSON HTTPMessage to and from Entity
 *
 * @param <T>
 *            class of the entity this converter is capable of encoding
 */
public class EntityConverter<T extends Entity> extends AbstractHttpMessageConverter<T> {

	private final Class<T> clazz;
	private final Class proxyClass;
	private final EntityFactory factory;

	public EntityConverter(T stub, EntityFactory factory) {
		super(MediaType.APPLICATION_JSON);
		this.clazz = stub.entityClass();
		this.proxyClass = stub.getClass();
		this.factory = factory;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// for inputs we get the actual Entity class for return values we get the proxy, so we need to check the proxy
		// class too. To correctly catch all classes we should encode
		return this.clazz.equals(clazz) || proxyClass.equals(clazz);
	}

	@Override
	protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		return factory.fromJson(clazz, IOUtils.toString(inputMessage.getBody(), Charsets.UTF_8));
	}

	@Override
	protected void writeInternal(T t, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		try (StringWriter swriter = new StringWriter();
				JSONWriter writer = new JSONWriter(swriter);
				OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody());) {
			new EntityEncoder<T>(factory, EntityFactory.getProperties(t.entityClass())).encode(writer, t);
			osw.write(swriter.toString());
		}
	}
}
