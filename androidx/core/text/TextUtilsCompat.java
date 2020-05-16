// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.text;

import android.text.TextUtils;
import android.os.Build$VERSION;
import java.util.Locale;

public final class TextUtilsCompat
{
    private static final Locale ROOT;
    
    static {
        ROOT = new Locale("", "");
    }
    
    private static int getLayoutDirectionFromFirstChar(final Locale inLocale) {
        final byte directionality = Character.getDirectionality(inLocale.getDisplayName(inLocale).charAt(0));
        if (directionality != 1 && directionality != 2) {
            return 0;
        }
        return 1;
    }
    
    public static int getLayoutDirectionFromLocale(final Locale locale) {
        if (Build$VERSION.SDK_INT >= 17) {
            return TextUtils.getLayoutDirectionFromLocale(locale);
        }
        if (locale != null && !locale.equals(TextUtilsCompat.ROOT)) {
            final String maximizeAndGetScript = ICUCompat.maximizeAndGetScript(locale);
            if (maximizeAndGetScript == null) {
                return getLayoutDirectionFromFirstChar(locale);
            }
            if (maximizeAndGetScript.equalsIgnoreCase("Arab") || maximizeAndGetScript.equalsIgnoreCase("Hebr")) {
                return 1;
            }
        }
        return 0;
    }
}
