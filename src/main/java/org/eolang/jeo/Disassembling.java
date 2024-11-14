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
package org.eolang.jeo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.eolang.jeo.representation.PrefixedName;

/**
 * Disassembling transformation.
 * @since 0.6
 */
public final class Disassembling implements Transformation {

    /**
     * Target folder where to save the disassembled class.
     */
    private final Path folder;

    /**
     * Representation to disassemble.
     */
    private final Representation repr;

    /**
     * Constructor.
     * @param target Target folder.
     * @param representation Representation to disassemble.
     */
    Disassembling(final Path target, final Representation representation) {
        this.folder = target;
        this.repr = representation;
    }

    @Override
    public Path source() {
        return this.repr.details().source().orElseThrow(
            () -> new IllegalStateException(
                String.format(
                    "Source is not defined for disassembling '%s'",
                    this.repr.details()
                )
            )
        );
    }

    @Override
    public Path target() {
        return this.folder.resolve(
            String.format(
                "%s.xmir",
                new PrefixedName(
                    this.repr.details().name()
                ).decode().replace('/', File.separatorChar)
            )
        );
    }

    @Override
    public byte[] transform() {
        return this.repr.toEO().toString().getBytes(StandardCharsets.UTF_8);
    }
}
