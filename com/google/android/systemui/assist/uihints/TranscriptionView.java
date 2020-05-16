// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import android.graphics.Paint$FontMetricsInt;
import android.graphics.Paint;
import android.graphics.Canvas;
import androidx.core.math.MathUtils;
import android.text.style.ReplacementSpan;
import java.util.function.Consumer;
import android.animation.TimeInterpolator;
import java.util.ArrayList;
import android.animation.ValueAnimator$AnimatorUpdateListener;
import java.util.List;
import android.view.ViewGroup$LayoutParams;
import android.content.res.Configuration;
import android.animation.Animator$AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import android.graphics.Shader;
import android.graphics.LinearGradient;
import android.graphics.Shader$TileMode;
import android.widget.TextView$BufferType;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R$color;
import com.android.systemui.R$dimen;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.AnimatorSet;
import android.text.SpannableStringBuilder;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import com.google.common.util.concurrent.SettableFuture;
import com.android.systemui.assist.DeviceConfigHelper;
import android.view.animation.PathInterpolator;
import android.widget.TextView;

public class TranscriptionView extends TextView implements TranscriptionSpaceView
{
    private static final PathInterpolator INTERPOLATOR_SCROLL;
    private final float BUMPER_DISTANCE_END_PX;
    private final float BUMPER_DISTANCE_START_PX;
    private final float FADE_DISTANCE_END_PX;
    private final float FADE_DISTANCE_START_PX;
    private final int TEXT_COLOR_DARK;
    private final int TEXT_COLOR_LIGHT;
    private boolean mCardVisible;
    private DeviceConfigHelper mDeviceConfigHelper;
    private int mDisplayWidthPx;
    private boolean mHasDarkBackground;
    private SettableFuture<Void> mHideFuture;
    private Matrix mMatrix;
    private int mRequestedTextColor;
    private float[] mStops;
    private ValueAnimator mTranscriptionAnimation;
    private TranscriptionAnimator mTranscriptionAnimator;
    private SpannableStringBuilder mTranscriptionBuilder;
    private AnimatorSet mVisibilityAnimators;
    
    static {
        INTERPOLATOR_SCROLL = new PathInterpolator(0.17f, 0.17f, 0.67f, 1.0f);
    }
    
    public TranscriptionView(final Context context) {
        this(context, null);
    }
    
    public TranscriptionView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public TranscriptionView(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public TranscriptionView(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mTranscriptionBuilder = new SpannableStringBuilder();
        this.mVisibilityAnimators = new AnimatorSet();
        this.mHideFuture = null;
        this.mHasDarkBackground = false;
        this.mCardVisible = false;
        this.mRequestedTextColor = 0;
        this.mMatrix = new Matrix();
        this.mDisplayWidthPx = 0;
        this.mTranscriptionAnimator = new TranscriptionAnimator();
        this.initializeDeviceConfigHelper(new DeviceConfigHelper());
        this.BUMPER_DISTANCE_START_PX = context.getResources().getDimension(R$dimen.zerostate_icon_left_margin) + context.getResources().getDimension(R$dimen.zerostate_icon_tap_padding);
        this.BUMPER_DISTANCE_END_PX = context.getResources().getDimension(R$dimen.keyboard_icon_right_margin) + context.getResources().getDimension(R$dimen.keyboard_icon_tap_padding);
        this.FADE_DISTANCE_START_PX = context.getResources().getDimension(R$dimen.zerostate_icon_size);
        this.FADE_DISTANCE_END_PX = context.getResources().getDimension(R$dimen.keyboard_icon_size) / 2.0f;
        this.TEXT_COLOR_DARK = context.getResources().getColor(R$color.transcription_text_dark);
        this.TEXT_COLOR_LIGHT = context.getResources().getColor(R$color.transcription_text_light);
        this.updateDisplayWidth();
        this.setHasDarkBackground(this.mHasDarkBackground ^ true);
    }
    
    private long getDurationFastMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_duration_per_px_fast", 3L);
    }
    
    private long getDurationMaxMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_max_duration", 400L);
    }
    
    private long getDurationMinMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_min_duration", 20L);
    }
    
    private long getDurationRegularMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_duration_per_px_regular", 4L);
    }
    
    private long getFadeInDurationMs() {
        return this.mDeviceConfigHelper.getLong("assist_transcription_fade_in_duration", 50L);
    }
    
    private float getFullyVisibleDistance(final float n) {
        final int mDisplayWidthPx = this.mDisplayWidthPx;
        final float n2 = (float)mDisplayWidthPx;
        final float bumper_DISTANCE_END_PX = this.BUMPER_DISTANCE_END_PX;
        final float bumper_DISTANCE_START_PX = this.BUMPER_DISTANCE_START_PX;
        final float fade_DISTANCE_END_PX = this.FADE_DISTANCE_END_PX;
        if (n < n2 - (bumper_DISTANCE_START_PX + bumper_DISTANCE_END_PX + fade_DISTANCE_END_PX + this.FADE_DISTANCE_START_PX)) {
            return (mDisplayWidthPx - n) / 2.0f;
        }
        return mDisplayWidthPx - n - fade_DISTANCE_END_PX - bumper_DISTANCE_END_PX;
    }
    
    @VisibleForTesting
    static float interpolate(final long n, final long n2, final float n3) {
        return (n2 - n) * n3 + n;
    }
    
    private void resetTranscription() {
        this.setTranscription("");
        this.mTranscriptionAnimator = new TranscriptionAnimator();
    }
    
    private void setUpSpans(int index, TranscriptionSpan transcriptionSpan) {
        this.mTranscriptionAnimator.clearSpans();
        final String string = this.mTranscriptionBuilder.toString();
        final String substring = string.substring(index);
        if (substring.length() > 0) {
            index = string.indexOf(substring, index);
            final int length = substring.length();
            if (transcriptionSpan == null) {
                transcriptionSpan = new TranscriptionSpan();
            }
            else {
                transcriptionSpan = new TranscriptionSpan(transcriptionSpan);
            }
            this.mTranscriptionBuilder.setSpan((Object)transcriptionSpan, index, length + index, 33);
            this.mTranscriptionAnimator.addSpan(transcriptionSpan);
        }
        this.setText((CharSequence)this.mTranscriptionBuilder, TextView$BufferType.SPANNABLE);
        this.updateColor();
    }
    
    private void updateColor() {
        int n;
        if ((n = this.mRequestedTextColor) == 0) {
            if (this.mHasDarkBackground) {
                n = this.TEXT_COLOR_DARK;
            }
            else {
                n = this.TEXT_COLOR_LIGHT;
            }
        }
        final LinearGradient shader = new LinearGradient(0.0f, 0.0f, (float)this.mDisplayWidthPx, 0.0f, new int[] { 0, n, n, 0 }, this.mStops, Shader$TileMode.CLAMP);
        this.mMatrix.setTranslate(-this.getTranslationX(), 0.0f);
        shader.setLocalMatrix(this.mMatrix);
        this.getPaint().setShader((Shader)shader);
        this.invalidate();
    }
    
    private void updateDisplayWidth() {
        final int rotatedWidth = DisplayUtils.getRotatedWidth(super.mContext);
        this.mDisplayWidthPx = rotatedWidth;
        final float bumper_DISTANCE_START_PX = this.BUMPER_DISTANCE_START_PX;
        this.mStops = new float[] { bumper_DISTANCE_START_PX / rotatedWidth, (bumper_DISTANCE_START_PX + this.FADE_DISTANCE_START_PX) / rotatedWidth, (rotatedWidth - this.FADE_DISTANCE_END_PX - this.BUMPER_DISTANCE_END_PX) / rotatedWidth, 1.0f };
        this.updateColor();
    }
    
    @VisibleForTesting
    long getAdaptiveDuration(final float n, float interpolate) {
        interpolate = interpolate(this.getDurationRegularMs(), this.getDurationFastMs(), n / interpolate);
        return Math.min(this.getDurationMaxMs(), Math.max(this.getDurationMinMs(), (long)(n * interpolate)));
    }
    
    public ListenableFuture<Void> hide(final boolean b) {
        final SettableFuture<Void> mHideFuture = this.mHideFuture;
        if (mHideFuture != null && !mHideFuture.isDone()) {
            return this.mHideFuture;
        }
        this.mHideFuture = SettableFuture.create();
        final _$$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZ_fPgiA $$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZ_fPgiA = new _$$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZ_fPgiA(this);
        if (!b) {
            if (this.mVisibilityAnimators.isRunning()) {
                this.mVisibilityAnimators.end();
            }
            else {
                $$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZ_fPgiA.run();
            }
            return Futures.immediateFuture((Void)null);
        }
        (this.mVisibilityAnimators = new AnimatorSet()).play((Animator)ObjectAnimator.ofFloat((Object)this, View.ALPHA, new float[] { this.getAlpha(), 0.0f }).setDuration(400L));
        if (!this.mCardVisible) {
            this.mVisibilityAnimators.play((Animator)ObjectAnimator.ofFloat((Object)this, View.TRANSLATION_Y, new float[] { this.getTranslationY(), (float)(this.getHeight() * -1) }).setDuration(700L));
        }
        this.mVisibilityAnimators.addListener((Animator$AnimatorListener)new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(final Animator animator) {
                super.onAnimationEnd(animator);
                $$Lambda$TranscriptionView$Qv69LoHEhmJSkqbPe36IZ_fPgiA.run();
            }
        });
        this.mVisibilityAnimators.start();
        return this.mHideFuture;
    }
    
    @VisibleForTesting
    void initializeDeviceConfigHelper(final DeviceConfigHelper mDeviceConfigHelper) {
        this.mDeviceConfigHelper = mDeviceConfigHelper;
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final String string = this.mTranscriptionBuilder.toString();
        this.resetTranscription();
        this.setTranscription(string);
    }
    
    public void onFontSizeChanged() {
        this.setTextSize(0, super.mContext.getResources().getDimension(R$dimen.transcription_text_size));
    }
    
    public void setCardVisible(final boolean mCardVisible) {
        this.mCardVisible = mCardVisible;
    }
    
    public void setHasDarkBackground(final boolean mHasDarkBackground) {
        if (mHasDarkBackground != this.mHasDarkBackground) {
            this.mHasDarkBackground = mHasDarkBackground;
            this.updateColor();
        }
    }
    
    void setTranscription(final String s) {
        final int n = 0;
        this.setVisibility(0);
        this.updateDisplayWidth();
        final ValueAnimator mTranscriptionAnimation = this.mTranscriptionAnimation;
        int n2 = n;
        if (mTranscriptionAnimation != null) {
            n2 = n;
            if (mTranscriptionAnimation.isRunning()) {
                n2 = 1;
            }
        }
        if (n2 != 0) {
            this.mTranscriptionAnimation.cancel();
        }
        final boolean empty = this.mTranscriptionBuilder.toString().isEmpty();
        final StringUtils.StringStabilityInfo calculateStringStabilityInfo = StringUtils.calculateStringStabilityInfo(this.mTranscriptionBuilder.toString(), s);
        this.mTranscriptionBuilder.clear();
        this.mTranscriptionBuilder.append((CharSequence)calculateStringStabilityInfo.stable);
        this.mTranscriptionBuilder.append((CharSequence)calculateStringStabilityInfo.unstable);
        final int width = (int)Math.ceil(this.getPaint().measureText(this.mTranscriptionBuilder.toString()));
        final ViewGroup$LayoutParams layoutParams = this.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = width;
            this.setLayoutParams(layoutParams);
        }
        this.updateColor();
        final TranscriptionSpan transcriptionSpan = null;
        if (!empty && !calculateStringStabilityInfo.stable.isEmpty()) {
            int length;
            final int n3 = length = calculateStringStabilityInfo.stable.length();
            TranscriptionSpan transcriptionSpan2 = transcriptionSpan;
            if (n2 != 0) {
                length = n3;
                transcriptionSpan2 = transcriptionSpan;
                if (!calculateStringStabilityInfo.stable.endsWith(" ")) {
                    length = n3;
                    transcriptionSpan2 = transcriptionSpan;
                    if (!calculateStringStabilityInfo.unstable.startsWith(" ")) {
                        final String[] split = calculateStringStabilityInfo.stable.split("\\s+");
                        int n4 = n3;
                        if (split.length > 0) {
                            n4 = n3 - split[split.length - 1].length();
                        }
                        final List<TranscriptionSpan> spans = this.mTranscriptionAnimator.getSpans();
                        length = n4;
                        transcriptionSpan2 = transcriptionSpan;
                        if (!spans.isEmpty()) {
                            transcriptionSpan2 = spans.get(spans.size() - 1);
                            length = n4;
                        }
                    }
                }
            }
            this.setUpSpans(length, transcriptionSpan2);
            (this.mTranscriptionAnimation = this.mTranscriptionAnimator.createAnimator()).start();
        }
        else {
            this.setUpSpans(calculateStringStabilityInfo.stable.length() + calculateStringStabilityInfo.unstable.length(), null);
            this.setX(this.getFullyVisibleDistance((float)width));
            this.updateColor();
        }
    }
    
    void setTranscriptionColor(final int mRequestedTextColor) {
        this.mRequestedTextColor = mRequestedTextColor;
        this.updateColor();
    }
    
    private class TranscriptionAnimator implements ValueAnimator$AnimatorUpdateListener
    {
        private float mDistance;
        private List<TranscriptionSpan> mSpans;
        private float mStartX;
        
        private TranscriptionAnimator() {
            this.mSpans = new ArrayList<TranscriptionSpan>();
        }
        
        void addSpan(final TranscriptionSpan transcriptionSpan) {
            this.mSpans.add(transcriptionSpan);
        }
        
        void clearSpans() {
            this.mSpans.clear();
        }
        
        ValueAnimator createAnimator() {
            final float measureText = TranscriptionView.this.getPaint().measureText(TranscriptionView.this.mTranscriptionBuilder.toString());
            this.mStartX = TranscriptionView.this.getX();
            this.mDistance = TranscriptionView.this.getFullyVisibleDistance(measureText) - this.mStartX;
            TranscriptionView.this.updateColor();
            final long adaptiveDuration = TranscriptionView.this.getAdaptiveDuration(Math.abs(this.mDistance), (float)TranscriptionView.this.mDisplayWidthPx);
            long duration;
            if (measureText > TranscriptionView.this.mDisplayWidthPx - TranscriptionView.this.getX()) {
                duration = TranscriptionView.this.getFadeInDurationMs() + adaptiveDuration;
            }
            else {
                duration = adaptiveDuration;
            }
            final float mDistance = this.mDistance;
            final float n = duration / (float)adaptiveDuration;
            final float mStartX = this.mStartX;
            final ValueAnimator setDuration = ValueAnimator.ofFloat(new float[] { mStartX, mStartX + mDistance * n }).setDuration(duration);
            setDuration.setInterpolator((TimeInterpolator)TranscriptionView.INTERPOLATOR_SCROLL);
            setDuration.addUpdateListener((ValueAnimator$AnimatorUpdateListener)this);
            return setDuration;
        }
        
        List<TranscriptionSpan> getSpans() {
            return this.mSpans;
        }
        
        public void onAnimationUpdate(final ValueAnimator valueAnimator) {
            final float floatValue = (float)valueAnimator.getAnimatedValue();
            if (Math.abs(floatValue - this.mStartX) < Math.abs(this.mDistance)) {
                TranscriptionView.this.setX(floatValue);
                TranscriptionView.this.updateColor();
            }
            this.mSpans.forEach(new _$$Lambda$TranscriptionView$TranscriptionAnimator$gS5Q9c9JhQDHs9CaSxI2Cr4w408(valueAnimator));
            TranscriptionView.this.invalidate();
        }
    }
    
    private class TranscriptionSpan extends ReplacementSpan
    {
        private float mCurrentFraction;
        private float mStartFraction;
        
        TranscriptionSpan() {
            this.mCurrentFraction = 0.0f;
            this.mStartFraction = 0.0f;
        }
        
        TranscriptionSpan(final TranscriptionSpan transcriptionSpan) {
            this.mCurrentFraction = 0.0f;
            this.mStartFraction = 0.0f;
            this.mStartFraction = MathUtils.clamp(transcriptionSpan.getCurrentFraction(), 0.0f, 1.0f);
        }
        
        private float getAlpha() {
            final float mStartFraction = this.mStartFraction;
            if (mStartFraction == 1.0f) {
                return 1.0f;
            }
            return MathUtils.clamp((1.0f - mStartFraction) / 1.0f * this.mCurrentFraction + mStartFraction, 0.0f, 1.0f);
        }
        
        public void draw(final Canvas canvas, final CharSequence charSequence, final int n, final int n2, final float n3, final int n4, final int n5, final int n6, final Paint paint) {
            paint.setAlpha((int)Math.ceil(this.getAlpha() * 255.0f));
            canvas.drawText(charSequence, n, n2, n3, (float)n5, paint);
        }
        
        float getCurrentFraction() {
            return this.mCurrentFraction;
        }
        
        public int getSize(final Paint paint, final CharSequence charSequence, final int n, final int n2, final Paint$FontMetricsInt paint$FontMetricsInt) {
            return (int)Math.ceil(TranscriptionView.this.getPaint().measureText(charSequence, 0, charSequence.length()));
        }
        
        void setCurrentFraction(final float mCurrentFraction) {
            this.mCurrentFraction = mCurrentFraction;
        }
    }
}
