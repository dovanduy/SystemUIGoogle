// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.tiles;

import android.content.Context;
import android.content.Intent;
import com.android.systemui.plugins.qs.DetailAdapter;
import com.android.systemui.qs.QSHost;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.tileimpl.QSTileImpl;

public class UserTile extends QSTileImpl<State> implements OnUserInfoChangedListener
{
    private Pair<String, Drawable> mLastUpdate;
    private final UserInfoController mUserInfoController;
    private final UserSwitcherController mUserSwitcherController;
    
    public UserTile(final QSHost qsHost, final UserSwitcherController mUserSwitcherController, final UserInfoController mUserInfoController) {
        super(qsHost);
        this.mUserSwitcherController = mUserSwitcherController;
        (this.mUserInfoController = mUserInfoController).observe(this.getLifecycle(), (UserInfoController.OnUserInfoChangedListener)this);
    }
    
    @Override
    public DetailAdapter getDetailAdapter() {
        return this.mUserSwitcherController.userDetailAdapter;
    }
    
    @Override
    public Intent getLongClickIntent() {
        return new Intent("android.settings.USER_SETTINGS");
    }
    
    @Override
    public int getMetricsCategory() {
        return 260;
    }
    
    @Override
    public CharSequence getTileLabel() {
        return this.getState().label;
    }
    
    @Override
    protected void handleClick() {
        this.showDetail(true);
    }
    
    @Override
    protected void handleUpdateState(final State state, final Object o) {
        Pair mLastUpdate;
        if (o != null) {
            mLastUpdate = (Pair)o;
        }
        else {
            mLastUpdate = this.mLastUpdate;
        }
        if (mLastUpdate != null) {
            final Object first = mLastUpdate.first;
            state.label = (CharSequence)first;
            state.contentDescription = (CharSequence)first;
            state.icon = new Icon(this) {
                @Override
                public Drawable getDrawable(final Context context) {
                    return (Drawable)mLastUpdate.second;
                }
            };
        }
    }
    
    @Override
    public State newTileState() {
        return new QSTile.State();
    }
    
    @Override
    public void onUserInfoChanged(final String s, final Drawable drawable, final String s2) {
        this.refreshState(this.mLastUpdate = (Pair<String, Drawable>)new Pair((Object)s, (Object)drawable));
    }
}
