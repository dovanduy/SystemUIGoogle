// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

import java.util.Arrays;

public class ArrayLinkedVariables
{
    private int ROW_SIZE;
    private SolverVariable candidate;
    int currentSize;
    private int[] mArrayIndices;
    private int[] mArrayNextIndices;
    private float[] mArrayValues;
    private final Cache mCache;
    private boolean mDidFillOnce;
    private int mHead;
    private int mLast;
    private final ArrayRow mRow;
    
    ArrayLinkedVariables(final ArrayRow mRow, final Cache mCache) {
        this.currentSize = 0;
        this.ROW_SIZE = 8;
        this.candidate = null;
        this.mArrayIndices = new int[8];
        this.mArrayNextIndices = new int[8];
        this.mArrayValues = new float[8];
        this.mHead = -1;
        this.mLast = -1;
        this.mDidFillOnce = false;
        this.mRow = mRow;
        this.mCache = mCache;
    }
    
    private boolean isNew(final SolverVariable solverVariable, final LinearSystem linearSystem) {
        final int usageInRowCount = solverVariable.usageInRowCount;
        boolean b = true;
        if (usageInRowCount > 1) {
            b = false;
        }
        return b;
    }
    
    final void add(final SolverVariable solverVariable, final float n, final boolean b) {
        if (n == 0.0f) {
            return;
        }
        int mHead = this.mHead;
        if (mHead == -1) {
            this.mHead = 0;
            this.mArrayValues[0] = n;
            this.mArrayIndices[0] = solverVariable.id;
            this.mArrayNextIndices[0] = -1;
            ++solverVariable.usageInRowCount;
            solverVariable.addToRow(this.mRow);
            ++this.currentSize;
            if (!this.mDidFillOnce) {
                final int mLast = this.mLast + 1;
                this.mLast = mLast;
                final int[] mArrayIndices = this.mArrayIndices;
                if (mLast >= mArrayIndices.length) {
                    this.mDidFillOnce = true;
                    this.mLast = mArrayIndices.length - 1;
                }
            }
            return;
        }
        int n2 = 0;
        int n3 = -1;
        while (mHead != -1 && n2 < this.currentSize) {
            final int[] mArrayIndices2 = this.mArrayIndices;
            final int n4 = mArrayIndices2[mHead];
            final int id = solverVariable.id;
            if (n4 == id) {
                final float[] mArrayValues = this.mArrayValues;
                mArrayValues[mHead] += n;
                if (mArrayValues[mHead] == 0.0f) {
                    if (mHead == this.mHead) {
                        this.mHead = this.mArrayNextIndices[mHead];
                    }
                    else {
                        final int[] mArrayNextIndices = this.mArrayNextIndices;
                        mArrayNextIndices[n3] = mArrayNextIndices[mHead];
                    }
                    if (b) {
                        solverVariable.removeFromRow(this.mRow);
                    }
                    if (this.mDidFillOnce) {
                        this.mLast = mHead;
                    }
                    --solverVariable.usageInRowCount;
                    --this.currentSize;
                }
                return;
            }
            if (mArrayIndices2[mHead] < id) {
                n3 = mHead;
            }
            mHead = this.mArrayNextIndices[mHead];
            ++n2;
        }
        int n5 = this.mLast;
        if (this.mDidFillOnce) {
            final int[] mArrayIndices3 = this.mArrayIndices;
            if (mArrayIndices3[n5] != -1) {
                n5 = mArrayIndices3.length;
            }
        }
        else {
            ++n5;
        }
        final int[] mArrayIndices4 = this.mArrayIndices;
        int n6 = n5;
        if (n5 >= mArrayIndices4.length) {
            n6 = n5;
            if (this.currentSize < mArrayIndices4.length) {
                int n7 = 0;
                while (true) {
                    final int[] mArrayIndices5 = this.mArrayIndices;
                    n6 = n5;
                    if (n7 >= mArrayIndices5.length) {
                        break;
                    }
                    if (mArrayIndices5[n7] == -1) {
                        n6 = n7;
                        break;
                    }
                    ++n7;
                }
            }
        }
        final int[] mArrayIndices6 = this.mArrayIndices;
        int length;
        if ((length = n6) >= mArrayIndices6.length) {
            length = mArrayIndices6.length;
            final int n8 = this.ROW_SIZE * 2;
            this.ROW_SIZE = n8;
            this.mDidFillOnce = false;
            this.mLast = length - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, n8);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[length] = solverVariable.id;
        this.mArrayValues[length] = n;
        if (n3 != -1) {
            final int[] mArrayNextIndices2 = this.mArrayNextIndices;
            mArrayNextIndices2[length] = mArrayNextIndices2[n3];
            mArrayNextIndices2[n3] = length;
        }
        else {
            this.mArrayNextIndices[length] = this.mHead;
            this.mHead = length;
        }
        ++solverVariable.usageInRowCount;
        solverVariable.addToRow(this.mRow);
        ++this.currentSize;
        if (!this.mDidFillOnce) {
            ++this.mLast;
        }
        final int mLast2 = this.mLast;
        final int[] mArrayIndices7 = this.mArrayIndices;
        if (mLast2 >= mArrayIndices7.length) {
            this.mDidFillOnce = true;
            this.mLast = mArrayIndices7.length - 1;
        }
    }
    
    SolverVariable chooseSubject(final LinearSystem linearSystem) {
        int mHead = this.mHead;
        SolverVariable solverVariable = null;
        int n = 0;
        int n3;
        final int n2 = n3 = n;
        float n5;
        float n4 = n5 = 0.0f;
        SolverVariable solverVariable2 = null;
        int n6 = n2;
        while (mHead != -1 && n < this.currentSize) {
            final float[] mArrayValues = this.mArrayValues;
            final float n7 = mArrayValues[mHead];
            final SolverVariable solverVariable3 = this.mCache.mIndexedVariables[this.mArrayIndices[mHead]];
            float n8 = 0.0f;
            Label_0139: {
                if (n7 < 0.0f) {
                    n8 = n7;
                    if (n7 <= -0.001f) {
                        break Label_0139;
                    }
                    mArrayValues[mHead] = 0.0f;
                    solverVariable3.removeFromRow(this.mRow);
                }
                else {
                    n8 = n7;
                    if (n7 >= 0.001f) {
                        break Label_0139;
                    }
                    mArrayValues[mHead] = 0.0f;
                    solverVariable3.removeFromRow(this.mRow);
                }
                n8 = 0.0f;
            }
            SolverVariable solverVariable4 = solverVariable;
            SolverVariable solverVariable5 = solverVariable2;
            int n9 = n6;
            int n10 = n3;
            float n11 = n4;
            float n12 = n5;
            Label_0488: {
                if (n8 != 0.0f) {
                    if (solverVariable3.mType == SolverVariable.Type.UNRESTRICTED) {
                        int n13;
                        if (solverVariable2 == null) {
                            n13 = (this.isNew(solverVariable3, linearSystem) ? 1 : 0);
                        }
                        else if (n4 > n8) {
                            n13 = (this.isNew(solverVariable3, linearSystem) ? 1 : 0);
                        }
                        else {
                            solverVariable4 = solverVariable;
                            solverVariable5 = solverVariable2;
                            n9 = n6;
                            n10 = n3;
                            n11 = n4;
                            n12 = n5;
                            if (n6 != 0) {
                                break Label_0488;
                            }
                            solverVariable4 = solverVariable;
                            solverVariable5 = solverVariable2;
                            n9 = n6;
                            n10 = n3;
                            n11 = n4;
                            n12 = n5;
                            if (!this.isNew(solverVariable3, linearSystem)) {
                                break Label_0488;
                            }
                            n13 = 1;
                        }
                        solverVariable4 = solverVariable;
                        solverVariable5 = solverVariable3;
                        n9 = n13;
                        n10 = n3;
                        n11 = n8;
                        n12 = n5;
                    }
                    else {
                        solverVariable4 = solverVariable;
                        solverVariable5 = solverVariable2;
                        n9 = n6;
                        n10 = n3;
                        n11 = n4;
                        n12 = n5;
                        if (solverVariable2 == null) {
                            solverVariable4 = solverVariable;
                            solverVariable5 = solverVariable2;
                            n9 = n6;
                            n10 = n3;
                            n11 = n4;
                            n12 = n5;
                            if (n8 < 0.0f) {
                                if (solverVariable == null) {
                                    n10 = (this.isNew(solverVariable3, linearSystem) ? 1 : 0);
                                }
                                else if (n5 > n8) {
                                    n10 = (this.isNew(solverVariable3, linearSystem) ? 1 : 0);
                                }
                                else {
                                    solverVariable4 = solverVariable;
                                    solverVariable5 = solverVariable2;
                                    n9 = n6;
                                    n10 = n3;
                                    n11 = n4;
                                    n12 = n5;
                                    if (n3 != 0) {
                                        break Label_0488;
                                    }
                                    solverVariable4 = solverVariable;
                                    solverVariable5 = solverVariable2;
                                    n9 = n6;
                                    n10 = n3;
                                    n11 = n4;
                                    n12 = n5;
                                    if (!this.isNew(solverVariable3, linearSystem)) {
                                        break Label_0488;
                                    }
                                    n10 = 1;
                                }
                                solverVariable4 = solverVariable3;
                                solverVariable5 = solverVariable2;
                                n9 = n6;
                                n11 = n4;
                                n12 = n8;
                            }
                        }
                    }
                }
            }
            mHead = this.mArrayNextIndices[mHead];
            ++n;
            solverVariable = solverVariable4;
            solverVariable2 = solverVariable5;
            n6 = n9;
            n3 = n10;
            n4 = n11;
            n5 = n12;
        }
        if (solverVariable2 != null) {
            return solverVariable2;
        }
        return solverVariable;
    }
    
    public final void clear() {
        for (int mHead = this.mHead, n = 0; mHead != -1 && n < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n) {
            final SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[mHead]];
            if (solverVariable != null) {
                solverVariable.removeFromRow(this.mRow);
            }
        }
        this.mHead = -1;
        this.mLast = -1;
        this.mDidFillOnce = false;
        this.currentSize = 0;
    }
    
    final boolean containsKey(final SolverVariable solverVariable) {
        int mHead = this.mHead;
        if (mHead == -1) {
            return false;
        }
        for (int n = 0; mHead != -1 && n < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n) {
            if (this.mArrayIndices[mHead] == solverVariable.id) {
                return true;
            }
        }
        return false;
    }
    
    void divideByAmount(final float n) {
        for (int mHead = this.mHead, n2 = 0; mHead != -1 && n2 < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n2) {
            final float[] mArrayValues = this.mArrayValues;
            mArrayValues[mHead] /= n;
        }
    }
    
    public final float get(final SolverVariable solverVariable) {
        for (int mHead = this.mHead, n = 0; mHead != -1 && n < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n) {
            if (this.mArrayIndices[mHead] == solverVariable.id) {
                return this.mArrayValues[mHead];
            }
        }
        return 0.0f;
    }
    
    SolverVariable getPivotCandidate(final boolean[] array, final SolverVariable solverVariable) {
        int mHead = this.mHead;
        int n = 0;
        SolverVariable solverVariable2 = null;
        float n2 = 0.0f;
        while (mHead != -1 && n < this.currentSize) {
            SolverVariable solverVariable3 = solverVariable2;
            float n3 = n2;
            Label_0162: {
                if (this.mArrayValues[mHead] < 0.0f) {
                    final SolverVariable solverVariable4 = this.mCache.mIndexedVariables[this.mArrayIndices[mHead]];
                    if (array != null) {
                        solverVariable3 = solverVariable2;
                        n3 = n2;
                        if (array[solverVariable4.id]) {
                            break Label_0162;
                        }
                    }
                    solverVariable3 = solverVariable2;
                    n3 = n2;
                    if (solverVariable4 != solverVariable) {
                        final SolverVariable.Type mType = solverVariable4.mType;
                        if (mType != SolverVariable.Type.SLACK) {
                            solverVariable3 = solverVariable2;
                            n3 = n2;
                            if (mType != SolverVariable.Type.ERROR) {
                                break Label_0162;
                            }
                        }
                        final float n4 = this.mArrayValues[mHead];
                        solverVariable3 = solverVariable2;
                        n3 = n2;
                        if (n4 < n2) {
                            solverVariable3 = solverVariable4;
                            n3 = n4;
                        }
                    }
                }
            }
            mHead = this.mArrayNextIndices[mHead];
            ++n;
            solverVariable2 = solverVariable3;
            n2 = n3;
        }
        return solverVariable2;
    }
    
    final SolverVariable getVariable(final int n) {
        for (int mHead = this.mHead, n2 = 0; mHead != -1 && n2 < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n2) {
            if (n2 == n) {
                return this.mCache.mIndexedVariables[this.mArrayIndices[mHead]];
            }
        }
        return null;
    }
    
    final float getVariableValue(final int n) {
        for (int mHead = this.mHead, n2 = 0; mHead != -1 && n2 < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n2) {
            if (n2 == n) {
                return this.mArrayValues[mHead];
            }
        }
        return 0.0f;
    }
    
    void invert() {
        for (int mHead = this.mHead, n = 0; mHead != -1 && n < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n) {
            final float[] mArrayValues = this.mArrayValues;
            mArrayValues[mHead] *= -1.0f;
        }
    }
    
    public final void put(final SolverVariable solverVariable, final float n) {
        if (n == 0.0f) {
            this.remove(solverVariable, true);
            return;
        }
        int mHead = this.mHead;
        if (mHead == -1) {
            this.mHead = 0;
            this.mArrayValues[0] = n;
            this.mArrayIndices[0] = solverVariable.id;
            this.mArrayNextIndices[0] = -1;
            ++solverVariable.usageInRowCount;
            solverVariable.addToRow(this.mRow);
            ++this.currentSize;
            if (!this.mDidFillOnce) {
                final int mLast = this.mLast + 1;
                this.mLast = mLast;
                final int[] mArrayIndices = this.mArrayIndices;
                if (mLast >= mArrayIndices.length) {
                    this.mDidFillOnce = true;
                    this.mLast = mArrayIndices.length - 1;
                }
            }
            return;
        }
        int n2 = 0;
        int n3 = -1;
        while (mHead != -1 && n2 < this.currentSize) {
            final int[] mArrayIndices2 = this.mArrayIndices;
            final int n4 = mArrayIndices2[mHead];
            final int id = solverVariable.id;
            if (n4 == id) {
                this.mArrayValues[mHead] = n;
                return;
            }
            if (mArrayIndices2[mHead] < id) {
                n3 = mHead;
            }
            mHead = this.mArrayNextIndices[mHead];
            ++n2;
        }
        int n5 = this.mLast;
        if (this.mDidFillOnce) {
            final int[] mArrayIndices3 = this.mArrayIndices;
            if (mArrayIndices3[n5] != -1) {
                n5 = mArrayIndices3.length;
            }
        }
        else {
            ++n5;
        }
        final int[] mArrayIndices4 = this.mArrayIndices;
        int n6 = n5;
        if (n5 >= mArrayIndices4.length) {
            n6 = n5;
            if (this.currentSize < mArrayIndices4.length) {
                int n7 = 0;
                while (true) {
                    final int[] mArrayIndices5 = this.mArrayIndices;
                    n6 = n5;
                    if (n7 >= mArrayIndices5.length) {
                        break;
                    }
                    if (mArrayIndices5[n7] == -1) {
                        n6 = n7;
                        break;
                    }
                    ++n7;
                }
            }
        }
        final int[] mArrayIndices6 = this.mArrayIndices;
        int length;
        if ((length = n6) >= mArrayIndices6.length) {
            length = mArrayIndices6.length;
            final int n8 = this.ROW_SIZE * 2;
            this.ROW_SIZE = n8;
            this.mDidFillOnce = false;
            this.mLast = length - 1;
            this.mArrayValues = Arrays.copyOf(this.mArrayValues, n8);
            this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
            this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
        }
        this.mArrayIndices[length] = solverVariable.id;
        this.mArrayValues[length] = n;
        if (n3 != -1) {
            final int[] mArrayNextIndices = this.mArrayNextIndices;
            mArrayNextIndices[length] = mArrayNextIndices[n3];
            mArrayNextIndices[n3] = length;
        }
        else {
            this.mArrayNextIndices[length] = this.mHead;
            this.mHead = length;
        }
        ++solverVariable.usageInRowCount;
        solverVariable.addToRow(this.mRow);
        ++this.currentSize;
        if (!this.mDidFillOnce) {
            ++this.mLast;
        }
        if (this.currentSize >= this.mArrayIndices.length) {
            this.mDidFillOnce = true;
        }
        final int mLast2 = this.mLast;
        final int[] mArrayIndices7 = this.mArrayIndices;
        if (mLast2 >= mArrayIndices7.length) {
            this.mDidFillOnce = true;
            this.mLast = mArrayIndices7.length - 1;
        }
    }
    
    public final float remove(final SolverVariable solverVariable, final boolean b) {
        if (this.candidate == solverVariable) {
            this.candidate = null;
        }
        int mHead = this.mHead;
        if (mHead == -1) {
            return 0.0f;
        }
        int n = 0;
        int n2 = -1;
        while (mHead != -1 && n < this.currentSize) {
            if (this.mArrayIndices[mHead] == solverVariable.id) {
                if (mHead == this.mHead) {
                    this.mHead = this.mArrayNextIndices[mHead];
                }
                else {
                    final int[] mArrayNextIndices = this.mArrayNextIndices;
                    mArrayNextIndices[n2] = mArrayNextIndices[mHead];
                }
                if (b) {
                    solverVariable.removeFromRow(this.mRow);
                }
                --solverVariable.usageInRowCount;
                --this.currentSize;
                this.mArrayIndices[mHead] = -1;
                if (this.mDidFillOnce) {
                    this.mLast = mHead;
                }
                return this.mArrayValues[mHead];
            }
            final int n3 = this.mArrayNextIndices[mHead];
            ++n;
            n2 = mHead;
            mHead = n3;
        }
        return 0.0f;
    }
    
    @Override
    public String toString() {
        int mHead = this.mHead;
        String string = "";
        for (int n = 0; mHead != -1 && n < this.currentSize; mHead = this.mArrayNextIndices[mHead], ++n) {
            final StringBuilder sb = new StringBuilder();
            sb.append(string);
            sb.append(" -> ");
            final String string2 = sb.toString();
            final StringBuilder sb2 = new StringBuilder();
            sb2.append(string2);
            sb2.append(this.mArrayValues[mHead]);
            sb2.append(" : ");
            final String string3 = sb2.toString();
            final StringBuilder sb3 = new StringBuilder();
            sb3.append(string3);
            sb3.append(this.mCache.mIndexedVariables[this.mArrayIndices[mHead]]);
            string = sb3.toString();
        }
        return string;
    }
    
    final void updateFromRow(final ArrayRow arrayRow, final ArrayRow arrayRow2, final boolean b) {
        int n = this.mHead;
    Label_0006:
        while (true) {
            for (int n2 = 0; n != -1 && n2 < this.currentSize; n = this.mArrayNextIndices[n], ++n2) {
                final int n3 = this.mArrayIndices[n];
                final SolverVariable variable = arrayRow2.variable;
                if (n3 == variable.id) {
                    final float n4 = this.mArrayValues[n];
                    this.remove(variable, b);
                    final ArrayLinkedVariables variables = arrayRow2.variables;
                    for (int mHead = variables.mHead, n5 = 0; mHead != -1 && n5 < variables.currentSize; mHead = variables.mArrayNextIndices[mHead], ++n5) {
                        this.add(this.mCache.mIndexedVariables[variables.mArrayIndices[mHead]], variables.mArrayValues[mHead] * n4, b);
                    }
                    arrayRow.constantValue += arrayRow2.constantValue * n4;
                    if (b) {
                        arrayRow2.variable.removeFromRow(arrayRow);
                    }
                    n = this.mHead;
                    continue Label_0006;
                }
            }
            break;
        }
    }
    
    void updateFromSystem(final ArrayRow arrayRow, final ArrayRow[] array) {
        int n = this.mHead;
    Label_0005:
        while (true) {
            for (int n2 = 0; n != -1 && n2 < this.currentSize; n = this.mArrayNextIndices[n], ++n2) {
                final SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[n]];
                if (solverVariable.definitionId != -1) {
                    final float n3 = this.mArrayValues[n];
                    this.remove(solverVariable, true);
                    final ArrayRow arrayRow2 = array[solverVariable.definitionId];
                    if (!arrayRow2.isSimpleDefinition) {
                        final ArrayLinkedVariables variables = arrayRow2.variables;
                        for (int mHead = variables.mHead, n4 = 0; mHead != -1 && n4 < variables.currentSize; mHead = variables.mArrayNextIndices[mHead], ++n4) {
                            this.add(this.mCache.mIndexedVariables[variables.mArrayIndices[mHead]], variables.mArrayValues[mHead] * n3, true);
                        }
                    }
                    arrayRow.constantValue += arrayRow2.constantValue * n3;
                    arrayRow2.variable.removeFromRow(arrayRow);
                    n = this.mHead;
                    continue Label_0005;
                }
            }
            break;
        }
    }
}
