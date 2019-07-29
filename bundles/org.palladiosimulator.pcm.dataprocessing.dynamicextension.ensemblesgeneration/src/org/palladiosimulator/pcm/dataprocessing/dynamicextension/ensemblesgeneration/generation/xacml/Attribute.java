package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.HashSet;
import java.util.Set;

public enum Attribute {
	//TODO other attributes
	INTERNALSTATE(Category.RESOURCE, "context:internalstate", "internalstate", Function.STRING_EQUALS),
	SHIFT_NAME(Category.SUBJECT, "context:shift:name", "shiftName", Function.STRING_EQUALS);
	
	protected static final String TYPE_STRING = "String";
	protected static final String TYPE_INT = "Int";
	protected static final String TYPE_DOUBLE = "Double";
	//TODO: TYPE_TIME
	
	private final Category attributeCategory;
	private final String attributeId;
	private final String scalaAttributeName;
	private final Function function;
	
	private Attribute(final Category attributeCategory, final String attributeId,
			final String scalaAttributeName, final Function function) {
		this.attributeCategory = attributeCategory;
		this.attributeId = attributeId;
		this.scalaAttributeName = scalaAttributeName;
		this.function = function;
	}
	
	public Category getAttributeCategory() {
		return this.attributeCategory;
	}
	
	public String getAttributeId() {
		return this.attributeId;
	}
	
	public String getScalaAttributeName() {
		return this.scalaAttributeName;
	}
	
	public String getScalaType() {
		return this.function.getScalaType();
	}
	
	public Function getFunction() {
		return this.function;
	}
	
	protected StringBuilder getCheckCode(final String value) {
		return this.function.getCheckCode(this.scalaAttributeName, value);
	}
	
	public static Set<Attribute> getCategoryAttributes(final Category category) {
		final var set = new HashSet<Attribute>();
		
		for (final Attribute attribute : values()) {
			if (attribute.getAttributeCategory().equals(category)) {
				set.add(attribute);
			}
		}
		
		return set;
	}
	
	public static Attribute of(final String attributeId, final Function function) {
		for (final Attribute attribute : values()) {
			if (attribute.getAttributeId().equals(attributeId) && attribute.getFunction().equals(function)) {
				return attribute;
			}
		}
		return null;
	}
}
