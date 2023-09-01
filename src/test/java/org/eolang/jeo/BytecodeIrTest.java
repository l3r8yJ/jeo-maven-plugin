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
package org.eolang.jeo;

import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BytecodeIr}.
 *
 * @since 0.1
 */
class BytecodeIrTest {

    /**
     * The name of the resource with the simplest class.
     */
    private static final String METHOD_BYTE = "MethodByte.class";

    @Test
    void parsesBytecode() {
        MatcherAssert.assertThat(
            "The simplest class should contain the object with MethodByte name",
            new BytecodeIr(new ResourceOf(BytecodeIrTest.METHOD_BYTE))
                .toEO().xpath("/o/@name").get(0),
            Matchers.equalTo("org/eolang/jeo/MethodByte")
        );
    }

    @Test
    void returnsTheSameBytes() {
        final ResourceOf input = new ResourceOf(BytecodeIrTest.METHOD_BYTE);
        final byte[] expected = new UncheckedBytes(new BytesOf(input)).asBytes();
        final byte[] actual = new BytecodeIr(input).toBytecode();
        MatcherAssert.assertThat(
            "The bytes should be the same",
            expected,
            Matchers.equalTo(actual)
        );
    }
}