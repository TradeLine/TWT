package org.tlsys.lex.declare;

import com.sun.tools.javac.code.Symbol;
import org.tlsys.lex.Context;
import org.tlsys.lex.Value;

import java.util.ArrayList;

public class AnnonimusClass extends VClass {
    public AnnonimusClass(Context context, VClass parent, Symbol.ClassSymbol classSymbol) {
        super("an"+Integer.toString(new Object().hashCode()).replace('-','_'), context, parent, classSymbol);
    }

    private ArrayList<Value> inputs = new ArrayList<>();
    private boolean useParent;

    public ArrayList<Value> getInputs() {
        return inputs;
    }

    public boolean isUseParent() {
        return useParent;
    }

    public void setParentContext(Context context) {
        this.parentContext = context;
    }

}
