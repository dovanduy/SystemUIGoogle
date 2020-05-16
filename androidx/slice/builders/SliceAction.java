// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.builders;

import androidx.slice.Slice;
import androidx.core.graphics.drawable.IconCompat;
import android.app.PendingIntent;
import androidx.slice.core.SliceActionImpl;

public class SliceAction implements androidx.slice.core.SliceAction
{
    private SliceActionImpl mSliceAction;
    
    public SliceAction(final PendingIntent pendingIntent, final IconCompat iconCompat, final int n, final CharSequence charSequence) {
        this.mSliceAction = new SliceActionImpl(pendingIntent, iconCompat, n, charSequence);
    }
    
    public static SliceAction create(final PendingIntent pendingIntent, final IconCompat iconCompat, final int n, final CharSequence charSequence) {
        return new SliceAction(pendingIntent, iconCompat, n, charSequence);
    }
    
    public static SliceAction createDeeplink(final PendingIntent pendingIntent, final IconCompat iconCompat, final int n, final CharSequence charSequence) {
        final SliceAction sliceAction = new SliceAction(pendingIntent, iconCompat, n, charSequence);
        sliceAction.mSliceAction.setActivity(true);
        return sliceAction;
    }
    
    public Slice buildSlice(final Slice.Builder builder) {
        return this.mSliceAction.buildSlice(builder);
    }
    
    @Override
    public IconCompat getIcon() {
        return this.mSliceAction.getIcon();
    }
    
    @Override
    public int getImageMode() {
        return this.mSliceAction.getImageMode();
    }
    
    @Override
    public int getPriority() {
        return this.mSliceAction.getPriority();
    }
    
    public CharSequence getTitle() {
        return this.mSliceAction.getTitle();
    }
    
    @Override
    public boolean isToggle() {
        return this.mSliceAction.isToggle();
    }
    
    public void setPrimaryAction(final Slice.Builder builder) {
        builder.addAction(this.mSliceAction.getAction(), this.mSliceAction.buildPrimaryActionSlice(builder), this.mSliceAction.getSubtype());
    }
}
