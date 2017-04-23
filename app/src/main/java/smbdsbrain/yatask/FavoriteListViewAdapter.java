package smbdsbrain.yatask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Paul on 4/16/2017.
 */

public class FavoriteListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<Translation> translations;

    FavoriteListViewAdapter(Context context, ArrayList<Translation> translations)
    {
        this.context = context;
        this.translations = translations;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = inflater.inflate(R.layout.translation_fav_layout, viewGroup, false);
        }

        final Translation tr = translations.get(pos);

        ((TextView)view.findViewById(R.id.translation_number)).setText(translations.get(pos).getTranslateLangs());
        ((TextView)view.findViewById(R.id.translation_original_text)).setText(translations.get(pos).getOriginalText());
        ((TextView)view.findViewById(R.id.translation_translated_text)).setText(translations.get(pos).getTranslatedText());
        ((ImageButton)view.findViewById(R.id.add_favorite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseWorker db = new DatabaseWorker(context);
                db.addToFavorite(tr);
                HistoryViewPagerAdapter.flv.setAdapter(new FavoriteListViewAdapter(context, db.getFavorites()));
            }
        });
        return view;
    }
}
