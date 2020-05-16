// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;

final class RegisteredDumpable<T>
{
    private final T dumpable;
    private final String name;
    
    public RegisteredDumpable(final String name, final T dumpable) {
        Intrinsics.checkParameterIsNotNull(name, "name");
        this.name = name;
        this.dumpable = dumpable;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this != o) {
            if (o instanceof RegisteredDumpable) {
                final RegisteredDumpable registeredDumpable = (RegisteredDumpable)o;
                if (Intrinsics.areEqual(this.name, registeredDumpable.name) && Intrinsics.areEqual(this.dumpable, registeredDumpable.dumpable)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    public final T getDumpable() {
        return this.dumpable;
    }
    
    public final String getName() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        final String name = this.name;
        int hashCode = 0;
        int hashCode2;
        if (name != null) {
            hashCode2 = name.hashCode();
        }
        else {
            hashCode2 = 0;
        }
        final T dumpable = this.dumpable;
        if (dumpable != null) {
            hashCode = dumpable.hashCode();
        }
        return hashCode2 * 31 + hashCode;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("RegisteredDumpable(name=");
        sb.append(this.name);
        sb.append(", dumpable=");
        sb.append(this.dumpable);
        sb.append(")");
        return sb.toString();
    }
}
