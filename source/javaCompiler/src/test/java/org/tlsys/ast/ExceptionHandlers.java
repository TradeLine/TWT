package org.tlsys.ast;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;

import java.util.ArrayList;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class ExceptionHandlers extends ArrayList<ExceptionHandler>
{

    public ExceptionHandlers(Code code)
    {

        CodeException previousCodeException= null;
        for (CodeException codeException : code.getExceptionTable())
        {
            if (previousCodeException != null && previousCodeException.getHandlerPC() == codeException.getHandlerPC())
            {
                previousCodeException.setEndPC(codeException.getEndPC());
            }
            else
            {
                add(new ExceptionHandler(codeException));
            }
            previousCodeException= codeException;
        }
    }
}
