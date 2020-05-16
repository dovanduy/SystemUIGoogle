// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.view.View$OnClickListener;
import android.widget.LinearLayout;
import com.android.settingslib.media.MediaDevice;
import android.media.session.MediaController;
import android.app.PendingIntent;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession$Token;
import android.view.View;
import android.widget.ImageButton;
import com.android.systemui.R$layout;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.view.ViewGroup;
import android.content.Context;
import com.android.systemui.R$id;
import com.android.systemui.media.MediaControlPanel;

public class QuickQSMediaPlayer extends MediaControlPanel
{
    private static final int[] QQS_ACTION_IDS;
    
    static {
        QQS_ACTION_IDS = new int[] { R$id.action0, R$id.action1, R$id.action2 };
    }
    
    public QuickQSMediaPlayer(final Context context, final ViewGroup viewGroup, final NotificationMediaManager notificationMediaManager, final Executor executor, final Executor executor2) {
        super(context, viewGroup, notificationMediaManager, R$layout.qqs_media_panel, QuickQSMediaPlayer.QQS_ACTION_IDS, executor, executor2);
    }
    
    public void setMediaSession(final MediaSession$Token mediaSession$Token, final Icon icon, int i, int min, final View view, final int[] array, final PendingIntent pendingIntent) {
        final int[] qqs_ACTION_IDS = QuickQSMediaPlayer.QQS_ACTION_IDS;
        String packageName;
        if (this.getController() != null) {
            packageName = this.getController().getPackageName();
        }
        else {
            packageName = "";
        }
        final MediaController mediaController = new MediaController(this.getContext(), mediaSession$Token);
        final MediaSession$Token mediaSessionToken = this.getMediaSessionToken();
        final int n = 0;
        final boolean b = mediaSessionToken != null && mediaSessionToken.equals((Object)mediaSession$Token) && packageName.equals(mediaController.getPackageName());
        if (this.getController() != null && !b && !this.isPlaying(mediaController)) {
            return;
        }
        super.setMediaSession(mediaSession$Token, icon, i, min, pendingIntent, null, null);
        final LinearLayout linearLayout = (LinearLayout)view;
        i = n;
        if (array != null) {
            ImageButton imageButton;
            ImageButton imageButton2;
            for (min = Math.min(Math.min(array.length, linearLayout.getChildCount()), qqs_ACTION_IDS.length), i = 0; i < min; ++i) {
                imageButton = (ImageButton)super.mMediaNotifView.findViewById(qqs_ACTION_IDS[i]);
                imageButton2 = (ImageButton)linearLayout.findViewById(MediaControlPanel.NOTIF_ACTION_IDS[array[i]]);
                if (imageButton2 != null && imageButton2.getDrawable() != null && imageButton2.getVisibility() == 0) {
                    imageButton.setImageDrawable(imageButton2.getDrawable().mutate());
                    imageButton.setVisibility(0);
                    imageButton.setOnClickListener((View$OnClickListener)new _$$Lambda$QuickQSMediaPlayer$3Xmz_t47lWXuHcTnnVnCq3vExio(imageButton2));
                }
                else {
                    imageButton.setVisibility(8);
                }
            }
        }
        while (i < qqs_ACTION_IDS.length) {
            ((ImageButton)super.mMediaNotifView.findViewById(qqs_ACTION_IDS[i])).setVisibility(8);
            ++i;
        }
    }
}
