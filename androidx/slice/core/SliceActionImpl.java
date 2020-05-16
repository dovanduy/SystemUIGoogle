// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.core;

import androidx.slice.Slice;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.SliceItem;
import android.app.PendingIntent;

public class SliceActionImpl implements SliceAction
{
    private PendingIntent mAction;
    private SliceItem mActionItem;
    private CharSequence mContentDescription;
    private IconCompat mIcon;
    private int mImageMode;
    private boolean mIsActivity;
    private boolean mIsChecked;
    private boolean mIsToggle;
    private int mPriority;
    private SliceItem mSliceItem;
    private CharSequence mTitle;
    
    public SliceActionImpl(final PendingIntent mAction, final IconCompat mIcon, final int mImageMode, final CharSequence mTitle) {
        this.mImageMode = 5;
        this.mPriority = -1;
        this.mAction = mAction;
        this.mIcon = mIcon;
        this.mTitle = mTitle;
        this.mImageMode = mImageMode;
    }
    
    public SliceActionImpl(SliceItem sliceItem) {
        this.mImageMode = 5;
        int int1 = -1;
        this.mPriority = -1;
        this.mSliceItem = sliceItem;
        sliceItem = SliceQuery.find(sliceItem, "action");
        if (sliceItem == null) {
            return;
        }
        this.mActionItem = sliceItem;
        this.mAction = sliceItem.getAction();
        final SliceItem find = SliceQuery.find(sliceItem.getSlice(), "image");
        if (find != null) {
            this.mIcon = find.getIcon();
            this.mImageMode = parseImageMode(find);
        }
        final SliceItem find2 = SliceQuery.find(sliceItem.getSlice(), "text", "title", null);
        if (find2 != null) {
            this.mTitle = find2.getSanitizedText();
        }
        final SliceItem subtype = SliceQuery.findSubtype(sliceItem.getSlice(), "text", "content_description");
        if (subtype != null) {
            this.mContentDescription = subtype.getText();
        }
        final boolean equals = "toggle".equals(sliceItem.getSubType());
        this.mIsToggle = equals;
        if (equals) {
            this.mIsChecked = sliceItem.hasHint("selected");
        }
        this.mIsActivity = this.mSliceItem.hasHint("activity");
        sliceItem = SliceQuery.findSubtype(sliceItem.getSlice(), "int", "priority");
        if (sliceItem != null) {
            int1 = sliceItem.getInt();
        }
        this.mPriority = int1;
    }
    
    private Slice.Builder buildSliceContent(final Slice.Builder builder) {
        final Slice.Builder builder2 = new Slice.Builder(builder);
        if (this.mIcon != null) {
            String[] array;
            if (this.mImageMode == 0) {
                array = new String[0];
            }
            else {
                array = new String[] { "no_tint" };
            }
            builder2.addIcon(this.mIcon, null, array);
        }
        final CharSequence mTitle = this.mTitle;
        if (mTitle != null) {
            builder2.addText(mTitle, null, "title");
        }
        final CharSequence mContentDescription = this.mContentDescription;
        if (mContentDescription != null) {
            builder2.addText(mContentDescription, "content_description", new String[0]);
        }
        if (this.mIsToggle && this.mIsChecked) {
            builder2.addHints("selected");
        }
        final int mPriority = this.mPriority;
        if (mPriority != -1) {
            builder2.addInt(mPriority, "priority", new String[0]);
        }
        if (this.mIsActivity) {
            builder.addHints("activity");
        }
        return builder2;
    }
    
    public static int parseImageMode(final SliceItem sliceItem) {
        if (!sliceItem.hasHint("no_tint")) {
            return 0;
        }
        if (sliceItem.hasHint("raw")) {
            int n;
            if (sliceItem.hasHint("large")) {
                n = 4;
            }
            else {
                n = 3;
            }
            return n;
        }
        if (sliceItem.hasHint("large")) {
            return 2;
        }
        return 1;
    }
    
    public Slice buildPrimaryActionSlice(Slice.Builder buildSliceContent) {
        buildSliceContent = this.buildSliceContent(buildSliceContent);
        buildSliceContent.addHints("shortcut", "title");
        return buildSliceContent.build();
    }
    
    public Slice buildSlice(final Slice.Builder builder) {
        builder.addHints("shortcut");
        builder.addAction(this.mAction, this.buildSliceContent(builder).build(), this.getSubtype());
        return builder.build();
    }
    
    public PendingIntent getAction() {
        PendingIntent pendingIntent = this.mAction;
        if (pendingIntent == null) {
            pendingIntent = this.mActionItem.getAction();
        }
        return pendingIntent;
    }
    
    public SliceItem getActionItem() {
        return this.mActionItem;
    }
    
    public CharSequence getContentDescription() {
        return this.mContentDescription;
    }
    
    @Override
    public IconCompat getIcon() {
        return this.mIcon;
    }
    
    @Override
    public int getImageMode() {
        return this.mImageMode;
    }
    
    @Override
    public int getPriority() {
        return this.mPriority;
    }
    
    public SliceItem getSliceItem() {
        return this.mSliceItem;
    }
    
    public String getSubtype() {
        String s;
        if (this.mIsToggle) {
            s = "toggle";
        }
        else {
            s = null;
        }
        return s;
    }
    
    public CharSequence getTitle() {
        return this.mTitle;
    }
    
    public boolean isChecked() {
        return this.mIsChecked;
    }
    
    public boolean isDefaultToggle() {
        return this.mIsToggle && this.mIcon == null;
    }
    
    @Override
    public boolean isToggle() {
        return this.mIsToggle;
    }
    
    public void setActivity(final boolean mIsActivity) {
        this.mIsActivity = mIsActivity;
    }
}
