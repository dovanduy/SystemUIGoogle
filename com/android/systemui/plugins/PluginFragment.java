// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.plugins;

import android.view.LayoutInflater;
import android.os.Bundle;
import android.content.Context;
import android.app.Fragment;

public abstract class PluginFragment extends Fragment implements Plugin
{
    private Context mPluginContext;
    
    public Context getContext() {
        return this.mPluginContext;
    }
    
    public void onCreate(final Context context, final Context mPluginContext) {
        this.mPluginContext = mPluginContext;
    }
    
    public LayoutInflater onGetLayoutInflater(final Bundle bundle) {
        return super.onGetLayoutInflater(bundle).cloneInContext(this.getContext());
    }
    
    public void onSaveInstanceState(final Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }
}
