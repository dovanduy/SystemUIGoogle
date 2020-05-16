// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.charging;

import android.animation.ValueAnimator;
import android.animation.Animator;
import android.animation.AnimatorSet;
import com.android.systemui.Interpolators;
import android.animation.TimeInterpolator;
import android.view.animation.PathInterpolator;
import android.animation.ObjectAnimator;
import com.android.systemui.R$dimen;
import com.android.systemui.R$integer;
import java.text.NumberFormat;
import android.widget.TextView;
import com.android.systemui.R$id;
import android.widget.ImageView;
import android.graphics.drawable.Animatable;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.ContextThemeWrapper;
import com.android.systemui.R$style;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.FrameLayout;

public class WirelessChargingLayout extends FrameLayout
{
    public WirelessChargingLayout(final Context context, final int n, final boolean b) {
        super(context);
        this.init(context, null, n, b);
    }
    
    public WirelessChargingLayout(final Context context, final AttributeSet set) {
        super(context, set);
        this.init(context, set, false);
    }
    
    private void init(final Context context, final AttributeSet set, final int n, final boolean b) {
        int n2 = R$style.ChargingAnim_WallpaperBackground;
        if (b) {
            n2 = R$style.ChargingAnim_DarkBackground;
        }
        FrameLayout.inflate((Context)new ContextThemeWrapper(context, n2), R$layout.wireless_charging_layout, (ViewGroup)this);
        final Animatable animatable = (Animatable)((ImageView)this.findViewById(R$id.wireless_charging_view)).getDrawable();
        final TextView textView = (TextView)this.findViewById(R$id.wireless_charging_percentage);
        if (n != -1) {
            textView.setText((CharSequence)NumberFormat.getPercentInstance().format(n / 100.0f));
            textView.setAlpha(0.0f);
        }
        final long startDelay = context.getResources().getInteger(R$integer.wireless_charging_fade_offset);
        final long duration = context.getResources().getInteger(R$integer.wireless_charging_fade_duration);
        final ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object)textView, "textSize", new float[] { context.getResources().getFloat(R$dimen.wireless_charging_anim_battery_level_text_size_start), context.getResources().getFloat(R$dimen.wireless_charging_anim_battery_level_text_size_end) });
        ((ValueAnimator)ofFloat).setInterpolator((TimeInterpolator)new PathInterpolator(0.0f, 0.0f, 0.0f, 1.0f));
        ((ValueAnimator)ofFloat).setDuration((long)context.getResources().getInteger(R$integer.wireless_charging_battery_level_text_scale_animation_duration));
        final ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat((Object)textView, "alpha", new float[] { 0.0f, 1.0f });
        ((ValueAnimator)ofFloat2).setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        ((ValueAnimator)ofFloat2).setDuration((long)context.getResources().getInteger(R$integer.wireless_charging_battery_level_text_opacity_duration));
        ((ValueAnimator)ofFloat2).setStartDelay((long)context.getResources().getInteger(R$integer.wireless_charging_anim_opacity_offset));
        final ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat((Object)textView, "alpha", new float[] { 1.0f, 0.0f });
        ((ValueAnimator)ofFloat3).setDuration(duration);
        ((ValueAnimator)ofFloat3).setInterpolator((TimeInterpolator)Interpolators.LINEAR);
        ((ValueAnimator)ofFloat3).setStartDelay(startDelay);
        final AnimatorSet set2 = new AnimatorSet();
        set2.playTogether(new Animator[] { (Animator)ofFloat, (Animator)ofFloat2, (Animator)ofFloat3 });
        animatable.start();
        set2.start();
    }
    
    private void init(final Context context, final AttributeSet set, final boolean b) {
        this.init(context, set, -1, false);
    }
}
