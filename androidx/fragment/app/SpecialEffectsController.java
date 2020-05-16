// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import androidx.fragment.R$id;
import androidx.core.os.CancellationSignal;
import java.util.ArrayList;
import android.view.ViewGroup;
import java.util.HashMap;

abstract class SpecialEffectsController
{
    final HashMap<Fragment, Operation> mAwaitingCompletionOperations;
    private final ViewGroup mContainer;
    final ArrayList<Operation> mPendingOperations;
    
    SpecialEffectsController(final ViewGroup mContainer) {
        this.mPendingOperations = new ArrayList<Operation>();
        this.mAwaitingCompletionOperations = new HashMap<Fragment, Operation>();
        this.mContainer = mContainer;
    }
    
    private void enqueue(final Type type, final FragmentStateManager fragmentStateManager, final CancellationSignal cancellationSignal) {
        if (cancellationSignal.isCanceled()) {
            return;
        }
        synchronized (this.mPendingOperations) {
            final CancellationSignal cancellationSignal2 = new CancellationSignal();
            final FragmentStateManagerOperation fragmentStateManagerOperation = new FragmentStateManagerOperation(type, fragmentStateManager, cancellationSignal2);
            this.mPendingOperations.add((Operation)fragmentStateManagerOperation);
            this.mAwaitingCompletionOperations.put(((Operation)fragmentStateManagerOperation).getFragment(), (Operation)fragmentStateManagerOperation);
            cancellationSignal.setOnCancelListener((CancellationSignal.OnCancelListener)new CancellationSignal.OnCancelListener() {
                @Override
                public void onCancel() {
                    synchronized (SpecialEffectsController.this.mPendingOperations) {
                        SpecialEffectsController.this.mPendingOperations.remove(fragmentStateManagerOperation);
                        SpecialEffectsController.this.mAwaitingCompletionOperations.remove(((Operation)fragmentStateManagerOperation).getFragment());
                        cancellationSignal2.cancel();
                    }
                }
            });
            ((Operation)fragmentStateManagerOperation).addCompletionListener(new Runnable() {
                @Override
                public void run() {
                    SpecialEffectsController.this.mAwaitingCompletionOperations.remove(((Operation)fragmentStateManagerOperation).getFragment());
                }
            });
        }
    }
    
    static SpecialEffectsController getOrCreateController(final ViewGroup viewGroup, final FragmentManager fragmentManager) {
        return getOrCreateController(viewGroup, fragmentManager.getSpecialEffectsControllerFactory());
    }
    
    static SpecialEffectsController getOrCreateController(final ViewGroup viewGroup, final SpecialEffectsControllerFactory specialEffectsControllerFactory) {
        final Object tag = viewGroup.getTag(R$id.special_effects_controller_view_tag);
        if (tag instanceof SpecialEffectsController) {
            return (SpecialEffectsController)tag;
        }
        final SpecialEffectsController controller = specialEffectsControllerFactory.createController(viewGroup);
        viewGroup.setTag(R$id.special_effects_controller_view_tag, (Object)controller);
        return controller;
    }
    
    void cancelAllOperations() {
        synchronized (this.mPendingOperations) {
            final Iterator<Operation> iterator = this.mAwaitingCompletionOperations.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().getCancellationSignal().cancel();
            }
            this.mAwaitingCompletionOperations.clear();
            this.mPendingOperations.clear();
        }
    }
    
    void enqueueAdd(final FragmentStateManager fragmentStateManager, final CancellationSignal cancellationSignal) {
        this.enqueue(Type.ADD, fragmentStateManager, cancellationSignal);
    }
    
    void enqueueRemove(final FragmentStateManager fragmentStateManager, final CancellationSignal cancellationSignal) {
        this.enqueue(Type.REMOVE, fragmentStateManager, cancellationSignal);
    }
    
    abstract void executeOperations(final List<Operation> p0);
    
    void executePendingOperations() {
        synchronized (this.mPendingOperations) {
            this.executeOperations(new ArrayList<Operation>(this.mPendingOperations));
            this.mPendingOperations.clear();
        }
    }
    
    Type getAwaitingCompletionType(final FragmentStateManager fragmentStateManager) {
        final Operation operation = this.mAwaitingCompletionOperations.get(fragmentStateManager.getFragment());
        if (operation != null) {
            return operation.getType();
        }
        return null;
    }
    
    public ViewGroup getContainer() {
        return this.mContainer;
    }
    
    private static class FragmentStateManagerOperation extends Operation
    {
        private final FragmentStateManager mFragmentStateManager;
        
        FragmentStateManagerOperation(final Type type, final FragmentStateManager mFragmentStateManager, final CancellationSignal cancellationSignal) {
            super(type, mFragmentStateManager.getFragment(), cancellationSignal);
            this.mFragmentStateManager = mFragmentStateManager;
        }
        
        @Override
        public void complete() {
            super.complete();
            this.mFragmentStateManager.moveToExpectedState();
        }
    }
    
    static class Operation
    {
        private final CancellationSignal mCancellationSignal;
        private final List<Runnable> mCompletionListeners;
        private final Fragment mFragment;
        private final Type mType;
        
        Operation(final Type mType, final Fragment mFragment, final CancellationSignal mCancellationSignal) {
            this.mCompletionListeners = new ArrayList<Runnable>();
            this.mType = mType;
            this.mFragment = mFragment;
            this.mCancellationSignal = mCancellationSignal;
        }
        
        final void addCompletionListener(final Runnable runnable) {
            this.mCompletionListeners.add(runnable);
        }
        
        public void complete() {
            final Iterator<Runnable> iterator = this.mCompletionListeners.iterator();
            while (iterator.hasNext()) {
                iterator.next().run();
            }
        }
        
        public final CancellationSignal getCancellationSignal() {
            return this.mCancellationSignal;
        }
        
        public final Fragment getFragment() {
            return this.mFragment;
        }
        
        public final Type getType() {
            return this.mType;
        }
        
        enum Type
        {
            ADD, 
            REMOVE;
        }
    }
}
