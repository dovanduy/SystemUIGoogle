// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.carrier;

import java.util.Objects;
import com.android.systemui.qs.QuickStatusBarHeader;
import android.content.res.ColorStateList;
import com.android.settingslib.Utils;
import android.graphics.drawable.Drawable;
import com.android.settingslib.graph.SignalDrawable;
import com.android.systemui.R$id;
import android.text.TextUtils;
import com.android.systemui.R$string;
import android.util.AttributeSet;
import android.content.Context;
import android.widget.ImageView;
import android.view.View;
import com.android.systemui.DualToneHandler;
import android.widget.TextView;
import android.widget.LinearLayout;

public class QSCarrier extends LinearLayout
{
    private TextView mCarrierText;
    private float mColorForegroundIntensity;
    private DualToneHandler mDualToneHandler;
    private CellSignalState mLastSignalState;
    private View mMobileGroup;
    private ImageView mMobileRoaming;
    private ImageView mMobileSignal;
    
    public QSCarrier(final Context context) {
        super(context);
    }
    
    public QSCarrier(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public QSCarrier(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
    }
    
    public QSCarrier(final Context context, final AttributeSet set, final int n, final int n2) {
        super(context, set, n, n2);
    }
    
    private boolean hasValidTypeContentDescription(final String s) {
        return TextUtils.equals((CharSequence)s, (CharSequence)super.mContext.getString(R$string.data_connection_no_internet)) || TextUtils.equals((CharSequence)s, (CharSequence)super.mContext.getString(R$string.cell_data_off_content_description)) || TextUtils.equals((CharSequence)s, (CharSequence)super.mContext.getString(R$string.not_default_data_content_description));
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mDualToneHandler = new DualToneHandler(this.getContext());
        this.mMobileGroup = this.findViewById(R$id.mobile_combo);
        this.mMobileSignal = (ImageView)this.findViewById(R$id.mobile_signal);
        this.mMobileRoaming = (ImageView)this.findViewById(R$id.mobile_roaming);
        this.mCarrierText = (TextView)this.findViewById(R$id.qs_carrier_text);
        this.mMobileSignal.setImageDrawable((Drawable)new SignalDrawable(super.mContext));
        final int colorAttrDefaultColor = Utils.getColorAttrDefaultColor(super.mContext, 16842800);
        ColorStateList.valueOf(colorAttrDefaultColor);
        this.mColorForegroundIntensity = QuickStatusBarHeader.getColorIntensity(colorAttrDefaultColor);
    }
    
    public void setCarrierText(final CharSequence text) {
        this.mCarrierText.setText(text);
    }
    
    public boolean updateState(final CellSignalState cellSignalState) {
        final boolean equals = Objects.equals(cellSignalState, this.mLastSignalState);
        final int n = 0;
        if (equals) {
            return false;
        }
        this.mLastSignalState = cellSignalState;
        final View mMobileGroup = this.mMobileGroup;
        int visibility;
        if (cellSignalState.visible) {
            visibility = 0;
        }
        else {
            visibility = 8;
        }
        mMobileGroup.setVisibility(visibility);
        if (cellSignalState.visible) {
            final ImageView mMobileRoaming = this.mMobileRoaming;
            int visibility2;
            if (cellSignalState.roaming) {
                visibility2 = n;
            }
            else {
                visibility2 = 8;
            }
            mMobileRoaming.setVisibility(visibility2);
            final ColorStateList value = ColorStateList.valueOf(this.mDualToneHandler.getSingleColor(this.mColorForegroundIntensity));
            this.mMobileRoaming.setImageTintList(value);
            this.mMobileSignal.setImageTintList(value);
            this.mMobileSignal.setImageLevel(cellSignalState.mobileSignalIconId);
            final StringBuilder contentDescription = new StringBuilder();
            final String contentDescription2 = cellSignalState.contentDescription;
            if (contentDescription2 != null) {
                contentDescription.append(contentDescription2);
                contentDescription.append(", ");
            }
            if (cellSignalState.roaming) {
                contentDescription.append(super.mContext.getString(R$string.data_connection_roaming));
                contentDescription.append(", ");
            }
            if (this.hasValidTypeContentDescription(cellSignalState.typeContentDescription)) {
                contentDescription.append(cellSignalState.typeContentDescription);
            }
            this.mMobileSignal.setContentDescription((CharSequence)contentDescription);
        }
        return true;
    }
}
