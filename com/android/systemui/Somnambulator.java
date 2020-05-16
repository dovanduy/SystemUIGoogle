// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui;

import android.service.dreams.Sandman;
import android.os.Parcelable;
import android.content.Intent$ShortcutIconResource;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;

public class Somnambulator extends Activity
{
    public void onStart() {
        super.onStart();
        final Intent intent = this.getIntent();
        if ("android.intent.action.CREATE_SHORTCUT".equals(intent.getAction())) {
            final Intent intent2 = new Intent((Context)this, (Class)Somnambulator.class);
            intent2.setFlags(276824064);
            final Intent intent3 = new Intent();
            intent3.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", (Parcelable)Intent$ShortcutIconResource.fromContext((Context)this, R$mipmap.ic_launcher_dreams));
            intent3.putExtra("android.intent.extra.shortcut.INTENT", (Parcelable)intent2);
            intent3.putExtra("android.intent.extra.shortcut.NAME", this.getString(R$string.start_dreams));
            this.setResult(-1, intent3);
        }
        else if (intent.hasCategory("android.intent.category.DESK_DOCK")) {
            Sandman.startDreamWhenDockedIfAppropriate((Context)this);
        }
        else {
            Sandman.startDreamByUserRequest((Context)this);
        }
        this.finish();
    }
}
