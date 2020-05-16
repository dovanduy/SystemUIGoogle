// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import androidx.core.util.Pools$SimplePool;
import androidx.core.util.Pools$Pool;
import androidx.collection.LongSparseArray;
import androidx.collection.SimpleArrayMap;

class ViewInfoStore
{
    final SimpleArrayMap<RecyclerView.ViewHolder, InfoRecord> mLayoutHolderMap;
    final LongSparseArray<RecyclerView.ViewHolder> mOldChangedHolders;
    
    ViewInfoStore() {
        this.mLayoutHolderMap = new SimpleArrayMap<RecyclerView.ViewHolder, InfoRecord>();
        this.mOldChangedHolders = new LongSparseArray<RecyclerView.ViewHolder>();
    }
    
    private RecyclerView.ItemAnimator.ItemHolderInfo popFromLayoutStep(final RecyclerView.ViewHolder viewHolder, final int n) {
        final int indexOfKey = this.mLayoutHolderMap.indexOfKey(viewHolder);
        if (indexOfKey < 0) {
            return null;
        }
        final InfoRecord infoRecord = this.mLayoutHolderMap.valueAt(indexOfKey);
        if (infoRecord != null) {
            final int flags = infoRecord.flags;
            if ((flags & n) != 0x0) {
                infoRecord.flags = (n & flags);
                RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo;
                if (n == 4) {
                    itemHolderInfo = infoRecord.preInfo;
                }
                else {
                    if (n != 8) {
                        throw new IllegalArgumentException("Must provide flag PRE or POST");
                    }
                    itemHolderInfo = infoRecord.postInfo;
                }
                if ((infoRecord.flags & 0xC) == 0x0) {
                    this.mLayoutHolderMap.removeAt(indexOfKey);
                    InfoRecord.recycle(infoRecord);
                }
                return itemHolderInfo;
            }
        }
        return null;
    }
    
    void addToAppearedInPreLayoutHolders(final RecyclerView.ViewHolder viewHolder, final RecyclerView.ItemAnimator.ItemHolderInfo preInfo) {
        InfoRecord obtain;
        if ((obtain = this.mLayoutHolderMap.get(viewHolder)) == null) {
            obtain = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, obtain);
        }
        obtain.flags |= 0x2;
        obtain.preInfo = preInfo;
    }
    
    void addToDisappearedInLayout(final RecyclerView.ViewHolder viewHolder) {
        InfoRecord obtain;
        if ((obtain = this.mLayoutHolderMap.get(viewHolder)) == null) {
            obtain = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, obtain);
        }
        obtain.flags |= 0x1;
    }
    
    void addToOldChangeHolders(final long n, final RecyclerView.ViewHolder viewHolder) {
        this.mOldChangedHolders.put(n, viewHolder);
    }
    
    void addToPostLayout(final RecyclerView.ViewHolder viewHolder, final RecyclerView.ItemAnimator.ItemHolderInfo postInfo) {
        InfoRecord obtain;
        if ((obtain = this.mLayoutHolderMap.get(viewHolder)) == null) {
            obtain = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, obtain);
        }
        obtain.postInfo = postInfo;
        obtain.flags |= 0x8;
    }
    
    void addToPreLayout(final RecyclerView.ViewHolder viewHolder, final RecyclerView.ItemAnimator.ItemHolderInfo preInfo) {
        InfoRecord obtain;
        if ((obtain = this.mLayoutHolderMap.get(viewHolder)) == null) {
            obtain = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, obtain);
        }
        obtain.preInfo = preInfo;
        obtain.flags |= 0x4;
    }
    
    void clear() {
        this.mLayoutHolderMap.clear();
        this.mOldChangedHolders.clear();
    }
    
    RecyclerView.ViewHolder getFromOldChangeHolders(final long n) {
        return this.mOldChangedHolders.get(n);
    }
    
    boolean isDisappearing(final RecyclerView.ViewHolder viewHolder) {
        final InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        boolean b = true;
        if (infoRecord == null || (infoRecord.flags & 0x1) == 0x0) {
            b = false;
        }
        return b;
    }
    
    boolean isInPreLayout(final RecyclerView.ViewHolder viewHolder) {
        final InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        return infoRecord != null && (infoRecord.flags & 0x4) != 0x0;
    }
    
    void onDetach() {
        InfoRecord.drainCache();
    }
    
    public void onViewDetached(final RecyclerView.ViewHolder viewHolder) {
        this.removeFromDisappearedInLayout(viewHolder);
    }
    
    RecyclerView.ItemAnimator.ItemHolderInfo popFromPostLayout(final RecyclerView.ViewHolder viewHolder) {
        return this.popFromLayoutStep(viewHolder, 8);
    }
    
    RecyclerView.ItemAnimator.ItemHolderInfo popFromPreLayout(final RecyclerView.ViewHolder viewHolder) {
        return this.popFromLayoutStep(viewHolder, 4);
    }
    
    void process(final ProcessCallback processCallback) {
        for (int i = this.mLayoutHolderMap.size() - 1; i >= 0; --i) {
            final RecyclerView.ViewHolder viewHolder = this.mLayoutHolderMap.keyAt(i);
            final InfoRecord infoRecord = this.mLayoutHolderMap.removeAt(i);
            final int flags = infoRecord.flags;
            if ((flags & 0x3) == 0x3) {
                processCallback.unused(viewHolder);
            }
            else if ((flags & 0x1) != 0x0) {
                final RecyclerView.ItemAnimator.ItemHolderInfo preInfo = infoRecord.preInfo;
                if (preInfo == null) {
                    processCallback.unused(viewHolder);
                }
                else {
                    processCallback.processDisappeared(viewHolder, preInfo, infoRecord.postInfo);
                }
            }
            else if ((flags & 0xE) == 0xE) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            }
            else if ((flags & 0xC) == 0xC) {
                processCallback.processPersistent(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            }
            else if ((flags & 0x4) != 0x0) {
                processCallback.processDisappeared(viewHolder, infoRecord.preInfo, null);
            }
            else if ((flags & 0x8) != 0x0) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            }
            InfoRecord.recycle(infoRecord);
        }
    }
    
    void removeFromDisappearedInLayout(final RecyclerView.ViewHolder viewHolder) {
        final InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            return;
        }
        infoRecord.flags &= 0xFFFFFFFE;
    }
    
    void removeViewHolder(final RecyclerView.ViewHolder viewHolder) {
        for (int i = this.mOldChangedHolders.size() - 1; i >= 0; --i) {
            if (viewHolder == this.mOldChangedHolders.valueAt(i)) {
                this.mOldChangedHolders.removeAt(i);
                break;
            }
        }
        final InfoRecord infoRecord = this.mLayoutHolderMap.remove(viewHolder);
        if (infoRecord != null) {
            InfoRecord.recycle(infoRecord);
        }
    }
    
    static class InfoRecord
    {
        static Pools$Pool<InfoRecord> sPool;
        int flags;
        RecyclerView.ItemAnimator.ItemHolderInfo postInfo;
        RecyclerView.ItemAnimator.ItemHolderInfo preInfo;
        
        static {
            InfoRecord.sPool = new Pools$SimplePool<InfoRecord>(20);
        }
        
        private InfoRecord() {
        }
        
        static void drainCache() {
            while (InfoRecord.sPool.acquire() != null) {}
        }
        
        static InfoRecord obtain() {
            InfoRecord infoRecord;
            if ((infoRecord = InfoRecord.sPool.acquire()) == null) {
                infoRecord = new InfoRecord();
            }
            return infoRecord;
        }
        
        static void recycle(final InfoRecord infoRecord) {
            infoRecord.flags = 0;
            infoRecord.preInfo = null;
            infoRecord.postInfo = null;
            InfoRecord.sPool.release(infoRecord);
        }
    }
    
    interface ProcessCallback
    {
        void processAppeared(final RecyclerView.ViewHolder p0, final RecyclerView.ItemAnimator.ItemHolderInfo p1, final RecyclerView.ItemAnimator.ItemHolderInfo p2);
        
        void processDisappeared(final RecyclerView.ViewHolder p0, final RecyclerView.ItemAnimator.ItemHolderInfo p1, final RecyclerView.ItemAnimator.ItemHolderInfo p2);
        
        void processPersistent(final RecyclerView.ViewHolder p0, final RecyclerView.ItemAnimator.ItemHolderInfo p1, final RecyclerView.ItemAnimator.ItemHolderInfo p2);
        
        void unused(final RecyclerView.ViewHolder p0);
    }
}
