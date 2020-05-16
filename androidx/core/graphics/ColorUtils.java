// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.graphics.Color;

public final class ColorUtils
{
    private static final ThreadLocal<double[]> TEMP_ARRAY;
    
    static {
        TEMP_ARRAY = new ThreadLocal<double[]>();
    }
    
    public static void RGBToHSL(final int n, final int n2, final int n3, final float[] array) {
        final float n4 = n / 255.0f;
        final float n5 = n2 / 255.0f;
        final float n6 = n3 / 255.0f;
        final float max = Math.max(n4, Math.max(n5, n6));
        final float min = Math.min(n4, Math.min(n5, n6));
        final float n7 = max - min;
        final float n8 = (max + min) / 2.0f;
        float n10;
        float n9;
        if (max == min) {
            n9 = (n10 = 0.0f);
        }
        else {
            float n11;
            if (max == n4) {
                n11 = (n5 - n6) / n7 % 6.0f;
            }
            else if (max == n5) {
                n11 = (n6 - n4) / n7 + 2.0f;
            }
            else {
                n11 = 4.0f + (n4 - n5) / n7;
            }
            final float n12 = n7 / (1.0f - Math.abs(2.0f * n8 - 1.0f));
            n9 = n11;
            n10 = n12;
        }
        float n14;
        final float n13 = n14 = n9 * 60.0f % 360.0f;
        if (n13 < 0.0f) {
            n14 = n13 + 360.0f;
        }
        array[0] = constrain(n14, 0.0f, 360.0f);
        array[1] = constrain(n10, 0.0f, 1.0f);
        array[2] = constrain(n8, 0.0f, 1.0f);
    }
    
    public static void RGBToXYZ(final int n, final int n2, final int n3, final double[] array) {
        if (array.length == 3) {
            final double n4 = n / 255.0;
            double pow;
            if (n4 < 0.04045) {
                pow = n4 / 12.92;
            }
            else {
                pow = Math.pow((n4 + 0.055) / 1.055, 2.4);
            }
            final double n5 = n2 / 255.0;
            double pow2;
            if (n5 < 0.04045) {
                pow2 = n5 / 12.92;
            }
            else {
                pow2 = Math.pow((n5 + 0.055) / 1.055, 2.4);
            }
            final double n6 = n3 / 255.0;
            double pow3;
            if (n6 < 0.04045) {
                pow3 = n6 / 12.92;
            }
            else {
                pow3 = Math.pow((n6 + 0.055) / 1.055, 2.4);
            }
            array[0] = (0.4124 * pow + 0.3576 * pow2 + 0.1805 * pow3) * 100.0;
            array[1] = (0.2126 * pow + 0.7152 * pow2 + 0.0722 * pow3) * 100.0;
            array[2] = (pow * 0.0193 + pow2 * 0.1192 + pow3 * 0.9505) * 100.0;
            return;
        }
        throw new IllegalArgumentException("outXyz must have a length of 3.");
    }
    
    public static int blendARGB(final int n, final int n2, final float n3) {
        final float n4 = 1.0f - n3;
        return Color.argb((int)(Color.alpha(n) * n4 + Color.alpha(n2) * n3), (int)(Color.red(n) * n4 + Color.red(n2) * n3), (int)(Color.green(n) * n4 + Color.green(n2) * n3), (int)(Color.blue(n) * n4 + Color.blue(n2) * n3));
    }
    
    public static double calculateContrast(final int n, final int i) {
        if (Color.alpha(i) == 255) {
            int compositeColors = n;
            if (Color.alpha(n) < 255) {
                compositeColors = compositeColors(n, i);
            }
            final double n2 = calculateLuminance(compositeColors) + 0.05;
            final double n3 = calculateLuminance(i) + 0.05;
            return Math.max(n2, n3) / Math.min(n2, n3);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("background can not be translucent: #");
        sb.append(Integer.toHexString(i));
        throw new IllegalArgumentException(sb.toString());
    }
    
    public static double calculateLuminance(final int n) {
        final double[] tempDouble3Array = getTempDouble3Array();
        colorToXYZ(n, tempDouble3Array);
        return tempDouble3Array[1] / 100.0;
    }
    
    public static int calculateMinimumAlpha(final int n, final int i, final float n2) {
        final int alpha = Color.alpha(i);
        int n3 = 255;
        if (alpha != 255) {
            final StringBuilder sb = new StringBuilder();
            sb.append("background can not be translucent: #");
            sb.append(Integer.toHexString(i));
            throw new IllegalArgumentException(sb.toString());
        }
        final double calculateContrast = calculateContrast(setAlphaComponent(n, 255), i);
        final double n4 = n2;
        if (calculateContrast < n4) {
            return -1;
        }
        for (int n5 = 0, n6 = 0; n5 <= 10 && n3 - n6 > 1; ++n5) {
            final int n7 = (n6 + n3) / 2;
            if (calculateContrast(setAlphaComponent(n, n7), i) < n4) {
                n6 = n7;
            }
            else {
                n3 = n7;
            }
        }
        return n3;
    }
    
    static float circularInterpolate(final float n, final float n2, final float n3) {
        float n4 = n;
        float n5 = n2;
        if (Math.abs(n2 - n) > 180.0f) {
            if (n2 > n) {
                n4 = n + 360.0f;
                n5 = n2;
            }
            else {
                n5 = n2 + 360.0f;
                n4 = n;
            }
        }
        return (n4 + (n5 - n4) * n3) % 360.0f;
    }
    
    public static void colorToHSL(final int n, final float[] array) {
        RGBToHSL(Color.red(n), Color.green(n), Color.blue(n), array);
    }
    
    public static void colorToXYZ(final int n, final double[] array) {
        RGBToXYZ(Color.red(n), Color.green(n), Color.blue(n), array);
    }
    
    private static int compositeAlpha(final int n, final int n2) {
        return 255 - (255 - n2) * (255 - n) / 255;
    }
    
    public static int compositeColors(final int n, final int n2) {
        final int alpha = Color.alpha(n2);
        final int alpha2 = Color.alpha(n);
        final int compositeAlpha = compositeAlpha(alpha2, alpha);
        return Color.argb(compositeAlpha, compositeComponent(Color.red(n), alpha2, Color.red(n2), alpha, compositeAlpha), compositeComponent(Color.green(n), alpha2, Color.green(n2), alpha, compositeAlpha), compositeComponent(Color.blue(n), alpha2, Color.blue(n2), alpha, compositeAlpha));
    }
    
    private static int compositeComponent(final int n, final int n2, final int n3, final int n4, final int n5) {
        if (n5 == 0) {
            return 0;
        }
        return (n * 255 * n2 + n3 * n4 * (255 - n2)) / (n5 * 255);
    }
    
    private static float constrain(final float n, float n2, final float n3) {
        if (n >= n2) {
            n2 = n;
            if (n > n3) {
                n2 = n3;
            }
        }
        return n2;
    }
    
    private static double[] getTempDouble3Array() {
        double[] value;
        if ((value = ColorUtils.TEMP_ARRAY.get()) == null) {
            value = new double[3];
            ColorUtils.TEMP_ARRAY.set(value);
        }
        return value;
    }
    
    public static int setAlphaComponent(final int n, final int n2) {
        if (n2 >= 0 && n2 <= 255) {
            return (n & 0xFFFFFF) | n2 << 24;
        }
        throw new IllegalArgumentException("alpha must be between 0 and 255.");
    }
}
