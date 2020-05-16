// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.media;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import android.app.Notification;
import java.util.List;
import android.content.Context;
import java.util.Collection;

public abstract class MediaManager
{
    protected final Collection<MediaDeviceCallback> mCallbacks;
    protected Context mContext;
    protected final List<MediaDevice> mMediaDevices;
    
    MediaManager(final Context mContext, final Notification notification) {
        this.mCallbacks = new CopyOnWriteArrayList<MediaDeviceCallback>();
        this.mMediaDevices = new ArrayList<MediaDevice>();
        this.mContext = mContext;
    }
    
    private Collection<MediaDeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList<MediaDeviceCallback>(this.mCallbacks);
    }
    
    protected void dispatchConnectedDeviceChanged(final String s) {
        final Iterator<MediaDeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onConnectedDeviceChanged(s);
        }
    }
    
    protected void dispatchDeviceListAdded() {
        final Iterator<MediaDeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onDeviceListAdded(new ArrayList<MediaDevice>(this.mMediaDevices));
        }
    }
    
    protected void dispatchOnRequestFailed(final int n) {
        final Iterator<MediaDeviceCallback> iterator = this.getCallbacks().iterator();
        while (iterator.hasNext()) {
            iterator.next().onRequestFailed(n);
        }
    }
    
    protected void registerCallback(final MediaDeviceCallback mediaDeviceCallback) {
        if (!this.mCallbacks.contains(mediaDeviceCallback)) {
            this.mCallbacks.add(mediaDeviceCallback);
        }
    }
    
    protected void unregisterCallback(final MediaDeviceCallback mediaDeviceCallback) {
        if (this.mCallbacks.contains(mediaDeviceCallback)) {
            this.mCallbacks.remove(mediaDeviceCallback);
        }
    }
    
    public interface MediaDeviceCallback
    {
        void onConnectedDeviceChanged(final String p0);
        
        void onDeviceListAdded(final List<MediaDevice> p0);
        
        void onRequestFailed(final int p0);
    }
}
