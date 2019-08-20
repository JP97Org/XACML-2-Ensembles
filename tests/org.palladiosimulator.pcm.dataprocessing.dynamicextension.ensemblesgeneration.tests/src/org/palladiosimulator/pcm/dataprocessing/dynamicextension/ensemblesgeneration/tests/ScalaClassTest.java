package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaClass;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;

public class ScalaClassTest {
    private static final List<ValueDeclaration> VAL_DECL = Arrays.asList(new ValueDeclaration("v1", "t1"),
            new ValueDeclaration("v2", "t2"));
    
    private ScalaClass scalaClass;
    private ScalaClass scalaObject;
    
    @Before
    public void setUp() {
        this.scalaClass = new ScalaClass(false, "name", "superClass");
        this.scalaObject = new ScalaClass(true, "name", "superClass");
    }
    
    /**
     * Tests a class definition.
     */
    @Test
    public void classTest() {
        Assert.assertEquals("class name extends superClass", this.scalaClass.getCodeDefinition().toString());
    }
    
    /**
     * Tests the addAllAttributes method for classes.
     */
    @Test
    public void addAttributesClassTest() {
        this.scalaClass.addAllAttributes(VAL_DECL);
        final StringJoiner valDeclStr = new StringJoiner(", ","","");
        for (var val : VAL_DECL) {
            valDeclStr.add(val.getCodeDefinition());
        }
        final String expected = "class name(" + valDeclStr + ") extends superClass";
        Assert.assertEquals(expected, this.scalaClass.getCodeDefinition().toString());
    }

    /**
     * Tests a singleton definition.
     */
    @Test
    public void objectTest() {
        Assert.assertEquals("object name extends superClass", this.scalaObject.getCodeDefinition().toString());
    }
    
    /**
     * Tests the addAllAttributes method for objects.
     */
    @Test
    public void addAttributesObjectTest() {
        this.scalaObject.addAllAttributes(VAL_DECL);
        final StringJoiner valDeclStr = new StringJoiner(", ","","");
        for (var val : VAL_DECL) {
            valDeclStr.add(val.getCodeDefinition());
        }
        final String expected = "object name(" + valDeclStr + ") extends superClass";
        Assert.assertEquals(expected, this.scalaObject.getCodeDefinition().toString());
    }
}
