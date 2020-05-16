// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.statusbar.phone;

import java.util.Iterator;
import android.view.View;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ContextualButtonGroup extends ButtonDispatcher
{
    private final List<ButtonData> mButtonData;
    
    public ContextualButtonGroup(final int n) {
        super(n);
        this.mButtonData = new ArrayList<ButtonData>();
    }
    
    private int getContextButtonIndex(final int n) {
        for (int i = 0; i < this.mButtonData.size(); ++i) {
            if (this.mButtonData.get(i).button.getId() == n) {
                return i;
            }
        }
        return -1;
    }
    
    public void addButton(final ContextualButton contextualButton) {
        contextualButton.attachToGroup(this);
        this.mButtonData.add(new ButtonData(contextualButton));
    }
    
    public void dump(final PrintWriter printWriter) {
        final View currentView = this.getCurrentView();
        printWriter.println("ContextualButtonGroup {");
        final StringBuilder sb = new StringBuilder();
        sb.append("      getVisibleContextButton(): ");
        sb.append(this.getVisibleContextButton());
        printWriter.println(sb.toString());
        final StringBuilder sb2 = new StringBuilder();
        sb2.append("      isVisible(): ");
        sb2.append(this.isVisible());
        printWriter.println(sb2.toString());
        final StringBuilder sb3 = new StringBuilder();
        sb3.append("      attached(): ");
        sb3.append(currentView != null && currentView.isAttachedToWindow());
        printWriter.println(sb3.toString());
        printWriter.println("      mButtonData [ ");
        for (int i = this.mButtonData.size() - 1; i >= 0; --i) {
            final ButtonData buttonData = this.mButtonData.get(i);
            final View currentView2 = buttonData.button.getCurrentView();
            final StringBuilder sb4 = new StringBuilder();
            sb4.append("            ");
            sb4.append(i);
            sb4.append(": markedVisible=");
            sb4.append(buttonData.markedVisible);
            sb4.append(" visible=");
            sb4.append(buttonData.button.getVisibility());
            sb4.append(" attached=");
            sb4.append(currentView2 != null && currentView2.isAttachedToWindow());
            sb4.append(" alpha=");
            sb4.append(buttonData.button.getAlpha());
            printWriter.println(sb4.toString());
        }
        printWriter.println("      ]");
        printWriter.println("    }");
    }
    
    public ContextualButton getVisibleContextButton() {
        for (int i = this.mButtonData.size() - 1; i >= 0; --i) {
            if (this.mButtonData.get(i).markedVisible) {
                return this.mButtonData.get(i).button;
            }
        }
        return null;
    }
    
    public int setButtonVisibility(int i, final boolean markedVisible) {
        final int contextButtonIndex = this.getContextButtonIndex(i);
        if (contextButtonIndex != -1) {
            this.setVisibility(4);
            this.mButtonData.get(contextButtonIndex).markedVisible = markedVisible;
            i = this.mButtonData.size() - 1;
            int n = 0;
            while (i >= 0) {
                final ButtonData buttonData = this.mButtonData.get(i);
                if (n == 0 && buttonData.markedVisible) {
                    buttonData.setVisibility(0);
                    this.setVisibility(0);
                    n = 1;
                }
                else {
                    buttonData.setVisibility(4);
                }
                --i;
            }
            return this.mButtonData.get(contextButtonIndex).button.getVisibility();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Cannot find the button id of ");
        sb.append(i);
        sb.append(" in context group");
        throw new RuntimeException(sb.toString());
    }
    
    public void updateIcons() {
        final Iterator<ButtonData> iterator = this.mButtonData.iterator();
        while (iterator.hasNext()) {
            iterator.next().button.updateIcon();
        }
    }
    
    private static final class ButtonData
    {
        ContextualButton button;
        boolean markedVisible;
        
        ButtonData(final ContextualButton button) {
            this.button = button;
            this.markedVisible = false;
        }
        
        void setVisibility(final int visibility) {
            this.button.setVisibility(visibility);
        }
    }
}
