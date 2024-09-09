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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eolang.jeo.representation.MethodName;
import org.eolang.jeo.representation.Signature;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotation;
import org.eolang.jeo.representation.bytecode.BytecodeAnnotations;
import org.eolang.jeo.representation.bytecode.BytecodeClass;
import org.eolang.jeo.representation.bytecode.BytecodeMaxs;
import org.eolang.jeo.representation.bytecode.BytecodeMethod;
import org.eolang.jeo.representation.bytecode.BytecodeMethodProperties;
import org.eolang.jeo.representation.bytecode.BytecodeParameters;
import org.eolang.jeo.representation.directives.DirectivesMethod;
import org.eolang.jeo.representation.directives.DirectivesMethodProperties;
import org.objectweb.asm.Opcodes;
import org.xembly.Directives;
import org.xembly.ImpossibleModificationException;
import org.xembly.Transformers;
import org.xembly.Xembler;

/**
 * XML method.
 *
 * @since 0.1
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
@ToString
@EqualsAndHashCode
public final class XmlMethod {

    /**
     * Method node.
     */
    @ToString.Include
    private final XmlNode node;

    /**
     * Method labels.
     */
    @EqualsAndHashCode.Exclude
    private final AllLabels labels;

    /**
     * Constructor.
     */
    public XmlMethod() {
        this("foo");
    }

    /**
     * Constructor.
     *
     * @param name Method name.
     */
    public XmlMethod(final String name) {
        this(name, Opcodes.ACC_PUBLIC, "()V");
    }

    /**
     * Constructor.
     *
     * @param name Method name.
     * @param access Access modifiers.
     * @param descriptor Method descriptor.
     * @param exceptions Method exceptions.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public XmlMethod(
        final String name,
        final int access,
        final String descriptor,
        final String... exceptions
    ) {
        this(XmlMethod.prestructor(name, access, descriptor, 0, 0, exceptions));
    }

    /**
     * Constructor.
     *
     * @param stack Max stack.
     * @param locals Max locals.
     */
    public XmlMethod(final int stack, final int locals) {
        this(XmlMethod.prestructor("foo", Opcodes.ACC_PUBLIC, "()V", stack, locals));
    }

    /**
     * Constructor.
     * @param xmlnode Method node.
     */
    public XmlMethod(final XmlNode xmlnode) {
        this.node = xmlnode;
        this.labels = new AllLabels();
    }

    /**
     * Convert to bytecode.
     * @param clazz Bytecode class.
     * @return Bytecode method.
     */
    public BytecodeMethod bytecode(final BytecodeClass clazz) {
        return new BytecodeMethod(
            clazz,
            this.trycatchEntries()
                .stream()
                .map(XmlTryCatchEntry::bytecode)
                .collect(Collectors.toList()),
            this.instructions()
                .stream()
                .map(XmlBytecodeEntry::bytecode)
                .collect(Collectors.toList()),
            this.annotations(),
            this.properties(),
            this.defvalue()
                .map(XmlDefaultValue::bytecode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList()),
            this.maxs().map(XmlMaxs::bytecode)
                .orElse(new BytecodeMaxs(0, 0))
        );
    }

    /**
     * Method name.
     * @return Name.
     */
    public String name() {
        return new MethodName(
            new Signature(
                this.node.attribute("name")
                    .orElseThrow(
                        () -> new IllegalStateException("Method 'name' attribute is not present")
                    )
            ).name()
        ).bytecode();
    }

    /**
     * All instructions.
     *
     * @param predicates Predicates to filter instructions.
     * @return Instructions.
     */
    @SafeVarargs
    public final List<XmlBytecodeEntry> instructions(
        final Predicate<XmlBytecodeEntry>... predicates
    ) {
        return this.node.child("base", "seq")
            .child("base", "tuple")
            .children()
            .filter(element -> element.attribute("base").isPresent())
            .map(XmlNode::toEntry)
            .filter(instr -> Arrays.stream(predicates).allMatch(predicate -> predicate.test(instr)))
            .collect(Collectors.toList());
    }

    /**
     * All the method instructions.
     *
     * @return Instructions.
     */
    public List<XmlNode> nodes() {
        return this.node.child("base", "seq")
            .child("base", "tuple")
            .children()
            .collect(Collectors.toList());
    }

    /**
     * Method max stack and locals.
     *
     * @return Maxs.
     */
    Optional<XmlMaxs> maxs() {
        return this.node.optchild("name", "maxs").map(XmlMaxs::new);
    }

    /**
     * Method properties as bytecode.
     *
     * @return Properties.
     */
    BytecodeMethodProperties properties() {
        return new BytecodeMethodProperties(
            this.access(),
            this.name(),
            this.descriptor(),
            this.signature(),
            this.params(),
            this.exceptions()
        );
    }

    /**
     * Checks if method is a constructor.
     *
     * @return True if method is a constructor.
     */
    boolean isConstructor() {
        return this.node.attribute("name").map(s -> s.contains("new")).orElse(false);
    }

    /**
     * Convert to Directives.
     * @return Directives of this method.
     */
    Directives toDirectives() {
        return new Directives().add("o").append(Directives.copyOf(this.node.node())).up();
    }

    /**
     * Clear max stack and max locals.
     *
     * @return Copy of the method without max stack and max locals.
     */
    XmlMethod withoutMaxs() {
        try {
            return new XmlMethod(
                new XmlNode(
                    new Xembler(
                        new Directives()
                            .xpath("./o[@name='maxs']")
                            .remove()
                    ).apply(this.node.node())
                )
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to remove maxs from a method '%s'",
                    this.node
                ),
                exception
            );
        }
    }

    /**
     * Method instructions.
     *
     * @param entries Instructions to add.
     * @return Copy of the method with added instructions.
     */
    XmlMethod withInstructions(final XmlNode... entries) {
        try {
            final Directives directives = new Directives()
                .xpath("./o[@base='seq']/o[@base='tuple']");
            for (final XmlNode entry : entries) {
                directives.add("o").append(Directives.copyOf(entry.node())).up();
            }
            return new XmlMethod(
                new XmlNode(
                    new Xembler(directives).apply(
                        this.withoutInstructions().node.node()
                    )
                )
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to add instructions '%s' to a method '%s'",
                    Arrays.toString(entries),
                    this.node
                ),
                exception
            );
        }
    }

    /**
     * Clear instructions.
     *
     * @return Method without instructions.
     */
    XmlMethod withoutInstructions() {
        try {
            return new XmlMethod(
                new XmlNode(
                    new Xembler(
                        new Directives()
                            .xpath("./o[@base='seq']/o[@base='tuple']/o")
                            .remove()
                    ).apply(this.node.node())
                )
            );
        } catch (final ImpossibleModificationException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to remove instructions from a method '%s'",
                    this.node
                ),
                exception
            );
        }
    }

    /**
     * Method access modifiers.
     *
     * @return Access modifiers.
     */
    private int access() {
        return new HexString(
            this.node.children().collect(Collectors.toList()).get(0).text()
        ).decodeAsInt();
    }

    /**
     * Method descriptor.
     *
     * @return Descriptor.
     */
    private String descriptor() {
        return new HexString(
            this.node.children().collect(Collectors.toList()).get(1).text()
        ).decode();
    }

    /**
     * Method signature.
     *
     * @return Signature.
     */
    private String signature() {
        return new HexString(
            this.node.children().collect(Collectors.toList()).get(2).text()
        ).decode();
    }

    /**
     * Method trycatch entries.
     *
     * @return Trycatch entries.
     */
    private List<XmlTryCatchEntry> trycatchEntries() {
        return this.node.children()
            .filter(
                element -> element.attribute("name")
                    .map(s -> s.contains("trycatchblocks"))
                    .orElse(false))
            .flatMap(XmlNode::children)
            .map(entry -> new XmlTryCatchEntry(entry, this.labels))
            .collect(Collectors.toList());
    }

    /**
     * Method annotations.
     *
     * @return Annotations.
     */
    private List<BytecodeAnnotation> annotations() {
        return this.node.children()
            .filter(element -> element.hasAttribute("name", "annotations"))
            .findFirst()
            .map(XmlAnnotations::new)
            .map(XmlAnnotations::bytecode)
            .map(BytecodeAnnotations::annotations)
            .orElse(new ArrayList<>(0));
    }

    /**
     * Annotation default value.
     *
     * @return Optional XMIR of the default value.
     */
    private Optional<XmlDefaultValue> defvalue() {
        return this.node.optchild("base", "annotation-default-value")
            .map(XmlDefaultValue::new);
    }

    /**
     * Method parameters.
     * @return Parameters.
     */
    private BytecodeParameters params() {
        final AtomicInteger index = new AtomicInteger(0);
        return new BytecodeParameters(
            this.node.children()
                .filter(
                    element -> element.hasAttribute("base", "param")
                        && !element.hasAttribute("name", "maxs")
                )
                .map(element -> new XmlParam(index.getAndIncrement(), element))
                .collect(Collectors.toMap(XmlParam::index, XmlParam::annotations))
        );
    }

    /**
     * Method exceptions.
     *
     * @return Exceptions.
     */
    private String[] exceptions() {
        return this.node.children().collect(Collectors.toList()).get(3)
            .children()
            .map(XmlNode::text)
            .map(HexString::new)
            .map(HexString::decode)
            .toArray(String[]::new);
    }

    /**
     * Create Method XmlNode by directives.
     *
     * @param name Method name.
     * @param access Method access modifiers.
     * @param descriptor Method descriptor.
     * @param stack Max stack.
     * @param locals Max locals.
     * @param exceptions Method exceptions.
     * @return Method XmlNode.
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    private static XmlNode prestructor(
        final String name,
        final int access,
        final String descriptor,
        final int stack,
        final int locals,
        final String... exceptions
    ) {
        final DirectivesMethodProperties props = new DirectivesMethodProperties(
            access, descriptor, "", exceptions
        );
        props.maxs(stack, locals);
        return new XmlNode(
            new Xembler(
                new DirectivesMethod(
                    name,
                    props
                ),
                new Transformers.Node()
            ).xmlQuietly()
        );
    }
}
