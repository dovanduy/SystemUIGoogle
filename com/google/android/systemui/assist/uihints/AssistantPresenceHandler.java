// 
// Decompiled by Procyon v0.5.36
// 

package com.google.android.systemui.assist.uihints;

import java.util.Iterator;
import android.content.ComponentName;
import java.util.HashSet;
import java.util.Set;
import com.android.internal.app.AssistUtils;

public class AssistantPresenceHandler implements ConfigInfoListener
{
    private final AssistUtils mAssistUtils;
    private final Set<AssistantPresenceChangeListener> mAssistantPresenceChangeListeners;
    private boolean mGoogleIsAssistant;
    private boolean mNgaIsAssistant;
    private boolean mSysUiIsNgaUi;
    private final Set<SysUiIsNgaUiChangeListener> mSysUiIsNgaUiChangeListeners;
    
    AssistantPresenceHandler(final AssistUtils mAssistUtils) {
        this.mAssistantPresenceChangeListeners = new HashSet<AssistantPresenceChangeListener>();
        this.mSysUiIsNgaUiChangeListeners = new HashSet<SysUiIsNgaUiChangeListener>();
        this.mAssistUtils = mAssistUtils;
    }
    
    private boolean fetchIsGoogleAssistant() {
        final ComponentName assistComponentForUser = this.mAssistUtils.getAssistComponentForUser(-2);
        return assistComponentForUser != null && "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService".equals(assistComponentForUser.flattenToString());
    }
    
    private void updateAssistantPresence(final boolean mGoogleIsAssistant, final boolean b, final boolean b2) {
        final boolean b3 = true;
        final boolean mNgaIsAssistant = mGoogleIsAssistant && b;
        final boolean mSysUiIsNgaUi = mNgaIsAssistant && b2 && b3;
        if (this.mGoogleIsAssistant != mGoogleIsAssistant || this.mNgaIsAssistant != mNgaIsAssistant) {
            this.mGoogleIsAssistant = mGoogleIsAssistant;
            this.mNgaIsAssistant = mNgaIsAssistant;
            final Iterator<AssistantPresenceChangeListener> iterator = this.mAssistantPresenceChangeListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().onAssistantPresenceChanged(this.mGoogleIsAssistant, this.mNgaIsAssistant);
            }
        }
        if (this.mSysUiIsNgaUi != mSysUiIsNgaUi) {
            this.mSysUiIsNgaUi = mSysUiIsNgaUi;
            final Iterator<SysUiIsNgaUiChangeListener> iterator2 = this.mSysUiIsNgaUiChangeListeners.iterator();
            while (iterator2.hasNext()) {
                iterator2.next().onSysUiIsNgaUiChanged(this.mSysUiIsNgaUi);
            }
        }
    }
    
    public boolean isNgaAssistant() {
        return this.mNgaIsAssistant;
    }
    
    public boolean isSysUiNgaUi() {
        return this.mSysUiIsNgaUi;
    }
    
    @Override
    public void onConfigInfo(final ConfigInfo configInfo) {
        this.updateAssistantPresence(this.fetchIsGoogleAssistant(), configInfo.ngaIsAssistant, configInfo.sysUiIsNgaUi);
    }
    
    public void registerAssistantPresenceChangeListener(final AssistantPresenceChangeListener assistantPresenceChangeListener) {
        this.mAssistantPresenceChangeListeners.add(assistantPresenceChangeListener);
    }
    
    public void registerSysUiIsNgaUiChangeListener(final SysUiIsNgaUiChangeListener sysUiIsNgaUiChangeListener) {
        this.mSysUiIsNgaUiChangeListeners.add(sysUiIsNgaUiChangeListener);
    }
    
    public void requestAssistantPresenceUpdate() {
        this.updateAssistantPresence(this.fetchIsGoogleAssistant(), this.mNgaIsAssistant, this.mSysUiIsNgaUi);
    }
    
    public interface AssistantPresenceChangeListener
    {
        void onAssistantPresenceChanged(final boolean p0, final boolean p1);
    }
    
    interface SysUiIsNgaUiChangeListener
    {
        void onSysUiIsNgaUiChanged(final boolean p0);
    }
}
