// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import java.util.List;
import androidx.core.util.Pools$SimplePool;
import androidx.core.util.Pools$Pool;
import java.util.ArrayList;

final class AdapterHelper implements OpReorderer.Callback
{
    final Callback mCallback;
    final boolean mDisableRecycler;
    private int mExistingUpdateTypes;
    Runnable mOnItemProcessedCallback;
    final OpReorderer mOpReorderer;
    final ArrayList<UpdateOp> mPendingUpdates;
    final ArrayList<UpdateOp> mPostponedList;
    private Pools$Pool<UpdateOp> mUpdateOpPool;
    
    AdapterHelper(final Callback callback) {
        this(callback, false);
    }
    
    AdapterHelper(final Callback mCallback, final boolean mDisableRecycler) {
        this.mUpdateOpPool = new Pools$SimplePool<UpdateOp>(30);
        this.mPendingUpdates = new ArrayList<UpdateOp>();
        this.mPostponedList = new ArrayList<UpdateOp>();
        this.mExistingUpdateTypes = 0;
        this.mCallback = mCallback;
        this.mDisableRecycler = mDisableRecycler;
        this.mOpReorderer = new OpReorderer((OpReorderer.Callback)this);
    }
    
    private void applyAdd(final UpdateOp updateOp) {
        this.postponeAndUpdateViewHolders(updateOp);
    }
    
    private void applyMove(final UpdateOp updateOp) {
        this.postponeAndUpdateViewHolders(updateOp);
    }
    
    private void applyRemove(final UpdateOp updateOp) {
        final int positionStart = updateOp.positionStart;
        int n = updateOp.itemCount + positionStart;
        int n2 = -1;
        int i = positionStart;
        int n3 = 0;
        while (i < n) {
            boolean b2;
            if (this.mCallback.findViewHolder(i) == null && !this.canFindInPreLayout(i)) {
                boolean b;
                if (n2 == 1) {
                    this.postponeAndUpdateViewHolders(this.obtainUpdateOp(2, positionStart, n3, null));
                    b = true;
                }
                else {
                    b = false;
                }
                final int n4 = 0;
                b2 = b;
                n2 = n4;
            }
            else {
                if (n2 == 0) {
                    this.dispatchAndUpdateViewHolders(this.obtainUpdateOp(2, positionStart, n3, null));
                    b2 = true;
                }
                else {
                    b2 = false;
                }
                n2 = 1;
            }
            int n5;
            if (b2) {
                i -= n3;
                n -= n3;
                n5 = 1;
            }
            else {
                n5 = n3 + 1;
            }
            ++i;
            n3 = n5;
        }
        UpdateOp obtainUpdateOp = updateOp;
        if (n3 != updateOp.itemCount) {
            this.recycleUpdateOp(updateOp);
            obtainUpdateOp = this.obtainUpdateOp(2, positionStart, n3, null);
        }
        if (n2 == 0) {
            this.dispatchAndUpdateViewHolders(obtainUpdateOp);
        }
        else {
            this.postponeAndUpdateViewHolders(obtainUpdateOp);
        }
    }
    
    private void applyUpdate(final UpdateOp updateOp) {
        final int positionStart = updateOp.positionStart;
        final int itemCount = updateOp.itemCount;
        int n = 0;
        int n2 = -1;
        int n3 = positionStart;
        int n6;
        int n9;
        for (int i = positionStart; i < itemCount + positionStart; ++i, n2 = n6, n = n9) {
            int n7;
            if (this.mCallback.findViewHolder(i) == null && !this.canFindInPreLayout(i)) {
                int n4 = n3;
                int n5 = n;
                if (n2 == 1) {
                    this.postponeAndUpdateViewHolders(this.obtainUpdateOp(4, n3, n, updateOp.payload));
                    n4 = i;
                    n5 = 0;
                }
                n6 = 0;
                n3 = n4;
                n7 = n5;
            }
            else {
                int n8 = n3;
                n7 = n;
                if (n2 == 0) {
                    this.dispatchAndUpdateViewHolders(this.obtainUpdateOp(4, n3, n, updateOp.payload));
                    n8 = i;
                    n7 = 0;
                }
                n6 = 1;
                n3 = n8;
            }
            n9 = n7 + 1;
        }
        UpdateOp obtainUpdateOp = updateOp;
        if (n != updateOp.itemCount) {
            final Object payload = updateOp.payload;
            this.recycleUpdateOp(updateOp);
            obtainUpdateOp = this.obtainUpdateOp(4, n3, n, payload);
        }
        if (n2 == 0) {
            this.dispatchAndUpdateViewHolders(obtainUpdateOp);
        }
        else {
            this.postponeAndUpdateViewHolders(obtainUpdateOp);
        }
    }
    
    private boolean canFindInPreLayout(final int n) {
        for (int size = this.mPostponedList.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPostponedList.get(i);
            final int cmd = updateOp.cmd;
            if (cmd == 8) {
                if (this.findPositionOffset(updateOp.itemCount, i + 1) == n) {
                    return true;
                }
            }
            else if (cmd == 1) {
                for (int positionStart = updateOp.positionStart, itemCount = updateOp.itemCount, j = positionStart; j < itemCount + positionStart; ++j) {
                    if (this.findPositionOffset(j, i + 1) == n) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private void dispatchAndUpdateViewHolders(UpdateOp obtainUpdateOp) {
        final int cmd = obtainUpdateOp.cmd;
        if (cmd != 1 && cmd != 8) {
            int updatePositionWithPostponed = this.updatePositionWithPostponed(obtainUpdateOp.positionStart, cmd);
            int positionStart = obtainUpdateOp.positionStart;
            final int cmd2 = obtainUpdateOp.cmd;
            int n;
            if (cmd2 != 2) {
                if (cmd2 != 4) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("op should be remove or update.");
                    sb.append(obtainUpdateOp);
                    throw new IllegalArgumentException(sb.toString());
                }
                n = 1;
            }
            else {
                n = 0;
            }
            int n2;
            for (int i = n2 = 1; i < obtainUpdateOp.itemCount; ++i) {
                final int updatePositionWithPostponed2 = this.updatePositionWithPostponed(obtainUpdateOp.positionStart + n * i, obtainUpdateOp.cmd);
                final int cmd3 = obtainUpdateOp.cmd;
                if ((cmd3 == 2) ? (updatePositionWithPostponed2 == updatePositionWithPostponed) : (cmd3 == 4 && updatePositionWithPostponed2 == updatePositionWithPostponed + 1)) {
                    ++n2;
                }
                else {
                    final UpdateOp obtainUpdateOp2 = this.obtainUpdateOp(obtainUpdateOp.cmd, updatePositionWithPostponed, n2, obtainUpdateOp.payload);
                    this.dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp2, positionStart);
                    this.recycleUpdateOp(obtainUpdateOp2);
                    int n3 = positionStart;
                    if (obtainUpdateOp.cmd == 4) {
                        n3 = positionStart + n2;
                    }
                    n2 = 1;
                    final int n4 = updatePositionWithPostponed2;
                    positionStart = n3;
                    updatePositionWithPostponed = n4;
                }
            }
            final Object payload = obtainUpdateOp.payload;
            this.recycleUpdateOp(obtainUpdateOp);
            if (n2 > 0) {
                obtainUpdateOp = this.obtainUpdateOp(obtainUpdateOp.cmd, updatePositionWithPostponed, n2, payload);
                this.dispatchFirstPassAndUpdateViewHolders(obtainUpdateOp, positionStart);
                this.recycleUpdateOp(obtainUpdateOp);
            }
            return;
        }
        throw new IllegalArgumentException("should not dispatch add or move for pre layout");
    }
    
    private void postponeAndUpdateViewHolders(final UpdateOp updateOp) {
        this.mPostponedList.add(updateOp);
        final int cmd = updateOp.cmd;
        if (cmd != 1) {
            if (cmd != 2) {
                if (cmd != 4) {
                    if (cmd != 8) {
                        final StringBuilder sb = new StringBuilder();
                        sb.append("Unknown update op type for ");
                        sb.append(updateOp);
                        throw new IllegalArgumentException(sb.toString());
                    }
                    this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
                }
                else {
                    this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                }
            }
            else {
                this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(updateOp.positionStart, updateOp.itemCount);
            }
        }
        else {
            this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
        }
    }
    
    private int updatePositionWithPostponed(int i, int itemCount) {
        int j = this.mPostponedList.size() - 1;
        int n = i;
        while (j >= 0) {
            final UpdateOp updateOp = this.mPostponedList.get(j);
            final int cmd = updateOp.cmd;
            if (cmd == 8) {
                int positionStart = updateOp.positionStart;
                i = updateOp.itemCount;
                if (positionStart >= i) {
                    final int n2 = i;
                    i = positionStart;
                    positionStart = n2;
                }
                if (n >= positionStart && n <= i) {
                    i = updateOp.positionStart;
                    if (positionStart == i) {
                        if (itemCount == 1) {
                            ++updateOp.itemCount;
                        }
                        else if (itemCount == 2) {
                            --updateOp.itemCount;
                        }
                        i = n + 1;
                    }
                    else {
                        if (itemCount == 1) {
                            updateOp.positionStart = i + 1;
                        }
                        else if (itemCount == 2) {
                            updateOp.positionStart = i - 1;
                        }
                        i = n - 1;
                    }
                }
                else {
                    final int positionStart2 = updateOp.positionStart;
                    if ((i = n) < positionStart2) {
                        if (itemCount == 1) {
                            updateOp.positionStart = positionStart2 + 1;
                            ++updateOp.itemCount;
                            i = n;
                        }
                        else {
                            i = n;
                            if (itemCount == 2) {
                                updateOp.positionStart = positionStart2 - 1;
                                --updateOp.itemCount;
                                i = n;
                            }
                        }
                    }
                }
            }
            else {
                final int positionStart3 = updateOp.positionStart;
                if (positionStart3 <= n) {
                    if (cmd == 1) {
                        i = n - updateOp.itemCount;
                    }
                    else {
                        i = n;
                        if (cmd == 2) {
                            i = n + updateOp.itemCount;
                        }
                    }
                }
                else if (itemCount == 1) {
                    updateOp.positionStart = positionStart3 + 1;
                    i = n;
                }
                else {
                    i = n;
                    if (itemCount == 2) {
                        updateOp.positionStart = positionStart3 - 1;
                        i = n;
                    }
                }
            }
            --j;
            n = i;
        }
        UpdateOp updateOp2;
        for (i = this.mPostponedList.size() - 1; i >= 0; --i) {
            updateOp2 = this.mPostponedList.get(i);
            if (updateOp2.cmd == 8) {
                itemCount = updateOp2.itemCount;
                if (itemCount == updateOp2.positionStart || itemCount < 0) {
                    this.mPostponedList.remove(i);
                    this.recycleUpdateOp(updateOp2);
                }
            }
            else if (updateOp2.itemCount <= 0) {
                this.mPostponedList.remove(i);
                this.recycleUpdateOp(updateOp2);
            }
        }
        return n;
    }
    
    public int applyPendingUpdatesToPosition(int n) {
        final int size = this.mPendingUpdates.size();
        int i = 0;
        int n2 = n;
        while (i < size) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            n = updateOp.cmd;
            if (n != 1) {
                if (n != 2) {
                    if (n != 8) {
                        n = n2;
                    }
                    else {
                        n = updateOp.positionStart;
                        if (n == n2) {
                            n = updateOp.itemCount;
                        }
                        else {
                            int n3;
                            if (n < (n3 = n2)) {
                                n3 = n2 - 1;
                            }
                            if (updateOp.itemCount <= (n = n3)) {
                                n = n3 + 1;
                            }
                        }
                    }
                }
                else {
                    final int positionStart = updateOp.positionStart;
                    if (positionStart <= (n = n2)) {
                        n = updateOp.itemCount;
                        if (positionStart + n > n2) {
                            return -1;
                        }
                        n = n2 - n;
                    }
                }
            }
            else if (updateOp.positionStart <= (n = n2)) {
                n = n2 + updateOp.itemCount;
            }
            ++i;
            n2 = n;
        }
        return n2;
    }
    
    void consumePostponedUpdates() {
        for (int size = this.mPostponedList.size(), i = 0; i < size; ++i) {
            this.mCallback.onDispatchSecondPass((UpdateOp)this.mPostponedList.get(i));
        }
        this.recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }
    
    void consumeUpdatesInOnePass() {
        this.consumePostponedUpdates();
        for (int size = this.mPendingUpdates.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            final int cmd = updateOp.cmd;
            if (cmd != 1) {
                if (cmd != 2) {
                    if (cmd != 4) {
                        if (cmd == 8) {
                            this.mCallback.onDispatchSecondPass(updateOp);
                            this.mCallback.offsetPositionsForMove(updateOp.positionStart, updateOp.itemCount);
                        }
                    }
                    else {
                        this.mCallback.onDispatchSecondPass(updateOp);
                        this.mCallback.markViewHoldersUpdated(updateOp.positionStart, updateOp.itemCount, updateOp.payload);
                    }
                }
                else {
                    this.mCallback.onDispatchSecondPass(updateOp);
                    this.mCallback.offsetPositionsForRemovingInvisible(updateOp.positionStart, updateOp.itemCount);
                }
            }
            else {
                this.mCallback.onDispatchSecondPass(updateOp);
                this.mCallback.offsetPositionsForAdd(updateOp.positionStart, updateOp.itemCount);
            }
            final Runnable mOnItemProcessedCallback = this.mOnItemProcessedCallback;
            if (mOnItemProcessedCallback != null) {
                mOnItemProcessedCallback.run();
            }
        }
        this.recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.mExistingUpdateTypes = 0;
    }
    
    void dispatchFirstPassAndUpdateViewHolders(final UpdateOp updateOp, final int n) {
        this.mCallback.onDispatchFirstPass(updateOp);
        final int cmd = updateOp.cmd;
        if (cmd != 2) {
            if (cmd != 4) {
                throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
            }
            this.mCallback.markViewHoldersUpdated(n, updateOp.itemCount, updateOp.payload);
        }
        else {
            this.mCallback.offsetPositionsForRemovingInvisible(n, updateOp.itemCount);
        }
    }
    
    int findPositionOffset(final int n) {
        return this.findPositionOffset(n, 0);
    }
    
    int findPositionOffset(int n, int n2) {
        final int size = this.mPostponedList.size();
        int i = n2;
        n2 = n;
        while (i < size) {
            final UpdateOp updateOp = this.mPostponedList.get(i);
            final int cmd = updateOp.cmd;
            if (cmd == 8) {
                n = updateOp.positionStart;
                if (n == n2) {
                    n = updateOp.itemCount;
                }
                else {
                    int n3;
                    if (n < (n3 = n2)) {
                        n3 = n2 - 1;
                    }
                    if (updateOp.itemCount <= (n = n3)) {
                        n = n3 + 1;
                    }
                }
            }
            else {
                final int positionStart = updateOp.positionStart;
                if (positionStart <= (n = n2)) {
                    if (cmd == 2) {
                        n = updateOp.itemCount;
                        if (n2 < positionStart + n) {
                            return -1;
                        }
                        n = n2 - n;
                    }
                    else {
                        n = n2;
                        if (cmd == 1) {
                            n = n2 + updateOp.itemCount;
                        }
                    }
                }
            }
            ++i;
            n2 = n;
        }
        return n2;
    }
    
    boolean hasAnyUpdateTypes(final int n) {
        return (this.mExistingUpdateTypes & n) != 0x0;
    }
    
    boolean hasPendingUpdates() {
        return this.mPendingUpdates.size() > 0;
    }
    
    boolean hasUpdates() {
        return !this.mPostponedList.isEmpty() && !this.mPendingUpdates.isEmpty();
    }
    
    @Override
    public UpdateOp obtainUpdateOp(final int cmd, final int positionStart, final int itemCount, final Object payload) {
        final UpdateOp updateOp = this.mUpdateOpPool.acquire();
        UpdateOp updateOp2;
        if (updateOp == null) {
            updateOp2 = new UpdateOp(cmd, positionStart, itemCount, payload);
        }
        else {
            updateOp.cmd = cmd;
            updateOp.positionStart = positionStart;
            updateOp.itemCount = itemCount;
            updateOp.payload = payload;
            updateOp2 = updateOp;
        }
        return updateOp2;
    }
    
    boolean onItemRangeChanged(final int n, final int n2, final Object o) {
        boolean b = false;
        if (n2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(this.obtainUpdateOp(4, n, n2, o));
        this.mExistingUpdateTypes |= 0x4;
        if (this.mPendingUpdates.size() == 1) {
            b = true;
        }
        return b;
    }
    
    boolean onItemRangeInserted(final int n, final int n2) {
        boolean b = false;
        if (n2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(this.obtainUpdateOp(1, n, n2, null));
        this.mExistingUpdateTypes |= 0x1;
        if (this.mPendingUpdates.size() == 1) {
            b = true;
        }
        return b;
    }
    
    boolean onItemRangeMoved(final int n, final int n2, final int n3) {
        boolean b = false;
        if (n == n2) {
            return false;
        }
        if (n3 == 1) {
            this.mPendingUpdates.add(this.obtainUpdateOp(8, n, n2, null));
            this.mExistingUpdateTypes |= 0x8;
            if (this.mPendingUpdates.size() == 1) {
                b = true;
            }
            return b;
        }
        throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
    }
    
    boolean onItemRangeRemoved(final int n, final int n2) {
        boolean b = false;
        if (n2 < 1) {
            return false;
        }
        this.mPendingUpdates.add(this.obtainUpdateOp(2, n, n2, null));
        this.mExistingUpdateTypes |= 0x2;
        if (this.mPendingUpdates.size() == 1) {
            b = true;
        }
        return b;
    }
    
    void preProcess() {
        this.mOpReorderer.reorderOps(this.mPendingUpdates);
        for (int size = this.mPendingUpdates.size(), i = 0; i < size; ++i) {
            final UpdateOp updateOp = this.mPendingUpdates.get(i);
            final int cmd = updateOp.cmd;
            if (cmd != 1) {
                if (cmd != 2) {
                    if (cmd != 4) {
                        if (cmd == 8) {
                            this.applyMove(updateOp);
                        }
                    }
                    else {
                        this.applyUpdate(updateOp);
                    }
                }
                else {
                    this.applyRemove(updateOp);
                }
            }
            else {
                this.applyAdd(updateOp);
            }
            final Runnable mOnItemProcessedCallback = this.mOnItemProcessedCallback;
            if (mOnItemProcessedCallback != null) {
                mOnItemProcessedCallback.run();
            }
        }
        this.mPendingUpdates.clear();
    }
    
    @Override
    public void recycleUpdateOp(final UpdateOp updateOp) {
        if (!this.mDisableRecycler) {
            updateOp.payload = null;
            this.mUpdateOpPool.release(updateOp);
        }
    }
    
    void recycleUpdateOpsAndClearList(final List<UpdateOp> list) {
        for (int size = list.size(), i = 0; i < size; ++i) {
            this.recycleUpdateOp(list.get(i));
        }
        list.clear();
    }
    
    void reset() {
        this.recycleUpdateOpsAndClearList(this.mPendingUpdates);
        this.recycleUpdateOpsAndClearList(this.mPostponedList);
        this.mExistingUpdateTypes = 0;
    }
    
    interface Callback
    {
        RecyclerView.ViewHolder findViewHolder(final int p0);
        
        void markViewHoldersUpdated(final int p0, final int p1, final Object p2);
        
        void offsetPositionsForAdd(final int p0, final int p1);
        
        void offsetPositionsForMove(final int p0, final int p1);
        
        void offsetPositionsForRemovingInvisible(final int p0, final int p1);
        
        void offsetPositionsForRemovingLaidOutOrNewView(final int p0, final int p1);
        
        void onDispatchFirstPass(final UpdateOp p0);
        
        void onDispatchSecondPass(final UpdateOp p0);
    }
    
    static final class UpdateOp
    {
        int cmd;
        int itemCount;
        Object payload;
        int positionStart;
        
        UpdateOp(final int cmd, final int positionStart, final int itemCount, final Object payload) {
            this.cmd = cmd;
            this.positionStart = positionStart;
            this.itemCount = itemCount;
            this.payload = payload;
        }
        
        String cmdToString() {
            final int cmd = this.cmd;
            if (cmd == 1) {
                return "add";
            }
            if (cmd == 2) {
                return "rm";
            }
            if (cmd == 4) {
                return "up";
            }
            if (cmd != 8) {
                return "??";
            }
            return "mv";
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UpdateOp)) {
                return false;
            }
            final UpdateOp updateOp = (UpdateOp)o;
            final int cmd = this.cmd;
            if (cmd != updateOp.cmd) {
                return false;
            }
            if (cmd == 8 && Math.abs(this.itemCount - this.positionStart) == 1 && this.itemCount == updateOp.positionStart && this.positionStart == updateOp.itemCount) {
                return true;
            }
            if (this.itemCount != updateOp.itemCount) {
                return false;
            }
            if (this.positionStart != updateOp.positionStart) {
                return false;
            }
            final Object payload = this.payload;
            if (payload != null) {
                if (!payload.equals(updateOp.payload)) {
                    return false;
                }
            }
            else if (updateOp.payload != null) {
                return false;
            }
            return true;
        }
        
        @Override
        public int hashCode() {
            return (this.cmd * 31 + this.positionStart) * 31 + this.itemCount;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append("[");
            sb.append(this.cmdToString());
            sb.append(",s:");
            sb.append(this.positionStart);
            sb.append("c:");
            sb.append(this.itemCount);
            sb.append(",p:");
            sb.append(this.payload);
            sb.append("]");
            return sb.toString();
        }
    }
}
