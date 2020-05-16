// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import java.util.Arrays;
import java.util.HashMap;

public class LinearSystem
{
    private static int POOL_SIZE = 1000;
    public static Metrics sMetrics;
    private int TABLE_SIZE;
    public boolean graphOptimizer;
    private boolean[] mAlreadyTestedCandidates;
    final Cache mCache;
    private Row mGoal;
    private int mMaxColumns;
    private int mMaxRows;
    int mNumColumns;
    int mNumRows;
    private SolverVariable[] mPoolVariables;
    private int mPoolVariablesCount;
    ArrayRow[] mRows;
    private final Row mTempGoal;
    private HashMap<String, SolverVariable> mVariables;
    int mVariablesID;
    
    public LinearSystem() {
        this.mVariablesID = 0;
        this.mVariables = null;
        this.TABLE_SIZE = 32;
        this.mMaxColumns = 32;
        this.mRows = null;
        this.graphOptimizer = false;
        this.mAlreadyTestedCandidates = new boolean[32];
        this.mNumColumns = 1;
        this.mNumRows = 0;
        this.mMaxRows = 32;
        this.mPoolVariables = new SolverVariable[LinearSystem.POOL_SIZE];
        this.mPoolVariablesCount = 0;
        this.mRows = new ArrayRow[32];
        this.releaseRows();
        final Cache mCache = new Cache();
        this.mCache = mCache;
        this.mGoal = (Row)new GoalRow(mCache);
        this.mTempGoal = (Row)new ArrayRow(this.mCache);
    }
    
    private SolverVariable acquireSolverVariable(final SolverVariable.Type type, final String s) {
        final SolverVariable solverVariable = this.mCache.solverVariablePool.acquire();
        SolverVariable solverVariable3;
        if (solverVariable == null) {
            final SolverVariable solverVariable2 = new SolverVariable(type, s);
            solverVariable2.setType(type, s);
            solverVariable3 = solverVariable2;
        }
        else {
            solverVariable.reset();
            solverVariable.setType(type, s);
            solverVariable3 = solverVariable;
        }
        final int mPoolVariablesCount = this.mPoolVariablesCount;
        final int pool_SIZE = LinearSystem.POOL_SIZE;
        if (mPoolVariablesCount >= pool_SIZE) {
            this.mPoolVariables = Arrays.copyOf(this.mPoolVariables, LinearSystem.POOL_SIZE = pool_SIZE * 2);
        }
        return this.mPoolVariables[this.mPoolVariablesCount++] = solverVariable3;
    }
    
    private void addError(final ArrayRow arrayRow) {
        arrayRow.addError(this, 0);
    }
    
    private final void addRow(final ArrayRow arrayRow) {
        final ArrayRow[] mRows = this.mRows;
        final int mNumRows = this.mNumRows;
        if (mRows[mNumRows] != null) {
            this.mCache.arrayRowPool.release(mRows[mNumRows]);
        }
        final ArrayRow[] mRows2 = this.mRows;
        final int mNumRows2 = this.mNumRows;
        mRows2[mNumRows2] = arrayRow;
        final SolverVariable variable = arrayRow.variable;
        variable.definitionId = mNumRows2;
        this.mNumRows = mNumRows2 + 1;
        variable.updateReferencesWithNewDefinition(arrayRow);
    }
    
    private void computeValues() {
        for (int i = 0; i < this.mNumRows; ++i) {
            final ArrayRow arrayRow = this.mRows[i];
            arrayRow.variable.computedValue = arrayRow.constantValue;
        }
    }
    
    public static ArrayRow createRowDimensionPercent(final LinearSystem linearSystem, final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final float n, final boolean b) {
        final ArrayRow row = linearSystem.createRow();
        if (b) {
            linearSystem.addError(row);
        }
        row.createRowDimensionPercent(solverVariable, solverVariable2, solverVariable3, n);
        return row;
    }
    
    private int enforceBFS(final Row row) throws Exception {
        while (true) {
            for (int i = 0; i < this.mNumRows; ++i) {
                final ArrayRow[] mRows = this.mRows;
                if (mRows[i].variable.mType != SolverVariable.Type.UNRESTRICTED) {
                    if (mRows[i].constantValue < 0.0f) {
                        final boolean b = true;
                        int n;
                        if (b) {
                            int j = 0;
                            n = 0;
                            while (j == 0) {
                                final Metrics sMetrics = LinearSystem.sMetrics;
                                if (sMetrics != null) {
                                    ++sMetrics.bfs;
                                }
                                final int n2 = n + 1;
                                float n3 = Float.MAX_VALUE;
                                int n4;
                                int definitionId = n4 = -1;
                                int k = 0;
                                int n5 = 0;
                                while (k < this.mNumRows) {
                                    final ArrayRow arrayRow = this.mRows[k];
                                    float n6;
                                    int n7;
                                    int n8;
                                    int n9;
                                    if (arrayRow.variable.mType == SolverVariable.Type.UNRESTRICTED) {
                                        n6 = n3;
                                        n7 = definitionId;
                                        n8 = n4;
                                        n9 = n5;
                                    }
                                    else if (arrayRow.isSimpleDefinition) {
                                        n6 = n3;
                                        n7 = definitionId;
                                        n8 = n4;
                                        n9 = n5;
                                    }
                                    else {
                                        n6 = n3;
                                        n7 = definitionId;
                                        n8 = n4;
                                        n9 = n5;
                                        if (arrayRow.constantValue < 0.0f) {
                                            int n10 = 1;
                                            while (true) {
                                                n6 = n3;
                                                n7 = definitionId;
                                                n8 = n4;
                                                n9 = n5;
                                                if (n10 >= this.mNumColumns) {
                                                    break;
                                                }
                                                final SolverVariable solverVariable = this.mCache.mIndexedVariables[n10];
                                                final float value = arrayRow.variables.get(solverVariable);
                                                float n11;
                                                int n12;
                                                int n13;
                                                int n14;
                                                if (value <= 0.0f) {
                                                    n11 = n3;
                                                    n12 = definitionId;
                                                    n13 = n4;
                                                    n14 = n5;
                                                }
                                                else {
                                                    final int n15 = 0;
                                                    int n16 = n4;
                                                    int n17 = n15;
                                                    while (true) {
                                                        n11 = n3;
                                                        n12 = definitionId;
                                                        n13 = n16;
                                                        n14 = n5;
                                                        if (n17 >= 7) {
                                                            break;
                                                        }
                                                        final float n18 = solverVariable.strengthVector[n17] / value;
                                                        int n19;
                                                        if ((n18 < n3 && n17 == n5) || n17 > (n19 = n5)) {
                                                            n16 = n10;
                                                            n19 = n17;
                                                            n3 = n18;
                                                            definitionId = k;
                                                        }
                                                        ++n17;
                                                        n5 = n19;
                                                    }
                                                }
                                                ++n10;
                                                n3 = n11;
                                                definitionId = n12;
                                                n4 = n13;
                                                n5 = n14;
                                            }
                                        }
                                    }
                                    ++k;
                                    n3 = n6;
                                    definitionId = n7;
                                    n4 = n8;
                                    n5 = n9;
                                }
                                if (definitionId != -1) {
                                    final ArrayRow arrayRow2 = this.mRows[definitionId];
                                    arrayRow2.variable.definitionId = -1;
                                    final Metrics sMetrics2 = LinearSystem.sMetrics;
                                    if (sMetrics2 != null) {
                                        ++sMetrics2.pivots;
                                    }
                                    arrayRow2.pivot(this.mCache.mIndexedVariables[n4]);
                                    final SolverVariable variable = arrayRow2.variable;
                                    variable.definitionId = definitionId;
                                    variable.updateReferencesWithNewDefinition(arrayRow2);
                                }
                                else {
                                    j = 1;
                                }
                                if (n2 > this.mNumColumns / 2) {
                                    j = 1;
                                }
                                n = n2;
                            }
                        }
                        else {
                            n = 0;
                        }
                        return n;
                    }
                }
            }
            final boolean b = false;
            continue;
        }
    }
    
    public static Metrics getMetrics() {
        return LinearSystem.sMetrics;
    }
    
    private void increaseTableSize() {
        final int n = this.TABLE_SIZE * 2;
        this.TABLE_SIZE = n;
        this.mRows = Arrays.copyOf(this.mRows, n);
        final Cache mCache = this.mCache;
        mCache.mIndexedVariables = Arrays.copyOf(mCache.mIndexedVariables, this.TABLE_SIZE);
        final int table_SIZE = this.TABLE_SIZE;
        this.mAlreadyTestedCandidates = new boolean[table_SIZE];
        this.mMaxColumns = table_SIZE;
        this.mMaxRows = table_SIZE;
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.tableSizeIncrease;
            sMetrics.maxTableSize = Math.max(sMetrics.maxTableSize, table_SIZE);
            final Metrics sMetrics2 = LinearSystem.sMetrics;
            sMetrics2.lastTableSize = sMetrics2.maxTableSize;
        }
    }
    
    private final int optimize(final Row row, final boolean b) {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.optimize;
        }
        for (int i = 0; i < this.mNumColumns; ++i) {
            this.mAlreadyTestedCandidates[i] = false;
        }
        int n;
        int j = n = 0;
        while (j == 0) {
            final Metrics sMetrics2 = LinearSystem.sMetrics;
            if (sMetrics2 != null) {
                ++sMetrics2.iterations;
            }
            final int n2 = n + 1;
            if (n2 >= this.mNumColumns * 2) {
                return n2;
            }
            if (row.getKey() != null) {
                this.mAlreadyTestedCandidates[row.getKey().id] = true;
            }
            final SolverVariable pivotCandidate = row.getPivotCandidate(this, this.mAlreadyTestedCandidates);
            if (pivotCandidate != null) {
                final boolean[] mAlreadyTestedCandidates = this.mAlreadyTestedCandidates;
                final int id = pivotCandidate.id;
                if (mAlreadyTestedCandidates[id]) {
                    return n2;
                }
                mAlreadyTestedCandidates[id] = true;
            }
            if (pivotCandidate != null) {
                float n3 = Float.MAX_VALUE;
                int k = 0;
                int definitionId = -1;
                while (k < this.mNumRows) {
                    final ArrayRow arrayRow = this.mRows[k];
                    float n4;
                    int n5;
                    if (arrayRow.variable.mType == SolverVariable.Type.UNRESTRICTED) {
                        n4 = n3;
                        n5 = definitionId;
                    }
                    else if (arrayRow.isSimpleDefinition) {
                        n4 = n3;
                        n5 = definitionId;
                    }
                    else {
                        n4 = n3;
                        n5 = definitionId;
                        if (arrayRow.hasVariable(pivotCandidate)) {
                            final float value = arrayRow.variables.get(pivotCandidate);
                            n4 = n3;
                            n5 = definitionId;
                            if (value < 0.0f) {
                                final float n6 = -arrayRow.constantValue / value;
                                n4 = n3;
                                n5 = definitionId;
                                if (n6 < n3) {
                                    n5 = k;
                                    n4 = n6;
                                }
                            }
                        }
                    }
                    ++k;
                    n3 = n4;
                    definitionId = n5;
                }
                if (definitionId > -1) {
                    final ArrayRow arrayRow2 = this.mRows[definitionId];
                    arrayRow2.variable.definitionId = -1;
                    final Metrics sMetrics3 = LinearSystem.sMetrics;
                    if (sMetrics3 != null) {
                        ++sMetrics3.pivots;
                    }
                    arrayRow2.pivot(pivotCandidate);
                    final SolverVariable variable = arrayRow2.variable;
                    variable.definitionId = definitionId;
                    variable.updateReferencesWithNewDefinition(arrayRow2);
                    n = n2;
                    continue;
                }
            }
            j = 1;
            n = n2;
        }
        return n;
    }
    
    private void releaseRows() {
        int n = 0;
        while (true) {
            final ArrayRow[] mRows = this.mRows;
            if (n >= mRows.length) {
                break;
            }
            final ArrayRow arrayRow = mRows[n];
            if (arrayRow != null) {
                this.mCache.arrayRowPool.release(arrayRow);
            }
            this.mRows[n] = null;
            ++n;
        }
    }
    
    private final void updateRowFromVariables(final ArrayRow arrayRow) {
        if (this.mNumRows > 0) {
            arrayRow.variables.updateFromSystem(arrayRow, this.mRows);
            if (arrayRow.variables.currentSize == 0) {
                arrayRow.isSimpleDefinition = true;
            }
        }
    }
    
    public void addCenterPoint(final ConstraintWidget constraintWidget, final ConstraintWidget constraintWidget2, final float n, final int n2) {
        final SolverVariable objectVariable = this.createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT));
        final SolverVariable objectVariable2 = this.createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.TOP));
        final SolverVariable objectVariable3 = this.createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT));
        final SolverVariable objectVariable4 = this.createObjectVariable(constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM));
        final SolverVariable objectVariable5 = this.createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.LEFT));
        final SolverVariable objectVariable6 = this.createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.TOP));
        final SolverVariable objectVariable7 = this.createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.RIGHT));
        final SolverVariable objectVariable8 = this.createObjectVariable(constraintWidget2.getAnchor(ConstraintAnchor.Type.BOTTOM));
        final ArrayRow row = this.createRow();
        final double n3 = n;
        final double sin = Math.sin(n3);
        final double n4 = n2;
        row.createRowWithAngle(objectVariable2, objectVariable4, objectVariable6, objectVariable8, (float)(sin * n4));
        this.addConstraint(row);
        final ArrayRow row2 = this.createRow();
        row2.createRowWithAngle(objectVariable, objectVariable3, objectVariable5, objectVariable7, (float)(Math.cos(n3) * n4));
        this.addConstraint(row2);
    }
    
    public void addCentering(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n, final float n2, final SolverVariable solverVariable3, final SolverVariable solverVariable4, final int n3, final int n4) {
        final ArrayRow row = this.createRow();
        row.createRowCentering(solverVariable, solverVariable2, n, n2, solverVariable3, solverVariable4, n3);
        if (n4 != 6) {
            row.addError(this, n4);
        }
        this.addConstraint(row);
    }
    
    public void addConstraint(final ArrayRow arrayRow) {
        if (arrayRow == null) {
            return;
        }
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.constraints;
            if (arrayRow.isSimpleDefinition) {
                ++sMetrics.simpleconstraints;
            }
        }
        final int mNumRows = this.mNumRows;
        final boolean b = true;
        if (mNumRows + 1 >= this.mMaxRows || this.mNumColumns + 1 >= this.mMaxColumns) {
            this.increaseTableSize();
        }
        int n = 0;
        if (!arrayRow.isSimpleDefinition) {
            this.updateRowFromVariables(arrayRow);
            if (arrayRow.isEmpty()) {
                return;
            }
            arrayRow.ensurePositiveConstant();
            if (arrayRow.chooseSubject(this)) {
                final SolverVariable extraVariable = this.createExtraVariable();
                arrayRow.variable = extraVariable;
                this.addRow(arrayRow);
                this.mTempGoal.initFromRow((Row)arrayRow);
                this.optimize(this.mTempGoal, true);
                n = (b ? 1 : 0);
                if (extraVariable.definitionId == -1) {
                    if (arrayRow.variable == extraVariable) {
                        final SolverVariable pickPivot = arrayRow.pickPivot(extraVariable);
                        if (pickPivot != null) {
                            final Metrics sMetrics2 = LinearSystem.sMetrics;
                            if (sMetrics2 != null) {
                                ++sMetrics2.pivots;
                            }
                            arrayRow.pivot(pickPivot);
                        }
                    }
                    if (!arrayRow.isSimpleDefinition) {
                        arrayRow.variable.updateReferencesWithNewDefinition(arrayRow);
                    }
                    --this.mNumRows;
                    n = (b ? 1 : 0);
                }
            }
            else {
                n = 0;
            }
            if (!arrayRow.hasKeyVariable()) {
                return;
            }
        }
        if (n == 0) {
            this.addRow(arrayRow);
        }
    }
    
    public ArrayRow addEquality(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n, final int n2) {
        final ArrayRow row = this.createRow();
        row.createRowEquals(solverVariable, solverVariable2, n);
        if (n2 != 6) {
            row.addError(this, n2);
        }
        this.addConstraint(row);
        return row;
    }
    
    public void addEquality(final SolverVariable solverVariable, final int n) {
        final int definitionId = solverVariable.definitionId;
        if (definitionId != -1) {
            final ArrayRow arrayRow = this.mRows[definitionId];
            if (arrayRow.isSimpleDefinition) {
                arrayRow.constantValue = (float)n;
            }
            else if (arrayRow.variables.currentSize == 0) {
                arrayRow.isSimpleDefinition = true;
                arrayRow.constantValue = (float)n;
            }
            else {
                final ArrayRow row = this.createRow();
                row.createRowEquals(solverVariable, n);
                this.addConstraint(row);
            }
        }
        else {
            final ArrayRow row2 = this.createRow();
            row2.createRowDefinition(solverVariable, n);
            this.addConstraint(row2);
        }
    }
    
    public void addGreaterBarrier(final SolverVariable solverVariable, final SolverVariable solverVariable2, final boolean b) {
        final ArrayRow row = this.createRow();
        final SolverVariable slackVariable = this.createSlackVariable();
        row.createRowGreaterThan(solverVariable, solverVariable2, slackVariable, slackVariable.strength = 0);
        if (b) {
            this.addSingleError(row, (int)(row.variables.get(slackVariable) * -1.0f), 1);
        }
        this.addConstraint(row);
    }
    
    public void addGreaterThan(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n, final int n2) {
        final ArrayRow row = this.createRow();
        final SolverVariable slackVariable = this.createSlackVariable();
        slackVariable.strength = 0;
        row.createRowGreaterThan(solverVariable, solverVariable2, slackVariable, n);
        if (n2 != 6) {
            this.addSingleError(row, (int)(row.variables.get(slackVariable) * -1.0f), n2);
        }
        this.addConstraint(row);
    }
    
    public void addLowerBarrier(final SolverVariable solverVariable, final SolverVariable solverVariable2, final boolean b) {
        final ArrayRow row = this.createRow();
        final SolverVariable slackVariable = this.createSlackVariable();
        row.createRowLowerThan(solverVariable, solverVariable2, slackVariable, slackVariable.strength = 0);
        if (b) {
            this.addSingleError(row, (int)(row.variables.get(slackVariable) * -1.0f), 1);
        }
        this.addConstraint(row);
    }
    
    public void addLowerThan(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n, final int n2) {
        final ArrayRow row = this.createRow();
        final SolverVariable slackVariable = this.createSlackVariable();
        slackVariable.strength = 0;
        row.createRowLowerThan(solverVariable, solverVariable2, slackVariable, n);
        if (n2 != 6) {
            this.addSingleError(row, (int)(row.variables.get(slackVariable) * -1.0f), n2);
        }
        this.addConstraint(row);
    }
    
    public void addRatio(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final SolverVariable solverVariable4, final float n, final int n2) {
        final ArrayRow row = this.createRow();
        row.createRowDimensionRatio(solverVariable, solverVariable2, solverVariable3, solverVariable4, n);
        if (n2 != 6) {
            row.addError(this, n2);
        }
        this.addConstraint(row);
    }
    
    void addSingleError(final ArrayRow arrayRow, final int n, final int n2) {
        arrayRow.addSingleError(this.createErrorVariable(n2, null), n);
    }
    
    public SolverVariable createErrorVariable(final int strength, final String s) {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.errors;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            this.increaseTableSize();
        }
        final SolverVariable acquireSolverVariable = this.acquireSolverVariable(SolverVariable.Type.ERROR, s);
        final int n = this.mVariablesID + 1;
        this.mVariablesID = n;
        ++this.mNumColumns;
        acquireSolverVariable.id = n;
        acquireSolverVariable.strength = strength;
        this.mCache.mIndexedVariables[n] = acquireSolverVariable;
        this.mGoal.addError(acquireSolverVariable);
        return acquireSolverVariable;
    }
    
    public SolverVariable createExtraVariable() {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.extravariables;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            this.increaseTableSize();
        }
        final SolverVariable acquireSolverVariable = this.acquireSolverVariable(SolverVariable.Type.SLACK, null);
        final int n = this.mVariablesID + 1;
        this.mVariablesID = n;
        ++this.mNumColumns;
        acquireSolverVariable.id = n;
        return this.mCache.mIndexedVariables[n] = acquireSolverVariable;
    }
    
    public SolverVariable createObjectVariable(final Object o) {
        SolverVariable solverVariable = null;
        if (o == null) {
            return null;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            this.increaseTableSize();
        }
        if (o instanceof ConstraintAnchor) {
            final ConstraintAnchor constraintAnchor = (ConstraintAnchor)o;
            SolverVariable solverVariable2;
            if ((solverVariable2 = constraintAnchor.getSolverVariable()) == null) {
                constraintAnchor.resetSolverVariable(this.mCache);
                solverVariable2 = constraintAnchor.getSolverVariable();
            }
            final int id = solverVariable2.id;
            if (id != -1 && id <= this.mVariablesID) {
                solverVariable = solverVariable2;
                if (this.mCache.mIndexedVariables[id] != null) {
                    return solverVariable;
                }
            }
            if (solverVariable2.id != -1) {
                solverVariable2.reset();
            }
            final int n = this.mVariablesID + 1;
            this.mVariablesID = n;
            ++this.mNumColumns;
            solverVariable2.id = n;
            solverVariable2.mType = SolverVariable.Type.UNRESTRICTED;
            this.mCache.mIndexedVariables[n] = solverVariable2;
            solverVariable = solverVariable2;
        }
        return solverVariable;
    }
    
    public ArrayRow createRow() {
        ArrayRow arrayRow = this.mCache.arrayRowPool.acquire();
        if (arrayRow == null) {
            arrayRow = new ArrayRow(this.mCache);
        }
        else {
            arrayRow.reset();
        }
        SolverVariable.increaseErrorId();
        return arrayRow;
    }
    
    public SolverVariable createSlackVariable() {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.slackvariables;
        }
        if (this.mNumColumns + 1 >= this.mMaxColumns) {
            this.increaseTableSize();
        }
        final SolverVariable acquireSolverVariable = this.acquireSolverVariable(SolverVariable.Type.SLACK, null);
        final int n = this.mVariablesID + 1;
        this.mVariablesID = n;
        ++this.mNumColumns;
        acquireSolverVariable.id = n;
        return this.mCache.mIndexedVariables[n] = acquireSolverVariable;
    }
    
    public Cache getCache() {
        return this.mCache;
    }
    
    public int getObjectVariableValue(final Object o) {
        final SolverVariable solverVariable = ((ConstraintAnchor)o).getSolverVariable();
        if (solverVariable != null) {
            return (int)(solverVariable.computedValue + 0.5f);
        }
        return 0;
    }
    
    public void minimize() throws Exception {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.minimize;
        }
        if (this.graphOptimizer) {
            final Metrics sMetrics2 = LinearSystem.sMetrics;
            if (sMetrics2 != null) {
                ++sMetrics2.graphOptimizer;
            }
            final int n = 0;
            int i = 0;
            while (true) {
                while (i < this.mNumRows) {
                    if (!this.mRows[i].isSimpleDefinition) {
                        final int n2 = n;
                        if (n2 == 0) {
                            this.minimizeGoal(this.mGoal);
                            return;
                        }
                        final Metrics sMetrics3 = LinearSystem.sMetrics;
                        if (sMetrics3 != null) {
                            ++sMetrics3.fullySolved;
                        }
                        this.computeValues();
                        return;
                    }
                    else {
                        ++i;
                    }
                }
                final int n2 = 1;
                continue;
            }
        }
        this.minimizeGoal(this.mGoal);
    }
    
    void minimizeGoal(final Row row) throws Exception {
        final Metrics sMetrics = LinearSystem.sMetrics;
        if (sMetrics != null) {
            ++sMetrics.minimizeGoal;
            sMetrics.maxVariables = Math.max(sMetrics.maxVariables, this.mNumColumns);
            final Metrics sMetrics2 = LinearSystem.sMetrics;
            sMetrics2.maxRows = Math.max(sMetrics2.maxRows, this.mNumRows);
        }
        this.updateRowFromVariables((ArrayRow)row);
        this.enforceBFS(row);
        this.optimize(row, false);
        this.computeValues();
    }
    
    public void reset() {
        int n = 0;
        Cache mCache;
        while (true) {
            mCache = this.mCache;
            final SolverVariable[] mIndexedVariables = mCache.mIndexedVariables;
            if (n >= mIndexedVariables.length) {
                break;
            }
            final SolverVariable solverVariable = mIndexedVariables[n];
            if (solverVariable != null) {
                solverVariable.reset();
            }
            ++n;
        }
        mCache.solverVariablePool.releaseAll(this.mPoolVariables, this.mPoolVariablesCount);
        this.mPoolVariablesCount = 0;
        Arrays.fill(this.mCache.mIndexedVariables, null);
        final HashMap<String, SolverVariable> mVariables = this.mVariables;
        if (mVariables != null) {
            mVariables.clear();
        }
        this.mVariablesID = 0;
        this.mGoal.clear();
        this.mNumColumns = 1;
        for (int i = 0; i < this.mNumRows; ++i) {
            this.mRows[i].used = false;
        }
        this.releaseRows();
        this.mNumRows = 0;
    }
    
    interface Row
    {
        void addError(final SolverVariable p0);
        
        void clear();
        
        SolverVariable getKey();
        
        SolverVariable getPivotCandidate(final LinearSystem p0, final boolean[] p1);
        
        void initFromRow(final Row p0);
    }
}
