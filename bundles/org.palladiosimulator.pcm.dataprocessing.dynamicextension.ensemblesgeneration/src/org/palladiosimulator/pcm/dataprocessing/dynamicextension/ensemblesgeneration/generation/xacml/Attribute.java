package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.att.research.xacml.api.XACML3;

/**
 * The attribute enum contains all XACML matches which should be converted to the respective checks
 * in the ensemble system.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum Attribute {
    INTERNALSTATE(Category.RESOURCE, "context:internalstate", "internalstate", Function.STRING_EQUALS), 
    SHIFT_NAME(Category.SUBJECT, "context:shift:name", "shiftName", Function.STRING_EQUALS), 
    ORGANISATION(Category.SUBJECT, "context:organisation", "organisation", Function.STRING_REGEX), 
    ROLE(Category.SUBJECT, "context:role", "role", Function.STRING_REGEX), 
    LOCATION(Category.SUBJECT, "context:location", "location", Function.STRING_REGEX), 
    PRIVACYLEVEL(Category.RESOURCE, "context:privacylevel", "privacylevel", Function.STRING_REGEX), 
    LESS_INT(Category.RESOURCE, "context:comparison:int", "valueInt", Function.LESS_INT), 
    GREATER_INT(Category.RESOURCE, "context:comparison:int", "valueInt", Function.GREATER_INT),
    LESS_DOUBLE(Category.RESOURCE, "context:comparison:double", "valueDouble", Function.LESS_DOUBLE), 
    GREATER_DOUBLE(Category.RESOURCE, "context:comparison:double", "valueDouble", Function.GREATER_DOUBLE),
    LESS_TIME(Category.ENVIRONMENT, XACML3.ID_ENVIRONMENT_CURRENT_TIME.stringValue(), "now", Function.LESS_TIME), 
    GREATER_TIME(Category.ENVIRONMENT, XACML3.ID_ENVIRONMENT_CURRENT_TIME.stringValue(), "now", Function.GREATER_TIME);

    protected static final String TYPE_STRING = "String";
    protected static final String TYPE_INT = "Int";
    protected static final String TYPE_DOUBLE = "Double";
    protected static final String TYPE_TIME = "LocalTime";

    private final Category attributeCategory;
    private final String attributeId;
    private final String scalaAttributeName;
    private final Function function;

    /**
     * Constructor for the Attribute enum values.
     * 
     * @param attributeCategory 
     *          - the category as a Category enum value
     * @param attributeId 
     *          - the id
     * @param scalaAttributeName 
     *          - the scala attribute name
     * @param function 
     *          - the match function as a Function enum value
     */
    private Attribute(final Category attributeCategory, final String attributeId, final String scalaAttributeName,
            final Function function) {
        this.attributeCategory = attributeCategory;
        this.attributeId = attributeId;
        this.scalaAttributeName = scalaAttributeName;
        this.function = function;
    }

    /**
     * Gets the attribute category.
     * 
     * @return the attribute category
     */
    public Category getAttributeCategory() {
        return this.attributeCategory;
    }

    /**
     * Gets the attribute id.
     * 
     * @return the attribute id
     */
    public String getAttributeId() {
        return this.attributeId;
    }

    /**
     * Gets the scala attribute name.
     * 
     * @return the scala attribute name
     */
    public String getScalaAttributeName() {
        return this.scalaAttributeName;
    }

    /**
     * Gets the scala type.
     * 
     * @return the scala type
     */
    public String getScalaType() {
        return this.function.getScalaType();
    }

    /**
     * Gets the match function.
     * 
     * @return the match function
     */
    public Function getFunction() {
        return this.function;
    }

    /**
     * Gets the check code for the given value.
     * 
     * @param value 
     *          - the given value
     * @return the check code for the given value
     */
    protected StringBuilder getCheckCode(final String value) {
        return this.function.getCheckCode(this.scalaAttributeName, value);
    }

    /**
     * Gets an alphabetically (by 1. scala attribute name, 2. match function id) sorted set 
     * of all attributes concerning the given category.
     * 
     * @param category - the given category
     * @return Gets a sorted set of all attributes concerning the given category
     */
    public static SortedSet<Attribute> getCategoryAttributes(final Category category) {
        final var set = new TreeSet<Attribute>(getAlphabeticComparator());

        for (final Attribute attribute : values()) {
            if (attribute.getAttributeCategory().equals(category)) {
                set.add(attribute);
            }
        }

        return set;
    }

    /**
     * Gets the alphabetic comparator for attributes.
     * 
     * @return the alphabetic comparator for attributes
     */
    private static Comparator<? super Attribute> getAlphabeticComparator() {
        final var comparator = new Comparator<Attribute>() {
            @Override
            public int compare(Attribute arg0, Attribute arg1) {
                final int result = arg0.getScalaAttributeName().compareTo(arg1.getScalaAttributeName());
                if (result == 0) {
                    return arg0.getFunction().getMatchId().compareTo(arg1.getFunction().getMatchId());
                }
                return result;
            }
        };

        return comparator;
    }

    /**
     * Gets the attribute defined by the attribute id and the match function.
     * 
     * @param attributeId 
     *          - the attribute ide
     * @param function 
     *          - the match function
     * @return the attribute defined by the attribute id and the match function or null if none found
     */
    public static Attribute of(final String attributeId, final Function function) {
        for (final Attribute attribute : values()) {
            if (attribute.getAttributeId().equals(attributeId) && attribute.getFunction().equals(function)) {
                return attribute;
            }
        }
        return null;
    }
}
