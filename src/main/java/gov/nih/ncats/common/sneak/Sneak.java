/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package gov.nih.ncats.common.sneak;

/**
 * Implementation of sneakyThrow originally created by Reinier Zwitserloot
 *
 * @see <a href="http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html">Original Java Posse Post by Reinier Zwitserloot</a>
 * @author dkatzel
 * @since 5.3
 */
public class Sneak {
    public static RuntimeException sneakyThrow(Throwable t) {
        if (t == null)
            throw new NullPointerException("t");
        Sneak.sneakyThrow0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}

