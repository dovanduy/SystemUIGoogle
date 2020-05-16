// 
// Decompiled by Procyon v0.5.36
// 

package androidx.core.app;

import androidx.core.content.ContextCompat;
import android.os.Bundle;
import java.util.Iterator;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import android.content.ComponentName;
import android.app.Activity;
import android.content.Context;
import java.util.ArrayList;
import android.content.Intent;

public final class TaskStackBuilder implements Iterable<Intent>
{
    private final ArrayList<Intent> mIntents;
    private final Context mSourceContext;
    
    private TaskStackBuilder(final Context mSourceContext) {
        this.mIntents = new ArrayList<Intent>();
        this.mSourceContext = mSourceContext;
    }
    
    public static TaskStackBuilder create(final Context context) {
        return new TaskStackBuilder(context);
    }
    
    public TaskStackBuilder addNextIntent(final Intent e) {
        this.mIntents.add(e);
        return this;
    }
    
    public TaskStackBuilder addParentStack(final Activity activity) {
        Intent supportParentActivityIntent;
        if (activity instanceof SupportParentable) {
            supportParentActivityIntent = ((SupportParentable)activity).getSupportParentActivityIntent();
        }
        else {
            supportParentActivityIntent = null;
        }
        Intent parentActivityIntent = supportParentActivityIntent;
        if (supportParentActivityIntent == null) {
            parentActivityIntent = NavUtils.getParentActivityIntent(activity);
        }
        if (parentActivityIntent != null) {
            ComponentName componentName;
            if ((componentName = parentActivityIntent.getComponent()) == null) {
                componentName = parentActivityIntent.resolveActivity(this.mSourceContext.getPackageManager());
            }
            this.addParentStack(componentName);
            this.addNextIntent(parentActivityIntent);
        }
        return this;
    }
    
    public TaskStackBuilder addParentStack(final ComponentName componentName) {
        final int size = this.mIntents.size();
        try {
            for (Intent element = NavUtils.getParentActivityIntent(this.mSourceContext, componentName); element != null; element = NavUtils.getParentActivityIntent(this.mSourceContext, element.getComponent())) {
                this.mIntents.add(size, element);
            }
            return this;
        }
        catch (PackageManager$NameNotFoundException cause) {
            Log.e("TaskStackBuilder", "Bad ComponentName while traversing activity parent metadata");
            throw new IllegalArgumentException((Throwable)cause);
        }
    }
    
    @Deprecated
    @Override
    public Iterator<Intent> iterator() {
        return this.mIntents.iterator();
    }
    
    public void startActivities() {
        this.startActivities(null);
    }
    
    public void startActivities(final Bundle bundle) {
        if (!this.mIntents.isEmpty()) {
            final ArrayList<Intent> mIntents = this.mIntents;
            final Intent[] array = mIntents.toArray(new Intent[mIntents.size()]);
            array[0] = new Intent(array[0]).addFlags(268484608);
            if (!ContextCompat.startActivities(this.mSourceContext, array, bundle)) {
                final Intent intent = new Intent(array[array.length - 1]);
                intent.addFlags(268435456);
                this.mSourceContext.startActivity(intent);
            }
            return;
        }
        throw new IllegalStateException("No intents added to TaskStackBuilder; cannot startActivities");
    }
    
    public interface SupportParentable
    {
        Intent getSupportParentActivityIntent();
    }
}
