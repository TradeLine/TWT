package org.tlsys;

import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.tlsys.ast.*;
import org.tlsys.classpath.ClasspathFile;
import org.tlsys.classpath.StreamClasspathFile;
import org.tlsys.type.Signature;
import org.tlsys.type.TypeVisitor;
import org.tlsys.utils.*;

import java.util.*;

/**
 * Created by Субочев Антон on 16.06.2016.
 */
public class Compile {

    private Map<String, Signature> signatures = new LinkedHashMap<String, Signature>();
    private transient Stack<Integer> ids;
    private Map<String, ClassUnit> classesByName= new LinkedHashMap<String, ClassUnit>();

    private transient int currentIndex;

    private transient int currentId;

    private int getUniqueId()
    {
        if (ids == null)
        {
            ids= new Stack<Integer>();
            for (Signature signature : signatures.values())
            {
                ids.add(signature.getId());
            }
            Collections.sort(ids);
        }

        while (currentIndex < ids.size() && ids.get(currentIndex) == currentId)
        {
            currentId+= 1;
            currentIndex+= 1;
        }

        currentId++;
        return currentId - 1;
    }

    private static Compile instance;
    public static Compile getInstance() {
        if (instance == null)
            instance = new Compile();
        return instance;
    }

    private List<String> genericSignatures= new ArrayList<String>();

    public int reductionLevel = 0;

    public Signature getSignature(FieldAccess fa)
    {
        return getSignature(fa.getType().getClassName(), fa.getName());
    }

    public Signature getSignature(String className, String relativeSignature)
    {
        return getSignature(className + '#' + relativeSignature);
    }

    public void addGenericSignature(String genericSignature)
    {
        this.genericSignatures.add(genericSignature);
    }

    public Signature getSignature(String signatureString)
    {
        if (signatureString.endsWith(";"))
        {

        }
        signatureString= signatureString.replaceAll("/", ".");

        Signature signature= signatures.get(signatureString);
        if (signature == null)
        {
            signature= new Signature(signatureString, getUniqueId());
            signatures.put(signatureString, signature);
        }

        return signature;
    }

    public ClassUnit getClassUnit(String className)
    {
        ClassUnit clazz= classesByName.get(className);
        if (clazz != null)
            return clazz;

        throw new RuntimeException("No such unit: " + className);
    }

    private ClassUnit javaLangObject;

    public ClassUnit getOrCreateClassUnit(String className)
    {
        ClassUnit classUnit= classesByName.get(className);
        if (classUnit != null)
            return classUnit;

        Signature signature= getSignature(className);
        classUnit= new ClassUnit(this, signature);
        classesByName.put(className, classUnit);

        if (className.equals("java.lang.Object"))
        {
            javaLangObject= classUnit;
        }

        return classUnit;
    }

    public void visitSuperTypes(ClassUnit clazz, TypeVisitor visitor)
    {
        visitor.visit(clazz);
        ClassUnit superClass= clazz.getSuperUnit();
        if (superClass != null)
        {
            visitSuperTypes(superClass, visitor);
        }

        for (ClassUnit interfaceUnit : clazz.getInterfaces())
        {
            visitor.visit(interfaceUnit);
            visitSuperTypes(interfaceUnit, visitor);
        }
    }

    public int currentGeneratedMethods = 0;

    public ClasspathFile getInputClassFile(String name) {
        return new StreamClasspathFile(Compile.class.getClassLoader().getResourceAsStream(name), name);
    }

    public List<String> writtenSignatures= new ArrayList<String>();
    private List<String> clinits= new ArrayList<String>();
    public List<String> getClinits()
    {
        return clinits;
    }

    public List<String> getWrittenSignatures()
    {
        if (writtenSignatures == null)
            writtenSignatures= new ArrayList<String>();

        return writtenSignatures;
    }

    private MemberUnit getMemberUnitOrNull(String className, Signature signature)
    {
        ClassUnit classUnit= getOrCreateClassUnit(className);
        if (classUnit == null)
            return null;
        return classUnit.getDeclaredMember(signature.toString());
    }

    private MemberUnit getOrCreateMemberUnit(String className, Signature signature, String nameAndSignature)
    {
        MemberUnit member= getMemberUnitOrNull(className, signature);

        if (member == null)
        {
            ClassUnit clazz= getClassUnit(className);
            if (signature.isMethod())
            {
                member= new MethodUnit(signature, clazz, nameAndSignature);
            }
            else if (signature.isConstructor())
            {
                member= new ConstructorUnit(signature, clazz);
            }
            else
            {
                member= new FieldUnit(signature, clazz);
            }

        }

        return member;
    }

    public Signature getArraySignature(Type type)
    {
        String signatureString= type.getSignature();

        if (!signatureString.startsWith("L") || !signatureString.endsWith(";"))
        {
            throw new RuntimeException("Not a class signature: " + signatureString);
        }
        signatureString= signatureString.substring(1, signatureString.length() - 1);
        return getSignature(signatureString);
    }

    public void addReference(MethodDeclaration decl, ArrayCreation ac)
    {
        ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
        Signature signature= getArraySignature(ac.getTypeBinding());
        for (int i= 0; i < ac.getDimensions().size(); i++)
        {

            source.addTarget(getSignature(signature.toString().substring(i) + "#length"));
        }
    }

    public ProcedureUnit getOrCreateProcedureUnit(MethodBinding methodBinding)
    {
        Signature signature= getSignature(methodBinding.getRelativeSignature());
        String className= methodBinding.getDeclaringClass().getClassName();
        return (ProcedureUnit) getOrCreateMemberUnit(className, signature, Pass1.extractMethodNameSignature(methodBinding));
    }

    public void addReference(MethodDeclaration decl, FieldAccess fa)
    {
        ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
        source.addTarget(getSignature(fa));
    }

    public void addReference(MethodDeclaration decl, MethodInvocation invocation)
    {
        ProcedureUnit source= getOrCreateProcedureUnit(decl.getMethodBinding());
        source.addTarget(getSignature(invocation.getMethodBinding().toString()));
    }

    public FieldUnit getOrCreateFieldUnit(ObjectType type, String name)
    {
        return (FieldUnit) getOrCreateMemberUnit(type.getClassName(), getSignature(name), "");
    }

    public BytecodeTransformer bytecodeTransformer;

    public void addTypeAnnotations(TypeDeclaration typeDecl)
    {
        for (Iterator<String> iterator= getTypeDeclarationsWithAnnotations().iterator(); iterator.hasNext();)
        {
            String declaredAnnotation= (String) iterator.next();
            if (declaredAnnotation.equals(typeDecl.getClassName()))
                iterator.remove();
        }

        if (!typeDecl.getAnnotations().isEmpty())
        {
            for (Map.Entry<String, String> entry : typeDecl.getAnnotations().entrySet())
            {
                getTypeDeclarationsWithAnnotations().add(typeDecl.getClassName() + "#" + entry.getKey() + "#" + entry.getValue());

            }
        }
    }

    private Set<String> typeDeclarationsWithAnnotations= new HashSet<String>();

    public Set<String> getTypeDeclarationsWithAnnotations()
    {
        return typeDeclarationsWithAnnotations;
    }

    public boolean optimize = true;

    private String singleEntryPoint;

    public String getSingleEntryPoint()
    {
        return singleEntryPoint;
    }
}
