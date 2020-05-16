// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

final class StringUtils
{
    private static int[][] calculateLongestCommonSubstringMatrix(final String s, final String s2) {
        final int[][] array = new int[s.length()][s2.length()];
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            for (int j = 0; j < s2.length(); ++j) {
                if (char1 == s2.charAt(j)) {
                    int n;
                    if (char1 == ' ') {
                        n = 0;
                    }
                    else {
                        n = 1;
                    }
                    int n2;
                    if (i != 0 && j != 0) {
                        n2 = array[i - 1][j - 1];
                    }
                    else {
                        n2 = 0;
                    }
                    array[i][j] = n2 + n;
                }
            }
        }
        return array;
    }
    
    public static StringStabilityInfo calculateStringStabilityInfo(final String s, final String s2) {
        if (!isNullOrEmpty(s) && !isNullOrEmpty(s2)) {
            return getRightMostStabilityInfoLeaf(s2, 0, s.length(), 0, s2.length(), calculateLongestCommonSubstringMatrix(s.toLowerCase(), s2.toLowerCase()));
        }
        return new StringStabilityInfo("", s2);
    }
    
    private static StringStabilityInfo getRightMostStabilityInfoLeaf(final String s, int i, final int n, int n2, final int n3, final int[][] array) {
        int n4 = -1;
        int n5 = 0;
        int n6 = -1;
        while (i < n) {
            int j = n2;
            int n7 = n6;
            while (j < n3) {
                int n8;
                if (array[i][j] > (n8 = n5)) {
                    n8 = array[i][j];
                    n4 = i;
                    n7 = j;
                }
                ++j;
                n5 = n8;
            }
            ++i;
            n6 = n7;
        }
        if (n5 == 0) {
            return new StringStabilityInfo(s, n2 - 1);
        }
        n2 = n4 + 1;
        if (n2 != n) {
            i = n6 + 1;
            if (i != n3) {
                return getRightMostStabilityInfoLeaf(s, n2, n, i, n3, array);
            }
        }
        return new StringStabilityInfo(s, n6);
    }
    
    private static boolean isNullOrEmpty(final String s) {
        return s == null || s.isEmpty();
    }
    
    public static final class StringStabilityInfo
    {
        final String stable;
        final String unstable;
        
        StringStabilityInfo(final String stable, int n) {
            if (n >= stable.length()) {
                this.stable = stable;
                this.unstable = "";
            }
            else {
                ++n;
                this.stable = stable.substring(0, n);
                this.unstable = stable.substring(n);
            }
        }
        
        StringStabilityInfo(String unstable, final String s) {
            String stable = unstable;
            if (unstable == null) {
                stable = "";
            }
            this.stable = stable;
            if ((unstable = s) == null) {
                unstable = "";
            }
            this.unstable = unstable;
        }
    }
}
