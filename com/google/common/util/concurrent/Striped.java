// 
// Decompiled by Procyon v0.5.36
// 

package com.google.common.util.concurrent;

public abstract class Striped<L>
{
    private Striped() {
    }
    
    static class LargeLazyStriped<L> extends PowerOfTwoStriped<L>
    {
    }
    
    private abstract static class PowerOfTwoStriped<L> extends Striped<L>
    {
    }
    
    static class SmallLazyStriped<L> extends PowerOfTwoStriped<L>
    {
    }
}
