// 
// Decompiled by Procyon v0.5.36
// 

package androidx.mediarouter.app;

public class MediaRouteDialogFactory
{
    private static final MediaRouteDialogFactory sDefault;
    
    static {
        sDefault = new MediaRouteDialogFactory();
    }
    
    public static MediaRouteDialogFactory getDefault() {
        return MediaRouteDialogFactory.sDefault;
    }
    
    public MediaRouteChooserDialogFragment onCreateChooserDialogFragment() {
        return new MediaRouteChooserDialogFragment();
    }
    
    public MediaRouteControllerDialogFragment onCreateControllerDialogFragment() {
        return new MediaRouteControllerDialogFragment();
    }
}
