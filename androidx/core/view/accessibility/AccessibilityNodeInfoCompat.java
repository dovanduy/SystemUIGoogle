// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.view.accessibility;

import android.text.Spannable;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo$CollectionItemInfo;
import android.view.accessibility.AccessibilityNodeInfo$CollectionInfo;
import android.text.SpannableString;
import android.text.TextUtils;
import android.graphics.Rect;
import java.util.Collections;
import android.view.accessibility.AccessibilityNodeInfo$AccessibilityAction;
import android.os.Bundle;
import androidx.core.R$id;
import java.lang.ref.WeakReference;
import android.util.SparseArray;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import android.os.Build$VERSION;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.accessibility.AccessibilityNodeInfo;

public class AccessibilityNodeInfoCompat
{
    private static int sClickableSpanId;
    private final AccessibilityNodeInfo mInfo;
    public int mParentVirtualDescendantId;
    private int mVirtualDescendantId;
    
    private AccessibilityNodeInfoCompat(final AccessibilityNodeInfo mInfo) {
        this.mParentVirtualDescendantId = -1;
        this.mVirtualDescendantId = -1;
        this.mInfo = mInfo;
    }
    
    private void addSpanLocationToExtras(final ClickableSpan clickableSpan, final Spanned spanned, final int i) {
        this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").add(spanned.getSpanStart((Object)clickableSpan));
        this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY").add(spanned.getSpanEnd((Object)clickableSpan));
        this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY").add(spanned.getSpanFlags((Object)clickableSpan));
        this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY").add(i);
    }
    
    private void clearExtrasSpans() {
        if (Build$VERSION.SDK_INT >= 19) {
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
            this.mInfo.getExtras().remove("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
        }
    }
    
    private List<Integer> extrasIntList(final String s) {
        if (Build$VERSION.SDK_INT < 19) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> integerArrayList;
        if ((integerArrayList = (ArrayList<Integer>)this.mInfo.getExtras().getIntegerArrayList(s)) == null) {
            integerArrayList = new ArrayList<Integer>();
            this.mInfo.getExtras().putIntegerArrayList(s, (ArrayList)integerArrayList);
        }
        return integerArrayList;
    }
    
    private static String getActionSymbolicName(final int n) {
        if (n == 1) {
            return "ACTION_FOCUS";
        }
        if (n == 2) {
            return "ACTION_CLEAR_FOCUS";
        }
        switch (n) {
            default: {
                switch (n) {
                    default: {
                        switch (n) {
                            default: {
                                return "ACTION_UNKNOWN";
                            }
                            case 16908361: {
                                return "ACTION_PAGE_RIGHT";
                            }
                            case 16908360: {
                                return "ACTION_PAGE_LEFT";
                            }
                            case 16908359: {
                                return "ACTION_PAGE_DOWN";
                            }
                            case 16908358: {
                                return "ACTION_PAGE_UP";
                            }
                            case 16908357: {
                                return "ACTION_HIDE_TOOLTIP";
                            }
                            case 16908356: {
                                return "ACTION_SHOW_TOOLTIP";
                            }
                        }
                        break;
                    }
                    case 16908349: {
                        return "ACTION_SET_PROGRESS";
                    }
                    case 16908348: {
                        return "ACTION_CONTEXT_CLICK";
                    }
                    case 16908347: {
                        return "ACTION_SCROLL_RIGHT";
                    }
                    case 16908346: {
                        return "ACTION_SCROLL_DOWN";
                    }
                    case 16908345: {
                        return "ACTION_SCROLL_LEFT";
                    }
                    case 16908344: {
                        return "ACTION_SCROLL_UP";
                    }
                    case 16908343: {
                        return "ACTION_SCROLL_TO_POSITION";
                    }
                    case 16908342: {
                        return "ACTION_SHOW_ON_SCREEN";
                    }
                }
                break;
            }
            case 16908354: {
                return "ACTION_MOVE_WINDOW";
            }
            case 2097152: {
                return "ACTION_SET_TEXT";
            }
            case 524288: {
                return "ACTION_COLLAPSE";
            }
            case 262144: {
                return "ACTION_EXPAND";
            }
            case 131072: {
                return "ACTION_SET_SELECTION";
            }
            case 65536: {
                return "ACTION_CUT";
            }
            case 32768: {
                return "ACTION_PASTE";
            }
            case 16384: {
                return "ACTION_COPY";
            }
            case 8192: {
                return "ACTION_SCROLL_BACKWARD";
            }
            case 4096: {
                return "ACTION_SCROLL_FORWARD";
            }
            case 2048: {
                return "ACTION_PREVIOUS_HTML_ELEMENT";
            }
            case 1024: {
                return "ACTION_NEXT_HTML_ELEMENT";
            }
            case 512: {
                return "ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY";
            }
            case 256: {
                return "ACTION_NEXT_AT_MOVEMENT_GRANULARITY";
            }
            case 128: {
                return "ACTION_CLEAR_ACCESSIBILITY_FOCUS";
            }
            case 64: {
                return "ACTION_ACCESSIBILITY_FOCUS";
            }
            case 32: {
                return "ACTION_LONG_CLICK";
            }
            case 16: {
                return "ACTION_CLICK";
            }
            case 8: {
                return "ACTION_CLEAR_SELECTION";
            }
            case 4: {
                return "ACTION_SELECT";
            }
        }
    }
    
    public static ClickableSpan[] getClickableSpans(final CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            return (ClickableSpan[])((Spanned)charSequence).getSpans(0, charSequence.length(), (Class)ClickableSpan.class);
        }
        return null;
    }
    
    private SparseArray<WeakReference<ClickableSpan>> getOrCreateSpansFromViewTags(final View view) {
        SparseArray spansFromViewTags;
        if ((spansFromViewTags = this.getSpansFromViewTags(view)) == null) {
            spansFromViewTags = new SparseArray();
            view.setTag(R$id.tag_accessibility_clickable_spans, (Object)spansFromViewTags);
        }
        return (SparseArray<WeakReference<ClickableSpan>>)spansFromViewTags;
    }
    
    private SparseArray<WeakReference<ClickableSpan>> getSpansFromViewTags(final View view) {
        return (SparseArray<WeakReference<ClickableSpan>>)view.getTag(R$id.tag_accessibility_clickable_spans);
    }
    
    private boolean hasSpans() {
        return this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY").isEmpty() ^ true;
    }
    
    private int idForClickableSpan(final ClickableSpan clickableSpan, final SparseArray<WeakReference<ClickableSpan>> sparseArray) {
        if (sparseArray != null) {
            for (int i = 0; i < sparseArray.size(); ++i) {
                if (clickableSpan.equals(((WeakReference)sparseArray.valueAt(i)).get())) {
                    return sparseArray.keyAt(i);
                }
            }
        }
        final int sClickableSpanId = AccessibilityNodeInfoCompat.sClickableSpanId;
        AccessibilityNodeInfoCompat.sClickableSpanId = sClickableSpanId + 1;
        return sClickableSpanId;
    }
    
    public static AccessibilityNodeInfoCompat obtain(final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        return wrap(AccessibilityNodeInfo.obtain(accessibilityNodeInfoCompat.mInfo));
    }
    
    private void removeCollectedSpans(final View view) {
        final SparseArray<WeakReference<ClickableSpan>> spansFromViewTags = this.getSpansFromViewTags(view);
        if (spansFromViewTags != null) {
            final ArrayList<Integer> list = new ArrayList<Integer>();
            final int n = 0;
            int i = 0;
            int j;
            while (true) {
                j = n;
                if (i >= spansFromViewTags.size()) {
                    break;
                }
                if (((WeakReference)spansFromViewTags.valueAt(i)).get() == null) {
                    list.add(i);
                }
                ++i;
            }
            while (j < list.size()) {
                spansFromViewTags.remove((int)list.get(j));
                ++j;
            }
        }
    }
    
    private void setBooleanProperty(final int n, final boolean b) {
        final Bundle extras = this.getExtras();
        if (extras != null) {
            final int int1 = extras.getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY", 0);
            int n2;
            if (b) {
                n2 = n;
            }
            else {
                n2 = 0;
            }
            extras.putInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.BOOLEAN_PROPERTY_KEY", n2 | (int1 & n));
        }
    }
    
    public static AccessibilityNodeInfoCompat wrap(final AccessibilityNodeInfo accessibilityNodeInfo) {
        return new AccessibilityNodeInfoCompat(accessibilityNodeInfo);
    }
    
    public void addAction(final int n) {
        this.mInfo.addAction(n);
    }
    
    public void addAction(final AccessibilityActionCompat accessibilityActionCompat) {
        if (Build$VERSION.SDK_INT >= 21) {
            this.mInfo.addAction((AccessibilityNodeInfo$AccessibilityAction)accessibilityActionCompat.mAction);
        }
    }
    
    public void addChild(final View view) {
        this.mInfo.addChild(view);
    }
    
    public void addSpansToExtras(final CharSequence charSequence, final View view) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 19 && sdk_INT < 26) {
            this.clearExtrasSpans();
            this.removeCollectedSpans(view);
            final ClickableSpan[] clickableSpans = getClickableSpans(charSequence);
            if (clickableSpans != null && clickableSpans.length > 0) {
                this.getExtras().putInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY", R$id.accessibility_action_clickable_span);
                final SparseArray<WeakReference<ClickableSpan>> orCreateSpansFromViewTags = this.getOrCreateSpansFromViewTags(view);
                for (int n = 0; clickableSpans != null && n < clickableSpans.length; ++n) {
                    final int idForClickableSpan = this.idForClickableSpan(clickableSpans[n], orCreateSpansFromViewTags);
                    orCreateSpansFromViewTags.put(idForClickableSpan, (Object)new WeakReference(clickableSpans[n]));
                    this.addSpanLocationToExtras(clickableSpans[n], (Spanned)charSequence, idForClickableSpan);
                }
            }
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof AccessibilityNodeInfoCompat)) {
            return false;
        }
        final AccessibilityNodeInfoCompat accessibilityNodeInfoCompat = (AccessibilityNodeInfoCompat)o;
        final AccessibilityNodeInfo mInfo = this.mInfo;
        if (mInfo == null) {
            if (accessibilityNodeInfoCompat.mInfo != null) {
                return false;
            }
        }
        else if (!mInfo.equals((Object)accessibilityNodeInfoCompat.mInfo)) {
            return false;
        }
        return this.mVirtualDescendantId == accessibilityNodeInfoCompat.mVirtualDescendantId && this.mParentVirtualDescendantId == accessibilityNodeInfoCompat.mParentVirtualDescendantId;
    }
    
    public List<AccessibilityActionCompat> getActionList() {
        List<Object> actionList;
        if (Build$VERSION.SDK_INT >= 21) {
            actionList = (List<Object>)this.mInfo.getActionList();
        }
        else {
            actionList = null;
        }
        if (actionList != null) {
            final ArrayList<AccessibilityActionCompat> list = new ArrayList<AccessibilityActionCompat>();
            for (int size = actionList.size(), i = 0; i < size; ++i) {
                list.add(new AccessibilityActionCompat(actionList.get(i)));
            }
            return list;
        }
        return Collections.emptyList();
    }
    
    public int getActions() {
        return this.mInfo.getActions();
    }
    
    @Deprecated
    public void getBoundsInParent(final Rect rect) {
        this.mInfo.getBoundsInParent(rect);
    }
    
    public void getBoundsInScreen(final Rect rect) {
        this.mInfo.getBoundsInScreen(rect);
    }
    
    public CharSequence getClassName() {
        return this.mInfo.getClassName();
    }
    
    public CharSequence getContentDescription() {
        return this.mInfo.getContentDescription();
    }
    
    public Bundle getExtras() {
        if (Build$VERSION.SDK_INT >= 19) {
            return this.mInfo.getExtras();
        }
        return new Bundle();
    }
    
    public int getMovementGranularities() {
        if (Build$VERSION.SDK_INT >= 16) {
            return this.mInfo.getMovementGranularities();
        }
        return 0;
    }
    
    public CharSequence getPackageName() {
        return this.mInfo.getPackageName();
    }
    
    public CharSequence getText() {
        if (this.hasSpans()) {
            final List<Integer> extrasIntList = this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_START_KEY");
            final List<Integer> extrasIntList2 = this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_END_KEY");
            final List<Integer> extrasIntList3 = this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_FLAGS_KEY");
            final List<Integer> extrasIntList4 = this.extrasIntList("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ID_KEY");
            final CharSequence text = this.mInfo.getText();
            final int length = this.mInfo.getText().length();
            int i = 0;
            final SpannableString spannableString = new SpannableString((CharSequence)TextUtils.substring(text, 0, length));
            while (i < extrasIntList.size()) {
                ((Spannable)spannableString).setSpan((Object)new AccessibilityClickableSpanCompat(extrasIntList4.get(i), this, this.getExtras().getInt("androidx.view.accessibility.AccessibilityNodeInfoCompat.SPANS_ACTION_ID_KEY")), (int)extrasIntList.get(i), (int)extrasIntList2.get(i), (int)extrasIntList3.get(i));
                ++i;
            }
            return (CharSequence)spannableString;
        }
        return this.mInfo.getText();
    }
    
    public String getViewIdResourceName() {
        if (Build$VERSION.SDK_INT >= 18) {
            return this.mInfo.getViewIdResourceName();
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        final AccessibilityNodeInfo mInfo = this.mInfo;
        int hashCode;
        if (mInfo == null) {
            hashCode = 0;
        }
        else {
            hashCode = mInfo.hashCode();
        }
        return hashCode;
    }
    
    public boolean isAccessibilityFocused() {
        return Build$VERSION.SDK_INT >= 16 && this.mInfo.isAccessibilityFocused();
    }
    
    public boolean isCheckable() {
        return this.mInfo.isCheckable();
    }
    
    public boolean isChecked() {
        return this.mInfo.isChecked();
    }
    
    public boolean isClickable() {
        return this.mInfo.isClickable();
    }
    
    public boolean isEnabled() {
        return this.mInfo.isEnabled();
    }
    
    public boolean isFocusable() {
        return this.mInfo.isFocusable();
    }
    
    public boolean isFocused() {
        return this.mInfo.isFocused();
    }
    
    public boolean isLongClickable() {
        return this.mInfo.isLongClickable();
    }
    
    public boolean isPassword() {
        return this.mInfo.isPassword();
    }
    
    public boolean isScrollable() {
        return this.mInfo.isScrollable();
    }
    
    public boolean isSelected() {
        return this.mInfo.isSelected();
    }
    
    public boolean isVisibleToUser() {
        return Build$VERSION.SDK_INT >= 16 && this.mInfo.isVisibleToUser();
    }
    
    public boolean performAction(final int n, final Bundle bundle) {
        return Build$VERSION.SDK_INT >= 16 && this.mInfo.performAction(n, bundle);
    }
    
    public void recycle() {
        this.mInfo.recycle();
    }
    
    public boolean removeAction(final AccessibilityActionCompat accessibilityActionCompat) {
        return Build$VERSION.SDK_INT >= 21 && this.mInfo.removeAction((AccessibilityNodeInfo$AccessibilityAction)accessibilityActionCompat.mAction);
    }
    
    public void setAccessibilityFocused(final boolean accessibilityFocused) {
        if (Build$VERSION.SDK_INT >= 16) {
            this.mInfo.setAccessibilityFocused(accessibilityFocused);
        }
    }
    
    public void setBoundsInScreen(final Rect boundsInScreen) {
        this.mInfo.setBoundsInScreen(boundsInScreen);
    }
    
    public void setCanOpenPopup(final boolean canOpenPopup) {
        if (Build$VERSION.SDK_INT >= 19) {
            this.mInfo.setCanOpenPopup(canOpenPopup);
        }
    }
    
    public void setClassName(final CharSequence className) {
        this.mInfo.setClassName(className);
    }
    
    public void setClickable(final boolean clickable) {
        this.mInfo.setClickable(clickable);
    }
    
    public void setCollectionInfo(final Object o) {
        if (Build$VERSION.SDK_INT >= 19) {
            final AccessibilityNodeInfo mInfo = this.mInfo;
            AccessibilityNodeInfo$CollectionInfo collectionInfo;
            if (o == null) {
                collectionInfo = null;
            }
            else {
                collectionInfo = (AccessibilityNodeInfo$CollectionInfo)((CollectionInfoCompat)o).mInfo;
            }
            mInfo.setCollectionInfo(collectionInfo);
        }
    }
    
    public void setCollectionItemInfo(final Object o) {
        if (Build$VERSION.SDK_INT >= 19) {
            final AccessibilityNodeInfo mInfo = this.mInfo;
            AccessibilityNodeInfo$CollectionItemInfo collectionItemInfo;
            if (o == null) {
                collectionItemInfo = null;
            }
            else {
                collectionItemInfo = (AccessibilityNodeInfo$CollectionItemInfo)((CollectionItemInfoCompat)o).mInfo;
            }
            mInfo.setCollectionItemInfo(collectionItemInfo);
        }
    }
    
    public void setContentDescription(final CharSequence contentDescription) {
        this.mInfo.setContentDescription(contentDescription);
    }
    
    public void setEnabled(final boolean enabled) {
        this.mInfo.setEnabled(enabled);
    }
    
    public void setFocusable(final boolean focusable) {
        this.mInfo.setFocusable(focusable);
    }
    
    public void setFocused(final boolean focused) {
        this.mInfo.setFocused(focused);
    }
    
    public void setHeading(final boolean heading) {
        if (Build$VERSION.SDK_INT >= 28) {
            this.mInfo.setHeading(heading);
        }
        else {
            this.setBooleanProperty(2, heading);
        }
    }
    
    public void setLongClickable(final boolean longClickable) {
        this.mInfo.setLongClickable(longClickable);
    }
    
    public void setMovementGranularities(final int movementGranularities) {
        if (Build$VERSION.SDK_INT >= 16) {
            this.mInfo.setMovementGranularities(movementGranularities);
        }
    }
    
    public void setPackageName(final CharSequence packageName) {
        this.mInfo.setPackageName(packageName);
    }
    
    public void setPaneTitle(final CharSequence paneTitle) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 28) {
            this.mInfo.setPaneTitle(paneTitle);
        }
        else if (sdk_INT >= 19) {
            this.mInfo.getExtras().putCharSequence("androidx.view.accessibility.AccessibilityNodeInfoCompat.PANE_TITLE_KEY", paneTitle);
        }
    }
    
    public void setParent(final View parent) {
        this.mParentVirtualDescendantId = -1;
        this.mInfo.setParent(parent);
    }
    
    public void setScreenReaderFocusable(final boolean screenReaderFocusable) {
        if (Build$VERSION.SDK_INT >= 28) {
            this.mInfo.setScreenReaderFocusable(screenReaderFocusable);
        }
        else {
            this.setBooleanProperty(1, screenReaderFocusable);
        }
    }
    
    public void setScrollable(final boolean scrollable) {
        this.mInfo.setScrollable(scrollable);
    }
    
    public void setSelected(final boolean selected) {
        this.mInfo.setSelected(selected);
    }
    
    public void setSource(final View source) {
        this.mVirtualDescendantId = -1;
        this.mInfo.setSource(source);
    }
    
    public void setVisibleToUser(final boolean visibleToUser) {
        if (Build$VERSION.SDK_INT >= 16) {
            this.mInfo.setVisibleToUser(visibleToUser);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        final Rect rect = new Rect();
        this.getBoundsInParent(rect);
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("; boundsInParent: ");
        sb2.append(rect);
        sb.append(sb2.toString());
        this.getBoundsInScreen(rect);
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("; boundsInScreen: ");
        sb3.append(rect);
        sb.append(sb3.toString());
        sb.append("; packageName: ");
        sb.append(this.getPackageName());
        sb.append("; className: ");
        sb.append(this.getClassName());
        sb.append("; text: ");
        sb.append(this.getText());
        sb.append("; contentDescription: ");
        sb.append(this.getContentDescription());
        sb.append("; viewId: ");
        sb.append(this.getViewIdResourceName());
        sb.append("; checkable: ");
        sb.append(this.isCheckable());
        sb.append("; checked: ");
        sb.append(this.isChecked());
        sb.append("; focusable: ");
        sb.append(this.isFocusable());
        sb.append("; focused: ");
        sb.append(this.isFocused());
        sb.append("; selected: ");
        sb.append(this.isSelected());
        sb.append("; clickable: ");
        sb.append(this.isClickable());
        sb.append("; longClickable: ");
        sb.append(this.isLongClickable());
        sb.append("; enabled: ");
        sb.append(this.isEnabled());
        sb.append("; password: ");
        sb.append(this.isPassword());
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("; scrollable: ");
        sb4.append(this.isScrollable());
        sb.append(sb4.toString());
        sb.append("; [");
        if (Build$VERSION.SDK_INT >= 21) {
            final List<AccessibilityActionCompat> actionList = this.getActionList();
            for (int i = 0; i < actionList.size(); ++i) {
                final AccessibilityActionCompat accessibilityActionCompat = actionList.get(i);
                String str;
                final String s = str = getActionSymbolicName(accessibilityActionCompat.getId());
                if (s.equals("ACTION_UNKNOWN")) {
                    str = s;
                    if (accessibilityActionCompat.getLabel() != null) {
                        str = accessibilityActionCompat.getLabel().toString();
                    }
                }
                sb.append(str);
                if (i != actionList.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        else {
            int n2;
            for (int j = this.getActions(); j != 0; j = n2) {
                final int n = 1 << Integer.numberOfTrailingZeros(j);
                n2 = (j & n);
                sb.append(getActionSymbolicName(n));
                if ((j = n2) != 0) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    public AccessibilityNodeInfo unwrap() {
        return this.mInfo;
    }
    
    public static class AccessibilityActionCompat
    {
        public static final AccessibilityActionCompat ACTION_CLEAR_FOCUS;
        public static final AccessibilityActionCompat ACTION_CLICK;
        public static final AccessibilityActionCompat ACTION_DISMISS;
        public static final AccessibilityActionCompat ACTION_FOCUS;
        public static final AccessibilityActionCompat ACTION_SCROLL_BACKWARD;
        public static final AccessibilityActionCompat ACTION_SCROLL_DOWN;
        public static final AccessibilityActionCompat ACTION_SCROLL_FORWARD;
        public static final AccessibilityActionCompat ACTION_SCROLL_LEFT;
        public static final AccessibilityActionCompat ACTION_SCROLL_RIGHT;
        public static final AccessibilityActionCompat ACTION_SCROLL_UP;
        final Object mAction;
        protected final AccessibilityViewCommand mCommand;
        private final int mId;
        private final Class<? extends AccessibilityViewCommand.CommandArguments> mViewCommandArgumentClass;
        
        static {
            final int sdk_INT = Build$VERSION.SDK_INT;
            final Object o = null;
            ACTION_FOCUS = new AccessibilityActionCompat(1, null);
            ACTION_CLEAR_FOCUS = new AccessibilityActionCompat(2, null);
            new AccessibilityActionCompat(4, null);
            new AccessibilityActionCompat(8, null);
            ACTION_CLICK = new AccessibilityActionCompat(16, null);
            new AccessibilityActionCompat(32, null);
            new AccessibilityActionCompat(64, null);
            new AccessibilityActionCompat(128, null);
            new AccessibilityActionCompat(256, null, AccessibilityViewCommand.MoveAtGranularityArguments.class);
            new AccessibilityActionCompat(512, null, AccessibilityViewCommand.MoveAtGranularityArguments.class);
            new AccessibilityActionCompat(1024, null, AccessibilityViewCommand.MoveHtmlArguments.class);
            new AccessibilityActionCompat(2048, null, AccessibilityViewCommand.MoveHtmlArguments.class);
            ACTION_SCROLL_FORWARD = new AccessibilityActionCompat(4096, null);
            ACTION_SCROLL_BACKWARD = new AccessibilityActionCompat(8192, null);
            new AccessibilityActionCompat(16384, null);
            new AccessibilityActionCompat(32768, null);
            new AccessibilityActionCompat(65536, null);
            new AccessibilityActionCompat(131072, null, AccessibilityViewCommand.SetSelectionArguments.class);
            new AccessibilityActionCompat(262144, null);
            new AccessibilityActionCompat(524288, null);
            ACTION_DISMISS = new AccessibilityActionCompat(1048576, null);
            new AccessibilityActionCompat(2097152, null, AccessibilityViewCommand.SetTextArguments.class);
            AccessibilityNodeInfo$AccessibilityAction action_SHOW_ON_SCREEN;
            if (sdk_INT >= 23) {
                action_SHOW_ON_SCREEN = AccessibilityNodeInfo$AccessibilityAction.ACTION_SHOW_ON_SCREEN;
            }
            else {
                action_SHOW_ON_SCREEN = null;
            }
            new AccessibilityActionCompat(action_SHOW_ON_SCREEN, 16908342, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_SCROLL_TO_POSITION;
            if (sdk_INT >= 23) {
                action_SCROLL_TO_POSITION = AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_TO_POSITION;
            }
            else {
                action_SCROLL_TO_POSITION = null;
            }
            new AccessibilityActionCompat(action_SCROLL_TO_POSITION, 16908343, null, null, AccessibilityViewCommand.ScrollToPositionArguments.class);
            AccessibilityNodeInfo$AccessibilityAction action_SCROLL_UP;
            if (sdk_INT >= 23) {
                action_SCROLL_UP = AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_UP;
            }
            else {
                action_SCROLL_UP = null;
            }
            ACTION_SCROLL_UP = new AccessibilityActionCompat(action_SCROLL_UP, 16908344, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_SCROLL_LEFT;
            if (sdk_INT >= 23) {
                action_SCROLL_LEFT = AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_LEFT;
            }
            else {
                action_SCROLL_LEFT = null;
            }
            ACTION_SCROLL_LEFT = new AccessibilityActionCompat(action_SCROLL_LEFT, 16908345, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_SCROLL_DOWN;
            if (sdk_INT >= 23) {
                action_SCROLL_DOWN = AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_DOWN;
            }
            else {
                action_SCROLL_DOWN = null;
            }
            ACTION_SCROLL_DOWN = new AccessibilityActionCompat(action_SCROLL_DOWN, 16908346, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_SCROLL_RIGHT;
            if (sdk_INT >= 23) {
                action_SCROLL_RIGHT = AccessibilityNodeInfo$AccessibilityAction.ACTION_SCROLL_RIGHT;
            }
            else {
                action_SCROLL_RIGHT = null;
            }
            ACTION_SCROLL_RIGHT = new AccessibilityActionCompat(action_SCROLL_RIGHT, 16908347, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_PAGE_UP;
            if (sdk_INT >= 29) {
                action_PAGE_UP = AccessibilityNodeInfo$AccessibilityAction.ACTION_PAGE_UP;
            }
            else {
                action_PAGE_UP = null;
            }
            new AccessibilityActionCompat(action_PAGE_UP, 16908358, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_PAGE_DOWN;
            if (sdk_INT >= 29) {
                action_PAGE_DOWN = AccessibilityNodeInfo$AccessibilityAction.ACTION_PAGE_DOWN;
            }
            else {
                action_PAGE_DOWN = null;
            }
            new AccessibilityActionCompat(action_PAGE_DOWN, 16908359, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_PAGE_LEFT;
            if (sdk_INT >= 29) {
                action_PAGE_LEFT = AccessibilityNodeInfo$AccessibilityAction.ACTION_PAGE_LEFT;
            }
            else {
                action_PAGE_LEFT = null;
            }
            new AccessibilityActionCompat(action_PAGE_LEFT, 16908360, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_PAGE_RIGHT;
            if (sdk_INT >= 29) {
                action_PAGE_RIGHT = AccessibilityNodeInfo$AccessibilityAction.ACTION_PAGE_RIGHT;
            }
            else {
                action_PAGE_RIGHT = null;
            }
            new AccessibilityActionCompat(action_PAGE_RIGHT, 16908361, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_CONTEXT_CLICK;
            if (sdk_INT >= 23) {
                action_CONTEXT_CLICK = AccessibilityNodeInfo$AccessibilityAction.ACTION_CONTEXT_CLICK;
            }
            else {
                action_CONTEXT_CLICK = null;
            }
            new AccessibilityActionCompat(action_CONTEXT_CLICK, 16908348, null, null, null);
            AccessibilityNodeInfo$AccessibilityAction action_SET_PROGRESS;
            if (sdk_INT >= 24) {
                action_SET_PROGRESS = AccessibilityNodeInfo$AccessibilityAction.ACTION_SET_PROGRESS;
            }
            else {
                action_SET_PROGRESS = null;
            }
            new AccessibilityActionCompat(action_SET_PROGRESS, 16908349, null, null, AccessibilityViewCommand.SetProgressArguments.class);
            AccessibilityNodeInfo$AccessibilityAction action_MOVE_WINDOW;
            if (sdk_INT >= 26) {
                action_MOVE_WINDOW = AccessibilityNodeInfo$AccessibilityAction.ACTION_MOVE_WINDOW;
            }
            else {
                action_MOVE_WINDOW = null;
            }
            new AccessibilityActionCompat(action_MOVE_WINDOW, 16908354, null, null, AccessibilityViewCommand.MoveWindowArguments.class);
            AccessibilityNodeInfo$AccessibilityAction action_SHOW_TOOLTIP;
            if (sdk_INT >= 28) {
                action_SHOW_TOOLTIP = AccessibilityNodeInfo$AccessibilityAction.ACTION_SHOW_TOOLTIP;
            }
            else {
                action_SHOW_TOOLTIP = null;
            }
            new AccessibilityActionCompat(action_SHOW_TOOLTIP, 16908356, null, null, null);
            Object action_HIDE_TOOLTIP = o;
            if (sdk_INT >= 28) {
                action_HIDE_TOOLTIP = AccessibilityNodeInfo$AccessibilityAction.ACTION_HIDE_TOOLTIP;
            }
            new AccessibilityActionCompat(action_HIDE_TOOLTIP, 16908357, null, null, null);
        }
        
        public AccessibilityActionCompat(final int n, final CharSequence charSequence) {
            this(null, n, charSequence, null, null);
        }
        
        private AccessibilityActionCompat(final int n, final CharSequence charSequence, final Class<? extends AccessibilityViewCommand.CommandArguments> clazz) {
            this(null, n, charSequence, null, clazz);
        }
        
        AccessibilityActionCompat(final Object o) {
            this(o, 0, null, null, null);
        }
        
        AccessibilityActionCompat(final Object mAction, final int mId, final CharSequence charSequence, final AccessibilityViewCommand mCommand, final Class<? extends AccessibilityViewCommand.CommandArguments> mViewCommandArgumentClass) {
            this.mId = mId;
            this.mCommand = mCommand;
            if (Build$VERSION.SDK_INT >= 21 && mAction == null) {
                this.mAction = new AccessibilityNodeInfo$AccessibilityAction(mId, charSequence);
            }
            else {
                this.mAction = mAction;
            }
            this.mViewCommandArgumentClass = mViewCommandArgumentClass;
        }
        
        public AccessibilityActionCompat createReplacementAction(final CharSequence charSequence, final AccessibilityViewCommand accessibilityViewCommand) {
            return new AccessibilityActionCompat(null, this.mId, charSequence, accessibilityViewCommand, this.mViewCommandArgumentClass);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof AccessibilityActionCompat)) {
                return false;
            }
            final AccessibilityActionCompat accessibilityActionCompat = (AccessibilityActionCompat)o;
            final Object mAction = this.mAction;
            if (mAction == null) {
                if (accessibilityActionCompat.mAction != null) {
                    return false;
                }
            }
            else if (!mAction.equals(accessibilityActionCompat.mAction)) {
                return false;
            }
            return true;
        }
        
        public int getId() {
            if (Build$VERSION.SDK_INT >= 21) {
                return ((AccessibilityNodeInfo$AccessibilityAction)this.mAction).getId();
            }
            return 0;
        }
        
        public CharSequence getLabel() {
            if (Build$VERSION.SDK_INT >= 21) {
                return ((AccessibilityNodeInfo$AccessibilityAction)this.mAction).getLabel();
            }
            return null;
        }
        
        @Override
        public int hashCode() {
            final Object mAction = this.mAction;
            int hashCode;
            if (mAction != null) {
                hashCode = mAction.hashCode();
            }
            else {
                hashCode = 0;
            }
            return hashCode;
        }
        
        public boolean perform(final View view, final Bundle bundle) {
            if (this.mCommand != null) {
                Object o = null;
                final Exception ex = null;
                final Class<? extends AccessibilityViewCommand.CommandArguments> mViewCommandArgumentClass = this.mViewCommandArgumentClass;
                if (mViewCommandArgumentClass != null) {
                    Object o2;
                    Exception ex2 = null;
                    try {
                        o = (AccessibilityViewCommand.CommandArguments)mViewCommandArgumentClass.getDeclaredConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                        try {
                            ((AccessibilityViewCommand.CommandArguments)o).setBundle(bundle);
                        }
                        catch (Exception ex) {
                            o2 = o;
                            ex2 = ex;
                        }
                    }
                    catch (Exception ex2) {
                        o2 = ex;
                    }
                    final Class<? extends AccessibilityViewCommand.CommandArguments> mViewCommandArgumentClass2 = this.mViewCommandArgumentClass;
                    String name;
                    if (mViewCommandArgumentClass2 == null) {
                        name = "null";
                    }
                    else {
                        name = mViewCommandArgumentClass2.getName();
                    }
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Failed to execute command with argument class ViewCommandArgument: ");
                    sb.append(name);
                    Log.e("A11yActionCompat", sb.toString(), (Throwable)ex2);
                    o = o2;
                }
                return this.mCommand.perform(view, (AccessibilityViewCommand.CommandArguments)o);
            }
            return false;
        }
    }
    
    public static class CollectionInfoCompat
    {
        final Object mInfo;
        
        CollectionInfoCompat(final Object mInfo) {
            this.mInfo = mInfo;
        }
        
        public static CollectionInfoCompat obtain(final int n, final int n2, final boolean b, final int n3) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 21) {
                return new CollectionInfoCompat(AccessibilityNodeInfo$CollectionInfo.obtain(n, n2, b, n3));
            }
            if (sdk_INT >= 19) {
                return new CollectionInfoCompat(AccessibilityNodeInfo$CollectionInfo.obtain(n, n2, b));
            }
            return new CollectionInfoCompat(null);
        }
    }
    
    public static class CollectionItemInfoCompat
    {
        final Object mInfo;
        
        CollectionItemInfoCompat(final Object mInfo) {
            this.mInfo = mInfo;
        }
        
        public static CollectionItemInfoCompat obtain(final int n, final int n2, final int n3, final int n4, final boolean b, final boolean b2) {
            final int sdk_INT = Build$VERSION.SDK_INT;
            if (sdk_INT >= 21) {
                return new CollectionItemInfoCompat(AccessibilityNodeInfo$CollectionItemInfo.obtain(n, n2, n3, n4, b, b2));
            }
            if (sdk_INT >= 19) {
                return new CollectionItemInfoCompat(AccessibilityNodeInfo$CollectionItemInfo.obtain(n, n2, n3, n4, b));
            }
            return new CollectionItemInfoCompat(null);
        }
    }
}
