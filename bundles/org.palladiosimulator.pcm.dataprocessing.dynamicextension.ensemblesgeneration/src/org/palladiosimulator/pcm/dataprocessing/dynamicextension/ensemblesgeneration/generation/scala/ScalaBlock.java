package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

public class ScalaBlock implements ScalaCode {
	private final StringBuilder preBlockCode; //TODO: ex.: class RunningExample extends Model
	private final StringBuilder blockCode; //TODO: the block content (inside {})
	private ScalaBlock next; //TODO: the following block
	
	public ScalaBlock() {
		this.preBlockCode = new StringBuilder();
		this.blockCode = new StringBuilder();
		this.next = null;
	}
	
	public void appendPreBlockCode(final StringBuilder code) {
		this.preBlockCode.append(code);
	}
	
	public void appendBlockCode(final StringBuilder code) {
		this.blockCode.append(code);
	}
	
	public void setNext(final ScalaBlock next) {
		this.next = next;
	}
	
	@Override
	public StringBuilder getCodeDefinition() {
		final StringBuilder builder = new StringBuilder();
		return builder.append(this.preBlockCode)
				.append(" {\n").append(this.blockCode).append("\n}\n")
				.append(this.next == null ? "" : this.next.getCodeDefinition());
	}
	
	@Override
	public String toString() {
		return getCodeDefinition().toString();
	}
}
