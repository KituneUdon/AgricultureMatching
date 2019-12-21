package com.example.s162132.agriculture;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.s162132.agriculture.R.id.buttonBosyuMy;
import static com.example.s162132.agriculture.R.id.buttonLoginMy;

public class MainActivity extends AppCompatActivity {

    TextView teName, teSex, teAge, teMail, teRecru, tvOccu;
    Button buttonBosyu, buttonLogin, buttonSearch;
    SharedPreferences pref;
    EditText edSearch, edSearchOther;
    private Spinner nSpinner;

    int n, nn;

    ListView listViewJoin, listViewOther;
    String getID, setName, setSex, setAge, setMail, ipAdress, setOccu, flg, result;
    String[] recru_id = new String[100];
    String[] title = new String[100];
    String[] name = new String[100];
    String[] day = new String[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TabHostの初期化および設定処理
        initTabs();

        getID = "";
        nSpinner = (Spinner) findViewById(R.id.spinnerOther);

        final Global IPAdress = (Global) this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        pref = getSharedPreferences("ID", MODE_PRIVATE);

        buttonBosyu = (Button) findViewById(buttonBosyuMy);
        buttonLogin = (Button) findViewById(buttonLoginMy);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);

        BosyuListener bl = new BosyuListener();
        LoginListener ll = new LoginListener();
        SearchListener bs = new SearchListener();

        buttonBosyu.setOnClickListener(bl);
        buttonLogin.setOnClickListener(ll);
        buttonSearch.setOnClickListener(bs);

        RegistrationListener rel = new RegistrationListener();
        findViewById(R.id.buttonnew).setOnClickListener(rel);

        listViewJoin = (ListView) findViewById(R.id.listViewJoin);
        listViewOther = (ListView) findViewById(R.id.listViewOther);

        listViewOther.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                TextView teRecru = (TextView) view.findViewById(R.id.textViewRecruOther);
                TextView tvFlg = (TextView) view.findViewById(R.id.textViewFlgOther);
                String flg = tvFlg.getText().toString();
                String str = teRecru.getText().toString();
                Intent intent = new Intent(MainActivity.this, OtherDetailActivity.class);
                intent.putExtra("ID", str);
                intent.putExtra("flg", flg);
                startActivity(intent);
            }
        });

        listViewJoin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                teRecru = (TextView) view.findViewById(R.id.textViewRecru);
                String str = teRecru.getText().toString();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("recru_id", str);
                startActivity(intent);
            }
        });

        teName = (TextView) findViewById(R.id.textViewName);
        teSex = (TextView) findViewById(R.id.textViewSex2);
        teAge = (TextView) findViewById(R.id.textViewAge2);
        teMail = (TextView) findViewById(R.id.textViewMail2);

        findViewById(R.id.buttonIP).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = ((EditText) findViewById(R.id.editTextIP)).getText().toString();
                        IPAdress.setIPAdress(str);
                    }
                }
        );

        findViewById(R.id.buttonOffering).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getID.equals("")) {
                            Toast.makeText(MainActivity.this, "ログインしてください。", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, OfferingActivity.class);
                            startActivity(intent);
                        }
                    }
                }
        );

        findViewById(R.id.buttonHistory).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getID.equals("")) {
                            Toast.makeText(MainActivity.this, "ログインしてください。", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                            startActivity(intent);
                        }

                    }
                }
        );

        findViewById(R.id.buttonSearchOther).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    edSearchOther = (EditText) findViewById(R.id.editTextSearchOther);
                                    String keyword = edSearchOther.getText().toString();
                                    // サーバーにあるphpを実行
                                    URL url = new URL("http://" + ipAdress + "/php/" + flg + "_search.php?keyword=" + keyword);
                                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                                    //phpの結果を
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
                                            listViewOther = (ListView) findViewById(R.id.listViewOther);
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
                                            listViewOther.setAdapter(simp);
                                        }
                                    });
                                } catch (Exception ex) {
                                    System.out.println(ex);
                                }
                            }
                        }).start();
                    }
                }
        );

        // リスナーを登録
        nSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String item = (String) spinner.getSelectedItem();

                switch (position) {
                    case 0:
                        flg = "trans";
                        break;
                    case 1:
                        flg = "rent";
                        break;
                    case 2:
                        flg = "advice";
                        break;
                    case 3:
                        flg = "other";
                }
                List(flg);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    protected void onStart() {
        TextView tvJob1, tvJob2, tvNameMy, tvSex1, tvAge1, tvMail1;

        final Global IPAdress = (Global) this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        getID = "";
        teName.setText("");
        teSex.setText("");
        teAge.setText("");
        teMail.setText("");
        getID = pref.getString("ID", "");
        buttonLogin = (Button) findViewById(buttonLoginMy);

        teName = (TextView) findViewById(R.id.textViewName);
        teSex = (TextView) findViewById(R.id.textViewSex2);
        teAge = (TextView) findViewById(R.id.textViewAge2);
        teMail = (TextView) findViewById(R.id.textViewMail2);
        tvOccu = (TextView) findViewById(R.id.textViewJob2);
        tvNameMy = (TextView) findViewById(R.id.textViewNameMy);
        tvJob1 = (TextView) findViewById(R.id.textViewJob1);
        tvJob2 = (TextView) findViewById(R.id.textViewJob2);
        tvAge1 = (TextView) findViewById(R.id.textViewAge1);
        tvSex1 = (TextView) findViewById(R.id.textViewSex1);
        tvMail1 = (TextView) findViewById(R.id.textViewMail);

        if (getID.equals("")) {
            buttonLogin.setText("ログイン");
            teName.setText("ようこそ、ゲストさん");
            tvNameMy.setVisibility(View.GONE);
            tvSex1.setVisibility(View.INVISIBLE);
            tvAge1.setVisibility(View.INVISIBLE);
            tvMail1.setVisibility(View.INVISIBLE);
            tvJob1.setVisibility(View.GONE);
            tvJob2.setVisibility(View.GONE);
            teSex.setVisibility(View.INVISIBLE);
            teAge.setVisibility(View.INVISIBLE);
            teMail.setVisibility(View.INVISIBLE);
        } else {
            tvNameMy.setVisibility(View.VISIBLE);
            tvSex1.setVisibility(View.VISIBLE);
            tvAge1.setVisibility(View.VISIBLE);
            tvMail1.setVisibility(View.VISIBLE);
            tvJob1.setVisibility(View.VISIBLE);
            tvJob2.setVisibility(View.VISIBLE);
            teSex.setVisibility(View.VISIBLE);
            teAge.setVisibility(View.VISIBLE);
            teMail.setVisibility(View.VISIBLE);

            //マイページの表示情報の取得
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // サーバーにあるphpを実行
                        URL url = new URL("http://" + ipAdress + "/php/user_info.php?ID=" + getID);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        //phpの結果を
                        String str = InputStreamToString(url.openStream());
                        String[] value = str.split(",", 0);
                        setName = value[0];
                        setSex = value[1];
                        setAge = value[2];
                        setMail = value[3];
                        setOccu = value[4];

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                teName.setText(setName);
                                teSex.setText(setSex);
                                teAge.setText(setAge);
                                teMail.setText(setMail);
                                tvOccu.setText(setOccu);
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }).start();
            buttonLogin.setText("ログアウト");
        }


        //リスト表示
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // サーバーにあるphpを実行
                    URL url = new URL("http://" + ipAdress + "/php/join_list_day.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //phpの結果を
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
                            }

                            SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.join_list,
                                    new String[]{"recru_id", "title", "name", "day"},
                                    new int[]{R.id.textViewRecru, R.id.textViewTitle_join,
                                            R.id.textViewName_join, R.id.textViewDay_join});
                            listViewJoin.setAdapter(simp);
                        }
                    });
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
        super.onStart();
    }

    protected void initTabs() {
        try {
            TabHost tabHost = (TabHost) findViewById(R.id.tabhost);

            tabHost.setup();
            TabHost.TabSpec spec;

            // Tab1
            spec = tabHost.newTabSpec("Tab1")
                    .setIndicator("手伝いに参加")
                    .setContent(R.id.activity_join);
            tabHost.addTab(spec);

            // Tab2
            spec = tabHost.newTabSpec("Tab2")
                    .setIndicator("その他の募集")
                    .setContent(R.id.activity_other);
            tabHost.addTab(spec);

            // Tab3
            spec = tabHost.newTabSpec("Tab3")
                    .setIndicator("マイページ")
                    .setContent(R.id.activity_my_page);
            tabHost.addTab(spec);

            tabHost.setCurrentTab(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    //検索機能
    class SearchListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        edSearch = (EditText) findViewById(R.id.editTextSearch);
                        String keyword = edSearch.getText().toString();
                        // サーバーにあるphpを実行
                        URL url = new URL("http://" + ipAdress + "/php/search.php?keyword=" + keyword);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();

                        //phpの結果を
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
                                listViewJoin = (ListView) findViewById(R.id.listViewJoin);
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
                                }

                                SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.join_list,
                                        new String[]{"recru_id", "title", "name", "day"},
                                        new int[]{R.id.textViewRecru, R.id.textViewTitle_join, R.id.textViewName_join, R.id.textViewDay_join});
                                listViewJoin.setAdapter(simp);
                                listViewJoin.deferNotifyDataSetChanged();
                            }
                        });
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            }).start();
        }
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

    class BosyuListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (getID.equals("")) {
                Toast.makeText(MainActivity.this, "ログインしてください。", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, ReclutiActivity.class);
                startActivity(intent);
            }
        }
    }

    class LoginListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (getID.equals("")) {
                Intent intent = new Intent(MainActivity.this, LogLogin.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                startActivity(intent);
            }
        }
    }

    class RegistrationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    void List(String flg) {
        final String fileName = flg;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // サーバーにあるphpを実行
                    URL url = new URL("http://" + ipAdress + "/php/" + fileName + "_list.php");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //phpの結果を
                    result = InputStreamToString(url.openStream());

                    String[] value = result.split(",", 0);
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
                            listViewOther = (ListView) findViewById(R.id.listViewOther);
                            ArrayList<HashMap<String, String>> list_data = new ArrayList<HashMap<String, String>>();
                            HashMap<String, String> hashTmp = new HashMap<String, String>();

                            int count = 1;

                            while (count < nn) {
                                hashTmp.clear();
                                hashTmp.put("recru_id", recru_id[count]);
                                hashTmp.put("title", title[count]);
                                hashTmp.put("name", name[count]);
                                hashTmp.put("flg", fileName);
                                list_data.add(new HashMap<String, String>(hashTmp));
                                count++;
                            }

                            SimpleAdapter simp = new SimpleAdapter(getApplicationContext(), list_data, R.layout.other_list,
                                    new String[]{"recru_id", "title", "name", "flg"},
                                    new int[]{R.id.textViewRecruOther, R.id.textViewTitleOther, R.id.textViewNameOther, R.id.textViewFlgOther});
                            listViewOther.setAdapter(simp);
                        }
                    });
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();
    }
}
