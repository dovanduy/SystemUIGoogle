// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

import java.util.Iterator;
import android.app.PendingIntent$CanceledException;
import android.content.Intent;
import android.util.Log;
import android.os.Build$VERSION;
import androidx.slice.core.SliceQuery;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceAction;
import java.util.List;
import android.view.ViewParent;
import android.widget.TextView;
import android.app.RemoteInput;
import android.view.View$OnClickListener;
import androidx.slice.SliceItem;
import android.view.ViewGroup;
import android.widget.LinearLayout$LayoutParams;
import androidx.core.widget.ImageViewCompat;
import android.content.res.ColorStateList;
import android.widget.ImageView$ScaleType;
import android.widget.ImageView;
import androidx.core.graphics.drawable.IconCompat;
import android.view.View;
import android.view.ViewGroup$LayoutParams;
import android.widget.FrameLayout$LayoutParams;
import android.util.TypedValue;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

public class ActionRow extends FrameLayout
{
    private final LinearLayout mActionsGroup;
    private int mColor;
    private final int mIconPadding;
    private final int mSize;
    
    public ActionRow(final Context context, final boolean b) {
        super(context);
        this.mColor = -16777216;
        this.mSize = (int)TypedValue.applyDimension(1, 48.0f, context.getResources().getDisplayMetrics());
        this.mIconPadding = (int)TypedValue.applyDimension(1, 12.0f, context.getResources().getDisplayMetrics());
        (this.mActionsGroup = new LinearLayout(context)).setOrientation(0);
        this.mActionsGroup.setLayoutParams((ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -2));
        this.addView((View)this.mActionsGroup);
    }
    
    private ImageView addAction(final IconCompat iconCompat, final boolean b) {
        final ImageView imageView = new ImageView(this.getContext());
        final int mIconPadding = this.mIconPadding;
        imageView.setPadding(mIconPadding, mIconPadding, mIconPadding, mIconPadding);
        imageView.setScaleType(ImageView$ScaleType.FIT_CENTER);
        imageView.setImageDrawable(iconCompat.loadDrawable(this.getContext()));
        if (b) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(this.mColor));
        }
        imageView.setBackground(SliceViewUtil.getDrawable(this.getContext(), 16843534));
        imageView.setTag((Object)b);
        this.addAction((View)imageView);
        return imageView;
    }
    
    private void addAction(final View view) {
        final LinearLayout mActionsGroup = this.mActionsGroup;
        final int mSize = this.mSize;
        mActionsGroup.addView(view, (ViewGroup$LayoutParams)new LinearLayout$LayoutParams(mSize, mSize, 1.0f));
    }
    
    private void createRemoteInputView(final int backgroundColor, final Context context) {
        final RemoteInputView inflate = RemoteInputView.inflate(context, (ViewGroup)this);
        ((View)inflate).setVisibility(4);
        this.addView((View)inflate, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(-1, -1));
        ((View)inflate).setBackgroundColor(backgroundColor);
    }
    
    private RemoteInputView findRemoteInputView(final View view) {
        if (view == null) {
            return null;
        }
        return (RemoteInputView)view.findViewWithTag(RemoteInputView.VIEW_TAG);
    }
    
    private void handleSetRemoteInputActions(final SliceItem sliceItem, final SliceItem sliceItem2, final SliceItem sliceItem3) {
        if (sliceItem.getRemoteInput().getAllowFreeFormInput()) {
            this.addAction(sliceItem2.getIcon(), sliceItem2.hasHint("no_tint") ^ true).setOnClickListener((View$OnClickListener)new View$OnClickListener() {
                public void onClick(final View view) {
                    ActionRow.this.handleRemoteInputClick(view, sliceItem3, sliceItem.getRemoteInput());
                }
            });
            this.createRemoteInputView(this.mColor, this.getContext());
        }
    }
    
    private void setColor(int i) {
        this.mColor = i;
        View child;
        for (i = 0; i < this.mActionsGroup.getChildCount(); ++i) {
            child = this.mActionsGroup.getChildAt(i);
            if ((int)child.getTag() == 0) {
                ImageViewCompat.setImageTintList((ImageView)child, ColorStateList.valueOf(this.mColor));
            }
        }
    }
    
    boolean handleRemoteInputClick(final View view, final SliceItem action, final RemoteInput remoteInput) {
        if (remoteInput == null) {
            return false;
        }
        ViewParent viewParent = view.getParent().getParent();
        RemoteInputView remoteInputView = null;
        RemoteInputView remoteInputView2;
        while (true) {
            remoteInputView2 = remoteInputView;
            if (viewParent == null) {
                break;
            }
            if (viewParent instanceof View) {
                remoteInputView2 = this.findRemoteInputView((View)viewParent);
                if ((remoteInputView = remoteInputView2) != null) {
                    break;
                }
            }
            viewParent = viewParent.getParent();
        }
        if (remoteInputView2 == null) {
            return false;
        }
        int n;
        final int a = n = view.getWidth();
        if (view instanceof TextView) {
            final TextView textView = (TextView)view;
            n = a;
            if (textView.getLayout() != null) {
                n = Math.min(a, (int)textView.getLayout().getLineWidth(0) + (textView.getCompoundPaddingLeft() + textView.getCompoundPaddingRight()));
            }
        }
        final int n2 = view.getLeft() + n / 2;
        final int n3 = view.getTop() + view.getHeight() / 2;
        final int width = remoteInputView2.getWidth();
        final int n4 = remoteInputView2.getHeight() - n3;
        final int max = Math.max(n2 + n3, n2 + n4);
        final int n5 = width - n2;
        remoteInputView2.setRevealParameters(n2, n3, Math.max(max, Math.max(n5 + n3, n5 + n4)));
        remoteInputView2.setAction(action);
        remoteInputView2.setRemoteInput(new RemoteInput[] { remoteInput }, remoteInput);
        remoteInputView2.focusAnimated();
        return true;
    }
    
    public void setActions(final List<SliceAction> list, int n) {
        this.removeAllViews();
        this.mActionsGroup.removeAllViews();
        this.addView((View)this.mActionsGroup);
        if (n != -1) {
            this.setColor(n);
        }
        final Iterator<SliceAction> iterator = list.iterator();
        while (true) {
            final boolean hasNext = iterator.hasNext();
            n = 0;
            boolean b = false;
            if (!hasNext) {
                if (this.getChildCount() == 0) {
                    n = 8;
                }
                this.setVisibility(n);
                return;
            }
            final SliceAction sliceAction = iterator.next();
            if (this.mActionsGroup.getChildCount() >= 5) {
                return;
            }
            final SliceActionImpl sliceActionImpl = (SliceActionImpl)sliceAction;
            final SliceItem sliceItem = sliceActionImpl.getSliceItem();
            final SliceItem actionItem = sliceActionImpl.getActionItem();
            final SliceItem find = SliceQuery.find(sliceItem, "input");
            final SliceItem find2 = SliceQuery.find(sliceItem, "image");
            if (find != null && find2 != null) {
                if (Build$VERSION.SDK_INT >= 21) {
                    this.handleSetRemoteInputActions(find, find2, actionItem);
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Received RemoteInput on API <20 ");
                    sb.append(find);
                    Log.w("ActionRow", sb.toString());
                }
            }
            else {
                if (sliceAction.getIcon() == null) {
                    continue;
                }
                final IconCompat icon = sliceAction.getIcon();
                if (icon == null || actionItem == null) {
                    continue;
                }
                if (sliceAction.getImageMode() == 0) {
                    b = true;
                }
                this.addAction(icon, b).setOnClickListener((View$OnClickListener)new View$OnClickListener(this) {
                    public void onClick(final View view) {
                        try {
                            actionItem.fireAction(null, null);
                        }
                        catch (PendingIntent$CanceledException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
