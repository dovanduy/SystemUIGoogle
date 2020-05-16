// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.solver.SolverVariable;

public class ConstraintAnchor
{
    private int mConnectionCreator;
    int mGoneMargin;
    public int mMargin;
    final ConstraintWidget mOwner;
    private ResolutionAnchor mResolutionAnchor;
    SolverVariable mSolverVariable;
    private Strength mStrength;
    ConstraintAnchor mTarget;
    final Type mType;
    
    public ConstraintAnchor(final ConstraintWidget mOwner, final Type mType) {
        this.mResolutionAnchor = new ResolutionAnchor(this);
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.NONE;
        final ConnectionType relaxed = ConnectionType.RELAXED;
        this.mConnectionCreator = 0;
        this.mOwner = mOwner;
        this.mType = mType;
    }
    
    public boolean connect(final ConstraintAnchor mTarget, final int mMargin, final int mGoneMargin, final Strength mStrength, final int mConnectionCreator, final boolean b) {
        if (mTarget == null) {
            this.mTarget = null;
            this.mMargin = 0;
            this.mGoneMargin = -1;
            this.mStrength = Strength.NONE;
            this.mConnectionCreator = 2;
            return true;
        }
        if (!b && !this.isValidConnection(mTarget)) {
            return false;
        }
        this.mTarget = mTarget;
        if (mMargin > 0) {
            this.mMargin = mMargin;
        }
        else {
            this.mMargin = 0;
        }
        this.mGoneMargin = mGoneMargin;
        this.mStrength = mStrength;
        this.mConnectionCreator = mConnectionCreator;
        return true;
    }
    
    public boolean connect(final ConstraintAnchor constraintAnchor, final int n, final Strength strength, final int n2) {
        return this.connect(constraintAnchor, n, -1, strength, n2, false);
    }
    
    public int getConnectionCreator() {
        return this.mConnectionCreator;
    }
    
    public int getMargin() {
        if (this.mOwner.getVisibility() == 8) {
            return 0;
        }
        if (this.mGoneMargin > -1) {
            final ConstraintAnchor mTarget = this.mTarget;
            if (mTarget != null && mTarget.mOwner.getVisibility() == 8) {
                return this.mGoneMargin;
            }
        }
        return this.mMargin;
    }
    
    public ConstraintWidget getOwner() {
        return this.mOwner;
    }
    
    public ResolutionAnchor getResolutionNode() {
        return this.mResolutionAnchor;
    }
    
    public SolverVariable getSolverVariable() {
        return this.mSolverVariable;
    }
    
    public Strength getStrength() {
        return this.mStrength;
    }
    
    public ConstraintAnchor getTarget() {
        return this.mTarget;
    }
    
    public Type getType() {
        return this.mType;
    }
    
    public boolean isConnected() {
        return this.mTarget != null;
    }
    
    public boolean isValidConnection(final ConstraintAnchor constraintAnchor) {
        final boolean b = false;
        final boolean b2 = false;
        final boolean b3 = false;
        if (constraintAnchor == null) {
            return false;
        }
        final Type type = constraintAnchor.getType();
        final Type mType = this.mType;
        if (type == mType) {
            return mType != Type.BASELINE || (constraintAnchor.getOwner().hasBaseline() && this.getOwner().hasBaseline());
        }
        switch (ConstraintAnchor$1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintAnchor$Type[mType.ordinal()]) {
            default: {
                throw new AssertionError((Object)this.mType.name());
            }
            case 6:
            case 7:
            case 8:
            case 9: {
                return false;
            }
            case 4:
            case 5: {
                boolean b4 = type == Type.TOP || type == Type.BOTTOM;
                if (constraintAnchor.getOwner() instanceof Guideline) {
                    boolean b5 = false;
                    Label_0202: {
                        if (!b4) {
                            b5 = b3;
                            if (type != Type.CENTER_Y) {
                                break Label_0202;
                            }
                        }
                        b5 = true;
                    }
                    b4 = b5;
                }
                return b4;
            }
            case 2:
            case 3: {
                boolean b6 = type == Type.LEFT || type == Type.RIGHT;
                if (constraintAnchor.getOwner() instanceof Guideline) {
                    boolean b7 = false;
                    Label_0267: {
                        if (!b6) {
                            b7 = b;
                            if (type != Type.CENTER_X) {
                                break Label_0267;
                            }
                        }
                        b7 = true;
                    }
                    b6 = b7;
                }
                return b6;
            }
            case 1: {
                boolean b8 = b2;
                if (type != Type.BASELINE) {
                    b8 = b2;
                    if (type != Type.CENTER_X) {
                        b8 = b2;
                        if (type != Type.CENTER_Y) {
                            b8 = true;
                        }
                    }
                }
                return b8;
            }
        }
    }
    
    public void reset() {
        this.mTarget = null;
        this.mMargin = 0;
        this.mGoneMargin = -1;
        this.mStrength = Strength.STRONG;
        this.mConnectionCreator = 0;
        final ConnectionType relaxed = ConnectionType.RELAXED;
        this.mResolutionAnchor.reset();
    }
    
    public void resetSolverVariable(final Cache cache) {
        final SolverVariable mSolverVariable = this.mSolverVariable;
        if (mSolverVariable == null) {
            this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED, null);
        }
        else {
            mSolverVariable.reset();
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.mOwner.getDebugName());
        sb.append(":");
        sb.append(this.mType.toString());
        return sb.toString();
    }
    
    public enum ConnectionType
    {
        RELAXED, 
        STRICT;
    }
    
    public enum Strength
    {
        NONE, 
        STRONG, 
        WEAK;
    }
    
    public enum Type
    {
        BASELINE, 
        BOTTOM, 
        CENTER, 
        CENTER_X, 
        CENTER_Y, 
        LEFT, 
        NONE, 
        RIGHT, 
        TOP;
    }
}
