/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author subochev
 */
public class GradleJarModule extends TWTModuleLoader {

    private final String name;
    
    public GradleJarModule(File file, String name) throws IOException {
        super(file);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
