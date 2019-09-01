package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.util;

import java.util.Objects;
import java.util.StringJoiner;

import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;

/**
 * Helper Class for creating Scala.
 * 
 * @author majuwa
 * @author Jonathan Schenkenberger
 * @version 1.01
 */
public final class ScalaHelper {
    // keywords and important words
    public static final String KEYWORD_CLASS = "class";
    public static final String KEYWORD_OBJECT = "object";
    public static final String KEYWORD_VAL = "val";
    public static final String KEYWORD_VAR = "var";
    public static final String KEYWORD_EXTENDS = "extends";
    public static final String KEYWORD_STRING = "String";
    public static final String KEYWORD_INT = "Int";
    public static final String KEYWORD_DOUBLE = "Double";
    public static final String KEYWORD_BOOLEAN = "Boolean";
    public static final String KEYWORD_TIME = "LocalTime";
    public static final String KEYWORD_COMPONENT = "Component";
    public static final String KEYWORD_SUBJECT = "Subject";
    public static final String KEYWORD_RESOURCE = "Resource";
    public static final String KEYWORD_ENSEMBLE = "Ensemble";
    public static final String KEYWORD_MODEL = "Model";
    public static final String KEYWORD_ALLOW = "allow";
    public static final String KEYWORD_ENSEMBLE_ROOT = "RootEnsemble";
    public static final String KEYWORD_DEF = "def";
    public static final String KEYWORD_NOW = "now";
    
    // policy set wide / model or system wide constants 
    public static final String MODEL_CLASS_NAME = "RunningExample";
    public static final ValueDeclaration NOW = new ValueDeclaration(KEYWORD_NOW, KEYWORD_TIME);
    public static final String PACKAGE = "package scenarios";
    public static final StringBuilder IMPORTS = new StringBuilder("import tcof.{Component, _}\n")
            .append("import java.time._\n").append("import java.time.format._\n")
            .append("import java.util.Collection\n").append("import java.util.ArrayList\n");
    public static final String SYSTEM = "System";
    public static final String ROOT_ENSEMBLE_NAME = "rootEnsemble";
    public static final String RULE_SUFFIX = "Rule";
    
    // policy wide / ensemble wide constants
    public static final String POLICY_PREFIX = "policy:";
    public static final String COMPONENTS = "components";
    public static final String SUBJECT_FIELD_NAME = "allowedSubjects";
    public static final String RESOURCE_FIELD_NAME = "allowedResources";
    
    // extraction constants
    public static final String VAR_NAME = "x";
    public static final String OR = " || ";
    public static final String AND = " && ";
    public static final String ACCESS_SUBJECT = "accessSubject";
    
    // text obligations constants
    public static final String ID_OBLIGATION_PREREQUISITE = "obligation:prerequisite";
    public static final String PREFIX_PREREQUISITE = "prereq_";
    public static final String ID_IS_END = "context:extension:isend";
    public static final String RETURN_TRUE = "return true";
    
    // constants for scenario definition
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
        Objects.requireNonNull(toParenthesize);
        return toParenthesize.insert(0, '(').append(')');
    }

    /**
     * Parenthesizes the given string joiner and returns a string builder with the content of the joiner.
     * 
     * @param toParenthesize - the given string joiner
     * @return the parenthesized string builder
     */
    public static StringBuilder parenthesize(final StringJoiner toParenthesize) {
        Objects.requireNonNull(toParenthesize);
        return new StringBuilder("(").append(toParenthesize).append(')');
    }
}
