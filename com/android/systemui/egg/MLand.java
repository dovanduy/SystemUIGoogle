// 
// Decompiled by Procyon v0.5.36
// 

package com.android.systemui.egg;

import android.graphics.ColorFilter;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Path;
import android.graphics.Outline;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.content.res.Resources;
import com.android.systemui.R$id;
import android.animation.LayoutTransition;
import android.animation.TimeAnimator$TimeListener;
import android.graphics.Color;
import android.graphics.PorterDuff$Mode;
import com.android.systemui.R$dimen;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable$Orientation;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Iterator;
import android.graphics.Canvas;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout$LayoutParams;
import android.view.InputDevice;
import android.view.ViewGroup$LayoutParams;
import android.view.ViewGroup$MarginLayoutParams;
import com.android.systemui.R$layout;
import android.view.LayoutInflater;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import android.graphics.Paint$Style;
import android.media.AudioAttributes$Builder;
import android.util.AttributeSet;
import android.content.Context;
import com.android.systemui.R$drawable;
import android.graphics.Rect;
import android.util.Log;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Paint;
import java.util.ArrayList;
import android.media.AudioManager;
import android.media.AudioAttributes;
import android.animation.TimeAnimator;
import android.widget.FrameLayout;

public class MLand extends FrameLayout
{
    static final int[] ANTENNAE;
    static final int[] CACTI;
    public static final boolean DEBUG;
    public static final boolean DEBUG_IDDQD;
    static final int[] EYES;
    static final int[] MOUNTAINS;
    static final int[] MOUTHS;
    private static Params PARAMS;
    private static final int[][] SKIES;
    private static float dp;
    private float dt;
    private TimeAnimator mAnim;
    private boolean mAnimating;
    private final AudioAttributes mAudioAttrs;
    private AudioManager mAudioManager;
    private int mCountdown;
    private int mCurrentPipeId;
    private boolean mFlipped;
    private boolean mFrozen;
    private ArrayList<Integer> mGameControllers;
    private int mHeight;
    private float mLastPipeTime;
    private ArrayList<Obstacle> mObstaclesInPlay;
    private Paint mPlayerTracePaint;
    private ArrayList<Player> mPlayers;
    private boolean mPlaying;
    private int mScene;
    private ViewGroup mScoreFields;
    private View mSplash;
    private int mTaps;
    private int mTimeOfDay;
    private Paint mTouchPaint;
    private Vibrator mVibrator;
    private int mWidth;
    private float t;
    
    static {
        DEBUG = Log.isLoggable("MLand", 3);
        DEBUG_IDDQD = Log.isLoggable("MLand.iddqd", 3);
        SKIES = new int[][] { { -4144897, -6250241 }, { -16777200, -16777216 }, { -16777152, -16777200 }, { -6258656, -14663552 } };
        MLand.dp = 1.0f;
        new Rect();
        ANTENNAE = new int[] { R$drawable.mm_antennae, R$drawable.mm_antennae2 };
        EYES = new int[] { R$drawable.mm_eyes, R$drawable.mm_eyes2 };
        MOUTHS = new int[] { R$drawable.mm_mouth1, R$drawable.mm_mouth2, R$drawable.mm_mouth3, R$drawable.mm_mouth4 };
        CACTI = new int[] { R$drawable.cactus1, R$drawable.cactus2, R$drawable.cactus3 };
        MOUNTAINS = new int[] { R$drawable.mountain1, R$drawable.mountain2, R$drawable.mountain3 };
    }
    
    public MLand(final Context context) {
        this(context, null);
    }
    
    public MLand(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public MLand(final Context context, final AttributeSet set, final int n) {
        super(context, set, n);
        this.mAudioAttrs = new AudioAttributes$Builder().setUsage(14).build();
        this.mPlayers = new ArrayList<Player>();
        this.mObstaclesInPlay = new ArrayList<Obstacle>();
        this.mCountdown = 0;
        this.mGameControllers = new ArrayList<Integer>();
        this.mVibrator = (Vibrator)context.getSystemService("vibrator");
        this.mAudioManager = (AudioManager)context.getSystemService("audio");
        this.setFocusable(true);
        MLand.PARAMS = new Params(this.getResources());
        this.mTimeOfDay = irand(0, MLand.SKIES.length - 1);
        this.mScene = irand(0, 3);
        (this.mTouchPaint = new Paint(1)).setColor(-2130706433);
        this.mTouchPaint.setStyle(Paint$Style.FILL);
        (this.mPlayerTracePaint = new Paint(1)).setColor(-2130706433);
        this.mPlayerTracePaint.setStyle(Paint$Style.STROKE);
        this.mPlayerTracePaint.setStrokeWidth(MLand.dp * 2.0f);
        this.setLayoutDirection(0);
        this.setupPlayers(1);
        MetricsLogger.count(this.getContext(), "egg_mland_create", 1);
    }
    
    public static void L(String format, final Object... args) {
        if (MLand.DEBUG) {
            if (args.length != 0) {
                format = String.format(format, args);
            }
            Log.d("MLand", format);
        }
    }
    
    private int addPlayerInternal(final Player e) {
        this.mPlayers.add(e);
        this.realignPlayers();
        final TextView scoreField = (TextView)LayoutInflater.from(this.getContext()).inflate(R$layout.mland_scorefield, (ViewGroup)null);
        final ViewGroup mScoreFields = this.mScoreFields;
        if (mScoreFields != null) {
            mScoreFields.addView((View)scoreField, (ViewGroup$LayoutParams)new ViewGroup$MarginLayoutParams(-2, -1));
        }
        e.setScoreField(scoreField);
        return this.mPlayers.size() - 1;
    }
    
    public static final float clamp(final float n) {
        float n2;
        if (n < 0.0f) {
            n2 = 0.0f;
        }
        else {
            n2 = n;
            if (n > 1.0f) {
                n2 = 1.0f;
            }
        }
        return n2;
    }
    
    private void clearPlayers() {
        while (this.mPlayers.size() > 0) {
            this.removePlayerInternal(this.mPlayers.get(0));
        }
    }
    
    public static final float frand() {
        return (float)Math.random();
    }
    
    public static final float frand(final float n, final float n2) {
        return lerp(frand(), n, n2);
    }
    
    public static final int irand(final int n, final int n2) {
        return Math.round(frand((float)n, (float)n2));
    }
    
    public static boolean isGamePad(final InputDevice inputDevice) {
        final int sources = inputDevice.getSources();
        return (sources & 0x401) == 0x401 || (sources & 0x1000010) == 0x1000010;
    }
    
    public static final float lerp(final float n, final float n2, final float n3) {
        return (n3 - n2) * n + n2;
    }
    
    private static float luma(final int n) {
        return (0xFF0000 & n) * 0.2126f / 1.671168E7f + (0xFF00 & n) * 0.7152f / 65280.0f + (n & 0xFF) * 0.0722f / 255.0f;
    }
    
    public static int pick(final int[] array) {
        return array[irand(0, array.length - 1)];
    }
    
    private void poke(final int n) {
        this.poke(n, -1.0f, -1.0f);
    }
    
    private void poke(final int i, final float n, final float n2) {
        L("poke(%d)", i);
        if (this.mFrozen) {
            return;
        }
        if (!this.mAnimating) {
            this.reset();
        }
        if (!this.mPlaying) {
            this.start(true);
        }
        else {
            final Player player = this.getPlayer(i);
            if (player == null) {
                return;
            }
            player.boost(n, n2);
            ++this.mTaps;
            if (MLand.DEBUG) {
                player.dv *= 0.5f;
                player.animate().setDuration(400L);
            }
        }
    }
    
    private void realignPlayers() {
        final int size = this.mPlayers.size();
        float x = (float)((this.mWidth - (size - 1) * MLand.PARAMS.PLAYER_SIZE) / 2);
        for (int i = 0; i < size; ++i) {
            this.mPlayers.get(i).setX(x);
            x += MLand.PARAMS.PLAYER_SIZE;
        }
    }
    
    private void removePlayerInternal(final Player o) {
        if (this.mPlayers.remove(o)) {
            this.removeView((View)o);
            this.mScoreFields.removeView((View)o.mScoreField);
            this.realignPlayers();
        }
    }
    
    public static final float rlerp(final float n, final float n2, final float n3) {
        return (n - n2) / (n3 - n2);
    }
    
    private void step(long n, final long n2) {
        final float t = n / 1000.0f;
        this.t = t;
        final float dt = n2 / 1000.0f;
        this.dt = dt;
        if (MLand.DEBUG) {
            this.t = t * 0.5f;
            this.dt = dt * 0.5f;
        }
        int childCount;
        int i;
        for (childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final View child = this.getChildAt(i);
            if (child instanceof GameView) {
                ((GameView)child).step(n, n2, this.t, this.dt);
            }
        }
        if (this.mPlaying) {
            int j;
            int n3;
            int n4;
            for (n3 = (j = 0); j < this.mPlayers.size(); ++j, n3 = n4) {
                final Player player = this.getPlayer(j);
                if (player.mAlive) {
                    if (player.below(this.mHeight)) {
                        if (MLand.DEBUG_IDDQD) {
                            this.poke(j);
                            this.unpoke(j);
                        }
                        else {
                            L("player %d hit the floor", j);
                            this.thump(j, 80L);
                            player.die();
                        }
                    }
                    int size = this.mObstaclesInPlay.size();
                    int a = 0;
                    while (true) {
                        final int index = size - 1;
                        if (size <= 0) {
                            break;
                        }
                        final Obstacle obstacle = this.mObstaclesInPlay.get(index);
                        int max;
                        if (obstacle.intersects(player) && !MLand.DEBUG_IDDQD) {
                            L("player hit an obstacle", new Object[0]);
                            this.thump(j, 80L);
                            player.die();
                            max = a;
                        }
                        else {
                            max = a;
                            if (obstacle.cleared(player)) {
                                max = a;
                                if (obstacle instanceof Stem) {
                                    max = Math.max(a, ((Stem)obstacle).id);
                                }
                            }
                        }
                        size = index;
                        a = max;
                    }
                    if (a > player.mScore) {
                        player.addScore(1);
                    }
                }
                n4 = n3;
                if (player.mAlive) {
                    n4 = n3 + 1;
                }
            }
            i = j;
            if (n3 == 0) {
                this.stop();
                MetricsLogger.count(this.getContext(), "egg_mland_taps", this.mTaps);
                this.mTaps = 0;
                final int size2 = this.mPlayers.size();
                int index2 = 0;
                while (true) {
                    i = j;
                    if (index2 >= size2) {
                        break;
                    }
                    MetricsLogger.histogram(this.getContext(), "egg_mland_score", this.mPlayers.get(index2).getScore());
                    ++index2;
                }
            }
        }
        while (true) {
            final int n5 = i - 1;
            if (i <= 0) {
                break;
            }
            final View child2 = this.getChildAt(n5);
            if (child2 instanceof Obstacle) {
                if (child2.getTranslationX() + child2.getWidth() < 0.0f) {
                    this.removeViewAt(n5);
                    this.mObstaclesInPlay.remove(child2);
                }
            }
            else if (child2 instanceof Scenery && child2.getTranslationX() + ((Scenery)child2).w < 0.0f) {
                child2.setTranslationX((float)this.getWidth());
            }
            i = n5;
        }
        if (this.mPlaying) {
            final float t2 = this.t;
            if (t2 - this.mLastPipeTime > MLand.PARAMS.OBSTACLE_PERIOD) {
                this.mLastPipeTime = t2;
                ++this.mCurrentPipeId;
                final float frand = frand();
                final int mHeight = this.mHeight;
                final Params params = MLand.PARAMS;
                final int obstacle_MIN = params.OBSTACLE_MIN;
                final int n6 = (int)(frand * (mHeight - obstacle_MIN * 2 - params.OBSTACLE_GAP)) + obstacle_MIN;
                final int obstacle_WIDTH = params.OBSTACLE_WIDTH;
                final int n7 = (obstacle_WIDTH - params.OBSTACLE_STEM_WIDTH) / 2;
                final int n8 = obstacle_WIDTH / 2;
                final int irand = irand(0, 250);
                final Stem e = new Stem(this.getContext(), (float)(n6 - n8), false);
                this.addView((View)e, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(MLand.PARAMS.OBSTACLE_STEM_WIDTH, (int)((Obstacle)e).h, 51));
                e.setTranslationX((float)(this.mWidth + n7));
                final float n9 = -((Obstacle)e).h;
                final float n10 = (float)n8;
                e.setTranslationY(n9 - n10);
                e.setTranslationZ(MLand.PARAMS.OBSTACLE_Z * 0.75f);
                final ViewPropertyAnimator translationY = e.animate().translationY(0.0f);
                n = irand;
                translationY.setStartDelay(n).setDuration(250L);
                this.mObstaclesInPlay.add((Obstacle)e);
                final Pop e2 = new Pop(this.getContext(), (float)MLand.PARAMS.OBSTACLE_WIDTH);
                final int obstacle_WIDTH2 = MLand.PARAMS.OBSTACLE_WIDTH;
                this.addView((View)e2, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(obstacle_WIDTH2, obstacle_WIDTH2, 51));
                e2.setTranslationX((float)this.mWidth);
                e2.setTranslationY((float)(-MLand.PARAMS.OBSTACLE_WIDTH));
                e2.setTranslationZ(MLand.PARAMS.OBSTACLE_Z);
                e2.setScaleX(0.25f);
                e2.setScaleY(-0.25f);
                e2.animate().translationY(((Obstacle)e).h - n7).scaleX(1.0f).scaleY(-1.0f).setStartDelay(n).setDuration(250L);
                this.mObstaclesInPlay.add((Obstacle)e2);
                final int irand2 = irand(0, 250);
                final Stem e3 = new Stem(this.getContext(), (float)(this.mHeight - n6 - MLand.PARAMS.OBSTACLE_GAP - n8), true);
                this.addView((View)e3, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(MLand.PARAMS.OBSTACLE_STEM_WIDTH, (int)((Obstacle)e3).h, 51));
                e3.setTranslationX((float)(this.mWidth + n7));
                e3.setTranslationY((float)(this.mHeight + n8));
                e3.setTranslationZ(MLand.PARAMS.OBSTACLE_Z * 0.75f);
                final ViewPropertyAnimator translationY2 = e3.animate().translationY(this.mHeight - ((Obstacle)e3).h);
                n = irand2;
                translationY2.setStartDelay(n).setDuration(400L);
                this.mObstaclesInPlay.add((Obstacle)e3);
                final Pop e4 = new Pop(this.getContext(), (float)MLand.PARAMS.OBSTACLE_WIDTH);
                final int obstacle_WIDTH3 = MLand.PARAMS.OBSTACLE_WIDTH;
                this.addView((View)e4, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(obstacle_WIDTH3, obstacle_WIDTH3, 51));
                e4.setTranslationX((float)this.mWidth);
                e4.setTranslationY((float)this.mHeight);
                e4.setTranslationZ(MLand.PARAMS.OBSTACLE_Z);
                e4.setScaleX(0.25f);
                e4.setScaleY(0.25f);
                e4.animate().translationY(this.mHeight - ((Obstacle)e3).h - n10).scaleX(1.0f).scaleY(1.0f).setStartDelay(n).setDuration(400L);
                this.mObstaclesInPlay.add((Obstacle)e4);
            }
        }
        this.invalidate();
    }
    
    private void thump(final int index, final long n) {
        if (this.mAudioManager.getRingerMode() == 0) {
            return;
        }
        if (index < this.mGameControllers.size()) {
            final InputDevice device = InputDevice.getDevice((int)this.mGameControllers.get(index));
            if (device != null && device.getVibrator().hasVibrator()) {
                device.getVibrator().vibrate((long)(n * 2.0f), this.mAudioAttrs);
                return;
            }
        }
        this.mVibrator.vibrate(n, this.mAudioAttrs);
    }
    
    private void unpoke(final int i) {
        L("unboost(%d)", i);
        if (!this.mFrozen && this.mAnimating) {
            if (this.mPlaying) {
                final Player player = this.getPlayer(i);
                if (player == null) {
                    return;
                }
                player.unboost();
            }
        }
    }
    
    public void addPlayer() {
        if (this.getNumPlayers() == 6) {
            return;
        }
        this.addPlayerInternal(Player.create(this));
    }
    
    public int getControllerPlayer(int index) {
        index = this.mGameControllers.indexOf(index);
        if (index >= 0 && index < this.mPlayers.size()) {
            return index;
        }
        return 0;
    }
    
    public ArrayList getGameControllers() {
        this.mGameControllers.clear();
        for (final int n : InputDevice.getDeviceIds()) {
            if (isGamePad(InputDevice.getDevice(n)) && !this.mGameControllers.contains(n)) {
                this.mGameControllers.add(n);
            }
        }
        return this.mGameControllers;
    }
    
    public float getGameTime() {
        return this.t;
    }
    
    public int getNumPlayers() {
        return this.mPlayers.size();
    }
    
    public Player getPlayer(final int index) {
        Player player;
        if (index < this.mPlayers.size()) {
            player = this.mPlayers.get(index);
        }
        else {
            player = null;
        }
        return player;
    }
    
    public void hideSplash() {
        final View mSplash = this.mSplash;
        if (mSplash != null && mSplash.getVisibility() == 0) {
            this.mSplash.setClickable(false);
            this.mSplash.animate().alpha(0.0f).translationZ(0.0f).setDuration(300L).withEndAction((Runnable)new Runnable() {
                @Override
                public void run() {
                    MLand.this.mSplash.setVisibility(8);
                }
            });
        }
    }
    
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        MLand.dp = this.getResources().getDisplayMetrics().density;
        this.reset();
        this.start(false);
    }
    
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (final Player player : this.mPlayers) {
            if (player.mTouchX > 0.0f) {
                this.mTouchPaint.setColor(player.color & 0x80FFFFFF);
                this.mPlayerTracePaint.setColor(player.color & 0x80FFFFFF);
                final float access$800 = player.mTouchX;
                final float access$801 = player.mTouchY;
                canvas.drawCircle(access$800, access$801, 100.0f, this.mTouchPaint);
                final float n = player.getX() + player.getPivotX();
                final float n2 = player.getY() + player.getPivotY();
                final float n3 = (float)Math.atan2(n - access$800, n2 - access$801);
                final double n4 = access$800;
                final double n5 = 1.5707964f - n3;
                canvas.drawLine((float)(n4 + Math.cos(n5) * 100.0), (float)(access$801 + Math.sin(n5) * 100.0), n, n2, this.mPlayerTracePaint);
            }
        }
    }
    
    public boolean onGenericMotionEvent(final MotionEvent motionEvent) {
        L("generic: %s", motionEvent);
        return false;
    }
    
    public boolean onKeyDown(final int i, final KeyEvent keyEvent) {
        L("keyDown: %d", i);
        if (i != 19 && i != 23 && i != 62 && i != 66 && i != 96) {
            return false;
        }
        this.poke(this.getControllerPlayer(keyEvent.getDeviceId()));
        return true;
    }
    
    public boolean onKeyUp(final int i, final KeyEvent keyEvent) {
        L("keyDown: %d", i);
        if (i != 19 && i != 23 && i != 62 && i != 66 && i != 96) {
            return false;
        }
        this.unpoke(this.getControllerPlayer(keyEvent.getDeviceId()));
        return true;
    }
    
    protected void onSizeChanged(final int n, final int n2, final int n3, final int n4) {
        MLand.dp = this.getResources().getDisplayMetrics().density;
        this.stop();
        this.reset();
        this.start(false);
    }
    
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        L("touch: %s", motionEvent);
        final int actionIndex = motionEvent.getActionIndex();
        final float x = motionEvent.getX(actionIndex);
        final float y = motionEvent.getY(actionIndex);
        int n2;
        final int n = n2 = (int)(this.getNumPlayers() * (x / this.getWidth()));
        if (this.mFlipped) {
            n2 = this.getNumPlayers() - 1 - n;
        }
        final int actionMasked = motionEvent.getActionMasked();
        Label_0107: {
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked == 5) {
                        break Label_0107;
                    }
                    if (actionMasked != 6) {
                        return false;
                    }
                }
                this.unpoke(n2);
                return true;
            }
        }
        this.poke(n2, x, y);
        return true;
    }
    
    public boolean onTrackballEvent(final MotionEvent motionEvent) {
        L("trackball: %s", motionEvent);
        final int action = motionEvent.getAction();
        if (action == 0) {
            this.poke(0);
            return true;
        }
        if (action != 1) {
            return false;
        }
        this.unpoke(0);
        return true;
    }
    
    public void removePlayer() {
        if (this.getNumPlayers() == 1) {
            return;
        }
        final ArrayList<Player> mPlayers = this.mPlayers;
        this.removePlayerInternal(mPlayers.get(mPlayers.size() - 1));
    }
    
    public void reset() {
        int i = 0;
        L("reset", new Object[0]);
        final GradientDrawable background = new GradientDrawable(GradientDrawable$Orientation.BOTTOM_TOP, MLand.SKIES[this.mTimeOfDay]);
        ((Drawable)background).setDither(true);
        this.setBackground((Drawable)background);
        final boolean mFlipped = frand() > 0.5f;
        this.mFlipped = mFlipped;
        final float n = -1.0f;
        float scaleX;
        if (mFlipped) {
            scaleX = -1.0f;
        }
        else {
            scaleX = 1.0f;
        }
        this.setScaleX(scaleX);
        int childCount = this.getChildCount();
        while (true) {
            final int n2 = childCount - 1;
            if (childCount <= 0) {
                break;
            }
            if (this.getChildAt(n2) instanceof GameView) {
                this.removeViewAt(n2);
            }
            childCount = n2;
        }
        this.mObstaclesInPlay.clear();
        this.mCurrentPipeId = 0;
        this.mWidth = this.getWidth();
        this.mHeight = this.getHeight();
        final int mTimeOfDay = this.mTimeOfDay;
        final boolean b = (mTimeOfDay == 0 || mTimeOfDay == 3) && frand() > 0.25;
        if (b) {
            final Star star = new Star(this.getContext());
            star.setBackgroundResource(R$drawable.sun);
            final int dimensionPixelSize = this.getResources().getDimensionPixelSize(R$dimen.sun_size);
            final float n3 = (float)dimensionPixelSize;
            star.setTranslationX(frand(n3, (float)(this.mWidth - dimensionPixelSize)));
            if (this.mTimeOfDay == 0) {
                star.setTranslationY(frand(n3, this.mHeight * 0.66f));
                star.getBackground().setTint(0);
            }
            else {
                final int mHeight = this.mHeight;
                star.setTranslationY(frand(mHeight * 0.66f, (float)(mHeight - dimensionPixelSize)));
                star.getBackground().setTintMode(PorterDuff$Mode.SRC_ATOP);
                star.getBackground().setTint(-1056997376);
            }
            this.addView((View)star, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(dimensionPixelSize, dimensionPixelSize));
        }
        if (!b) {
            final int mTimeOfDay2 = this.mTimeOfDay;
            final boolean b2 = mTimeOfDay2 == 1 || mTimeOfDay2 == 2;
            final float frand = frand();
            if ((b2 && frand < 0.75f) || frand < 0.5f) {
                final Star star2 = new Star(this.getContext());
                star2.setBackgroundResource(R$drawable.moon);
                final Drawable background2 = star2.getBackground();
                int alpha;
                if (b2) {
                    alpha = 255;
                }
                else {
                    alpha = 128;
                }
                background2.setAlpha(alpha);
                float scaleX2;
                if (frand() > 0.5) {
                    scaleX2 = n;
                }
                else {
                    scaleX2 = 1.0f;
                }
                star2.setScaleX(scaleX2);
                star2.setRotation(star2.getScaleX() * frand(5.0f, 30.0f));
                final int dimensionPixelSize2 = this.getResources().getDimensionPixelSize(R$dimen.sun_size);
                final float n4 = (float)dimensionPixelSize2;
                star2.setTranslationX(frand(n4, (float)(this.mWidth - dimensionPixelSize2)));
                star2.setTranslationY(frand(n4, (float)(this.mHeight - dimensionPixelSize2)));
                this.addView((View)star2, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(dimensionPixelSize2, dimensionPixelSize2));
            }
        }
        final int n5 = this.mHeight / 6;
        final boolean b3 = frand() < 0.25;
        while (i < 20) {
            final double n6 = frand();
            GameView gameView;
            if (n6 < 0.3 && this.mTimeOfDay != 0) {
                gameView = new Star(this.getContext());
            }
            else if (n6 < 0.6 && !b3) {
                gameView = new Cloud(this.getContext());
            }
            else {
                final int mScene = this.mScene;
                Scenery scenery;
                if (mScene != 1) {
                    if (mScene != 2) {
                        scenery = new Building(this.getContext());
                    }
                    else {
                        scenery = new Mountain(this.getContext());
                    }
                }
                else {
                    scenery = new Cactus(this.getContext());
                }
                final float z = i / 20.0f;
                scenery.z = z;
                scenery.v = z * 0.85f;
                if (this.mScene == 0) {
                    scenery.setBackgroundColor(-7829368);
                    scenery.h = irand(MLand.PARAMS.BUILDING_HEIGHT_MIN, n5);
                }
                final int n7 = (int)(scenery.z * 255.0f);
                final Drawable background3 = scenery.getBackground();
                gameView = scenery;
                if (background3 != null) {
                    background3.setColorFilter(Color.rgb(n7, n7, n7), PorterDuff$Mode.MULTIPLY);
                    gameView = scenery;
                }
            }
            final FrameLayout$LayoutParams frameLayout$LayoutParams = new FrameLayout$LayoutParams(((Scenery)gameView).w, ((Scenery)gameView).h);
            if (gameView instanceof Building) {
                frameLayout$LayoutParams.gravity = 80;
            }
            else {
                frameLayout$LayoutParams.gravity = 48;
                final float frand2 = frand();
                if (gameView instanceof Star) {
                    frameLayout$LayoutParams.topMargin = (int)(frand2 * frand2 * this.mHeight);
                }
                else {
                    final int mHeight2 = this.mHeight;
                    frameLayout$LayoutParams.topMargin = (int)(1.0f - frand2 * frand2 * mHeight2 / 2.0f) + mHeight2 / 2;
                }
            }
            this.addView((View)gameView, (ViewGroup$LayoutParams)frameLayout$LayoutParams);
            final int width = frameLayout$LayoutParams.width;
            ((FrameLayout)gameView).setTranslationX(frand((float)(-width), (float)(this.mWidth + width)));
            ++i;
        }
        for (final Player player : this.mPlayers) {
            this.addView((View)player);
            player.reset();
        }
        this.realignPlayers();
        final TimeAnimator mAnim = this.mAnim;
        if (mAnim != null) {
            mAnim.cancel();
        }
        (this.mAnim = new TimeAnimator()).setTimeListener((TimeAnimator$TimeListener)new TimeAnimator$TimeListener() {
            public void onTimeUpdate(final TimeAnimator timeAnimator, final long n, final long n2) {
                MLand.this.step(n, n2);
            }
        });
    }
    
    public void setScoreFieldHolder(final ViewGroup mScoreFields) {
        this.mScoreFields = mScoreFields;
        if (mScoreFields != null) {
            final LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.setDuration(250L);
            this.mScoreFields.setLayoutTransition(layoutTransition);
        }
        final Iterator<Player> iterator = this.mPlayers.iterator();
        while (iterator.hasNext()) {
            this.mScoreFields.addView((View)iterator.next().mScoreField, (ViewGroup$LayoutParams)new ViewGroup$MarginLayoutParams(-2, -1));
        }
    }
    
    public void setSplash(final View mSplash) {
        this.mSplash = mSplash;
    }
    
    public void setupPlayers(final int n) {
        this.clearPlayers();
        for (int i = 0; i < n; ++i) {
            this.addPlayerInternal(Player.create(this));
        }
    }
    
    public void showSplash() {
        final View mSplash = this.mSplash;
        if (mSplash != null && mSplash.getVisibility() != 0) {
            this.mSplash.setClickable(true);
            this.mSplash.setAlpha(0.0f);
            this.mSplash.setVisibility(0);
            this.mSplash.animate().alpha(1.0f).setDuration(1000L);
            this.mSplash.findViewById(R$id.play_button_image).setAlpha(1.0f);
            this.mSplash.findViewById(R$id.play_button_text).setAlpha(0.0f);
            this.mSplash.findViewById(R$id.play_button).setEnabled(true);
            this.mSplash.findViewById(R$id.play_button).requestFocus();
        }
    }
    
    public void start(final boolean b) {
        String s;
        if (b) {
            s = "true";
        }
        else {
            s = "false";
        }
        L("start(startPlaying=%s)", s);
        if (b && this.mCountdown <= 0) {
            this.showSplash();
            this.mSplash.findViewById(R$id.play_button).setEnabled(false);
            final View viewById = this.mSplash.findViewById(R$id.play_button_image);
            final TextView textView = (TextView)this.mSplash.findViewById(R$id.play_button_text);
            viewById.animate().alpha(0.0f);
            textView.animate().alpha(1.0f);
            this.mCountdown = 3;
            this.post((Runnable)new Runnable() {
                @Override
                public void run() {
                    if (MLand.this.mCountdown == 0) {
                        MLand.this.startPlaying();
                    }
                    else {
                        MLand.this.postDelayed((Runnable)this, 500L);
                    }
                    textView.setText((CharSequence)String.valueOf(MLand.this.mCountdown));
                    MLand.this.mCountdown--;
                }
            });
        }
        final Iterator<Player> iterator = this.mPlayers.iterator();
        while (iterator.hasNext()) {
            iterator.next().setVisibility(4);
        }
        if (!this.mAnimating) {
            this.mAnim.start();
            this.mAnimating = true;
        }
    }
    
    public void startPlaying() {
        this.mPlaying = true;
        this.t = 0.0f;
        this.mLastPipeTime = this.getGameTime() - MLand.PARAMS.OBSTACLE_PERIOD;
        this.hideSplash();
        this.realignPlayers();
        this.mTaps = 0;
        final int size = this.mPlayers.size();
        MetricsLogger.histogram(this.getContext(), "egg_mland_players", size);
        for (int i = 0; i < size; ++i) {
            final Player player = this.mPlayers.get(i);
            player.setVisibility(0);
            player.reset();
            player.start();
            player.boost(-1.0f, -1.0f);
            player.unboost();
        }
    }
    
    public void stop() {
        if (this.mAnimating) {
            this.mAnim.cancel();
            this.mAnim = null;
            this.mAnimating = false;
            this.mPlaying = false;
            this.mTimeOfDay = irand(0, MLand.SKIES.length - 1);
            this.mScene = irand(0, 3);
            this.mFrozen = true;
            final Iterator<Player> iterator = this.mPlayers.iterator();
            while (iterator.hasNext()) {
                iterator.next().die();
            }
            this.postDelayed((Runnable)new Runnable() {
                @Override
                public void run() {
                    MLand.this.mFrozen = false;
                }
            }, 250L);
        }
    }
    
    public boolean willNotDraw() {
        return MLand.DEBUG ^ true;
    }
    
    private class Building extends Scenery
    {
        public Building(final MLand mLand, final Context context) {
            mLand.super(context);
            super.w = MLand.irand(MLand.PARAMS.BUILDING_WIDTH_MIN, MLand.PARAMS.BUILDING_WIDTH_MAX);
            super.h = 0;
        }
    }
    
    private class Cactus extends Building
    {
        public Cactus(final MLand mLand, final Context context) {
            mLand.super(context);
            this.setBackgroundResource(MLand.pick(MLand.CACTI));
            final int irand = MLand.irand(MLand.PARAMS.BUILDING_WIDTH_MAX / 4, MLand.PARAMS.BUILDING_WIDTH_MAX / 2);
            super.h = irand;
            super.w = irand;
        }
    }
    
    private class Cloud extends Scenery
    {
        public Cloud(final MLand mLand, final Context context) {
            mLand.super(context);
            int backgroundResource;
            if (MLand.frand() < 0.01f) {
                backgroundResource = R$drawable.cloud_off;
            }
            else {
                backgroundResource = R$drawable.cloud;
            }
            this.setBackgroundResource(backgroundResource);
            this.getBackground().setAlpha(64);
            final int irand = MLand.irand(MLand.PARAMS.CLOUD_SIZE_MIN, MLand.PARAMS.CLOUD_SIZE_MAX);
            super.h = irand;
            super.w = irand;
            super.z = 0.0f;
            super.v = MLand.frand(0.15f, 0.5f);
        }
    }
    
    private interface GameView
    {
        void step(final long p0, final long p1, final float p2, final float p3);
    }
    
    private class Mountain extends Building
    {
        public Mountain(final MLand mLand, final Context context) {
            mLand.super(context);
            this.setBackgroundResource(MLand.pick(MLand.MOUNTAINS));
            final int irand = MLand.irand(MLand.PARAMS.BUILDING_WIDTH_MAX / 2, MLand.PARAMS.BUILDING_WIDTH_MAX);
            super.h = irand;
            super.w = irand;
            super.z = 0.0f;
        }
    }
    
    private class Obstacle extends View implements GameView
    {
        public float h;
        public final Rect hitRect;
        
        public Obstacle(final MLand mLand, final Context context, final float h) {
            super(context);
            this.hitRect = new Rect();
            this.setBackgroundColor(-65536);
            this.h = h;
        }
        
        public boolean cleared(final Player player) {
            for (int n = player.corners.length / 2, i = 0; i < n; ++i) {
                if (this.hitRect.right >= (int)player.corners[i * 2]) {
                    return false;
                }
            }
            return true;
        }
        
        public boolean intersects(final Player player) {
            for (int n = player.corners.length / 2, i = 0; i < n; ++i) {
                final float[] corners = player.corners;
                final int n2 = i * 2;
                if (this.hitRect.contains((int)corners[n2], (int)corners[n2 + 1])) {
                    return true;
                }
            }
            return false;
        }
        
        public void step(final long n, final long n2, final float n3, final float n4) {
            this.setTranslationX(this.getTranslationX() - MLand.PARAMS.TRANSLATION_PER_SEC * n4);
            this.getHitRect(this.hitRect);
        }
    }
    
    private static class Params
    {
        public int BOOST_DV;
        public int BUILDING_HEIGHT_MIN;
        public int BUILDING_WIDTH_MAX;
        public int BUILDING_WIDTH_MIN;
        public int CLOUD_SIZE_MAX;
        public int CLOUD_SIZE_MIN;
        public int G;
        public int MAX_V;
        public int OBSTACLE_GAP;
        public int OBSTACLE_MIN;
        public int OBSTACLE_PERIOD;
        public int OBSTACLE_SPACING;
        public int OBSTACLE_STEM_WIDTH;
        public int OBSTACLE_WIDTH;
        public float OBSTACLE_Z;
        public int PLAYER_HIT_SIZE;
        public int PLAYER_SIZE;
        public float PLAYER_Z;
        public float PLAYER_Z_BOOST;
        public int STAR_SIZE_MAX;
        public int STAR_SIZE_MIN;
        public float TRANSLATION_PER_SEC;
        
        public Params(final Resources resources) {
            this.TRANSLATION_PER_SEC = resources.getDimension(R$dimen.translation_per_sec);
            final int dimensionPixelSize = resources.getDimensionPixelSize(R$dimen.obstacle_spacing);
            this.OBSTACLE_SPACING = dimensionPixelSize;
            this.OBSTACLE_PERIOD = (int)(dimensionPixelSize / this.TRANSLATION_PER_SEC);
            this.BOOST_DV = resources.getDimensionPixelSize(R$dimen.boost_dv);
            this.PLAYER_HIT_SIZE = resources.getDimensionPixelSize(R$dimen.player_hit_size);
            this.PLAYER_SIZE = resources.getDimensionPixelSize(R$dimen.player_size);
            this.OBSTACLE_WIDTH = resources.getDimensionPixelSize(R$dimen.obstacle_width);
            this.OBSTACLE_STEM_WIDTH = resources.getDimensionPixelSize(R$dimen.obstacle_stem_width);
            this.OBSTACLE_GAP = resources.getDimensionPixelSize(R$dimen.obstacle_gap);
            this.OBSTACLE_MIN = resources.getDimensionPixelSize(R$dimen.obstacle_height_min);
            this.BUILDING_HEIGHT_MIN = resources.getDimensionPixelSize(R$dimen.building_height_min);
            this.BUILDING_WIDTH_MIN = resources.getDimensionPixelSize(R$dimen.building_width_min);
            this.BUILDING_WIDTH_MAX = resources.getDimensionPixelSize(R$dimen.building_width_max);
            this.CLOUD_SIZE_MIN = resources.getDimensionPixelSize(R$dimen.cloud_size_min);
            this.CLOUD_SIZE_MAX = resources.getDimensionPixelSize(R$dimen.cloud_size_max);
            this.STAR_SIZE_MIN = resources.getDimensionPixelSize(R$dimen.star_size_min);
            this.STAR_SIZE_MAX = resources.getDimensionPixelSize(R$dimen.star_size_max);
            this.G = resources.getDimensionPixelSize(R$dimen.G);
            this.MAX_V = resources.getDimensionPixelSize(R$dimen.max_v);
            resources.getDimensionPixelSize(R$dimen.scenery_z);
            this.OBSTACLE_Z = (float)resources.getDimensionPixelSize(R$dimen.obstacle_z);
            this.PLAYER_Z = (float)resources.getDimensionPixelSize(R$dimen.player_z);
            this.PLAYER_Z_BOOST = (float)resources.getDimensionPixelSize(R$dimen.player_z_boost);
            resources.getDimensionPixelSize(R$dimen.hud_z);
            if (this.OBSTACLE_MIN <= this.OBSTACLE_WIDTH / 2) {
                MLand.L("error: obstacles might be too short, adjusting", new Object[0]);
                this.OBSTACLE_MIN = this.OBSTACLE_WIDTH / 2 + 1;
            }
        }
    }
    
    private static class Player extends ImageView implements GameView
    {
        static int sNextColor;
        public int color;
        public final float[] corners;
        public float dv;
        private boolean mAlive;
        private boolean mBoosting;
        private MLand mLand;
        private int mScore;
        private TextView mScoreField;
        private float mTouchX;
        private float mTouchY;
        private final int[] sColors;
        private final float[] sHull;
        
        public Player(final Context context) {
            super(context);
            this.mTouchX = -1.0f;
            this.mTouchY = -1.0f;
            this.sColors = new int[] { -2407369, -12879641, -740352, -15753896, -8710016, -6381922 };
            final float[] array;
            final float[] sHull = array = new float[16];
            array[0] = 0.3f;
            array[1] = 0.0f;
            array[2] = 0.7f;
            array[3] = 0.0f;
            array[4] = 0.92f;
            array[5] = 0.33f;
            array[6] = 0.92f;
            array[7] = 0.75f;
            array[8] = 0.6f;
            array[9] = 1.0f;
            array[10] = 0.4f;
            array[11] = 1.0f;
            array[12] = 0.08f;
            array[13] = 0.75f;
            array[14] = 0.08f;
            array[15] = 0.33f;
            this.sHull = sHull;
            this.corners = new float[sHull.length];
            this.setBackgroundResource(R$drawable.android);
            this.getBackground().setTintMode(PorterDuff$Mode.SRC_ATOP);
            final int[] sColors = this.sColors;
            final int sNextColor = Player.sNextColor;
            Player.sNextColor = sNextColor + 1;
            this.color = sColors[sNextColor % sColors.length];
            this.getBackground().setTint(this.color);
            this.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider(this) {
                public void getOutline(final View view, final Outline outline) {
                    final int width = view.getWidth();
                    final int height = view.getHeight();
                    final int n = (int)(width * 0.3f);
                    final int n2 = (int)(height * 0.2f);
                    outline.setRect(n, n2, width - n, height - n2);
                }
            });
        }
        
        private void addScore(final int n) {
            this.setScore(this.mScore + n);
        }
        
        public static Player create(final MLand mLand) {
            final Player player = new Player(mLand.getContext());
            player.mLand = mLand;
            player.reset();
            player.setVisibility(4);
            mLand.addView((View)player, (ViewGroup$LayoutParams)new FrameLayout$LayoutParams(MLand.PARAMS.PLAYER_SIZE, MLand.PARAMS.PLAYER_SIZE));
            return player;
        }
        
        private void setScore(final int n) {
            this.mScore = n;
            final TextView mScoreField = this.mScoreField;
            if (mScoreField != null) {
                String value;
                if (MLand.DEBUG_IDDQD) {
                    value = "??";
                }
                else {
                    value = String.valueOf(n);
                }
                mScoreField.setText((CharSequence)value);
            }
        }
        
        public boolean below(final int n) {
            for (int n2 = this.corners.length / 2, i = 0; i < n2; ++i) {
                if ((int)this.corners[i * 2 + 1] >= n) {
                    return true;
                }
            }
            return false;
        }
        
        public void boost() {
            this.mBoosting = true;
            this.dv = (float)(-MLand.PARAMS.BOOST_DV);
            this.animate().cancel();
            this.animate().scaleX(1.25f).scaleY(1.25f).translationZ(MLand.PARAMS.PLAYER_Z_BOOST).setDuration(100L);
            this.setScaleX(1.25f);
            this.setScaleY(1.25f);
        }
        
        public void boost(final float mTouchX, final float mTouchY) {
            this.mTouchX = mTouchX;
            this.mTouchY = mTouchY;
            this.boost();
        }
        
        public void die() {
            this.mAlive = false;
        }
        
        public int getScore() {
            return this.mScore;
        }
        
        public void prepareCheckIntersections() {
            final int n = (MLand.PARAMS.PLAYER_SIZE - MLand.PARAMS.PLAYER_HIT_SIZE) / 2;
            final int player_HIT_SIZE = MLand.PARAMS.PLAYER_HIT_SIZE;
            for (int n2 = this.sHull.length / 2, i = 0; i < n2; ++i) {
                final float[] corners = this.corners;
                int n3 = i * 2;
                final float n4 = (float)player_HIT_SIZE;
                final float[] sHull = this.sHull;
                final float n5 = sHull[n3];
                final float n6 = (float)n;
                corners[n3] = n5 * n4 + n6;
                ++n3;
                corners[n3] = n4 * sHull[n3] + n6;
            }
            this.getMatrix().mapPoints(this.corners);
        }
        
        public void reset() {
            this.setY((float)(this.mLand.mHeight / 2 + (int)(Math.random() * MLand.PARAMS.PLAYER_SIZE) - MLand.PARAMS.PLAYER_SIZE / 2));
            this.setScore(0);
            this.setScoreField(this.mScoreField);
            this.mBoosting = false;
            this.dv = 0.0f;
        }
        
        public void setScoreField(TextView mScoreField) {
            this.mScoreField = mScoreField;
            if (mScoreField != null) {
                this.setScore(this.mScore);
                this.mScoreField.getBackground().setColorFilter(this.color, PorterDuff$Mode.SRC_ATOP);
                mScoreField = this.mScoreField;
                int textColor;
                if (luma(this.color) > 0.7f) {
                    textColor = -16777216;
                }
                else {
                    textColor = -1;
                }
                mScoreField.setTextColor(textColor);
            }
        }
        
        public void start() {
            this.mAlive = true;
        }
        
        public void step(final long n, final long n2, float translationY, float n3) {
            if (!this.mAlive) {
                this.setTranslationX(this.getTranslationX() - MLand.PARAMS.TRANSLATION_PER_SEC * n3);
                return;
            }
            if (this.mBoosting) {
                this.dv = (float)(-MLand.PARAMS.BOOST_DV);
            }
            else {
                this.dv += MLand.PARAMS.G;
            }
            if (this.dv < -MLand.PARAMS.MAX_V) {
                this.dv = (float)(-MLand.PARAMS.MAX_V);
            }
            else if (this.dv > MLand.PARAMS.MAX_V) {
                this.dv = (float)MLand.PARAMS.MAX_V;
            }
            n3 = (translationY = this.getTranslationY() + this.dv * n3);
            if (n3 < 0.0f) {
                translationY = 0.0f;
            }
            this.setTranslationY(translationY);
            this.setRotation(MLand.lerp(MLand.clamp(MLand.rlerp(this.dv, (float)MLand.PARAMS.MAX_V, (float)(MLand.PARAMS.MAX_V * -1))), 90.0f, -90.0f) + 90.0f);
            this.prepareCheckIntersections();
        }
        
        public void unboost() {
            this.mBoosting = false;
            this.mTouchY = -1.0f;
            this.mTouchX = -1.0f;
            this.animate().cancel();
            this.animate().scaleX(1.0f).scaleY(1.0f).translationZ(MLand.PARAMS.PLAYER_Z).setDuration(200L);
        }
    }
    
    private class Pop extends Obstacle
    {
        Drawable antenna;
        int cx;
        int cy;
        Drawable eyes;
        int mRotate;
        Drawable mouth;
        int r;
        
        public Pop(final MLand mLand, final Context context, final float n) {
            mLand.super(context, n);
            this.setBackgroundResource(R$drawable.mm_head);
            this.antenna = context.getDrawable(MLand.pick(MLand.ANTENNAE));
            if (MLand.frand() > 0.5f) {
                this.eyes = context.getDrawable(MLand.pick(MLand.EYES));
                if (MLand.frand() > 0.8f) {
                    this.mouth = context.getDrawable(MLand.pick(MLand.MOUTHS));
                }
            }
            this.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider(mLand) {
                public void getOutline(final View view, final Outline outline) {
                    final int n = (int)(Pop.this.getWidth() * 1.0f / 6.0f);
                    outline.setOval(n, n, Pop.this.getWidth() - n, Pop.this.getHeight() - n);
                }
            });
        }
        
        @Override
        public boolean intersects(final Player player) {
            for (int n = player.corners.length / 2, i = 0; i < n; ++i) {
                final float[] corners = player.corners;
                final int n2 = i * 2;
                if (Math.hypot((int)corners[n2] - this.cx, (int)corners[n2 + 1] - this.cy) <= this.r) {
                    return true;
                }
            }
            return false;
        }
        
        public void onDraw(final Canvas canvas) {
            super.onDraw(canvas);
            final Drawable antenna = this.antenna;
            if (antenna != null) {
                antenna.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                this.antenna.draw(canvas);
            }
            final Drawable eyes = this.eyes;
            if (eyes != null) {
                eyes.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                this.eyes.draw(canvas);
            }
            final Drawable mouth = this.mouth;
            if (mouth != null) {
                mouth.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                this.mouth.draw(canvas);
            }
        }
        
        @Override
        public void step(final long n, final long n2, final float n3, final float n4) {
            super.step(n, n2, n3, n4);
            if (this.mRotate != 0) {
                this.setRotation(this.getRotation() + n4 * 45.0f * this.mRotate);
            }
            final Rect hitRect = super.hitRect;
            this.cx = (hitRect.left + hitRect.right) / 2;
            this.cy = (hitRect.top + hitRect.bottom) / 2;
            this.r = this.getWidth() / 3;
        }
    }
    
    private class Scenery extends FrameLayout implements GameView
    {
        public int h;
        public float v;
        public int w;
        public float z;
        
        public Scenery(final MLand mLand, final Context context) {
            super(context);
        }
        
        public void step(final long n, final long n2, final float n3, final float n4) {
            this.setTranslationX(this.getTranslationX() - MLand.PARAMS.TRANSLATION_PER_SEC * n4 * this.v);
        }
    }
    
    private class Star extends Scenery
    {
        public Star(final MLand mLand, final Context context) {
            mLand.super(context);
            this.setBackgroundResource(R$drawable.star);
            final int irand = MLand.irand(MLand.PARAMS.STAR_SIZE_MIN, MLand.PARAMS.STAR_SIZE_MAX);
            super.h = irand;
            super.w = irand;
            super.z = 0.0f;
            super.v = 0.0f;
        }
    }
    
    private class Stem extends Obstacle
    {
        int id;
        boolean mDrawShadow;
        GradientDrawable mGradient;
        Path mJandystripe;
        Paint mPaint;
        Paint mPaint2;
        Path mShadow;
        
        public Stem(final MLand mLand, final Context context, final float n, final boolean mDrawShadow) {
            mLand.super(context, n);
            this.mPaint = new Paint();
            this.mShadow = new Path();
            this.mGradient = new GradientDrawable();
            this.id = mLand.mCurrentPipeId;
            this.mDrawShadow = mDrawShadow;
            this.setBackground((Drawable)null);
            this.mGradient.setOrientation(GradientDrawable$Orientation.LEFT_RIGHT);
            this.mPaint.setColor(-16777216);
            this.mPaint.setColorFilter((ColorFilter)new PorterDuffColorFilter(570425344, PorterDuff$Mode.MULTIPLY));
            if (MLand.frand() < 0.01f) {
                this.mGradient.setColors(new int[] { -1, -2236963 });
                this.mJandystripe = new Path();
                (this.mPaint2 = new Paint()).setColor(-65536);
                this.mPaint2.setColorFilter((ColorFilter)new PorterDuffColorFilter(-65536, PorterDuff$Mode.MULTIPLY));
            }
            else {
                this.mGradient.setColors(new int[] { -4412764, -6190977 });
            }
        }
        
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.setWillNotDraw(false);
            this.setOutlineProvider((ViewOutlineProvider)new ViewOutlineProvider() {
                public void getOutline(final View view, final Outline outline) {
                    outline.setRect(0, 0, Stem.this.getWidth(), Stem.this.getHeight());
                }
            });
        }
        
        public void onDraw(final Canvas canvas) {
            final int width = canvas.getWidth();
            final int height = canvas.getHeight();
            final GradientDrawable mGradient = this.mGradient;
            final float n = (float)width;
            mGradient.setGradientCenter(0.75f * n, 0.0f);
            final GradientDrawable mGradient2 = this.mGradient;
            int i = 0;
            mGradient2.setBounds(0, 0, width, height);
            this.mGradient.draw(canvas);
            final Path mJandystripe = this.mJandystripe;
            if (mJandystripe != null) {
                mJandystripe.reset();
                this.mJandystripe.moveTo(0.0f, n);
                this.mJandystripe.lineTo(n, 0.0f);
                this.mJandystripe.lineTo(n, (float)(width * 2));
                this.mJandystripe.lineTo(0.0f, (float)(width * 3));
                this.mJandystripe.close();
                while (i < height) {
                    canvas.drawPath(this.mJandystripe, this.mPaint2);
                    final Path mJandystripe2 = this.mJandystripe;
                    final int n2 = width * 4;
                    mJandystripe2.offset(0.0f, (float)n2);
                    i += n2;
                }
            }
            if (!this.mDrawShadow) {
                return;
            }
            this.mShadow.reset();
            this.mShadow.moveTo(0.0f, 0.0f);
            this.mShadow.lineTo(n, 0.0f);
            this.mShadow.lineTo(n, MLand.PARAMS.OBSTACLE_WIDTH * 0.4f + 1.5f * n);
            this.mShadow.lineTo(0.0f, MLand.PARAMS.OBSTACLE_WIDTH * 0.4f);
            this.mShadow.close();
            canvas.drawPath(this.mShadow, this.mPaint);
        }
    }
}
