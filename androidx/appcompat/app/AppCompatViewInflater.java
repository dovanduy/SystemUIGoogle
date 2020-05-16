// 
// Decompiled by Procyon v0.5.36
// 

package androidx.appcompat.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import androidx.appcompat.widget.TintContextWrapper;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.view.ContextThemeWrapper;
import android.util.Log;
import androidx.appcompat.R$styleable;
import android.view.InflateException;
import android.content.res.TypedArray;
import android.view.View$OnClickListener;
import androidx.core.view.ViewCompat;
import android.os.Build$VERSION;
import android.content.ContextWrapper;
import android.util.AttributeSet;
import android.content.Context;
import android.view.View;
import java.lang.reflect.Constructor;
import androidx.collection.SimpleArrayMap;

public class AppCompatViewInflater
{
    private static final String[] sClassPrefixList;
    private static final SimpleArrayMap<String, Constructor<? extends View>> sConstructorMap;
    private static final Class<?>[] sConstructorSignature;
    private static final int[] sOnClickAttrs;
    private final Object[] mConstructorArgs;
    
    static {
        sConstructorSignature = new Class[] { Context.class, AttributeSet.class };
        sOnClickAttrs = new int[] { 16843375 };
        sClassPrefixList = new String[] { "android.widget.", "android.view.", "android.webkit." };
        sConstructorMap = new SimpleArrayMap<String, Constructor<? extends View>>();
    }
    
    public AppCompatViewInflater() {
        this.mConstructorArgs = new Object[2];
    }
    
    private void checkOnClickListener(final View view, final AttributeSet set) {
        final Context context = view.getContext();
        if (context instanceof ContextWrapper) {
            if (Build$VERSION.SDK_INT < 15 || ViewCompat.hasOnClickListeners(view)) {
                final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, AppCompatViewInflater.sOnClickAttrs);
                final String string = obtainStyledAttributes.getString(0);
                if (string != null) {
                    view.setOnClickListener((View$OnClickListener)new DeclaredOnClickListener(view, string));
                }
                obtainStyledAttributes.recycle();
            }
        }
    }
    
    private View createViewByPrefix(final Context context, final String str, String string) throws ClassNotFoundException, InflateException {
        Label_0092: {
            Constructor<? extends View> constructor;
            if ((constructor = AppCompatViewInflater.sConstructorMap.get(str)) != null) {
                break Label_0092;
            }
            Label_0058: {
                if (string == null) {
                    break Label_0058;
                }
                try {
                    final StringBuilder sb = new StringBuilder();
                    sb.append(string);
                    sb.append(str);
                    string = sb.toString();
                    while (true) {
                        constructor = Class.forName(string, false, context.getClassLoader()).asSubclass(View.class).getConstructor(AppCompatViewInflater.sConstructorSignature);
                        AppCompatViewInflater.sConstructorMap.put(str, constructor);
                        constructor.setAccessible(true);
                        return (View)constructor.newInstance(this.mConstructorArgs);
                        string = str;
                        continue;
                    }
                }
                catch (Exception ex) {
                    return null;
                }
            }
        }
    }
    
    private View createViewFromTag(final Context context, final String s, final AttributeSet set) {
        final String[] sClassPrefixList = AppCompatViewInflater.sClassPrefixList;
        String attributeValue = s;
        if (s.equals("view")) {
            attributeValue = set.getAttributeValue((String)null, "class");
        }
        try {
            this.mConstructorArgs[0] = context;
            this.mConstructorArgs[1] = set;
            if (-1 == attributeValue.indexOf(46)) {
                for (int i = 0; i < sClassPrefixList.length; ++i) {
                    final View viewByPrefix = this.createViewByPrefix(context, attributeValue, sClassPrefixList[i]);
                    if (viewByPrefix != null) {
                        return viewByPrefix;
                    }
                }
                return null;
            }
            return this.createViewByPrefix(context, attributeValue, null);
        }
        catch (Exception ex) {
            return null;
        }
        finally {
            final Object[] mConstructorArgs = this.mConstructorArgs;
            mConstructorArgs[1] = (mConstructorArgs[0] = null);
        }
    }
    
    private static Context themifyContext(final Context context, final AttributeSet set, final boolean b, final boolean b2) {
        final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, R$styleable.View, 0, 0);
        int resourceId;
        if (b) {
            resourceId = obtainStyledAttributes.getResourceId(R$styleable.View_android_theme, 0);
        }
        else {
            resourceId = 0;
        }
        int n = resourceId;
        if (b2 && (n = resourceId) == 0) {
            final int resourceId2 = obtainStyledAttributes.getResourceId(R$styleable.View_theme, 0);
            if ((n = resourceId2) != 0) {
                Log.i("AppCompatViewInflater", "app:theme is now deprecated. Please move to using android:theme instead.");
                n = resourceId2;
            }
        }
        obtainStyledAttributes.recycle();
        Object o = context;
        if (n != 0) {
            if (context instanceof ContextThemeWrapper) {
                o = context;
                if (((ContextThemeWrapper)context).getThemeResId() == n) {
                    return (Context)o;
                }
            }
            o = new ContextThemeWrapper(context, n);
        }
        return (Context)o;
    }
    
    private void verifyNotNull(final View view, final String str) {
        if (view != null) {
            return;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(AppCompatViewInflater.class.getName());
        sb.append(" asked to inflate view for <");
        sb.append(str);
        sb.append(">, but returned null");
        throw new IllegalStateException(sb.toString());
    }
    
    protected AppCompatAutoCompleteTextView createAutoCompleteTextView(final Context context, final AttributeSet set) {
        return new AppCompatAutoCompleteTextView(context, set);
    }
    
    protected AppCompatButton createButton(final Context context, final AttributeSet set) {
        return new AppCompatButton(context, set);
    }
    
    protected AppCompatCheckBox createCheckBox(final Context context, final AttributeSet set) {
        return new AppCompatCheckBox(context, set);
    }
    
    protected AppCompatCheckedTextView createCheckedTextView(final Context context, final AttributeSet set) {
        return new AppCompatCheckedTextView(context, set);
    }
    
    protected AppCompatEditText createEditText(final Context context, final AttributeSet set) {
        return new AppCompatEditText(context, set);
    }
    
    protected AppCompatImageButton createImageButton(final Context context, final AttributeSet set) {
        return new AppCompatImageButton(context, set);
    }
    
    protected AppCompatImageView createImageView(final Context context, final AttributeSet set) {
        return new AppCompatImageView(context, set);
    }
    
    protected AppCompatMultiAutoCompleteTextView createMultiAutoCompleteTextView(final Context context, final AttributeSet set) {
        return new AppCompatMultiAutoCompleteTextView(context, set);
    }
    
    protected AppCompatRadioButton createRadioButton(final Context context, final AttributeSet set) {
        return new AppCompatRadioButton(context, set);
    }
    
    protected AppCompatRatingBar createRatingBar(final Context context, final AttributeSet set) {
        return new AppCompatRatingBar(context, set);
    }
    
    protected AppCompatSeekBar createSeekBar(final Context context, final AttributeSet set) {
        return new AppCompatSeekBar(context, set);
    }
    
    protected AppCompatSpinner createSpinner(final Context context, final AttributeSet set) {
        return new AppCompatSpinner(context, set);
    }
    
    protected AppCompatTextView createTextView(final Context context, final AttributeSet set) {
        return new AppCompatTextView(context, set);
    }
    
    protected AppCompatToggleButton createToggleButton(final Context context, final AttributeSet set) {
        return new AppCompatToggleButton(context, set);
    }
    
    protected View createView(final Context context, final String s, final AttributeSet set) {
        return null;
    }
    
    final View createView(View o, final String s, final Context context, final AttributeSet set, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        Context context2;
        if (b && o != null) {
            context2 = ((View)o).getContext();
        }
        else {
            context2 = context;
        }
        Context themifyContext = null;
        Label_0046: {
            if (!b2) {
                themifyContext = context2;
                if (!b3) {
                    break Label_0046;
                }
            }
            themifyContext = themifyContext(context2, set, b2, b3);
        }
        Context wrap = themifyContext;
        if (b4) {
            wrap = TintContextWrapper.wrap(themifyContext);
        }
        switch (s) {
            default: {
                o = this.createView(wrap, s, set);
                break;
            }
            case "ToggleButton": {
                o = this.createToggleButton(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "SeekBar": {
                o = this.createSeekBar(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "RatingBar": {
                o = this.createRatingBar(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "MultiAutoCompleteTextView": {
                o = this.createMultiAutoCompleteTextView(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "AutoCompleteTextView": {
                o = this.createAutoCompleteTextView(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "CheckedTextView": {
                o = this.createCheckedTextView(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "RadioButton": {
                o = this.createRadioButton(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "CheckBox": {
                o = this.createCheckBox(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "ImageButton": {
                o = this.createImageButton(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "Spinner": {
                o = this.createSpinner(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "EditText": {
                o = this.createEditText(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "Button": {
                o = this.createButton(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "ImageView": {
                o = this.createImageView(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
            case "TextView": {
                o = this.createTextView(wrap, set);
                this.verifyNotNull((View)o, s);
                break;
            }
        }
        Object viewFromTag = o;
        if (o == null) {
            viewFromTag = o;
            if (context != wrap) {
                viewFromTag = this.createViewFromTag(wrap, s, set);
            }
        }
        if (viewFromTag != null) {
            this.checkOnClickListener((View)viewFromTag, set);
        }
        return (View)viewFromTag;
    }
    
    private static class DeclaredOnClickListener implements View$OnClickListener
    {
        private final View mHostView;
        private final String mMethodName;
        private Context mResolvedContext;
        private Method mResolvedMethod;
        
        public DeclaredOnClickListener(final View mHostView, final String mMethodName) {
            this.mHostView = mHostView;
            this.mMethodName = mMethodName;
        }
        
        private void resolveMethod(Context context) {
        Label_0047_Outer:
            while (true) {
                Label_0070: {
                    if (context == null) {
                        break Label_0070;
                    }
                    while (true) {
                        try {
                            if (!context.isRestricted()) {
                                final Method method = context.getClass().getMethod(this.mMethodName, View.class);
                                if (method != null) {
                                    this.mResolvedMethod = method;
                                    this.mResolvedContext = context;
                                    return;
                                }
                            }
                            if (context instanceof ContextWrapper) {
                                context = ((ContextWrapper)context).getBaseContext();
                                continue Label_0047_Outer;
                            }
                            context = null;
                            continue Label_0047_Outer;
                            final int id = this.mHostView.getId();
                            // iftrue(Label_0089:, id != -1)
                            while (true) {
                                Block_5: {
                                    break Block_5;
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Could not find method ");
                                    sb.append(this.mMethodName);
                                    sb.append("(View) in a parent or ancestor Context for android:onClick attribute defined on view ");
                                    sb.append(this.mHostView.getClass());
                                    sb.append((String)context);
                                    throw new IllegalStateException(sb.toString());
                                }
                                context = (Context)"";
                                continue;
                                Label_0089: {
                                    context = (Context)new StringBuilder();
                                }
                                ((StringBuilder)context).append(" with id '");
                                ((StringBuilder)context).append(this.mHostView.getContext().getResources().getResourceEntryName(id));
                                ((StringBuilder)context).append("'");
                                context = (Context)((StringBuilder)context).toString();
                                continue;
                            }
                        }
                        catch (NoSuchMethodException ex) {
                            continue;
                        }
                        break;
                    }
                }
            }
        }
        
        public void onClick(final View view) {
            if (this.mResolvedMethod == null) {
                this.resolveMethod(this.mHostView.getContext());
            }
            try {
                this.mResolvedMethod.invoke(this.mResolvedContext, view);
            }
            catch (InvocationTargetException cause) {
                throw new IllegalStateException("Could not execute method for android:onClick", cause);
            }
            catch (IllegalAccessException cause2) {
                throw new IllegalStateException("Could not execute non-public method for android:onClick", cause2);
            }
        }
    }
}
