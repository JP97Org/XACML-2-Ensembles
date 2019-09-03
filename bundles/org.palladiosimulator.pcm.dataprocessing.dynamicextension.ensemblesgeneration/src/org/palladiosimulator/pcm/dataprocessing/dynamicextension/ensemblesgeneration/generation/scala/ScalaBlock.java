package org.palladiosimulator.pcm.dataprocessing.dynamicextension.ensemblesgeneration.generation.scala;

import java.util.Iterator;
import java.util.Objects;

/**
 * Represents a scala block with a {@code StringBuilder} definition of pre-block code, a
 * {@code StringBuilder} definition of block code (inside {}) and an optional next block which is
 * appended after the block itself.
 * 
 * @author Jonathan Schenkenberger
 * @version 1.0
 */
public class ScalaBlock implements ScalaCode, Iterable<StringBuilder> {
    private final StringBuilder preBlockCode;
    private final StringBuilder blockCode;
    private ScalaBlock next;

    /**
     * Creates a new empty scala block.
     */
    public ScalaBlock() {
        this.preBlockCode = new StringBuilder();
        this.blockCode = new StringBuilder();
        this.next = null;
    }

    /**
     * Appends the given code definition to the pre-block code.
     * 
     * @param code
     *            - the given code definition
     */
    public void appendPreBlockCode(final StringBuilder code) {
        Objects.requireNonNull(code);
        this.preBlockCode.append(code);
    }

    /**
     * Appends the given code to the pre-block code.
     * 
     * @param code
     *            - the given code as an instance of a {@code ScalaCode} type
     */
    public void appendPreBlockCode(final ScalaCode code) {
        Objects.requireNonNull(code);
        appendPreBlockCode(code.getCodeDefinition());
    }

    /**
     * Appends the given code definition to the block code.
     * 
     * @param code
     *            - the given code definition
     */
    public void appendBlockCode(final StringBuilder code) {
        Objects.requireNonNull(code);
        this.blockCode.append(code);
    }

    /**
     * Appends the given code to the block code.
     * 
     * @param code
     *            - the given code as an instance of a {@code ScalaCode} type
     */
    public void appendBlockCode(final ScalaCode code) {
        Objects.requireNonNull(code);
        appendBlockCode(code.getCodeDefinition());
    }

    /**
     * Sets the given scala block as the following block.
     * 
     * @param next
     *            - the following block
     */
    public void setNext(final ScalaBlock next) {
        this.next = next;
    }
    
    @Override
    public Iterator<StringBuilder> iterator() {
        final ScalaBlock thisBlock = this;
        return new Iterator<StringBuilder>() {
            private ScalaBlock now = thisBlock;
            
            @Override
            public boolean hasNext() {
                return this.now != null;
            }

            @Override
            public StringBuilder next() {
                if (hasNext()) {
                    final StringBuilder before = this.now.getCodeDefinitionOfThisBlock();
                    this.now = this.now.next;
                    return before;
                }
                return null;
            }
        };
    }
    
    /**
     * Gets the code definition of only this block. This method can be used to allow blockwise writing. 
     * 
     * @return the code definition of only this block
     */
    public StringBuilder getCodeDefinitionOfThisBlock() {
        final StringBuilder builder = new StringBuilder();
        return builder.append(this.preBlockCode)
                .append(" {\n")
                .append(this.blockCode)
                .append("\n}\n");
    }

    @Override
    public StringBuilder getCodeDefinition() {
        return getCodeDefinitionOfThisBlock()
                .append(this.next == null ? "" : this.next.getCodeDefinition());
    }

    @Override
    public String toString() {
        return getCodeDefinition().toString();
    }
}
