// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.widget;

import android.view.ActionMode;
import android.view.MenuItem;
import java.lang.reflect.InvocationTargetException;
import android.view.Menu;
import android.text.Editable;
import java.util.Iterator;
import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.PackageManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.Intent;
import java.lang.reflect.Method;
import android.view.ActionMode$Callback;
import android.graphics.Paint$FontMetricsInt;
import android.graphics.PorterDuff$Mode;
import androidx.core.util.Preconditions;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.text.TextPaint;
import androidx.core.text.PrecomputedTextCompat;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build$VERSION;
import android.text.TextDirectionHeuristics;
import android.text.method.PasswordTransformationMethod;
import android.text.TextDirectionHeuristic;
import android.widget.TextView;

public final class TextViewCompat
{
    public static int getFirstBaselineToTopHeight(final TextView textView) {
        return textView.getPaddingTop() - textView.getPaint().getFontMetricsInt().top;
    }
    
    public static int getLastBaselineToBottomHeight(final TextView textView) {
        return textView.getPaddingBottom() + textView.getPaint().getFontMetricsInt().bottom;
    }
    
    private static TextDirectionHeuristic getTextDirectionHeuristic(final TextView textView) {
        if (textView.getTransformationMethod() instanceof PasswordTransformationMethod) {
            return TextDirectionHeuristics.LTR;
        }
        final int sdk_INT = Build$VERSION.SDK_INT;
        boolean b = false;
        if (sdk_INT >= 28 && (textView.getInputType() & 0xF) == 0x3) {
            final byte directionality = Character.getDirectionality(DecimalFormatSymbols.getInstance(textView.getTextLocale()).getDigitStrings()[0].codePointAt(0));
            if (directionality != 1 && directionality != 2) {
                return TextDirectionHeuristics.LTR;
            }
            return TextDirectionHeuristics.RTL;
        }
        else {
            if (textView.getLayoutDirection() == 1) {
                b = true;
            }
            switch (textView.getTextDirection()) {
                default: {
                    TextDirectionHeuristic textDirectionHeuristic;
                    if (b) {
                        textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_RTL;
                    }
                    else {
                        textDirectionHeuristic = TextDirectionHeuristics.FIRSTSTRONG_LTR;
                    }
                    return textDirectionHeuristic;
                }
                case 7: {
                    return TextDirectionHeuristics.FIRSTSTRONG_RTL;
                }
                case 6: {
                    return TextDirectionHeuristics.FIRSTSTRONG_LTR;
                }
                case 5: {
                    return TextDirectionHeuristics.LOCALE;
                }
                case 4: {
                    return TextDirectionHeuristics.RTL;
                }
                case 3: {
                    return TextDirectionHeuristics.LTR;
                }
                case 2: {
                    return TextDirectionHeuristics.ANYRTL_LTR;
                }
            }
        }
    }
    
    public static PrecomputedTextCompat.Params getTextMetricsParams(final TextView textView) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 28) {
            return new PrecomputedTextCompat.Params(textView.getTextMetricsParams());
        }
        final PrecomputedTextCompat.Params.Builder builder = new PrecomputedTextCompat.Params.Builder(new TextPaint((Paint)textView.getPaint()));
        if (sdk_INT >= 23) {
            builder.setBreakStrategy(textView.getBreakStrategy());
            builder.setHyphenationFrequency(textView.getHyphenationFrequency());
        }
        if (sdk_INT >= 18) {
            builder.setTextDirection(getTextDirectionHeuristic(textView));
        }
        return builder.build();
    }
    
    public static void setCompoundDrawableTintList(final TextView textView, final ColorStateList list) {
        Preconditions.checkNotNull(textView);
        if (Build$VERSION.SDK_INT >= 24) {
            textView.setCompoundDrawableTintList(list);
        }
        else if (textView instanceof TintableCompoundDrawablesView) {
            ((TintableCompoundDrawablesView)textView).setSupportCompoundDrawablesTintList(list);
        }
    }
    
    public static void setCompoundDrawableTintMode(final TextView textView, final PorterDuff$Mode porterDuff$Mode) {
        Preconditions.checkNotNull(textView);
        if (Build$VERSION.SDK_INT >= 24) {
            textView.setCompoundDrawableTintMode(porterDuff$Mode);
        }
        else if (textView instanceof TintableCompoundDrawablesView) {
            ((TintableCompoundDrawablesView)textView).setSupportCompoundDrawablesTintMode(porterDuff$Mode);
        }
    }
    
    public static void setFirstBaselineToTopHeight(final TextView textView, final int firstBaselineToTopHeight) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        Preconditions.checkArgumentNonnegative(firstBaselineToTopHeight);
        if (sdk_INT >= 28) {
            textView.setFirstBaselineToTopHeight(firstBaselineToTopHeight);
            return;
        }
        final Paint$FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        int a;
        if (sdk_INT >= 16 && !textView.getIncludeFontPadding()) {
            a = fontMetricsInt.ascent;
        }
        else {
            a = fontMetricsInt.top;
        }
        if (firstBaselineToTopHeight > Math.abs(a)) {
            textView.setPadding(textView.getPaddingLeft(), firstBaselineToTopHeight + a, textView.getPaddingRight(), textView.getPaddingBottom());
        }
    }
    
    public static void setLastBaselineToBottomHeight(final TextView textView, final int n) {
        Preconditions.checkArgumentNonnegative(n);
        final Paint$FontMetricsInt fontMetricsInt = textView.getPaint().getFontMetricsInt();
        int a;
        if (Build$VERSION.SDK_INT >= 16 && !textView.getIncludeFontPadding()) {
            a = fontMetricsInt.descent;
        }
        else {
            a = fontMetricsInt.bottom;
        }
        if (n > Math.abs(a)) {
            textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), n - a);
        }
    }
    
    public static void setLineHeight(final TextView textView, final int n) {
        Preconditions.checkArgumentNonnegative(n);
        final int fontMetricsInt = textView.getPaint().getFontMetricsInt((Paint$FontMetricsInt)null);
        if (n != fontMetricsInt) {
            textView.setLineSpacing((float)(n - fontMetricsInt), 1.0f);
        }
    }
    
    public static void setPrecomputedText(final TextView textView, final PrecomputedTextCompat text) {
        if (Build$VERSION.SDK_INT >= 29) {
            textView.setText((CharSequence)text.getPrecomputedText());
        }
        else {
            if (!getTextMetricsParams(textView).equalsWithoutTextDirection(text.getParams())) {
                throw new IllegalArgumentException("Given text can not be applied to TextView.");
            }
            textView.setText((CharSequence)text);
        }
    }
    
    public static void setTextAppearance(final TextView textView, final int textAppearance) {
        if (Build$VERSION.SDK_INT >= 23) {
            textView.setTextAppearance(textAppearance);
        }
        else {
            textView.setTextAppearance(textView.getContext(), textAppearance);
        }
    }
    
    public static ActionMode$Callback wrapCustomSelectionActionModeCallback(final TextView textView, final ActionMode$Callback actionMode$Callback) {
        final int sdk_INT = Build$VERSION.SDK_INT;
        if (sdk_INT >= 26 && sdk_INT <= 27 && !(actionMode$Callback instanceof OreoCallback)) {
            return (ActionMode$Callback)new OreoCallback(actionMode$Callback, textView);
        }
        return actionMode$Callback;
    }
    
    private static class OreoCallback implements ActionMode$Callback
    {
        private final ActionMode$Callback mCallback;
        private boolean mCanUseMenuBuilderReferences;
        private boolean mInitializedMenuBuilderReferences;
        private Class<?> mMenuBuilderClass;
        private Method mMenuBuilderRemoveItemAtMethod;
        private final TextView mTextView;
        
        OreoCallback(final ActionMode$Callback mCallback, final TextView mTextView) {
            this.mCallback = mCallback;
            this.mTextView = mTextView;
            this.mInitializedMenuBuilderReferences = false;
        }
        
        private Intent createProcessTextIntent() {
            return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
        }
        
        private Intent createProcessTextIntentForResolveInfo(final ResolveInfo resolveInfo, final TextView textView) {
            final Intent putExtra = this.createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", this.isEditable(textView) ^ true);
            final ActivityInfo activityInfo = resolveInfo.activityInfo;
            return putExtra.setClassName(activityInfo.packageName, activityInfo.name);
        }
        
        private List<ResolveInfo> getSupportedActivities(final Context context, final PackageManager packageManager) {
            final ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>();
            if (!(context instanceof Activity)) {
                return list;
            }
            for (final ResolveInfo resolveInfo : packageManager.queryIntentActivities(this.createProcessTextIntent(), 0)) {
                if (this.isSupportedActivity(resolveInfo, context)) {
                    list.add(resolveInfo);
                }
            }
            return list;
        }
        
        private boolean isEditable(final TextView textView) {
            return textView instanceof Editable && textView.onCheckIsTextEditor() && textView.isEnabled();
        }
        
        private boolean isSupportedActivity(final ResolveInfo resolveInfo, final Context context) {
            final boolean equals = context.getPackageName().equals(resolveInfo.activityInfo.packageName);
            final boolean b = true;
            if (equals) {
                return true;
            }
            final ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (!activityInfo.exported) {
                return false;
            }
            final String permission = activityInfo.permission;
            boolean b2 = b;
            if (permission != null) {
                b2 = (context.checkSelfPermission(permission) == 0 && b);
            }
            return b2;
        }
        
        private void recomputeProcessTextMenuItems(final Menu obj) {
            final Context context = this.mTextView.getContext();
            final PackageManager packageManager = context.getPackageManager();
            if (!this.mInitializedMenuBuilderReferences) {
                this.mInitializedMenuBuilderReferences = true;
                try {
                    final Class<?> forName = Class.forName("com.android.internal.view.menu.MenuBuilder");
                    this.mMenuBuilderClass = forName;
                    this.mMenuBuilderRemoveItemAtMethod = forName.getDeclaredMethod("removeItemAt", Integer.TYPE);
                    this.mCanUseMenuBuilderReferences = true;
                }
                catch (ClassNotFoundException | NoSuchMethodException ex) {
                    this.mMenuBuilderClass = null;
                    this.mMenuBuilderRemoveItemAtMethod = null;
                    this.mCanUseMenuBuilderReferences = false;
                }
            }
            try {
                Label_0132: {
                    if (this.mCanUseMenuBuilderReferences && this.mMenuBuilderClass.isInstance(obj)) {
                        final Method method = this.mMenuBuilderRemoveItemAtMethod;
                        break Label_0132;
                    }
                    try {
                        final Method method = obj.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                        for (int i = obj.size() - 1; i >= 0; --i) {
                            final MenuItem item = obj.getItem(i);
                            if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                                method.invoke(obj, i);
                            }
                        }
                        final List<ResolveInfo> supportedActivities = this.getSupportedActivities(context, packageManager);
                        for (int j = 0; j < supportedActivities.size(); ++j) {
                            final ResolveInfo resolveInfo = supportedActivities.get(j);
                            obj.add(0, 0, j + 100, resolveInfo.loadLabel(packageManager)).setIntent(this.createProcessTextIntentForResolveInfo(resolveInfo, this.mTextView)).setShowAsAction(1);
                        }
                    }
                    catch (IllegalAccessException ex2) {}
                }
            }
            catch (NoSuchMethodException ex3) {}
            catch (IllegalAccessException ex4) {}
            catch (InvocationTargetException ex5) {}
        }
        
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return this.mCallback.onActionItemClicked(actionMode, menuItem);
        }
        
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return this.mCallback.onCreateActionMode(actionMode, menu);
        }
        
        public void onDestroyActionMode(final ActionMode actionMode) {
            this.mCallback.onDestroyActionMode(actionMode);
        }
        
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            this.recomputeProcessTextMenuItems(menu);
            return this.mCallback.onPrepareActionMode(actionMode, menu);
        }
    }
}
