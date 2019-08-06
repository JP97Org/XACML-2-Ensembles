package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueInitialisation;

public class ValueInitialisationTest {
    private ValueInitialisation valInit;
    
    @Before
    public void setUp() {
        this.valInit = new ValueInitialisation("name", "expression");
    }
    
    @Test
    public void test() {
        Assert.assertEquals("val name = expression\n", this.valInit.getCodeDefinition().toString());
    }
}
