// 
// Decompiled by Procyon v0.5.36
// 

package com.android.keyguard;

import android.view.MotionEvent;
import android.view.View;
import android.view.View$OnTouchListener;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;

public class EmergencyCarrierArea extends AlphaOptimizedLinearLayout
{
    private CarrierText mCarrierText;
    private EmergencyButton mEmergencyButton;
    
    public EmergencyCarrierArea(final Context context) {
        super(context);
    }
    
    public EmergencyCarrierArea(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mCarrierText = (CarrierText)this.findViewById(R$id.carrier_text);
        (this.mEmergencyButton = (EmergencyButton)this.findViewById(R$id.emergency_call_button)).setOnTouchListener((View$OnTouchListener)new View$OnTouchListener() {
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                if (EmergencyCarrierArea.this.mCarrierText.getVisibility() != 0) {
                    return false;
                }
                final int action = motionEvent.getAction();
                if (action != 0) {
                    if (action == 1) {
                        EmergencyCarrierArea.this.mCarrierText.animate().alpha(1.0f);
                    }
                }
                else {
                    EmergencyCarrierArea.this.mCarrierText.animate().alpha(0.0f);
                }
                return false;
            }
        });
    }
    
    public void setCarrierTextVisible(final boolean b) {
        final CarrierText mCarrierText = this.mCarrierText;
        int visibility;
        if (b) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mCarrierText.setVisibility(visibility);
    }
}
