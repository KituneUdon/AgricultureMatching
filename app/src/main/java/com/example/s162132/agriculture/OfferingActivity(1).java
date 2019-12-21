package com.example.s162132.agriculture;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.AdapterView.OnItemSelectedListener;

public class OfferingActivity extends AppCompatActivity {

    private Spinner nSpinner;

    String ipAdress, getID, content, name, flg, result, title;
    SharedPreferences pref;
    EditText etContent, etTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offering);

        nSpinner = (Spinner) findViewById(R.id.spinner);

        final Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();
        pref = getSharedPreferences("ID", MODE_PRIVATE);
        getID = pref.getString("ID", "");

        etContent = (EditText)findViewById(R.id.editTextContent);

        findViewById(R.id.buttonBack).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        // リスナーを登録
        nSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.buttonSent).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (content.equals("")) {
                            Toast.makeText(OfferingActivity.this, "入力されていません。", Toast.LENGTH_SHORT).show();
                        } else {
                            SentDB(flg);
                        }
                    }
                }
        );
    }

    void SentDB(String flg){
        name = flg;
        content = etContent.getText().toString();
        title = etTitle.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String val = "ID=" + getID + "&title=" + title + "&content=" + content;
                    // サーバーにあるphpを実行
                    URL url = new URL("http://" + ipAdress + "/php/" + name + "_insert.php?" + val);
                    System.out.println(name);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //phpの結果を
                    result = InputStreamToString(url.openStream());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result.equals(getID)) {
                                Toast.makeText(OfferingActivity.this,
                                        "投稿に成功しました。", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(OfferingActivity.this,
                                        "投稿に失敗しました。", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
