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
    
    String FILE="twt_data.bin";

    String getName();

    TWTClassLoader getJavaClassLoader();

    VClassLoader getTWTClassLoader();
    Collection<TWTModule> getParents();
}
