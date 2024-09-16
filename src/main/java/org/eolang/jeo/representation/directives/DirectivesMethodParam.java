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
package org.eolang.jeo.representation.directives;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Directives for a method parameter.
 * @since 0.6
 */
public final class DirectivesMethodParam implements Iterable<Directive> {

    /**
     * Base64 decoder.
     */
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    /**
     * Index of the parameter.
     */
    private final int index;

    /**
     * Type of the parameter.
     */
    private final Type type;

    /**
     * Annotations of the parameter.
     */
    private final List<? extends Iterable<Directive>> annotations;

    /**
     * Constructor.
     * @param index Index of the parameter.
     * @param type Type of the parameter.
     * @param annotations Annotations of the parameter.
     */
    public DirectivesMethodParam(
        final int index,
        final Type type,
        final List<? extends Iterable<Directive>> annotations
    ) {
        this.index = index;
        this.type = type;
        this.annotations = annotations;
    }

    @Override
    public Iterator<Directive> iterator() {
        return new Directives().add("o")
            .attr("base", "param")
            .attr("line", new Random().nextInt(Integer.MAX_VALUE))
            .attr(
                "name",
                String.format(
                    "param-%s-%d",
                    DirectivesMethodParam.ENCODER.encodeToString(
                        this.type.toString().getBytes(StandardCharsets.UTF_8)
                    ),
                    this.index
                )
            )
            .append(
                this.annotations.stream()
                    .map(Directives::new)
                    .reduce(new Directives(), Directives::append)
            )
            .up()
            .iterator();
    }
}
