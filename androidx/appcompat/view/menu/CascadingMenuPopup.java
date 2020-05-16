// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import androidx.core.view.GravityCompat;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.FrameLayout;
import android.os.Build$VERSION;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.graphics.Rect;
import androidx.core.view.ViewCompat;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.HeaderViewListAdapter;
import android.widget.AdapterView$OnItemClickListener;
import android.util.AttributeSet;
import androidx.appcompat.widget.MenuPopupWindow;
import android.content.res.Resources;
import androidx.appcompat.R$dimen;
import android.os.SystemClock;
import android.view.MenuItem;
import java.util.Iterator;
import java.util.ArrayList;
import androidx.appcompat.R$layout;
import android.view.ViewTreeObserver;
import android.os.Handler;
import java.util.List;
import androidx.appcompat.widget.MenuItemHoverListener;
import android.view.ViewTreeObserver$OnGlobalLayoutListener;
import android.content.Context;
import android.view.View$OnAttachStateChangeListener;
import android.view.View;
import android.widget.PopupWindow$OnDismissListener;
import android.view.View$OnKeyListener;

final class CascadingMenuPopup extends MenuPopup implements MenuPresenter, View$OnKeyListener, PopupWindow$OnDismissListener
{
    private static final int ITEM_LAYOUT;
    private View mAnchorView;
    private final View$OnAttachStateChangeListener mAttachStateChangeListener;
    private final Context mContext;
    private int mDropDownGravity;
    private boolean mForceShowIcon;
    final ViewTreeObserver$OnGlobalLayoutListener mGlobalLayoutListener;
    private boolean mHasXOffset;
    private boolean mHasYOffset;
    private int mLastPosition;
    private final MenuItemHoverListener mMenuItemHoverListener;
    private final int mMenuMaxWidth;
    private PopupWindow$OnDismissListener mOnDismissListener;
    private final boolean mOverflowOnly;
    private final List<MenuBuilder> mPendingMenus;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;
    private Callback mPresenterCallback;
    private int mRawDropDownGravity;
    boolean mShouldCloseImmediately;
    private boolean mShowTitle;
    final List<CascadingMenuInfo> mShowingMenus;
    View mShownAnchorView;
    final Handler mSubMenuHoverHandler;
    ViewTreeObserver mTreeObserver;
    private int mXOffset;
    private int mYOffset;
    
    static {
        ITEM_LAYOUT = R$layout.abc_cascading_menu_item_layout;
    }
    
    public CascadingMenuPopup(final Context mContext, final View mAnchorView, final int mPopupStyleAttr, final int mPopupStyleRes, final boolean mOverflowOnly) {
        this.mPendingMenus = new ArrayList<MenuBuilder>();
        this.mShowingMenus = new ArrayList<CascadingMenuInfo>();
        this.mGlobalLayoutListener = (ViewTreeObserver$OnGlobalLayoutListener)new ViewTreeObserver$OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if (CascadingMenuPopup.this.isShowing() && CascadingMenuPopup.this.mShowingMenus.size() > 0 && !((CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(0)).window.isModal()) {
                    final View mShownAnchorView = CascadingMenuPopup.this.mShownAnchorView;
                    if (mShownAnchorView != null && mShownAnchorView.isShown()) {
                        final Iterator<CascadingMenuInfo> iterator = CascadingMenuPopup.this.mShowingMenus.iterator();
                        while (iterator.hasNext()) {
                            ((CascadingMenuInfo)iterator.next()).window.show();
                        }
                    }
                    else {
                        CascadingMenuPopup.this.dismiss();
                    }
                }
            }
        };
        this.mAttachStateChangeListener = (View$OnAttachStateChangeListener)new View$OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(final View view) {
            }
            
            public void onViewDetachedFromWindow(final View view) {
                final ViewTreeObserver mTreeObserver = CascadingMenuPopup.this.mTreeObserver;
                if (mTreeObserver != null) {
                    if (!mTreeObserver.isAlive()) {
                        CascadingMenuPopup.this.mTreeObserver = view.getViewTreeObserver();
                    }
                    final CascadingMenuPopup this$0 = CascadingMenuPopup.this;
                    this$0.mTreeObserver.removeGlobalOnLayoutListener(this$0.mGlobalLayoutListener);
                }
                view.removeOnAttachStateChangeListener((View$OnAttachStateChangeListener)this);
            }
        };
        this.mMenuItemHoverListener = new MenuItemHoverListener() {
            @Override
            public void onItemHoverEnter(final MenuBuilder menuBuilder, final MenuItem menuItem) {
                final Handler mSubMenuHoverHandler = CascadingMenuPopup.this.mSubMenuHoverHandler;
                CascadingMenuInfo cascadingMenuInfo = null;
                mSubMenuHoverHandler.removeCallbacksAndMessages((Object)null);
                final int size = CascadingMenuPopup.this.mShowingMenus.size();
                int i = 0;
                while (true) {
                    while (i < size) {
                        if (menuBuilder == ((CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(i)).menu) {
                            if (i == -1) {
                                return;
                            }
                            if (++i < CascadingMenuPopup.this.mShowingMenus.size()) {
                                cascadingMenuInfo = (CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(i);
                            }
                            CascadingMenuPopup.this.mSubMenuHoverHandler.postAtTime((Runnable)new Runnable() {
                                @Override
                                public void run() {
                                    final CascadingMenuInfo val$nextInfo = cascadingMenuInfo;
                                    if (val$nextInfo != null) {
                                        CascadingMenuPopup.this.mShouldCloseImmediately = true;
                                        val$nextInfo.menu.close(false);
                                        CascadingMenuPopup.this.mShouldCloseImmediately = false;
                                    }
                                    if (menuItem.isEnabled() && menuItem.hasSubMenu()) {
                                        menuBuilder.performItemAction(menuItem, 4);
                                    }
                                }
                            }, (Object)menuBuilder, SystemClock.uptimeMillis() + 200L);
                            return;
                        }
                        else {
                            ++i;
                        }
                    }
                    i = -1;
                    continue;
                }
            }
            
            @Override
            public void onItemHoverExit(final MenuBuilder menuBuilder, final MenuItem menuItem) {
                CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages((Object)menuBuilder);
            }
        };
        this.mRawDropDownGravity = 0;
        this.mDropDownGravity = 0;
        this.mContext = mContext;
        this.mAnchorView = mAnchorView;
        this.mPopupStyleAttr = mPopupStyleAttr;
        this.mPopupStyleRes = mPopupStyleRes;
        this.mOverflowOnly = mOverflowOnly;
        this.mForceShowIcon = false;
        this.mLastPosition = this.getInitialMenuPosition();
        final Resources resources = mContext.getResources();
        this.mMenuMaxWidth = Math.max(resources.getDisplayMetrics().widthPixels / 2, resources.getDimensionPixelSize(R$dimen.abc_config_prefDialogWidth));
        this.mSubMenuHoverHandler = new Handler();
    }
    
    private MenuPopupWindow createPopupWindow() {
        final MenuPopupWindow menuPopupWindow = new MenuPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
        menuPopupWindow.setHoverListener(this.mMenuItemHoverListener);
        menuPopupWindow.setOnItemClickListener((AdapterView$OnItemClickListener)this);
        menuPopupWindow.setOnDismissListener((PopupWindow$OnDismissListener)this);
        menuPopupWindow.setAnchorView(this.mAnchorView);
        menuPopupWindow.setDropDownGravity(this.mDropDownGravity);
        menuPopupWindow.setModal(true);
        menuPopupWindow.setInputMethodMode(2);
        return menuPopupWindow;
    }
    
    private int findIndexOfAddedMenu(final MenuBuilder menuBuilder) {
        for (int size = this.mShowingMenus.size(), i = 0; i < size; ++i) {
            if (menuBuilder == this.mShowingMenus.get(i).menu) {
                return i;
            }
        }
        return -1;
    }
    
    private MenuItem findMenuItemForSubmenu(final MenuBuilder menuBuilder, final MenuBuilder menuBuilder2) {
        for (int size = menuBuilder.size(), i = 0; i < size; ++i) {
            final MenuItem item = menuBuilder.getItem(i);
            if (item.hasSubMenu() && menuBuilder2 == item.getSubMenu()) {
                return item;
            }
        }
        return null;
    }
    
    private View findParentViewForSubmenu(final CascadingMenuInfo cascadingMenuInfo, final MenuBuilder menuBuilder) {
        final MenuItem menuItemForSubmenu = this.findMenuItemForSubmenu(cascadingMenuInfo.menu, menuBuilder);
        if (menuItemForSubmenu == null) {
            return null;
        }
        final ListView listView = cascadingMenuInfo.getListView();
        final ListAdapter adapter = listView.getAdapter();
        final boolean b = adapter instanceof HeaderViewListAdapter;
        int i = 0;
        int headersCount;
        MenuAdapter menuAdapter;
        if (b) {
            final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter)adapter;
            headersCount = headerViewListAdapter.getHeadersCount();
            menuAdapter = (MenuAdapter)headerViewListAdapter.getWrappedAdapter();
        }
        else {
            menuAdapter = (MenuAdapter)adapter;
            headersCount = 0;
        }
        while (true) {
            while (i < menuAdapter.getCount()) {
                if (menuItemForSubmenu == menuAdapter.getItem(i)) {
                    if (i == -1) {
                        return null;
                    }
                    final int n = i + headersCount - listView.getFirstVisiblePosition();
                    if (n >= 0 && n < listView.getChildCount()) {
                        return listView.getChildAt(n);
                    }
                    return null;
                }
                else {
                    ++i;
                }
            }
            i = -1;
            continue;
        }
    }
    
    private int getInitialMenuPosition() {
        final int layoutDirection = ViewCompat.getLayoutDirection(this.mAnchorView);
        int n = 1;
        if (layoutDirection == 1) {
            n = 0;
        }
        return n;
    }
    
    private int getNextMenuPosition(final int n) {
        final List<CascadingMenuInfo> mShowingMenus = this.mShowingMenus;
        final ListView listView = mShowingMenus.get(mShowingMenus.size() - 1).getListView();
        final int[] array = new int[2];
        listView.getLocationOnScreen(array);
        final Rect rect = new Rect();
        this.mShownAnchorView.getWindowVisibleDisplayFrame(rect);
        if (this.mLastPosition == 1) {
            if (array[0] + listView.getWidth() + n > rect.right) {
                return 0;
            }
            return 1;
        }
        else {
            if (array[0] - n < 0) {
                return 1;
            }
            return 0;
        }
    }
    
    private void showMenu(final MenuBuilder menuBuilder) {
        final LayoutInflater from = LayoutInflater.from(this.mContext);
        final MenuAdapter adapter = new MenuAdapter(menuBuilder, from, this.mOverflowOnly, CascadingMenuPopup.ITEM_LAYOUT);
        if (!this.isShowing() && this.mForceShowIcon) {
            adapter.setForceShowIcon(true);
        }
        else if (this.isShowing()) {
            adapter.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(menuBuilder));
        }
        int contentWidth = MenuPopup.measureIndividualMenuWidth((ListAdapter)adapter, null, this.mContext, this.mMenuMaxWidth);
        final MenuPopupWindow popupWindow = this.createPopupWindow();
        popupWindow.setAdapter((ListAdapter)adapter);
        popupWindow.setContentWidth(contentWidth);
        popupWindow.setDropDownGravity(this.mDropDownGravity);
        Object o;
        View parentViewForSubmenu;
        if (this.mShowingMenus.size() > 0) {
            final List<CascadingMenuInfo> mShowingMenus = this.mShowingMenus;
            o = mShowingMenus.get(mShowingMenus.size() - 1);
            parentViewForSubmenu = this.findParentViewForSubmenu((CascadingMenuInfo)o, menuBuilder);
        }
        else {
            o = (parentViewForSubmenu = null);
        }
        if (parentViewForSubmenu != null) {
            popupWindow.setTouchModal(false);
            popupWindow.setEnterTransition(null);
            final int nextMenuPosition = this.getNextMenuPosition(contentWidth);
            final boolean b = nextMenuPosition == 1;
            this.mLastPosition = nextMenuPosition;
            int n;
            int verticalOffset;
            if (Build$VERSION.SDK_INT >= 26) {
                popupWindow.setAnchorView(parentViewForSubmenu);
                verticalOffset = (n = 0);
            }
            else {
                final int[] array = new int[2];
                this.mAnchorView.getLocationOnScreen(array);
                final int[] array2 = new int[2];
                parentViewForSubmenu.getLocationOnScreen(array2);
                if ((this.mDropDownGravity & 0x7) == 0x5) {
                    array[0] += this.mAnchorView.getWidth();
                    array2[0] += parentViewForSubmenu.getWidth();
                }
                n = array2[0] - array[0];
                verticalOffset = array2[1] - array[1];
            }
            int horizontalOffset = 0;
            Label_0373: {
                Label_0366: {
                    if ((this.mDropDownGravity & 0x5) == 0x5) {
                        if (!b) {
                            contentWidth = parentViewForSubmenu.getWidth();
                            break Label_0366;
                        }
                    }
                    else {
                        if (!b) {
                            break Label_0366;
                        }
                        contentWidth = parentViewForSubmenu.getWidth();
                    }
                    horizontalOffset = n + contentWidth;
                    break Label_0373;
                }
                horizontalOffset = n - contentWidth;
            }
            popupWindow.setHorizontalOffset(horizontalOffset);
            popupWindow.setOverlapAnchor(true);
            popupWindow.setVerticalOffset(verticalOffset);
        }
        else {
            if (this.mHasXOffset) {
                popupWindow.setHorizontalOffset(this.mXOffset);
            }
            if (this.mHasYOffset) {
                popupWindow.setVerticalOffset(this.mYOffset);
            }
            popupWindow.setEpicenterBounds(this.getEpicenterBounds());
        }
        this.mShowingMenus.add(new CascadingMenuInfo(popupWindow, menuBuilder, this.mLastPosition));
        popupWindow.show();
        final ListView listView = popupWindow.getListView();
        listView.setOnKeyListener((View$OnKeyListener)this);
        if (o == null && this.mShowTitle && menuBuilder.getHeaderTitle() != null) {
            final FrameLayout frameLayout = (FrameLayout)from.inflate(R$layout.abc_popup_menu_header_item_layout, (ViewGroup)listView, false);
            final TextView textView = (TextView)frameLayout.findViewById(16908310);
            frameLayout.setEnabled(false);
            textView.setText(menuBuilder.getHeaderTitle());
            listView.addHeaderView((View)frameLayout, (Object)null, false);
            popupWindow.show();
        }
    }
    
    @Override
    public void addMenu(final MenuBuilder menuBuilder) {
        menuBuilder.addMenuPresenter(this, this.mContext);
        if (this.isShowing()) {
            this.showMenu(menuBuilder);
        }
        else {
            this.mPendingMenus.add(menuBuilder);
        }
    }
    
    @Override
    protected boolean closeMenuOnSubMenuOpened() {
        return false;
    }
    
    public void dismiss() {
        int i = this.mShowingMenus.size();
        if (i > 0) {
            final CascadingMenuInfo[] array = this.mShowingMenus.toArray(new CascadingMenuInfo[i]);
            --i;
            while (i >= 0) {
                final CascadingMenuInfo cascadingMenuInfo = array[i];
                if (cascadingMenuInfo.window.isShowing()) {
                    cascadingMenuInfo.window.dismiss();
                }
                --i;
            }
        }
    }
    
    @Override
    public boolean flagActionItems() {
        return false;
    }
    
    public ListView getListView() {
        ListView listView;
        if (this.mShowingMenus.isEmpty()) {
            listView = null;
        }
        else {
            final List<CascadingMenuInfo> mShowingMenus = this.mShowingMenus;
            listView = mShowingMenus.get(mShowingMenus.size() - 1).getListView();
        }
        return listView;
    }
    
    public boolean isShowing() {
        final int size = this.mShowingMenus.size();
        boolean b = false;
        if (size > 0) {
            b = b;
            if (this.mShowingMenus.get(0).window.isShowing()) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public void onCloseMenu(final MenuBuilder menuBuilder, final boolean b) {
        final int indexOfAddedMenu = this.findIndexOfAddedMenu(menuBuilder);
        if (indexOfAddedMenu < 0) {
            return;
        }
        final int n = indexOfAddedMenu + 1;
        if (n < this.mShowingMenus.size()) {
            this.mShowingMenus.get(n).menu.close(false);
        }
        final CascadingMenuInfo cascadingMenuInfo = this.mShowingMenus.remove(indexOfAddedMenu);
        cascadingMenuInfo.menu.removeMenuPresenter(this);
        if (this.mShouldCloseImmediately) {
            cascadingMenuInfo.window.setExitTransition(null);
            cascadingMenuInfo.window.setAnimationStyle(0);
        }
        cascadingMenuInfo.window.dismiss();
        final int size = this.mShowingMenus.size();
        if (size > 0) {
            this.mLastPosition = this.mShowingMenus.get(size - 1).position;
        }
        else {
            this.mLastPosition = this.getInitialMenuPosition();
        }
        if (size == 0) {
            this.dismiss();
            final Callback mPresenterCallback = this.mPresenterCallback;
            if (mPresenterCallback != null) {
                mPresenterCallback.onCloseMenu(menuBuilder, true);
            }
            final ViewTreeObserver mTreeObserver = this.mTreeObserver;
            if (mTreeObserver != null) {
                if (mTreeObserver.isAlive()) {
                    this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
                }
                this.mTreeObserver = null;
            }
            this.mShownAnchorView.removeOnAttachStateChangeListener(this.mAttachStateChangeListener);
            this.mOnDismissListener.onDismiss();
        }
        else if (b) {
            this.mShowingMenus.get(0).menu.close(false);
        }
    }
    
    public void onDismiss() {
        while (true) {
            for (int size = this.mShowingMenus.size(), i = 0; i < size; ++i) {
                final CascadingMenuInfo cascadingMenuInfo = this.mShowingMenus.get(i);
                if (!cascadingMenuInfo.window.isShowing()) {
                    if (cascadingMenuInfo != null) {
                        cascadingMenuInfo.menu.close(false);
                    }
                    return;
                }
            }
            final CascadingMenuInfo cascadingMenuInfo = null;
            continue;
        }
    }
    
    public boolean onKey(final View view, final int n, final KeyEvent keyEvent) {
        if (keyEvent.getAction() == 1 && n == 82) {
            this.dismiss();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onSubMenuSelected(final SubMenuBuilder subMenuBuilder) {
        for (final CascadingMenuInfo cascadingMenuInfo : this.mShowingMenus) {
            if (subMenuBuilder == cascadingMenuInfo.menu) {
                cascadingMenuInfo.getListView().requestFocus();
                return true;
            }
        }
        if (subMenuBuilder.hasVisibleItems()) {
            this.addMenu(subMenuBuilder);
            final Callback mPresenterCallback = this.mPresenterCallback;
            if (mPresenterCallback != null) {
                mPresenterCallback.onOpenSubMenu(subMenuBuilder);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void setAnchorView(final View mAnchorView) {
        if (this.mAnchorView != mAnchorView) {
            this.mAnchorView = mAnchorView;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(mAnchorView));
        }
    }
    
    @Override
    public void setCallback(final Callback mPresenterCallback) {
        this.mPresenterCallback = mPresenterCallback;
    }
    
    @Override
    public void setForceShowIcon(final boolean mForceShowIcon) {
        this.mForceShowIcon = mForceShowIcon;
    }
    
    @Override
    public void setGravity(final int mRawDropDownGravity) {
        if (this.mRawDropDownGravity != mRawDropDownGravity) {
            this.mRawDropDownGravity = mRawDropDownGravity;
            this.mDropDownGravity = GravityCompat.getAbsoluteGravity(mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
        }
    }
    
    @Override
    public void setHorizontalOffset(final int mxOffset) {
        this.mHasXOffset = true;
        this.mXOffset = mxOffset;
    }
    
    @Override
    public void setOnDismissListener(final PopupWindow$OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }
    
    @Override
    public void setShowTitle(final boolean mShowTitle) {
        this.mShowTitle = mShowTitle;
    }
    
    @Override
    public void setVerticalOffset(final int myOffset) {
        this.mHasYOffset = true;
        this.mYOffset = myOffset;
    }
    
    public void show() {
        if (this.isShowing()) {
            return;
        }
        final Iterator<MenuBuilder> iterator = this.mPendingMenus.iterator();
        while (iterator.hasNext()) {
            this.showMenu(iterator.next());
        }
        this.mPendingMenus.clear();
        if ((this.mShownAnchorView = this.mAnchorView) != null) {
            final boolean b = this.mTreeObserver == null;
            final ViewTreeObserver viewTreeObserver = this.mShownAnchorView.getViewTreeObserver();
            this.mTreeObserver = viewTreeObserver;
            if (b) {
                viewTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
            }
            this.mShownAnchorView.addOnAttachStateChangeListener(this.mAttachStateChangeListener);
        }
    }
    
    @Override
    public void updateMenuView(final boolean b) {
        final Iterator<CascadingMenuInfo> iterator = this.mShowingMenus.iterator();
        while (iterator.hasNext()) {
            MenuPopup.toMenuAdapter(iterator.next().getListView().getAdapter()).notifyDataSetChanged();
        }
    }
    
    private static class CascadingMenuInfo
    {
        public final MenuBuilder menu;
        public final int position;
        public final MenuPopupWindow window;
        
        public CascadingMenuInfo(final MenuPopupWindow window, final MenuBuilder menu, final int position) {
            this.window = window;
            this.menu = menu;
            this.position = position;
        }
        
        public ListView getListView() {
            return this.window.getListView();
        }
    }
}
