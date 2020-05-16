// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.assist.ui;

import android.animation.ArgbEvaluator;
import android.util.Log;
import android.util.MathUtils;
import android.graphics.Paint$Cap;
import java.util.Iterator;
import android.graphics.Canvas;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.NavigationBarController;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import com.android.systemui.R$attr;
import android.graphics.Paint$Join;
import android.graphics.Paint$Style;
import android.util.AttributeSet;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Paint;
import java.util.ArrayList;
import com.android.systemui.statusbar.phone.NavigationBarTransitions;
import android.view.View;

public class InvocationLightsView extends View implements DarkIntensityListener
{
    protected final ArrayList<EdgeLight> mAssistInvocationLights;
    private final int mDarkColor;
    protected final PerimeterPathGuide mGuide;
    private final int mLightColor;
    private final Paint mPaint;
    private final Path mPath;
    private boolean mRegistered;
    private int[] mScreenLocation;
    private final int mStrokeWidth;
    private boolean mUseNavBarColor;
    private final int mViewHeight;
    
    public InvocationLightsView(final Context context) {
        this(context, null);
    }
    
    public InvocationLightsView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public InvocationLightsView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public InvocationLightsView(final Context context, final AttributeSet set, int i, int n) {
        super(context, set, i, n);
        this.mAssistInvocationLights = new ArrayList<EdgeLight>();
        this.mPaint = new Paint();
        this.mPath = new Path();
        this.mScreenLocation = new int[2];
        this.mRegistered = false;
        this.mUseNavBarColor = true;
        i = DisplayUtils.convertDpToPx(3.0f, context);
        this.mStrokeWidth = i;
        this.mPaint.setStrokeWidth((float)i);
        this.mPaint.setStyle(Paint$Style.STROKE);
        this.mPaint.setStrokeJoin(Paint$Join.MITER);
        this.mPaint.setAntiAlias(true);
        n = DisplayUtils.getWidth(context);
        i = DisplayUtils.getHeight(context);
        this.mGuide = new PerimeterPathGuide(context, this.createCornerPathRenderer(context), this.mStrokeWidth / 2, n, i);
        this.mViewHeight = Math.max(Math.max(DisplayUtils.getCornerRadiusBottom(context), DisplayUtils.getCornerRadiusTop(context)), DisplayUtils.convertDpToPx(3.0f, context));
        i = Utils.getThemeAttr(super.mContext, R$attr.darkIconTheme);
        n = Utils.getThemeAttr(super.mContext, R$attr.lightIconTheme);
        final ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(super.mContext, n);
        final ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(super.mContext, i);
        this.mLightColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper, R$attr.singleToneColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor((Context)contextThemeWrapper2, R$attr.singleToneColor);
        for (i = 0; i < 4; ++i) {
            this.mAssistInvocationLights.add(new EdgeLight(0, 0.0f, 0.0f));
        }
    }
    
    private void attemptRegisterNavBarListener() {
        if (!this.mRegistered) {
            final NavigationBarController navigationBarController = Dependency.get(NavigationBarController.class);
            if (navigationBarController == null) {
                return;
            }
            final NavigationBarFragment defaultNavigationBarFragment = navigationBarController.getDefaultNavigationBarFragment();
            if (defaultNavigationBarFragment == null) {
                return;
            }
            this.updateDarkness(defaultNavigationBarFragment.getBarTransitions().addDarkIntensityListener((NavigationBarTransitions.DarkIntensityListener)this));
            this.mRegistered = true;
        }
    }
    
    private void attemptUnregisterNavBarListener() {
        if (this.mRegistered) {
            final NavigationBarController navigationBarController = Dependency.get(NavigationBarController.class);
            if (navigationBarController == null) {
                return;
            }
            final NavigationBarFragment defaultNavigationBarFragment = navigationBarController.getDefaultNavigationBarFragment();
            if (defaultNavigationBarFragment == null) {
                return;
            }
            defaultNavigationBarFragment.getBarTransitions().removeDarkIntensityListener((NavigationBarTransitions.DarkIntensityListener)this);
            this.mRegistered = false;
        }
    }
    
    private void renderLight(final EdgeLight edgeLight, final Canvas canvas) {
        if (edgeLight.getLength() > 0.0f) {
            this.mGuide.strokeSegment(this.mPath, edgeLight.getStart(), edgeLight.getStart() + edgeLight.getLength());
            this.mPaint.setColor(edgeLight.getColor());
            canvas.drawPath(this.mPath, this.mPaint);
        }
    }
    
    protected CornerPathRenderer createCornerPathRenderer(final Context context) {
        return new CircularCornerPathRenderer(context);
    }
    
    public void hide() {
        this.setVisibility(8);
        final Iterator<EdgeLight> iterator = this.mAssistInvocationLights.iterator();
        while (iterator.hasNext()) {
            iterator.next().setEndpoints(0.0f, 0.0f);
        }
        this.attemptUnregisterNavBarListener();
    }
    
    public void onDarkIntensity(final float n) {
        this.updateDarkness(n);
    }
    
    protected void onDraw(final Canvas canvas) {
        this.getLocationOnScreen(this.mScreenLocation);
        final int[] mScreenLocation = this.mScreenLocation;
        canvas.translate((float)(-mScreenLocation[0]), (float)(-mScreenLocation[1]));
        if (this.mUseNavBarColor) {
            final Iterator<EdgeLight> iterator = this.mAssistInvocationLights.iterator();
            while (iterator.hasNext()) {
                this.renderLight(iterator.next(), canvas);
            }
        }
        else {
            this.mPaint.setStrokeCap(Paint$Cap.ROUND);
            this.renderLight(this.mAssistInvocationLights.get(0), canvas);
            this.renderLight(this.mAssistInvocationLights.get(3), canvas);
            this.mPaint.setStrokeCap(Paint$Cap.BUTT);
            this.renderLight(this.mAssistInvocationLights.get(1), canvas);
            this.renderLight(this.mAssistInvocationLights.get(2), canvas);
        }
    }
    
    protected void onFinishInflate() {
        this.getLayoutParams().height = this.mViewHeight;
        this.requestLayout();
    }
    
    public void onInvocationProgress(float n) {
        if (n == 0.0f) {
            this.setVisibility(8);
        }
        else {
            this.attemptRegisterNavBarListener();
            final float regionWidth = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM_LEFT);
            final float n2 = (regionWidth - 0.6f * regionWidth) / 2.0f;
            final float lerp = MathUtils.lerp(0.0f, this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) / 4.0f, n);
            final float n3 = -regionWidth;
            final float n4 = 1.0f - n;
            n = (n3 + n2) * n4;
            final float n5 = this.mGuide.getRegionWidth(PerimeterPathGuide.Region.BOTTOM) + (regionWidth - n2) * n4;
            final float n6 = n + lerp;
            this.setLight(0, n, n6);
            final float n7 = 2.0f * lerp;
            this.setLight(1, n6, n + n7);
            n = n5 - lerp;
            this.setLight(2, n5 - n7, n);
            this.setLight(3, n, n5);
            this.setVisibility(0);
        }
        this.invalidate();
    }
    
    protected void onLayout(final boolean b, int rotation, final int n, final int n2, final int n3) {
        super.onLayout(b, rotation, n, n2, n3);
        rotation = this.getContext().getDisplay().getRotation();
        this.mGuide.setRotation(rotation);
    }
    
    public void setColors(final int color, final int color2, final int color3, final int color4) {
        this.mUseNavBarColor = false;
        this.attemptUnregisterNavBarListener();
        this.mAssistInvocationLights.get(0).setColor(color);
        this.mAssistInvocationLights.get(1).setColor(color2);
        this.mAssistInvocationLights.get(2).setColor(color3);
        this.mAssistInvocationLights.get(3).setColor(color4);
    }
    
    public void setColors(final Integer n) {
        if (n == null) {
            this.mUseNavBarColor = true;
            this.mPaint.setStrokeCap(Paint$Cap.BUTT);
            this.attemptRegisterNavBarListener();
        }
        else {
            this.setColors(n, n, n, n);
        }
    }
    
    protected void setLight(final int n, final float n2, final float n3) {
        if (n < 0 || n >= 4) {
            final StringBuilder sb = new StringBuilder();
            sb.append("invalid invocation light index: ");
            sb.append(n);
            Log.w("InvocationLightsView", sb.toString());
        }
        this.mAssistInvocationLights.get(n).setEndpoints(n2, n3);
    }
    
    protected void updateDarkness(final float n) {
        if (this.mUseNavBarColor) {
            final int intValue = (int)ArgbEvaluator.getInstance().evaluate(n, (Object)this.mLightColor, (Object)this.mDarkColor);
            boolean b = true;
            final Iterator<EdgeLight> iterator = this.mAssistInvocationLights.iterator();
            while (iterator.hasNext()) {
                b &= iterator.next().setColor(intValue);
            }
            if (b) {
                this.invalidate();
            }
        }
    }
}
