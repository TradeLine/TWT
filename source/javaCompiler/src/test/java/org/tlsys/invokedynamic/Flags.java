package org.tlsys.invokedynamic;

import static org.objectweb.asm.Opcodes.ACC_PRIVATE;

public class Flags {
    public static int makeNonPrivate(int access)
    {
        if (hasFlag(access, ACC_PRIVATE))
        {
            return clearFlag(access, ACC_PRIVATE); // make package-private (i.e. no flag)
        }
        return access;
    }

    public static boolean hasFlag(int subject, int flag)
    {
        return (subject & flag) == flag;
    }

    public static int clearFlag(int subject, int flag)
    {
        return subject & ~flag;
    }
}
