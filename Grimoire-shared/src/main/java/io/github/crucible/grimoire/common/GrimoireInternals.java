package io.github.crucible.grimoire.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Throwables;

import io.github.crucible.grimoire.common.api.lib.Environment;

public class GrimoireInternals {

    public static void executeInEnvironment(Environment side, Supplier<Runnable> supplier) {
        side.execute(supplier);
    }

    @SuppressWarnings("unchecked")
    public static <T> void ifInstance(Object obj, Class<T> ofClass, Consumer<T> consumer) {
        if (ofClass.isAssignableFrom(obj.getClass())) {
            consumer.accept((T) obj);
        }
    }

    public static Environment getEnvironment() {
        return GrimoireCore.INSTANCE.getEnvironment();
    }

    public static String getMD5Digest(File file) {
        if (file.exists() && file.isFile()) {
            try {
                InputStream stream = new FileInputStream(file);
                String md5 = DigestUtils.md5Hex(stream);
                stream.close();

                return md5;
            } catch (Exception ex) {
                Throwables.propagate(ex);
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> void cast(Object object, Class<T> castClass, Consumer<T> onCast) {
        onCast.accept((T)object);
    }

    public static String sanitizePath(String URLPath) {
        return URLPath.replace("file:/", "").replace("%20", " ").replace("%5B", "[").replace("%5D", "]")
                .replace("%5b", "[").replace("%5d", "]");
    }

}