// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.res.TypedArray;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import java.util.List;
import com.android.systemui.statusbar.policy.ConfigurationController;
import android.widget.FrameLayout;

public class AutoReinflateContainer extends FrameLayout implements ConfigurationListener
{
    private final List<InflateListener> mInflateListeners;
    private final int mLayout;
    
    public AutoReinflateContainer(final Context context, final AttributeSet set) {
        super(context, set);
        this.mInflateListeners = new ArrayList<InflateListener>();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.AutoReinflateContainer);
        if (obtainStyledAttributes.hasValue(R$styleable.AutoReinflateContainer_android_layout)) {
            this.mLayout = obtainStyledAttributes.getResourceId(R$styleable.AutoReinflateContainer_android_layout, 0);
            obtainStyledAttributes.recycle();
            this.inflateLayout();
            return;
        }
        throw new IllegalArgumentException("AutoReinflateContainer must contain a layout");
    }
    
    public void addInflateListener(final InflateListener inflateListener) {
        this.mInflateListeners.add(inflateListener);
        inflateListener.onInflated(this.getChildAt(0));
    }
    
    public void inflateLayout() {
        this.removeAllViews();
        this.inflateLayoutImpl();
        for (int size = this.mInflateListeners.size(), i = 0; i < size; ++i) {
            this.mInflateListeners.get(i).onInflated(this.getChildAt(0));
        }
    }
    
    protected void inflateLayoutImpl() {
        LayoutInflater.from(this.getContext()).inflate(this.mLayout, (ViewGroup)this);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void onDensityOrFontScaleChanged() {
        this.inflateLayout();
    }
    
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Dependency.get(ConfigurationController.class).removeCallback((ConfigurationController.ConfigurationListener)this);
    }
    
    public void onLocaleListChanged() {
        this.inflateLayout();
    }
    
    public void onOverlayChanged() {
        this.inflateLayout();
    }
    
    public void onUiModeChanged() {
        this.inflateLayout();
    }
    
    public interface InflateListener
    {
        void onInflated(final View p0);
    }
}
