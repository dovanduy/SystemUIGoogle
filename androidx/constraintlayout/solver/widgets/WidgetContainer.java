// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Cache;
import java.util.ArrayList;

public class WidgetContainer extends ConstraintWidget
{
    protected ArrayList<ConstraintWidget> mChildren;
    
    public WidgetContainer() {
        this.mChildren = new ArrayList<ConstraintWidget>();
    }
    
    public void add(final ConstraintWidget e) {
        this.mChildren.add(e);
        if (e.getParent() != null) {
            ((WidgetContainer)e.getParent()).remove(e);
        }
        e.setParent(this);
    }
    
    public ConstraintWidgetContainer getRootConstraintContainer() {
        ConstraintWidget parent = this.getParent();
        ConstraintWidgetContainer constraintWidgetContainer;
        if (this instanceof ConstraintWidgetContainer) {
            constraintWidgetContainer = (ConstraintWidgetContainer)this;
        }
        else {
            constraintWidgetContainer = null;
        }
        while (parent != null) {
            final ConstraintWidget parent2 = parent.getParent();
            if (parent instanceof ConstraintWidgetContainer) {
                constraintWidgetContainer = (ConstraintWidgetContainer)parent;
            }
            parent = parent2;
        }
        return constraintWidgetContainer;
    }
    
    public void layout() {
        this.updateDrawPosition();
        final ArrayList<ConstraintWidget> mChildren = this.mChildren;
        if (mChildren == null) {
            return;
        }
        for (int size = mChildren.size(), i = 0; i < size; ++i) {
            final ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof WidgetContainer) {
                ((WidgetContainer)constraintWidget).layout();
            }
        }
    }
    
    public void remove(final ConstraintWidget o) {
        this.mChildren.remove(o);
        o.setParent(null);
    }
    
    public void removeAllChildren() {
        this.mChildren.clear();
    }
    
    @Override
    public void reset() {
        this.mChildren.clear();
        super.reset();
    }
    
    @Override
    public void resetSolverVariables(final Cache cache) {
        super.resetSolverVariables(cache);
        for (int size = this.mChildren.size(), i = 0; i < size; ++i) {
            this.mChildren.get(i).resetSolverVariables(cache);
        }
    }
    
    @Override
    public void setOffset(int i, int size) {
        super.setOffset(i, size);
        for (size = this.mChildren.size(), i = 0; i < size; ++i) {
            this.mChildren.get(i).setOffset(this.getRootX(), this.getRootY());
        }
    }
    
    @Override
    public void updateDrawPosition() {
        super.updateDrawPosition();
        final ArrayList<ConstraintWidget> mChildren = this.mChildren;
        if (mChildren == null) {
            return;
        }
        for (int size = mChildren.size(), i = 0; i < size; ++i) {
            final ConstraintWidget constraintWidget = this.mChildren.get(i);
            constraintWidget.setOffset(this.getDrawX(), this.getDrawY());
            if (!(constraintWidget instanceof ConstraintWidgetContainer)) {
                constraintWidget.updateDrawPosition();
            }
        }
    }
}
