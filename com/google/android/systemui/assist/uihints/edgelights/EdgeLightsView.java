// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints.edgelights;

import com.android.internal.logging.MetricsLogger;
import android.metrics.LogMaker;
import android.content.res.Configuration;
import android.os.Looper;
import java.util.Iterator;
import android.graphics.Canvas;
import java.util.function.Consumer;
import android.content.res.Resources;
import com.android.systemui.R$color;
import com.google.android.systemui.assist.uihints.edgelights.mode.Gone;
import com.android.systemui.assist.ui.CornerPathRenderer;
import com.android.systemui.assist.ui.PathSpecCornerPathRenderer;
import android.graphics.Paint$Join;
import android.graphics.Paint$Cap;
import android.graphics.Paint$Style;
import com.google.android.systemui.assist.uihints.DisplayUtils;
import java.util.HashSet;
import java.util.ArrayList;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.assist.ui.PerimeterPathGuide;
import android.graphics.Path;
import android.graphics.Paint;
import java.util.Set;
import com.android.systemui.assist.ui.EdgeLight;
import java.util.List;
import android.view.View;

public class EdgeLightsView extends View
{
    private List<EdgeLight> mAssistInvocationLights;
    private EdgeLight[] mAssistLights;
    private Set<EdgeLightsListener> mListeners;
    private Mode mMode;
    private final Paint mPaint;
    private final Path mPath;
    private final PerimeterPathGuide mPerimeterPathGuide;
    private int[] mScreenLocation;
    
    public EdgeLightsView(final Context context) {
        this(context, null);
    }
    
    public EdgeLightsView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public EdgeLightsView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public EdgeLightsView(final Context context, final AttributeSet set, int convertDpToPx, final int n) {
        super(context, set, convertDpToPx, n);
        this.mPaint = new Paint();
        this.mAssistLights = new EdgeLight[0];
        this.mAssistInvocationLights = new ArrayList<EdgeLight>();
        this.mPath = new Path();
        this.mListeners = new HashSet<EdgeLightsListener>();
        this.mScreenLocation = new int[2];
        convertDpToPx = DisplayUtils.convertDpToPx(3.0f, context);
        this.mPaint.setStrokeWidth((float)convertDpToPx);
        this.mPaint.setStyle(Paint$Style.STROKE);
        this.mPaint.setStrokeCap(Paint$Cap.ROUND);
        this.mPaint.setStrokeJoin(Paint$Join.MITER);
        this.mPaint.setAntiAlias(true);
        this.mPerimeterPathGuide = new PerimeterPathGuide(context, new PathSpecCornerPathRenderer(context), convertDpToPx / 2, DisplayUtils.getWidth(context), DisplayUtils.getHeight(context));
        this.commitModeTransition(this.mMode = (Mode)new Gone());
        final Resources resources = this.getResources();
        this.mAssistInvocationLights.add(new EdgeLight(resources.getColor(R$color.edge_light_blue), 0.0f, 0.0f));
        this.mAssistInvocationLights.add(new EdgeLight(resources.getColor(R$color.edge_light_red), 0.0f, 0.0f));
        this.mAssistInvocationLights.add(new EdgeLight(resources.getColor(R$color.edge_light_yellow), 0.0f, 0.0f));
        this.mAssistInvocationLights.add(new EdgeLight(resources.getColor(R$color.edge_light_green), 0.0f, 0.0f));
    }
    
    private void renderLight(final Canvas canvas, final EdgeLight edgeLight) {
        this.mPerimeterPathGuide.strokeSegment(this.mPath, edgeLight.getStart(), edgeLight.getStart() + edgeLight.getLength());
        this.mPaint.setColor(edgeLight.getColor());
        canvas.drawPath(this.mPath, this.mPaint);
    }
    
    private void renderLights(final Canvas canvas, final List<EdgeLight> list) {
        if (list.isEmpty()) {
            return;
        }
        this.mPaint.setStrokeCap(Paint$Cap.ROUND);
        this.renderLight(canvas, list.get(0));
        if (list.size() > 1) {
            this.renderLight(canvas, list.get(list.size() - 1));
        }
        if (list.size() > 2) {
            this.mPaint.setStrokeCap(Paint$Cap.BUTT);
            final Iterator<EdgeLight> iterator = list.subList(1, list.size() - 1).iterator();
            while (iterator.hasNext()) {
                this.renderLight(canvas, iterator.next());
            }
        }
    }
    
    private void renderLights(final Canvas canvas, final EdgeLight[] array) {
        if (array.length == 0) {
            return;
        }
        this.mPaint.setStrokeCap(Paint$Cap.ROUND);
        this.renderLight(canvas, array[0]);
        if (array.length > 1) {
            this.renderLight(canvas, array[array.length - 1]);
        }
        if (array.length > 2) {
            this.mPaint.setStrokeCap(Paint$Cap.BUTT);
            for (int i = 1; i < array.length - 1; ++i) {
                this.renderLight(canvas, array[i]);
            }
        }
    }
    
    private void updateRotation() {
        this.mPerimeterPathGuide.setRotation(this.getContext().getDisplay().getRotation());
    }
    
    protected void addListener(final EdgeLightsListener edgeLightsListener) {
        this.mListeners.add(edgeLightsListener);
    }
    
    public void commitModeTransition(final Mode mMode) {
        mMode.start(this, this.mPerimeterPathGuide, this.mMode);
        this.mMode = mMode;
        this.mListeners.forEach(new _$$Lambda$EdgeLightsView$VdVujPZ2gFciRhgbRZT_MFCQZNQ(this));
        this.mAssistInvocationLights.forEach((Consumer<? super Object>)_$$Lambda$EdgeLightsView$4tzYB8xooL0BcgRnRG7YnRnlnWk.INSTANCE);
        this.invalidate();
    }
    
    public List<EdgeLight> getAssistInvocationLights() {
        return this.mAssistInvocationLights;
    }
    
    public EdgeLight[] getAssistLights() {
        if (Looper.getMainLooper().isCurrentThread()) {
            return this.mAssistLights;
        }
        throw new IllegalStateException("Must be called from main thread");
    }
    
    public Mode getMode() {
        return this.mMode;
    }
    
    public void onAudioLevelUpdate(final float n, final float n2) {
        this.mMode.onAudioLevelUpdate(n, n2);
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.updateRotation();
        this.mMode.onConfigurationChanged();
    }
    
    protected void onDraw(final Canvas canvas) {
        this.getLocationOnScreen(this.mScreenLocation);
        final int[] mScreenLocation = this.mScreenLocation;
        canvas.translate((float)(-mScreenLocation[0]), (float)(-mScreenLocation[1]));
        this.renderLights(canvas, this.mAssistLights);
        this.renderLights(canvas, this.mAssistInvocationLights);
    }
    
    protected void onLayout(final boolean b, final int n, final int n2, final int n3, final int n4) {
        super.onLayout(b, n, n2, n3, n4);
        this.updateRotation();
    }
    
    public void setAssistLights(final EdgeLight[] array) {
        this.post((Runnable)new _$$Lambda$EdgeLightsView$_h6pEZiEvPvqcfe85_YS1VNFuZo(this, array));
    }
    
    public void setVisibility(final int visibility) {
        final int visibility2 = this.getVisibility();
        super.setVisibility(visibility);
        if (visibility2 == 8) {
            this.updateRotation();
        }
    }
    
    public interface Mode
    {
        int getSubType();
        
        default void logState() {
            MetricsLogger.action(new LogMaker(1716).setType(6).setSubtype(this.getSubType()));
        }
        
        default void onAudioLevelUpdate(final float n, final float n2) {
        }
        
        default void onConfigurationChanged() {
        }
        
        void onNewModeRequest(final EdgeLightsView p0, final Mode p1);
        
        default boolean preventsInvocations() {
            return false;
        }
        
        void start(final EdgeLightsView p0, final PerimeterPathGuide p1, final Mode p2);
    }
}
