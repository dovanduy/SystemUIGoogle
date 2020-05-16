// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.qs;

import android.widget.TextView;
import android.content.res.ColorStateList;
import android.widget.ImageView;
import android.view.View$OnClickListener;
import android.widget.LinearLayout;
import android.app.Notification$Builder;
import com.android.settingslib.media.MediaDevice;
import android.app.Notification;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession$Token;
import android.view.View$OnLongClickListener;
import android.media.session.MediaController;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import androidx.lifecycle.Observer;
import android.view.View;
import com.android.systemui.util.SysuiLifecycle;
import com.android.systemui.R$layout;
import java.util.concurrent.Executor;
import com.android.systemui.statusbar.NotificationMediaManager;
import android.view.ViewGroup;
import android.content.Context;
import com.android.systemui.R$id;
import com.android.systemui.media.SeekBarViewModel;
import com.android.systemui.media.SeekBarObserver;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.media.MediaControlPanel;

public class QSMediaPlayer extends MediaControlPanel
{
    static final int[] QS_ACTION_IDS;
    private final DelayableExecutor mBackgroundExecutor;
    private final QSPanel mParent;
    private final SeekBarObserver mSeekBarObserver;
    private final SeekBarViewModel mSeekBarViewModel;
    
    static {
        QS_ACTION_IDS = new int[] { R$id.action0, R$id.action1, R$id.action2, R$id.action3, R$id.action4 };
    }
    
    public QSMediaPlayer(final Context context, final ViewGroup viewGroup, final NotificationMediaManager notificationMediaManager, final Executor executor, final DelayableExecutor mBackgroundExecutor) {
        super(context, viewGroup, notificationMediaManager, R$layout.qs_media_panel, QSMediaPlayer.QS_ACTION_IDS, executor, mBackgroundExecutor);
        this.mParent = (QSPanel)viewGroup;
        this.mBackgroundExecutor = mBackgroundExecutor;
        this.mSeekBarViewModel = new SeekBarViewModel(mBackgroundExecutor);
        this.mSeekBarObserver = new SeekBarObserver(this.getView());
        this.mSeekBarViewModel.getProgress().observe(SysuiLifecycle.viewAttachLifecycle((View)viewGroup), (Observer<? super SeekBarViewModel.Progress>)this.mSeekBarObserver);
        final SeekBar seekBar = (SeekBar)this.getView().findViewById(R$id.media_progress_bar);
        seekBar.setOnSeekBarChangeListener(this.mSeekBarViewModel.getSeekBarListener());
        seekBar.setOnTouchListener(this.mSeekBarViewModel.getSeekBarTouchListener());
    }
    
    @Override
    public void clearControls() {
        super.clearControls();
        super.mMediaNotifView.setOnLongClickListener((View$OnLongClickListener)new _$$Lambda$QSMediaPlayer$CptGc_dUDkkQaP1PXxJpHsf__lo(super.mMediaNotifView.findViewById(R$id.media_guts), super.mMediaNotifView.findViewById(R$id.qs_media_controls_options)));
    }
    
    public void setListening(final boolean listening) {
        this.mSeekBarViewModel.setListening(listening);
    }
    
    public void setMediaSession(final MediaSession$Token mediaSession$Token, final Icon icon, final int n, int n2, View viewById, final Notification notification, final MediaDevice mediaDevice) {
        final int[] qs_ACTION_IDS = QSMediaPlayer.QS_ACTION_IDS;
        super.setMediaSession(mediaSession$Token, icon, n, n2, notification.contentIntent, Notification$Builder.recoverBuilder(this.getContext(), notification).loadHeaderAppName(), mediaDevice);
        final LinearLayout linearLayout = (LinearLayout)viewById;
        n2 = 0;
        int i;
        while (true) {
            i = n2;
            if (n2 >= linearLayout.getChildCount() || (i = n2) >= qs_ACTION_IDS.length) {
                break;
            }
            final ImageButton imageButton = (ImageButton)super.mMediaNotifView.findViewById(qs_ACTION_IDS[n2]);
            final ImageButton imageButton2 = (ImageButton)linearLayout.findViewById(MediaControlPanel.NOTIF_ACTION_IDS[n2]);
            if (imageButton2 != null && imageButton2.getDrawable() != null && imageButton2.getVisibility() == 0) {
                imageButton.setImageDrawable(imageButton2.getDrawable().mutate());
                imageButton.setVisibility(0);
                imageButton.setOnClickListener((View$OnClickListener)new _$$Lambda$QSMediaPlayer$dK6y1faS6RL9_HNj59W_IpX5fk4(imageButton2));
            }
            else {
                imageButton.setVisibility(8);
            }
            ++n2;
        }
        while (i < qs_ACTION_IDS.length) {
            ((ImageButton)super.mMediaNotifView.findViewById(qs_ACTION_IDS[i])).setVisibility(8);
            ++i;
        }
        this.mBackgroundExecutor.execute(new _$$Lambda$QSMediaPlayer$VTYxXm6KxGii_iwI1K4Lk53fIic(this, new MediaController(this.getContext(), mediaSession$Token), n));
        viewById = super.mMediaNotifView.findViewById(R$id.media_guts);
        final View viewById2 = super.mMediaNotifView.findViewById(R$id.qs_media_controls_options);
        viewById2.setMinimumHeight(viewById.getHeight());
        viewById2.findViewById(R$id.remove).setOnClickListener((View$OnClickListener)new _$$Lambda$QSMediaPlayer$hfSXNSVx4IQyYBFdYgBK7ccu1S0(this));
        ((ImageView)viewById2.findViewById(R$id.remove_icon)).setImageTintList(ColorStateList.valueOf(n));
        ((TextView)viewById2.findViewById(R$id.remove_text)).setTextColor(n);
        final TextView textView = (TextView)viewById2.findViewById(R$id.cancel);
        textView.setTextColor(n);
        textView.setOnClickListener((View$OnClickListener)new _$$Lambda$QSMediaPlayer$auA4NrkQicQ4xsVxLVW3YUNUiKQ(viewById2, viewById));
        super.mMediaNotifView.setOnLongClickListener((View$OnLongClickListener)null);
        viewById2.setVisibility(8);
        viewById.setVisibility(0);
    }
}
