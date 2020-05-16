// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

import java.util.Arrays;

public class SolverVariable
{
    private static int uniqueErrorId = 1;
    public float computedValue;
    int definitionId;
    public int id;
    ArrayRow[] mClientEquations;
    int mClientEquationsCount;
    private String mName;
    Type mType;
    public int strength;
    float[] strengthVector;
    public int usageInRowCount;
    
    public SolverVariable(final Type mType, final String s) {
        this.id = -1;
        this.definitionId = -1;
        this.strength = 0;
        this.strengthVector = new float[7];
        this.mClientEquations = new ArrayRow[8];
        this.mClientEquationsCount = 0;
        this.usageInRowCount = 0;
        this.mType = mType;
    }
    
    static void increaseErrorId() {
        ++SolverVariable.uniqueErrorId;
    }
    
    public final void addToRow(final ArrayRow arrayRow) {
        int n = 0;
        while (true) {
            final int mClientEquationsCount = this.mClientEquationsCount;
            if (n >= mClientEquationsCount) {
                final ArrayRow[] mClientEquations = this.mClientEquations;
                if (mClientEquationsCount >= mClientEquations.length) {
                    this.mClientEquations = Arrays.copyOf(mClientEquations, mClientEquations.length * 2);
                }
                final ArrayRow[] mClientEquations2 = this.mClientEquations;
                final int mClientEquationsCount2 = this.mClientEquationsCount;
                mClientEquations2[mClientEquationsCount2] = arrayRow;
                this.mClientEquationsCount = mClientEquationsCount2 + 1;
                return;
            }
            if (this.mClientEquations[n] == arrayRow) {
                return;
            }
            ++n;
        }
    }
    
    public final void removeFromRow(final ArrayRow arrayRow) {
        final int mClientEquationsCount = this.mClientEquationsCount;
        int i = 0;
        for (int j = 0; j < mClientEquationsCount; ++j) {
            if (this.mClientEquations[j] == arrayRow) {
                while (i < mClientEquationsCount - j - 1) {
                    final ArrayRow[] mClientEquations = this.mClientEquations;
                    final int n = j + i;
                    mClientEquations[n] = mClientEquations[n + 1];
                    ++i;
                }
                --this.mClientEquationsCount;
                return;
            }
        }
    }
    
    public void reset() {
        this.mName = null;
        this.mType = Type.UNKNOWN;
        this.strength = 0;
        this.id = -1;
        this.definitionId = -1;
        this.computedValue = 0.0f;
        this.mClientEquationsCount = 0;
        this.usageInRowCount = 0;
    }
    
    public void setType(final Type mType, final String s) {
        this.mType = mType;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(this.mName);
        return sb.toString();
    }
    
    public final void updateReferencesWithNewDefinition(final ArrayRow arrayRow) {
        for (int mClientEquationsCount = this.mClientEquationsCount, i = 0; i < mClientEquationsCount; ++i) {
            final ArrayRow[] mClientEquations = this.mClientEquations;
            mClientEquations[i].variables.updateFromRow(mClientEquations[i], arrayRow, false);
        }
        this.mClientEquationsCount = 0;
    }
    
    public enum Type
    {
        CONSTANT, 
        ERROR, 
        SLACK, 
        UNKNOWN, 
        UNRESTRICTED;
    }
}
