// 
// Decompiled by Procyon v0.5.36
// 

package androidx.leanback.widget;

import java.util.ArrayList;
import android.os.Bundle;
import android.util.Log;
import android.speech.RecognitionListener;
import android.content.Intent;
import android.os.Build$VERSION;
import android.view.MotionEvent;
import android.os.SystemClock;
import android.view.View$OnClickListener;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView$OnEditorActionListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View$OnFocusChangeListener;
import androidx.leanback.R$id;
import android.text.TextUtils;
import androidx.leanback.R$string;
import androidx.leanback.R$raw;
import android.content.res.Resources;
import androidx.leanback.R$integer;
import androidx.leanback.R$color;
import android.view.ViewGroup$LayoutParams;
import android.widget.RelativeLayout$LayoutParams;
import androidx.leanback.R$dimen;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import android.view.LayoutInflater;
import android.util.AttributeSet;
import android.speech.SpeechRecognizer;
import android.media.SoundPool;
import android.util.SparseIntArray;
import android.view.inputmethod.InputMethodManager;
import android.os.Handler;
import android.content.Context;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;

public class SearchBar extends RelativeLayout
{
    static final String TAG;
    boolean mAutoStartRecognition;
    private int mBackgroundAlpha;
    private int mBackgroundSpeechAlpha;
    private Drawable mBadgeDrawable;
    private ImageView mBadgeView;
    private Drawable mBarBackground;
    private int mBarHeight;
    private final Context mContext;
    final Handler mHandler;
    private String mHint;
    private final InputMethodManager mInputMethodManager;
    private boolean mListening;
    private SearchBarPermissionListener mPermissionListener;
    boolean mRecognizing;
    SearchBarListener mSearchBarListener;
    String mSearchQuery;
    SearchEditText mSearchTextEditor;
    SparseIntArray mSoundMap;
    SoundPool mSoundPool;
    SpeechOrbView mSpeechOrbView;
    private SpeechRecognitionCallback mSpeechRecognitionCallback;
    private SpeechRecognizer mSpeechRecognizer;
    private final int mTextColor;
    private final int mTextColorSpeechMode;
    private final int mTextHintColor;
    private final int mTextHintColorSpeechMode;
    private String mTitle;
    
    static {
        TAG = SearchBar.class.getSimpleName();
    }
    
    public SearchBar(final Context context) {
        this(context, null);
    }
    
    public SearchBar(final Context context, final AttributeSet set) {
        this(context, set, 0);
    }
    
    public SearchBar(final Context mContext, final AttributeSet set, final int n) {
        super(mContext, set, n);
        this.mHandler = new Handler();
        this.mAutoStartRecognition = false;
        this.mSoundMap = new SparseIntArray();
        this.mRecognizing = false;
        this.mContext = mContext;
        final Resources resources = this.getResources();
        LayoutInflater.from(this.getContext()).inflate(R$layout.lb_search_bar, (ViewGroup)this, true);
        this.mBarHeight = this.getResources().getDimensionPixelSize(R$dimen.lb_search_bar_height);
        final RelativeLayout$LayoutParams layoutParams = new RelativeLayout$LayoutParams(-1, this.mBarHeight);
        layoutParams.addRule(10, -1);
        this.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
        this.setBackgroundColor(0);
        this.setClipChildren(false);
        this.mSearchQuery = "";
        this.mInputMethodManager = (InputMethodManager)mContext.getSystemService("input_method");
        this.mTextColorSpeechMode = resources.getColor(R$color.lb_search_bar_text_speech_mode);
        this.mTextColor = resources.getColor(R$color.lb_search_bar_text);
        this.mBackgroundSpeechAlpha = resources.getInteger(R$integer.lb_search_bar_speech_mode_background_alpha);
        this.mBackgroundAlpha = resources.getInteger(R$integer.lb_search_bar_text_mode_background_alpha);
        this.mTextHintColorSpeechMode = resources.getColor(R$color.lb_search_bar_hint_speech_mode);
        this.mTextHintColor = resources.getColor(R$color.lb_search_bar_hint);
    }
    
    private boolean isVoiceMode() {
        return this.mSpeechOrbView.isFocused();
    }
    
    private void loadSounds(final Context context) {
        final int lb_voice_failure = R$raw.lb_voice_failure;
        int i = 0;
        final int lb_voice_open = R$raw.lb_voice_open;
        final int lb_voice_no_input = R$raw.lb_voice_no_input;
        final int lb_voice_success = R$raw.lb_voice_success;
        while (i < 4) {
            final int n = (new int[] { lb_voice_failure, lb_voice_open, lb_voice_no_input, lb_voice_success })[i];
            this.mSoundMap.put(n, this.mSoundPool.load(context, n, 1));
            ++i;
        }
    }
    
    private void play(final int n) {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                SearchBar.this.mSoundPool.play(SearchBar.this.mSoundMap.get(n), 1.0f, 1.0f, 1, 0, 1.0f);
            }
        });
    }
    
    private void updateHint() {
        String s = this.getResources().getString(R$string.lb_search_bar_hint);
        if (!TextUtils.isEmpty((CharSequence)this.mTitle)) {
            if (this.isVoiceMode()) {
                s = this.getResources().getString(R$string.lb_search_bar_hint_with_title_speech, new Object[] { this.mTitle });
            }
            else {
                s = this.getResources().getString(R$string.lb_search_bar_hint_with_title, new Object[] { this.mTitle });
            }
        }
        else if (this.isVoiceMode()) {
            s = this.getResources().getString(R$string.lb_search_bar_hint_speech);
        }
        this.mHint = s;
        final SearchEditText mSearchTextEditor = this.mSearchTextEditor;
        if (mSearchTextEditor != null) {
            mSearchTextEditor.setHint((CharSequence)s);
        }
    }
    
    void hideNativeKeyboard() {
        this.mInputMethodManager.hideSoftInputFromWindow(this.mSearchTextEditor.getWindowToken(), 0);
    }
    
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSoundPool = new SoundPool(2, 1, 0);
        this.loadSounds(this.mContext);
    }
    
    protected void onDetachedFromWindow() {
        this.stopRecognition();
        this.mSoundPool.release();
        super.onDetachedFromWindow();
    }
    
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mBarBackground = ((RelativeLayout)this.findViewById(R$id.lb_search_bar_items)).getBackground();
        this.mSearchTextEditor = (SearchEditText)this.findViewById(R$id.lb_search_text_editor);
        final ImageView mBadgeView = (ImageView)this.findViewById(R$id.lb_search_bar_badge);
        this.mBadgeView = mBadgeView;
        final Drawable mBadgeDrawable = this.mBadgeDrawable;
        if (mBadgeDrawable != null) {
            mBadgeView.setImageDrawable(mBadgeDrawable);
        }
        this.mSearchTextEditor.setOnFocusChangeListener((View$OnFocusChangeListener)new View$OnFocusChangeListener() {
            public void onFocusChange(final View view, final boolean b) {
                if (b) {
                    SearchBar.this.showNativeKeyboard();
                }
                else {
                    SearchBar.this.hideNativeKeyboard();
                }
                SearchBar.this.updateUi(b);
            }
        });
        this.mSearchTextEditor.addTextChangedListener((TextWatcher)new TextWatcher() {
            final /* synthetic */ Runnable val$mOnTextChangedRunnable = new Runnable(this) {
                @Override
                public void run() {
                    final SearchBar this$0 = SearchBar.this;
                    this$0.setSearchQueryInternal(this$0.mSearchTextEditor.getText().toString());
                }
            };
            
            public void afterTextChanged(final Editable editable) {
            }
            
            public void beforeTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
            }
            
            public void onTextChanged(final CharSequence charSequence, final int n, final int n2, final int n3) {
                final SearchBar this$0 = SearchBar.this;
                if (this$0.mRecognizing) {
                    return;
                }
                this$0.mHandler.removeCallbacks(this.val$mOnTextChangedRunnable);
                SearchBar.this.mHandler.post(this.val$mOnTextChangedRunnable);
            }
        });
        this.mSearchTextEditor.setOnKeyboardDismissListener((SearchEditText.OnKeyboardDismissListener)new SearchEditText.OnKeyboardDismissListener() {
            @Override
            public void onKeyboardDismiss() {
                final SearchBar this$0 = SearchBar.this;
                final SearchBarListener mSearchBarListener = this$0.mSearchBarListener;
                if (mSearchBarListener != null) {
                    mSearchBarListener.onKeyboardDismiss(this$0.mSearchQuery);
                }
            }
        });
        this.mSearchTextEditor.setOnEditorActionListener((TextView$OnEditorActionListener)new TextView$OnEditorActionListener() {
            public boolean onEditorAction(final TextView textView, final int n, final KeyEvent keyEvent) {
                boolean b = true;
                if (3 == n || n == 0) {
                    final SearchBar this$0 = SearchBar.this;
                    if (this$0.mSearchBarListener != null) {
                        this$0.hideNativeKeyboard();
                        SearchBar.this.mHandler.postDelayed((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                SearchBar.this.submitQuery();
                            }
                        }, 500L);
                        return b;
                    }
                }
                if (1 == n) {
                    final SearchBar this$2 = SearchBar.this;
                    if (this$2.mSearchBarListener != null) {
                        this$2.hideNativeKeyboard();
                        SearchBar.this.mHandler.postDelayed((Runnable)new Runnable() {
                            @Override
                            public void run() {
                                final SearchBar this$0 = SearchBar.this;
                                this$0.mSearchBarListener.onKeyboardDismiss(this$0.mSearchQuery);
                            }
                        }, 500L);
                        return b;
                    }
                }
                if (2 == n) {
                    SearchBar.this.hideNativeKeyboard();
                    SearchBar.this.mHandler.postDelayed((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            final SearchBar this$0 = SearchBar.this;
                            this$0.mAutoStartRecognition = true;
                            this$0.mSpeechOrbView.requestFocus();
                        }
                    }, 500L);
                }
                else {
                    b = false;
                }
                return b;
            }
        });
        this.mSearchTextEditor.setPrivateImeOptions("escapeNorth,voiceDismiss");
        (this.mSpeechOrbView = (SpeechOrbView)this.findViewById(R$id.lb_search_bar_speech_orb)).setOnOrbClickedListener((View$OnClickListener)new View$OnClickListener() {
            public void onClick(final View view) {
                SearchBar.this.toggleRecognition();
            }
        });
        this.mSpeechOrbView.setOnFocusChangeListener((View$OnFocusChangeListener)new View$OnFocusChangeListener() {
            public void onFocusChange(final View view, final boolean b) {
                if (b) {
                    SearchBar.this.hideNativeKeyboard();
                    final SearchBar this$0 = SearchBar.this;
                    if (this$0.mAutoStartRecognition) {
                        this$0.startRecognition();
                        SearchBar.this.mAutoStartRecognition = false;
                    }
                }
                else {
                    SearchBar.this.stopRecognition();
                }
                SearchBar.this.updateUi(b);
            }
        });
        this.updateUi(this.hasFocus());
        this.updateHint();
    }
    
    void playSearchFailure() {
        this.play(R$raw.lb_voice_failure);
    }
    
    void playSearchOpen() {
        this.play(R$raw.lb_voice_open);
    }
    
    void playSearchSuccess() {
        this.play(R$raw.lb_voice_success);
    }
    
    public void setNextFocusDownId(final int n) {
        this.mSpeechOrbView.setNextFocusDownId(n);
        this.mSearchTextEditor.setNextFocusDownId(n);
    }
    
    void setSearchQueryInternal(final String mSearchQuery) {
        if (TextUtils.equals((CharSequence)this.mSearchQuery, (CharSequence)mSearchQuery)) {
            return;
        }
        this.mSearchQuery = mSearchQuery;
        final SearchBarListener mSearchBarListener = this.mSearchBarListener;
        if (mSearchBarListener != null) {
            mSearchBarListener.onSearchQueryChange(mSearchQuery);
        }
    }
    
    void showNativeKeyboard() {
        this.mHandler.post((Runnable)new Runnable() {
            @Override
            public void run() {
                SearchBar.this.mSearchTextEditor.requestFocusFromTouch();
                SearchBar.this.mSearchTextEditor.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float)SearchBar.this.mSearchTextEditor.getWidth(), (float)SearchBar.this.mSearchTextEditor.getHeight(), 0));
                SearchBar.this.mSearchTextEditor.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float)SearchBar.this.mSearchTextEditor.getWidth(), (float)SearchBar.this.mSearchTextEditor.getHeight(), 0));
            }
        });
    }
    
    public void startRecognition() {
        if (this.mRecognizing) {
            return;
        }
        if (!this.hasFocus()) {
            this.requestFocus();
        }
        if (this.mSpeechRecognitionCallback != null) {
            this.mSearchTextEditor.setText((CharSequence)"");
            this.mSearchTextEditor.setHint((CharSequence)"");
            this.mSpeechRecognitionCallback.recognizeSpeech();
            this.mRecognizing = true;
            return;
        }
        if (this.mSpeechRecognizer == null) {
            return;
        }
        if (this.getContext().checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            if (Build$VERSION.SDK_INT >= 23) {
                final SearchBarPermissionListener mPermissionListener = this.mPermissionListener;
                if (mPermissionListener != null) {
                    mPermissionListener.requestAudioPermission();
                    return;
                }
            }
            throw new IllegalStateException("android.permission.RECORD_AUDIO required for search");
        }
        this.mRecognizing = true;
        this.mSearchTextEditor.setText((CharSequence)"");
        final Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
        this.mSpeechRecognizer.setRecognitionListener((RecognitionListener)new RecognitionListener() {
            public void onBeginningOfSpeech() {
            }
            
            public void onBufferReceived(final byte[] array) {
            }
            
            public void onEndOfSpeech() {
            }
            
            public void onError(final int n) {
                switch (n) {
                    default: {
                        Log.d(SearchBar.TAG, "recognizer other error");
                        break;
                    }
                    case 9: {
                        Log.w(SearchBar.TAG, "recognizer insufficient permissions");
                        break;
                    }
                    case 8: {
                        Log.w(SearchBar.TAG, "recognizer busy");
                        break;
                    }
                    case 7: {
                        Log.w(SearchBar.TAG, "recognizer no match");
                        break;
                    }
                    case 6: {
                        Log.w(SearchBar.TAG, "recognizer speech timeout");
                        break;
                    }
                    case 5: {
                        Log.w(SearchBar.TAG, "recognizer client error");
                        break;
                    }
                    case 4: {
                        Log.w(SearchBar.TAG, "recognizer server error");
                        break;
                    }
                    case 3: {
                        Log.w(SearchBar.TAG, "recognizer audio error");
                        break;
                    }
                    case 2: {
                        Log.w(SearchBar.TAG, "recognizer network error");
                        break;
                    }
                    case 1: {
                        Log.w(SearchBar.TAG, "recognizer network timeout");
                        break;
                    }
                }
                SearchBar.this.stopRecognition();
                SearchBar.this.playSearchFailure();
            }
            
            public void onEvent(final int n, final Bundle bundle) {
            }
            
            public void onPartialResults(final Bundle bundle) {
                final ArrayList stringArrayList = bundle.getStringArrayList("results_recognition");
                if (stringArrayList != null) {
                    if (stringArrayList.size() != 0) {
                        final String s = stringArrayList.get(0);
                        String s2;
                        if (stringArrayList.size() > 1) {
                            s2 = stringArrayList.get(1);
                        }
                        else {
                            s2 = null;
                        }
                        SearchBar.this.mSearchTextEditor.updateRecognizedText(s, s2);
                    }
                }
            }
            
            public void onReadyForSpeech(final Bundle bundle) {
                SearchBar.this.mSpeechOrbView.showListening();
                SearchBar.this.playSearchOpen();
            }
            
            public void onResults(final Bundle bundle) {
                final ArrayList stringArrayList = bundle.getStringArrayList("results_recognition");
                if (stringArrayList != null) {
                    SearchBar.this.mSearchQuery = stringArrayList.get(0);
                    final SearchBar this$0 = SearchBar.this;
                    this$0.mSearchTextEditor.setText((CharSequence)this$0.mSearchQuery);
                    SearchBar.this.submitQuery();
                }
                SearchBar.this.stopRecognition();
                SearchBar.this.playSearchSuccess();
            }
            
            public void onRmsChanged(final float n) {
                int soundLevel;
                if (n < 0.0f) {
                    soundLevel = 0;
                }
                else {
                    soundLevel = (int)(n * 10.0f);
                }
                SearchBar.this.mSpeechOrbView.setSoundLevel(soundLevel);
            }
        });
        this.mListening = true;
        this.mSpeechRecognizer.startListening(intent);
    }
    
    public void stopRecognition() {
        if (!this.mRecognizing) {
            return;
        }
        this.mSearchTextEditor.setText((CharSequence)this.mSearchQuery);
        this.mSearchTextEditor.setHint((CharSequence)this.mHint);
        this.mRecognizing = false;
        if (this.mSpeechRecognitionCallback == null) {
            if (this.mSpeechRecognizer != null) {
                this.mSpeechOrbView.showNotListening();
                if (this.mListening) {
                    this.mSpeechRecognizer.cancel();
                    this.mListening = false;
                }
                this.mSpeechRecognizer.setRecognitionListener((RecognitionListener)null);
            }
        }
    }
    
    void submitQuery() {
        if (!TextUtils.isEmpty((CharSequence)this.mSearchQuery)) {
            final SearchBarListener mSearchBarListener = this.mSearchBarListener;
            if (mSearchBarListener != null) {
                mSearchBarListener.onSearchQuerySubmit(this.mSearchQuery);
            }
        }
    }
    
    void toggleRecognition() {
        if (this.mRecognizing) {
            this.stopRecognition();
        }
        else {
            this.startRecognition();
        }
    }
    
    void updateUi(final boolean b) {
        if (b) {
            this.mBarBackground.setAlpha(this.mBackgroundSpeechAlpha);
            if (this.isVoiceMode()) {
                this.mSearchTextEditor.setTextColor(this.mTextHintColorSpeechMode);
                this.mSearchTextEditor.setHintTextColor(this.mTextHintColorSpeechMode);
            }
            else {
                this.mSearchTextEditor.setTextColor(this.mTextColorSpeechMode);
                this.mSearchTextEditor.setHintTextColor(this.mTextHintColorSpeechMode);
            }
        }
        else {
            this.mBarBackground.setAlpha(this.mBackgroundAlpha);
            this.mSearchTextEditor.setTextColor(this.mTextColor);
            this.mSearchTextEditor.setHintTextColor(this.mTextHintColor);
        }
        this.updateHint();
    }
    
    public interface SearchBarListener
    {
        void onKeyboardDismiss(final String p0);
        
        void onSearchQueryChange(final String p0);
        
        void onSearchQuerySubmit(final String p0);
    }
    
    public interface SearchBarPermissionListener
    {
        void requestAudioPermission();
    }
}
