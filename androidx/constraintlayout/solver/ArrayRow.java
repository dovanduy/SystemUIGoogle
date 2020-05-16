// 
// Decompiled by Procyon v0.5.36
// 

package androidx.constraintlayout.solver;

public class ArrayRow implements Row
{
    float constantValue;
    boolean isSimpleDefinition;
    boolean used;
    SolverVariable variable;
    public final ArrayLinkedVariables variables;
    
    public ArrayRow(final Cache cache) {
        this.variable = null;
        this.constantValue = 0.0f;
        this.isSimpleDefinition = false;
        this.variables = new ArrayLinkedVariables(this, cache);
    }
    
    public ArrayRow addError(final LinearSystem linearSystem, final int n) {
        this.variables.put(linearSystem.createErrorVariable(n, "ep"), 1.0f);
        this.variables.put(linearSystem.createErrorVariable(n, "em"), -1.0f);
        return this;
    }
    
    @Override
    public void addError(final SolverVariable solverVariable) {
        final int strength = solverVariable.strength;
        float n = 1.0f;
        if (strength != 1) {
            if (strength == 2) {
                n = 1000.0f;
            }
            else if (strength == 3) {
                n = 1000000.0f;
            }
            else if (strength == 4) {
                n = 1.0E9f;
            }
            else if (strength == 5) {
                n = 1.0E12f;
            }
        }
        this.variables.put(solverVariable, n);
    }
    
    ArrayRow addSingleError(final SolverVariable solverVariable, final int n) {
        this.variables.put(solverVariable, (float)n);
        return this;
    }
    
    boolean chooseSubject(final LinearSystem linearSystem) {
        final SolverVariable chooseSubject = this.variables.chooseSubject(linearSystem);
        boolean b;
        if (chooseSubject == null) {
            b = true;
        }
        else {
            this.pivot(chooseSubject);
            b = false;
        }
        if (this.variables.currentSize == 0) {
            this.isSimpleDefinition = true;
        }
        return b;
    }
    
    @Override
    public void clear() {
        this.variables.clear();
        this.variable = null;
        this.constantValue = 0.0f;
    }
    
    ArrayRow createRowCentering(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n, final float n2, final SolverVariable solverVariable3, final SolverVariable solverVariable4, final int n3) {
        if (solverVariable2 == solverVariable3) {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable4, 1.0f);
            this.variables.put(solverVariable2, -2.0f);
            return this;
        }
        if (n2 == 0.5f) {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable2, -1.0f);
            this.variables.put(solverVariable3, -1.0f);
            this.variables.put(solverVariable4, 1.0f);
            if (n > 0 || n3 > 0) {
                this.constantValue = (float)(-n + n3);
            }
        }
        else if (n2 <= 0.0f) {
            this.variables.put(solverVariable, -1.0f);
            this.variables.put(solverVariable2, 1.0f);
            this.constantValue = (float)n;
        }
        else if (n2 >= 1.0f) {
            this.variables.put(solverVariable3, -1.0f);
            this.variables.put(solverVariable4, 1.0f);
            this.constantValue = (float)n3;
        }
        else {
            final ArrayLinkedVariables variables = this.variables;
            final float n4 = 1.0f - n2;
            variables.put(solverVariable, n4 * 1.0f);
            this.variables.put(solverVariable2, n4 * -1.0f);
            this.variables.put(solverVariable3, -1.0f * n2);
            this.variables.put(solverVariable4, 1.0f * n2);
            if (n > 0 || n3 > 0) {
                this.constantValue = -n * n4 + n3 * n2;
            }
        }
        return this;
    }
    
    ArrayRow createRowDefinition(final SolverVariable variable, final int n) {
        this.variable = variable;
        final float n2 = (float)n;
        variable.computedValue = n2;
        this.constantValue = n2;
        this.isSimpleDefinition = true;
        return this;
    }
    
    ArrayRow createRowDimensionPercent(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final float n) {
        this.variables.put(solverVariable, -1.0f);
        this.variables.put(solverVariable2, 1.0f - n);
        this.variables.put(solverVariable3, n);
        return this;
    }
    
    public ArrayRow createRowDimensionRatio(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final SolverVariable solverVariable4, final float n) {
        this.variables.put(solverVariable, -1.0f);
        this.variables.put(solverVariable2, 1.0f);
        this.variables.put(solverVariable3, n);
        this.variables.put(solverVariable4, -n);
        return this;
    }
    
    public ArrayRow createRowEqualMatchDimensions(float n, final float n2, final float n3, final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final SolverVariable solverVariable4) {
        this.constantValue = 0.0f;
        if (n2 != 0.0f && n != n3) {
            if (n == 0.0f) {
                this.variables.put(solverVariable, 1.0f);
                this.variables.put(solverVariable2, -1.0f);
            }
            else if (n3 == 0.0f) {
                this.variables.put(solverVariable3, 1.0f);
                this.variables.put(solverVariable4, -1.0f);
            }
            else {
                n = n / n2 / (n3 / n2);
                this.variables.put(solverVariable, 1.0f);
                this.variables.put(solverVariable2, -1.0f);
                this.variables.put(solverVariable4, n);
                this.variables.put(solverVariable3, -n);
            }
        }
        else {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable2, -1.0f);
            this.variables.put(solverVariable4, 1.0f);
            this.variables.put(solverVariable3, -1.0f);
        }
        return this;
    }
    
    public ArrayRow createRowEquals(final SolverVariable solverVariable, final int n) {
        if (n < 0) {
            this.constantValue = (float)(n * -1);
            this.variables.put(solverVariable, 1.0f);
        }
        else {
            this.constantValue = (float)n;
            this.variables.put(solverVariable, -1.0f);
        }
        return this;
    }
    
    public ArrayRow createRowEquals(final SolverVariable solverVariable, final SolverVariable solverVariable2, final int n) {
        int n2 = 0;
        final int n3 = 0;
        if (n != 0) {
            n2 = n3;
            int n4;
            if ((n4 = n) < 0) {
                n4 = n * -1;
                n2 = 1;
            }
            this.constantValue = (float)n4;
        }
        if (n2 == 0) {
            this.variables.put(solverVariable, -1.0f);
            this.variables.put(solverVariable2, 1.0f);
        }
        else {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable2, -1.0f);
        }
        return this;
    }
    
    public ArrayRow createRowGreaterThan(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final int n) {
        int n2 = 0;
        final int n3 = 0;
        if (n != 0) {
            n2 = n3;
            int n4;
            if ((n4 = n) < 0) {
                n4 = n * -1;
                n2 = 1;
            }
            this.constantValue = (float)n4;
        }
        if (n2 == 0) {
            this.variables.put(solverVariable, -1.0f);
            this.variables.put(solverVariable2, 1.0f);
            this.variables.put(solverVariable3, 1.0f);
        }
        else {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable2, -1.0f);
            this.variables.put(solverVariable3, -1.0f);
        }
        return this;
    }
    
    public ArrayRow createRowLowerThan(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final int n) {
        int n2 = 0;
        final int n3 = 0;
        if (n != 0) {
            n2 = n3;
            int n4;
            if ((n4 = n) < 0) {
                n4 = n * -1;
                n2 = 1;
            }
            this.constantValue = (float)n4;
        }
        if (n2 == 0) {
            this.variables.put(solverVariable, -1.0f);
            this.variables.put(solverVariable2, 1.0f);
            this.variables.put(solverVariable3, -1.0f);
        }
        else {
            this.variables.put(solverVariable, 1.0f);
            this.variables.put(solverVariable2, -1.0f);
            this.variables.put(solverVariable3, 1.0f);
        }
        return this;
    }
    
    public ArrayRow createRowWithAngle(final SolverVariable solverVariable, final SolverVariable solverVariable2, final SolverVariable solverVariable3, final SolverVariable solverVariable4, final float n) {
        this.variables.put(solverVariable3, 0.5f);
        this.variables.put(solverVariable4, 0.5f);
        this.variables.put(solverVariable, -0.5f);
        this.variables.put(solverVariable2, -0.5f);
        this.constantValue = -n;
        return this;
    }
    
    void ensurePositiveConstant() {
        final float constantValue = this.constantValue;
        if (constantValue < 0.0f) {
            this.constantValue = constantValue * -1.0f;
            this.variables.invert();
        }
    }
    
    @Override
    public SolverVariable getKey() {
        return this.variable;
    }
    
    @Override
    public SolverVariable getPivotCandidate(final LinearSystem linearSystem, final boolean[] array) {
        return this.variables.getPivotCandidate(array, null);
    }
    
    boolean hasKeyVariable() {
        final SolverVariable variable = this.variable;
        return variable != null && (variable.mType == SolverVariable.Type.UNRESTRICTED || this.constantValue >= 0.0f);
    }
    
    boolean hasVariable(final SolverVariable solverVariable) {
        return this.variables.containsKey(solverVariable);
    }
    
    @Override
    public void initFromRow(final Row row) {
        if (row instanceof ArrayRow) {
            final ArrayRow arrayRow = (ArrayRow)row;
            this.variable = null;
            this.variables.clear();
            int n = 0;
            while (true) {
                final ArrayLinkedVariables variables = arrayRow.variables;
                if (n >= variables.currentSize) {
                    break;
                }
                this.variables.add(variables.getVariable(n), arrayRow.variables.getVariableValue(n), true);
                ++n;
            }
        }
    }
    
    public boolean isEmpty() {
        return this.variable == null && this.constantValue == 0.0f && this.variables.currentSize == 0;
    }
    
    SolverVariable pickPivot(final SolverVariable solverVariable) {
        return this.variables.getPivotCandidate(null, solverVariable);
    }
    
    void pivot(final SolverVariable variable) {
        final SolverVariable variable2 = this.variable;
        if (variable2 != null) {
            this.variables.put(variable2, -1.0f);
            this.variable = null;
        }
        final float n = this.variables.remove(variable, true) * -1.0f;
        this.variable = variable;
        if (n == 1.0f) {
            return;
        }
        this.constantValue /= n;
        this.variables.divideByAmount(n);
    }
    
    public void reset() {
        this.variable = null;
        this.variables.clear();
        this.constantValue = 0.0f;
        this.isSimpleDefinition = false;
    }
    
    String toReadableString() {
        String str;
        if (this.variable == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append("0");
            str = sb.toString();
        }
        else {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            sb2.append(this.variable);
            str = sb2.toString();
        }
        final StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(" = ");
        String str2 = sb3.toString();
        final float constantValue = this.constantValue;
        int i = 0;
        int n;
        if (constantValue != 0.0f) {
            final StringBuilder sb4 = new StringBuilder();
            sb4.append(str2);
            sb4.append(this.constantValue);
            str2 = sb4.toString();
            n = 1;
        }
        else {
            n = 0;
        }
        while (i < this.variables.currentSize) {
            final SolverVariable variable = this.variables.getVariable(i);
            if (variable != null) {
                final float variableValue = this.variables.getVariableValue(i);
                final float n2 = fcmpl(variableValue, 0.0f);
                if (n2 != 0) {
                    final String string = variable.toString();
                    String s = null;
                    float f = 0.0f;
                    Label_0322: {
                        if (n == 0) {
                            s = str2;
                            f = variableValue;
                            if (variableValue >= 0.0f) {
                                break Label_0322;
                            }
                            final StringBuilder sb5 = new StringBuilder();
                            sb5.append(str2);
                            sb5.append("- ");
                            s = sb5.toString();
                        }
                        else {
                            if (n2 > 0) {
                                final StringBuilder sb6 = new StringBuilder();
                                sb6.append(str2);
                                sb6.append(" + ");
                                s = sb6.toString();
                                f = variableValue;
                                break Label_0322;
                            }
                            final StringBuilder sb7 = new StringBuilder();
                            sb7.append(str2);
                            sb7.append(" - ");
                            s = sb7.toString();
                        }
                        f = variableValue * -1.0f;
                    }
                    if (f == 1.0f) {
                        final StringBuilder sb8 = new StringBuilder();
                        sb8.append(s);
                        sb8.append(string);
                        str2 = sb8.toString();
                    }
                    else {
                        final StringBuilder sb9 = new StringBuilder();
                        sb9.append(s);
                        sb9.append(f);
                        sb9.append(" ");
                        sb9.append(string);
                        str2 = sb9.toString();
                    }
                    n = 1;
                }
            }
            ++i;
        }
        String string2 = str2;
        if (n == 0) {
            final StringBuilder sb10 = new StringBuilder();
            sb10.append(str2);
            sb10.append("0.0");
            string2 = sb10.toString();
        }
        return string2;
    }
    
    @Override
    public String toString() {
        return this.toReadableString();
    }
}
