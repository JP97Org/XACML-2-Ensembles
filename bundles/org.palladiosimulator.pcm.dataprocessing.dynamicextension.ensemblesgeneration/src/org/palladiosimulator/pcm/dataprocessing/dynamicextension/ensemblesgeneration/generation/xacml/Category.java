package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import com.att.research.xacml.api.XACML3;

/**
 * The category enum contains the XACML attribute categories which are relevant for the generation of
 * the respective component or situation definitions.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public enum Category {
    SUBJECT(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue()), 
    RESOURCE(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE.stringValue()), 
    ENVIRONMENT(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT.stringValue());

    private final String categoryId;

    /**
     * Constructor for the Category enum values.
     * 
     * @param categoryId
     *          - XACML category id
     */
    private Category(final String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the category id.
     * 
     * @return the XACML category id
     */
    public String getCategoryId() {
        return this.categoryId;
    }

    /**
     * Gets the category defined by the category id.
     * 
     * @param categoryId
     *          - the category id
     * @return the category defined by the category id or null if none is found
     */
    public static Category of(final String categoryId) {
        for (final Category category : values()) {
            if (category.getCategoryId().equals(categoryId)) {
                return category;
            }
        }
        return null;
    }
}
