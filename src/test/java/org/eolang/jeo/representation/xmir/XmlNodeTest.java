/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.jeo.representation.bytecode.BytecodeInstruction;
import org.eolang.jeo.representation.bytecode.BytecodeLabel;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;
import org.xembly.ImpossibleModificationException;
import org.xembly.Xembler;

/**
 * Test case for {@link XmlNode}.
 * @since 0.1
 */
final class XmlNodeTest {

    @Test
    void retrievesTheFirstChild() {
        MatcherAssert.assertThat(
            "Can't retrieve the first child, or the first child is not the expected one",
            new XmlNode("<o><o name='inner'/></o>").firstChild(),
            Matchers.equalTo(new XmlNode("<o name='inner'/>"))
        );
    }

    @Test
    void retrievesChildren() {
        final List<XmlNode> children = new XmlNode("<o><o name='inner1'/><o name='inner2'/></o>")
            .children().collect(Collectors.toList());
        MatcherAssert.assertThat(
            "Size of children is not as expected",
            children,
            Matchers.hasSize(2)
        );
        MatcherAssert.assertThat(
            "Can't retrieve the children, or the children are not the expected ones",
            children,
            Matchers.contains(
                new XmlNode("<o name='inner1'/>"),
                new XmlNode("<o name='inner2'/>")
            )
        );
    }

    @Test
    void retrievesAttribute() {
        final Optional<String> attribute = new XmlNode("<o name='some'/>").attribute("name");
        MatcherAssert.assertThat(
            "Can't retrieve the attribute",
            attribute.isPresent(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            "he attribute is not the expected one",
            attribute.get(),
            Matchers.equalTo("some")
        );
    }

    @Test
    void retrievesText() {
        MatcherAssert.assertThat(
            "Can't retrieve the text, or the text is not the expected one",
            new XmlNode("<o>text</o>").text(),
            Matchers.equalTo("text")
        );
    }

    @Test
    void retrievesChild() {
        final XmlNode child = new XmlNode(
            "<program><o>text</o></program>"
        ).child("o");
        final String expected = "<o>text</o>";
        MatcherAssert.assertThat(
            String.format(
                "Retrieved XML: %s does not match with expected %s",
                child,
                expected
            ),
            child,
            new IsEqual<>(new XmlNode(expected))
        );
    }

    @Test
    void retrievesChildObjects() {
        final List<XmlNode> objects = new XmlNode(
            "<program><o>o1</o><o>o2</o></program>"
        ).children().collect(Collectors.toList());
        final List<XmlNode> expected = new ListOf<>(
            new XmlNode("<o>o1</o>"),
            new XmlNode("<o>o2</o>")
        );
        MatcherAssert.assertThat(
            String.format(
                "Retrieved child objects: %s don't match with expected: %s",
                objects,
                expected
            ),
            objects,
            new IsEqual<>(expected)
        );
    }

    @Test
    void convertsToLabelEntry() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert to label entry",
            new XmlNode(
                new Xembler(
                    new BytecodeLabel("lbl").directives()
                ).xml()
            ).toEntry(),
            Matchers.instanceOf(XmlLabel.class)
        );
    }

    @Test
    void convertsToInstructionEntry() throws ImpossibleModificationException {
        MatcherAssert.assertThat(
            "Can't convert to instruction entry",
            new XmlNode(
                new Xembler(
                    new BytecodeInstruction(Opcodes.ICONST_2).directives()
                ).xml()
            ).toEntry(),
            Matchers.instanceOf(XmlInstruction.class)
        );
    }
}
