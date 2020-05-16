// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

public final class ItemAlignmentFacet
{
    private ItemAlignmentDef[] mAlignmentDefs;
    
    public ItemAlignmentFacet() {
        this.mAlignmentDefs = new ItemAlignmentDef[] { new ItemAlignmentDef() };
    }
    
    public ItemAlignmentDef[] getAlignmentDefs() {
        return this.mAlignmentDefs;
    }
    
    public static class ItemAlignmentDef
    {
        private boolean mAlignToBaseline;
        int mFocusViewId;
        int mOffset;
        float mOffsetPercent;
        boolean mOffsetWithPadding;
        int mViewId;
        
        public ItemAlignmentDef() {
            this.mViewId = -1;
            this.mFocusViewId = -1;
            this.mOffset = 0;
            this.mOffsetPercent = 50.0f;
            this.mOffsetWithPadding = false;
        }
        
        public final int getItemAlignmentFocusViewId() {
            int n = this.mFocusViewId;
            if (n == -1) {
                n = this.mViewId;
            }
            return n;
        }
        
        public boolean isAlignedToTextViewBaseLine() {
            return this.mAlignToBaseline;
        }
    }
}
