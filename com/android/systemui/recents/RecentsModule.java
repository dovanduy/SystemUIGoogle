// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.recents;

import com.android.systemui.R$string;
import com.android.systemui.dagger.ContextComponentHelper;
import android.content.Context;

public abstract class RecentsModule
{
    public static RecentsImplementation provideRecentsImpl(final Context context, final ContextComponentHelper contextComponentHelper) {
        final String string = context.getString(R$string.config_recentsComponent);
        if (string != null && string.length() != 0) {
            Object resolveRecents;
            if ((resolveRecents = contextComponentHelper.resolveRecents(string)) == null) {
                try {
                    final Class<?> loadClass = context.getClassLoader().loadClass(string);
                    try {
                        final RecentsImplementation recentsImplementation = (RecentsImplementation)loadClass.newInstance();
                    }
                    finally {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Error creating recents component: ");
                        sb.append(string);
                        final Throwable cause;
                        throw new RuntimeException(sb.toString(), cause);
                    }
                }
                finally {
                    resolveRecents = new StringBuilder();
                    ((StringBuilder)resolveRecents).append("Error loading recents component: ");
                    ((StringBuilder)resolveRecents).append(string);
                    final Throwable cause2;
                    throw new RuntimeException(((StringBuilder)resolveRecents).toString(), cause2);
                }
            }
            return (RecentsImplementation)resolveRecents;
        }
        throw new RuntimeException("No recents component configured", null);
    }
}
