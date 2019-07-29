package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

public class ValueDeclaration implements ScalaCode {
	private final String name;
	private final String type;
	private final boolean isOptional;
	
	public ValueDeclaration(final String name, final String type) {
		this.name = name;
		this.type = type;
		this.isOptional = false;
	}
	
	public ValueDeclaration(final String name, final String type, final boolean optional) {
		this.name = name;
		this.type = type;
		this.isOptional = optional;
	}
	
	@Override
	public StringBuilder getCodeDefinition() {
		return new StringBuilder(ScalaHelper.KEYWORD_VAL).append(" ").append(this.name).append(": ").append(this.type)
				.append(this.isOptional ? " = null" : "");
	}
}
