// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Collections;
import android.graphics.drawable.Icon;
import android.os.UserHandle;
import com.android.systemui.R$dimen;
import android.util.ArrayMap;
import android.view.ViewGroup;
import com.android.systemui.statusbar.StatusIconDisplayable;
import java.io.PrintWriter;
import java.io.FileDescriptor;
import android.os.Bundle;
import java.util.Iterator;
import java.util.List;
import com.android.internal.statusbar.StatusBarIcon;
import java.util.function.Consumer;
import com.android.systemui.Dependency;
import java.util.ArrayList;
import android.util.ArraySet;
import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.tuner.TunerService;

public class StatusBarIconControllerImpl extends StatusBarIconList implements Tunable, ConfigurationListener, Dumpable, Callbacks, StatusBarIconController
{
    private Context mContext;
    private final ArraySet<String> mIconBlacklist;
    private final ArrayList<IconManager> mIconGroups;
    
    public StatusBarIconControllerImpl(final Context mContext, final CommandQueue commandQueue) {
        super(mContext.getResources().getStringArray(17236079));
        this.mIconGroups = new ArrayList<IconManager>();
        this.mIconBlacklist = (ArraySet<String>)new ArraySet();
        Dependency.get(ConfigurationController.class).addCallback((ConfigurationController.ConfigurationListener)this);
        this.mContext = mContext;
        this.loadDimens();
        commandQueue.addCallback((CommandQueue.Callbacks)this);
        Dependency.get(TunerService.class).addTunable((TunerService.Tunable)this, "icon_blacklist");
    }
    
    private void addSystemIcon(int viewIndex, final StatusBarIconHolder statusBarIconHolder) {
        final String slotName = this.getSlotName(viewIndex);
        viewIndex = this.getViewIndex(viewIndex, statusBarIconHolder.getTag());
        this.mIconGroups.forEach(new _$$Lambda$StatusBarIconControllerImpl$fL8PZXISckai_5GwvhWVS3QVTsY(viewIndex, slotName, this.mIconBlacklist.contains((Object)slotName), statusBarIconHolder));
    }
    
    private void handleSet(int viewIndex, final StatusBarIconHolder statusBarIconHolder) {
        viewIndex = this.getViewIndex(viewIndex, statusBarIconHolder.getTag());
        this.mIconGroups.forEach(new _$$Lambda$StatusBarIconControllerImpl$ayp5xWywAkBOOSd_6MshVHM8Mms(viewIndex, statusBarIconHolder));
    }
    
    private void loadDimens() {
    }
    
    private void setIcon(final int n, final StatusBarIcon statusBarIcon) {
        if (statusBarIcon == null) {
            this.removeAllIconsForSlot(this.getSlotName(n));
            return;
        }
        this.setIcon(n, StatusBarIconHolder.fromIcon(statusBarIcon));
    }
    
    @Override
    public void addIconGroup(final IconManager e) {
        this.mIconGroups.add(e);
        final ArrayList<Slot> slots = this.getSlots();
        for (int i = 0; i < slots.size(); ++i) {
            final Slot slot = slots.get(i);
            final List<StatusBarIconHolder> holderListInViewOrder = slot.getHolderListInViewOrder();
            final boolean contains = this.mIconBlacklist.contains((Object)slot.getName());
            for (final StatusBarIconHolder statusBarIconHolder : holderListInViewOrder) {
                statusBarIconHolder.getTag();
                e.onIconAdded(this.getViewIndex(this.getSlotIndex(slot.getName()), statusBarIconHolder.getTag()), slot.getName(), contains, statusBarIconHolder);
            }
        }
    }
    
    public void dispatchDemoCommand(final String s, final Bundle bundle) {
        for (final IconManager iconManager : this.mIconGroups) {
            if (iconManager.isDemoable()) {
                iconManager.dispatchDemoCommand(s, bundle);
            }
        }
    }
    
    @Override
    public void dump(final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        printWriter.println("StatusBarIconController state:");
        for (final IconManager iconManager : this.mIconGroups) {
            if (iconManager.shouldLog()) {
                final ViewGroup mGroup = iconManager.mGroup;
                final int childCount = mGroup.getChildCount();
                final StringBuilder sb = new StringBuilder();
                sb.append("  icon views: ");
                sb.append(childCount);
                printWriter.println(sb.toString());
                for (int i = 0; i < childCount; ++i) {
                    final StatusIconDisplayable obj = (StatusIconDisplayable)mGroup.getChildAt(i);
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("    [");
                    sb2.append(i);
                    sb2.append("] icon=");
                    sb2.append(obj);
                    printWriter.println(sb2.toString());
                }
            }
        }
        super.dump(printWriter);
    }
    
    @Override
    public void onDensityOrFontScaleChanged() {
        this.loadDimens();
    }
    
    @Override
    public void onTuningChanged(final String anObject, final String s) {
        if (!"icon_blacklist".equals(anObject)) {
            return;
        }
        this.mIconBlacklist.clear();
        this.mIconBlacklist.addAll((ArraySet)StatusBarIconController.getIconBlacklist(this.mContext, s));
        final ArrayList<Slot> slots = this.getSlots();
        final ArrayMap arrayMap = new ArrayMap();
        for (int i = slots.size() - 1; i >= 0; --i) {
            final Slot slot = slots.get(i);
            arrayMap.put((Object)slot, (Object)slot.getHolderList());
            this.removeAllIconsForSlot(slot.getName());
        }
        for (int j = 0; j < slots.size(); ++j) {
            final Slot slot2 = slots.get(j);
            final List list = (List)arrayMap.get((Object)slot2);
            if (list != null) {
                final Iterator<StatusBarIconHolder> iterator = list.iterator();
                while (iterator.hasNext()) {
                    this.setIcon(this.getSlotIndex(slot2.getName()), iterator.next());
                }
            }
        }
    }
    
    @Override
    public void removeAllIconsForSlot(final String s) {
        final Slot slot = this.getSlot(s);
        if (!slot.hasIconsInSlot()) {
            return;
        }
        final int slotIndex = this.getSlotIndex(s);
        for (final StatusBarIconHolder statusBarIconHolder : slot.getHolderListInViewOrder()) {
            final int viewIndex = this.getViewIndex(slotIndex, statusBarIconHolder.getTag());
            slot.removeForTag(statusBarIconHolder.getTag());
            this.mIconGroups.forEach(new _$$Lambda$StatusBarIconControllerImpl$uTqaHUAWHbu0P16vDWL0oAyCetk(viewIndex));
        }
    }
    
    @Override
    public void removeIcon(int viewIndex, final int n) {
        if (this.getIcon(viewIndex, n) == null) {
            return;
        }
        super.removeIcon(viewIndex, n);
        viewIndex = this.getViewIndex(viewIndex, 0);
        this.mIconGroups.forEach(new _$$Lambda$StatusBarIconControllerImpl$XIHL8F8kJA04U9X_9IHtSYwXxLU(viewIndex));
    }
    
    @Override
    public void removeIcon(final String s) {
        this.removeAllIconsForSlot(s);
    }
    
    @Override
    public void removeIconGroup(final IconManager o) {
        o.destroy();
        this.mIconGroups.remove(o);
    }
    
    @Override
    public void setExternalIcon(final String s) {
        this.mIconGroups.forEach(new _$$Lambda$StatusBarIconControllerImpl$rsmVGSlXlU7ffeIAEgpWeyyu04I(this.getViewIndex(this.getSlotIndex(s), 0), this.mContext.getResources().getDimensionPixelSize(R$dimen.status_bar_icon_drawing_size)));
    }
    
    @Override
    public void setIcon(final int n, final StatusBarIconHolder statusBarIconHolder) {
        final boolean b = this.getIcon(n, statusBarIconHolder.getTag()) == null;
        super.setIcon(n, statusBarIconHolder);
        if (b) {
            this.addSystemIcon(n, statusBarIconHolder);
        }
        else {
            this.handleSet(n, statusBarIconHolder);
        }
    }
    
    @Override
    public void setIcon(final String s, final int n, final CharSequence contentDescription) {
        final int slotIndex = this.getSlotIndex(s);
        final StatusBarIconHolder icon = this.getIcon(slotIndex, 0);
        if (icon == null) {
            this.setIcon(slotIndex, StatusBarIconHolder.fromIcon(new StatusBarIcon(UserHandle.SYSTEM, this.mContext.getPackageName(), Icon.createWithResource(this.mContext, n), 0, 0, contentDescription)));
        }
        else {
            icon.getIcon().icon = Icon.createWithResource(this.mContext, n);
            icon.getIcon().contentDescription = contentDescription;
            this.handleSet(slotIndex, icon);
        }
    }
    
    @Override
    public void setIcon(final String s, final StatusBarIcon statusBarIcon) {
        this.setIcon(this.getSlotIndex(s), statusBarIcon);
    }
    
    @Override
    public void setIconVisibility(final String s, final boolean visible) {
        final int slotIndex = this.getSlotIndex(s);
        final StatusBarIconHolder icon = this.getIcon(slotIndex, 0);
        if (icon != null) {
            if (icon.isVisible() != visible) {
                icon.setVisible(visible);
                this.handleSet(slotIndex, icon);
            }
        }
    }
    
    @Override
    public void setMobileIcons(final String s, final List<StatusBarSignalPolicy.MobileIconState> list) {
        final Slot slot = this.getSlot(s);
        final int slotIndex = this.getSlotIndex(s);
        Collections.reverse(list);
        for (final StatusBarSignalPolicy.MobileIconState mobileState : list) {
            final StatusBarIconHolder holderForTag = slot.getHolderForTag(mobileState.subId);
            if (holderForTag == null) {
                this.setIcon(slotIndex, StatusBarIconHolder.fromMobileIconState(mobileState));
            }
            else {
                holderForTag.setMobileState(mobileState);
                this.handleSet(slotIndex, holderForTag);
            }
        }
    }
    
    @Override
    public void setSignalIcon(final String s, final StatusBarSignalPolicy.WifiIconState wifiState) {
        final int slotIndex = this.getSlotIndex(s);
        if (wifiState == null) {
            this.removeIcon(slotIndex, 0);
            return;
        }
        final StatusBarIconHolder icon = this.getIcon(slotIndex, 0);
        if (icon == null) {
            this.setIcon(slotIndex, StatusBarIconHolder.fromWifiIconState(wifiState));
        }
        else {
            icon.setWifiState(wifiState);
            this.handleSet(slotIndex, icon);
        }
    }
}
