package org.tlsys;

import junit.framework.Assert;
import org.junit.Test;
import org.tlsys.loads.ClassRecord;
import org.tlsys.twt.FSSourceProvider;
import org.tlsys.twt.members.VClass;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ConstTest extends TestVM {

    @Test
    public void const_True() throws ScriptException, IOException, URISyntaxException {
        String packageLen = TestVM.class.getPackage().getName();
        String classPath = new File(TestVM.class.getResource("").toURI()).toString();
        File testClasses = new File(classPath.substring(0, classPath.length() - packageLen.length()-2));
        File projectDir = testClasses.getParentFile().getParentFile().getParentFile();
        File mainSource = new File(projectDir, "src" + File.separator + "main" + File.separator + "java");

        FSSourceProvider fs = new FSSourceProvider(mainSource);

        CompileClassLoader loader = new CompileClassLoader();
        JavaSourceSet js = new JavaSourceSet(loader, fs);
        loader.setJavaSourceSet(js);

        JClass classRecord = (JClass)loader.findClassByName(ClassRecord.class.getName()).get();
        classRecord.compileAll();

        for(VClass cl : loader.getClasses()) {
            if (cl instanceof JClass)
                ((JClass) cl).compileAll();
        }

        System.out.println(""+loader);
    }

    @Test
    public void const_False() throws ScriptException, IOException {
        /*
        g.generate(new TestConst(false, boolClass, null), out);
        engine.eval("Assert.assertFalse(" + out.toString() + ")");
        */
    }

    @Test
    public void const_String() throws ScriptException, IOException {
        /*
        final String STR = "Hello from JAVA";
        g.generate(new TestConst(STR, stringClass, null), out);
        engine.eval("Assert.assertEquals(" + out.toString() + ", '" + STR + "')");
        */
    }
}
