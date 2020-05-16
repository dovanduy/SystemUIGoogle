// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.List;
import java.util.Iterator;
import com.android.internal.annotations.VisibleForTesting;
import java.util.Collection;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StatusBarIconList
{
    private ArrayList<Slot> mSlots;
    
    public StatusBarIconList(final String[] array) {
        this.mSlots = new ArrayList<Slot>();
        for (int length = array.length, i = 0; i < length; ++i) {
            this.mSlots.add(new Slot(array[i], null));
        }
    }
    
    public void dump(final PrintWriter printWriter) {
        printWriter.println("StatusBarIconList state:");
        final int size = this.mSlots.size();
        final StringBuilder sb = new StringBuilder();
        sb.append("  icon slots: ");
        sb.append(size);
        printWriter.println(sb.toString());
        for (int i = 0; i < size; ++i) {
            printWriter.printf("    %2d:%s\n", i, this.mSlots.get(i).toString());
        }
    }
    
    public StatusBarIconHolder getIcon(final int index, final int n) {
        return this.mSlots.get(index).getHolderForTag(n);
    }
    
    protected Slot getSlot(final String s) {
        return this.mSlots.get(this.getSlotIndex(s));
    }
    
    public int getSlotIndex(final String anObject) {
        for (int size = this.mSlots.size(), i = 0; i < size; ++i) {
            if (this.mSlots.get(i).getName().equals(anObject)) {
                return i;
            }
        }
        this.mSlots.add(0, new Slot(anObject, null));
        return 0;
    }
    
    public String getSlotName(final int index) {
        return this.mSlots.get(index).getName();
    }
    
    protected ArrayList<Slot> getSlots() {
        return new ArrayList<Slot>(this.mSlots);
    }
    
    public int getViewIndex(final int index, final int n) {
        int i = 0;
        int n2 = 0;
        while (i < index) {
            final Slot slot = this.mSlots.get(i);
            int n3 = n2;
            if (slot.hasIconsInSlot()) {
                n3 = n2 + slot.numberOfIcons();
            }
            ++i;
            n2 = n3;
        }
        return n2 + this.mSlots.get(index).viewIndexOffsetForTag(n);
    }
    
    public void removeIcon(final int index, final int n) {
        this.mSlots.get(index).removeForTag(n);
    }
    
    public void setIcon(final int index, final StatusBarIconHolder statusBarIconHolder) {
        this.mSlots.get(index).addHolder(statusBarIconHolder);
    }
    
    public static class Slot
    {
        private StatusBarIconHolder mHolder;
        private final String mName;
        private ArrayList<StatusBarIconHolder> mSubSlots;
        
        public Slot(final String mName, final StatusBarIconHolder mHolder) {
            this.mName = mName;
            this.mHolder = mHolder;
        }
        
        private int getIndexForTag(final int n) {
            for (int i = 0; i < this.mSubSlots.size(); ++i) {
                if (this.mSubSlots.get(i).getTag() == n) {
                    return i;
                }
            }
            return -1;
        }
        
        private void setSubSlot(final StatusBarIconHolder statusBarIconHolder, final int n) {
            if (this.mSubSlots == null) {
                (this.mSubSlots = new ArrayList<StatusBarIconHolder>()).add(statusBarIconHolder);
                return;
            }
            if (this.getIndexForTag(n) != -1) {
                return;
            }
            this.mSubSlots.add(statusBarIconHolder);
        }
        
        private String subSlotsString() {
            if (this.mSubSlots == null) {
                return "";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(this.mSubSlots.size());
            sb.append(" subSlots");
            return sb.toString();
        }
        
        public void addHolder(final StatusBarIconHolder mHolder) {
            final int tag = mHolder.getTag();
            if (tag == 0) {
                this.mHolder = mHolder;
            }
            else {
                this.setSubSlot(mHolder, tag);
            }
        }
        
        @VisibleForTesting
        public void clear() {
            this.mHolder = null;
            if (this.mSubSlots != null) {
                this.mSubSlots = null;
            }
        }
        
        public StatusBarIconHolder getHolderForTag(final int n) {
            if (n == 0) {
                return this.mHolder;
            }
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots != null) {
                for (final StatusBarIconHolder statusBarIconHolder : mSubSlots) {
                    if (statusBarIconHolder.getTag() == n) {
                        return statusBarIconHolder;
                    }
                }
            }
            return null;
        }
        
        public List<StatusBarIconHolder> getHolderList() {
            final ArrayList<StatusBarIconHolder> list = new ArrayList<StatusBarIconHolder>();
            final StatusBarIconHolder mHolder = this.mHolder;
            if (mHolder != null) {
                list.add(mHolder);
            }
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots != null) {
                list.addAll(mSubSlots);
            }
            return list;
        }
        
        public List<StatusBarIconHolder> getHolderListInViewOrder() {
            final ArrayList<StatusBarIconHolder> list = new ArrayList<StatusBarIconHolder>();
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots != null) {
                for (int i = mSubSlots.size() - 1; i >= 0; --i) {
                    list.add(this.mSubSlots.get(i));
                }
            }
            final StatusBarIconHolder mHolder = this.mHolder;
            if (mHolder != null) {
                list.add(mHolder);
            }
            return list;
        }
        
        public String getName() {
            return this.mName;
        }
        
        public boolean hasIconsInSlot() {
            final StatusBarIconHolder mHolder = this.mHolder;
            boolean b = true;
            if (mHolder != null) {
                return true;
            }
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots == null) {
                return false;
            }
            if (mSubSlots.size() <= 0) {
                b = false;
            }
            return b;
        }
        
        public int numberOfIcons() {
            int n;
            if (this.mHolder == null) {
                n = 0;
            }
            else {
                n = 1;
            }
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots == null) {
                return n;
            }
            return n + mSubSlots.size();
        }
        
        public void removeForTag(int indexForTag) {
            if (indexForTag == 0) {
                this.mHolder = null;
            }
            else {
                indexForTag = this.getIndexForTag(indexForTag);
                if (indexForTag != -1) {
                    this.mSubSlots.remove(indexForTag);
                }
            }
        }
        
        @Override
        public String toString() {
            return String.format("(%s) %s", this.mName, this.subSlotsString());
        }
        
        public int viewIndexOffsetForTag(final int n) {
            final ArrayList<StatusBarIconHolder> mSubSlots = this.mSubSlots;
            if (mSubSlots == null) {
                return 0;
            }
            final int size = mSubSlots.size();
            if (n == 0) {
                return size;
            }
            return size - this.getIndexForTag(n) - 1;
        }
    }
}
