package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util.ScalaHelper;

import com.att.research.xacml.api.XACML3;

/**
 * The function enum contains all XACML match functions which are used in the PCM-2-XACML system.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum Function {
    STRING_EQUALS(XACML3.ID_FUNCTION_STRING_EQUAL.stringValue(), Attribute.TYPE_STRING) {
        @Override
        protected StringBuilder getCheckCode(final String scalaAttributeName, final String value) {
            return ScalaHelper.parenthesize(new StringBuilder(AttributeExtractor.VAR_NAME).append(".")
                    .append(scalaAttributeName).append(" == ").append("\"").append(value).append("\""));
        }
    },
    STRING_REGEX(XACML3.ID_FUNCTION_STRING_REGEXP_MATCH.stringValue(), Attribute.TYPE_STRING) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            final StringBuilder nullCheck = nullCheck(scalaAttributeName);
            final StringBuilder matchAgainstValue = ScalaHelper
                    .parenthesize(new StringBuilder("\"").append(value.replaceAll("\\\\", "\\\\\\\\")).append("\""));
            return ScalaHelper.parenthesize(nullCheck.append(AttributeExtractor.VAR_NAME).append(".")
                    .append(scalaAttributeName).append(".").append("matches").append(matchAgainstValue));
        }
    },
    LESS_INT(XACML3.ID_FUNCTION_INTEGER_LESS_THAN.stringValue(), Attribute.TYPE_INT) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // value defined in policy ('value') must be less than the request value
            return compare(scalaAttributeName, " > ", value);
        }
    },
    LESS_DOUBLE(XACML3.ID_FUNCTION_DOUBLE_LESS_THAN.stringValue(), Attribute.TYPE_DOUBLE) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // value defined in policy ('value') must be less than the request value
            return compare(scalaAttributeName, " > ", value);
        }
    },
    GREATER_INT(XACML3.ID_FUNCTION_INTEGER_GREATER_THAN.stringValue(), Attribute.TYPE_INT) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // value defined in policy ('value') must be greater than the request value
            return compare(scalaAttributeName, " < ", value);
        }
    },
    GREATER_DOUBLE(XACML3.ID_FUNCTION_DOUBLE_GREATER_THAN.stringValue(), Attribute.TYPE_DOUBLE) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // value defined in policy ('value') must be greater than the request value
            return compare(scalaAttributeName, " < ", value);
        }
    },
    LESS_TIME(XACML3.ID_FUNCTION_TIME_LESS_THAN_OR_EQUAL.stringValue(), Attribute.TYPE_TIME) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // time defined in policy ('value') must be less than the request value
            return compareTime(scalaAttributeName, " isAfter ", value);
        }
    },
    GREATER_TIME(XACML3.ID_FUNCTION_TIME_GREATER_THAN_OR_EQUAL.stringValue(), Attribute.TYPE_TIME) {
        @Override
        protected StringBuilder getCheckCode(String scalaAttributeName, String value) {
            // time defined in policy ('value') must be greater than the request value
            return compareTime(scalaAttributeName, " isBefore ", value);
        }
    };

    private final String matchId;
    private final String scalaType;

    /**
     * Constructor for the Function enum values.
     * 
     * @param matchId
     *          - the XACML match id
     * @param scalaType
     *          - the datatype in scala
     */
    private Function(final String matchId, final String scalaType) {
        this.matchId = matchId;
        this.scalaType = scalaType;
    }

    /**
     * Gets the match id.
     * 
     * @return the match id
     */
    public String getMatchId() {
        return this.matchId;
    }

    /**
     * Gets the name of the datatype in scala.
     * 
     * @return the name of the datatype
     */
    protected String getScalaType() {
        return this.scalaType;
    }

    /**
     * Gets the check code for the given scala attribute name and the given value.
     * 
     * @param scalaAttributeName
     *            - the scala attribute name
     * @param value
     *            - the given value
     * @return the check code for the given value
     */
    protected abstract StringBuilder getCheckCode(String scalaAttributeName, String value);

    /**
     * Gets the function defined by the match id.
     * 
     * @param matchId
     *            - the match id
     * @return the function defined by the match id
     */
    public static Function of(final String matchId) {
        for (final Function function : values()) {
            if (function.getMatchId().equals(matchId)) {
                return function;
            }
        }
        return null;
    }

    /**
     * Creates a {@code StringBuilder} which defines a check of the scala attribute against null (!=null).
     * 
     * @param scalaAttributeName 
     *          - the name of the scala attribute
     * @return the {@code StringBuilder} which defines a check of the scala attribute against null (!=null).
     */
    private static StringBuilder nullCheck(final String scalaAttributeName) {
        return new StringBuilder(AttributeExtractor.VAR_NAME).append(".").append(scalaAttributeName)
                .append(" != null && ");
    }

    /**
     * Creates a {@code StringBuilder} which defines a comparison.
     * 
     * @param scalaAttributeName
     *          - the name of the scala attribute
     * @param comparison
     *          - the comparison sting (" > ", " < ", " >= ", " <= ")
     * @param value
     *          - the value to which the scala attribute should be compared to
     * @return the {@code StringBuilder} which defines a comparison
     */
    private static StringBuilder compare(final String scalaAttributeName, final String comparison, final String value) {
        return ScalaHelper.parenthesize(new StringBuilder(AttributeExtractor.VAR_NAME).append(".")
                .append(scalaAttributeName).append(comparison).append(value));
    }

    /**
     * Creates a {@code StringBuilder} which defines a time comparison.
     * 
     * @param scalaAttributeName
     *          - the name of the scala attribute
     * @param comparison
     *          - the comparison sting (" isAfter ", " isBefore ")
     * @param value
     *          - the value to which the scala attribute should be compared to
     * @return the {@code StringBuilder} which defines a time comparison
     */
    private static StringBuilder compareTime(String scalaAttributeName, String comparison, String value) {
        final StringBuilder parsedValue = ScalaHelper.parenthesize(
                new StringBuilder("LocalTime.parse(\"").append(value).append("\", DateTimeFormatter.ISO_OFFSET_TIME)"));
        final StringBuilder comparisonStringBuilder = ScalaHelper
                .parenthesize(new StringBuilder(scalaAttributeName).append(comparison).append(parsedValue));
        final StringBuilder equalityStringBuilder = ScalaHelper
                .parenthesize(new StringBuilder(scalaAttributeName).append(" equals ").append(parsedValue));
        return ScalaHelper.parenthesize(comparisonStringBuilder.append(" || ").append(equalityStringBuilder));
    }
}
