// 
// Decompiled by Procyon v0.5.36
// 

package androidx.fragment.app;

import android.content.res.TypedArray;
import android.util.Log;
import androidx.fragment.R$styleable;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import android.view.LayoutInflater$Factory2;

class FragmentLayoutInflaterFactory implements LayoutInflater$Factory2
{
    private final FragmentManager mFragmentManager;
    
    FragmentLayoutInflaterFactory(final FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }
    
    public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        if (FragmentContainerView.class.getName().equals(s)) {
            return (View)new FragmentContainerView(context, set, this.mFragmentManager);
        }
        final boolean equals = "fragment".equals(s);
        final Fragment fragment = null;
        if (!equals) {
            return null;
        }
        final String attributeValue = set.getAttributeValue((String)null, "class");
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.Fragment);
        String string;
        if ((string = attributeValue) == null) {
            string = obtainStyledAttributes.getString(R$styleable.Fragment_android_name);
        }
        final int resourceId = obtainStyledAttributes.getResourceId(R$styleable.Fragment_android_id, -1);
        final String string2 = obtainStyledAttributes.getString(R$styleable.Fragment_android_tag);
        obtainStyledAttributes.recycle();
        if (string == null || !FragmentFactory.isFragmentClass(context.getClassLoader(), string)) {
            return null;
        }
        int id;
        if (view != null) {
            id = view.getId();
        }
        else {
            id = 0;
        }
        if (id == -1 && resourceId == -1 && string2 == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(set.getPositionDescription());
            sb.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
            sb.append(string);
            throw new IllegalArgumentException(sb.toString());
        }
        Fragment fragmentById = fragment;
        if (resourceId != -1) {
            fragmentById = this.mFragmentManager.findFragmentById(resourceId);
        }
        Fragment fragmentByTag;
        if ((fragmentByTag = fragmentById) == null) {
            fragmentByTag = fragmentById;
            if (string2 != null) {
                fragmentByTag = this.mFragmentManager.findFragmentByTag(string2);
            }
        }
        Fragment obj;
        if ((obj = fragmentByTag) == null) {
            obj = fragmentByTag;
            if (id != -1) {
                obj = this.mFragmentManager.findFragmentById(id);
            }
        }
        if (FragmentManager.isLoggingEnabled(2)) {
            final StringBuilder sb2 = new StringBuilder();
            sb2.append("onCreateView: id=0x");
            sb2.append(Integer.toHexString(resourceId));
            sb2.append(" fname=");
            sb2.append(string);
            sb2.append(" existing=");
            sb2.append(obj);
            Log.v("FragmentManager", sb2.toString());
        }
        FragmentStateManager fragmentStateManager;
        if (obj == null) {
            obj = this.mFragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), string);
            obj.mFromLayout = true;
            int mFragmentId;
            if (resourceId != 0) {
                mFragmentId = resourceId;
            }
            else {
                mFragmentId = id;
            }
            obj.mFragmentId = mFragmentId;
            obj.mContainerId = id;
            obj.mTag = string2;
            obj.mInLayout = true;
            final FragmentManager mFragmentManager = this.mFragmentManager;
            obj.mFragmentManager = mFragmentManager;
            obj.mHost = mFragmentManager.getHost();
            obj.onInflate(this.mFragmentManager.getHost().getContext(), set, obj.mSavedFragmentState);
            fragmentStateManager = this.mFragmentManager.createOrGetFragmentStateManager(obj);
            this.mFragmentManager.addFragment(obj);
        }
        else {
            if (obj.mInLayout) {
                final StringBuilder sb3 = new StringBuilder();
                sb3.append(set.getPositionDescription());
                sb3.append(": Duplicate id 0x");
                sb3.append(Integer.toHexString(resourceId));
                sb3.append(", tag ");
                sb3.append(string2);
                sb3.append(", or parent id 0x");
                sb3.append(Integer.toHexString(id));
                sb3.append(" with another fragment for ");
                sb3.append(string);
                throw new IllegalArgumentException(sb3.toString());
            }
            obj.mInLayout = true;
            final FragmentManager mFragmentManager2 = this.mFragmentManager;
            obj.mFragmentManager = mFragmentManager2;
            obj.mHost = mFragmentManager2.getHost();
            obj.onInflate(this.mFragmentManager.getHost().getContext(), set, obj.mSavedFragmentState);
            fragmentStateManager = this.mFragmentManager.createOrGetFragmentStateManager(obj);
        }
        fragmentStateManager.moveToExpectedState();
        fragmentStateManager.ensureInflatedView();
        final View mView = obj.mView;
        if (mView != null) {
            if (resourceId != 0) {
                mView.setId(resourceId);
            }
            if (obj.mView.getTag() == null) {
                obj.mView.setTag((Object)string2);
            }
            return obj.mView;
        }
        final StringBuilder sb4 = new StringBuilder();
        sb4.append("Fragment ");
        sb4.append(string);
        sb4.append(" did not create a view.");
        throw new IllegalStateException(sb4.toString());
    }
    
    public View onCreateView(final String s, final Context context, final AttributeSet set) {
        return this.onCreateView(null, s, context, set);
    }
}
