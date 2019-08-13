package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util;

import org.eclipse.emf.codegen.util.CodeGenUtil;

/**
 * Helper Class for creating Scala.
 * 
 * @author majuwa
 * @author Jonathan Schenkenberger
 * @version 1.01
 */
public final class ScalaHelper {
    public static final String KEYWORD_CLASS = "class";
    public static final String KEYWORD_OBJECT = "object";
    public static final String KEYWORD_VAL = "val";
    public static final String KEYWORD_EXTENDS = "extends";
    public static final String KEYWORD_STRING = "String";
    public static final String KEYWORD_INT = "Int";
    public static final String KEYWORD_DOUBLE = "Double";
    public static final String KEYWORD_BOOLEAN = "Boolean";
    public static final String KEYWORD_TIME = "LocalTime";
    public static final String KEYWORD_COMPONENT = "Component";
    public static final String KEYWORD_ENSEMBLE = "Ensemble";
    public static final String KEYWORD_MODEL = "Model";
    public static final String KEYWORD_ALLOW = "allow";
    public static final String KEYWORD_ENSEMBLE_ROOT = "RootEnsemble";
    public static final String KEYWORD_DEF = "def";
    public static final String KEYWORD_NOW = "now";
    
    public static final String SCENARIO_NAME = "scenario";
    public static final String NEW_VARIABLE = " = new " + SCENARIO_NAME + ".";

    /**
     * Private constructor.
     */
    private ScalaHelper() {

    }

    /**
     * Creates an correct Java Identifier
     * 
     * @param s - the identifier name
     * @return {@link String} with Java Identifier
     */
    public static String createIdentifier(String s) {
        if (s == null) {
            throw new NullPointerException("Tried to create identifier from null");
        }
        return CodeGenUtil.validJavaIdentifier(s);
    }

    /**
     * Parenthesizes the given string builder.
     * 
     * @param toParenthesize - the given string builder
     * @return the parenthesized string builder
     */
    public static StringBuilder parenthesize(final StringBuilder toParenthesize) {
        return toParenthesize.insert(0, '(').append(')');
    }

}
