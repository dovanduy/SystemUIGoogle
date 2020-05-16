// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.graphics;

import android.util.Log;
import android.graphics.Path;
import java.util.ArrayList;

public class PathParser
{
    private static void addNode(final ArrayList<PathDataNode> list, final char c, final float[] array) {
        list.add(new PathDataNode(c, array));
    }
    
    public static boolean canMorph(final PathDataNode[] array, final PathDataNode[] array2) {
        if (array == null || array2 == null) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].mType != array2[i].mType || array[i].mParams.length != array2[i].mParams.length) {
                return false;
            }
        }
        return true;
    }
    
    static float[] copyOfRange(final float[] array, final int n, int a) {
        if (n > a) {
            throw new IllegalArgumentException();
        }
        final int length = array.length;
        if (n >= 0 && n <= length) {
            a -= n;
            final int min = Math.min(a, length - n);
            final float[] array2 = new float[a];
            System.arraycopy(array, n, array2, 0, min);
            return array2;
        }
        throw new ArrayIndexOutOfBoundsException();
    }
    
    public static PathDataNode[] createNodesFromPathData(final String s) {
        if (s == null) {
            return null;
        }
        final ArrayList<PathDataNode> list = new ArrayList<PathDataNode>();
        int i = 1;
        int n = 0;
        while (i < s.length()) {
            i = nextStart(s, i);
            final String trim = s.substring(n, i).trim();
            if (trim.length() > 0) {
                addNode(list, trim.charAt(0), getFloats(trim));
            }
            n = i;
            ++i;
        }
        if (i - n == 1 && n < s.length()) {
            addNode(list, s.charAt(n), new float[0]);
        }
        return list.toArray(new PathDataNode[list.size()]);
    }
    
    public static Path createPathFromPathData(final String str) {
        final Path path = new Path();
        final PathDataNode[] nodesFromPathData = createNodesFromPathData(str);
        if (nodesFromPathData != null) {
            try {
                PathDataNode.nodesToPath(nodesFromPathData, path);
                return path;
            }
            catch (RuntimeException cause) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Error in parsing ");
                sb.append(str);
                throw new RuntimeException(sb.toString(), cause);
            }
        }
        return null;
    }
    
    public static PathDataNode[] deepCopyNodes(final PathDataNode[] array) {
        if (array == null) {
            return null;
        }
        final PathDataNode[] array2 = new PathDataNode[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new PathDataNode(array[i]);
        }
        return array2;
    }
    
    private static void extract(final String s, final int n, final ExtractFloatResult extractFloatResult) {
        extractFloatResult.mEndWithNegOrDot = false;
        int i = n;
        final int n2 = 0;
        int n4;
        int n3 = n4 = n2;
        int n5 = n2;
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            Label_0153: {
                Label_0147: {
                    if (char1 != ' ') {
                        if (char1 != 'E' && char1 != 'e') {
                            switch (char1) {
                                case 46: {
                                    if (n4 == 0) {
                                        n5 = 0;
                                        n4 = 1;
                                        break Label_0153;
                                    }
                                    extractFloatResult.mEndWithNegOrDot = true;
                                    break Label_0147;
                                }
                                case 45: {
                                    if (i != n && n5 == 0) {
                                        extractFloatResult.mEndWithNegOrDot = true;
                                        break Label_0147;
                                    }
                                    break;
                                }
                                case 44: {
                                    break Label_0147;
                                }
                            }
                            n5 = 0;
                            break Label_0153;
                        }
                        n5 = 1;
                        break Label_0153;
                    }
                }
                n5 = 0;
                n3 = 1;
            }
            if (n3 != 0) {
                break;
            }
            ++i;
        }
        extractFloatResult.mEndPosition = i;
    }
    
    private static float[] getFloats(final String str) {
        if (str.charAt(0) != 'z') {
            if (str.charAt(0) != 'Z') {
                try {
                    final float[] array = new float[str.length()];
                    final ExtractFloatResult extractFloatResult = new ExtractFloatResult();
                    final int length = str.length();
                    int i = 1;
                    int n = 0;
                    while (i < length) {
                        extract(str, i, extractFloatResult);
                        final int mEndPosition = extractFloatResult.mEndPosition;
                        int n2 = n;
                        if (i < mEndPosition) {
                            array[n] = Float.parseFloat(str.substring(i, mEndPosition));
                            n2 = n + 1;
                        }
                        if (extractFloatResult.mEndWithNegOrDot) {
                            i = mEndPosition;
                            n = n2;
                        }
                        else {
                            i = mEndPosition + 1;
                            n = n2;
                        }
                    }
                    return copyOfRange(array, 0, n);
                }
                catch (NumberFormatException cause) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("error in parsing \"");
                    sb.append(str);
                    sb.append("\"");
                    throw new RuntimeException(sb.toString(), cause);
                }
            }
        }
        return new float[0];
    }
    
    private static int nextStart(final String s, int i) {
        while (i < s.length()) {
            final char char1 = s.charAt(i);
            if (((char1 - 'A') * (char1 - 'Z') <= 0 || (char1 - 'a') * (char1 - 'z') <= 0) && char1 != 'e' && char1 != 'E') {
                return i;
            }
            ++i;
        }
        return i;
    }
    
    public static void updateNodes(final PathDataNode[] array, final PathDataNode[] array2) {
        for (int i = 0; i < array2.length; ++i) {
            array[i].mType = array2[i].mType;
            for (int j = 0; j < array2[i].mParams.length; ++j) {
                array[i].mParams[j] = array2[i].mParams[j];
            }
        }
    }
    
    private static class ExtractFloatResult
    {
        int mEndPosition;
        boolean mEndWithNegOrDot;
        
        ExtractFloatResult() {
        }
    }
    
    public static class PathDataNode
    {
        public float[] mParams;
        public char mType;
        
        PathDataNode(final char c, final float[] mParams) {
            this.mType = c;
            this.mParams = mParams;
        }
        
        PathDataNode(final PathDataNode pathDataNode) {
            this.mType = pathDataNode.mType;
            final float[] mParams = pathDataNode.mParams;
            this.mParams = PathParser.copyOfRange(mParams, 0, mParams.length);
        }
        
        private static void addCommand(final Path path, final float[] array, final char c, final char c2, final float[] array2) {
            final float n = array[0];
            final float n2 = array[1];
            final float n3 = array[2];
            final float n4 = array[3];
            final float n5 = array[4];
            final float n6 = array[5];
            float n7 = n;
            float n8 = n2;
            float n9 = n3;
            float n10 = n4;
            int n11 = 0;
            Label_0340: {
                switch (c2) {
                    case 'Z':
                    case 'z': {
                        path.close();
                        path.moveTo(n5, n6);
                        n7 = (n9 = n5);
                        n8 = (n10 = n6);
                    }
                    default: {
                        n10 = n4;
                        n9 = n3;
                        n8 = n2;
                        n7 = n;
                    }
                    case 'L':
                    case 'M':
                    case 'T':
                    case 'l':
                    case 'm':
                    case 't': {
                        n11 = 2;
                        break Label_0340;
                    }
                    case 'Q':
                    case 'S':
                    case 'q':
                    case 's': {
                        n11 = 4;
                        n7 = n;
                        n8 = n2;
                        n9 = n3;
                        n10 = n4;
                        break Label_0340;
                    }
                    case 'H':
                    case 'V':
                    case 'h':
                    case 'v': {
                        n11 = 1;
                        n7 = n;
                        n8 = n2;
                        n9 = n3;
                        n10 = n4;
                        break Label_0340;
                    }
                    case 'C':
                    case 'c': {
                        n11 = 6;
                        break;
                    }
                    case 'A':
                    case 'a': {
                        n11 = 7;
                        break;
                    }
                }
                n10 = n4;
                n9 = n3;
                n8 = n2;
                n7 = n;
            }
            final int n12 = 0;
            char c3 = c;
            float n13 = n6;
            float n14 = n5;
        Label_0493_Outer:
            for (int i = n12; i < array2.length; i += n11, c3 = c2) {
                if (c2 != 'A') {
                    if (c2 == 'C') {
                        int n15 = i;
                        final float n16 = array2[n15 + 0];
                        final float n17 = array2[n15 + 1];
                        final int n18 = n15 + 2;
                        final float n19 = array2[n18];
                        final int n20 = n15 + 3;
                        final float n21 = array2[n20];
                        final int n22 = n15 + 4;
                        final float n23 = array2[n22];
                        n15 += 5;
                        path.cubicTo(n16, n17, n19, n21, n23, array2[n15]);
                        n7 = array2[n22];
                        n8 = array2[n15];
                        n9 = array2[n18];
                        n10 = array2[n20];
                        continue;
                    }
                    if (c2 == 'H') {
                        final int n24 = i + 0;
                        path.lineTo(array2[n24], n8);
                        n7 = array2[n24];
                        continue;
                    }
                    if (c2 == 'Q') {
                        int n25 = i;
                        final int n26 = n25 + 0;
                        final float n27 = array2[n26];
                        final int n28 = n25 + 1;
                        final float n29 = array2[n28];
                        final int n30 = n25 + 2;
                        final float n31 = array2[n30];
                        n25 += 3;
                        path.quadTo(n27, n29, n31, array2[n25]);
                        n9 = array2[n26];
                        n10 = array2[n28];
                        n7 = array2[n30];
                        n8 = array2[n25];
                        continue;
                    }
                    if (c2 == 'V') {
                        final int n32 = i + 0;
                        path.lineTo(n7, array2[n32]);
                        n8 = array2[n32];
                        continue;
                    }
                    if (c2 != 'a') {
                        while (true) {
                            float n43 = 0.0f;
                            float n46 = 0.0f;
                            float n64 = 0.0f;
                            Label_1560: {
                                float n44 = 0.0f;
                                float n45 = 0.0f;
                                Label_1549: {
                                    if (c2 == 'c') {
                                        final float n33 = array2[i + 0];
                                        final float n34 = array2[i + 1];
                                        final int n35 = i + 2;
                                        final float n36 = array2[n35];
                                        final int n37 = i + 3;
                                        final float n38 = array2[n37];
                                        final int n39 = i + 4;
                                        final float n40 = array2[n39];
                                        final int n41 = i + 5;
                                        path.rCubicTo(n33, n34, n36, n38, n40, array2[n41]);
                                        final float n42 = array2[n35] + n7;
                                        n43 = array2[n37] + n8;
                                        n44 = n7 + array2[n39];
                                        n45 = array2[n41];
                                        n46 = n42;
                                        break Label_1549;
                                    }
                                    if (c2 != 'h') {
                                        if (c2 == 'q') {
                                            final int n47 = i + 0;
                                            final float n48 = array2[n47];
                                            final int n49 = i + 1;
                                            final float n50 = array2[n49];
                                            final int n51 = i + 2;
                                            final float n52 = array2[n51];
                                            final int n53 = i + 3;
                                            path.rQuadTo(n48, n50, n52, array2[n53]);
                                            final float n54 = array2[n47] + n7;
                                            n43 = array2[n49] + n8;
                                            n44 = n7 + array2[n51];
                                            n45 = array2[n53];
                                            n46 = n54;
                                            break Label_1549;
                                        }
                                        float n73 = 0.0f;
                                        Label_0898: {
                                            if (c2 != 'v') {
                                                if (c2 != 'L') {
                                                    if (c2 != 'M') {
                                                        if (c2 == 'S') {
                                                            float n55 = 0.0f;
                                                            float n56 = 0.0f;
                                                            Label_1073: {
                                                                if (c3 != 'c' && c3 != 's' && c3 != 'C') {
                                                                    n55 = n8;
                                                                    n56 = n7;
                                                                    if (c3 != 'S') {
                                                                        break Label_1073;
                                                                    }
                                                                }
                                                                n56 = n7 * 2.0f - n9;
                                                                n55 = n8 * 2.0f - n10;
                                                            }
                                                            final int n57 = i + 0;
                                                            final float n58 = array2[n57];
                                                            final int n59 = i + 1;
                                                            final float n60 = array2[n59];
                                                            final int n61 = i + 2;
                                                            final float n62 = array2[n61];
                                                            final int n63 = i + 3;
                                                            path.cubicTo(n56, n55, n58, n60, n62, array2[n63]);
                                                            n46 = array2[n57];
                                                            n43 = array2[n59];
                                                            n64 = array2[n61];
                                                            n8 = array2[n63];
                                                            break Label_1560;
                                                        }
                                                        if (c2 == 'T') {
                                                            float n65 = 0.0f;
                                                            float n66 = 0.0f;
                                                            Label_0962: {
                                                                if (c3 != 'q' && c3 != 't' && c3 != 'Q') {
                                                                    n65 = n8;
                                                                    n66 = n7;
                                                                    if (c3 != 'T') {
                                                                        break Label_0962;
                                                                    }
                                                                }
                                                                n66 = n7 * 2.0f - n9;
                                                                n65 = n8 * 2.0f - n10;
                                                            }
                                                            final int n67 = i + 0;
                                                            final float n68 = array2[n67];
                                                            final int n69 = i + 1;
                                                            path.quadTo(n66, n65, n68, array2[n69]);
                                                            n7 = array2[n67];
                                                            n8 = array2[n69];
                                                            n10 = n65;
                                                            n9 = n66;
                                                            continue Label_0493_Outer;
                                                        }
                                                        if (c2 == 'l') {
                                                            final int n70 = i + 0;
                                                            final float n71 = array2[n70];
                                                            final int n72 = i + 1;
                                                            path.rLineTo(n71, array2[n72]);
                                                            n7 += array2[n70];
                                                            n73 = array2[n72];
                                                            break Label_0898;
                                                        }
                                                        if (c2 != 'm') {
                                                            if (c2 == 's') {
                                                                float n74;
                                                                float n75;
                                                                if (c3 != 'c' && c3 != 's' && c3 != 'C' && c3 != 'S') {
                                                                    n74 = 0.0f;
                                                                    n75 = 0.0f;
                                                                }
                                                                else {
                                                                    n75 = n8 - n10;
                                                                    n74 = n7 - n9;
                                                                }
                                                                final int n76 = i + 0;
                                                                final float n77 = array2[n76];
                                                                final int n78 = i + 1;
                                                                final float n79 = array2[n78];
                                                                final int n80 = i + 2;
                                                                final float n81 = array2[n80];
                                                                final int n82 = i + 3;
                                                                path.rCubicTo(n74, n75, n77, n79, n81, array2[n82]);
                                                                final float n83 = array2[n76] + n7;
                                                                n43 = array2[n78] + n8;
                                                                n44 = n7 + array2[n80];
                                                                n45 = array2[n82];
                                                                n46 = n83;
                                                                break Label_1549;
                                                            }
                                                            if (c2 != 't') {
                                                                break Label_0493;
                                                            }
                                                            float n84;
                                                            float n85;
                                                            if (c3 != 'q' && c3 != 't' && c3 != 'Q' && c3 != 'T') {
                                                                n84 = 0.0f;
                                                                n85 = 0.0f;
                                                            }
                                                            else {
                                                                n85 = n7 - n9;
                                                                n84 = n8 - n10;
                                                            }
                                                            final int n86 = i + 0;
                                                            final float n87 = array2[n86];
                                                            final int n88 = i + 1;
                                                            path.rQuadTo(n85, n84, n87, array2[n88]);
                                                            final float n89 = n7 + array2[n86];
                                                            final float n90 = n8 + array2[n88];
                                                            n10 = n84 + n8;
                                                            n9 = n85 + n7;
                                                            n8 = n90;
                                                            n7 = n89;
                                                            break Label_0493;
                                                        }
                                                        else {
                                                            final int n91 = i + 0;
                                                            n7 += array2[n91];
                                                            final int n92 = i + 1;
                                                            n8 += array2[n92];
                                                            if (i > 0) {
                                                                path.rLineTo(array2[n91], array2[n92]);
                                                                break Label_0493;
                                                            }
                                                            path.rMoveTo(array2[n91], array2[n92]);
                                                        }
                                                    }
                                                    else {
                                                        final int n93 = i + 0;
                                                        n7 = array2[n93];
                                                        final int n94 = i + 1;
                                                        n8 = array2[n94];
                                                        if (i > 0) {
                                                            path.lineTo(array2[n93], array2[n94]);
                                                            break Label_0493;
                                                        }
                                                        path.moveTo(array2[n93], array2[n94]);
                                                    }
                                                    n13 = n8;
                                                    n14 = n7;
                                                    continue Label_0493_Outer;
                                                }
                                                final int n95 = i + 0;
                                                final float n96 = array2[n95];
                                                final int n97 = i + 1;
                                                path.lineTo(n96, array2[n97]);
                                                n7 = array2[n95];
                                                n8 = array2[n97];
                                                break Label_0493;
                                            }
                                            else {
                                                final int n98 = i + 0;
                                                path.rLineTo(0.0f, array2[n98]);
                                                n73 = array2[n98];
                                            }
                                        }
                                        n8 += n73;
                                    }
                                    else {
                                        final int n99 = i + 0;
                                        path.rLineTo(array2[n99], 0.0f);
                                        n7 += array2[n99];
                                    }
                                    continue Label_0493_Outer;
                                }
                                n8 += n45;
                                n64 = n44;
                            }
                            n10 = n43;
                            n9 = n46;
                            n7 = n64;
                            continue;
                        }
                    }
                    final int n100 = i + 5;
                    final float n101 = array2[n100];
                    final int n102 = i + 6;
                    drawArc(path, n7, n8, n101 + n7, array2[n102] + n8, array2[i + 0], array2[i + 1], array2[i + 2], array2[i + 3] != 0.0f, array2[i + 4] != 0.0f);
                    n7 += array2[n100];
                    n8 += array2[n102];
                }
                else {
                    final int n103 = i;
                    final int n104 = n103 + 5;
                    final float n105 = array2[n104];
                    final int n106 = n103 + 6;
                    drawArc(path, n7, n8, n105, array2[n106], array2[n103 + 0], array2[n103 + 1], array2[n103 + 2], array2[n103 + 3] != 0.0f, array2[n103 + 4] != 0.0f);
                    n7 = array2[n104];
                    n8 = array2[n106];
                }
                n10 = n8;
                n9 = n7;
            }
            array[0] = n7;
            array[1] = n8;
            array[2] = n9;
            array[3] = n10;
            array[4] = n14;
            array[5] = n13;
        }
        
        private static void arcToBezier(final Path path, final double n, final double n2, final double n3, double n4, double n5, double n6, double n7, double n8, double n9) {
            final int n10 = (int)Math.ceil(Math.abs(n9 * 4.0 / 3.141592653589793));
            final double cos = Math.cos(n7);
            final double sin = Math.sin(n7);
            final double cos2 = Math.cos(n8);
            final double sin2 = Math.sin(n8);
            n7 = -n3;
            final double n11 = n7 * cos;
            final double n12 = n4 * sin;
            final double n13 = n7 * sin;
            final double n14 = n4 * cos;
            n9 /= n10;
            n7 = sin2 * n13 + cos2 * n14;
            double n15 = n11 * sin2 - n12 * cos2;
            int i = 0;
            double n16 = n8;
            n8 = n6;
            n4 = n13;
            double n17 = n5;
            n5 = sin;
            n6 = cos;
            while (i < n10) {
                final double n18 = n16 + n9;
                final double sin3 = Math.sin(n18);
                final double cos3 = Math.cos(n18);
                final double n19 = n + n3 * n6 * cos3 - n12 * sin3;
                final double n20 = n2 + n3 * n5 * cos3 + n14 * sin3;
                final double n21 = n11 * sin3 - n12 * cos3;
                final double n22 = sin3 * n4 + cos3 * n14;
                final double a = n18 - n16;
                final double tan = Math.tan(a / 2.0);
                final double n23 = Math.sin(a) * (Math.sqrt(tan * 3.0 * tan + 4.0) - 1.0) / 3.0;
                path.rLineTo(0.0f, 0.0f);
                path.cubicTo((float)(n17 + n15 * n23), (float)(n8 + n7 * n23), (float)(n19 - n23 * n21), (float)(n20 - n23 * n22), (float)n19, (float)n20);
                ++i;
                n17 = n19;
                n16 = n18;
                n7 = n22;
                n15 = n21;
                n8 = n20;
            }
        }
        
        private static void drawArc(final Path path, final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final boolean b, final boolean b2) {
            final double radians = Math.toRadians(n7);
            final double cos = Math.cos(radians);
            final double sin = Math.sin(radians);
            final double n8 = n;
            final double n9 = n2;
            final double n10 = n5;
            final double n11 = (n8 * cos + n9 * sin) / n10;
            final double n12 = -n;
            final double n13 = n6;
            final double n14 = (n12 * sin + n9 * cos) / n13;
            final double n15 = n3;
            final double n16 = n4;
            final double n17 = (n15 * cos + n16 * sin) / n10;
            final double n18 = (-n3 * sin + n16 * cos) / n13;
            final double n19 = n11 - n17;
            final double n20 = n14 - n18;
            final double n21 = (n11 + n17) / 2.0;
            final double n22 = (n14 + n18) / 2.0;
            final double n23 = n19 * n19 + n20 * n20;
            if (n23 == 0.0) {
                Log.w("PathParser", " Points are coincident");
                return;
            }
            final double a = 1.0 / n23 - 0.25;
            if (a < 0.0) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Points are too far apart ");
                sb.append(n23);
                Log.w("PathParser", sb.toString());
                final float n24 = (float)(Math.sqrt(n23) / 1.99999);
                drawArc(path, n, n2, n3, n4, n5 * n24, n6 * n24, n7, b, b2);
                return;
            }
            final double sqrt = Math.sqrt(a);
            final double n25 = n19 * sqrt;
            final double n26 = sqrt * n20;
            double n27;
            double n28;
            if (b == b2) {
                n27 = n21 - n26;
                n28 = n22 + n25;
            }
            else {
                n27 = n21 + n26;
                n28 = n22 - n25;
            }
            final double atan2 = Math.atan2(n14 - n28, n11 - n27);
            final double n29 = Math.atan2(n18 - n28, n17 - n27) - atan2;
            final double n30 = dcmpl(n29, 0.0);
            final boolean b3 = n30 >= 0;
            double n31 = n29;
            if (b2 != b3) {
                if (n30 > 0) {
                    n31 = n29 - 6.283185307179586;
                }
                else {
                    n31 = n29 + 6.283185307179586;
                }
            }
            final double n32 = n27 * n10;
            final double n33 = n28 * n13;
            arcToBezier(path, n32 * cos - n33 * sin, n32 * sin + n33 * cos, n10, n13, n8, n9, radians, atan2, n31);
        }
        
        public static void nodesToPath(final PathDataNode[] array, final Path path) {
            final float[] array2 = new float[6];
            final char c = 'm';
            int i = 0;
            char c2 = c;
            while (i < array.length) {
                addCommand(path, array2, c2, array[i].mType, array[i].mParams);
                final char mType = array[i].mType;
                ++i;
                c2 = mType;
            }
        }
        
        public void interpolatePathDataNode(final PathDataNode pathDataNode, final PathDataNode pathDataNode2, final float n) {
            this.mType = pathDataNode.mType;
            int n2 = 0;
            while (true) {
                final float[] mParams = pathDataNode.mParams;
                if (n2 >= mParams.length) {
                    break;
                }
                this.mParams[n2] = mParams[n2] * (1.0f - n) + pathDataNode2.mParams[n2] * n;
                ++n2;
            }
        }
    }
}
