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
package org.eolang.jeo.representation.asm;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;
import org.xembly.Directives;

/**
 * Data Object Directive in EO language.
 *
 * @since 0.1.0
 */
final class EoData {

    /**
     * Data.
     */
    private final Object data;

    /**
     * Constructor.
     * @param data Data.
     */
    EoData(final Object data) {
        this.data = data;
    }

    /**
     * Directives.
     * @return Directives
     */
    Directives directives() {
        return new Directives().add("o")
            .attr("base", this.type())
            .attr("data", "bytes")
            .set(this.value())
            .up();
    }

    /**
     * Value of the data.
     * @return Value
     */
    private String value() {
        final byte[] res;
        if (this.data instanceof String) {
            res = ((String) this.data).getBytes(StandardCharsets.UTF_8);
        } else {
            res = ByteBuffer
                .allocate(Long.BYTES)
                .putLong((int) this.data)
                .array();
        }
        return EoData.bytesToHex(res);
    }

    /**
     * Type of the data.
     * @return Type
     */
    private String type() {
        final String res;
        if (this.data instanceof String) {
            res = "string";
        } else {
            res = "int";
        }
        return res;
    }

    /**
     * Bytes to HEX.
     *
     * @param bytes Bytes.
     * @return Hexadecimal value as string.
     */
    private static String bytesToHex(final byte... bytes) {
        final StringJoiner out = new StringJoiner(" ");
        for (final byte bty : bytes) {
            out.add(String.format("%02X", bty));
        }
        return out.toString();
    }
}