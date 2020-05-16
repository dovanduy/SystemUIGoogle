// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.provider.DeviceConfig;

public final class QSSettingsControllerKt
{
    public static final QSSettingsPanel getQSSettingsPanelOption() {
        final int int1 = DeviceConfig.getInt("systemui", "qs_use_settings_panels", 0);
        QSSettingsPanel qsSettingsPanel;
        if (int1 != 1) {
            if (int1 != 2) {
                if (int1 != 3) {
                    qsSettingsPanel = QSSettingsPanel.DEFAULT;
                }
                else {
                    qsSettingsPanel = QSSettingsPanel.USE_DETAIL;
                }
            }
            else {
                qsSettingsPanel = QSSettingsPanel.OPEN_CLICK;
            }
        }
        else {
            qsSettingsPanel = QSSettingsPanel.OPEN_LONG_PRESS;
        }
        return qsSettingsPanel;
    }
}
