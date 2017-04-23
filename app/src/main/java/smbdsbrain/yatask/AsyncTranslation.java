package smbdsbrain.yatask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Paul on 4/10/2017.
 */

public class AsyncTranslation extends AsyncTask<String, Void, ArrayList<Translation>> {

    String api_key;
    String dict_api_key;
    String langs;
    Activity context;

    boolean running = true;

    public AsyncTranslation(Activity context, String api_key, String dict_api_key, String langs)
    {
        this.context = context;
        this.api_key = api_key;
        this.dict_api_key = dict_api_key;
        this.langs = langs;
    }


    private String downloadString(String urlString) {

        try {
            Thread.sleep(75);
        }
        catch (Exception exc)
        {


        }
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String result = "";

        try {
            URL url = new URL(urlString);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            result = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected ArrayList<Translation> doInBackground(String...  strings) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean lunch = sp.getBoolean("lunch", false);

        if(lunch) {
            ArrayList<Translation> translations = new ArrayList<>();
            Translation lunchTr = new Translation(context.getResources().getString(R.string.lunch_original), context.getResources().getString(R.string.lunch_translated), "ru-ru");
            for(int i = 0; i < 10; i++)
                translations.add(lunchTr);
            return translations;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        DatabaseWorker dbworker = new DatabaseWorker(context);
        ArrayList<Translation> translations = dbworker.getCache(strings[0], langs);
        boolean caching = false;

        if(translations.size() == 0) {

            caching = true;
            //tries translate with Yandex Dictionary
            resultJson = downloadString("https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=" +
                    dict_api_key +
                    "&lang=" + langs +
                    "&text=" + URLEncoder.encode(strings[0]));

            String[] res = new String[1];
            res[0] = resultJson;

            //ArrayList<Translation> translations = new ArrayList<>();

            //parsing ya dict result
            try {
                Thread.sleep(500);
                JSONObject dictRes = new JSONObject(resultJson);
                JSONArray means = dictRes.getJSONArray("def");
                //ArrayList<Translation> trs = /*new Translation[means.length()]*/ new ArrayList<Translation>();
                for (int i = 0; i < means.length(); i++) {
                    JSONObject tr = means.getJSONObject(i);
                    String translated = "";
                    JSONArray _trs = tr.getJSONArray("tr");

                    for (int j = 0; j < _trs.length(); j++) {
                        translated += _trs.getJSONObject(j).getString("text");
                        try {
                            JSONArray syn = _trs.getJSONObject(j).getJSONArray("syn");
                            if (syn.length() != 0)
                                translated += ", ";
                            for (int k = 0; k < syn.length(); k++) {
                                translated += syn.getJSONObject(k).getString("text") + (k != syn.length() - 1 ? ", " : "");
                            }
                        } catch (Exception exc) {

                        }
                        translations.add(new Translation(tr.getString("text"), translated, langs));
                        translated = "";
                    }
                }
                //translations = (Translation[]) trs.toArray();
            } catch (Exception e) {

            }
            try {
                if (translations == null || translations.size() == 0) {
                    resultJson = downloadString("https://translate.yandex.net/api/v1.5/tr.json/translate?lang=" + langs +
                            "&key=" + api_key +
                            "&text=" + URLEncoder.encode(strings[0]));
                    try {
                        JSONArray text = new JSONObject(resultJson).getJSONArray("text");
                        ArrayList<Translation> trs = new ArrayList<>();
                        for (int i = 0; i < text.length(); i++)
                            translations.add(new Translation(strings[0], text.getString(i), langs));
                        //translations = (Translation[]) trs.toArray();
                    } catch (Exception e) {

                    }
                }
            } catch (Exception exc) {
            }
        }

        for(int i = 0; i < translations.size(); i++) {
            if(running)
                dbworker.addToHistory(translations.get(i));
            if(caching)
                dbworker.addToCache(strings[0], translations.get(i));
        }

        return translations;
    }

    @Override
    protected void onCancelled() {
        running = false;
    }


    protected void onPostExecute(ArrayList<Translation> result) {
        if(result == null || result.size() == 0)
            return;

        /* //depreciated :D
        TextView tv = (TextView) context.findViewById(R.id.textView4);
        tv.setText(result.get(0).getTranslatedText());*/

        ListView lv = (ListView) context.findViewById(R.id.translations_listview);
        lv.setAdapter(new TranslationsAdapter(context, result));

        DatabaseWorker databaseWorker =  new DatabaseWorker(context);
        HistoryViewPagerAdapter.hlv.setAdapter(new HistoryListViewAdapter(context, databaseWorker.getHistory()));
    }
}
