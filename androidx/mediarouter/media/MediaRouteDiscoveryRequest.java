// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.media;

import android.os.Bundle;

public final class MediaRouteDiscoveryRequest
{
    private final Bundle mBundle;
    private MediaRouteSelector mSelector;
    
    public MediaRouteDiscoveryRequest(final MediaRouteSelector mSelector, final boolean b) {
        if (mSelector != null) {
            final Bundle mBundle = new Bundle();
            this.mBundle = mBundle;
            this.mSelector = mSelector;
            mBundle.putBundle("selector", mSelector.asBundle());
            this.mBundle.putBoolean("activeScan", b);
            return;
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    private void ensureSelector() {
        if (this.mSelector == null && (this.mSelector = MediaRouteSelector.fromBundle(this.mBundle.getBundle("selector"))) == null) {
            this.mSelector = MediaRouteSelector.EMPTY;
        }
    }
    
    public Bundle asBundle() {
        return this.mBundle;
    }
    
    @Override
    public boolean equals(final Object o) {
        final boolean b = o instanceof MediaRouteDiscoveryRequest;
        boolean b3;
        final boolean b2 = b3 = false;
        if (b) {
            final MediaRouteDiscoveryRequest mediaRouteDiscoveryRequest = (MediaRouteDiscoveryRequest)o;
            b3 = b2;
            if (this.getSelector().equals(mediaRouteDiscoveryRequest.getSelector())) {
                b3 = b2;
                if (this.isActiveScan() == mediaRouteDiscoveryRequest.isActiveScan()) {
                    b3 = true;
                }
            }
        }
        return b3;
    }
    
    public MediaRouteSelector getSelector() {
        this.ensureSelector();
        return this.mSelector;
    }
    
    @Override
    public int hashCode() {
        return (this.isActiveScan() ? 1 : 0) ^ this.getSelector().hashCode();
    }
    
    public boolean isActiveScan() {
        return this.mBundle.getBoolean("activeScan");
    }
    
    public boolean isValid() {
        this.ensureSelector();
        return this.mSelector.isValid();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("DiscoveryRequest{ selector=");
        sb.append(this.getSelector());
        sb.append(", activeScan=");
        sb.append(this.isActiveScan());
        sb.append(", isValid=");
        sb.append(this.isValid());
        sb.append(" }");
        return sb.toString();
    }
}
