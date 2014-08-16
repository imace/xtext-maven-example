/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.xtext.builddsl.lib;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jdt.core.compiler.batch.BatchCompiler;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.xtext.builddsl.lib.JavaCompilerParams;

@SuppressWarnings("all")
public class JavaCompiler {
  public static void javac(final Procedure1<? super JavaCompilerParams> init) {
    final JavaCompilerParams params = new JavaCompilerParams();
    init.apply(params);
    final ArrayList<String> list = CollectionLiterals.<String>newArrayList();
    File _destination = params.getDestination();
    boolean _equals = Objects.equal(_destination, null);
    if (_equals) {
      list.add("-d");
      list.add("none");
    } else {
      list.add("-d");
      File _destination_1 = params.getDestination();
      String _string = _destination_1.toString();
      list.add(_string);
    }
    Collection<File> _classpath = params.getClasspath();
    boolean _isEmpty = _classpath.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      list.add("-classpath");
      Collection<File> _classpath_1 = params.getClasspath();
      String _join = IterableExtensions.join(_classpath_1, ":");
      list.add(_join);
    }
    Collection<File> _sources = params.getSources();
    final Function1<File, String> _function = new Function1<File, String>() {
      public String apply(final File it) {
        return it.toString();
      }
    };
    Iterable<String> _map = IterableExtensions.<File, String>map(_sources, _function);
    Iterables.<String>addAll(list, _map);
    InputOutput.<String>print("compiling Java files...");
    PrintWriter _printWriter = new PrintWriter(System.out);
    PrintWriter _printWriter_1 = new PrintWriter(System.err);
    final boolean result = BatchCompiler.compile(((String[]) ((String[])Conversions.unwrapArray(list, String.class))), _printWriter, _printWriter_1, null);
    if (result) {
      InputOutput.<String>println("success.");
    } else {
      InputOutput.<String>println("failed.");
    }
  }
  
  public static ClassLoader newClasspath(final File... entries) {
    final Function1<File, URL> _function = new Function1<File, URL>() {
      public URL apply(final File it) {
        try {
          URI _uRI = it.toURI();
          return _uRI.toURL();
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    };
    List<URL> _map = ListExtensions.<File, URL>map(((List<File>)Conversions.doWrapArray(entries)), _function);
    return new URLClassLoader(((URL[])Conversions.unwrapArray(_map, URL.class)));
  }
}
