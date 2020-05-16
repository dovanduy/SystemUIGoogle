// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import androidx.slice.SliceUtils;
import android.content.pm.ApplicationInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager;
import androidx.core.graphics.drawable.IconCompat;
import android.net.Uri;
import androidx.slice.core.SliceActionImpl;
import android.content.Intent;
import android.app.PendingIntent;
import androidx.slice.core.SliceQuery;
import androidx.slice.core.SliceAction;
import android.content.Context;
import androidx.slice.Slice;
import androidx.slice.SliceItem;

public class SliceContent
{
    protected SliceItem mContentDescr;
    protected SliceItem mLayoutDirItem;
    protected int mRowIndex;
    protected SliceItem mSliceItem;
    
    public SliceContent(final Slice slice) {
        if (slice == null) {
            return;
        }
        this.init(new SliceItem(slice, "slice", null, slice.getHints()));
        this.mRowIndex = -1;
    }
    
    public SliceContent(final SliceItem sliceItem, final int mRowIndex) {
        if (sliceItem == null) {
            return;
        }
        this.init(sliceItem);
        this.mRowIndex = mRowIndex;
    }
    
    private SliceAction fallBackToAppData(final Context context, final SliceItem sliceItem, final SliceItem sliceItem2, int n, final SliceItem sliceItem3) {
        final SliceItem find = SliceQuery.find(this.mSliceItem, "slice", null, (String)null);
        if (find == null) {
            return null;
        }
        final Uri uri = find.getSlice().getUri();
        IconCompat icon;
        if (sliceItem2 != null) {
            icon = sliceItem2.getIcon();
        }
        else {
            icon = null;
        }
        CharSequence text;
        if (sliceItem != null) {
            text = sliceItem.getText();
        }
        else {
            text = null;
        }
        CharSequence charSequence = text;
        IconCompat iconCompat = icon;
        int n2 = n;
        SliceItem sliceItem4 = sliceItem3;
        if (context != null) {
            final PackageManager packageManager = context.getPackageManager();
            final ProviderInfo resolveContentProvider = packageManager.resolveContentProvider(uri.getAuthority(), 0);
            ApplicationInfo applicationInfo;
            if (resolveContentProvider != null) {
                applicationInfo = resolveContentProvider.applicationInfo;
            }
            else {
                applicationInfo = null;
            }
            charSequence = text;
            iconCompat = icon;
            n2 = n;
            sliceItem4 = sliceItem3;
            if (applicationInfo != null) {
                IconCompat iconFromDrawable;
                if ((iconFromDrawable = icon) == null) {
                    iconFromDrawable = SliceViewUtil.createIconFromDrawable(packageManager.getApplicationIcon(applicationInfo));
                    n = 2;
                }
                CharSequence applicationLabel;
                if ((applicationLabel = text) == null) {
                    applicationLabel = packageManager.getApplicationLabel(applicationInfo);
                }
                charSequence = applicationLabel;
                iconCompat = iconFromDrawable;
                n2 = n;
                if ((sliceItem4 = sliceItem3) == null) {
                    final Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(applicationInfo.packageName);
                    charSequence = applicationLabel;
                    iconCompat = iconFromDrawable;
                    n2 = n;
                    sliceItem4 = sliceItem3;
                    if (launchIntentForPackage != null) {
                        sliceItem4 = new SliceItem(PendingIntent.getActivity(context, 0, launchIntentForPackage, 0), new Slice.Builder(uri).build(), "action", null, new String[0]);
                        n2 = n;
                        iconCompat = iconFromDrawable;
                        charSequence = applicationLabel;
                    }
                }
            }
        }
        SliceItem sliceItem5;
        if ((sliceItem5 = sliceItem4) == null) {
            sliceItem5 = new SliceItem(PendingIntent.getActivity(context, 0, new Intent(), 0), null, "action", null, null);
        }
        if (charSequence != null && iconCompat != null && sliceItem5 != null) {
            return new SliceActionImpl(sliceItem5.getAction(), iconCompat, n2, charSequence);
        }
        return null;
    }
    
    private void init(final SliceItem mSliceItem) {
        this.mSliceItem = mSliceItem;
        if ("slice".equals(mSliceItem.getFormat()) || "action".equals(mSliceItem.getFormat())) {
            SliceQuery.findTopLevelItem(mSliceItem.getSlice(), "int", "color", null, null);
            this.mLayoutDirItem = SliceQuery.findTopLevelItem(mSliceItem.getSlice(), "int", "layout_direction", null, null);
        }
        this.mContentDescr = SliceQuery.findSubtype(mSliceItem, "text", "content_description");
    }
    
    public CharSequence getContentDescription() {
        final SliceItem mContentDescr = this.mContentDescr;
        CharSequence text;
        if (mContentDescr != null) {
            text = mContentDescr.getText();
        }
        else {
            text = null;
        }
        return text;
    }
    
    public int getHeight(final SliceStyle sliceStyle, final SliceViewPolicy sliceViewPolicy) {
        return 0;
    }
    
    public int getLayoutDir() {
        final SliceItem mLayoutDirItem = this.mLayoutDirItem;
        int resolveLayoutDirection;
        if (mLayoutDirItem != null) {
            resolveLayoutDirection = SliceViewUtil.resolveLayoutDirection(mLayoutDirItem.getInt());
        }
        else {
            resolveLayoutDirection = -1;
        }
        return resolveLayoutDirection;
    }
    
    public int getRowIndex() {
        return this.mRowIndex;
    }
    
    public SliceAction getShortcut(final Context context) {
        final SliceItem mSliceItem = this.mSliceItem;
        if (mSliceItem == null) {
            return null;
        }
        final SliceItem find = SliceQuery.find(mSliceItem, "action", new String[] { "title", "shortcut" }, null);
        SliceItem find2;
        SliceItem find3;
        if (find != null) {
            find2 = SliceQuery.find(find, "image", "title", null);
            find3 = SliceQuery.find(find, "text", null, (String)null);
        }
        else {
            find2 = (find3 = null);
        }
        SliceItem find4 = find;
        if (find == null) {
            find4 = SliceQuery.find(this.mSliceItem, "action", null, (String)null);
        }
        SliceItem find5;
        if ((find5 = find2) == null) {
            find5 = SliceQuery.find(this.mSliceItem, "image", "title", null);
        }
        SliceItem find6;
        if ((find6 = find3) == null) {
            find6 = SliceQuery.find(this.mSliceItem, "text", "title", null);
        }
        SliceItem find7;
        if ((find7 = find5) == null) {
            find7 = SliceQuery.find(this.mSliceItem, "image", null, (String)null);
        }
        SliceItem find8;
        if ((find8 = find6) == null) {
            find8 = SliceQuery.find(this.mSliceItem, "text", null, (String)null);
        }
        int imageMode;
        if (find7 != null) {
            imageMode = SliceUtils.parseImageMode(find7);
        }
        else {
            imageMode = 5;
        }
        if (context != null) {
            return this.fallBackToAppData(context, find8, find7, imageMode, find4);
        }
        if (find7 != null && find4 != null && find8 != null) {
            return new SliceActionImpl(find4.getAction(), find7.getIcon(), imageMode, find8.getText());
        }
        return null;
    }
    
    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }
    
    public boolean isValid() {
        return this.mSliceItem != null;
    }
}
