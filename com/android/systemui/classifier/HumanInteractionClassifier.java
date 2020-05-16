// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.classifier;

import android.hardware.SensorEvent;
import com.android.systemui.R$bool;
import android.util.DisplayMetrics;
import android.provider.Settings$Global;
import android.os.Looper;
import android.database.ContentObserver;
import android.os.Handler;
import android.content.Context;
import android.view.MotionEvent;
import java.util.ArrayDeque;

public class HumanInteractionClassifier extends Classifier
{
    private static HumanInteractionClassifier sInstance;
    private final ArrayDeque<MotionEvent> mBufferedEvents;
    private final Context mContext;
    private int mCurrentType;
    private final float mDpi;
    private boolean mEnableClassifier;
    private final GestureClassifier[] mGestureClassifiers;
    private final Handler mHandler;
    private final HistoryEvaluator mHistoryEvaluator;
    protected final ContentObserver mSettingsObserver;
    private final StrokeClassifier[] mStrokeClassifiers;
    
    private HumanInteractionClassifier(final Context mContext) {
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mBufferedEvents = new ArrayDeque<MotionEvent>();
        this.mEnableClassifier = false;
        this.mCurrentType = 7;
        this.mSettingsObserver = new ContentObserver(this.mHandler) {
            public void onChange(final boolean b) {
                HumanInteractionClassifier.this.updateConfiguration();
            }
        };
        this.mContext = mContext;
        final DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        final float mDpi = (displayMetrics.xdpi + displayMetrics.ydpi) / 2.0f;
        this.mDpi = mDpi;
        super.mClassifierData = new ClassifierData(mDpi);
        this.mHistoryEvaluator = new HistoryEvaluator();
        this.mStrokeClassifiers = new StrokeClassifier[] { new AnglesClassifier(super.mClassifierData), new SpeedClassifier(super.mClassifierData), new DurationCountClassifier(super.mClassifierData), new EndPointRatioClassifier(super.mClassifierData), new EndPointLengthClassifier(super.mClassifierData), new AccelerationClassifier(super.mClassifierData), new SpeedAnglesClassifier(super.mClassifierData), new LengthCountClassifier(super.mClassifierData), new DirectionClassifier(super.mClassifierData) };
        this.mGestureClassifiers = new GestureClassifier[] { new PointerCountClassifier(super.mClassifierData), new ProximityClassifier(super.mClassifierData) };
        this.mContext.getContentResolver().registerContentObserver(Settings$Global.getUriFor("HIC_enable"), false, this.mSettingsObserver, -1);
        this.updateConfiguration();
    }
    
    private void addTouchEvent(final MotionEvent motionEvent) {
        if (!super.mClassifierData.update(motionEvent)) {
            return;
        }
        final StrokeClassifier[] mStrokeClassifiers = this.mStrokeClassifiers;
        for (int length = mStrokeClassifiers.length, i = 0; i < length; ++i) {
            mStrokeClassifiers[i].onTouchEvent(motionEvent);
        }
        final GestureClassifier[] mGestureClassifiers = this.mGestureClassifiers;
        for (int length2 = mGestureClassifiers.length, j = 0; j < length2; ++j) {
            mGestureClassifiers[j].onTouchEvent(motionEvent);
        }
        final int size = super.mClassifierData.getEndingStrokes().size();
        int index = 0;
        StringBuilder sb;
        float n;
        while (true) {
            sb = null;
            StringBuilder sb2 = null;
            n = 0.0f;
            float n2 = 0.0f;
            if (index >= size) {
                break;
            }
            final Stroke stroke = super.mClassifierData.getEndingStrokes().get(index);
            if (FalsingLog.ENABLED) {
                sb2 = new StringBuilder("stroke");
            }
            for (final StrokeClassifier strokeClassifier : this.mStrokeClassifiers) {
                final float falseTouchEvaluation = strokeClassifier.getFalseTouchEvaluation(this.mCurrentType, stroke);
                if (FalsingLog.ENABLED) {
                    String str = strokeClassifier.getTag();
                    final StringBuilder append = sb2.append(" ");
                    if (falseTouchEvaluation < 1.0f) {
                        str = str.toLowerCase();
                    }
                    append.append(str);
                    append.append("=");
                    append.append(falseTouchEvaluation);
                }
                n2 += falseTouchEvaluation;
            }
            if (FalsingLog.ENABLED) {
                FalsingLog.i(" addTouchEvent", sb2.toString());
            }
            this.mHistoryEvaluator.addStroke(n2);
            ++index;
        }
        final int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            StringBuilder sb3 = sb;
            if (FalsingLog.ENABLED) {
                sb3 = new StringBuilder("gesture");
            }
            final GestureClassifier[] mGestureClassifiers2 = this.mGestureClassifiers;
            final int length4 = mGestureClassifiers2.length;
            int l = 0;
            float n3 = n;
            while (l < length4) {
                final GestureClassifier gestureClassifier = mGestureClassifiers2[l];
                final float falseTouchEvaluation2 = gestureClassifier.getFalseTouchEvaluation(this.mCurrentType);
                if (FalsingLog.ENABLED) {
                    String str2 = gestureClassifier.getTag();
                    final StringBuilder append2 = sb3.append(" ");
                    if (falseTouchEvaluation2 < 1.0f) {
                        str2 = str2.toLowerCase();
                    }
                    append2.append(str2);
                    append2.append("=");
                    append2.append(falseTouchEvaluation2);
                }
                n3 += falseTouchEvaluation2;
                ++l;
            }
            if (FalsingLog.ENABLED) {
                FalsingLog.i(" addTouchEvent", sb3.toString());
            }
            this.mHistoryEvaluator.addGesture(n3);
            this.setType(7);
        }
        super.mClassifierData.cleanUp(motionEvent);
    }
    
    public static HumanInteractionClassifier getInstance(final Context context) {
        if (HumanInteractionClassifier.sInstance == null) {
            HumanInteractionClassifier.sInstance = new HumanInteractionClassifier(context);
        }
        return HumanInteractionClassifier.sInstance;
    }
    
    private void updateConfiguration() {
        this.mEnableClassifier = (Settings$Global.getInt(this.mContext.getContentResolver(), "HIC_enable", (int)(this.mContext.getResources().getBoolean(R$bool.config_lockscreenAntiFalsingClassifierEnabled) ? 1 : 0)) != 0);
    }
    
    @Override
    public String getTag() {
        return "HIC";
    }
    
    public boolean isEnabled() {
        return this.mEnableClassifier;
    }
    
    public boolean isFalseTouch() {
        final boolean mEnableClassifier = this.mEnableClassifier;
        int n = 0;
        int i = 0;
        if (mEnableClassifier) {
            final float evaluation = this.mHistoryEvaluator.getEvaluation();
            if (evaluation >= 5.0f) {
                i = 1;
            }
            n = i;
            if (FalsingLog.ENABLED) {
                final StringBuilder sb = new StringBuilder();
                sb.append("eval=");
                sb.append(evaluation);
                sb.append(" result=");
                sb.append(i);
                FalsingLog.i("isFalseTouch", sb.toString());
                n = i;
            }
        }
        return n != 0;
    }
    
    @Override
    public void onSensorChanged(final SensorEvent sensorEvent) {
        final StrokeClassifier[] mStrokeClassifiers = this.mStrokeClassifiers;
        final int length = mStrokeClassifiers.length;
        final int n = 0;
        for (int i = 0; i < length; ++i) {
            mStrokeClassifiers[i].onSensorChanged(sensorEvent);
        }
        final GestureClassifier[] mGestureClassifiers = this.mGestureClassifiers;
        for (int length2 = mGestureClassifiers.length, j = n; j < length2; ++j) {
            mGestureClassifiers[j].onSensorChanged(sensorEvent);
        }
    }
    
    @Override
    public void onTouchEvent(final MotionEvent motionEvent) {
        if (!this.mEnableClassifier) {
            return;
        }
        final int mCurrentType = this.mCurrentType;
        if (mCurrentType != 2 && mCurrentType != 9) {
            this.addTouchEvent(motionEvent);
        }
        else {
            this.mBufferedEvents.add(MotionEvent.obtain(motionEvent));
            while (new Point(motionEvent.getX() / this.mDpi, motionEvent.getY() / this.mDpi).dist(new Point(this.mBufferedEvents.getFirst().getX() / this.mDpi, this.mBufferedEvents.getFirst().getY() / this.mDpi)) > 0.1f) {
                this.addTouchEvent(this.mBufferedEvents.getFirst());
                this.mBufferedEvents.remove();
            }
            if (motionEvent.getActionMasked() == 1) {
                this.mBufferedEvents.getFirst().setAction(1);
                this.addTouchEvent(this.mBufferedEvents.getFirst());
                this.mBufferedEvents.clear();
            }
        }
    }
    
    public void setType(final int mCurrentType) {
        this.mCurrentType = mCurrentType;
    }
}
