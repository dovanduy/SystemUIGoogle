// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

public class DirectionEvaluator
{
    public static float evaluate(final float a, final float a2, final int n) {
        final boolean b = Math.abs(a2) >= Math.abs(a);
        Label_0119: {
            if (n != 0) {
                if (n != 1) {
                    if (n != 2) {
                        if (n != 4) {
                            if (n != 5) {
                                if (n != 6) {
                                    if (n != 8) {
                                        if (n != 9) {
                                            return 0.0f;
                                        }
                                        break Label_0119;
                                    }
                                }
                                else {
                                    if (a > 0.0 && a2 > 0.0) {
                                        return 5.5f;
                                    }
                                    return 0.0f;
                                }
                            }
                            else {
                                if (a < 0.0 && a2 > 0.0) {
                                    return 5.5f;
                                }
                                return 0.0f;
                            }
                        }
                        if (!b || a2 >= 0.0) {
                            return 5.5f;
                        }
                        return 0.0f;
                    }
                }
                else {
                    if (b) {
                        return 5.5f;
                    }
                    return 0.0f;
                }
            }
        }
        if (!b || a2 <= 0.0) {
            return 5.5f;
        }
        return 0.0f;
    }
}
