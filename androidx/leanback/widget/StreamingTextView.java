// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.graphics.Paint$FontMetricsInt;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.text.style.ReplacementSpan;
import android.text.SpannedString;
import android.widget.TextView;
import androidx.core.widget.TextViewCompat;
import android.view.ActionMode$Callback;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.leanback.R$drawable;
import android.os.Build$VERSION;
import android.view.View;
import android.graphics.BitmapFactory;
import java.util.regex.Matcher;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.content.Context;
import android.animation.ObjectAnimator;
import java.util.Random;
import android.graphics.Bitmap;
import android.util.Property;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.widget.EditText;

@SuppressLint({ "AppCompatCustomView" })
class StreamingTextView extends EditText
{
    private static final Pattern SPLIT_PATTERN;
    private static final Property<StreamingTextView, Integer> STREAM_POSITION_PROPERTY;
    Bitmap mOneDot;
    final Random mRandom;
    int mStreamPosition;
    private ObjectAnimator mStreamingAnimation;
    Bitmap mTwoDot;
    
    static {
        SPLIT_PATTERN = Pattern.compile("\\S+");
        STREAM_POSITION_PROPERTY = new Property<StreamingTextView, Integer>("streamPosition") {
            public Integer get(final StreamingTextView streamingTextView) {
                return streamingTextView.getStreamPosition();
            }
            
            public void set(final StreamingTextView streamingTextView, final Integer n) {
                streamingTextView.setStreamPosition(n);
            }
        };
    }
    
    public StreamingTextView(final Context context, final AttributeSet set) {
        super(context, set);
        this.mRandom = new Random();
    }
    
    public StreamingTextView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mRandom = new Random();
    }
    
    private void addDottySpans(final SpannableStringBuilder spannableStringBuilder, final String input, final int n) {
        final Matcher matcher = StreamingTextView.SPLIT_PATTERN.matcher(input);
        while (matcher.find()) {
            final int n2 = matcher.start() + n;
            spannableStringBuilder.setSpan((Object)new DottySpan(input.charAt(matcher.start()), n2), n2, matcher.end() + n, 33);
        }
    }
    
    private void cancelStreamAnimation() {
        final ObjectAnimator mStreamingAnimation = this.mStreamingAnimation;
        if (mStreamingAnimation != null) {
            mStreamingAnimation.cancel();
        }
    }
    
    private Bitmap getScaledBitmap(final int n, final float n2) {
        final Bitmap decodeResource = BitmapFactory.decodeResource(this.getResources(), n);
        return Bitmap.createScaledBitmap(decodeResource, (int)(decodeResource.getWidth() * n2), (int)(decodeResource.getHeight() * n2), false);
    }
    
    public static boolean isLayoutRtl(final View view) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        boolean b = false;
        if (sdk_INT >= 17) {
            b = b;
            if (1 == view.getLayoutDirection()) {
                b = true;
            }
        }
        return b;
    }
    
    private void startStreamAnimation() {
        this.cancelStreamAnimation();
        final int streamPosition = this.getStreamPosition();
        final int length = this.length();
        final int n = length - streamPosition;
        if (n > 0) {
            if (this.mStreamingAnimation == null) {
                (this.mStreamingAnimation = new ObjectAnimator()).setTarget((Object)this);
                this.mStreamingAnimation.setProperty((Property)StreamingTextView.STREAM_POSITION_PROPERTY);
            }
            this.mStreamingAnimation.setIntValues(new int[] { streamPosition, length });
            this.mStreamingAnimation.setDuration(n * 50L);
            this.mStreamingAnimation.start();
        }
    }
    
    private void updateText(final CharSequence text) {
        this.setText(text);
        this.bringPointIntoView(this.length());
    }
    
    int getStreamPosition() {
        return this.mStreamPosition;
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mOneDot = this.getScaledBitmap(R$drawable.lb_text_dot_one, 1.3f);
        this.mTwoDot = this.getScaledBitmap(R$drawable.lb_text_dot_two, 1.3f);
        this.reset();
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName((CharSequence)"androidx.leanback.widget.StreamingTextView");
    }
    
    public void reset() {
        this.mStreamPosition = -1;
        this.cancelStreamAnimation();
        this.setText((CharSequence)"");
    }
    
    public void setCustomSelectionActionModeCallback(final ActionMode$Callback actionMode$Callback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback((TextView)this, actionMode$Callback));
    }
    
    void setStreamPosition(final int mStreamPosition) {
        this.mStreamPosition = mStreamPosition;
        this.invalidate();
    }
    
    public void updateRecognizedText(final String s, final String s2) {
        String s3 = s;
        if (s == null) {
            s3 = "";
        }
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder((CharSequence)s3);
        if (s2 != null) {
            final int length = spannableStringBuilder.length();
            spannableStringBuilder.append((CharSequence)s2);
            this.addDottySpans(spannableStringBuilder, s2, length);
        }
        this.mStreamPosition = Math.max(s3.length(), this.mStreamPosition);
        this.updateText((CharSequence)new SpannedString((CharSequence)spannableStringBuilder));
        this.startStreamAnimation();
    }
    
    private class DottySpan extends ReplacementSpan
    {
        private final int mPosition;
        private final int mSeed;
        
        public DottySpan(final int mSeed, final int mPosition) {
            this.mSeed = mSeed;
            this.mPosition = mPosition;
        }
        
        public void draw(final Canvas canvas, final CharSequence charSequence, int n, int n2, final float n3, int width, final int n4, int n5, final Paint paint) {
            final int n6 = (int)paint.measureText(charSequence, n, n2);
            width = StreamingTextView.this.mOneDot.getWidth();
            final int n7 = width * 2;
            n2 = n6 / n7;
            n5 = n6 % n7 / 2;
            final boolean layoutRtl = StreamingTextView.isLayoutRtl((View)StreamingTextView.this);
            StreamingTextView.this.mRandom.setSeed(this.mSeed);
            final int alpha = paint.getAlpha();
            float n8;
            float n9;
            Bitmap mTwoDot;
            Bitmap mOneDot;
            for (n = 0; n < n2 && this.mPosition + n < StreamingTextView.this.mStreamPosition; ++n) {
                n8 = (float)(n * n7 + n5 + width / 2);
                if (layoutRtl) {
                    n9 = n6 + n3 - n8 - width;
                }
                else {
                    n9 = n3 + n8;
                }
                paint.setAlpha((StreamingTextView.this.mRandom.nextInt(4) + 1) * 63);
                if (StreamingTextView.this.mRandom.nextBoolean()) {
                    mTwoDot = StreamingTextView.this.mTwoDot;
                    canvas.drawBitmap(mTwoDot, n9, (float)(n4 - mTwoDot.getHeight()), paint);
                }
                else {
                    mOneDot = StreamingTextView.this.mOneDot;
                    canvas.drawBitmap(mOneDot, n9, (float)(n4 - mOneDot.getHeight()), paint);
                }
            }
            paint.setAlpha(alpha);
        }
        
        public int getSize(final Paint paint, final CharSequence charSequence, final int n, final int n2, final Paint$FontMetricsInt paint$FontMetricsInt) {
            return (int)paint.measureText(charSequence, n, n2);
        }
    }
}
