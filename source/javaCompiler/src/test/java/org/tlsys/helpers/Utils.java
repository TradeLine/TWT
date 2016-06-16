package org.tlsys.helpers;

import java.io.File;

/**
 * Created by Субочев Антон on 15.06.2016.
 */
public class Utils {
    public static final int TEMP_DIR_ATTEMPTS= 10000;

    public static String convertDashedToCamel(String input)
    {
        StringBuilder result= new StringBuilder();
        for (int i= 0; i < input.length(); i++)
        {
            char charToAdd= input.charAt(i);
            if (charToAdd == '-')
                charToAdd= (input.charAt(++i) + "").toUpperCase().charAt(0);
            result.append(charToAdd);
        }
        return result.toString();
    }

    public static File createTempDir()
    {
        File baseDir= new File(System.getProperty("java.io.tmpdir"));
        String baseName= System.currentTimeMillis() + "-";

        for (int counter= 0; counter < TEMP_DIR_ATTEMPTS; counter++)
        {
            File tempDir= new File(baseDir, baseName + counter);
            if (tempDir.mkdir())
            {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried " + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }

    public static File createTempDir(String name)
    {
        File baseDir= new File(System.getProperty("java.io.tmpdir"));

        File tempDir= new File(baseDir, name);
        if (tempDir.exists() || tempDir.mkdir())
            return tempDir;
        else
            throw new IllegalStateException("Failed to create directory within " + baseDir + ": " + name);

    }
}
