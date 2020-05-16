// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.logging;

import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.statusbar.StatusBarState;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import com.android.systemui.log.LogLevel;
import kotlin.jvm.internal.Intrinsics;
import com.android.systemui.log.LogBuffer;

public final class QSLogger
{
    private final LogBuffer buffer;
    
    public QSLogger(final LogBuffer buffer) {
        Intrinsics.checkParameterIsNotNull(buffer, "buffer");
        this.buffer = buffer;
    }
    
    public static final /* synthetic */ LogBuffer access$getBuffer$p(final QSLogger qsLogger) {
        return qsLogger.buffer;
    }
    
    private final String toStateString(final int n) {
        String s;
        if (n != 0) {
            if (n != 1) {
                if (n != 2) {
                    s = "wrong state";
                }
                else {
                    s = "active";
                }
            }
            else {
                s = "inactive";
            }
        }
        else {
            s = "unavailable";
        }
        return s;
    }
    
    public final void logAllTilesChangeListening(final boolean bool1, final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "containerName");
        Intrinsics.checkParameterIsNotNull(str2, "allSpecs");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logAllTilesChangeListening.QSLogger$logAllTilesChangeListening$2 instance = QSLogger$logAllTilesChangeListening.QSLogger$logAllTilesChangeListening$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setBool1(bool1);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        access$getBuffer$p.push(obtain);
    }
    
    public final void logPanelExpanded(final boolean bool1, final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "containerName");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logPanelExpanded.QSLogger$logPanelExpanded$2 instance = QSLogger$logPanelExpanded.QSLogger$logPanelExpanded$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        obtain.setBool1(bool1);
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileAdded(final String str1) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logTileAdded.QSLogger$logTileAdded$2 instance = QSLogger$logTileAdded.QSLogger$logTileAdded$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileChangeListening(final String str1, final boolean bool1) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        final LogLevel verbose = LogLevel.VERBOSE;
        final QSLogger$logTileChangeListening.QSLogger$logTileChangeListening$2 instance = QSLogger$logTileChangeListening.QSLogger$logTileChangeListening$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", verbose, (Function1<? super LogMessage, String>)instance);
        obtain.setBool1(bool1);
        obtain.setStr1(str1);
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileClick(final String str1, final int int1, final int n) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logTileClick.QSLogger$logTileClick$2 instance = QSLogger$logTileClick.QSLogger$logTileClick$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        obtain.setStr2(StatusBarState.toShortString(int1));
        obtain.setStr3(this.toStateString(n));
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileDestroyed(final String str1, final String str2) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        Intrinsics.checkParameterIsNotNull(str2, "reason");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logTileDestroyed.QSLogger$logTileDestroyed$2 instance = QSLogger$logTileDestroyed.QSLogger$logTileDestroyed$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        obtain.setStr2(str2);
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileLongClick(final String str1, final int int1, final int n) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logTileLongClick.QSLogger$logTileLongClick$2 instance = QSLogger$logTileLongClick.QSLogger$logTileLongClick$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        obtain.setStr2(StatusBarState.toShortString(int1));
        obtain.setStr3(this.toStateString(n));
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileSecondaryClick(final String str1, final int int1, final int n) {
        Intrinsics.checkParameterIsNotNull(str1, "tileSpec");
        final LogLevel debug = LogLevel.DEBUG;
        final QSLogger$logTileSecondaryClick.QSLogger$logTileSecondaryClick$2 instance = QSLogger$logTileSecondaryClick.QSLogger$logTileSecondaryClick$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", debug, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str1);
        obtain.setInt1(int1);
        obtain.setStr2(StatusBarState.toShortString(int1));
        obtain.setStr3(this.toStateString(n));
        access$getBuffer$p.push(obtain);
    }
    
    public final void logTileUpdated(String str3, final QSTile.State state) {
        Intrinsics.checkParameterIsNotNull(str3, "tileSpec");
        Intrinsics.checkParameterIsNotNull(state, "state");
        final LogLevel verbose = LogLevel.VERBOSE;
        final QSLogger$logTileUpdated.QSLogger$logTileUpdated$2 instance = QSLogger$logTileUpdated.QSLogger$logTileUpdated$2.INSTANCE;
        final LogBuffer access$getBuffer$p = access$getBuffer$p(this);
        final LogMessageImpl obtain = access$getBuffer$p.obtain("QSLog", verbose, (Function1<? super LogMessage, String>)instance);
        obtain.setStr1(str3);
        final CharSequence label = state.label;
        final String s = null;
        if (label != null) {
            str3 = label.toString();
        }
        else {
            str3 = null;
        }
        obtain.setStr2(str3);
        final QSTile.Icon icon = state.icon;
        str3 = s;
        if (icon != null) {
            str3 = icon.toString();
        }
        obtain.setStr3(str3);
        obtain.setInt1(state.state);
        if (state instanceof QSTile.SignalState) {
            obtain.setBool1(true);
            final QSTile.SignalState signalState = (QSTile.SignalState)state;
            obtain.setBool2(signalState.activityIn);
            obtain.setBool3(signalState.activityOut);
        }
        access$getBuffer$p.push(obtain);
    }
}
