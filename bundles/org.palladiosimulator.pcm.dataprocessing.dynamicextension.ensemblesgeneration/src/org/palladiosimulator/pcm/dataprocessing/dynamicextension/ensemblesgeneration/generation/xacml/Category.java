package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.xacml;

import com.att.research.xacml.api.XACML3;

public enum Category {
	SUBJECT(XACML3.ID_SUBJECT_CATEGORY_ACCESS_SUBJECT.stringValue()),
	RESOURCE(XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE.stringValue()),
	ENVIRONMENT(XACML3.ID_ATTRIBUTE_CATEGORY_ENVIRONMENT.stringValue());
	
	private final String categoryId;
	
	private Category(final String categoryId) {
		this.categoryId = categoryId;
	}
	
	public String getCategoryId() {
		return this.categoryId;
	}
	
	public static Category of(final String categoryId) {
		for (final Category category : values()) {
			if (category.getCategoryId().equals(categoryId)) {
				return category;
			}
		}
		return null;
	}
}
