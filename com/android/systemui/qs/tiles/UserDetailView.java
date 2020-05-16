// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.RestrictedLockUtils;
import android.graphics.Bitmap;
import android.graphics.drawable.LayerDrawable;
import com.android.systemui.R$drawable;
import com.android.systemui.R$color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View$OnClickListener;
import android.widget.BaseAdapter;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.qs.PseudoGridView;

public class UserDetailView extends PseudoGridView
{
    protected Adapter mAdapter;
    
    public UserDetailView(final Context context, final AttributeSet set) {
        super(context, set);
    }
    
    public static UserDetailView inflate(final Context context, final ViewGroup viewGroup, final boolean b) {
        return (UserDetailView)LayoutInflater.from(context).inflate(R$layout.qs_user_detail, viewGroup, b);
    }
    
    public void createAndSetAdapter(final UserSwitcherController userSwitcherController) {
        ViewGroupAdapterBridge.link(this, this.mAdapter = new Adapter(super.mContext, userSwitcherController));
    }
    
    public void refreshAdapter() {
        ((UserSwitcherController.BaseUserAdapter)this.mAdapter).refresh();
    }
    
    public static class Adapter extends BaseUserAdapter implements View$OnClickListener
    {
        private final Context mContext;
        protected UserSwitcherController mController;
        private View mCurrentUserView;
        
        public Adapter(final Context mContext, final UserSwitcherController mController) {
            super(mController);
            this.mContext = mContext;
            this.mController = mController;
        }
        
        private static Drawable getDrawable(final Context context, final UserRecord userRecord) {
            final Drawable iconDrawable = UserSwitcherController.BaseUserAdapter.getIconDrawable(context, userRecord);
            int n;
            if (userRecord.isCurrent) {
                n = R$color.qs_user_switcher_selected_avatar_icon_color;
            }
            else {
                n = R$color.qs_user_switcher_avatar_icon_color;
            }
            iconDrawable.setTint(context.getResources().getColor(n, context.getTheme()));
            int n2;
            if (userRecord.isCurrent) {
                n2 = R$drawable.bg_avatar_selected;
            }
            else {
                n2 = R$drawable.qs_bg_avatar;
            }
            return (Drawable)new LayerDrawable(new Drawable[] { context.getDrawable(n2), iconDrawable });
        }
        
        public UserDetailItemView createUserDetailItemView(final View view, final ViewGroup viewGroup, final UserRecord tag) {
            final UserDetailItemView convertOrInflate = UserDetailItemView.convertOrInflate(this.mContext, view, viewGroup);
            if (tag.isCurrent && !tag.isGuest) {
                convertOrInflate.setOnClickListener((View$OnClickListener)null);
                convertOrInflate.setClickable(false);
            }
            else {
                convertOrInflate.setOnClickListener((View$OnClickListener)this);
            }
            final String name = ((UserSwitcherController.BaseUserAdapter)this).getName(this.mContext, tag);
            final Bitmap picture = tag.picture;
            if (picture == null) {
                convertOrInflate.bind(name, getDrawable(this.mContext, tag), tag.resolveId());
            }
            else {
                convertOrInflate.bind(name, picture, tag.info.id);
            }
            convertOrInflate.setActivated(tag.isCurrent);
            if (tag.isCurrent) {
                this.mCurrentUserView = (View)convertOrInflate;
            }
            convertOrInflate.setDisabledByAdmin(tag.isDisabledByAdmin);
            if (!tag.isSwitchToEnabled) {
                convertOrInflate.setEnabled(false);
            }
            convertOrInflate.setTag((Object)tag);
            return convertOrInflate;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            return (View)this.createUserDetailItemView(view, viewGroup, ((UserSwitcherController.BaseUserAdapter)this).getItem(n));
        }
        
        public void onClick(final View view) {
            final UserRecord userRecord = (UserRecord)view.getTag();
            if (userRecord.isDisabledByAdmin) {
                this.mController.startActivity(RestrictedLockUtils.getShowAdminSupportDetailsIntent(this.mContext, userRecord.enforcedAdmin));
            }
            else if (userRecord.isSwitchToEnabled) {
                MetricsLogger.action(this.mContext, 156);
                if (!userRecord.isAddUser && !userRecord.isRestricted && !userRecord.isDisabledByAdmin) {
                    final View mCurrentUserView = this.mCurrentUserView;
                    if (mCurrentUserView != null) {
                        mCurrentUserView.setActivated(false);
                    }
                    view.setActivated(true);
                }
                ((UserSwitcherController.BaseUserAdapter)this).switchTo(userRecord);
            }
        }
    }
}
