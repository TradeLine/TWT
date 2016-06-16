package org.tlsys.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.bcel.classfile.AnnotationEntry;
import org.apache.bcel.classfile.Annotations;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.AttributeReader;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.DescendingVisitor;
import org.apache.bcel.classfile.ElementValuePair;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.ParameterAnnotationEntry;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.commons.io.IOUtils;

//import org.tlsys.DragomeJsCompiler;
import org.tlsys.Compile;
import org.tlsys.Pass1;
import org.tlsys.annotations.AnnotationReader;
import org.tlsys.ast.*;
import org.tlsys.UnhandledCompilerProblemException;
import org.tlsys.generators.DragomeJavaScriptGenerator;
import org.tlsys.invokedynamic.InvokeDynamicBackporter;
import org.tlsys.type.Signature;
import org.tlsys.utils.ClassUnit;
import org.tlsys.utils.Utils;

public class Parser
{

    public static String getResourcePath(String name)
    {
        name= name.replace('.', '/') + ".class";
        java.net.URL url= Parser.class.getClassLoader().getResource(name);
        if (url == null)
            throw new RuntimeException("Resource not found: " + name);
        return url.getPath();
    }

    private JavaClass jc;

    private ClassUnit fileUnit;

    private static final Logger LOG = Logger.getLogger(Parser.class.getName());

    private final InvokeDynamicBackporter lambdaUsageBackporter= new InvokeDynamicBackporter();

    public Parser(ClassUnit theFileUnit)
    {
        fileUnit= theFileUnit;
        fileUnit.annotations= null;

        AttributeReader r= new AnnotationReader(fileUnit);
        Attribute.addAttributeReader("RuntimeVisibleAnnotations", r);

        try
        {
            InputStream openInputStream= fileUnit.getClassFile().openInputStream();

            String filename= fileUnit.getName();
            byte[] originalByteArray= IOUtils.toByteArray(openInputStream);
            byte[] transformedArray= originalByteArray;

            transformedArray= lambdaUsageBackporter.transform(filename, transformedArray);

            if (Compile.getInstance().bytecodeTransformer != null)
                if (Compile.getInstance().bytecodeTransformer.requiresTransformation(filename))
                    transformedArray= Compile.getInstance().bytecodeTransformer.transform(filename, transformedArray);

            fileUnit.setBytecodeArrayI(transformedArray);

            ClassParser cp= new ClassParser(new ByteArrayInputStream(transformedArray), filename);
            jc= cp.parse();

        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    public TypeDeclaration parse()
    {
        DescendingVisitor classWalker= new DescendingVisitor(jc, new EmptyVisitor()
        {
            public void visitConstantClass(ConstantClass obj)
            {
                ConstantPool cp= jc.getConstantPool();
                String bytes= obj.getBytes(cp);
                fileUnit.addDependency(bytes.replace("/", "."));
            }
        });
        classWalker.visit();

        org.apache.bcel.classfile.Method[] bcelMethods= jc.getMethods();
        org.apache.bcel.classfile.Field[] bcelFields= jc.getFields();

        ObjectType type= new ObjectType(jc.getClassName());
        Map<String, String> annotationsValues= getAnnotationsValues(jc.getAttributes(), "::::");

        for (Field field : bcelFields)
        {
            Attribute[] attributes= field.getAttributes();
            String name= ":" + field.getName() + ":::";
            Map<String, String> methodAnnotationsValues= getAnnotationsValues(attributes, name);
            annotationsValues.putAll(methodAnnotationsValues);
        }

        for (Method method : bcelMethods)
        {
            Attribute[] attributes= method.getAttributes();
            String name= "::" + method.getName() + ":";
            Map<String, String> methodAnnotationsValues= getAnnotationsValues(attributes, name + ":");

            ParameterAnnotationEntry[] parameterAnnotationEntries= method.getParameterAnnotationEntries();
            for (int i= 0; i < parameterAnnotationEntries.length; i++)
            {
                AnnotationEntry[] annotationEntries= parameterAnnotationEntries[i].getAnnotationEntries();
                putEntries(name + "arg" + i + ":", annotationsValues, annotationEntries);
            }

            annotationsValues.putAll(methodAnnotationsValues);
        }

        TypeDeclaration typeDecl= new TypeDeclaration(type, jc.getAccessFlags(), annotationsValues);
        fileUnit.isInterface= Modifier.isInterface(typeDecl.getAccess());

        fileUnit.isAbstract= Modifier.isAbstract(typeDecl.getAccess());

        Compile.getInstance().addTypeAnnotations(typeDecl);

        fileUnit.setAnnotations(annotationsValues);

        if (!type.getClassName().equals("java.lang.Object"))
        {

            ObjectType superType= new ObjectType(jc.getSuperclassName());
            typeDecl.setSuperType(superType);
            ClassUnit superUnit= Compile.getInstance().getOrCreateClassUnit(superType.getClassName());
            fileUnit.setSuperUnit(superUnit);

            String[] interfaceNames= jc.getInterfaceNames();
            for (int i= 0; i < interfaceNames.length; i++)
            {
                ObjectType interfaceType= new ObjectType(interfaceNames[i]);
                ClassUnit interfaceUnit= Compile.getInstance().getOrCreateClassUnit(interfaceType.getClassName());
                fileUnit.addInterface(interfaceUnit);
            }
        }

        Field[] fields= jc.getFields();
        for (int i= 0; i < fields.length; i++)
        {
            Field field= fields[i];
            VariableDeclaration variableDecl= new VariableDeclaration(VariableDeclaration.NON_LOCAL);
            variableDecl.setName(field.getName());
            variableDecl.setModifiers(field.getModifiers());
            variableDecl.setType(field.getType());

            typeDecl.addField(variableDecl);
        }

        for (int i= 0; i < bcelMethods.length; i++)
        {
            Method method= bcelMethods[i];

            Map<String, String> methodAnnotationsValues= null;

            try
            {
                methodAnnotationsValues= checkSuperAnnotations(method, jc, "MethodAlias", 0, 4);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            MethodBinding binding= MethodBinding.lookup(jc.getClassName(), method.getName(), method.getSignature());

            String genericSignature= method.getGenericSignature();
            if (genericSignature != null && !genericSignature.equals(method.getSignature()))
            {
                Signature signature= Compile.getInstance().getSignature(binding.toString()).relative();
                String normalizedSignature= DragomeJavaScriptGenerator.normalizeExpression(signature);
                String normalizedClassname= DragomeJavaScriptGenerator.normalizeExpression(type.getClassName());
                Compile.getInstance().addGenericSignature(normalizedClassname + "|" + normalizedSignature + "|" + genericSignature);
                //		System.out.println(genericSignature);
            }

            if (Compile.getInstance().getSingleEntryPoint() != null)
            {
                Signature signature= Compile.getInstance().getSignature(binding.toString());
                String singleSignature= Compile.getInstance().getSingleEntryPoint();
                if (!signature.toString().equals(singleSignature))
                    continue;
            }

            MethodDeclaration methodDecl= new MethodDeclaration(binding, method.getAccessFlags(), method.getCode(), methodAnnotationsValues);
            typeDecl.addMethod(methodDecl);

            parseMethod(typeDecl, methodDecl, method);
        }

        return typeDecl;
    }

    // Recursive algorithm to check for annotations. If the super method has the annotation it will use it. if the child has it, it will skip the super method.
    private Map<String, String> checkSuperAnnotations(final Method method, JavaClass curClass, final String annotationName, int nDepth, final int maxRecursive) throws ClassNotFoundException
    {
        String methodName= method.getName();
        nDepth++;
        Map<String, String> curAnnotationsValues= null;

        Method curMethod= null;
        Method[] methods= curClass.getMethods();
        for (int j= 0; j < methods.length; j++)
        { // find the method if there is one.
            if (methods[j].getName().equals(methodName) && methods[j].getArgumentTypes().length == method.getArgumentTypes().length)
            {
                curMethod= methods[j];
                break;
            }
        }

        if (curMethod != null)
        {
            Attribute[] attributes= curMethod.getAttributes();
            curAnnotationsValues= getAnnotationsValues(attributes, "");

            Set<Map.Entry<String, String>> entrySet= curAnnotationsValues.entrySet();
            Iterator<Map.Entry<String, String>> iterator= entrySet.iterator();
            while (iterator.hasNext())
            {
                Map.Entry<String, String> next= iterator.next();
                String key= next.getKey();
                if (key.contains(annotationName))
                    return curAnnotationsValues; // if contains the annotation already skip checking the super methods.
            }
        }
        else
            curAnnotationsValues= new LinkedHashMap<String, String>();

        if (nDepth >= maxRecursive)
            return curAnnotationsValues;

        JavaClass[] interfaces= curClass.getInterfaces();
        for (int i= 0; i < interfaces.length; i++)
        { // check interfaces
            JavaClass javaClass= interfaces[i];
            Map<String, String> returnedAnnotation= checkSuperAnnotations(method, javaClass, annotationName, nDepth, maxRecursive);
            mergeAnno(curAnnotationsValues, returnedAnnotation, annotationName);
        }
        JavaClass superClass= curClass.getSuperClass(); // check super class
        if (superClass != null && superClass.getClassName().contains("java.lang.Object") == false)
        { // stop checking super when It detects root java object.
            Map<String, String> returnedAnnotation= checkSuperAnnotations(method, superClass, annotationName, nDepth, maxRecursive);
            mergeAnno(curAnnotationsValues, returnedAnnotation, annotationName);
        }
        return curAnnotationsValues;
    }

    private void mergeAnno(Map<String, String> curAnnotationsValues, Map<String, String> returnedAnnotation, String annoationName)
    {
        Set<Map.Entry<String, String>> entrySet= returnedAnnotation.entrySet();
        Iterator<Map.Entry<String, String>> iterator= entrySet.iterator();
        while (iterator.hasNext())
        {
            Map.Entry<String, String> next= iterator.next();
            String key= next.getKey();
            String value= next.getValue();
            if (key.contains(annoationName))
            {
                boolean containsKey= curAnnotationsValues.containsKey(key);
                if (containsKey == false)
                    curAnnotationsValues.put(key, value);
            }
        }
    }

    private Map<String, String> getAnnotationsValues(Attribute[] attributes, String prefix)
    {
        Map<String, String> result= new LinkedHashMap<String, String>();
        for (Attribute attribute : attributes)
        {
            if (attribute instanceof Annotations)
            {
                Annotations annotations= (Annotations) attribute;
                AnnotationEntry[] entries= annotations.getAnnotationEntries();
                List<AnnotationEntry> newEntries= new ArrayList<AnnotationEntry>();
                putEntries(prefix, result, entries);
            }
        }
        return result;
    }
    private void putEntries(String prefix, Map<String, String> result, AnnotationEntry[] entries)
    {
        for (AnnotationEntry entry : entries)
        {
            String key= Type.getType(entry.getAnnotationType()) + "#" + prefix;

            if (entry.getElementValuePairs().length == 0)
                result.put(key, " ");

            for (int i= 0; i < entry.getElementValuePairs().length; i++)
            {
                ElementValuePair elementValuePair= entry.getElementValuePairs()[i];
                result.put(key + elementValuePair.getNameString(), elementValuePair.getValue().toString());
            }
        }
    }

    public void parseMethod(TypeDeclaration typeDecl, MethodDeclaration methodDecl, Method method)
    {
        Type[] types= method.getArgumentTypes();

        int offset;
        if (Modifier.isStatic(methodDecl.getAccess()))
        {
            offset= 0;
        }
        else
        {

            offset= 1;
        }
        for (int i= 0; i < types.length; i++)
        {
            VariableDeclaration variableDecl= new VariableDeclaration(VariableDeclaration.LOCAL_PARAMETER);
            variableDecl.setName(VariableDeclaration.getLocalVariableName(method, offset, 0));
            variableDecl.setType(types[i]);
            methodDecl.addParameter(variableDecl);
            offset+= types[i].getSize();
        }

        if (methodDecl.getCode() == null)
            return;

        LOG.info("Parsing " + methodDecl.toString());
        Pass1 pass1= new Pass1(jc);

        try
        {
            pass1.parse(method, methodDecl);
        }
        catch (Throwable ex)
        {
            if (ex instanceof UnhandledCompilerProblemException)
            {
                Pass1.setClassNotReversible(methodDecl);
            }
            else
            {
                ASTNode node= null;
                if (ex instanceof ParseException)
                {
                    node= ((ParseException) ex).getAstNode();
                }
                else
                {
                    node= Pass1.getCurrentNode();
                }

                /*
                if (DragomeJsCompiler.compiler.isFailOnError())
                {
                */
                    throw Utils.generateException(ex, methodDecl, node);
                /*
                }
                else
                {
                    fileUnit.addNotReversibleMethod(Pass1.extractMethodNameSignature(methodDecl.getMethodBinding()));
                    //String msg= Utils.generateExceptionMessage(methodDecl, node);
                    //DragomeJsCompiler.errorCount++;
                    //		    Log.getLogger().error(msg + "\n" + Utils.stackTraceToString(ex));
                }
                */

            }
            Block body= new Block();
            ThrowStatement throwStmt= new ThrowStatement();
			/*
			MethodBinding binding= MethodBinding.lookup("java.lang.RuntimeException", "<init>", "(java/lang/String)V;");
			ClassInstanceCreation cic= new ClassInstanceCreation(methodDecl, binding);
			cic.addArgument(new StringLiteral("Unresolved decompilation problem"));
			throwStmt.setExpression(cic);
			body.appendChild(throwStmt);*/
            methodDecl.setBody(body);

        }

        if (Compile.getInstance().optimize && methodDecl.getBody().getLastChild() instanceof ReturnStatement)
        {
            ReturnStatement ret= (ReturnStatement) methodDecl.getBody().getLastChild();
            if (ret.getExpression() == null)
            {
                methodDecl.getBody().removeChild(ret);
            }
        }

        //		Pass1.dump(methodDecl.getBody(), "Body of " + methodDecl.toString());

        return;
    }

    public ConstantPool getConstantPool()
    {
        return jc.getConstantPool();
    }

    public String toString()
    {
        return jc.getClassName();
    }
}