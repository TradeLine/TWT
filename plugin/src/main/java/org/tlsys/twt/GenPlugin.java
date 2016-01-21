package org.tlsys.twt;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.JavacTask;
import com.sun.tools.javac.api.JavacTool;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.tlsys.lex.Value;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.CodeGenerator;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;
import org.tlsys.lex.MethodNotFoundException;
import org.tlsys.lex.VVar;
import org.tlsys.lex.declare.*;

import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

@Mojo(name = "gen", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class GenPlugin extends AbstractMojo {

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepository;

    @Component
    private MavenProject project;
    private PomLoader loader;
    private DependencyNode rootNode;
    private PomClassLoader projectClassLoader;
    @Component
    private ArtifactFactory artifactFactory;
    @Component
    private ArtifactCollector artifactCollector;
    @Component
    private DependencyTreeBuilder treeBuilder;
    @Component
    private ArtifactMetadataSource artifactMetadataSource;

    public GenPlugin(ArtifactRepository localRepo, MavenProject project, DependencyNode rootNode) {
        this.project = project;
        loader = new PomLoader();
        this.rootNode = rootNode;
        this.localRepository = localRepo;
    }

    public GenPlugin() {
    }

    public static Collection<File> getCompileClasses(File file, SourceProvider sourceProvider) throws IOException {
        if (!file.isDirectory())
            return null;
        HashSet<File> out = new HashSet<>();
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                Collection<File> aa = getCompileClasses(f, sourceProvider);
                if (aa != null)
                    out.addAll(aa);
                continue;
            }
            if (!f.isFile())
                continue;
            if (!f.getName().endsWith(".class"))
                continue;
            try (FileInputStream fis = new FileInputStream(f)) {
                ClassReader cr = new ClassReader(fis);
                ClassNode classNode = new ClassNode();
                cr.accept(classNode, 0);

                if (classNode.visibleAnnotations != null)
                    for (Object o : classNode.visibleAnnotations) {
                        AnnotationNode an = (AnnotationNode) o;
                        if (an.desc.equals("L" + JSClass.class.getName().replace('.', '/') + ";")) {
                            if (classNode.name.contains("$")) {
                                String cn = classNode.name.substring(0, classNode.name.indexOf("$")).replace('/', '.');
                                File ff = sourceProvider.getSourceForClass(cn);
                                if (ff != null)
                                    out.add(ff);
                            } else {
                                File ff = sourceProvider.getSourceForClass(classNode.name.replace('/', '.'));
                                if (ff != null)
                                    out.add(ff);
                            }
                        }
                    }
            }
        }

        return out;
    }


    public PomLoader getLoader() {
        return loader;
    }

    public PomClassLoader getProjectClassLoader() {
        return projectClassLoader;
    }

    public void process() throws MojoExecutionException, MojoFailureException {
        Objects.requireNonNull(rootNode, "Root node project is NULL");
        String s = project.getBuild().getOutputDirectory();
        try {
            String nn = project.getGroupId() + "-" + project.getArtifactId() + "-" + project.getVersion();
            projectClassLoader = new PomClassLoader(localRepository, rootNode, new File(project.getFile().getParent() + File.separator + "target" + File.separator + "classes"), nn, loader);
            VClassLoader classLoader = new VClassLoader(nn);
            for (PomClassLoader p : projectClassLoader.parents) {
                if (p.getJSClassLoader() != null)
                    classLoader.parents.add(p.getJSClassLoader());
            }
            projectClassLoader.setJsClassLoader(classLoader);


            JavaCompiler compiler = JavacTool.create();

            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, Charset.forName("UTF-8"));
            ProjectFileManager projectFileManager = new ProjectFileManager(fileManager, projectClassLoader);


            Collection<File> ff = getCompileClasses(new File(s), name -> {

                String classFilePath = name.replace('.', File.separatorChar) + ".java";
                File f = new File(project.getFile().getParent() + "/src/main/java" + File.separator + classFilePath);
                if (f.isFile()) {
                    return f;
                }
                return null;
            });

            Iterable<? extends JavaFileObject> fileObjects = projectFileManager.getStandardFileManager().getJavaFileObjectsFromFiles(ff);

            List<String> options = Arrays.asList("-proc:none");

            JavaCompiler.CompilationTask task = compiler.getTask(null, projectFileManager, null, options, null, fileObjects);
            JavacTask javacTask = (JavacTask) task;
            Types types = javacTask.getTypes();
            Iterator<? extends CompilationUnitTree> it = javacTask.parse().iterator();
            ArrayList<CompilationUnitTree> compiled = new ArrayList<>();
            while (it.hasNext()) {
                CompilationUnitTree cu = it.next();
                compiled.add(cu);
            }

            javacTask.analyze();

            ArrayList<Pair> pairs = new ArrayList<>();

            for (CompilationUnitTree c : compiled) {
                analise(null, classLoader, c, pairs);
            }

            for (Pair p : pairs) {
                analiseExtends(p, classLoader);
            }

            for (Pair p : pairs) {
                analiseDef(p, classLoader);
            }

            for (Pair p : pairs) {
                Compiller compiller1 = new Compiller(classLoader, p.vclass);
                for (Map.Entry<JCTree, Member> t : p.members.entrySet())
                    if (t.getValue() instanceof VExecute)
                        analizeCode(compiller1, t.getValue(), t.getKey(), classLoader);
            }

            for (Pair p : pairs) {
                Compiller compiller1 = new Compiller(classLoader, p.vclass);
                for (Map.Entry<JCTree, Member> t : p.members.entrySet())
                    if (t.getValue() instanceof VVar)
                        analizeCode(compiller1, t.getValue(), t.getKey(), classLoader);
            }

            for (Pair p : pairs) {
                Compiller compiller1 = new Compiller(classLoader, p.vclass);
                for (Map.Entry<JCTree, Member> t : p.members.entrySet())
                    if (t.getValue() instanceof StaticBlock)
                        analizeCode(compiller1, t.getValue(), t.getKey(), classLoader);
            }


        } catch (Throwable e) {
            throw new MojoExecutionException("Generate error", e);
        }
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ArtifactFilter artifactFilter = new ScopeArtifactFilter(null);
        try {
            rootNode = treeBuilder.buildDependencyTree(project,
                    localRepository, artifactFactory, artifactMetadataSource,
                    artifactFilter, artifactCollector);
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("Dependency error", e);
        }

        loader = new PomLoader();
        process();
        //Save lib file
        try (FileOutputStream fout = new FileOutputStream(new File(project.getBuild().getOutputDirectory() + File.separator + PomClassLoader.JSLIB))) {
            projectClassLoader.saveJSClassLoader(fout);
        } catch (Throwable e) {
            throw new MojoExecutionException("Generate error", e);
        }
    }

    public void analizeCode(Compiller com, Member member, JCTree tree, VClassLoader loader) throws VClassNotFoundException, MethodNotFoundException {
        if (tree instanceof JCTree.JCVariableDecl) {
            JCTree.JCVariableDecl v = (JCTree.JCVariableDecl) tree;
            VField f = (VField) member;
            if (v.init == null)
                f.init = com.init(f.getType());
            else
                f.init = com.op(v.init, f.getParent());
            return;
        }

        if (member instanceof VExecute) {
            com.exeCode((VExecute) member, (JCTree.JCMethodDecl) tree);
            return;
        }

        throw new RuntimeException("Code analize for " + tree.getClass().getName() + " not ready yet");
    }

    public void analiseExtends(Pair p, VClassLoader loader) throws VClassNotFoundException {
        JCTree.JCExpression ex = p.desl.getExtendsClause();
        if (ex != null) {
            p.vclass.extendsClass = loader.loadClass(ex.type.tsym.toString());
        } else
            p.vclass.extendsClass = loader.loadClass(java.lang.Object.class.getName());
        if (p.vclass.alias != null && p.vclass.alias.equals(java.lang.Object.class.getName()))
            p.vclass.extendsClass = null;
        for (JCTree.JCExpression e : p.desl.implementing) {
            p.vclass.implementsList.add(loader.loadClass(e.type.tsym.toString()));
        }
    }

    public void analiseDef(Pair p, VClassLoader loader) throws CompileException {
        try {
            Compiller com = new Compiller(loader, p.vclass);
            for (JCTree t : p.desl.defs) {
                Member m = com.memDec(t);
                if (m != null)
                    p.members.put(t, m);
            }
        } catch (VClassNotFoundException e) {
            throw new CompileException("Error analize " + p.vclass.fullName, e);
        }
    }

    public void analizeClass(VClass parent, JCTree.JCClassDecl des, VClassLoader vClassLoader, ArrayList<Pair> pairs) throws MethodNotFoundException, VClassNotFoundException {
        VClass v = analise(parent, des, vClassLoader);
        pairs.add(new Pair(v, des));
        vClassLoader.addClass(v);

        for (JCTree t : des.defs) {
            if (t instanceof JCTree.JCClassDecl) {
                analizeClass(v, (JCTree.JCClassDecl) t, vClassLoader, pairs);
            }
        }
    }

    public void analise(VClass parent, VClassLoader vClassLoader, CompilationUnitTree t, ArrayList<Pair> pairs) throws MethodNotFoundException, VClassNotFoundException {
        for (Tree tt : t.getTypeDecls()) {
            if (tt instanceof JCTree.JCClassDecl) {
                analizeClass(parent, (JCTree.JCClassDecl) tt, vClassLoader, pairs);
            }
        }
    }

    public VClass analise(VClass parent, JCTree.JCClassDecl c, VClassLoader vClassLoader) throws MethodNotFoundException, VClassNotFoundException {
        Iterator<JCTree.JCAnnotation> it = c.getModifiers().annotations.iterator();
        String name = c.name.toString();
        String fullName = c.sym.toString();
        String aliase = null;
        String codeGenerator = null;
        VClass v = new VClass(parent, c.sym);
        v.name = name;
        v.fullName = fullName;
        v.setClassLoader(vClassLoader);
        while (it.hasNext()) {
            JCTree.JCAnnotation an = it.next();
            if (an.type instanceof Type.ClassType) {
                if (an.type.toString().equals(ClassName.class.getName())) {
                    JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                    JCTree.JCLiteral val = (JCTree.JCLiteral) a.getExpression();
                    aliase = (String) val.getValue();
                }
                if (an.type.toString().equals(ReplaceClass.class.getName())) {
                    JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                    JCTree.JCFieldAccess val = (JCTree.JCFieldAccess) a.getExpression();
                    aliase = ""+val.type.toString().substring(Class.class.getName().length()+1);//val.selected.toString();
                    aliase = aliase.substring(0, aliase.length()-1);
                    //aliase = val.selected.toString();
                }
                if (an.type.toString().equals(CodeGenerator.class.getName())) {
                    JCTree.JCAssign a = (JCTree.JCAssign) an.getArguments().get(0);
                    JCTree.JCFieldAccess val = (JCTree.JCFieldAccess) a.getExpression();
                    codeGenerator = ""+val.type.toString().substring(Class.class.getName().length()+1);//val.selected.toString();
                    codeGenerator = codeGenerator.substring(0, codeGenerator.length()-1);
                }
            }
        }



        v.alias = aliase;
        v.codeGenerator = codeGenerator;
        return v;
    }

    private class Pair {
        public final HashMap<JCTree, Member> members = new HashMap<>();
        public VClass vclass;
        public JCTree.JCClassDecl desl;

        public Pair(VClass vclass, JCTree.JCClassDecl desl) {
            this.vclass = vclass;
            this.desl = desl;
        }
    }
}
