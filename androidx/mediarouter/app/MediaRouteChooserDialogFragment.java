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

public class MediaRouteChooserDialogFragment extends DialogFragment
{
    private Dialog mDialog;
    private MediaRouteSelector mSelector;
    private boolean mUseDynamicGroup;
    
    public MediaRouteChooserDialogFragment() {
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
    
    public MediaRouteSelector getRouteSelector() {
        this.ensureRouteSelector();
        return this.mSelector;
    }
    
    @Override
    public void onConfigurationChanged(final Configuration configuration) {
        super.onConfigurationChanged(configuration);
        final Dialog mDialog = this.mDialog;
        if (mDialog == null) {
            return;
        }
        if (this.mUseDynamicGroup) {
            ((MediaRouteDynamicChooserDialog)mDialog).updateLayout();
        }
        else {
            ((MediaRouteChooserDialog)mDialog).updateLayout();
        }
    }
    
    public MediaRouteChooserDialog onCreateChooserDialog(final Context context, final Bundle bundle) {
        return new MediaRouteChooserDialog(context);
    }
    
    @Override
    public Dialog onCreateDialog(final Bundle bundle) {
        if (this.mUseDynamicGroup) {
            final MediaRouteDynamicChooserDialog onCreateDynamicChooserDialog = this.onCreateDynamicChooserDialog(this.getContext());
            this.mDialog = onCreateDynamicChooserDialog;
            onCreateDynamicChooserDialog.setRouteSelector(this.getRouteSelector());
        }
        else {
            final MediaRouteChooserDialog onCreateChooserDialog = this.onCreateChooserDialog(this.getContext(), bundle);
            this.mDialog = onCreateChooserDialog;
            onCreateChooserDialog.setRouteSelector(this.getRouteSelector());
        }
        return this.mDialog;
    }
    
    public MediaRouteDynamicChooserDialog onCreateDynamicChooserDialog(final Context context) {
        return new MediaRouteDynamicChooserDialog(context);
    }
    
    public void setRouteSelector(final MediaRouteSelector routeSelector) {
        if (routeSelector != null) {
            this.ensureRouteSelector();
            if (!this.mSelector.equals(routeSelector)) {
                this.mSelector = routeSelector;
                Bundle arguments;
                if ((arguments = this.getArguments()) == null) {
                    arguments = new Bundle();
                }
                arguments.putBundle("selector", routeSelector.asBundle());
                this.setArguments(arguments);
                final Dialog mDialog = this.mDialog;
                if (mDialog != null) {
                    if (this.mUseDynamicGroup) {
                        ((MediaRouteDynamicChooserDialog)mDialog).setRouteSelector(routeSelector);
                    }
                    else {
                        ((MediaRouteChooserDialog)mDialog).setRouteSelector(routeSelector);
                    }
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
