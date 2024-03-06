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

import org.eolang.jeo.representation.HexData;
import org.objectweb.asm.Type;

/**
 * XML operand.
 * @since 0.3
 */
public final class XmlOperand {

    /**
     * Raw XML node which represents an instruction operand.
     */
    private final XmlNode raw;

    /**
     * All found labels.
     */
    private final AllLabels labels;

    /**
     * Constructor.
     * @param node Raw XML operand node.
     */
    public XmlOperand(final XmlNode node) {
        this.raw = node;
        this.labels = new AllLabels();
    }

    public Object asObject() {


        final String attr = this.raw.attribute("base")
            .orElseThrow(
                () -> new IllegalStateException(
                    String.format(
                        "'%s' is not an argument because it doesn't have 'base' attribute",
                        this.raw
                    )
                )
            );
        HexData.DataType.byBase(attr).decode(this.raw.text());


        final Object result;
        if (attr.equals("int")) {
            result = new HexString(this.raw.text()).decodeAsInt();
        } else if (attr.equals("long")) {
            result = new HexString(this.raw.text()).decodeAsLong();
        } else if (attr.equals("label")) {
            result = this.labels.label(
                this.raw.children()
                    .map(XmlNode::text)
                    .map(String::trim)
                    .map(HexString::new)
                    .map(HexString::decode)
                    .findFirst()
                    .orElseThrow()
            );
        } else if (attr.equals("reference")) {
            result = Type.getType(String.format("L%s;", new HexString(this.raw.text()).decode()));
        } else {
            result = new HexString(this.raw.text()).decode();
        }
        return result;
    }
}
