// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.tuner;

import android.view.View$OnClickListener;
import com.android.systemui.R$string;
import android.widget.TextView;
import android.widget.Switch;
import com.android.systemui.R$id;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.R$layout;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.os.Bundle;
import android.content.ContentResolver;
import android.provider.Settings$Secure;
import android.app.Fragment;

public class PowerNotificationControlsFragment extends Fragment
{
    private boolean isEnabled() {
        final ContentResolver contentResolver = this.getContext().getContentResolver();
        boolean b = false;
        if (Settings$Secure.getInt(contentResolver, "show_importance_slider", 0) == 1) {
            b = true;
        }
        return b;
    }
    
    public void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
    }
    
    public View onCreateView(final LayoutInflater layoutInflater, final ViewGroup viewGroup, final Bundle bundle) {
        return layoutInflater.inflate(R$layout.power_notification_controls_settings, viewGroup, false);
    }
    
    public void onPause() {
        super.onPause();
        MetricsLogger.visibility(this.getContext(), 392, false);
    }
    
    public void onResume() {
        super.onResume();
        MetricsLogger.visibility(this.getContext(), 392, true);
    }
    
    public void onViewCreated(View viewById, final Bundle bundle) {
        super.onViewCreated(viewById, bundle);
        viewById = viewById.findViewById(R$id.switch_bar);
        final Switch switch1 = (Switch)viewById.findViewById(16908352);
        final TextView textView = (TextView)viewById.findViewById(R$id.switch_text);
        switch1.setChecked(this.isEnabled());
        String text;
        if (this.isEnabled()) {
            text = this.getString(R$string.switch_bar_on);
        }
        else {
            text = this.getString(R$string.switch_bar_off);
        }
        textView.setText((CharSequence)text);
        switch1.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                final int checked = (PowerNotificationControlsFragment.this.isEnabled() ^ true) ? 1 : 0;
                MetricsLogger.action(PowerNotificationControlsFragment.this.getContext(), 393, (boolean)(checked != 0));
                Settings$Secure.putInt(PowerNotificationControlsFragment.this.getContext().getContentResolver(), "show_importance_slider", checked);
                switch1.setChecked((boolean)(checked != 0));
                final TextView val$switchText = textView;
                String text;
                if (checked != 0) {
                    text = PowerNotificationControlsFragment.this.getString(R$string.switch_bar_on);
                }
                else {
                    text = PowerNotificationControlsFragment.this.getString(R$string.switch_bar_off);
                }
                val$switchText.setText((CharSequence)text);
            }
        });
    }
}
