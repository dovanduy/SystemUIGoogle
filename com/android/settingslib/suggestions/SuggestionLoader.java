// 
// Decompiled by Procyon v0.5.36
// 

package com.android.settingslib.suggestions;

import android.util.Log;
import android.content.Context;
import android.service.settings.suggestions.Suggestion;
import java.util.List;
import com.android.settingslib.utils.AsyncLoader;

@Deprecated
public class SuggestionLoader extends AsyncLoader<List<Suggestion>>
{
    private final SuggestionController mSuggestionController;
    
    public SuggestionLoader(final Context context, final SuggestionController mSuggestionController) {
        super(context);
        this.mSuggestionController = mSuggestionController;
    }
    
    public List<Suggestion> loadInBackground() {
        final List<Suggestion> suggestions = this.mSuggestionController.getSuggestions();
        if (suggestions == null) {
            Log.d("SuggestionLoader", "data is null");
        }
        else {
            final StringBuilder sb = new StringBuilder();
            sb.append("data size ");
            sb.append(suggestions.size());
            Log.d("SuggestionLoader", sb.toString());
        }
        return suggestions;
    }
    
    @Override
    protected void onDiscardResult(final List<Suggestion> list) {
    }
}
