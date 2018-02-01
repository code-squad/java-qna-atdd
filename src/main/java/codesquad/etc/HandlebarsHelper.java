package codesquad.etc;

import com.github.jknack.handlebars.Options;

import java.io.IOException;

@pl.allegro.tech.boot.autoconfigure.handlebars.HandlebarsHelper
public class HandlebarsHelper {
    public static CharSequence equals(final Object str1, final Options options) throws IOException {
        Object str2 = options.param(0, "arg0");

        if (str1 == null || str2 == null)
            return options.inverse();

        if (str1.equals(str2))
            return options.fn();

        return options.inverse();
    }
}
