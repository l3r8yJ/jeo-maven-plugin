/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.jeo.representation.xmir;

import com.jcabi.xml.XMLDocument;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link XmlInstruction}.
 * @since 0.1
 */
final class XmlInstructionTest {

    /**
     * Default instruction which we use for testing.
     * This XML is compare with all other XMLs.
     */
    private static final String INSTRUCTION = new StringBuilder()
        .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\">")
        .append("<o base=\"string\" data=\"bytes\">1</o>")
        .append("<o base=\"string\" data=\"bytes\">2</o>")
        .append("<o base=\"string\" data=\"bytes\">3</o>")
        .append("</o>")
        .toString();

    @Test
    void comparesSuccessfullyWithSpaces() {
        MatcherAssert.assertThat(
            "Xml Instruction nodes with different empty spaces, but with the same content should be the same, but it wasn't",
            new XmlInstruction(
                new XMLDocument(
                    new StringBuilder()
                        .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\">\n")
                        .append("   <o base=\"string\" data=\"bytes\">1</o>\n")
                        .append("   <o base=\"string\" data=\"bytes\">2</o>\n")
                        .append("   <o base=\"string\" data=\"bytes\">3</o></o>")
                        .toString()
                ).node().getFirstChild()
            ),
            Matchers.equalTo(
                new XmlInstruction(
                    new XMLDocument(
                        XmlInstructionTest.INSTRUCTION
                    ).node().getFirstChild()
                )
            )
        );
    }

    @Test
    void comparesSuccessfullyWithDifferentOpcodeNumber() {
        MatcherAssert.assertThat(
            "Xml Instruction nodes with different opcode number in name, but with the same content should be the same, but it wasn't",
            new XmlInstruction(
                new XMLDocument(
                    new StringBuilder()
                        .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-66\">\n")
                        .append("   <o base=\"string\" data=\"bytes\">1</o>\n")
                        .append("   <o base=\"string\" data=\"bytes\">2</o>\n")
                        .append("   <o base=\"string\" data=\"bytes\">3</o></o>")
                        .toString()
                ).node().getFirstChild()
            ),
            Matchers.equalTo(
                new XmlInstruction(
                    new XMLDocument(
                        XmlInstructionTest.INSTRUCTION
                    ).node().getFirstChild()
                )
            )
        );
    }

    @Test
    void comparesSuccessfullyWithDifferentTextNodes() {
        MatcherAssert.assertThat(
            "Xml Instruction with different arguments should not be equal, but it was",
            new XmlInstruction(
                new XMLDocument(
                    new StringBuilder()
                        .append("<o base=\"opcode\" name=\"INVOKESPECIAL-183-66\">")
                        .append("   <o base=\"string\" data=\"bytes\">32</o>")
                        .append("   <o base=\"string\" data=\"bytes\">23</o>")
                        .append("   <o base=\"string\" data=\"bytes\">14</o></o>")
                        .toString()
                ).node().getFirstChild()
            ),
            Matchers.not(
                Matchers.equalTo(
                    new XmlInstruction(
                        new XMLDocument(
                            XmlInstructionTest.INSTRUCTION
                        ).node().getFirstChild()
                    )
                )
            )
        );
    }

    @Test
    void comparesDeeply() {
        MatcherAssert.assertThat(
            "Xml Instruction with different child content should not be equal, but it was",
            new XmlInstruction(
                new XMLDocument(
                    "<o base=\"opcode\" name=\"INVOKESPECIAL-183-55\"></o>"
                ).node().getFirstChild()
            ),
            Matchers.not(
                Matchers.equalTo(
                    new XmlInstruction(
                        new XMLDocument(
                            XmlInstructionTest.INSTRUCTION
                        ).node().getFirstChild()
                    )
                )
            )
        );
    }

    @Test
    void comparesDifferentInstructions() {
        MatcherAssert.assertThat(
            "Xml Instruction with different content should not be equal, but it was",
            new XmlInstruction(
                new XMLDocument("<o base=\"opcode\" name=\"DUP-89-55\"/>\n")
                    .node()
                    .getFirstChild()
            ),
            Matchers.not(
                Matchers.equalTo(
                    new XmlInstruction(
                        new XMLDocument(
                            XmlInstructionTest.INSTRUCTION
                        ).node().getFirstChild()
                    )
                )
            )
        );
    }
}