// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view;

import android.view.ViewConfiguration;
import android.os.Build$VERSION;
import androidx.appcompat.R$bool;
import android.content.res.Configuration;
import android.content.Context;

public class ActionBarPolicy
{
    private Context mContext;
    
    private ActionBarPolicy(final Context mContext) {
        this.mContext = mContext;
    }
    
    public static ActionBarPolicy get(final Context context) {
        return new ActionBarPolicy(context);
    }
    
    public boolean enableHomeButtonByDefault() {
        return this.mContext.getApplicationInfo().targetSdkVersion < 14;
    }
    
    public int getEmbeddedMenuWidthLimit() {
        return this.mContext.getResources().getDisplayMetrics().widthPixels / 2;
    }
    
    public int getMaxActionButtons() {
        final Configuration configuration = this.mContext.getResources().getConfiguration();
        final int screenWidthDp = configuration.screenWidthDp;
        final int screenHeightDp = configuration.screenHeightDp;
        if (configuration.smallestScreenWidthDp > 600 || screenWidthDp > 600 || (screenWidthDp > 960 && screenHeightDp > 720) || (screenWidthDp > 720 && screenHeightDp > 960)) {
            return 5;
        }
        if (screenWidthDp >= 500 || (screenWidthDp > 640 && screenHeightDp > 480) || (screenWidthDp > 480 && screenHeightDp > 640)) {
            return 4;
        }
        if (screenWidthDp >= 360) {
            return 3;
        }
        return 2;
    }
    
    public boolean hasEmbeddedTabs() {
        return this.mContext.getResources().getBoolean(R$bool.abc_action_bar_embed_tabs);
    }
    
    public boolean showsOverflowMenuButton() {
        return Build$VERSION.SDK_INT >= 19 || (ViewConfiguration.get(this.mContext).hasPermanentMenuKey() ^ true);
    }
}
