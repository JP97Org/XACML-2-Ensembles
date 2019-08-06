package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.tests;

import org.junit.*;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaBlock;
import org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala.ScalaCode;

public class ScalaBlockTest {
    private ScalaBlock block;
    
    @Before
    public void setUp() {
        this.block = new ScalaBlock();
    }
    
    @Test
    public void appendPreBlockCodeTestOne() {
        this.block.appendPreBlockCode(new StringBuilder("preBlock"));
        Assert.assertEquals("preBlock {\n\n}\n", this.block.getCodeDefinition().toString());
    }
    
    @Test
    public void appendPreBlockCodeTestTwo() {
        this.block.appendPreBlockCode(new ScalaCode() {
            @Override
            public StringBuilder getCodeDefinition() {
                return new StringBuilder("preBlock");
            }
        });
        Assert.assertEquals("preBlock {\n\n}\n", this.block.getCodeDefinition().toString());
    }
    
    @Test
    public void appendBlockCodeTestOne() {
        this.block.appendBlockCode(new StringBuilder("block"));
        Assert.assertEquals(" {\nblock\n}\n", this.block.getCodeDefinition().toString());
    }
    
    @Test
    public void appendBlockCodeTestTwo() {
        this.block.appendBlockCode(new ScalaCode() {
            @Override
            public StringBuilder getCodeDefinition() {
                return new StringBuilder("block");
            }
        });
        Assert.assertEquals(" {\nblock\n}\n", this.block.getCodeDefinition().toString());
    }
    
    @Test
    public void setNextTest() {
        this.block.setNext(new ScalaBlock());
        Assert.assertEquals(" {\n\n}\n {\n\n}\n", this.block.getCodeDefinition().toString());
    }
    
    @Test
    public void combinedTest() {
        this.block.appendPreBlockCode(new StringBuilder("preBlock"));
        this.block.appendBlockCode(new StringBuilder("block"));
        this.block.setNext(new ScalaBlock());
        Assert.assertEquals("preBlock {\nblock\n}\n {\n\n}\n", this.block.getCodeDefinition().toString());
    }
}
