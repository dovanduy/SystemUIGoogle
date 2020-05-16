// 
// Decompiled by Procyon v0.5.36
// 

package androidx.slice.widget;

public class EventInfo
{
    public int actionCount;
    public int actionIndex;
    public int actionPosition;
    public int actionType;
    public int rowIndex;
    public int rowTemplateType;
    public int sliceMode;
    public int state;
    
    public EventInfo(final int sliceMode, final int actionType, final int rowTemplateType, final int rowIndex) {
        this.sliceMode = sliceMode;
        this.actionType = actionType;
        this.rowTemplateType = rowTemplateType;
        this.rowIndex = rowIndex;
        this.actionPosition = -1;
        this.actionIndex = -1;
        this.actionCount = -1;
        this.state = -1;
    }
    
    private static String actionToString(final int i) {
        if (i == 0) {
            return "TOGGLE";
        }
        if (i == 1) {
            return "BUTTON";
        }
        if (i == 2) {
            return "SLIDER";
        }
        if (i == 3) {
            return "CONTENT";
        }
        if (i == 4) {
            return "SEE MORE";
        }
        if (i != 5) {
            final StringBuilder sb = new StringBuilder();
            sb.append("unknown action: ");
            sb.append(i);
            return sb.toString();
        }
        return "SELECTION";
    }
    
    private static String positionToString(final int i) {
        if (i == 0) {
            return "START";
        }
        if (i == 1) {
            return "END";
        }
        if (i != 2) {
            final StringBuilder sb = new StringBuilder();
            sb.append("unknown position: ");
            sb.append(i);
            return sb.toString();
        }
        return "CELL";
    }
    
    private static String rowTypeToString(final int i) {
        switch (i) {
            default: {
                final StringBuilder sb = new StringBuilder();
                sb.append("unknown row type: ");
                sb.append(i);
                return sb.toString();
            }
            case 6: {
                return "SELECTION";
            }
            case 5: {
                return "PROGRESS";
            }
            case 4: {
                return "SLIDER";
            }
            case 3: {
                return "TOGGLE";
            }
            case 2: {
                return "MESSAGING";
            }
            case 1: {
                return "GRID";
            }
            case 0: {
                return "LIST";
            }
            case -1: {
                return "SHORTCUT";
            }
        }
    }
    
    public void setPosition(final int actionPosition, final int actionIndex, final int actionCount) {
        this.actionPosition = actionPosition;
        this.actionIndex = actionIndex;
        this.actionCount = actionCount;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("mode=");
        sb.append(SliceView.modeToString(this.sliceMode));
        sb.append(", actionType=");
        sb.append(actionToString(this.actionType));
        sb.append(", rowTemplateType=");
        sb.append(rowTypeToString(this.rowTemplateType));
        sb.append(", rowIndex=");
        sb.append(this.rowIndex);
        sb.append(", actionPosition=");
        sb.append(positionToString(this.actionPosition));
        sb.append(", actionIndex=");
        sb.append(this.actionIndex);
        sb.append(", actionCount=");
        sb.append(this.actionCount);
        sb.append(", state=");
        sb.append(this.state);
        return sb.toString();
    }
}
