package smbdsbrain.yatask;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Paul on 3/20/2017.
 */

public class HistoryViewPagerAdapter extends PagerAdapter {

    int lenght = 2;
    Context context;
    String[] titles;

    static ListView flv = null;
    static ListView hlv = null;

    HistoryViewPagerAdapter(Context context)
    {
        this.context = context;
        titles = context.getResources().getStringArray(R.array.history_titles);
    }

    @Override
    public int getCount() {
        return lenght;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position < titles.length ? position : 0];
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position){
        LayoutInflater inflater = LayoutInflater.from(context);
        int viewNum = R.layout.page_main;
        View view = null;
        if(position == 0)
        {
            view = inflater.inflate(R.layout.page_history, collection, false);
            ListView lv = (ListView)view.findViewById(R.id.history_listview);
            hlv = lv;
            DatabaseWorker db = new DatabaseWorker(context);
            lv.setAdapter(new FavoriteListViewAdapter(context, db.getFavorites()));
            HistoryViewPagerAdapter.hlv.setAdapter(new HistoryListViewAdapter(context, db.getHistory()));
        } else if(position == 1)
        {
            view = inflater.inflate(R.layout.page_favs, collection, false);
            //TODO: Add it to asyncTask
            ListView lv = (ListView)view.findViewById(R.id.favorites_listview);
            flv = lv;
            DatabaseWorker db = new DatabaseWorker(context);
            lv.setAdapter(new FavoriteListViewAdapter(context, db.getFavorites()));
        }

        ((ViewPager) collection).addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }
}