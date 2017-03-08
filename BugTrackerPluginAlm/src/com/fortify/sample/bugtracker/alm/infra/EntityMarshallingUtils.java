/*
 * (C) Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.fortify.sample.bugtracker.alm.infra;

import java.io.*;

import javax.xml.bind.*;

/**
 *
 * This file has been taken as is from ALM REST documentation
 * available at http://almhost:port/qcbin/Help/doc_library/api_refs/REST/webframe.html
 *
 * A utility class for converting between JAXB annotated objects and XML.
 */
public class EntityMarshallingUtils {

	private EntityMarshallingUtils() {
	}

	/**
	 * @param <T>
	 *            the type we want to convert the XML into
	 * @param c
	 *            the class of the parameterized type
	 * @param xml
	 *            the instance XML description
	 * @return a deserialization of the XML into an object of type T of class class <T>
	 * @throws JAXBException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T marshal(Class<T> c, String xml) throws JAXBException {
		T res;
		if (c == xml.getClass()) {
			res = (T) xml;
		} else {
			JAXBContext ctx = JAXBContext.newInstance(c);
			Unmarshaller marshaller = ctx.createUnmarshaller();
			res = (T) marshaller.unmarshal(new StringReader(xml));
		}
		return res;
	}

	/**
	 * @param <T>
	 *            the type to serialize
	 * @param c
	 *            the class of the type to serialize
	 * @param o
	 *            the instance containing the data to serialize
	 * @return a string representation of the data.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> String unmarshal(Class<T> c, Object o) throws Exception {
		JAXBContext ctx = JAXBContext.newInstance(c);
		Marshaller marshaller = ctx.createMarshaller();
		StringWriter entityXml = new StringWriter();
		marshaller.marshal(o, entityXml);
		String entityString = entityXml.toString();
		return entityString;
	}
}