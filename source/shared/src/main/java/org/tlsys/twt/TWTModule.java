/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tlsys.twt;

import java.io.IOException;
import java.util.Collection;
import org.tlsys.lex.declare.VClassLoader;

/**
 * Интерфейс модуля TWT
 * @author Субочев Антон
 */
public interface TWTModule {
    
    public static final String FILE="twt_data.bin";

    public String getName();

    public TWTClassLoader getJavaClassLoader();

    public VClassLoader getTWTClassLoader();
    public Collection<TWTModule> getParents();
}
