// 
// Decompiled by Procyon v0.5.36
// 

package kotlin.sequences;

import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import java.util.Iterator;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function2;

class SequencesKt__SequenceBuilderKt
{
    public static final <T> Iterator<T> iterator(final Function2<? super SequenceScope<? super T>, ? super Continuation<? super Unit>, ?> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "block");
        final SequenceBuilderIterator sequenceBuilderIterator = new SequenceBuilderIterator<Object>();
        sequenceBuilderIterator.setNextStep(IntrinsicsKt.createCoroutineUnintercepted((Function2<? super SequenceBuilderIterator<? super Object>, ? super Continuation<? super Object>, ?>)function2, (SequenceBuilderIterator<? super Object>)sequenceBuilderIterator, (Continuation<? super Object>)sequenceBuilderIterator));
        return (Iterator<T>)sequenceBuilderIterator;
    }
    
    public static <T> Sequence<T> sequence(final Function2<? super SequenceScope<? super T>, ? super Continuation<? super Unit>, ?> function2) {
        Intrinsics.checkParameterIsNotNull(function2, "block");
        return (Sequence<T>)new SequencesKt__SequenceBuilderKt$sequence$$inlined$Sequence.SequencesKt__SequenceBuilderKt$sequence$$inlined$Sequence$1((Function2)function2);
    }
}
