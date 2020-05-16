// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import android.view.View;

class ItemAlignment
{
    public final Axis horizontal;
    private int mOrientation;
    public final Axis vertical;
    
    ItemAlignment() {
        this.mOrientation = 0;
        this.vertical = new Axis(1);
        this.horizontal = new Axis(0);
    }
    
    public final void setOrientation(final int mOrientation) {
        this.mOrientation = mOrientation;
    }
    
    static final class Axis extends ItemAlignmentDef
    {
        private int mOrientation;
        
        Axis(final int mOrientation) {
            this.mOrientation = mOrientation;
        }
        
        public int getAlignmentPosition(final View view) {
            return ItemAlignmentFacetHelper.getAlignmentPosition(view, this, this.mOrientation);
        }
    }
}
