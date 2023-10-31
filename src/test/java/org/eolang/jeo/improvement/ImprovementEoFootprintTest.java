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
package org.eolang.jeo.improvement;

import com.jcabi.xml.XMLDocument;
import java.nio.file.Path;
import java.util.Collections;
import org.eolang.jeo.representation.EoRepresentation;
import org.hamcrest.MatcherAssert;
import org.hamcrest.io.FileMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test case for {@link ImprovementEoFootprint}.
 * @since 0.1
 */
class ImprovementEoFootprintTest {

    @Test
    void savesSuccessfully(@TempDir final Path temp) {
        new ImprovementEoFootprint(temp).apply(
            Collections.singleton(new EoRepresentation(new XMLDocument("")))
        );
        final Path expected = temp.resolve(
            "generated-sources/eo/org/eolang/jeo/EoRepresentation.eo"
        );
        MatcherAssert.assertThat(
            String.format("The EO file was not saved to the expected location '%s'", expected),
            expected.toFile(),
            FileMatchers.anExistingFile()
        );
    }

}