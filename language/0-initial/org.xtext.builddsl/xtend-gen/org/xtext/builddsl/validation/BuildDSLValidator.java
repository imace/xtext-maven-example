/**
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.xtext.builddsl.validation;

import com.google.common.base.Objects;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.common.types.TypesPackage;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.XbasePackage;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.validation.XbaseJavaValidator;
import org.eclipse.xtext.xtype.XtypePackage;
import org.xtext.builddsl.build.BuildPackage;
import org.xtext.builddsl.build.Task;

@SuppressWarnings("all")
public class BuildDSLValidator extends XbaseJavaValidator {
  public final static String CYCLIC_DEPENDENCY = "build.issue.cyclicDependency";
  
  protected List<EPackage> getEPackages() {
    return CollectionLiterals.<EPackage>newArrayList(
      BuildPackage.eINSTANCE, 
      XbasePackage.eINSTANCE, 
      TypesPackage.eINSTANCE, 
      XtypePackage.eINSTANCE);
  }
  
  @Check
  public void checkNoRecursiveDependencies(final Task task) {
    final Procedure1<Set<Task>> _function = new Procedure1<Set<Task>>() {
      public void apply(final Set<Task> cycle) {
        int _size = cycle.size();
        boolean _equals = (_size == 1);
        if (_equals) {
          StringConcatenation _builder = new StringConcatenation();
          _builder.append("The task \'");
          String _name = task.getName();
          _builder.append(_name, "");
          _builder.append("\' cannot depend on itself.");
          Task _head = IterableExtensions.<Task>head(cycle);
          BuildDSLValidator.this.error(_builder.toString(), _head, BuildPackage.Literals.DECLARATION__NAME, BuildDSLValidator.CYCLIC_DEPENDENCY);
        } else {
          StringConcatenation _builder_1 = new StringConcatenation();
          _builder_1.append("There is a cyclic dependency that involves tasks ");
          final Function1<Task, String> _function = new Function1<Task, String>() {
            public String apply(final Task it) {
              return it.getName();
            }
          };
          Iterable<String> _map = IterableExtensions.<Task, String>map(cycle, _function);
          String _join = IterableExtensions.join(_map, ", ");
          _builder_1.append(_join, "");
          Task _head_1 = IterableExtensions.<Task>head(cycle);
          BuildDSLValidator.this.error(_builder_1.toString(), _head_1, BuildPackage.Literals.DECLARATION__NAME, BuildDSLValidator.CYCLIC_DEPENDENCY);
        }
      }
    };
    this.findDependentTasks(task, _function);
  }
  
  private Collection<Task> findDependentTasks(final Task it, final Procedure1<? super Set<Task>> cycleHandler) {
    LinkedHashSet<Task> _xblockexpression = null;
    {
      final LinkedHashSet<Task> tasks = CollectionLiterals.<Task>newLinkedHashSet();
      this.internalFindDependentTasksRec(it, tasks);
      final LinkedHashSet<Task> result = CollectionLiterals.<Task>newLinkedHashSet();
      boolean changed = true;
      boolean _while = changed;
      while (_while) {
        {
          changed = false;
          List<Task> _list = IterableExtensions.<Task>toList(tasks);
          for (final Task t : _list) {
            EList<Task> _depends = t.getDepends();
            boolean _containsAll = result.containsAll(_depends);
            if (_containsAll) {
              changed = true;
              result.add(t);
              tasks.remove(t);
            }
          }
        }
        _while = changed;
      }
      boolean _and = false;
      boolean _isEmpty = tasks.isEmpty();
      boolean _not = (!_isEmpty);
      if (!_not) {
        _and = false;
      } else {
        boolean _notEquals = (!Objects.equal(cycleHandler, null));
        _and = _notEquals;
      }
      if (_and) {
        cycleHandler.apply(tasks);
      }
      _xblockexpression = result;
    }
    return _xblockexpression;
  }
  
  private void internalFindDependentTasksRec(final Task task, final Set<Task> set) {
    boolean _add = set.add(task);
    boolean _not = (!_add);
    if (_not) {
      return;
    }
    EList<Task> _depends = task.getDepends();
    for (final Task t : _depends) {
      this.internalFindDependentTasksRec(t, set);
    }
  }
}
