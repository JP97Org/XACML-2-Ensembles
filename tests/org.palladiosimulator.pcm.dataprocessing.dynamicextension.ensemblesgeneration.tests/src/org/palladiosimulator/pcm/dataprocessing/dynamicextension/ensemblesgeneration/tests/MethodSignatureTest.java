package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.MethodSignature;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ValueDeclaration;

public class MethodSignatureTest {
    private static final String NAME = "name";
    private static final String TYPE = "t";
    private static final List<ValueDeclaration> VAL_DECL = Arrays.asList(new ValueDeclaration("v1", "t1"),
            new ValueDeclaration("v2", "t2"));

    private MethodSignature signature;
    private MethodSignature withoutArgs;

    @Before
    public void setUp() {
        this.signature = new MethodSignature(NAME, VAL_DECL, TYPE);
        this.withoutArgs = new MethodSignature(NAME, null, TYPE);
    }
    
    @Test
    public void signatureTest() {
        final StringJoiner valDeclStr = new StringJoiner(", ","","");
        for (var val : VAL_DECL) {
            valDeclStr.add(val.getCodeDefinition().toString().replaceFirst("val ", ""));
        }
        Assert.assertEquals("def " + NAME + "(" + valDeclStr + ") : " + TYPE + " =", this.signature.getCodeDefinition().toString());
    }
    
    @Test
    public void withoutArgsTest() {
        Assert.assertEquals("def " + NAME + "() : " + TYPE + " =", this.withoutArgs.getCodeDefinition().toString());
    }
}
