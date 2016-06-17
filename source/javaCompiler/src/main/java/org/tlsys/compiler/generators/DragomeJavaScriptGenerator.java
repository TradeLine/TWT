package org.tlsys.compiler.generators;

import org.tlsys.compiler.type.Signature;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public class DragomeJavaScriptGenerator {
    public static String normalizeExpression(Object object)
    {
        if (object instanceof Signature)
        {
            Signature signature= (Signature) object;
            String string= signature.toString();

            string= string.replaceAll("\\[\\]", "_ARRAYTYPE");
            String result= string.replaceAll("\\(\\)", "\\$");
            result= result.replaceAll("\\)", "\\$").replaceAll("\\(", "___").replaceAll("\\.", "_").replaceAll(",", "__").replaceAll("<", "").replaceAll(">", "").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll(";", "\\$");

            if (signature.isMethod() || signature.isConstructor())
            {
                result= "$" + result;

                if (signature.isConstructor())
                {
                    result= result.replaceAll("___$", "");
                    result= result.replace("$init", "$init_");
                    return "$" + result;
                }
                else
                {
                    result= result.replaceAll("___$", "");
                    if (result.contains("clinit"))
                        result= "$" + result + "_";

                    if ("$$clinit$void_".equals(result))
                        result= "$$clinit_";

                    return result;
                }
            }

            return result;
        }
        else
        {
            String string= object.toString();

            string= string.replaceAll("\\[\\]", "_ARRAYTYPE");

            //string= modifyMethodName(string);
            return string.replaceAll("\\(", "_").replaceAll("\\)", "_").replaceAll("\\.", "_").replaceAll(",", "__").replaceAll("<", "_").replaceAll(">", "_").replaceAll("\\[", "_").replaceAll("\\]", "_").replaceAll(";", "\\$");
        }
    }
}
