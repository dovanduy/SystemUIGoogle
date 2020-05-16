// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.widget.TextView;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.View;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.content.Context;
import android.view.View$AccessibilityDelegate;

class KeyguardClockAccessibilityDelegate extends View$AccessibilityDelegate
{
    private final String mFancyColon;
    
    public KeyguardClockAccessibilityDelegate(final Context context) {
        this.mFancyColon = context.getString(R$string.keyguard_fancy_colon);
    }
    
    public static boolean isNeeded(final Context context) {
        return TextUtils.isEmpty((CharSequence)context.getString(R$string.keyguard_fancy_colon)) ^ true;
    }
    
    private CharSequence replaceFancyColon(final CharSequence charSequence) {
        if (TextUtils.isEmpty((CharSequence)this.mFancyColon)) {
            return charSequence;
        }
        return charSequence.toString().replace(this.mFancyColon, ":");
    }
    
    public void onInitializeAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(view, accessibilityEvent);
        if (TextUtils.isEmpty((CharSequence)this.mFancyColon)) {
            return;
        }
        final CharSequence contentDescription = accessibilityEvent.getContentDescription();
        if (!TextUtils.isEmpty(contentDescription)) {
            accessibilityEvent.setContentDescription(this.replaceFancyColon(contentDescription));
        }
    }
    
    public void onInitializeAccessibilityNodeInfo(final View view, final AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(view, accessibilityNodeInfo);
        if (TextUtils.isEmpty((CharSequence)this.mFancyColon)) {
            return;
        }
        if (!TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
            accessibilityNodeInfo.setText(this.replaceFancyColon(accessibilityNodeInfo.getText()));
        }
        if (!TextUtils.isEmpty(accessibilityNodeInfo.getContentDescription())) {
            accessibilityNodeInfo.setContentDescription(this.replaceFancyColon(accessibilityNodeInfo.getContentDescription()));
        }
    }
    
    public void onPopulateAccessibilityEvent(final View view, final AccessibilityEvent accessibilityEvent) {
        if (TextUtils.isEmpty((CharSequence)this.mFancyColon)) {
            super.onPopulateAccessibilityEvent(view, accessibilityEvent);
        }
        else {
            final CharSequence text = ((TextView)view).getText();
            if (!TextUtils.isEmpty(text)) {
                accessibilityEvent.getText().add(this.replaceFancyColon(text));
            }
        }
    }
}
