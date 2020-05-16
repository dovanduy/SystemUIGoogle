// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R$color;
import com.android.systemui.statusbar.CommandQueue;
import android.content.Context;
import android.graphics.Rect;
import com.android.systemui.plugins.DarkIconDispatcher;
import android.util.ArrayMap;

public class DarkIconDispatcherImpl implements SysuiDarkIconDispatcher, DarkIntensityApplier
{
    private float mDarkIntensity;
    private int mDarkModeIconColorSingleTone;
    private int mIconTint;
    private int mLightModeIconColorSingleTone;
    private final ArrayMap<Object, DarkReceiver> mReceivers;
    private final Rect mTintArea;
    private final LightBarTransitionsController mTransitionsController;
    
    public DarkIconDispatcherImpl(final Context context, final CommandQueue commandQueue) {
        this.mTintArea = new Rect();
        this.mReceivers = (ArrayMap<Object, DarkReceiver>)new ArrayMap();
        this.mIconTint = -1;
        this.mDarkModeIconColorSingleTone = context.getColor(R$color.dark_mode_icon_color_single_tone);
        this.mLightModeIconColorSingleTone = context.getColor(R$color.light_mode_icon_color_single_tone);
        this.mTransitionsController = new LightBarTransitionsController(context, (LightBarTransitionsController.DarkIntensityApplier)this, commandQueue);
    }
    
    private void applyIconTint() {
        for (int i = 0; i < this.mReceivers.size(); ++i) {
            ((DarkReceiver)this.mReceivers.valueAt(i)).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
        }
    }
    
    @Override
    public void addDarkReceiver(final ImageView imageView) {
        final _$$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I $$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I = new _$$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I(this, imageView);
        this.mReceivers.put((Object)imageView, (Object)$$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I);
        ((DarkIconDispatcher.DarkReceiver)$$Lambda$DarkIconDispatcherImpl$ok51JmL9mmr4FNW4V8J0PDfHR6I).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }
    
    @Override
    public void addDarkReceiver(final DarkReceiver darkReceiver) {
        this.mReceivers.put((Object)darkReceiver, (Object)darkReceiver);
        darkReceiver.onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }
    
    @Override
    public void applyDark(final DarkReceiver darkReceiver) {
        ((DarkReceiver)this.mReceivers.get((Object)darkReceiver)).onDarkChanged(this.mTintArea, this.mDarkIntensity, this.mIconTint);
    }
    
    @Override
    public void applyDarkIntensity(final float mDarkIntensity) {
        this.mDarkIntensity = mDarkIntensity;
        this.mIconTint = (int)ArgbEvaluator.getInstance().evaluate(mDarkIntensity, (Object)this.mLightModeIconColorSingleTone, (Object)this.mDarkModeIconColorSingleTone);
        this.applyIconTint();
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("DarkIconDispatcher: ");
        final StringBuilder sb = new StringBuilder();
        sb.append("  mIconTint: 0x");
        sb.append(Integer.toHexString(this.mIconTint));
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("  mDarkIntensity: ");
        sb2.append(this.mDarkIntensity);
        sb2.append("f");
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("  mTintArea: ");
        sb3.append(this.mTintArea);
        printWriter.println(sb3.toString());
    }
    
    @Override
    public int getTintAnimationDuration() {
        return 120;
    }
    
    @Override
    public LightBarTransitionsController getTransitionsController() {
        return this.mTransitionsController;
    }
    
    @Override
    public void removeDarkReceiver(final ImageView imageView) {
        this.mReceivers.remove((Object)imageView);
    }
    
    @Override
    public void removeDarkReceiver(final DarkReceiver darkReceiver) {
        this.mReceivers.remove((Object)darkReceiver);
    }
    
    @Override
    public void setIconsDarkArea(final Rect rect) {
        if (rect == null && this.mTintArea.isEmpty()) {
            return;
        }
        if (rect == null) {
            this.mTintArea.setEmpty();
        }
        else {
            this.mTintArea.set(rect);
        }
        this.applyIconTint();
    }
}
