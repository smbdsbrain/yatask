package smbdsbrain.yatask;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import static android.R.attr.editable;

/**
 * Created by Paul on 3/20/2017.
 */

public class MainPagerAdapter extends PagerAdapter {

    int lenght = 3;
    Context context;
    FragmentManager fm;
    static String langs = "";

    MainPagerAdapter(Context context)
    {
        this.context = context;
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
    public Object instantiateItem(ViewGroup collection, int position){
        LayoutInflater inflater = LayoutInflater.from(context);
        int viewNum = R.layout.page_main;
        View view = null;
        if(position == 0)
        {
            view = inflater.inflate(R.layout.page_main, collection, false);
            final EditText et = (EditText) view.findViewById(R.id.phrase);
            et.addTextChangedListener(new TextWatcher() {

                AsyncTranslation at;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //Toast.makeText(context, editable.toString(), Toast.LENGTH_LONG).show();
                    if(at != null)
                        at.cancel(true);

                    at = new AsyncTranslation((Activity) context, context.getString(R.string.api_key),
                            context.getString(R.string.dict_api_key), langs);
                    at.execute(editable.toString());
                }
            });
            final Spinner spinner_from = (Spinner)view.findViewById(R.id.spinner_from_tr);
            final Spinner spinner_to = (Spinner)view.findViewById(R.id.spinner_to_tr);

            final SharedPreferences mSettings = context.getSharedPreferences("mysettings", Context.MODE_PRIVATE);

            int i_from = mSettings.getInt("tr_from", 60);
            int i_to = mSettings.getInt("tr_to", 3);

            final String[] langsss = context.getResources().getStringArray(R.array.lang_codes);
            MainPagerAdapter.langs = langsss[i_from] + "-" + langsss[i_to];

            spinner_from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    MainPagerAdapter.langs = langsss[i] + "-" + langsss[spinner_to.getSelectedItemPosition()];
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt("tr_from", i);
                    editor.apply();

                    AsyncTranslation at = new AsyncTranslation((Activity) context, context.getString(R.string.api_key),
                            context.getString(R.string.dict_api_key), langs);
                    at.execute(et.getText().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner_to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //Spinner _spinner_from = (Spinner)view.findViewById(R.id.spinner_from_tr);
                    MainPagerAdapter.langs = langsss[spinner_from.getSelectedItemPosition()] + "-" + langsss[i];
                    SharedPreferences.Editor editor = mSettings.edit();
                    editor.putInt("tr_to", i);
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinner_from.setSelection(i_from);
            spinner_to.setSelection(i_to);

            ImageButton switch_langs = (ImageButton)view.findViewById(R.id.switch_lang);
            switch_langs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int from = spinner_from.getSelectedItemPosition();
                    int to = spinner_to.getSelectedItemPosition();

                    spinner_from.setSelection(to);
                    spinner_to.setSelection(from);
                }
            });

        } else if(position == 1)
        {
            view = inflater.inflate(R.layout.page_history_and_favs, collection, false);
            ViewPager vp = (ViewPager) view.findViewById(R.id.history_viewpager);
            PagerAdapter pagerAdapter = new HistoryViewPagerAdapter(context);
            vp.setAdapter(pagerAdapter);
            TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
            //tabLayout.setTabTextColors(context.getResources().getColorStateList(R.color.tabTitle));
            tabLayout.setTabsFromPagerAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(vp);
        } else if(position == 2)
        {
            view = inflater.inflate(R.layout.page_settings, collection, false);
            PrefFragment frag1 = new PrefFragment();
            //final EditText et = ;
            FragmentTransaction fTrans;
            fTrans = fm.beginTransaction();
            fTrans.add(R.id.frgmCont, frag1);
            fTrans.commit();
        }

        ((ViewPager) collection).addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }
}
