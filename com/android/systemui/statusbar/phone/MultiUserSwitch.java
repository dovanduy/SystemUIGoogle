// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import com.android.systemui.Dependency;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.view.accessibility.AccessibilityEvent;
import com.android.systemui.plugins.qs.DetailAdapter;
import android.view.ViewGroup;
import android.view.View;
import com.android.systemui.R$string;
import android.text.TextUtils;
import java.util.function.Supplier;
import com.android.systemui.DejankUtils;
import java.util.Objects;
import android.content.ContentResolver;
import com.android.systemui.R$bool;
import android.content.ComponentName;
import android.app.admin.DevicePolicyManager;
import android.provider.Settings$Global;
import android.util.AttributeSet;
import android.content.Context;
import android.os.UserManager;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.statusbar.policy.KeyguardUserSwitcher;
import android.view.View$OnClickListener;
import android.widget.FrameLayout;

public class MultiUserSwitch extends FrameLayout implements View$OnClickListener
{
    private boolean mKeyguardMode;
    private KeyguardUserSwitcher mKeyguardUserSwitcher;
    protected QSPanel mQsPanel;
    private final int[] mTmpInt2;
    private UserSwitcherController.BaseUserAdapter mUserListener;
    final UserManager mUserManager;
    protected UserSwitcherController mUserSwitcherController;
    
    public MultiUserSwitch(final Context context, final AttributeSet set) {
        super(context, set);
        this.mTmpInt2 = new int[2];
        this.mUserManager = UserManager.get(this.getContext());
    }
    
    private void refreshContentDescription() {
        final UserManager mUserManager = this.mUserManager;
        Objects.requireNonNull(mUserManager);
        final boolean booleanValue = DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$lI0G44FrWq1y6zRNSfoZ1zlECXw(mUserManager));
        CharSequence string = null;
        String currentUserName = null;
        Label_0057: {
            if (booleanValue) {
                final UserSwitcherController mUserSwitcherController = this.mUserSwitcherController;
                if (mUserSwitcherController != null) {
                    currentUserName = mUserSwitcherController.getCurrentUserName(super.mContext);
                    break Label_0057;
                }
            }
            currentUserName = null;
        }
        if (!TextUtils.isEmpty((CharSequence)currentUserName)) {
            string = super.mContext.getString(R$string.accessibility_quick_settings_user, new Object[] { currentUserName });
        }
        if (!TextUtils.equals(this.getContentDescription(), string)) {
            this.setContentDescription(string);
        }
    }
    
    private void registerListener() {
        if (this.mUserManager.isUserSwitcherEnabled() && this.mUserListener == null) {
            final UserSwitcherController mUserSwitcherController = this.mUserSwitcherController;
            if (mUserSwitcherController != null) {
                this.mUserListener = new UserSwitcherController.BaseUserAdapter(mUserSwitcherController) {
                    public View getView(final int n, final View view, final ViewGroup viewGroup) {
                        return null;
                    }
                    
                    public void notifyDataSetChanged() {
                        MultiUserSwitch.this.refreshContentDescription();
                    }
                };
                this.refreshContentDescription();
            }
        }
    }
    
    protected DetailAdapter getUserDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }
    
    public boolean hasOverlappingRendering() {
        return false;
    }
    
    public boolean isMultiUserEnabled() {
        return DejankUtils.whitelistIpcs((Supplier<Boolean>)new _$$Lambda$MultiUserSwitch$xTCgN3mrazovqBP0eNshKQ_KXu4(this));
    }
    
    public void onClick(View child) {
        if (this.mKeyguardMode) {
            final KeyguardUserSwitcher mKeyguardUserSwitcher = this.mKeyguardUserSwitcher;
            if (mKeyguardUserSwitcher != null) {
                mKeyguardUserSwitcher.show(true);
            }
        }
        else if (this.mQsPanel != null && this.mUserSwitcherController != null) {
            if (this.getChildCount() > 0) {
                child = this.getChildAt(0);
            }
            else {
                child = this;
            }
            ((View)child).getLocationInWindow(this.mTmpInt2);
            final int[] mTmpInt2 = this.mTmpInt2;
            mTmpInt2[0] += ((View)child).getWidth() / 2;
            final int[] mTmpInt3 = this.mTmpInt2;
            mTmpInt3[1] += ((View)child).getHeight() / 2;
            this.mQsPanel.showDetailAdapter(true, this.getUserDetailAdapter(), this.mTmpInt2);
        }
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.setOnClickListener((View$OnClickListener)this);
        this.refreshContentDescription();
    }
    
    public void onInitializeAccessibilityEvent(final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        accessibilityEvent.setClassName((CharSequence)Button.class.getName());
    }
    
    public void onInitializeAccessibilityNodeInfo(final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName((CharSequence)Button.class.getName());
    }
    
    public void setClickable(final boolean clickable) {
        super.setClickable(clickable);
        this.refreshContentDescription();
    }
    
    public void setKeyguardMode(final boolean mKeyguardMode) {
        this.mKeyguardMode = mKeyguardMode;
        this.registerListener();
    }
    
    public void setKeyguardUserSwitcher(final KeyguardUserSwitcher mKeyguardUserSwitcher) {
        this.mKeyguardUserSwitcher = mKeyguardUserSwitcher;
    }
    
    public void setQsPanel(final QSPanel mQsPanel) {
        this.mQsPanel = mQsPanel;
        this.setUserSwitcherController(Dependency.get(UserSwitcherController.class));
    }
    
    public void setUserSwitcherController(final UserSwitcherController mUserSwitcherController) {
        this.mUserSwitcherController = mUserSwitcherController;
        this.registerListener();
        this.refreshContentDescription();
    }
}
