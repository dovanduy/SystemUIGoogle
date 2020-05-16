// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.columbus.sensors;

import java.util.ArrayList;

public class Util
{
    public static int getMaxId(final ArrayList<Float> list) {
        int i = 0;
        float n = -3.4028235E38f;
        int n2 = 0;
        while (i < list.size()) {
            float floatValue = n;
            if (n < list.get(i)) {
                floatValue = list.get(i);
                n2 = i;
            }
            ++i;
            n = floatValue;
        }
        return n2;
    }
}
