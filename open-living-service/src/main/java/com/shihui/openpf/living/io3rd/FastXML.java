package com.shihui.openpf.living.io3rd;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.Class;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;



public class FastXML {
	
	private JAXBContext context;
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	
	private FastXML(Class... beanClasses) throws JAXBException, IllegalArgumentException{
		context = JAXBContext.newInstance(beanClasses);
		marshaller = context.createMarshaller();
		marshaller.setProperty("jaxb.encoding", "ISO-8859-1");
		marshaller.setProperty("jaxb.fragment", true);
		unmarshaller = context.createUnmarshaller();
	}

	public static FastXML instance(Class... beanClasses) {
		try {
			FastXML inst = new FastXML(beanClasses);
			return inst;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String  bean2xml(Object object) {
		
		try {
			StringWriter writer = new StringWriter();
			marshaller.marshal(object, writer);
			return "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object xml2bean(String xml) {
		try {
			return unmarshaller.unmarshal(new StringReader(xml));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String beanToXml(Object object) {
		try {
			FastXML inst = new FastXML(object.getClass());
			return inst.bean2xml(object);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object xmlToBean(String xml, Class... beanClasses) {
		try {
			FastXML inst = new FastXML(beanClasses);
			return inst.xml2bean(xml);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
