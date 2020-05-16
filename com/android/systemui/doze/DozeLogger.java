// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.doze;

import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class DozeLogger
{
    private final LogBuffer buffer;
    
    public DozeLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public final void logDozeStateChanged(final DozeMachine.State state) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logDozeStateChanged.DozeLogger$logDozeStateChanged$2.INSTANCE);
        obtain.setStr1(state.name());
        buffer.push(obtain);
    }
    
    public final void logDozeSuppressed(final DozeMachine.State state) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logDozeSuppressed.DozeLogger$logDozeSuppressed$2.INSTANCE);
        obtain.setStr1(state.name());
        buffer.push(obtain);
    }
    
    public final void logDozing(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logDozing.DozeLogger$logDozing$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logEmergencyCall() {
        final LogBuffer buffer = this.buffer;
        buffer.push(buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logEmergencyCall.DozeLogger$logEmergencyCall$2.INSTANCE));
    }
    
    public final void logFling(final boolean bool1, final boolean bool2, final boolean bool3, final boolean bool4) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logFling.DozeLogger$logFling$2.INSTANCE);
        obtain.setBool1(bool1);
        obtain.setBool2(bool2);
        obtain.setBool3(bool3);
        obtain.setBool4(bool4);
        buffer.push(obtain);
    }
    
    public final void logKeyguardBouncerChanged(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logKeyguardBouncerChanged.DozeLogger$logKeyguardBouncerChanged$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logKeyguardVisibilityChange(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logKeyguardVisibilityChange.DozeLogger$logKeyguardVisibilityChange$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logMissedTick(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "delay");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.ERROR, (Function1<? super LogMessage, String>)DozeLogger$logMissedTick.DozeLogger$logMissedTick$2.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logNotificationPulse() {
        final LogBuffer buffer = this.buffer;
        buffer.push(buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logNotificationPulse.DozeLogger$logNotificationPulse$2.INSTANCE));
    }
    
    public final void logPickupWakeup(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logPickupWakeup.DozeLogger$logPickupWakeup$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logProximityResult(final boolean bool1, final long long1, final int int1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logProximityResult.DozeLogger$logProximityResult$2.INSTANCE);
        obtain.setBool1(bool1);
        obtain.setLong1(long1);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logPulseDropped(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "reason");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logPulseDropped.DozeLogger$logPulseDropped$4.INSTANCE);
        obtain.setStr1(str1);
        buffer.push(obtain);
    }
    
    public final void logPulseDropped(final boolean bool1, final DozeMachine.State state, final boolean bool2) {
        Intrinsics.checkParameterIsNotNull(state, "state");
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logPulseDropped.DozeLogger$logPulseDropped$2.INSTANCE);
        obtain.setBool1(bool1);
        obtain.setStr1(state.name());
        obtain.setBool2(bool2);
        buffer.push(obtain);
    }
    
    public final void logPulseFinish() {
        final LogBuffer buffer = this.buffer;
        buffer.push(buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logPulseFinish.DozeLogger$logPulseFinish$2.INSTANCE));
    }
    
    public final void logPulseStart(final int int1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logPulseStart.DozeLogger$logPulseStart$2.INSTANCE);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logPulseTouchDisabledByProx(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logPulseTouchDisabledByProx.DozeLogger$logPulseTouchDisabledByProx$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logScreenOff(final int int1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logScreenOff.DozeLogger$logScreenOff$2.INSTANCE);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logScreenOn(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.INFO, (Function1<? super LogMessage, String>)DozeLogger$logScreenOn.DozeLogger$logScreenOn$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
    
    public final void logSensorTriggered(final int int1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logSensorTriggered.DozeLogger$logSensorTriggered$2.INSTANCE);
        obtain.setInt1(int1);
        buffer.push(obtain);
    }
    
    public final void logTimeTickScheduled(final long long1, final long long2) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logTimeTickScheduled.DozeLogger$logTimeTickScheduled$2.INSTANCE);
        obtain.setLong1(long1);
        obtain.setLong2(long2);
        buffer.push(obtain);
    }
    
    public final void logWakeDisplay(final boolean bool1) {
        final LogBuffer buffer = this.buffer;
        final LogMessageImpl obtain = buffer.obtain("DozeLog", LogLevel.DEBUG, (Function1<? super LogMessage, String>)DozeLogger$logWakeDisplay.DozeLogger$logWakeDisplay$2.INSTANCE);
        obtain.setBool1(bool1);
        buffer.push(obtain);
    }
}
