// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.settings;

import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.AttributeSet;
import android.content.Context;
import com.android.settingslib.RestrictedLockUtils;
import android.widget.SeekBar;

public class ToggleSeekBar extends SeekBar
{
    private String mAccessibilityLabel;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    
    public ToggleSeekBar(final Context context) {
        super(context);
        this.mEnforcedAdmin = null;
    }
    
    public ToggleSeekBar(final Context context, final AttributeSet set) {
        super(context, set);
        this.mEnforcedAdmin = null;
    }
    
    public ToggleSeekBar(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mEnforcedAdmin = null;
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        final String mAccessibilityLabel = this.mAccessibilityLabel;
        if (mAccessibilityLabel != null) {
            accessibilityNodeInfo.setText((CharSequence)mAccessibilityLabel);
        }
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        final RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin = this.mEnforcedAdmin;
        if (mEnforcedAdmin != null) {
            Dependency.get(ActivityStarter.class).postStartActivityDismissingKeyguard(RestrictedLockUtils.getShowAdminSupportDetailsIntent(super.mContext, mEnforcedAdmin), 0);
            return true;
        }
        if (!this.isEnabled()) {
            this.setEnabled(true);
        }
        return super.onTouchEvent(motionEvent);
    }
    
    public void setAccessibilityLabel(final String mAccessibilityLabel) {
        this.mAccessibilityLabel = mAccessibilityLabel;
    }
    
    public void setEnforcedAdmin(final RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin) {
        this.mEnforcedAdmin = mEnforcedAdmin;
    }
}
