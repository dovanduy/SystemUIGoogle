// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.view.menu;

import android.view.MenuItem$OnMenuItemClickListener;
import android.view.MenuItem$OnActionExpandListener;
import androidx.core.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ContextMenu$ContextMenuInfo;
import android.view.View;
import android.view.ActionProvider;
import androidx.core.graphics.drawable.DrawableCompat;
import android.content.Intent;
import android.graphics.PorterDuff$Mode;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.content.Context;
import androidx.core.internal.view.SupportMenuItem;

public class ActionMenuItem implements SupportMenuItem
{
    private CharSequence mContentDescription;
    private Context mContext;
    private int mFlags;
    private final int mGroup;
    private boolean mHasIconTint;
    private boolean mHasIconTintMode;
    private Drawable mIconDrawable;
    private ColorStateList mIconTintList;
    private PorterDuff$Mode mIconTintMode;
    private final int mId;
    private Intent mIntent;
    private final int mOrdering;
    private char mShortcutAlphabeticChar;
    private int mShortcutAlphabeticModifiers;
    private char mShortcutNumericChar;
    private int mShortcutNumericModifiers;
    private CharSequence mTitle;
    private CharSequence mTitleCondensed;
    private CharSequence mTooltipText;
    
    public ActionMenuItem(final Context mContext, final int mGroup, final int mId, final int n, final int mOrdering, final CharSequence mTitle) {
        this.mShortcutNumericModifiers = 4096;
        this.mShortcutAlphabeticModifiers = 4096;
        this.mIconTintList = null;
        this.mIconTintMode = null;
        this.mHasIconTint = false;
        this.mHasIconTintMode = false;
        this.mFlags = 16;
        this.mContext = mContext;
        this.mId = mId;
        this.mGroup = mGroup;
        this.mOrdering = mOrdering;
        this.mTitle = mTitle;
    }
    
    private void applyIconTint() {
        if (this.mIconDrawable != null && (this.mHasIconTint || this.mHasIconTintMode)) {
            final Drawable wrap = DrawableCompat.wrap(this.mIconDrawable);
            this.mIconDrawable = wrap;
            final Drawable mutate = wrap.mutate();
            this.mIconDrawable = mutate;
            if (this.mHasIconTint) {
                DrawableCompat.setTintList(mutate, this.mIconTintList);
            }
            if (this.mHasIconTintMode) {
                DrawableCompat.setTintMode(this.mIconDrawable, this.mIconTintMode);
            }
        }
    }
    
    @Override
    public boolean collapseActionView() {
        return false;
    }
    
    @Override
    public boolean expandActionView() {
        return false;
    }
    
    public ActionProvider getActionProvider() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public View getActionView() {
        return null;
    }
    
    @Override
    public int getAlphabeticModifiers() {
        return this.mShortcutAlphabeticModifiers;
    }
    
    public char getAlphabeticShortcut() {
        return this.mShortcutAlphabeticChar;
    }
    
    @Override
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }
    
    public int getGroupId() {
        return this.mGroup;
    }
    
    public Drawable getIcon() {
        return this.mIconDrawable;
    }
    
    @Override
    public ColorStateList getIconTintList() {
        return this.mIconTintList;
    }
    
    @Override
    public PorterDuff$Mode getIconTintMode() {
        return this.mIconTintMode;
    }
    
    public Intent getIntent() {
        return this.mIntent;
    }
    
    public int getItemId() {
        return this.mId;
    }
    
    public ContextMenu$ContextMenuInfo getMenuInfo() {
        return null;
    }
    
    @Override
    public int getNumericModifiers() {
        return this.mShortcutNumericModifiers;
    }
    
    public char getNumericShortcut() {
        return this.mShortcutNumericChar;
    }
    
    public int getOrder() {
        return this.mOrdering;
    }
    
    public SubMenu getSubMenu() {
        return null;
    }
    
    @Override
    public androidx.core.view.ActionProvider getSupportActionProvider() {
        return null;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public CharSequence getTitleCondensed() {
        CharSequence charSequence = this.mTitleCondensed;
        if (charSequence == null) {
            charSequence = this.mTitle;
        }
        return charSequence;
    }
    
    @Override
    public CharSequence getTooltipText() {
        return this.mTooltipText;
    }
    
    public boolean hasSubMenu() {
        return false;
    }
    
    @Override
    public boolean isActionViewExpanded() {
        return false;
    }
    
    public boolean isCheckable() {
        final int mFlags = this.mFlags;
        boolean b = true;
        if ((mFlags & 0x1) == 0x0) {
            b = false;
        }
        return b;
    }
    
    public boolean isChecked() {
        return (this.mFlags & 0x2) != 0x0;
    }
    
    public boolean isEnabled() {
        return (this.mFlags & 0x10) != 0x0;
    }
    
    public boolean isVisible() {
        return (this.mFlags & 0x8) == 0x0;
    }
    
    public boolean requiresActionButton() {
        return true;
    }
    
    public boolean requiresOverflow() {
        return false;
    }
    
    public MenuItem setActionProvider(final ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }
    
    public SupportMenuItem setActionView(final int n) {
        throw new UnsupportedOperationException();
    }
    
    public SupportMenuItem setActionView(final View view) {
        throw new UnsupportedOperationException();
    }
    
    public MenuItem setAlphabeticShortcut(final char ch) {
        this.mShortcutAlphabeticChar = Character.toLowerCase(ch);
        return (MenuItem)this;
    }
    
    @Override
    public MenuItem setAlphabeticShortcut(final char ch, final int n) {
        this.mShortcutAlphabeticChar = Character.toLowerCase(ch);
        this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(n);
        return (MenuItem)this;
    }
    
    public MenuItem setCheckable(final boolean b) {
        this.mFlags = ((b ? 1 : 0) | (this.mFlags & 0xFFFFFFFE));
        return (MenuItem)this;
    }
    
    public MenuItem setChecked(final boolean b) {
        final int mFlags = this.mFlags;
        int n;
        if (b) {
            n = 2;
        }
        else {
            n = 0;
        }
        this.mFlags = (n | (mFlags & 0xFFFFFFFD));
        return (MenuItem)this;
    }
    
    @Override
    public SupportMenuItem setContentDescription(final CharSequence mContentDescription) {
        this.mContentDescription = mContentDescription;
        return this;
    }
    
    public MenuItem setEnabled(final boolean b) {
        final int mFlags = this.mFlags;
        int n;
        if (b) {
            n = 16;
        }
        else {
            n = 0;
        }
        this.mFlags = (n | (mFlags & 0xFFFFFFEF));
        return (MenuItem)this;
    }
    
    public MenuItem setIcon(final int n) {
        this.mIconDrawable = ContextCompat.getDrawable(this.mContext, n);
        this.applyIconTint();
        return (MenuItem)this;
    }
    
    public MenuItem setIcon(final Drawable mIconDrawable) {
        this.mIconDrawable = mIconDrawable;
        this.applyIconTint();
        return (MenuItem)this;
    }
    
    @Override
    public MenuItem setIconTintList(final ColorStateList mIconTintList) {
        this.mIconTintList = mIconTintList;
        this.mHasIconTint = true;
        this.applyIconTint();
        return (MenuItem)this;
    }
    
    @Override
    public MenuItem setIconTintMode(final PorterDuff$Mode mIconTintMode) {
        this.mIconTintMode = mIconTintMode;
        this.mHasIconTintMode = true;
        this.applyIconTint();
        return (MenuItem)this;
    }
    
    public MenuItem setIntent(final Intent mIntent) {
        this.mIntent = mIntent;
        return (MenuItem)this;
    }
    
    public MenuItem setNumericShortcut(final char c) {
        this.mShortcutNumericChar = c;
        return (MenuItem)this;
    }
    
    @Override
    public MenuItem setNumericShortcut(final char c, final int n) {
        this.mShortcutNumericChar = c;
        this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(n);
        return (MenuItem)this;
    }
    
    public MenuItem setOnActionExpandListener(final MenuItem$OnActionExpandListener menuItem$OnActionExpandListener) {
        throw new UnsupportedOperationException();
    }
    
    public MenuItem setOnMenuItemClickListener(final MenuItem$OnMenuItemClickListener menuItem$OnMenuItemClickListener) {
        return (MenuItem)this;
    }
    
    public MenuItem setShortcut(final char c, final char ch) {
        this.mShortcutNumericChar = c;
        this.mShortcutAlphabeticChar = Character.toLowerCase(ch);
        return (MenuItem)this;
    }
    
    @Override
    public MenuItem setShortcut(final char c, final char ch, final int n, final int n2) {
        this.mShortcutNumericChar = c;
        this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(n);
        this.mShortcutAlphabeticChar = Character.toLowerCase(ch);
        this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(n2);
        return (MenuItem)this;
    }
    
    @Override
    public void setShowAsAction(final int n) {
    }
    
    public SupportMenuItem setShowAsActionFlags(final int showAsAction) {
        this.setShowAsAction(showAsAction);
        return this;
    }
    
    @Override
    public SupportMenuItem setSupportActionProvider(final androidx.core.view.ActionProvider actionProvider) {
        throw new UnsupportedOperationException();
    }
    
    public MenuItem setTitle(final int n) {
        this.mTitle = this.mContext.getResources().getString(n);
        return (MenuItem)this;
    }
    
    public MenuItem setTitle(final CharSequence mTitle) {
        this.mTitle = mTitle;
        return (MenuItem)this;
    }
    
    public MenuItem setTitleCondensed(final CharSequence mTitleCondensed) {
        this.mTitleCondensed = mTitleCondensed;
        return (MenuItem)this;
    }
    
    @Override
    public SupportMenuItem setTooltipText(final CharSequence mTooltipText) {
        this.mTooltipText = mTooltipText;
        return this;
    }
    
    public MenuItem setVisible(final boolean b) {
        final int mFlags = this.mFlags;
        int n = 8;
        if (b) {
            n = 0;
        }
        this.mFlags = ((mFlags & 0x8) | n);
        return (MenuItem)this;
    }
}
