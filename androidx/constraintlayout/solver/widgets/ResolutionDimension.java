// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

public class ResolutionDimension extends ResolutionNode
{
    float value;
    
    public ResolutionDimension() {
        this.value = 0.0f;
    }
    
    public void remove() {
        super.state = 2;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.value = 0.0f;
    }
    
    public void resolve(final int n) {
        if (super.state == 0 || this.value != n) {
            this.value = (float)n;
            if (super.state == 1) {
                this.invalidate();
            }
            this.didResolve();
        }
    }
}
