// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.widget;

import android.util.AttributeSet;
import android.content.Context;
import android.widget.ProgressBar;

public class ContentLoadingProgressBar extends ProgressBar
{
    private final Runnable mDelayedHide;
    private final Runnable mDelayedShow;
    boolean mDismissed;
    boolean mPostedHide;
    boolean mPostedShow;
    long mStartTime;
    
    public ContentLoadingProgressBar(final Context context, final AttributeSet set) {
        super(context, set, 0);
        this.mDismissed = false;
        this.mDelayedHide = new Runnable() {
            @Override
            public void run() {
                final ContentLoadingProgressBar this$0 = ContentLoadingProgressBar.this;
                this$0.mPostedHide = false;
                this$0.mStartTime = -1L;
                this$0.setVisibility(8);
            }
        };
        this.mDelayedShow = new Runnable() {
            @Override
            public void run() {
                final ContentLoadingProgressBar this$0 = ContentLoadingProgressBar.this;
                this$0.mPostedShow = false;
                if (!this$0.mDismissed) {
                    this$0.mStartTime = System.currentTimeMillis();
                    ContentLoadingProgressBar.this.setVisibility(0);
                }
            }
        };
    }
    
    private void removeCallbacks() {
        this.removeCallbacks(this.mDelayedHide);
        this.removeCallbacks(this.mDelayedShow);
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.removeCallbacks();
    }
    
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.removeCallbacks();
    }
}
