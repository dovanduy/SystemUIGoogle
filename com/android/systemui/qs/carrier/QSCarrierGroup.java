// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.carrier;

import android.widget.TextView;
import android.view.View;
import com.android.systemui.R$id;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.LinearLayout;

public class QSCarrierGroup extends LinearLayout
{
    public QSCarrierGroup(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    QSCarrier getCarrier1View() {
        return (QSCarrier)this.findViewById(R$id.carrier1);
    }
    
    QSCarrier getCarrier2View() {
        return (QSCarrier)this.findViewById(R$id.carrier2);
    }
    
    QSCarrier getCarrier3View() {
        return (QSCarrier)this.findViewById(R$id.carrier3);
    }
    
    View getCarrierDivider1() {
        return this.findViewById(R$id.qs_carrier_divider1);
    }
    
    View getCarrierDivider2() {
        return this.findViewById(R$id.qs_carrier_divider2);
    }
    
    TextView getNoSimTextView() {
        return (TextView)this.findViewById(R$id.no_carrier_text);
    }
}
