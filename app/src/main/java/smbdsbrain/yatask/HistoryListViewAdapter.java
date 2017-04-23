package smbdsbrain.yatask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Paul on 4/22/2017.
 */

public class HistoryListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Translation> translations;
    int[] favHashCodes;
    HistoryListViewAdapter(Context context, ArrayList<Translation> translations)
    {
        this.context = context;
        this.translations = translations;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        DatabaseWorker dbworker = new DatabaseWorker(context);
        favHashCodes = dbworker.getFavHashCodes();
    }


    @Override
    public int getCount() {
        return translations.size();
    }

    @Override
    public Object getItem(int i) {
        return translations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.translation_history_layout, viewGroup, false);
        }

        final Translation tr = translations.get(pos);

        ((TextView)view.findViewById(R.id.translation_number)).setText(translations.get(pos).getTranslateLangs());
        ((TextView)view.findViewById(R.id.translation_original_text)).setText(translations.get(pos).getOriginalText());
        ((TextView)view.findViewById(R.id.translation_translated_text)).setText(translations.get(pos).getTranslatedText());
        final ImageButton favButton = ((ImageButton)view.findViewById(R.id.add_his_favorite));
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseWorker db = new DatabaseWorker(context);
                if(db.addToFavorite(tr))
                    favButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_24dp));
                else
                    favButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp));
                HistoryViewPagerAdapter.flv.setAdapter(new FavoriteListViewAdapter(context, db.getFavorites()));
            }
        });

        if(Arrays.binarySearch(favHashCodes, translations.get(pos).hashCode()) >= 0)
            favButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_black_24dp));
        else
            favButton.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border_black_24dp));
        return view;
    }
}