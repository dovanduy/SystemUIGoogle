// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import com.android.systemui.plugins.Plugin;
import com.android.systemui.shared.plugins.PluginManager;
import android.util.Log;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import com.android.systemui.plugins.ViewProvider;
import com.android.systemui.plugins.PluginListener;

public class PluginInflateContainer extends AutoReinflateContainer implements PluginListener<ViewProvider>
{
    private Class<?> mClass;
    private View mPluginView;
    
    public PluginInflateContainer(Context string, final AttributeSet set) {
        super(string, set);
        string = (Context)string.obtainStyledAttributes(set, R$styleable.PluginInflateContainer).getString(R$styleable.PluginInflateContainer_viewType);
        try {
            this.mClass = Class.forName((String)string);
        }
        catch (Exception ex) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Problem getting class info ");
            sb.append((String)string);
            Log.d("PluginInflateContainer", sb.toString(), (Throwable)ex);
            this.mClass = null;
        }
    }
    
    @Override
    protected void inflateLayoutImpl() {
        final View mPluginView = this.mPluginView;
        if (mPluginView != null) {
            this.addView(mPluginView);
        }
        else {
            super.inflateLayoutImpl();
        }
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mClass != null) {
            Dependency.get(PluginManager.class).addPluginListener((PluginListener<Plugin>)this, this.mClass);
        }
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mClass != null) {
            Dependency.get(PluginManager.class).removePluginListener(this);
        }
    }
    
    @Override
    public void onPluginConnected(final ViewProvider viewProvider, final Context context) {
        this.mPluginView = viewProvider.getView();
        this.inflateLayout();
    }
    
    @Override
    public void onPluginDisconnected(final ViewProvider viewProvider) {
        this.mPluginView = null;
        this.inflateLayout();
    }
}
