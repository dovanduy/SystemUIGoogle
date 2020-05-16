// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.screenshot;

import android.view.View$OnClickListener;
import android.graphics.drawable.Icon;
import com.android.systemui.R$id;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import android.view.View;
import android.app.PendingIntent;
import com.android.systemui.R$color;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScreenshotActionChip extends LinearLayout
{
    private ImageView mIcon;
    private int mIconColor;
    private TextView mText;
    
    public ScreenshotActionChip(final Context context) {
        this(context, null);
    }
    
    public ScreenshotActionChip(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public ScreenshotActionChip(final Context context, final AttributeSet set, final int n) {
        this(context, set, n, 0);
    }
    
    public ScreenshotActionChip(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
        this.mIconColor = context.getColor(R$color.global_screenshot_button_icon);
    }
    
    protected void onFinishInflate() {
        this.mIcon = (ImageView)this.findViewById(R$id.screenshot_action_chip_icon);
        this.mText = (TextView)this.findViewById(R$id.screenshot_action_chip_text);
    }
    
    void setIcon(final Icon imageIcon, final boolean b) {
        if (b) {
            imageIcon.setTint(this.mIconColor);
        }
        this.mIcon.setImageIcon(imageIcon);
    }
    
    void setPendingIntent(final PendingIntent pendingIntent, final Runnable runnable) {
        this.setOnClickListener((View$OnClickListener)new _$$Lambda$ScreenshotActionChip$ES_yR9a8Hwpm3rCY_qaXPGrIojI(pendingIntent, runnable));
    }
    
    void setText(final CharSequence text) {
        this.mText.setText(text);
    }
}
