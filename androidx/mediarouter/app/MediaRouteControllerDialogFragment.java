// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.mediarouter.media.MediaRouteSelector;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;

public class MediaRouteControllerDialogFragment extends DialogFragment
{
    private Dialog mDialog;
    private MediaRouteSelector mSelector;
    private boolean mUseDynamicGroup;
    
    public MediaRouteControllerDialogFragment() {
        this.mUseDynamicGroup = false;
        this.setCancelable(true);
    }
    
    private void ensureRouteSelector() {
        if (this.mSelector == null) {
            final Bundle arguments = this.getArguments();
            if (arguments != null) {
                this.mSelector = MediaRouteSelector.fromBundle(arguments.getBundle("selector"));
            }
            if (this.mSelector == null) {
                this.mSelector = MediaRouteSelector.EMPTY;
            }
        }
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final Dialog mDialog = this.mDialog;
        if (mDialog != null) {
            if (this.mUseDynamicGroup) {
                ((MediaRouteDynamicControllerDialog)mDialog).updateLayout();
            }
            else {
                ((MediaRouteControllerDialog)mDialog).updateLayout();
            }
        }
    }
    
    public MediaRouteControllerDialog onCreateControllerDialog(final Context context, final Bundle bundle) {
        return new MediaRouteControllerDialog(context);
    }
    
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        if (this.mUseDynamicGroup) {
            final MediaRouteDynamicControllerDialog onCreateDynamicControllerDialog = this.onCreateDynamicControllerDialog(this.getContext());
            this.mDialog = onCreateDynamicControllerDialog;
            onCreateDynamicControllerDialog.setRouteSelector(this.mSelector);
        }
        else {
            this.mDialog = this.onCreateControllerDialog(this.getContext(), bundle);
        }
        return this.mDialog;
    }
    
    public MediaRouteDynamicControllerDialog onCreateDynamicControllerDialog(final Context context) {
        return new MediaRouteDynamicControllerDialog(context);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        final Dialog mDialog = this.mDialog;
        if (mDialog != null && !this.mUseDynamicGroup) {
            ((MediaRouteControllerDialog)mDialog).clearGroupListAnimation(false);
        }
    }
    
    public void setRouteSelector(final MediaRouteSelector mediaRouteSelector) {
        if (mediaRouteSelector != null) {
            this.ensureRouteSelector();
            if (!this.mSelector.equals(mediaRouteSelector)) {
                this.mSelector = mediaRouteSelector;
                Bundle arguments;
                if ((arguments = this.getArguments()) == null) {
                    arguments = new Bundle();
                }
                arguments.putBundle("selector", mediaRouteSelector.asBundle());
                this.setArguments(arguments);
                final Dialog mDialog = this.mDialog;
                if (mDialog != null && this.mUseDynamicGroup) {
                    ((MediaRouteDynamicControllerDialog)mDialog).setRouteSelector(mediaRouteSelector);
                }
            }
            return;
        }
        throw new IllegalArgumentException("selector must not be null");
    }
    
    void setUseDynamicGroup(final boolean mUseDynamicGroup) {
        if (this.mDialog == null) {
            this.mUseDynamicGroup = mUseDynamicGroup;
            return;
        }
        throw new IllegalStateException("This must be called before creating dialog");
    }
}
