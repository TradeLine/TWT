package org.tlsys.twt.dom;

import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.DomNode;
import org.tlsys.twt.annotations.JSClass;

import java.util.ArrayList;

@JSClass
@DomNode("style")
public class Style implements DomElement {

    private ArrayList<Class> classes = new ArrayList<>();

    public Style() {
        Script.code(this,".type = 'text/css'");
    }

    private static DomElement getOrCreateHead() {
        DomElement[] heads = DOM.getElementsByTagName(Document.get(), "head");
        if (heads.length == 0) {
            DomElement[] htmls = DOM.getElementsByTagName(Document.get(), "html");
            DomElement html = null;
            if (htmls.length == 0) {
                html = Document.get().createElement("html");
                DOM.appendChild(Document.get(), html);
            } else
                html = htmls[0];

            DomElement de = Document.get().createElement("head");
            DOM.appendChild(html, de);
            return de;
        }
        return heads[0];
    }

    public Style attach() {
        if (isAttached()) {
            System.out.println("Style already attached");
            return this;
        }
        DOM.appendChild(getOrCreateHead(), this);
        System.out.println("Style was attached to");
        Script.code("console.info(", getOrCreateHead(), ")");
        return this;
    }

    public Style dettach() {
        if (isAttached())
            return this;
        DOM.removeChild(getOrCreateHead(), this);
        return this;
    }

    public boolean isAttached() {
        return DOM.getParent(this) == getOrCreateHead();
    }

    public Class createClass(String name) {
        Class cl = new Class(name);
        classes.add(cl);
        return cl;
    }

    @Override
    public String toString() {
        String body = "";
        for (Class cl : classes)
            body += cl.toString();
        return body;
    }

    public void update() {
        DOM.setHTML(this, toString());
        System.out.println("CSS=" + toString());

        Script.code("console.dir(",this,")");
    }

    public class Class {
        private final ArrayList<Record> records = new ArrayList<>();
        private String name;

        public Class(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class addRecord(String name, String value) {
            records.add(new Record(name, value));
            return this;
        }

        public Class createClass(String name) {
            return Style.this.createClass(name);
        }

        public Style attach() {
            Style.this.attach();
            return Style.this;
        }

        public Style dettach() {
            Style.this.dettach();
            return Style.this;
        }

        public Style update() {
            System.out.println("CLASS::update...");
            Style.this.update();
            return Style.this;
        }

        @Override
        public String toString() {
            String body = name + " {";

            for (Record r : records)
                body += r.toString() + ";";
            body = body + "}";
            return body;
        }
    }

    public class Record {
        private String name;
        private String value;

        public Record(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String toString() {
            return getName() + ":" + getValue();
        }
    }
}
