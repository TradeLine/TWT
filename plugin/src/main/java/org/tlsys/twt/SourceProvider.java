package org.tlsys.twt;

import java.io.File;

public interface SourceProvider {
    public File getSourceForClass(String className) throws SourceNotFoundException;

    public class SourceNotFoundException extends RuntimeException {
        private String className;

        public SourceNotFoundException(String className) {
            this.className = className;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public String getMessage() {
            return "Source \"" + getClassName() + "\" not found";
        }
    }
}
