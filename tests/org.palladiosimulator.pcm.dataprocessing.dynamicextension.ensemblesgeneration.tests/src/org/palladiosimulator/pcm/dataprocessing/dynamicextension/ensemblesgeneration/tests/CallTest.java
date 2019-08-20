package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.Call;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;

public class CallTest {
    private static final String NAME = "name";
    private static final String CALL_CONTENT = "content";
    private static final List<ValueDeclaration> VAL_DECL = Arrays.asList(new ValueDeclaration("v1", "t1"),
            new ValueDeclaration("v2", "t2"));

    private Call callOne;
    private Call callTwo;

    @Before
    public void setUp() {
        this.callOne = new Call(NAME, CALL_CONTENT);
        this.callTwo = new Call(NAME, VAL_DECL);
    }
    
    /**
     * Tests first constructor.
     */
    @Test
    public void oneTest() {
        Assert.assertEquals(NAME + "(" + CALL_CONTENT + ")", this.callOne.getCodeDefinition().toString());
    }
    
    /**
     * Tests second constructor.
     */
    @Test
    public void twoTest() {
        final StringJoiner valDeclStr = new StringJoiner(", ","","");
        for (var val : VAL_DECL) {
            valDeclStr.add(val.getName());
        }
        final String expected = NAME + "(" + valDeclStr + ")";
        Assert.assertEquals(expected, this.callTwo.getCodeDefinition().toString());
    }
}
