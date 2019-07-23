package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

public class ScalaCode {
	private final StringBuilder preBlockCode; //TODO: ex.: class RunningExample extends Model
	private final StringBuilder blockCode; //TODO: the block content (inside {})
	
	public ScalaCode() {
		this.preBlockCode = new StringBuilder();
		this.blockCode = new StringBuilder();
	}
	
	public void appendPreBlockCode(final StringBuilder code) {
		this.preBlockCode.append(code);
	}
	
	public void appendBlockCode(final StringBuilder code) {
		this.blockCode.append(code);
	}
	
	public StringBuilder getCode() {
		final StringBuilder builder = new StringBuilder();
		return builder.append(this.preBlockCode).append(" {\n").append(this.blockCode).append("\n}\n");
	}
	
	@Override
	public String toString() {
		return getCode().toString();
	}
}
