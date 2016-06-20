package org.tlsys.compiler.utils;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.tlsys.compiler.ast.ASTNode;
import org.tlsys.compiler.ast.MethodDeclaration;
import org.tlsys.twt.nodes.ArrayClassReferance;
import org.tlsys.twt.nodes.ClassReferance;
import org.tlsys.twt.nodes.SimpleClassReferance;
import org.tlsys.twt.nodes.TPrimitive;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

public class Utils {
    private static final Logger LOG = Logger.getLogger(Utils.class.getName());

    private Utils() {
    }

    public static String generateExceptionMessage(MethodDeclaration methodDecl, ASTNode node) {
        String msg = null;
        if (node != null) {
            int line = methodDecl.getLineNumberCursor().getLineNumber(node);
            if (line != -1) {
                msg = "Error near line " + line;
            }
        }
        if (msg == null) {
            msg = "Error";
        }

        msg += " in " + methodDecl.getMethodBinding();

        return msg;
    }

    public static RuntimeException generateException(Throwable e, MethodDeclaration methodDecl, ASTNode node) {
        String msg = generateExceptionMessage(methodDecl, node);
        LOG.severe(msg);
        return new RuntimeException(msg, e);
    }

    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        writer.close();
        return sw.getBuffer().toString();
    }

    public static String currentTimeStamp() {
        return DateFormat.getDateTimeInstance().format(new Date());
    }

    public static String getSignature(Type type) {
        String signature;

        if (type instanceof ArrayType) {
            ArrayType aType = (ArrayType) type;
            signature = getSignature(aType.getBasicType());
            for (int i = 0; i < aType.getDimensions(); i++) {
                signature += "[]";
            }
        } else if (type instanceof ObjectType) {
            signature = ((ObjectType) type).getClassName();
        } else {
            if (!(type instanceof BasicType))
                throw new RuntimeException();
            signature = type.toString();
        }

        return signature;
    }

    public static String escape(String str) {
        int len = str.length();
        StringBuffer buf = new StringBuffer(len + 5);
        char[] ch = str.toCharArray();

        for (int i = 0; i < len; i++) {
            switch (ch[i]) {
                case '\\':
                    buf.append("\\\\");
                    break;
                case '\n':
                    buf.append("\\n");
                    break;
                case '\r':
                    buf.append("\\r");
                    break;
                case '\t':
                    buf.append("\\t");
                    break;
                case '\b':
                    buf.append("\\b");
                    break;
                case '"':
                    buf.append("\\\"");
                    break;
                default:
                    buf.append(ch[i]);
            }
        }

        return '"' + buf.toString() + '"';
    }

    public static File resolve(File baseDir, String path) {
        File resolvedFile = new File(path);
        if (!resolvedFile.isAbsolute()) {
            resolvedFile = new File(baseDir, path);
        }
        return resolvedFile;
    }

    public static ClassReferance getReferanceBySignatyre(String signatyre) {
        Objects.requireNonNull(signatyre, "Signature is NULL");
        if (signatyre.isEmpty())
            throw new IllegalArgumentException("Signature is empty");

        if (signatyre.equals("Z")) {
            return TPrimitive.BOOLEAN.asReferance();
        }
        if (signatyre.equals("B")) {
            return TPrimitive.BYTE.asReferance();
        }
        if (signatyre.equals("C")) {
            return TPrimitive.CHAR.asReferance();
        }
        if (signatyre.equals("S")) {
            return TPrimitive.SHORT.asReferance();
        }
        if (signatyre.equals("I")) {
            return TPrimitive.INT.asReferance();
        }

        if (signatyre.equals("J")) {
            return TPrimitive.LONG.asReferance();
        }

        if (signatyre.equals("F")) {
            return TPrimitive.FLOAT.asReferance();
        }

        if (signatyre.equals("D")) {
            return TPrimitive.DOUBLE.asReferance();
        }

        if (signatyre.charAt(0) == '[')
            return new ArrayClassReferance(getReferanceBySignatyre(signatyre.substring(1)));

        if (signatyre.charAt(0) == 'L') {
            return new SimpleClassReferance(signatyre.substring(1, signatyre.length()-1));
        }

        throw new RuntimeException("Not supported argument! " + signatyre);
    }

    public static ClassReferance getReferance(Type t) {
        return getReferanceBySignatyre(t.getSignature());
    }

}
