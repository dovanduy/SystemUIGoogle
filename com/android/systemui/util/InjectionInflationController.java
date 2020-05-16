// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.util;

import com.android.systemui.qs.QuickQSPanel;
import com.android.systemui.qs.QuickStatusBarHeader;
import com.android.systemui.qs.QSFooterImpl;
import com.android.systemui.qs.QSPanel;
import com.android.systemui.qs.customize.QSCustomizer;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.keyguard.KeyguardSliceView;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.systemui.statusbar.NotificationShelf;
import java.lang.reflect.InvocationTargetException;
import android.view.InflateException;
import android.util.AttributeSet;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.android.systemui.dagger.SystemUIRootComponent;
import java.lang.reflect.Method;
import android.util.ArrayMap;
import android.view.LayoutInflater$Factory2;

public class InjectionInflationController
{
    private final LayoutInflater$Factory2 mFactory;
    private final ArrayMap<String, Method> mInjectionMap;
    private final ViewCreator mViewCreator;
    
    public InjectionInflationController(final SystemUIRootComponent systemUIRootComponent) {
        this.mInjectionMap = (ArrayMap<String, Method>)new ArrayMap();
        this.mFactory = (LayoutInflater$Factory2)new InjectionFactory();
        this.mViewCreator = systemUIRootComponent.createViewCreator();
        this.initInjectionMap();
    }
    
    private void initInjectionMap() {
        for (final Method method : ViewInstanceCreator.class.getDeclaredMethods()) {
            if (View.class.isAssignableFrom(method.getReturnType()) && (method.getModifiers() & 0x1) != 0x0) {
                this.mInjectionMap.put((Object)method.getReturnType().getName(), (Object)method);
            }
        }
    }
    
    public LayoutInflater injectable(LayoutInflater cloneInContext) {
        cloneInContext = cloneInContext.cloneInContext(cloneInContext.getContext());
        cloneInContext.setPrivateFactory(this.mFactory);
        return cloneInContext;
    }
    
    private class InjectionFactory implements LayoutInflater$Factory2
    {
        public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
            return this.onCreateView(s, context, set);
        }
        
        public View onCreateView(final String s, final Context context, final AttributeSet set) {
            final Method method = (Method)InjectionInflationController.this.mInjectionMap.get((Object)s);
            if (method != null) {
                final ViewAttributeProvider viewAttributeProvider = new ViewAttributeProvider(context, set);
                try {
                    return (View)method.invoke(InjectionInflationController.this.mViewCreator.createInstanceCreator(viewAttributeProvider), new Object[0]);
                }
                catch (InvocationTargetException ex) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("Could not inflate ");
                    sb.append(s);
                    throw new InflateException(sb.toString(), (Throwable)ex);
                }
                catch (IllegalAccessException ex2) {
                    final StringBuilder sb2 = new StringBuilder();
                    sb2.append("Could not inflate ");
                    sb2.append(s);
                    throw new InflateException(sb2.toString(), (Throwable)ex2);
                }
            }
            return null;
        }
    }
    
    public class ViewAttributeProvider
    {
        private final AttributeSet mAttrs;
        private final Context mContext;
        
        private ViewAttributeProvider(final InjectionInflationController injectionInflationController, final Context mContext, final AttributeSet mAttrs) {
            this.mContext = mContext;
            this.mAttrs = mAttrs;
        }
        
        public AttributeSet provideAttributeSet() {
            return this.mAttrs;
        }
        
        public Context provideContext() {
            return this.mContext;
        }
    }
    
    public interface ViewCreator
    {
        ViewInstanceCreator createInstanceCreator(final ViewAttributeProvider p0);
    }
    
    public interface ViewInstanceCreator
    {
        NotificationShelf creatNotificationShelf();
        
        KeyguardClockSwitch createKeyguardClockSwitch();
        
        KeyguardMessageArea createKeyguardMessageArea();
        
        KeyguardSliceView createKeyguardSliceView();
        
        NotificationStackScrollLayout createNotificationStackScrollLayout();
        
        QSCustomizer createQSCustomizer();
        
        QSPanel createQSPanel();
        
        QSFooterImpl createQsFooter();
        
        QuickStatusBarHeader createQsHeader();
        
        QuickQSPanel createQuickQSPanel();
    }
}
