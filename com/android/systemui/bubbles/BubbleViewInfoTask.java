// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.bubbles;

import com.android.launcher3.icons.BitmapInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.service.notification.StatusBarNotification;
import android.content.pm.PackageManager$NameNotFoundException;
import android.util.Log;
import com.android.internal.graphics.ColorUtils;
import android.graphics.RectF;
import android.graphics.Matrix;
import android.util.PathParser;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.content.pm.ShortcutInfo;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.app.Person;
import android.app.Notification;
import android.app.Notification$MessagingStyle$Message;
import android.os.Parcelable;
import android.app.Notification$InboxStyle;
import android.app.Notification$MessagingStyle;
import android.app.Notification$MediaStyle;
import android.text.TextUtils;
import android.app.Notification$BigTextStyle;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import android.content.Context;
import java.lang.ref.WeakReference;
import android.os.AsyncTask;

public class BubbleViewInfoTask extends AsyncTask<Void, Void, BubbleViewInfo>
{
    private Bubble mBubble;
    private Callback mCallback;
    private WeakReference<Context> mContext;
    private BubbleIconFactory mIconFactory;
    private WeakReference<BubbleStackView> mStackView;
    
    BubbleViewInfoTask(final Bubble mBubble, final Context referent, final BubbleStackView referent2, final BubbleIconFactory mIconFactory, final Callback mCallback) {
        this.mBubble = mBubble;
        this.mContext = new WeakReference<Context>(referent);
        this.mStackView = new WeakReference<BubbleStackView>(referent2);
        this.mIconFactory = mIconFactory;
        this.mCallback = mCallback;
    }
    
    static Bubble.FlyoutMessage extractFlyoutMessage(final Context context, final NotificationEntry notificationEntry) {
        final Notification notification = notificationEntry.getSbn().getNotification();
        final Class notificationStyle = notification.getNotificationStyle();
        final Bubble.FlyoutMessage flyoutMessage = new Bubble.FlyoutMessage();
        flyoutMessage.isGroupChat = notification.extras.getBoolean("android.isGroupConversation");
        try {
            Label_0084: {
                if (!Notification$BigTextStyle.class.equals(notificationStyle)) {
                    break Label_0084;
                }
                CharSequence message = notification.extras.getCharSequence("android.bigText");
                Label_0077: {
                    if (!TextUtils.isEmpty(message)) {
                        break Label_0077;
                    }
                    try {
                        message = notification.extras.getCharSequence("android.text");
                        flyoutMessage.message = message;
                        return flyoutMessage;
                        Label_0268: {
                            flyoutMessage.message = notification.extras.getCharSequence("android.text");
                        }
                        return flyoutMessage;
                        // iftrue(Label_0288:, charSequenceArray == null || charSequenceArray.length <= 0)
                        // iftrue(Label_0268:, !Notification$MediaStyle.class.equals((Object)notificationStyle))
                        // iftrue(Label_0141:, senderPerson == null)
                        // iftrue(Label_0215:, senderPerson == null || senderPerson.getIcon() == null)
                        // iftrue(Label_0217:, !Notification$MessagingStyle.class.equals((Object)notificationStyle))
                        // iftrue(Label_0257:, !Notification$InboxStyle.class.equals((Object)notificationStyle))
                        // iftrue(Label_0288:, latestIncomingMessage == null)
                        Person senderPerson = null;
                    Block_10:
                        while (true) {
                            Block_6: {
                            Label_0143_Outer:
                                while (true) {
                                    final CharSequence[] charSequenceArray = notification.extras.getCharSequenceArray("android.textLines");
                                    Block_8: {
                                        Block_14: {
                                            break Block_14;
                                            Label_0257:
                                            return flyoutMessage;
                                            Label_0215:
                                            return flyoutMessage;
                                            final Notification$MessagingStyle$Message latestIncomingMessage;
                                            flyoutMessage.message = latestIncomingMessage.getText();
                                            senderPerson = latestIncomingMessage.getSenderPerson();
                                            break Block_8;
                                        }
                                        flyoutMessage.message = charSequenceArray[charSequenceArray.length - 1];
                                        return flyoutMessage;
                                    }
                                    CharSequence name = senderPerson.getName();
                                    while (true) {
                                        break Label_0143;
                                        Label_0203:
                                        flyoutMessage.senderAvatar = senderPerson.getIcon().loadDrawable(context);
                                        return flyoutMessage;
                                        flyoutMessage.senderName = name;
                                        flyoutMessage.senderAvatar = null;
                                        break Block_10;
                                        break Block_6;
                                        Label_0141:
                                        name = null;
                                        continue;
                                    }
                                    Label_0217:
                                    continue Label_0143_Outer;
                                }
                            }
                            final Notification$MessagingStyle$Message latestIncomingMessage = Notification$MessagingStyle.findLatestIncomingMessage(Notification$MessagingStyle$Message.getMessagesFromBundleArray((Parcelable[])notification.extras.get("android.messages")));
                            continue;
                        }
                        // iftrue(Label_0203:, senderPerson.getIcon().getType() != 4 && senderPerson.getIcon().getType() != 6)
                        context.grantUriPermission(context.getPackageName(), senderPerson.getIcon().getUri(), 1);
                    }
                    catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (ClassCastException ex2) {}
        catch (NullPointerException ex3) {}
        catch (ArrayIndexOutOfBoundsException ex4) {}
        Label_0288: {
            return flyoutMessage;
        }
    }
    
    protected BubbleViewInfo doInBackground(final Void... array) {
        return BubbleViewInfo.populate(this.mContext.get(), this.mStackView.get(), this.mIconFactory, this.mBubble);
    }
    
    protected void onPostExecute(final BubbleViewInfo viewInfo) {
        if (viewInfo != null) {
            this.mBubble.setViewInfo(viewInfo);
            if (this.mCallback != null && !this.isCancelled()) {
                this.mCallback.onBubbleViewsReady(this.mBubble);
            }
        }
    }
    
    static class BubbleViewInfo
    {
        String appName;
        Bitmap badgedBubbleImage;
        int dotColor;
        Path dotPath;
        BubbleExpandedView expandedView;
        Bubble.FlyoutMessage flyoutMessage;
        BadgedImageView imageView;
        ShortcutInfo shortcutInfo;
        
        static BubbleViewInfo populate(final Context context, final BubbleStackView stackView, final BubbleIconFactory bubbleIconFactory, final Bubble bubble) {
            final BubbleViewInfo bubbleViewInfo = new BubbleViewInfo();
            if (!bubble.isInflated()) {
                final LayoutInflater from = LayoutInflater.from(context);
                bubbleViewInfo.imageView = (BadgedImageView)from.inflate(R$layout.bubble_view, (ViewGroup)stackView, false);
                (bubbleViewInfo.expandedView = (BubbleExpandedView)from.inflate(R$layout.bubble_expanded_view, (ViewGroup)stackView, false)).setStackView(stackView);
            }
            final StatusBarNotification sbn = bubble.getEntry().getSbn();
            Object o = sbn.getPackageName();
            final String shortcutId = bubble.getEntry().getBubbleMetadata().getShortcutId();
            if (shortcutId != null) {
                bubbleViewInfo.shortcutInfo = BubbleExperimentConfig.getShortcutInfo(context, (String)o, sbn.getUser(), shortcutId);
            }
            else {
                final String shortcutId2 = sbn.getNotification().getShortcutId();
                if (BubbleExperimentConfig.useShortcutInfoToBubble(context) && shortcutId2 != null) {
                    bubbleViewInfo.shortcutInfo = BubbleExperimentConfig.getShortcutInfo(context, (String)o, sbn.getUser(), shortcutId2);
                }
            }
            final PackageManager packageManager = context.getPackageManager();
            try {
                final ApplicationInfo applicationInfo = packageManager.getApplicationInfo((String)o, 795136);
                if (applicationInfo != null) {
                    bubbleViewInfo.appName = String.valueOf(packageManager.getApplicationLabel(applicationInfo));
                }
                Object applicationIcon = packageManager.getApplicationIcon((String)o);
                final Drawable userBadgedIcon = packageManager.getUserBadgedIcon((Drawable)applicationIcon, sbn.getUser());
                o = bubbleIconFactory.getBubbleDrawable(context, bubbleViewInfo.shortcutInfo, bubble.getEntry().getBubbleMetadata());
                if (o != null) {
                    applicationIcon = o;
                }
                final BitmapInfo badgeBitmap = bubbleIconFactory.getBadgeBitmap(userBadgedIcon);
                bubbleViewInfo.badgedBubbleImage = bubbleIconFactory.getBubbleBitmap((Drawable)applicationIcon, badgeBitmap).icon;
                o = PathParser.createPathFromPathData(context.getResources().getString(17039911));
                final Matrix matrix = new Matrix();
                final float scale = bubbleIconFactory.getNormalizer().getScale((Drawable)applicationIcon, null, null, null);
                matrix.setScale(scale, scale, 50.0f, 50.0f);
                ((Path)o).transform(matrix);
                bubbleViewInfo.dotPath = (Path)o;
                bubbleViewInfo.dotColor = ColorUtils.blendARGB(badgeBitmap.color, -1, 0.54f);
                bubbleViewInfo.flyoutMessage = BubbleViewInfoTask.extractFlyoutMessage(context, bubble.getEntry());
                return bubbleViewInfo;
            }
            catch (PackageManager$NameNotFoundException ex) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Unable to find package: ");
                sb.append((String)o);
                Log.w("Bubbles", sb.toString());
                return null;
            }
        }
    }
    
    public interface Callback
    {
        void onBubbleViewsReady(final Bubble p0);
    }
}
