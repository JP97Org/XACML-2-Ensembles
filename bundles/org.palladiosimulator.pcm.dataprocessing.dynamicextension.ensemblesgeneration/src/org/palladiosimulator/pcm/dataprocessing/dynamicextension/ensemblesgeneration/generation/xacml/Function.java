package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import com.att.research.xacml.api.XACML3;

public enum Function {
	// TODO other functions
	STRING_EQUALS(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue(), Attribute.TYPE_STRING) {
		@Override
		protected StringBuilder getCheckCode(final String scalaAttributeName, final String value) {
			return ScalaHelper.parenthesize(new StringBuilder()
					.append(AttributeExtractor.VAR_NAME)
					.append(".")
					.append(scalaAttributeName)
					.append(" == ")
					.append("\"")
					.append(value)
					.append("\""));
		}
	};
	
	private final String matchId;
	private final String scalaType;
	
	private Function(final String matchId, final String scalaType) {
		this.matchId = matchId;
		this.scalaType = scalaType;
	}
	
	public String getMatchId() {
		return this.matchId;
	}
	
	protected String getScalaType() {
		return this.scalaType;
	}
	
	protected abstract StringBuilder getCheckCode(String scalaAttributeName, String value);
	
	public static Function of(final String matchId) {
		for (final Function function : values()) {
			if (function.getMatchId().equals(matchId)) {
				return function;
			}
		}
		return null;
	}
}
