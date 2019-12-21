package com.example.s162132.agriculture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoryActivity extends AppCompatActivity
        implements RadioGroup.OnCheckedChangeListener{

    SharedPreferences pref;
    String getID, ipAdress, flg, flg2;
    int n, nn;
    ListView listViewHistory;
    TextView teRecru;

    String[] recru_id = new String[100];
    String[] title = new String[100];
    String[] name = new String[100];
    String[] day = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        pref = getSharedPreferences("ID", MODE_PRIVATE);
        getID = pref.getString("ID", "");
        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        flg = "recru";
        flg2 = "0";

        ((RadioGroup)findViewById(R.id.radioGroup2)).setOnCheckedChangeListener(this);
        listViewHistory = (ListView)findViewById(R.id.listViewHistory);
        Spinner nSpinner = (Spinner)findViewById(R.id.spinner3) ;

        // リスナーを登録
        nSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();

                switch (position) {
                    case 0:
                        flg = "recru";
                        break;
                    case 1:
                        flg = "trans";
                        break;
                    case 2:
                        flg = "rent";
                        break;
                    case 3:
                        flg = "advice";
                        break;
                    case 4:
                        flg = "other";
                }
                ListShow(flg, flg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                teRecru = (TextView) view.findViewById(R.id.textViewRecru);
                String str = teRecru.getText().toString();
                Intent intent = new Intent(HistoryActivity.this, EditingActivity.class);
                intent.putExtra("recru_id", str);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonBack).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radioButtonAll) {
            flg2 = "0";
        } else {
            flg2 = "1";
        }
        ListShow(flg, flg2);
    }

    public void ListShow(final String flg, final String flg2) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String strUrl = null;
                    if (flg2.equals("0")) {
                        strUrl = "http://" + ipAdress + "/php/" + flg + "_all.php?ID=" + getID;
                    } else {
                        strUrl = "http://" + ipAdress + "/php/" + flg + "_now.php?ID=" + getID;
                    }
                    //URL url = new URL("http://" + ipAdress + "/php/join_list_day.php");
                    URL url = new URL(strUrl);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    if (flg.equals("recru")) {
                        String str = InputStreamToString(url.openStream());
                        String[] value = str.split(",", 0);
                        n = 0;
                        nn = 1;
                        while (!(value[n].equals("終了"))) {
                            if (value[n].equals("</br>")) {
                                nn++;
                                n++;
                            } else {
                                recru_id[nn] = value[n];
                                n++;
                                title[nn] = value[n];
                                n++;
                                name[nn] = value[n];
                                n++;
                                day[nn] = value[n];
                                n++;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (flg.equals("recru")) {
                                    ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
                                    HashMap<String, String> hashTmp = new HashMap<String, String>();

                                    int count = 1;
                                    while (count < nn) {
                                        hashTmp.clear();
                                        hashTmp.put("recru_id", recru_id[count]);
                                        hashTmp.put("title", title[count]);
                                        hashTmp.put("name", name[count]);
                                        hashTmp.put("day", day[count]);
                                        list_data.add(new HashMap<String, String>(hashTmp));
                                        count++;
                                        System.out.println("count:" + count);
                                    }

                                    SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.join_list,
                                            new String[]{"recru_id", "title", "name", "day"},
                                            new int[]{R.id.textViewRecru, R.id.textViewTitle_join,
                                                    R.id.textViewName_join, R.id.textViewDay_join});
                                    listViewHistory.setAdapter(simp);
                                } else {

                                }
                            }
                        });
                    } else {
                        String str = InputStreamToString(url.openStream());
                        String[] value = str.split(",", 0);
                        n = 0;
                        nn = 1;
                        while (!(value[n].equals("終了"))) {
                            if (value[n].equals("</br>")) {
                                nn++;
                                n++;
                            } else {
                                recru_id[nn] = value[n];
                                n++;
                                title[nn] = value[n];
                                n++;
                                name[nn] = value[n];
                                n++;
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
                                HashMap<String, String> hashTmp = new HashMap<String, String>();

                                int count = 1;

                                while (count < nn) {
                                    hashTmp.clear();
                                    hashTmp.put("recru_id", recru_id[count]);
                                    hashTmp.put("title", title[count]);
                                    hashTmp.put("name", name[count]);
                                    hashTmp.put("flg", flg);
                                    list_data.add(new HashMap<String, String>(hashTmp));
                                    count++;
                                }

                                SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.other_list,
                                        new String[]{"recru_id", "title", "name", "flg"},
                                        new int[]{R.id.textViewRecruOther, R.id.textViewTitleOther, R.id.textViewNameOther, R.id.textViewFlgOther});
                                listViewHistory.setAdapter(simp);
                            }
                        });
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
    }

    // InputStream -> String
    static String InputStreamToString(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
