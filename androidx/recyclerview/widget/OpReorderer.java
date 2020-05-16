// 
// Decompiled by Procyon v0.5.36
// 

package androidx.recyclerview.widget;

import java.util.List;

class OpReorderer
{
    final Callback mCallback;
    
    OpReorderer(final Callback mCallback) {
        this.mCallback = mCallback;
    }
    
    private int getLastMoveOutOfOrder(final List<AdapterHelper.UpdateOp> list) {
        int i = list.size() - 1;
        int n = 0;
        while (i >= 0) {
            int n2;
            if (list.get(i).cmd == 8) {
                if ((n2 = n) != 0) {
                    return i;
                }
            }
            else {
                n2 = 1;
            }
            --i;
            n = n2;
        }
        return -1;
    }
    
    private void swapMoveAdd(final List<AdapterHelper.UpdateOp> list, final int n, final AdapterHelper.UpdateOp updateOp, final int n2, final AdapterHelper.UpdateOp updateOp2) {
        int n3;
        if (updateOp.itemCount < updateOp2.positionStart) {
            n3 = -1;
        }
        else {
            n3 = 0;
        }
        int n4 = n3;
        if (updateOp.positionStart < updateOp2.positionStart) {
            n4 = n3 + 1;
        }
        final int positionStart = updateOp2.positionStart;
        final int positionStart2 = updateOp.positionStart;
        if (positionStart <= positionStart2) {
            updateOp.positionStart = positionStart2 + updateOp2.itemCount;
        }
        final int positionStart3 = updateOp2.positionStart;
        final int itemCount = updateOp.itemCount;
        if (positionStart3 <= itemCount) {
            updateOp.itemCount = itemCount + updateOp2.itemCount;
        }
        updateOp2.positionStart += n4;
        list.set(n, updateOp2);
        list.set(n2, updateOp);
    }
    
    private void swapMoveOp(final List<AdapterHelper.UpdateOp> list, final int n, final int n2) {
        final AdapterHelper.UpdateOp updateOp = list.get(n);
        final AdapterHelper.UpdateOp updateOp2 = list.get(n2);
        final int cmd = updateOp2.cmd;
        if (cmd != 1) {
            if (cmd != 2) {
                if (cmd == 4) {
                    this.swapMoveUpdate(list, n, updateOp, n2, updateOp2);
                }
            }
            else {
                this.swapMoveRemove(list, n, updateOp, n2, updateOp2);
            }
        }
        else {
            this.swapMoveAdd(list, n, updateOp, n2, updateOp2);
        }
    }
    
    void reorderOps(final List<AdapterHelper.UpdateOp> list) {
        while (true) {
            final int lastMoveOutOfOrder = this.getLastMoveOutOfOrder(list);
            if (lastMoveOutOfOrder == -1) {
                break;
            }
            this.swapMoveOp(list, lastMoveOutOfOrder, lastMoveOutOfOrder + 1);
        }
    }
    
    void swapMoveRemove(final List<AdapterHelper.UpdateOp> list, final int n, final AdapterHelper.UpdateOp updateOp, final int n2, final AdapterHelper.UpdateOp updateOp2) {
        final int positionStart = updateOp.positionStart;
        final int itemCount = updateOp.itemCount;
        boolean b = false;
        int n3;
        if (positionStart < itemCount) {
            if (updateOp2.positionStart == positionStart && updateOp2.itemCount == itemCount - positionStart) {
                n3 = 0;
                b = true;
            }
            else {
                n3 = 0;
            }
        }
        else if (updateOp2.positionStart == itemCount + 1 && updateOp2.itemCount == positionStart - itemCount) {
            n3 = ((b = true) ? 1 : 0);
        }
        else {
            n3 = 1;
        }
        final int itemCount2 = updateOp.itemCount;
        final int positionStart2 = updateOp2.positionStart;
        if (itemCount2 < positionStart2) {
            updateOp2.positionStart = positionStart2 - 1;
        }
        else {
            final int itemCount3 = updateOp2.itemCount;
            if (itemCount2 < positionStart2 + itemCount3) {
                updateOp2.itemCount = itemCount3 - 1;
                updateOp.cmd = 2;
                updateOp.itemCount = 1;
                if (updateOp2.itemCount == 0) {
                    list.remove(n2);
                    this.mCallback.recycleUpdateOp(updateOp2);
                }
                return;
            }
        }
        final int positionStart3 = updateOp.positionStart;
        final int positionStart4 = updateOp2.positionStart;
        AdapterHelper.UpdateOp obtainUpdateOp = null;
        if (positionStart3 <= positionStart4) {
            updateOp2.positionStart = positionStart4 + 1;
        }
        else {
            final int itemCount4 = updateOp2.itemCount;
            if (positionStart3 < positionStart4 + itemCount4) {
                obtainUpdateOp = this.mCallback.obtainUpdateOp(2, positionStart3 + 1, positionStart4 + itemCount4 - positionStart3, null);
                updateOp2.itemCount = updateOp.positionStart - updateOp2.positionStart;
            }
        }
        if (b) {
            list.set(n, updateOp2);
            list.remove(n2);
            this.mCallback.recycleUpdateOp(updateOp);
            return;
        }
        if (n3 != 0) {
            if (obtainUpdateOp != null) {
                final int positionStart5 = updateOp.positionStart;
                if (positionStart5 > obtainUpdateOp.positionStart) {
                    updateOp.positionStart = positionStart5 - obtainUpdateOp.itemCount;
                }
                final int itemCount5 = updateOp.itemCount;
                if (itemCount5 > obtainUpdateOp.positionStart) {
                    updateOp.itemCount = itemCount5 - obtainUpdateOp.itemCount;
                }
            }
            final int positionStart6 = updateOp.positionStart;
            if (positionStart6 > updateOp2.positionStart) {
                updateOp.positionStart = positionStart6 - updateOp2.itemCount;
            }
            final int itemCount6 = updateOp.itemCount;
            if (itemCount6 > updateOp2.positionStart) {
                updateOp.itemCount = itemCount6 - updateOp2.itemCount;
            }
        }
        else {
            if (obtainUpdateOp != null) {
                final int positionStart7 = updateOp.positionStart;
                if (positionStart7 >= obtainUpdateOp.positionStart) {
                    updateOp.positionStart = positionStart7 - obtainUpdateOp.itemCount;
                }
                final int itemCount7 = updateOp.itemCount;
                if (itemCount7 >= obtainUpdateOp.positionStart) {
                    updateOp.itemCount = itemCount7 - obtainUpdateOp.itemCount;
                }
            }
            final int positionStart8 = updateOp.positionStart;
            if (positionStart8 >= updateOp2.positionStart) {
                updateOp.positionStart = positionStart8 - updateOp2.itemCount;
            }
            final int itemCount8 = updateOp.itemCount;
            if (itemCount8 >= updateOp2.positionStart) {
                updateOp.itemCount = itemCount8 - updateOp2.itemCount;
            }
        }
        list.set(n, updateOp2);
        if (updateOp.positionStart != updateOp.itemCount) {
            list.set(n2, updateOp);
        }
        else {
            list.remove(n2);
        }
        if (obtainUpdateOp != null) {
            list.add(n, obtainUpdateOp);
        }
    }
    
    void swapMoveUpdate(final List<AdapterHelper.UpdateOp> list, final int n, final AdapterHelper.UpdateOp updateOp, final int n2, final AdapterHelper.UpdateOp updateOp2) {
        final int itemCount = updateOp.itemCount;
        final int positionStart = updateOp2.positionStart;
        AdapterHelper.UpdateOp obtainUpdateOp = null;
        AdapterHelper.UpdateOp obtainUpdateOp2 = null;
        Label_0089: {
            if (itemCount < positionStart) {
                updateOp2.positionStart = positionStart - 1;
            }
            else {
                final int itemCount2 = updateOp2.itemCount;
                if (itemCount < positionStart + itemCount2) {
                    updateOp2.itemCount = itemCount2 - 1;
                    obtainUpdateOp2 = this.mCallback.obtainUpdateOp(4, updateOp.positionStart, 1, updateOp2.payload);
                    break Label_0089;
                }
            }
            obtainUpdateOp2 = null;
        }
        final int positionStart2 = updateOp.positionStart;
        final int positionStart3 = updateOp2.positionStart;
        if (positionStart2 <= positionStart3) {
            updateOp2.positionStart = positionStart3 + 1;
        }
        else {
            final int itemCount3 = updateOp2.itemCount;
            if (positionStart2 < positionStart3 + itemCount3) {
                final int n3 = positionStart3 + itemCount3 - positionStart2;
                obtainUpdateOp = this.mCallback.obtainUpdateOp(4, positionStart2 + 1, n3, updateOp2.payload);
                updateOp2.itemCount -= n3;
            }
        }
        list.set(n2, updateOp);
        if (updateOp2.itemCount > 0) {
            list.set(n, updateOp2);
        }
        else {
            list.remove(n);
            this.mCallback.recycleUpdateOp(updateOp2);
        }
        if (obtainUpdateOp2 != null) {
            list.add(n, obtainUpdateOp2);
        }
        if (obtainUpdateOp != null) {
            list.add(n, obtainUpdateOp);
        }
    }
    
    interface Callback
    {
        AdapterHelper.UpdateOp obtainUpdateOp(final int p0, final int p1, final int p2, final Object p3);
        
        void recycleUpdateOp(final AdapterHelper.UpdateOp p0);
    }
}
