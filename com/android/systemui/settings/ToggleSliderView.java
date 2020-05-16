// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import com.android.settingslib.RestrictedLockUtils;
import android.view.MotionEvent;
import android.content.res.TypedArray;
import com.android.systemui.R$id;
import com.android.systemui.R$styleable;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.View;
import android.widget.SeekBar;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.CompoundButton;
import android.widget.SeekBar$OnSeekBarChangeListener;
import com.android.systemui.statusbar.policy.BrightnessMirrorController;
import android.widget.TextView;
import android.widget.CompoundButton$OnCheckedChangeListener;
import android.widget.RelativeLayout;

public class ToggleSliderView extends RelativeLayout implements ToggleSlider
{
    private final CompoundButton$OnCheckedChangeListener mCheckListener;
    private TextView mLabel;
    private Listener mListener;
    private ToggleSliderView mMirror;
    private BrightnessMirrorController mMirrorController;
    private final SeekBar$OnSeekBarChangeListener mSeekListener;
    private ToggleSeekBar mSlider;
    private CompoundButton mToggle;
    private boolean mTracking;
    
    public ToggleSliderView(final Context context) {
        this(context, null);
    }
    
    public ToggleSliderView(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ToggleSliderView(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mCheckListener = (CompoundButton$OnCheckedChangeListener)new CompoundButton$OnCheckedChangeListener() {
            public void onCheckedChanged(final CompoundButton compoundButton, final boolean checked) {
                ToggleSliderView.this.mSlider.setEnabled(checked ^ true);
                if (ToggleSliderView.this.mListener != null) {
                    final Listener access$100 = ToggleSliderView.this.mListener;
                    final ToggleSliderView this$0 = ToggleSliderView.this;
                    access$100.onChanged(this$0, this$0.mTracking, checked, ToggleSliderView.this.mSlider.getProgress(), false);
                }
                if (ToggleSliderView.this.mMirror != null) {
                    ToggleSliderView.this.mMirror.mToggle.setChecked(checked);
                }
            }
        };
        this.mSeekListener = (SeekBar$OnSeekBarChangeListener)new SeekBar$OnSeekBarChangeListener() {
            public void onProgressChanged(final SeekBar seekBar, final int n, final boolean b) {
                if (ToggleSliderView.this.mListener != null) {
                    final Listener access$100 = ToggleSliderView.this.mListener;
                    final ToggleSliderView this$0 = ToggleSliderView.this;
                    access$100.onChanged(this$0, this$0.mTracking, ToggleSliderView.this.mToggle.isChecked(), n, false);
                }
            }
            
            public void onStartTrackingTouch(final SeekBar seekBar) {
                ToggleSliderView.this.mTracking = true;
                if (ToggleSliderView.this.mListener != null) {
                    final Listener access$100 = ToggleSliderView.this.mListener;
                    final ToggleSliderView this$0 = ToggleSliderView.this;
                    access$100.onChanged(this$0, this$0.mTracking, ToggleSliderView.this.mToggle.isChecked(), ToggleSliderView.this.mSlider.getProgress(), false);
                }
                ToggleSliderView.this.mToggle.setChecked(false);
                if (ToggleSliderView.this.mMirrorController != null) {
                    ToggleSliderView.this.mMirrorController.showMirror();
                    ToggleSliderView.this.mMirrorController.setLocation((View)ToggleSliderView.this.getParent());
                }
            }
            
            public void onStopTrackingTouch(final SeekBar seekBar) {
                ToggleSliderView.this.mTracking = false;
                if (ToggleSliderView.this.mListener != null) {
                    final Listener access$100 = ToggleSliderView.this.mListener;
                    final ToggleSliderView this$0 = ToggleSliderView.this;
                    access$100.onChanged(this$0, this$0.mTracking, ToggleSliderView.this.mToggle.isChecked(), ToggleSliderView.this.mSlider.getProgress(), true);
                }
                if (ToggleSliderView.this.mMirrorController != null) {
                    ToggleSliderView.this.mMirrorController.hideMirror();
                }
            }
        };
        View.inflate(context, R$layout.status_bar_toggle_slider, (ViewGroup)this);
        context.getResources();
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.ToggleSliderView, n, 0);
        (this.mToggle = (CompoundButton)this.findViewById(R$id.toggle)).setOnCheckedChangeListener(this.mCheckListener);
        (this.mSlider = (ToggleSeekBar)this.findViewById(R$id.slider)).setOnSeekBarChangeListener(this.mSeekListener);
        (this.mLabel = (TextView)this.findViewById(R$id.label)).setText((CharSequence)obtainStyledAttributes.getString(R$styleable.ToggleSliderView_text));
        this.mSlider.setAccessibilityLabel(this.getContentDescription().toString());
        obtainStyledAttributes.recycle();
    }
    
    public boolean dispatchTouchEvent(final MotionEvent motionEvent) {
        if (this.mMirror != null) {
            final MotionEvent copy = motionEvent.copy();
            this.mMirror.dispatchTouchEvent(copy);
            copy.recycle();
        }
        return super.dispatchTouchEvent(motionEvent);
    }
    
    public int getValue() {
        return this.mSlider.getProgress();
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final Listener mListener = this.mListener;
        if (mListener != null) {
            mListener.onInit(this);
        }
    }
    
    public void setChecked(final boolean checked) {
        this.mToggle.setChecked(checked);
    }
    
    public void setEnforcedAdmin(final RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        final CompoundButton mToggle = this.mToggle;
        final boolean b = true;
        mToggle.setEnabled(enforcedAdmin == null);
        this.mSlider.setEnabled(enforcedAdmin == null && b);
        this.mSlider.setEnforcedAdmin(enforcedAdmin);
    }
    
    public void setMax(final int n) {
        this.mSlider.setMax(n);
        final ToggleSliderView mMirror = this.mMirror;
        if (mMirror != null) {
            mMirror.setMax(n);
        }
    }
    
    public void setMirror(final ToggleSliderView mMirror) {
        this.mMirror = mMirror;
        if (mMirror != null) {
            mMirror.setChecked(this.mToggle.isChecked());
            this.mMirror.setMax(this.mSlider.getMax());
            this.mMirror.setValue(this.mSlider.getProgress());
        }
    }
    
    public void setMirrorController(final BrightnessMirrorController mMirrorController) {
        this.mMirrorController = mMirrorController;
    }
    
    public void setOnChangedListener(final Listener mListener) {
        this.mListener = mListener;
    }
    
    public void setValue(final int n) {
        this.mSlider.setProgress(n);
        final ToggleSliderView mMirror = this.mMirror;
        if (mMirror != null) {
            mMirror.setValue(n);
        }
    }
}
