// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice;

import androidx.slice.core.SliceActionImpl;
import java.util.ArrayList;
import androidx.slice.core.SliceQuery;
import androidx.slice.core.SliceAction;
import java.util.List;
import androidx.slice.widget.ListContent;
import androidx.slice.widget.RowContent;
import android.content.Context;

public class SliceMetadata
{
    private Context mContext;
    private long mExpiry;
    private RowContent mHeaderContent;
    private long mLastUpdated;
    private ListContent mListContent;
    private Slice mSlice;
    private List<SliceAction> mSliceActions;
    
    private SliceMetadata(final Context mContext, final Slice mSlice) {
        this.mSlice = mSlice;
        this.mContext = mContext;
        final SliceItem find = SliceQuery.find(mSlice, "long", "ttl", null);
        if (find != null) {
            this.mExpiry = find.getLong();
        }
        final SliceItem find2 = SliceQuery.find(mSlice, "long", "last_updated", null);
        if (find2 != null) {
            this.mLastUpdated = find2.getLong();
        }
        final ListContent mListContent = new ListContent(mSlice);
        this.mListContent = mListContent;
        this.mHeaderContent = mListContent.getHeader();
        this.mListContent.getHeaderTemplateType();
        this.mListContent.getShortcut(this.mContext);
        if ((this.mSliceActions = this.mListContent.getSliceActions()) == null) {
            final RowContent mHeaderContent = this.mHeaderContent;
            if (mHeaderContent != null && SliceQuery.hasHints(mHeaderContent.getSliceItem(), "list_item")) {
                final ArrayList<SliceItem> endItems = this.mHeaderContent.getEndItems();
                final ArrayList<SliceActionImpl> mSliceActions = new ArrayList<SliceActionImpl>();
                for (int i = 0; i < endItems.size(); ++i) {
                    if (SliceQuery.find((SliceItem)endItems.get(i), "action") != null) {
                        mSliceActions.add(new SliceActionImpl((SliceItem)endItems.get(i)));
                    }
                }
                if (mSliceActions.size() > 0) {
                    this.mSliceActions = (List<SliceAction>)mSliceActions;
                }
            }
        }
    }
    
    public static SliceMetadata from(final Context context, final Slice slice) {
        return new SliceMetadata(context, slice);
    }
    
    public static List<SliceAction> getSliceActions(final Slice slice) {
        final SliceItem find = SliceQuery.find(slice, "slice", "actions", null);
        List<SliceItem> all;
        if (find != null) {
            all = SliceQuery.findAll(find, "slice", new String[] { "actions", "shortcut" }, null);
        }
        else {
            all = null;
        }
        if (all != null) {
            final ArrayList list = new ArrayList<SliceActionImpl>(all.size());
            for (int i = 0; i < all.size(); ++i) {
                list.add(new SliceActionImpl(all.get(i)));
            }
            return (List<SliceAction>)list;
        }
        return null;
    }
    
    public long getLastUpdatedTime() {
        return this.mLastUpdated;
    }
    
    public ListContent getListContent() {
        return this.mListContent;
    }
    
    public int getLoadingState() {
        final boolean b = SliceQuery.find(this.mSlice, null, "partial", null) != null;
        if (!this.mListContent.isValid()) {
            return 0;
        }
        if (b) {
            return 1;
        }
        return 2;
    }
    
    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }
    
    public long getTimeToExpiry() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long mExpiry = this.mExpiry;
        long n2;
        final long n = n2 = 0L;
        if (mExpiry != 0L) {
            n2 = n;
            if (mExpiry != -1L) {
                if (currentTimeMillis > mExpiry) {
                    n2 = n;
                }
                else {
                    n2 = mExpiry - currentTimeMillis;
                }
            }
        }
        return n2;
    }
    
    public boolean isExpired() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long mExpiry = this.mExpiry;
        return mExpiry != 0L && mExpiry != -1L && currentTimeMillis > mExpiry;
    }
    
    public boolean isPermissionSlice() {
        return this.mSlice.hasHint("permission_request");
    }
    
    public boolean neverExpires() {
        return this.mExpiry == -1L;
    }
}
