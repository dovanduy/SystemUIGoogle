// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.view.View;
import com.android.systemui.FontSizeUtils;
import com.android.systemui.R$dimen;
import android.content.res.Configuration;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import com.android.systemui.qs.DataUsageGraph;
import com.android.systemui.R$id;
import android.widget.TextView;
import com.android.settingslib.Utils;
import com.android.systemui.R$string;
import com.android.settingslib.net.DataUsageController;
import android.util.AttributeSet;
import android.content.Context;
import java.text.DecimalFormat;
import android.widget.LinearLayout;

public class DataUsageDetailView extends LinearLayout
{
    private final DecimalFormat FORMAT;
    
    public DataUsageDetailView(final Context context, final AttributeSet set) {
        super(context, set);
        this.FORMAT = new DecimalFormat("#.##");
    }
    
    private String formatBytes(final long a) {
        final double n = (double)Math.abs(a);
        double n2;
        String str;
        if (n > 1.048576E8) {
            n2 = n / 1.073741824E9;
            str = "GB";
        }
        else if (n > 102400.0) {
            n2 = n / 1048576.0;
            str = "MB";
        }
        else {
            n2 = n / 1024.0;
            str = "KB";
        }
        final StringBuilder sb = new StringBuilder();
        final DecimalFormat format = this.FORMAT;
        int n3;
        if (a < 0L) {
            n3 = -1;
        }
        else {
            n3 = 1;
        }
        sb.append(format.format(n2 * n3));
        sb.append(" ");
        sb.append(str);
        return sb.toString();
    }
    
    public void bind(final DataUsageController.DataUsageInfo dataUsageInfo) {
        final Resources resources = super.mContext.getResources();
        final long usageLevel = dataUsageInfo.usageLevel;
        final long warningLevel = dataUsageInfo.warningLevel;
        ColorStateList colorError = null;
        final int n = 1;
        int visibility = 0;
        int text = 0;
        long usageLevel2 = 0L;
        String text2 = null;
        String text3 = null;
        Label_0220: {
            if (usageLevel >= warningLevel) {
                final long limitLevel = dataUsageInfo.limitLevel;
                if (limitLevel > 0L) {
                    if (usageLevel <= limitLevel) {
                        text = R$string.quick_settings_cellular_detail_remaining_data;
                        usageLevel2 = limitLevel - usageLevel;
                        text2 = resources.getString(R$string.quick_settings_cellular_detail_data_used, new Object[] { this.formatBytes(usageLevel) });
                        text3 = resources.getString(R$string.quick_settings_cellular_detail_data_limit, new Object[] { this.formatBytes(dataUsageInfo.limitLevel) });
                        break Label_0220;
                    }
                    text = R$string.quick_settings_cellular_detail_over_limit;
                    usageLevel2 = usageLevel - limitLevel;
                    text2 = resources.getString(R$string.quick_settings_cellular_detail_data_used, new Object[] { this.formatBytes(usageLevel) });
                    text3 = resources.getString(R$string.quick_settings_cellular_detail_data_limit, new Object[] { this.formatBytes(dataUsageInfo.limitLevel) });
                    colorError = Utils.getColorError(super.mContext);
                    break Label_0220;
                }
            }
            text = R$string.quick_settings_cellular_detail_data_usage;
            usageLevel2 = dataUsageInfo.usageLevel;
            text2 = resources.getString(R$string.quick_settings_cellular_detail_data_warning, new Object[] { this.formatBytes(dataUsageInfo.warningLevel) });
            text3 = null;
        }
        ColorStateList colorAccent = colorError;
        if (colorError == null) {
            colorAccent = Utils.getColorAccent(super.mContext);
        }
        ((TextView)this.findViewById(16908310)).setText(text);
        final TextView textView = (TextView)this.findViewById(R$id.usage_text);
        textView.setText((CharSequence)this.formatBytes(usageLevel2));
        textView.setTextColor(colorAccent);
        final DataUsageGraph dataUsageGraph = (DataUsageGraph)this.findViewById(R$id.usage_graph);
        dataUsageGraph.setLevels(dataUsageInfo.limitLevel, dataUsageInfo.warningLevel, dataUsageInfo.usageLevel);
        ((TextView)this.findViewById(R$id.usage_carrier_text)).setText((CharSequence)dataUsageInfo.carrier);
        ((TextView)this.findViewById(R$id.usage_period_text)).setText((CharSequence)dataUsageInfo.period);
        final TextView textView2 = (TextView)this.findViewById(R$id.usage_info_top_text);
        int visibility2;
        if (text2 != null) {
            visibility2 = 0;
        }
        else {
            visibility2 = 8;
        }
        textView2.setVisibility(visibility2);
        textView2.setText((CharSequence)text2);
        final TextView textView3 = (TextView)this.findViewById(R$id.usage_info_bottom_text);
        int visibility3;
        if (text3 != null) {
            visibility3 = 0;
        }
        else {
            visibility3 = 8;
        }
        textView3.setVisibility(visibility3);
        textView3.setText((CharSequence)text3);
        int n2 = n;
        if (dataUsageInfo.warningLevel <= 0L) {
            if (dataUsageInfo.limitLevel > 0L) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
        }
        if (n2 == 0) {
            visibility = 8;
        }
        dataUsageGraph.setVisibility(visibility);
        if (n2 == 0) {
            textView2.setVisibility(8);
        }
    }
    
    protected void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FontSizeUtils.updateFontSize((View)this, 16908310, R$dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize((View)this, R$id.usage_text, R$dimen.qs_data_usage_usage_text_size);
        FontSizeUtils.updateFontSize((View)this, R$id.usage_carrier_text, R$dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize((View)this, R$id.usage_info_top_text, R$dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize((View)this, R$id.usage_period_text, R$dimen.qs_data_usage_text_size);
        FontSizeUtils.updateFontSize((View)this, R$id.usage_info_bottom_text, R$dimen.qs_data_usage_text_size);
    }
}
