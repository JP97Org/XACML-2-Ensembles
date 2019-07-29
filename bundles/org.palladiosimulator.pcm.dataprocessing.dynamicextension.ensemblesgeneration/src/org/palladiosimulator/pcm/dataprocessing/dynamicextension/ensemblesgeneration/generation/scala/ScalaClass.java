package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.ArrayList;
import java.util.List;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

public class ScalaClass implements ScalaCode {
	private final boolean singleton;
	private final String name;
	private final String superClass;
	
	private final List<ValueDeclaration> attributes;
	
	public ScalaClass(final boolean singleton, final String name, final String superClass) {
		this.singleton = singleton;
		this.name = name;
		this.superClass = superClass;
		this.attributes = new ArrayList<>();
	}
	
	public void addAllAttributes(final List<ValueDeclaration> attributes) {
		this.attributes.addAll(attributes);
	}
	
	@Override
	public StringBuilder getCodeDefinition() {
		return new StringBuilder(this.singleton ? ScalaHelper.KEYWORD_OBJECT : ScalaHelper.KEYWORD_CLASS)
				.append(" ").append(this.name).append(getAttributeList()).append(" ").append(ScalaHelper.KEYWORD_EXTENDS).append(" ")
				.append(this.superClass);
	}
	
	private StringBuilder getAttributeList() {
		if (!this.attributes.isEmpty()) {
			final StringBuilder builder = new StringBuilder("(");
			for (final ValueDeclaration attribute : this.attributes) {
				builder.append(attribute.getCodeDefinition()).append(", ");
			}
			return builder.delete(builder.length() - 2, builder.length()).append(")");
		}
		return new StringBuilder();
	}
}
