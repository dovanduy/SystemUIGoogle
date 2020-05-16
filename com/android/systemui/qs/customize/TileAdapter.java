// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs.customize;

import androidx.core.view.ViewCompat;
import android.graphics.Canvas;
import com.android.systemui.R$drawable;
import com.android.systemui.R$id;
import android.graphics.drawable.Drawable;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.qs.tileimpl.QSIconViewImpl;
import android.widget.FrameLayout;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.plugins.qs.QSTile;
import android.content.res.Resources;
import android.view.View$OnClickListener;
import android.widget.TextView;
import com.android.systemui.qs.external.CustomTile;
import android.app.AlertDialog;
import android.app.Dialog;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import android.content.DialogInterface;
import android.content.DialogInterface$OnClickListener;
import com.android.systemui.R$string;
import android.app.AlertDialog$Builder;
import java.util.Collection;
import com.android.internal.logging.UiEventLogger$UiEventEnum;
import android.view.View$OnLayoutChangeListener;
import android.view.View;
import com.android.systemui.R$integer;
import com.android.internal.logging.UiEventLoggerImpl;
import java.util.ArrayList;
import com.android.internal.logging.UiEventLogger;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.systemui.qs.QSTileHost;
import android.os.Handler;
import android.content.Context;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.List;
import android.view.accessibility.AccessibilityManager;
import androidx.recyclerview.widget.RecyclerView;

public class TileAdapter extends Adapter<Holder> implements TileStateListener
{
    private int mAccessibilityAction;
    private int mAccessibilityFromIndex;
    private CharSequence mAccessibilityFromLabel;
    private final AccessibilityManager mAccessibilityManager;
    private List<TileInfo> mAllTiles;
    private final ItemTouchHelper.Callback mCallbacks;
    private final Context mContext;
    private Holder mCurrentDrag;
    private List<String> mCurrentSpecs;
    private final ItemDecoration mDecoration;
    private int mEditIndex;
    private final Handler mHandler;
    private QSTileHost mHost;
    private final ItemTouchHelper mItemTouchHelper;
    private final int mMinNumTiles;
    private boolean mNeedsFocus;
    private List<TileInfo> mOtherTiles;
    private final GridLayoutManager.SpanSizeLookup mSizeLookup;
    private int mTileDividerIndex;
    private final List<TileInfo> mTiles;
    private UiEventLogger mUiEventLogger;
    
    public TileAdapter(final Context mContext) {
        this.mHandler = new Handler();
        this.mTiles = new ArrayList<TileInfo>();
        this.mAccessibilityAction = 0;
        this.mUiEventLogger = (UiEventLogger)new UiEventLoggerImpl();
        this.mSizeLookup = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int n) {
                final int itemViewType = TileAdapter.this.getItemViewType(n);
                final int n2 = n = 3;
                if (itemViewType != 1) {
                    n = n2;
                    if (itemViewType != 4) {
                        if (itemViewType == 3) {
                            n = n2;
                        }
                        else {
                            n = 1;
                        }
                    }
                }
                return n;
            }
        };
        this.mCallbacks = new ItemTouchHelper.Callback() {
            @Override
            public boolean canDropOver(final RecyclerView recyclerView, final ViewHolder viewHolder, final ViewHolder viewHolder2) {
                final int adapterPosition = viewHolder2.getAdapterPosition();
                final boolean b = false;
                final boolean b2 = false;
                boolean b3 = b;
                if (adapterPosition != 0) {
                    if (adapterPosition == -1) {
                        b3 = b;
                    }
                    else {
                        if (!TileAdapter.this.canRemoveTiles() && viewHolder.getAdapterPosition() < TileAdapter.this.mEditIndex) {
                            boolean b4 = b2;
                            if (adapterPosition < TileAdapter.this.mEditIndex) {
                                b4 = true;
                            }
                            return b4;
                        }
                        b3 = b;
                        if (adapterPosition <= TileAdapter.this.mEditIndex + 1) {
                            b3 = true;
                        }
                    }
                }
                return b3;
            }
            
            @Override
            public int getMovementFlags(final RecyclerView recyclerView, final ViewHolder viewHolder) {
                final int itemViewType = viewHolder.getItemViewType();
                if (itemViewType != 1 && itemViewType != 3 && itemViewType != 4) {
                    return ItemTouchHelper.Callback.makeMovementFlags(15, 0);
                }
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            
            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }
            
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }
            
            @Override
            public boolean onMove(final RecyclerView recyclerView, final ViewHolder viewHolder, final ViewHolder viewHolder2) {
                final int adapterPosition = viewHolder.getAdapterPosition();
                final int adapterPosition2 = viewHolder2.getAdapterPosition();
                return adapterPosition != 0 && adapterPosition != -1 && adapterPosition2 != 0 && adapterPosition2 != -1 && TileAdapter.this.move(adapterPosition, adapterPosition2, viewHolder2.itemView);
            }
            
            @Override
            public void onSelectedChanged(ViewHolder viewHolder, int adapterPosition) {
                super.onSelectedChanged(viewHolder, adapterPosition);
                if (adapterPosition != 2) {
                    viewHolder = null;
                }
                if (viewHolder == TileAdapter.this.mCurrentDrag) {
                    return;
                }
                if (TileAdapter.this.mCurrentDrag != null) {
                    adapterPosition = ((RecyclerView.ViewHolder)TileAdapter.this.mCurrentDrag).getAdapterPosition();
                    if (adapterPosition == -1) {
                        return;
                    }
                    final TileInfo tileInfo = TileAdapter.this.mTiles.get(adapterPosition);
                    TileAdapter.this.mCurrentDrag.mTileView.setShowAppLabel(adapterPosition > TileAdapter.this.mEditIndex && !tileInfo.isSystem);
                    TileAdapter.this.mCurrentDrag.stopDrag();
                    TileAdapter.this.mCurrentDrag = null;
                }
                if (viewHolder != null) {
                    TileAdapter.this.mCurrentDrag = (Holder)viewHolder;
                    TileAdapter.this.mCurrentDrag.startDrag();
                }
                TileAdapter.this.mHandler.post((Runnable)new Runnable() {
                    @Override
                    public void run() {
                        final TileAdapter this$0 = TileAdapter.this;
                        ((RecyclerView.Adapter)this$0).notifyItemChanged(this$0.mEditIndex);
                    }
                });
            }
            
            @Override
            public void onSwiped(final ViewHolder viewHolder, final int n) {
            }
        };
        this.mContext = mContext;
        this.mAccessibilityManager = (AccessibilityManager)mContext.getSystemService((Class)AccessibilityManager.class);
        this.mItemTouchHelper = new ItemTouchHelper(this.mCallbacks);
        this.mDecoration = new TileItemDecoration(mContext);
        this.mMinNumTiles = mContext.getResources().getInteger(R$integer.quick_settings_min_num_tiles);
    }
    
    private boolean canRemoveTiles() {
        return this.mCurrentSpecs.size() > this.mMinNumTiles;
    }
    
    private void clearAccessibilityState() {
        if (this.mAccessibilityAction == 1) {
            this.mTiles.remove(--this.mEditIndex);
            --this.mTileDividerIndex;
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        this.mAccessibilityAction = 0;
    }
    
    private void focusOnHolder(final Holder holder) {
        if (this.mNeedsFocus) {
            holder.mTileView.requestLayout();
            holder.mTileView.addOnLayoutChangeListener((View$OnLayoutChangeListener)new View$OnLayoutChangeListener(this) {
                public void onLayoutChange(final View view, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
                    holder.mTileView.removeOnLayoutChangeListener((View$OnLayoutChangeListener)this);
                    holder.mTileView.requestFocus();
                }
            });
            this.mNeedsFocus = false;
        }
    }
    
    private TileInfo getAndRemoveOther(final String anObject) {
        for (int i = 0; i < this.mOtherTiles.size(); ++i) {
            if (this.mOtherTiles.get(i).spec.equals(anObject)) {
                return this.mOtherTiles.remove(i);
            }
        }
        return null;
    }
    
    private <T> void move(final int n, final int n2, final List<T> list) {
        list.add(n2, list.remove(n));
        ((RecyclerView.Adapter)this).notifyItemMoved(n, n2);
    }
    
    private boolean move(final int n, final int n2, final View view) {
        if (n2 == n) {
            return true;
        }
        final CharSequence label = this.mTiles.get(n).state.label;
        this.move(n, n2, this.mTiles);
        this.updateDividerLocations();
        final int mEditIndex = this.mEditIndex;
        if (n2 >= mEditIndex) {
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_REMOVE, 0, strip(this.mTiles.get(n2)));
        }
        else if (n >= mEditIndex) {
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_ADD, 0, strip(this.mTiles.get(n2)));
        }
        else {
            this.mUiEventLogger.log((UiEventLogger$UiEventEnum)QSEditEvent.QS_EDIT_MOVE, 0, strip(this.mTiles.get(n2)));
        }
        this.saveSpecs(this.mHost);
        return true;
    }
    
    private void recalcSpecs() {
        if (this.mCurrentSpecs != null) {
            if (this.mAllTiles != null) {
                this.mOtherTiles = new ArrayList<TileInfo>(this.mAllTiles);
                this.mTiles.clear();
                this.mTiles.add(null);
                final int n = 0;
                for (int i = 0; i < this.mCurrentSpecs.size(); ++i) {
                    final TileInfo andRemoveOther = this.getAndRemoveOther(this.mCurrentSpecs.get(i));
                    if (andRemoveOther != null) {
                        this.mTiles.add(andRemoveOther);
                    }
                }
                this.mTiles.add(null);
                int n2;
                for (int j = n; j < this.mOtherTiles.size(); j = n2 + 1) {
                    final TileInfo tileInfo = this.mOtherTiles.get(j);
                    n2 = j;
                    if (tileInfo.isSystem) {
                        this.mOtherTiles.remove(j);
                        this.mTiles.add(tileInfo);
                        n2 = j - 1;
                    }
                }
                this.mTileDividerIndex = this.mTiles.size();
                this.mTiles.add(null);
                this.mTiles.addAll(this.mOtherTiles);
                this.updateDividerLocations();
                ((RecyclerView.Adapter)this).notifyDataSetChanged();
            }
        }
    }
    
    private void selectPosition(final int n, final View view) {
        if (this.mAccessibilityAction == 1) {
            this.mTiles.remove(this.mEditIndex--);
            ((RecyclerView.Adapter)this).notifyItemRemoved(this.mEditIndex);
        }
        this.mAccessibilityAction = 0;
        this.move(this.mAccessibilityFromIndex, n, view);
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    private void setSelectableForHeaders(final View view) {
        if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
            final int mAccessibilityAction = this.mAccessibilityAction;
            int importantForAccessibility = 1;
            final boolean b = mAccessibilityAction == 0;
            view.setFocusable(b);
            if (!b) {
                importantForAccessibility = 4;
            }
            view.setImportantForAccessibility(importantForAccessibility);
            view.setFocusableInTouchMode(b);
        }
    }
    
    private void showAccessibilityDialog(final int n, final View view) {
        final TileInfo tileInfo = this.mTiles.get(n);
        final AlertDialog create = new AlertDialog$Builder(this.mContext).setItems(new CharSequence[] { this.mContext.getString(R$string.accessibility_qs_edit_move_tile, new Object[] { tileInfo.state.label }), this.mContext.getString(R$string.accessibility_qs_edit_remove_tile, new Object[] { tileInfo.state.label }) }, (DialogInterface$OnClickListener)new DialogInterface$OnClickListener() {
            public void onClick(final DialogInterface dialogInterface, int n) {
                if (n == 0) {
                    TileAdapter.this.startAccessibleMove(n);
                }
                else {
                    final TileAdapter this$0 = TileAdapter.this;
                    final int val$position = n;
                    if (tileInfo.isSystem) {
                        n = this$0.mEditIndex;
                    }
                    else {
                        n = this$0.mTileDividerIndex;
                    }
                    this$0.move(val$position, n, view);
                    final TileAdapter this$2 = TileAdapter.this;
                    ((RecyclerView.Adapter)this$2).notifyItemChanged(this$2.mTileDividerIndex);
                    ((RecyclerView.Adapter)TileAdapter.this).notifyDataSetChanged();
                }
            }
        }).setNegativeButton(17039360, (DialogInterface$OnClickListener)null).create();
        SystemUIDialog.setShowForAllUsers((Dialog)create, true);
        SystemUIDialog.applyFlags(create);
        create.show();
    }
    
    private void startAccessibleAdd(int mAccessibilityFromIndex) {
        this.mAccessibilityFromIndex = mAccessibilityFromIndex;
        this.mAccessibilityFromLabel = this.mTiles.get(mAccessibilityFromIndex).state.label;
        this.mAccessibilityAction = 1;
        final List<TileInfo> mTiles = this.mTiles;
        mAccessibilityFromIndex = this.mEditIndex++;
        mTiles.add(mAccessibilityFromIndex, null);
        ++this.mTileDividerIndex;
        this.mNeedsFocus = true;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    private void startAccessibleMove(final int mAccessibilityFromIndex) {
        this.mAccessibilityFromIndex = mAccessibilityFromIndex;
        this.mAccessibilityFromLabel = this.mTiles.get(mAccessibilityFromIndex).state.label;
        this.mAccessibilityAction = 2;
        this.mNeedsFocus = true;
        ((RecyclerView.Adapter)this).notifyDataSetChanged();
    }
    
    private static String strip(final TileInfo tileInfo) {
        String s2;
        final String s = s2 = tileInfo.spec;
        if (s.startsWith("custom(")) {
            s2 = CustomTile.getComponentFromSpec(s).getPackageName();
        }
        return s2;
    }
    
    private void updateDividerLocations() {
        this.mEditIndex = -1;
        this.mTileDividerIndex = this.mTiles.size();
        for (int i = 1; i < this.mTiles.size(); ++i) {
            if (this.mTiles.get(i) == null) {
                if (this.mEditIndex == -1) {
                    this.mEditIndex = i;
                }
                else {
                    this.mTileDividerIndex = i;
                }
            }
        }
        final int size = this.mTiles.size();
        final int mTileDividerIndex = this.mTileDividerIndex;
        if (size - 1 == mTileDividerIndex) {
            ((RecyclerView.Adapter)this).notifyItemChanged(mTileDividerIndex);
        }
    }
    
    @Override
    public int getItemCount() {
        return this.mTiles.size();
    }
    
    public ItemDecoration getItemDecoration() {
        return this.mDecoration;
    }
    
    public ItemTouchHelper getItemTouchHelper() {
        return this.mItemTouchHelper;
    }
    
    @Override
    public int getItemViewType(final int n) {
        if (n == 0) {
            return 3;
        }
        if (this.mAccessibilityAction == 1 && n == this.mEditIndex - 1) {
            return 2;
        }
        if (n == this.mTileDividerIndex) {
            return 4;
        }
        if (this.mTiles.get(n) == null) {
            return 1;
        }
        return 0;
    }
    
    public GridLayoutManager.SpanSizeLookup getSizeLookup() {
        return this.mSizeLookup;
    }
    
    public void onBindViewHolder(final Holder holder, final int n) {
        if (((RecyclerView.ViewHolder)holder).getItemViewType() == 3) {
            this.setSelectableForHeaders(holder.itemView);
            return;
        }
        final int itemViewType = ((RecyclerView.ViewHolder)holder).getItemViewType();
        int n2 = 4;
        final boolean b = false;
        if (itemViewType == 4) {
            final View itemView = holder.itemView;
            if (this.mTileDividerIndex < this.mTiles.size() - 1) {
                n2 = 0;
            }
            itemView.setVisibility(n2);
            return;
        }
        if (((RecyclerView.ViewHolder)holder).getItemViewType() == 1) {
            final Resources resources = this.mContext.getResources();
            String text;
            if (this.mCurrentDrag == null) {
                text = resources.getString(R$string.drag_to_add_tiles);
            }
            else if (!this.canRemoveTiles() && ((RecyclerView.ViewHolder)this.mCurrentDrag).getAdapterPosition() < this.mEditIndex) {
                text = resources.getString(R$string.drag_to_remove_disabled, new Object[] { this.mMinNumTiles });
            }
            else {
                text = resources.getString(R$string.drag_to_remove_tiles);
            }
            ((TextView)holder.itemView.findViewById(16908310)).setText((CharSequence)text);
            this.setSelectableForHeaders(holder.itemView);
            return;
        }
        if (((RecyclerView.ViewHolder)holder).getItemViewType() == 2) {
            holder.mTileView.setClickable(true);
            holder.mTileView.setFocusable(true);
            holder.mTileView.setFocusableInTouchMode(true);
            holder.mTileView.setVisibility(0);
            holder.mTileView.setImportantForAccessibility(1);
            holder.mTileView.setContentDescription((CharSequence)this.mContext.getString(R$string.accessibility_qs_edit_tile_add, new Object[] { this.mAccessibilityFromLabel, n }));
            holder.mTileView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    TileAdapter.this.selectPosition(((RecyclerView.ViewHolder)holder).getAdapterPosition(), view);
                }
            });
            this.focusOnHolder(holder);
            return;
        }
        final TileInfo tileInfo = this.mTiles.get(n);
        if (n > this.mEditIndex) {
            final QSTile.State state = tileInfo.state;
            state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_add_tile_label, new Object[] { state.label });
        }
        else {
            final int mAccessibilityAction = this.mAccessibilityAction;
            if (mAccessibilityAction == 1) {
                tileInfo.state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_tile_add, new Object[] { this.mAccessibilityFromLabel, n });
            }
            else if (mAccessibilityAction == 2) {
                tileInfo.state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_tile_move, new Object[] { this.mAccessibilityFromLabel, n });
            }
            else {
                tileInfo.state.contentDescription = this.mContext.getString(R$string.accessibility_qs_edit_tile_label, new Object[] { n, tileInfo.state.label });
            }
        }
        holder.mTileView.handleStateChanged(tileInfo.state);
        holder.mTileView.setShowAppLabel(n > this.mEditIndex && !tileInfo.isSystem);
        if (this.mAccessibilityManager.isTouchExplorationEnabled()) {
            boolean focusableInTouchMode = false;
            Label_0568: {
                if (this.mAccessibilityAction != 0) {
                    focusableInTouchMode = b;
                    if (n >= this.mEditIndex) {
                        break Label_0568;
                    }
                }
                focusableInTouchMode = true;
            }
            holder.mTileView.setClickable(focusableInTouchMode);
            holder.mTileView.setFocusable(focusableInTouchMode);
            final CustomizeTileView access$100 = holder.mTileView;
            if (focusableInTouchMode) {
                n2 = 1;
            }
            access$100.setImportantForAccessibility(n2);
            holder.mTileView.setFocusableInTouchMode(focusableInTouchMode);
            if (focusableInTouchMode) {
                holder.mTileView.setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                    public void onClick(final View view) {
                        final int adapterPosition = ((RecyclerView.ViewHolder)holder).getAdapterPosition();
                        if (adapterPosition == -1) {
                            return;
                        }
                        if (TileAdapter.this.mAccessibilityAction != 0) {
                            TileAdapter.this.selectPosition(adapterPosition, view);
                        }
                        else if (adapterPosition < TileAdapter.this.mEditIndex && TileAdapter.this.canRemoveTiles()) {
                            TileAdapter.this.showAccessibilityDialog(adapterPosition, view);
                        }
                        else if (adapterPosition < TileAdapter.this.mEditIndex && !TileAdapter.this.canRemoveTiles()) {
                            TileAdapter.this.startAccessibleMove(adapterPosition);
                        }
                        else {
                            TileAdapter.this.startAccessibleAdd(adapterPosition);
                        }
                    }
                });
                if (n == this.mAccessibilityFromIndex) {
                    this.focusOnHolder(holder);
                }
            }
        }
    }
    
    public Holder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
        final Context context = viewGroup.getContext();
        final LayoutInflater from = LayoutInflater.from(context);
        if (n == 3) {
            return new Holder(from.inflate(R$layout.qs_customize_header, viewGroup, false));
        }
        if (n == 4) {
            return new Holder(from.inflate(R$layout.qs_customize_tile_divider, viewGroup, false));
        }
        if (n == 1) {
            return new Holder(from.inflate(R$layout.qs_customize_divider, viewGroup, false));
        }
        final FrameLayout frameLayout = (FrameLayout)from.inflate(R$layout.qs_customize_tile_frame, viewGroup, false);
        frameLayout.addView((View)new CustomizeTileView(context, new QSIconViewImpl(context)));
        return new Holder((View)frameLayout);
    }
    
    public boolean onFailedToRecycleView(final Holder holder) {
        holder.clearDrag();
        return true;
    }
    
    @Override
    public void onTilesChanged(final List<TileInfo> mAllTiles) {
        this.mAllTiles = mAllTiles;
        this.recalcSpecs();
    }
    
    public void resetTileSpecs(final QSTileHost qsTileHost, final List<String> tileSpecs) {
        qsTileHost.changeTiles(this.mCurrentSpecs, tileSpecs);
        this.setTileSpecs(tileSpecs);
    }
    
    public void saveSpecs(final QSTileHost qsTileHost) {
        final ArrayList<String> mCurrentSpecs = new ArrayList<String>();
        this.clearAccessibilityState();
        for (int n = 1; n < this.mTiles.size() && this.mTiles.get(n) != null; ++n) {
            mCurrentSpecs.add(this.mTiles.get(n).spec);
        }
        qsTileHost.changeTiles(this.mCurrentSpecs, mCurrentSpecs);
        this.mCurrentSpecs = mCurrentSpecs;
    }
    
    public void setHost(final QSTileHost mHost) {
        this.mHost = mHost;
    }
    
    public void setTileSpecs(final List<String> mCurrentSpecs) {
        if (mCurrentSpecs.equals(this.mCurrentSpecs)) {
            return;
        }
        this.mCurrentSpecs = mCurrentSpecs;
        this.recalcSpecs();
    }
    
    public class Holder extends ViewHolder
    {
        private CustomizeTileView mTileView;
        
        public Holder(final TileAdapter tileAdapter, final View view) {
            super(view);
            if (view instanceof FrameLayout) {
                (this.mTileView = (CustomizeTileView)((FrameLayout)view).getChildAt(0)).setBackground((Drawable)null);
                this.mTileView.getIcon().disableAnimation();
            }
        }
        
        public void clearDrag() {
            super.itemView.clearAnimation();
            this.mTileView.findViewById(R$id.tile_label).clearAnimation();
            this.mTileView.findViewById(R$id.tile_label).setAlpha(1.0f);
            this.mTileView.getAppLabel().clearAnimation();
            this.mTileView.getAppLabel().setAlpha(0.6f);
        }
        
        public void startDrag() {
            super.itemView.animate().setDuration(100L).scaleX(1.2f).scaleY(1.2f);
            this.mTileView.findViewById(R$id.tile_label).animate().setDuration(100L).alpha(0.0f);
            this.mTileView.getAppLabel().animate().setDuration(100L).alpha(0.0f);
        }
        
        public void stopDrag() {
            super.itemView.animate().setDuration(100L).scaleX(1.0f).scaleY(1.0f);
            this.mTileView.findViewById(R$id.tile_label).animate().setDuration(100L).alpha(1.0f);
            this.mTileView.getAppLabel().animate().setDuration(100L).alpha(0.6f);
        }
    }
    
    private class TileItemDecoration extends ItemDecoration
    {
        private final Drawable mDrawable;
        
        private TileItemDecoration(final Context context) {
            this.mDrawable = context.getDrawable(R$drawable.qs_customize_tile_decoration);
        }
        
        @Override
        public void onDraw(final Canvas canvas, final RecyclerView recyclerView, final State state) {
            super.onDraw(canvas, recyclerView, state);
            final int childCount = recyclerView.getChildCount();
            final int width = recyclerView.getWidth();
            final int bottom = recyclerView.getBottom();
            for (int i = 0; i < childCount; ++i) {
                final View child = recyclerView.getChildAt(i);
                final RecyclerView.ViewHolder childViewHolder = recyclerView.getChildViewHolder(child);
                if (childViewHolder.getAdapterPosition() != 0 && (childViewHolder.getAdapterPosition() >= TileAdapter.this.mEditIndex || child instanceof TextView)) {
                    this.mDrawable.setBounds(0, child.getTop() + ((LayoutParams)child.getLayoutParams()).topMargin + Math.round(ViewCompat.getTranslationY(child)), width, bottom);
                    this.mDrawable.draw(canvas);
                    break;
                }
            }
        }
    }
}
