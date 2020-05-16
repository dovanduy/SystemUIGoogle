// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.SliceItem;
import android.graphics.drawable.Drawable;
import android.os.Build$VERSION;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.widget.ImageView;
import androidx.core.content.ContextCompat;
import androidx.appcompat.R$color;
import androidx.appcompat.R$attr;
import androidx.core.graphics.drawable.DrawableCompat;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import androidx.slice.view.R$layout;
import android.view.LayoutInflater;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.content.Intent;
import android.widget.Checkable;
import android.content.res.Resources;
import androidx.slice.view.R$dimen;
import android.content.Context;
import androidx.slice.core.SliceActionImpl;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.view.View$OnClickListener;
import android.widget.FrameLayout;

public class SliceActionView extends FrameLayout implements View$OnClickListener, CompoundButton$OnCheckedChangeListener
{
    static final int[] CHECKED_STATE_SET;
    private View mActionView;
    private EventInfo mEventInfo;
    private int mIconSize;
    private int mImageSize;
    private SliceActionLoadingListener mLoadingListener;
    private SliceView.OnSliceActionListener mObserver;
    private ProgressBar mProgressView;
    private SliceActionImpl mSliceAction;
    
    static {
        CHECKED_STATE_SET = new int[] { 16842912 };
    }
    
    public SliceActionView(final Context context) {
        super(context);
        final Resources resources = this.getContext().getResources();
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mImageSize = resources.getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
    }
    
    private void sendActionInternal() {
        final SliceActionImpl mSliceAction = this.mSliceAction;
        if (mSliceAction != null) {
            if (mSliceAction.getActionItem() != null) {
                Intent putExtra = null;
                try {
                    if (this.mSliceAction.isToggle()) {
                        final boolean checked = ((Checkable)this.mActionView).isChecked();
                        putExtra = new Intent().addFlags(268435456).putExtra("android.app.slice.extra.TOGGLE_STATE", checked);
                        if (this.mEventInfo != null) {
                            this.mEventInfo.state = (checked ? 1 : 0);
                        }
                    }
                    if (this.mSliceAction.getActionItem().fireActionInternal(this.getContext(), putExtra)) {
                        this.setLoading(true);
                        if (this.mLoadingListener != null) {
                            int rowIndex;
                            if (this.mEventInfo != null) {
                                rowIndex = this.mEventInfo.rowIndex;
                            }
                            else {
                                rowIndex = -1;
                            }
                            this.mLoadingListener.onSliceActionLoading(this.mSliceAction.getSliceItem(), rowIndex);
                        }
                    }
                    if (this.mObserver != null && this.mEventInfo != null) {
                        this.mObserver.onSliceAction(this.mEventInfo, this.mSliceAction.getSliceItem());
                    }
                }
                catch (PendingIntent$CanceledException ex) {
                    final View mActionView = this.mActionView;
                    if (mActionView instanceof Checkable) {
                        mActionView.setSelected(true ^ ((Checkable)mActionView).isChecked());
                    }
                    Log.e("SliceActionView", "PendingIntent for slice cannot be sent", (Throwable)ex);
                }
            }
        }
    }
    
    public SliceActionImpl getAction() {
        return this.mSliceAction;
    }
    
    public void onCheckedChanged(final CompoundButton compoundButton, final boolean b) {
        if (this.mSliceAction != null) {
            if (this.mActionView != null) {
                this.sendActionInternal();
            }
        }
    }
    
    public void onClick(final View view) {
        if (this.mSliceAction != null) {
            if (this.mActionView != null) {
                this.sendActionInternal();
            }
        }
    }
    
    public void sendAction() {
        final SliceActionImpl mSliceAction = this.mSliceAction;
        if (mSliceAction == null) {
            return;
        }
        if (mSliceAction.isToggle()) {
            this.toggle();
        }
        else {
            this.sendActionInternal();
        }
    }
    
    public void setAction(final SliceActionImpl mSliceAction, final EventInfo mEventInfo, final SliceView.OnSliceActionListener mObserver, int mImageSize, final SliceActionLoadingListener mLoadingListener) {
        final int[] empty_STATE_SET = FrameLayout.EMPTY_STATE_SET;
        final int[] checked_STATE_SET = SliceActionView.CHECKED_STATE_SET;
        final View mActionView = this.mActionView;
        if (mActionView != null) {
            this.removeView(mActionView);
            this.mActionView = null;
        }
        final ProgressBar mProgressView = this.mProgressView;
        if (mProgressView != null) {
            this.removeView((View)mProgressView);
            this.mProgressView = null;
        }
        this.mSliceAction = mSliceAction;
        this.mEventInfo = mEventInfo;
        this.mObserver = mObserver;
        this.mActionView = null;
        this.mLoadingListener = mLoadingListener;
        final boolean defaultToggle = mSliceAction.isDefaultToggle();
        final int n = 0;
        if (defaultToggle) {
            final Switch mActionView2 = (Switch)LayoutInflater.from(this.getContext()).inflate(R$layout.abc_slice_switch, (ViewGroup)this, false);
            mActionView2.setChecked(mSliceAction.isChecked());
            mActionView2.setOnCheckedChangeListener((CompoundButton$OnCheckedChangeListener)this);
            mActionView2.setMinimumHeight(this.mImageSize);
            mActionView2.setMinimumWidth(this.mImageSize);
            this.addView((View)mActionView2);
            if (mImageSize != -1) {
                final ColorStateList list = new ColorStateList(new int[][] { checked_STATE_SET, empty_STATE_SET }, new int[] { mImageSize, SliceViewUtil.getColorAttr(this.getContext(), 16842800) });
                final Drawable wrap = DrawableCompat.wrap(mActionView2.getTrackDrawable());
                DrawableCompat.setTintList(wrap, list);
                mActionView2.setTrackDrawable(wrap);
                int n2;
                if ((n2 = SliceViewUtil.getColorAttr(this.getContext(), R$attr.colorSwitchThumbNormal)) == 0) {
                    n2 = ContextCompat.getColor(this.getContext(), R$color.switch_thumb_normal_material_light);
                }
                final ColorStateList list2 = new ColorStateList(new int[][] { checked_STATE_SET, empty_STATE_SET }, new int[] { mImageSize, n2 });
                final Drawable wrap2 = DrawableCompat.wrap(mActionView2.getThumbDrawable());
                DrawableCompat.setTintList(wrap2, list2);
                mActionView2.setThumbDrawable(wrap2);
            }
            this.mActionView = (View)mActionView2;
        }
        else if (mSliceAction.getIcon() != null) {
            if (mSliceAction.isToggle()) {
                final ImageToggle mActionView3 = new ImageToggle(this.getContext());
                mActionView3.setChecked(mSliceAction.isChecked());
                this.mActionView = (View)mActionView3;
            }
            else {
                this.mActionView = (View)new ImageView(this.getContext());
            }
            this.addView(this.mActionView);
            final Drawable loadDrawable = this.mSliceAction.getIcon().loadDrawable(this.getContext());
            ((ImageView)this.mActionView).setImageDrawable(loadDrawable);
            if (mImageSize != -1 && this.mSliceAction.getImageMode() == 0 && loadDrawable != null) {
                DrawableCompat.setTint(loadDrawable, mImageSize);
            }
            final FrameLayout$LayoutParams layoutParams = (FrameLayout$LayoutParams)this.mActionView.getLayoutParams();
            mImageSize = this.mImageSize;
            layoutParams.width = mImageSize;
            layoutParams.height = mImageSize;
            this.mActionView.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            mImageSize = n;
            if (mSliceAction.getImageMode() == 0) {
                mImageSize = this.mIconSize / 2;
            }
            this.mActionView.setPadding(mImageSize, mImageSize, mImageSize, mImageSize);
            mImageSize = 16843534;
            if (Build$VERSION.SDK_INT >= 21) {
                mImageSize = 16843868;
            }
            this.mActionView.setBackground(SliceViewUtil.getDrawable(this.getContext(), mImageSize));
            this.mActionView.setOnClickListener((View$OnClickListener)this);
        }
        if (this.mActionView != null) {
            CharSequence contentDescription;
            if (mSliceAction.getContentDescription() != null) {
                contentDescription = mSliceAction.getContentDescription();
            }
            else {
                contentDescription = mSliceAction.getTitle();
            }
            this.mActionView.setContentDescription(contentDescription);
        }
    }
    
    public void setLoading(final boolean b) {
        final int n = 0;
        if (b) {
            if (this.mProgressView == null) {
                this.addView((View)(this.mProgressView = (ProgressBar)LayoutInflater.from(this.getContext()).inflate(R$layout.abc_slice_progress_view, (ViewGroup)this, false)));
            }
            SliceViewUtil.tintIndeterminateProgressBar(this.getContext(), this.mProgressView);
        }
        final View mActionView = this.mActionView;
        int visibility;
        if (b) {
            visibility = 8;
        }
        else {
            visibility = 0;
        }
        mActionView.setVisibility(visibility);
        final ProgressBar mProgressView = this.mProgressView;
        int visibility2;
        if (b) {
            visibility2 = n;
        }
        else {
            visibility2 = 8;
        }
        mProgressView.setVisibility(visibility2);
    }
    
    public void toggle() {
        if (this.mActionView != null) {
            final SliceActionImpl mSliceAction = this.mSliceAction;
            if (mSliceAction != null && mSliceAction.isToggle()) {
                ((Checkable)this.mActionView).toggle();
            }
        }
    }
    
    private static class ImageToggle extends ImageView implements Checkable, View$OnClickListener
    {
        private boolean mIsChecked;
        private View$OnClickListener mListener;
        
        ImageToggle(final Context context) {
            super(context);
            super.setOnClickListener((View$OnClickListener)this);
        }
        
        public boolean isChecked() {
            return this.mIsChecked;
        }
        
        public void onClick(final View view) {
            this.toggle();
        }
        
        public int[] onCreateDrawableState(final int n) {
            final int[] onCreateDrawableState = super.onCreateDrawableState(n + 1);
            if (this.mIsChecked) {
                ImageView.mergeDrawableStates(onCreateDrawableState, SliceActionView.CHECKED_STATE_SET);
            }
            return onCreateDrawableState;
        }
        
        public void setChecked(final boolean mIsChecked) {
            if (this.mIsChecked != mIsChecked) {
                this.mIsChecked = mIsChecked;
                this.refreshDrawableState();
                final View$OnClickListener mListener = this.mListener;
                if (mListener != null) {
                    mListener.onClick((View)this);
                }
            }
        }
        
        public void setOnClickListener(final View$OnClickListener mListener) {
            this.mListener = mListener;
        }
        
        public void toggle() {
            this.setChecked(this.isChecked() ^ true);
        }
    }
    
    interface SliceActionLoadingListener
    {
        void onSliceActionLoading(final SliceItem p0, final int p1);
    }
}
