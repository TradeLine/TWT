/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt.module;

import org.gradle.api.Project;
import org.gradle.api.Task;
import static org.tlsys.twt.compile.CompilePlugin.findAllCompileTaskInDependency;
import org.tlsys.twt.compile.CompileTask;

/**
 *
 * @author subochev
 */
public class ModulePlugin implements org.gradle.api.Plugin<Project> {

    @Override
    public void apply(Project t) {
        Task module = t.getTasks().create("moduleTwt",ModuleTask.class);
        for (Task tt : findAllCompileTaskInDependency(t)) {
            tt.dependsOn(module);
        }
        t.getTasks().getByName("jar").dependsOn(module);
    }
    
}
