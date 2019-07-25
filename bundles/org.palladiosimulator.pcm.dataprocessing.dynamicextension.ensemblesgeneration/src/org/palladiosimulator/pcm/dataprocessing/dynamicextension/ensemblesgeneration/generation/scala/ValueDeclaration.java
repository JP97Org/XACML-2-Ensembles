package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

public class ValueDeclaration {
	private final String name;
	private final String type;
	
	public ValueDeclaration(final String name, final String type) {
		this.name = name;
		this.type = type;
	}
	
	public ValueDeclaration(final boolean var, final String name, final String type) {
		this.name = name;
		this.type = type;
	}
	
	public StringBuilder getDefinition() {
		return new StringBuilder(ScalaHelper.KEYWORD_VAL).append(" ").append(this.name).append(": ").append(this.type);
	}
}
