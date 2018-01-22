package org.pinguin.gui.util;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;

public class BindHelper<T> {

	private final Map<String, Property<?>> propMap = new HashMap<>();
	private T to;

	public BindHelper() {
	}

	public T getTo() {
		return to;
	}

	public void setTo(T to) {
		this.to = to;
	}

	public <B> void bind(String fieldName, Property<B> property) {
		property.bindBidirectional(getProperty(fieldName));
	}

	@SuppressWarnings("unchecked")
	public <B> Property<B> getProperty(String fieldName) {
		if (!propMap.containsKey(fieldName)) {
			Property<B> jbObjProp = buildJBObjProp(to, fieldName);
			propMap.put(fieldName, jbObjProp);
		}
		return (Property<B>) propMap.get(fieldName);
	}

	@SuppressWarnings("unchecked")
	private <B> Property<B> buildJBObjProp(Object parent, String fieldName) {
		try {
			return JavaBeanObjectPropertyBuilder.create().bean(parent).name(fieldName).build();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("Falha ao construir uma Property.", e);
		}
	}

}
