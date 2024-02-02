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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.eolang.jeo.representation.JavaName;
import org.eolang.jeo.representation.XmirRepresentation;

/**
 * Disassemble a representation to a file.
 * @since 0.2
 */
public final class Disassemble implements Translation {

    /**
     * Where to save the EO.
     */
    private final Path target;

    /**
     * Constructor.
     * @param target Where to save the EO.
     */
    public Disassemble(final Path target) {
        this.target = target;
    }

    @Override
    public Representation apply(final Representation representation) {
        final String name = new JavaName(representation.details().name()).decode();
        final Path path = this.target
            .resolve(String.format("%s.xmir", name.replace('/', File.separatorChar)));
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, representation.toEO().toString().getBytes(StandardCharsets.UTF_8));
            return new XmirRepresentation(path);
        } catch (final IOException exception) {
            throw new IllegalStateException(
                String.format("Can't save XML to %s", path),
                exception
            );
        }
    }
}
