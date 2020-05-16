// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.mediarouter.R$string;
import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuff$Mode;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.R$drawable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View$OnClickListener;
import android.graphics.drawable.AnimationDrawable;
import androidx.appcompat.widget.AppCompatImageButton;

class MediaRouteExpandCollapseButton extends AppCompatImageButton
{
    final AnimationDrawable mCollapseAnimationDrawable;
    final String mCollapseGroupDescription;
    final AnimationDrawable mExpandAnimationDrawable;
    final String mExpandGroupDescription;
    boolean mIsGroupExpanded;
    View$OnClickListener mListener;
    
    public MediaRouteExpandCollapseButton(final Context context) {
        this(context, null);
    }
    
    public MediaRouteExpandCollapseButton(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public MediaRouteExpandCollapseButton(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mExpandAnimationDrawable = (AnimationDrawable)ContextCompat.getDrawable(context, R$drawable.mr_group_expand);
        this.mCollapseAnimationDrawable = (AnimationDrawable)ContextCompat.getDrawable(context, R$drawable.mr_group_collapse);
        final PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(MediaRouterThemeHelper.getControllerColor(context, n), PorterDuff$Mode.SRC_IN);
        this.mExpandAnimationDrawable.setColorFilter((ColorFilter)porterDuffColorFilter);
        this.mCollapseAnimationDrawable.setColorFilter((ColorFilter)porterDuffColorFilter);
        this.mExpandGroupDescription = context.getString(R$string.mr_controller_expand_group);
        this.mCollapseGroupDescription = context.getString(R$string.mr_controller_collapse_group);
        this.setImageDrawable(this.mExpandAnimationDrawable.getFrame(0));
        this.setContentDescription((CharSequence)this.mExpandGroupDescription);
        super.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final MediaRouteExpandCollapseButton this$0 = MediaRouteExpandCollapseButton.this;
                final boolean mIsGroupExpanded = this$0.mIsGroupExpanded ^ true;
                this$0.mIsGroupExpanded = mIsGroupExpanded;
                if (mIsGroupExpanded) {
                    this$0.setImageDrawable((Drawable)this$0.mExpandAnimationDrawable);
                    MediaRouteExpandCollapseButton.this.mExpandAnimationDrawable.start();
                    final MediaRouteExpandCollapseButton this$2 = MediaRouteExpandCollapseButton.this;
                    this$2.setContentDescription((CharSequence)this$2.mCollapseGroupDescription);
                }
                else {
                    this$0.setImageDrawable((Drawable)this$0.mCollapseAnimationDrawable);
                    MediaRouteExpandCollapseButton.this.mCollapseAnimationDrawable.start();
                    final MediaRouteExpandCollapseButton this$3 = MediaRouteExpandCollapseButton.this;
                    this$3.setContentDescription((CharSequence)this$3.mExpandGroupDescription);
                }
                final View$OnClickListener mListener = MediaRouteExpandCollapseButton.this.mListener;
                if (mListener != null) {
                    mListener.onClick(view);
                }
            }
        });
    }
    
    public void setOnClickListener(final View$OnClickListener mListener) {
        this.mListener = mListener;
    }
}
