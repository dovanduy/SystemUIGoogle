// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.controls.ui;

import com.android.systemui.R$drawable;
import kotlin.jvm.functions.Function1;
import kotlin.collections.MapsKt;
import kotlin.TuplesKt;
import com.android.systemui.R$color;
import kotlin.Pair;
import java.util.Map;

public final class RenderInfoKt
{
    private static final Map<Integer, Pair<Integer, Integer>> deviceColorMap;
    private static final Map<Integer, IconState> deviceIconMap;
    
    static {
        final Integer value = 49002;
        final Pair<Integer, Pair<Integer, Integer>> to = TuplesKt.to(value, new Pair<Integer, Integer>(R$color.thermo_heat_foreground, R$color.control_enabled_thermo_heat_background));
        final Integer value2 = 49003;
        final Pair<Integer, Pair<Integer, Integer>> to2 = TuplesKt.to(value2, new Pair<Integer, Integer>(R$color.thermo_cool_foreground, R$color.control_enabled_thermo_cool_background));
        final Integer value3 = 13;
        deviceColorMap = MapsKt.withDefault((Map<Integer, ?>)MapsKt.mapOf(to, to2, TuplesKt.to(value3, new Pair<Integer, Integer>(R$color.light_foreground, R$color.control_enabled_light_background))), (Function1<? super Integer, ?>)RenderInfoKt$deviceColorMap.RenderInfoKt$deviceColorMap$1.INSTANCE);
        deviceIconMap = MapsKt.withDefault((Map<Integer, ?>)MapsKt.mapOf(TuplesKt.to(49000, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(49001, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(value, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(value2, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(49004, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(49005, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(49, new IconState(R$drawable.ic_device_thermostat_off, R$drawable.ic_device_thermostat_on)), TuplesKt.to(value3, new IconState(R$drawable.ic_device_light_off, R$drawable.ic_device_light_on)), TuplesKt.to(50, new IconState(R$drawable.ic_device_camera_off, R$drawable.ic_device_camera_on)), TuplesKt.to(45, new IconState(R$drawable.ic_device_lock_off, R$drawable.ic_device_lock_on)), TuplesKt.to(21, new IconState(R$drawable.ic_device_switch_off, R$drawable.ic_device_switch_on)), TuplesKt.to(15, new IconState(R$drawable.ic_device_outlet_off, R$drawable.ic_device_outlet_on)), TuplesKt.to(32, new IconState(R$drawable.ic_device_vacuum_off, R$drawable.ic_device_vacuum_on)), TuplesKt.to(26, new IconState(R$drawable.ic_device_mop_off, R$drawable.ic_device_mop_on)), TuplesKt.to(3, new IconState(R$drawable.ic_device_air_freshener_off, R$drawable.ic_device_air_freshener_on)), TuplesKt.to(4, new IconState(R$drawable.ic_device_air_purifier_off, R$drawable.ic_device_air_purifier_on)), TuplesKt.to(8, new IconState(R$drawable.ic_device_fan_off, R$drawable.ic_device_fan_on)), TuplesKt.to(10, new IconState(R$drawable.ic_device_hood_off, R$drawable.ic_device_hood_on)), TuplesKt.to(12, new IconState(R$drawable.ic_device_kettle_off, R$drawable.ic_device_kettle_on)), TuplesKt.to(14, new IconState(R$drawable.ic_device_microwave_off, R$drawable.ic_device_microwave_on)), TuplesKt.to(17, new IconState(R$drawable.ic_device_remote_control_off, R$drawable.ic_device_remote_control_on)), TuplesKt.to(18, new IconState(R$drawable.ic_device_set_top_off, R$drawable.ic_device_set_top_on)), TuplesKt.to(20, new IconState(R$drawable.ic_device_styler_off, R$drawable.ic_device_styler_on)), TuplesKt.to(22, new IconState(R$drawable.ic_device_tv_off, R$drawable.ic_device_tv_on)), TuplesKt.to(23, new IconState(R$drawable.ic_device_water_heater_off, R$drawable.ic_device_water_heater_on)), TuplesKt.to(24, new IconState(R$drawable.ic_device_dishwasher_off, R$drawable.ic_device_dishwasher_on)), TuplesKt.to(28, new IconState(R$drawable.ic_device_multicooker_off, R$drawable.ic_device_multicooker_on)), TuplesKt.to(30, new IconState(R$drawable.ic_device_sprinkler_off, R$drawable.ic_device_sprinkler_on)), TuplesKt.to(31, new IconState(R$drawable.ic_device_washer_off, R$drawable.ic_device_washer_on)), TuplesKt.to(34, new IconState(R$drawable.ic_device_blinds_off, R$drawable.ic_device_blinds_on)), TuplesKt.to(38, new IconState(R$drawable.ic_device_drawer_off, R$drawable.ic_device_drawer_on)), TuplesKt.to(39, new IconState(R$drawable.ic_device_garage_off, R$drawable.ic_device_garage_on)), TuplesKt.to(40, new IconState(R$drawable.ic_device_gate_off, R$drawable.ic_device_gate_on)), TuplesKt.to(41, new IconState(R$drawable.ic_device_pergola_off, R$drawable.ic_device_pergola_on)), TuplesKt.to(43, new IconState(R$drawable.ic_device_window_off, R$drawable.ic_device_window_on)), TuplesKt.to(44, new IconState(R$drawable.ic_device_valve_off, R$drawable.ic_device_valve_on)), TuplesKt.to(46, new IconState(R$drawable.ic_device_security_system_off, R$drawable.ic_device_security_system_on)), TuplesKt.to(48, new IconState(R$drawable.ic_device_refrigerator_off, R$drawable.ic_device_refrigerator_on)), TuplesKt.to(51, new IconState(R$drawable.ic_device_doorbell_off, R$drawable.ic_device_doorbell_on)), TuplesKt.to(52, new IconState(-1, -1))), (Function1<? super Integer, ?>)RenderInfoKt$deviceIconMap.RenderInfoKt$deviceIconMap$1.INSTANCE);
    }
}
