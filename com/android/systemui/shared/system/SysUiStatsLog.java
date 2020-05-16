// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.shared.system;

import android.util.StatsEvent$Builder;
import android.util.StatsLog;
import android.util.StatsEvent;

public class SysUiStatsLog
{
    public static void write(final int atomId, final int n) {
        final StatsEvent$Builder builder = StatsEvent.newBuilder();
        builder.setAtomId(atomId);
        builder.writeInt(n);
        builder.usePooledBuffer();
        StatsLog.write(builder.build());
    }
    
    public static void write(final int atomId, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9) {
        final StatsEvent$Builder builder = StatsEvent.newBuilder();
        builder.setAtomId(atomId);
        builder.writeInt(n);
        builder.writeInt(n2);
        builder.writeInt(n3);
        builder.writeInt(n4);
        builder.writeInt(n5);
        builder.writeInt(n6);
        builder.writeInt(n7);
        builder.writeInt(n8);
        builder.writeInt(n9);
        builder.usePooledBuffer();
        StatsLog.write(builder.build());
    }
    
    public static void write(final int atomId, final int n, final int n2, final byte[] array) {
        final StatsEvent$Builder builder = StatsEvent.newBuilder();
        builder.setAtomId(atomId);
        builder.writeInt(n);
        builder.writeInt(n2);
        byte[] array2 = array;
        if (array == null) {
            array2 = new byte[0];
        }
        builder.writeByteArray(array2);
        builder.usePooledBuffer();
        StatsLog.write(builder.build());
    }
    
    public static void write(final int atomId, final String s, final String s2, final int n, final int n2, final int n3, final int n4, final float n5, final float n6, final boolean b, final boolean b2, final boolean b3) {
        final StatsEvent$Builder builder = StatsEvent.newBuilder();
        builder.setAtomId(atomId);
        builder.writeString(s);
        builder.writeString(s2);
        builder.writeInt(n);
        builder.writeInt(n2);
        builder.writeInt(n3);
        builder.writeInt(n4);
        builder.writeFloat(n5);
        builder.writeFloat(n6);
        builder.writeBoolean(b);
        builder.writeBoolean(b2);
        builder.writeBoolean(b3);
        builder.usePooledBuffer();
        StatsLog.write(builder.build());
    }
}
