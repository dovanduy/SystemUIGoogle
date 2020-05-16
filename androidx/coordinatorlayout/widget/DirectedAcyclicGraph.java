// 
// Decompiled by Procyon v0.5.36
// 

package androidx.coordinatorlayout.widget;

import androidx.core.util.Pools$SimplePool;
import java.util.HashSet;
import androidx.core.util.Pools$Pool;
import java.util.ArrayList;
import androidx.collection.SimpleArrayMap;

public final class DirectedAcyclicGraph<T>
{
    private final SimpleArrayMap<T, ArrayList<T>> mGraph;
    private final Pools$Pool<ArrayList<T>> mListPool;
    private final ArrayList<T> mSortResult;
    private final HashSet<T> mSortTmpMarked;
    
    public DirectedAcyclicGraph() {
        this.mListPool = new Pools$SimplePool<ArrayList<T>>(10);
        this.mGraph = new SimpleArrayMap<T, ArrayList<T>>();
        this.mSortResult = new ArrayList<T>();
        this.mSortTmpMarked = new HashSet<T>();
    }
    
    private void dfs(final T e, final ArrayList<T> list, final HashSet<T> set) {
        if (list.contains(e)) {
            return;
        }
        if (!set.contains(e)) {
            set.add(e);
            final ArrayList<T> list2 = this.mGraph.get(e);
            if (list2 != null) {
                for (int i = 0; i < list2.size(); ++i) {
                    this.dfs(list2.get(i), list, set);
                }
            }
            set.remove(e);
            list.add(e);
            return;
        }
        throw new RuntimeException("This graph contains cyclic dependencies");
    }
    
    private ArrayList<T> getEmptyList() {
        ArrayList<T> list;
        if ((list = this.mListPool.acquire()) == null) {
            list = new ArrayList<T>();
        }
        return list;
    }
    
    private void poolList(final ArrayList<T> list) {
        list.clear();
        this.mListPool.release(list);
    }
    
    public void addEdge(final T t, final T e) {
        if (this.mGraph.containsKey(t) && this.mGraph.containsKey(e)) {
            ArrayList<T> emptyList;
            if ((emptyList = this.mGraph.get(t)) == null) {
                emptyList = this.getEmptyList();
                this.mGraph.put(t, emptyList);
            }
            emptyList.add(e);
            return;
        }
        throw new IllegalArgumentException("All nodes must be present in the graph before being added as an edge");
    }
    
    public void addNode(final T t) {
        if (!this.mGraph.containsKey(t)) {
            this.mGraph.put(t, null);
        }
    }
    
    public void clear() {
        for (int size = this.mGraph.size(), i = 0; i < size; ++i) {
            final ArrayList<T> list = this.mGraph.valueAt(i);
            if (list != null) {
                this.poolList(list);
            }
        }
        this.mGraph.clear();
    }
    
    public boolean contains(final T t) {
        return this.mGraph.containsKey(t);
    }
    
    public ArrayList<T> getSortedList() {
        this.mSortResult.clear();
        this.mSortTmpMarked.clear();
        for (int size = this.mGraph.size(), i = 0; i < size; ++i) {
            this.dfs(this.mGraph.keyAt(i), this.mSortResult, this.mSortTmpMarked);
        }
        return this.mSortResult;
    }
    
    public boolean hasOutgoingEdges(final T o) {
        for (int size = this.mGraph.size(), i = 0; i < size; ++i) {
            final ArrayList<T> list = this.mGraph.valueAt(i);
            if (list != null && list.contains(o)) {
                return true;
            }
        }
        return false;
    }
}
