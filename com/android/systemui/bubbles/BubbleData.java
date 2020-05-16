// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import android.util.Pair;
import java.util.Collections;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting$Visibility;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import java.util.function.ToLongFunction;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import android.app.PendingIntent;
import android.app.Notification$BubbleMetadata;
import android.app.PendingIntent$CanceledException;
import android.util.Log;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.function.Predicate;
import java.util.Objects;
import com.android.systemui.R$integer;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.HashMap;
import android.content.Context;
import java.util.List;
import java.util.Map;
import java.util.Comparator;

public class BubbleData
{
    private static final Comparator<Bubble> BUBBLES_BY_SORT_KEY_DESCENDING;
    private static final Comparator<Map.Entry<String, Long>> GROUPS_BY_MAX_SORT_KEY_DESCENDING;
    private final List<Bubble> mBubbles;
    private final Context mContext;
    private boolean mExpanded;
    private Listener mListener;
    private final int mMaxBubbles;
    private final int mMaxOverflowBubbles;
    private final List<Bubble> mOverflowBubbles;
    private final List<Bubble> mPendingBubbles;
    private Bubble mSelectedBubble;
    private Update mStateChange;
    private HashMap<String, String> mSuppressedGroupKeys;
    private BubbleController.NotificationSuppressionChangedListener mSuppressionListener;
    private TimeSource mTimeSource;
    
    static {
        BUBBLES_BY_SORT_KEY_DESCENDING = Comparator.comparing((Function<? super Bubble, ? extends Comparable>)_$$Lambda$BubbleData$vPZCImnk7rTPTX1c7nr0PX7FO2o.INSTANCE).reversed();
        GROUPS_BY_MAX_SORT_KEY_DESCENDING = Comparator.comparing((Function<? super Map.Entry<String, Long>, ? extends Comparable>)_$$Lambda$JmVH_PWbzq5woEs3Hauzhf2I3Jc.INSTANCE).reversed();
    }
    
    public BubbleData(final Context mContext) {
        this.mTimeSource = (TimeSource)_$$Lambda$0E0fwzH9SS6_aB9lL5npMzupI4Q.INSTANCE;
        this.mSuppressedGroupKeys = new HashMap<String, String>();
        this.mContext = mContext;
        this.mBubbles = new ArrayList<Bubble>();
        this.mOverflowBubbles = new ArrayList<Bubble>();
        this.mPendingBubbles = new ArrayList<Bubble>();
        this.mStateChange = new Update((List)this.mBubbles, (List)this.mOverflowBubbles);
        this.mMaxBubbles = this.mContext.getResources().getInteger(R$integer.bubbles_max_rendered);
        this.mMaxOverflowBubbles = this.mContext.getResources().getInteger(R$integer.bubbles_max_overflow);
    }
    
    private void dispatchPendingChanges() {
        if (this.mListener != null && this.mStateChange.anythingChanged()) {
            this.mListener.applyUpdate(this.mStateChange);
        }
        this.mStateChange = new Update((List)this.mBubbles, (List)this.mOverflowBubbles);
    }
    
    private void doAdd(final Bubble addedBubble) {
        final boolean hasBubbleWithGroupId = this.hasBubbleWithGroupId(addedBubble.getGroupId());
        int firstIndexForGroup;
        if (this.isExpanded() && !(hasBubbleWithGroupId ^ true)) {
            firstIndexForGroup = this.findFirstIndexForGroup(addedBubble.getGroupId());
        }
        else {
            firstIndexForGroup = 0;
        }
        if (this.insertBubble(firstIndexForGroup, addedBubble) < this.mBubbles.size() - 1) {
            this.mStateChange.orderChanged = true;
        }
        this.mStateChange.addedBubble = addedBubble;
        if (!this.isExpanded()) {
            final Update mStateChange = this.mStateChange;
            mStateChange.orderChanged |= this.packGroup(this.findFirstIndexForGroup(addedBubble.getGroupId()));
            this.setSelectedBubbleInternal(this.mBubbles.get(0));
        }
    }
    
    private void doRemove(final String anObject, final int n) {
        for (int i = 0; i < this.mPendingBubbles.size(); ++i) {
            if (this.mPendingBubbles.get(i).getKey().equals(anObject)) {
                final List<Bubble> mPendingBubbles = this.mPendingBubbles;
                mPendingBubbles.remove(mPendingBubbles.get(i));
            }
        }
        final int indexForKey = this.indexForKey(anObject);
        if (indexForKey == -1) {
            return;
        }
        final Bubble b = this.mBubbles.get(indexForKey);
        if (this.mBubbles.size() == 1) {
            this.setExpandedInternal(false);
            this.setSelectedBubbleInternal(null);
        }
        if (indexForKey < this.mBubbles.size() - 1) {
            this.mStateChange.orderChanged = true;
        }
        this.mBubbles.remove(indexForKey);
        this.mStateChange.bubbleRemoved(b, n);
        if (!this.isExpanded()) {
            final Update mStateChange = this.mStateChange;
            mStateChange.orderChanged |= this.repackAll();
        }
        this.overflowBubble(n, b);
        if (Objects.equals(this.mSelectedBubble, b)) {
            this.setSelectedBubbleInternal(this.mBubbles.get(Math.min(indexForKey, this.mBubbles.size() - 1)));
        }
        this.maybeSendDeleteIntent(n, b.getEntry());
    }
    
    private void doUpdate(final Bubble updatedBubble) {
        this.mStateChange.updatedBubble = updatedBubble;
        if (!this.isExpanded()) {
            final int index = this.mBubbles.indexOf(updatedBubble);
            this.mBubbles.remove(updatedBubble);
            final int insertBubble = this.insertBubble(0, updatedBubble);
            if (index != insertBubble) {
                this.packGroup(insertBubble);
                this.mStateChange.orderChanged = true;
            }
            this.setSelectedBubbleInternal(this.mBubbles.get(0));
        }
    }
    
    private int findFirstIndexForGroup(final String anObject) {
        for (int i = 0; i < this.mBubbles.size(); ++i) {
            if (this.mBubbles.get(i).getGroupId().equals(anObject)) {
                return i;
            }
        }
        return 0;
    }
    
    private boolean hasBubbleWithGroupId(final String s) {
        return this.mBubbles.stream().anyMatch(new _$$Lambda$BubbleData$FNLczJNo9xVuN2ajZ5gz4uSU_hU(s));
    }
    
    private int indexForKey(final String anObject) {
        for (int i = 0; i < this.mBubbles.size(); ++i) {
            if (this.mBubbles.get(i).getKey().equals(anObject)) {
                return i;
            }
        }
        return -1;
    }
    
    private int insertBubble(int i, final Bubble bubble) {
        final long sortKey = sortKey(bubble);
        Object anObject = null;
        while (i < this.mBubbles.size()) {
            final Bubble bubble2 = this.mBubbles.get(i);
            final String groupId = bubble2.getGroupId();
            if ((groupId.equals(anObject) ^ true) && sortKey > sortKey(bubble2)) {
                this.mBubbles.add(i, bubble);
                return i;
            }
            ++i;
            anObject = groupId;
        }
        this.mBubbles.add(bubble);
        return this.mBubbles.size() - 1;
    }
    
    private void maybeSendDeleteIntent(final int n, final NotificationEntry notificationEntry) {
        if (n == 1) {
            final Notification$BubbleMetadata bubbleMetadata = notificationEntry.getBubbleMetadata();
            PendingIntent deleteIntent;
            if (bubbleMetadata != null) {
                deleteIntent = bubbleMetadata.getDeleteIntent();
            }
            else {
                deleteIntent = null;
            }
            if (deleteIntent != null) {
                try {
                    deleteIntent.send();
                }
                catch (PendingIntent$CanceledException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to send delete intent for bubble with key: ");
                    sb.append(notificationEntry.getKey());
                    Log.w("Bubbles", sb.toString());
                }
            }
        }
    }
    
    private void moveOverflowBubbleToPending(final Bubble bubble) {
        bubble.markUpdatedAt(this.mTimeSource.currentTimeMillis());
        this.mOverflowBubbles.remove(bubble);
        this.mPendingBubbles.add(bubble);
    }
    
    private boolean packGroup(final int n) {
        final String groupId = this.mBubbles.get(n).getGroupId();
        final ArrayList<Bubble> list = new ArrayList<Bubble>();
        for (int i = this.mBubbles.size() - 1; i > n; --i) {
            if (this.mBubbles.get(i).getGroupId().equals(groupId)) {
                list.add(0, this.mBubbles.get(i));
            }
        }
        if (list.isEmpty()) {
            return false;
        }
        this.mBubbles.removeAll(list);
        this.mBubbles.addAll(n + 1, list);
        return true;
    }
    
    private boolean repackAll() {
        if (this.mBubbles.isEmpty()) {
            return false;
        }
        final HashMap<Object, Long> hashMap = new HashMap<Object, Long>();
        for (final Bubble bubble : this.mBubbles) {
            final long longValue = hashMap.getOrDefault(bubble.getGroupId(), 0L);
            final long sortKey = sortKey(bubble);
            if (sortKey > longValue) {
                hashMap.put(bubble.getGroupId(), sortKey);
            }
        }
        final List<? super Object> list = hashMap.entrySet().stream().sorted((Comparator<? super Object>)BubbleData.GROUPS_BY_MAX_SORT_KEY_DESCENDING).map((Function<? super Object, ?>)_$$Lambda$CSz_ibwXhtkKNl72Q8tR5oBgkWk.INSTANCE).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList());
        final ArrayList<Bubble> list2 = new ArrayList<Bubble>(this.mBubbles.size());
        final Iterator<String> iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            this.mBubbles.stream().filter(new _$$Lambda$BubbleData$LN_vRMJMi2Y8aqUpU41Urc29gp8(iterator2.next())).sorted((Comparator<? super Object>)BubbleData.BUBBLES_BY_SORT_KEY_DESCENDING).forEachOrdered(new _$$Lambda$0tU2wih_2wwdAnw6hE7FT9YuCis(list2));
        }
        if (list2.equals(this.mBubbles)) {
            return false;
        }
        this.mBubbles.clear();
        this.mBubbles.addAll(list2);
        return true;
    }
    
    private void setExpandedInternal(final boolean b) {
        if (this.mExpanded == b) {
            return;
        }
        if (b) {
            if (this.mBubbles.isEmpty()) {
                Log.e("Bubbles", "Attempt to expand stack when empty!");
                return;
            }
            final Bubble mSelectedBubble = this.mSelectedBubble;
            if (mSelectedBubble == null) {
                Log.e("Bubbles", "Attempt to expand stack without selected bubble!");
                return;
            }
            mSelectedBubble.markUpdatedAt(this.mTimeSource.currentTimeMillis());
            this.mSelectedBubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
            final Update mStateChange = this.mStateChange;
            mStateChange.orderChanged |= this.repackAll();
        }
        else if (!this.mBubbles.isEmpty()) {
            final Update mStateChange2 = this.mStateChange;
            mStateChange2.orderChanged |= this.repackAll();
            if (this.mBubbles.indexOf(this.mSelectedBubble) > 0) {
                if (!this.mSelectedBubble.isOngoing() && this.mBubbles.get(0).isOngoing()) {
                    this.setSelectedBubbleInternal(this.mBubbles.get(0));
                }
                else {
                    this.mBubbles.remove(this.mSelectedBubble);
                    this.mBubbles.add(0, this.mSelectedBubble);
                    final Update mStateChange3 = this.mStateChange;
                    mStateChange3.orderChanged |= this.packGroup(0);
                }
            }
        }
        this.mExpanded = b;
        final Update mStateChange4 = this.mStateChange;
        mStateChange4.expanded = b;
        mStateChange4.expandedChanged = true;
    }
    
    private void setSelectedBubbleInternal(final Bubble bubble) {
        if (Objects.equals(bubble, this.mSelectedBubble)) {
            return;
        }
        if (bubble != null && !this.mBubbles.contains(bubble) && !this.mOverflowBubbles.contains(bubble)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Cannot select bubble which doesn't exist! (");
            sb.append(bubble);
            sb.append(") bubbles=");
            sb.append(this.mBubbles);
            Log.e("Bubbles", sb.toString());
            return;
        }
        if (this.mExpanded && bubble != null) {
            bubble.markAsAccessedAt(this.mTimeSource.currentTimeMillis());
        }
        this.mSelectedBubble = bubble;
        final Update mStateChange = this.mStateChange;
        mStateChange.selectedBubble = bubble;
        mStateChange.selectionChanged = true;
    }
    
    private static long sortKey(final Bubble bubble) {
        long lastUpdateTime = bubble.getLastUpdateTime();
        if (bubble.isOngoing()) {
            lastUpdateTime |= 0x4000000000000000L;
        }
        return lastUpdateTime;
    }
    
    private void trim() {
        if (this.mBubbles.size() > this.mMaxBubbles) {
            this.mBubbles.stream().sorted(Comparator.comparingLong((ToLongFunction<? super Object>)_$$Lambda$x9O8XLDgnXklCbpbq_xgakOvcgY.INSTANCE)).filter(new _$$Lambda$BubbleData$L4D7VVn4Leo4I7cRC0GQT5zjp6A(this)).findFirst().ifPresent(new _$$Lambda$BubbleData$zcghr5fPhIHcncBh7TIyplT8aUo(this));
        }
    }
    
    void addSummaryToSuppress(final String key, final String value) {
        this.mSuppressedGroupKeys.put(key, value);
    }
    
    public void dismissAll(final int n) {
        if (this.mBubbles.isEmpty()) {
            return;
        }
        this.setExpandedInternal(false);
        this.setSelectedBubbleInternal(null);
        while (!this.mBubbles.isEmpty()) {
            this.doRemove(this.mBubbles.get(0).getKey(), n);
        }
        this.dispatchPendingChanges();
    }
    
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.print("selected: ");
        final Bubble mSelectedBubble = this.mSelectedBubble;
        String key;
        if (mSelectedBubble != null) {
            key = mSelectedBubble.getKey();
        }
        else {
            key = "null";
        }
        printWriter.println(key);
        printWriter.print("expanded: ");
        printWriter.println(this.mExpanded);
        printWriter.print("count:    ");
        printWriter.println(this.mBubbles.size());
        final Iterator<Bubble> iterator = this.mBubbles.iterator();
        while (iterator.hasNext()) {
            iterator.next().dump(fileDescriptor, printWriter, array);
        }
        printWriter.print("summaryKeys: ");
        printWriter.println(this.mSuppressedGroupKeys.size());
        for (final String str : this.mSuppressedGroupKeys.keySet()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("   suppressing: ");
            sb.append(str);
            printWriter.println(sb.toString());
        }
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    Bubble getBubbleWithKey(final String anObject) {
        for (int i = 0; i < this.mBubbles.size(); ++i) {
            final Bubble bubble = this.mBubbles.get(i);
            if (bubble.getKey().equals(anObject)) {
                return bubble;
            }
        }
        return null;
    }
    
    Bubble getBubbleWithView(final View obj) {
        for (int i = 0; i < this.mBubbles.size(); ++i) {
            final Bubble bubble = this.mBubbles.get(i);
            if (bubble.getIconView() != null && bubble.getIconView().equals(obj)) {
                return bubble;
            }
        }
        return null;
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    public List<Bubble> getBubbles() {
        return Collections.unmodifiableList((List<? extends Bubble>)this.mBubbles);
    }
    
    ArrayList<Bubble> getBubblesInGroup(final String s) {
        final ArrayList<Bubble> list = new ArrayList<Bubble>();
        if (s == null) {
            return list;
        }
        for (final Bubble e : this.mBubbles) {
            if (s.equals(e.getEntry().getSbn().getGroupKey())) {
                list.add(e);
            }
        }
        return list;
    }
    
    Bubble getOrCreateBubble(final NotificationEntry entry) {
        final Bubble bubbleWithKey = this.getBubbleWithKey(entry.getKey());
        Bubble bubble3;
        if (bubbleWithKey == null) {
            final int n = 0;
            int n2 = 0;
            int i;
            while (true) {
                i = n;
                if (n2 >= this.mOverflowBubbles.size()) {
                    break;
                }
                final Bubble bubble = this.mOverflowBubbles.get(n2);
                if (bubble.getKey().equals(entry.getKey())) {
                    this.moveOverflowBubbleToPending(bubble);
                    bubble.setEntry(entry);
                    return bubble;
                }
                ++n2;
            }
            while (i < this.mPendingBubbles.size()) {
                final Bubble bubble2 = this.mPendingBubbles.get(i);
                if (bubble2.getKey().equals(entry.getKey())) {
                    bubble2.setEntry(entry);
                    return bubble2;
                }
                ++i;
            }
            bubble3 = new Bubble(entry, this.mSuppressionListener);
            this.mPendingBubbles.add(bubble3);
        }
        else {
            bubbleWithKey.setEntry(entry);
            bubble3 = bubbleWithKey;
        }
        return bubble3;
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    Bubble getOverflowBubbleWithKey(final String anObject) {
        for (int i = 0; i < this.mOverflowBubbles.size(); ++i) {
            final Bubble bubble = this.mOverflowBubbles.get(i);
            if (bubble.getKey().equals(anObject)) {
                return bubble;
            }
        }
        return null;
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    public List<Bubble> getOverflowBubbles() {
        return Collections.unmodifiableList((List<? extends Bubble>)this.mOverflowBubbles);
    }
    
    public Bubble getSelectedBubble() {
        return this.mSelectedBubble;
    }
    
    String getSummaryKey(final String key) {
        return this.mSuppressedGroupKeys.get(key);
    }
    
    public boolean hasBubbleWithKey(final String s) {
        return this.getBubbleWithKey(s) != null;
    }
    
    public boolean hasBubbles() {
        return this.mBubbles.isEmpty() ^ true;
    }
    
    public boolean isExpanded() {
        return this.mExpanded;
    }
    
    boolean isSummarySuppressed(final String key) {
        return this.mSuppressedGroupKeys.containsKey(key);
    }
    
    public void notificationEntryRemoved(final NotificationEntry notificationEntry, final int n) {
        this.doRemove(notificationEntry.getKey(), n);
        this.dispatchPendingChanges();
    }
    
    void notificationEntryUpdated(final Bubble bubble, final boolean b, final boolean b2) {
        this.mPendingBubbles.remove(bubble);
        final Bubble bubbleWithKey = this.getBubbleWithKey(bubble.getKey());
        final boolean b3 = b | (bubble.getEntry().getRanking().visuallyInterruptive() ^ true);
        if (bubbleWithKey == null) {
            bubble.setSuppressFlyout(b3);
            this.doAdd(bubble);
            this.trim();
        }
        else {
            bubble.setSuppressFlyout(b3);
            this.doUpdate(bubble);
        }
        if (bubble.shouldAutoExpand()) {
            this.setSelectedBubbleInternal(bubble);
            if (!this.mExpanded) {
                this.setExpandedInternal(true);
            }
        }
        else if (this.mSelectedBubble == null) {
            this.setSelectedBubbleInternal(bubble);
        }
        final boolean mExpanded = this.mExpanded;
        boolean suppressNotification = false;
        final boolean b4 = mExpanded && this.mSelectedBubble == bubble;
        if (b4 || !b2 || !bubble.showInShade()) {
            suppressNotification = true;
        }
        bubble.setSuppressNotification(suppressNotification);
        bubble.setShowDot(b4 ^ true);
        this.dispatchPendingChanges();
    }
    
    void notifyDisplayEmpty(final int n) {
        for (final Bubble bubble : this.mBubbles) {
            if (bubble.getDisplayId() == n) {
                if (bubble.getExpandedView() != null) {
                    bubble.getExpandedView().notifyDisplayEmpty();
                    break;
                }
                break;
            }
        }
    }
    
    void overflowBubble(final int n, final Bubble bubble) {
        if (n == 2 || n == 1) {
            this.mOverflowBubbles.add(0, bubble);
            bubble.stopInflation();
            if (this.mOverflowBubbles.size() == this.mMaxOverflowBubbles + 1) {
                final List<Bubble> mOverflowBubbles = this.mOverflowBubbles;
                mOverflowBubbles.remove(mOverflowBubbles.size() - 1);
            }
        }
    }
    
    public void promoteBubbleFromOverflow(final Bubble bubble, final BubbleStackView bubbleStackView, final BubbleIconFactory bubbleIconFactory) {
        this.moveOverflowBubbleToPending(bubble);
        bubble.inflate(new _$$Lambda$BubbleData$bfPVpWupBg8Uri1C6TK8ElH2jLU(this, bubble), this.mContext, bubbleStackView, bubbleIconFactory);
        this.dispatchPendingChanges();
    }
    
    void removeSuppressedSummary(final String key) {
        this.mSuppressedGroupKeys.remove(key);
    }
    
    public void setExpanded(final boolean expandedInternal) {
        this.setExpandedInternal(expandedInternal);
        this.dispatchPendingChanges();
    }
    
    public void setListener(final Listener mListener) {
        this.mListener = mListener;
    }
    
    public void setSelectedBubble(final Bubble selectedBubbleInternal) {
        this.setSelectedBubbleInternal(selectedBubbleInternal);
        this.dispatchPendingChanges();
    }
    
    public void setSuppressionChangedListener(final BubbleController.NotificationSuppressionChangedListener mSuppressionListener) {
        this.mSuppressionListener = mSuppressionListener;
    }
    
    @VisibleForTesting(visibility = VisibleForTesting$Visibility.PRIVATE)
    void setTimeSource(final TimeSource mTimeSource) {
        this.mTimeSource = mTimeSource;
    }
    
    interface Listener
    {
        void applyUpdate(final Update p0);
    }
    
    interface TimeSource
    {
        long currentTimeMillis();
    }
    
    static final class Update
    {
        Bubble addedBubble;
        final List<Bubble> bubbles;
        boolean expanded;
        boolean expandedChanged;
        boolean orderChanged;
        final List<Pair<Bubble, Integer>> removedBubbles;
        Bubble selectedBubble;
        boolean selectionChanged;
        Bubble updatedBubble;
        
        private Update(final List<Bubble> list, final List<Bubble> list2) {
            this.removedBubbles = new ArrayList<Pair<Bubble, Integer>>();
            this.bubbles = Collections.unmodifiableList((List<? extends Bubble>)list);
            Collections.unmodifiableList((List<?>)list2);
        }
        
        boolean anythingChanged() {
            return this.expandedChanged || this.selectionChanged || this.addedBubble != null || this.updatedBubble != null || !this.removedBubbles.isEmpty() || this.orderChanged;
        }
        
        void bubbleRemoved(final Bubble bubble, final int i) {
            this.removedBubbles.add((Pair<Bubble, Integer>)new Pair((Object)bubble, (Object)i));
        }
    }
}
