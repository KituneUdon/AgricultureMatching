package com.example.s162132.agriculture;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailActivity extends AppCompatActivity {

    String recru_id, setName, setDay, setStartTime, setFinishTime, setJob,
            setPlace, setContent, setBelongings, setClothes, setMail, ipAdress, setPhone;
    TextView teName, teDay, teStartTime, teFinishTime, tePlace, tvJob,
            teContent, teBelongings, teClothes, teMail, tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        teName = (TextView) findViewById(R.id.textViewName);
        teDay = (TextView) findViewById(R.id.textViewDay);
        teStartTime = (TextView) findViewById(R.id.textViewStartTime);
        teFinishTime = (TextView)findViewById(R.id.textViewFinishTime);
        tePlace = (TextView) findViewById(R.id.textViewPlace);
        teContent = (TextView) findViewById(R.id.textViewContent);
        teBelongings = (TextView) findViewById(R.id.textViewBelongings);
        teClothes = (TextView) findViewById(R.id.textViewClothes);
        teMail = (TextView) findViewById(R.id.textViewMail);
        tvPhone = (TextView) findViewById(R.id.textViewPhone);
        tvJob = (TextView)findViewById(R.id.textViewJob2);

        Intent intent = getIntent();
        recru_id = intent.getStringExtra("recru_id");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // サーバーにあるphpを実行
                    URL url = new URL("http://" + ipAdress + "/php/detail3.php?recru_id=" + recru_id);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //phpの結果を
                    String str = InputStreamToString(con.getInputStream());
                    System.out.println(str);
                    String[] value = str.split(",", 0);
                    setName = value[0];
                    setDay = value[1];
                    setStartTime = value[2];
                    setFinishTime = value[3];
                    setPlace = value[4];
                    setContent = value[5];
                    setBelongings = value[6];
                    setClothes = value[7];
                    setMail = value[8];
                    setPhone = value[9];
                    setJob = value[10];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            teName.setText(setName);
                            teDay.setText(setDay);
                            teStartTime.setText(setStartTime);
                            teFinishTime.setText(setFinishTime);
                            tePlace.setText(setPlace);
                            teContent.setText(setContent);
                            teBelongings.setText(setBelongings);
                            teClothes.setText(setClothes);
                            teMail.setText(setMail);
                            tvPhone.setText(setPhone);
                            tvJob.setText(setJob);
                        }
                    });
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }).start();

        findViewById(R.id.buttonBack).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );

        findViewById(R.id.buttonSentMail).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        // データタイプを指定
                        intent.setType("message/rfc822");
                        // 宛先を指定
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{setMail});
                        startActivity(intent);
                    }
                }
        );

        findViewById(R.id.buttonCall).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:" + setPhone));

                        startActivity(intent);
                    }
                }
        );
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
