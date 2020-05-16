// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.dump;

import android.os.IBinder;
import android.content.Intent;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.app.Service;

public class SystemUIAuxiliaryDumpService extends Service
{
    private final DumpManager mDumpManager;
    
    public SystemUIAuxiliaryDumpService(final DumpManager mDumpManager) {
        this.mDumpManager = mDumpManager;
    }
    
    protected void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        this.mDumpManager.dump(fileDescriptor, printWriter, new String[] { "--dump-priority", "NORMAL" });
    }
    
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
