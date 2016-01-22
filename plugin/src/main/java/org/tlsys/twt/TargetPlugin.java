package org.tlsys.twt;

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
import org.tlsys.lex.declare.VClass;
import org.tlsys.lex.declare.VClassNotFoundException;

import java.io.*;

@Mojo(name = "gen2", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class TargetPlugin extends AbstractMojo {
    @Component
    private MavenProject project;

    @Parameter(required = true)
    private Target[] targets;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepository;

    @Component
    private ArtifactFactory artifactFactory;

    @Component
    private ArtifactCollector artifactCollector;

    @Component
    private DependencyTreeBuilder treeBuilder;

    @Component
    private ArtifactMetadataSource artifactMetadataSource;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ArtifactFilter artifactFilter = new ScopeArtifactFilter(null);

            DependencyNode rootNode = treeBuilder.buildDependencyTree(project,
                    localRepository, artifactFactory, artifactMetadataSource,
                    artifactFilter, artifactCollector);

            GenPlugin plug = new GenPlugin(localRepository, project, rootNode);
            plug.process();

            CodeGenerator.renaming(plug.getProjectClassLoader().getJSClassLoader());

            for (Target t : targets) {
                try (FileOutputStream fout = new FileOutputStream(new File(t.getOut()))) {
                    PrintStream ps = new PrintStream(fout);
                    VClass clazz = plug.getProjectClassLoader().getJSClassLoader().loadClass(t.getMain());
                    CodeGenerator.generate(clazz, t.getMethods(), ps);
                } catch (FileNotFoundException e) {
                    throw new MojoExecutionException("File not found", e);
                } catch (IOException e) {
                    throw new MojoExecutionException("", e);
                } catch (CompileException e) {
                    throw new MojoExecutionException("", e);
                }
            }
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("Dependensy reader error", e);
        }

        /*

        */
    }
}
