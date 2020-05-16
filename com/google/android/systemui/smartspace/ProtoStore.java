// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.smartspace;

import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import android.util.Log;
import java.io.FileInputStream;
import com.google.protobuf.nano.MessageNano;
import android.content.Context;

public class ProtoStore
{
    private final Context mContext;
    
    public ProtoStore(final Context context) {
        this.mContext = context.getApplicationContext();
    }
    
    public <T extends MessageNano> boolean load(String s, final T t) {
        final File fileStreamPath = this.mContext.getFileStreamPath(s);
        try {
            s = (String)new FileInputStream(fileStreamPath);
            try {
                final int len = (int)fileStreamPath.length();
                final byte[] b = new byte[len];
                ((FileInputStream)s).read(b, 0, len);
                MessageNano.mergeFrom(t, b);
                ((FileInputStream)s).close();
                return true;
            }
            finally {
                try {
                    ((FileInputStream)s).close();
                }
                finally {
                    final Throwable exception;
                    ((Throwable)t).addSuppressed(exception);
                }
            }
        }
        catch (Exception ex) {
            Log.e("ProtoStore", "unable to load data", (Throwable)ex);
            return false;
        }
        catch (FileNotFoundException ex2) {
            Log.d("ProtoStore", "no cached data");
            return false;
        }
    }
    
    public void store(final MessageNano messageNano, final String str) {
        try {
            final FileOutputStream openFileOutput = this.mContext.openFileOutput(str, 0);
            Label_0029: {
                if (messageNano == null) {
                    break Label_0029;
                }
                try {
                    openFileOutput.write(MessageNano.toByteArray(messageNano));
                }
                finally {
                    if (openFileOutput != null) {
                        try {
                            openFileOutput.close();
                        }
                        finally {
                            final Throwable exception;
                            ((Throwable)messageNano).addSuppressed(exception);
                        }
                    }
                    // iftrue(Label_0121:, openFileOutput == null)
                Block_5:
                    while (true) {
                        break Block_5;
                        final StringBuilder sb = new StringBuilder();
                        sb.append("deleting ");
                        sb.append(str);
                        Log.d("ProtoStore", sb.toString());
                        this.mContext.deleteFile(str);
                        continue;
                    }
                    openFileOutput.close();
                }
            }
        }
        catch (Exception ex) {
            Log.e("ProtoStore", "unable to write file", (Throwable)ex);
        }
        catch (FileNotFoundException ex2) {
            Log.d("ProtoStore", "file does not exist");
        }
        Label_0121:;
    }
}
