// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.graphics.drawable;

import android.graphics.drawable.Drawable;
import android.util.StateSet;
import android.util.AttributeSet;
import android.content.res.Resources$Theme;
import android.content.res.Resources;
import android.annotation.SuppressLint;

@SuppressLint({ "RestrictedAPI" })
class StateListDrawable extends DrawableContainer
{
    private boolean mMutated;
    private StateListState mStateListState;
    
    StateListDrawable(final StateListState constantState) {
        if (constantState != null) {
            this.setConstantState(constantState);
        }
    }
    
    StateListDrawable(final StateListState stateListState, final Resources resources) {
        this.setConstantState(new StateListState(stateListState, this, resources));
        this.onStateChange(this.getState());
    }
    
    @Override
    public void applyTheme(final Resources$Theme resources$Theme) {
        super.applyTheme(resources$Theme);
        this.onStateChange(this.getState());
    }
    
    @Override
    void clearMutated() {
        super.clearMutated();
        this.mMutated = false;
    }
    
    StateListState cloneConstantState() {
        return new StateListState(this.mStateListState, this, null);
    }
    
    int[] extractStateSet(final AttributeSet set) {
        final int attributeCount = set.getAttributeCount();
        final int[] array = new int[attributeCount];
        int n;
        int n2;
        for (int i = n = 0; i < attributeCount; ++i, n = n2) {
            final int attributeNameResource = set.getAttributeNameResource(i);
            n2 = n;
            if (attributeNameResource != 0) {
                n2 = n;
                if (attributeNameResource != 16842960) {
                    n2 = n;
                    if (attributeNameResource != 16843161) {
                        int n3;
                        if (set.getAttributeBooleanValue(i, false)) {
                            n3 = attributeNameResource;
                        }
                        else {
                            n3 = -attributeNameResource;
                        }
                        array[n] = n3;
                        n2 = n + 1;
                    }
                }
            }
        }
        return StateSet.trimStateSet(array, n);
    }
    
    public boolean isStateful() {
        return true;
    }
    
    @Override
    public Drawable mutate() {
        if (!this.mMutated) {
            super.mutate();
            this.mStateListState.mutate();
            this.mMutated = true;
        }
        return this;
    }
    
    @Override
    protected boolean onStateChange(final int[] array) {
        final boolean onStateChange = super.onStateChange(array);
        int n;
        if ((n = this.mStateListState.indexOfStateSet(array)) < 0) {
            n = this.mStateListState.indexOfStateSet(StateSet.WILD_CARD);
        }
        return this.selectDrawable(n) || onStateChange;
    }
    
    @Override
    void setConstantState(final DrawableContainerState constantState) {
        super.setConstantState(constantState);
        if (constantState instanceof StateListState) {
            this.mStateListState = (StateListState)constantState;
        }
    }
    
    static class StateListState extends DrawableContainerState
    {
        int[][] mStateSets;
        
        StateListState(final StateListState stateListState, final StateListDrawable stateListDrawable, final Resources resources) {
            super((DrawableContainerState)stateListState, stateListDrawable, resources);
            if (stateListState != null) {
                this.mStateSets = stateListState.mStateSets;
            }
            else {
                this.mStateSets = new int[((DrawableContainerState)this).getCapacity()][];
            }
        }
        
        int addStateSet(final int[] array, final Drawable drawable) {
            final int addChild = ((DrawableContainerState)this).addChild(drawable);
            this.mStateSets[addChild] = array;
            return addChild;
        }
        
        @Override
        public void growArray(final int n, final int n2) {
            super.growArray(n, n2);
            final int[][] mStateSets = new int[n2][];
            System.arraycopy(this.mStateSets, 0, mStateSets, 0, n);
            this.mStateSets = mStateSets;
        }
        
        int indexOfStateSet(final int[] array) {
            final int[][] mStateSets = this.mStateSets;
            for (int childCount = ((DrawableContainerState)this).getChildCount(), i = 0; i < childCount; ++i) {
                if (StateSet.stateSetMatches(mStateSets[i], array)) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        void mutate() {
            final int[][] mStateSets = this.mStateSets;
            final int[][] mStateSets2 = new int[mStateSets.length][];
            for (int i = mStateSets.length - 1; i >= 0; --i) {
                final int[][] mStateSets3 = this.mStateSets;
                int[] array;
                if (mStateSets3[i] != null) {
                    array = mStateSets3[i].clone();
                }
                else {
                    array = null;
                }
                mStateSets2[i] = array;
            }
            this.mStateSets = mStateSets2;
        }
        
        public Drawable newDrawable() {
            return new StateListDrawable(this, null);
        }
        
        public Drawable newDrawable(final Resources resources) {
            return new StateListDrawable(this, resources);
        }
    }
}
