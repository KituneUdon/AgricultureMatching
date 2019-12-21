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

public class OtherDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvContent, tvName, tvJob, tvMail, tvPhone;
    String ipAdress, id, flg, setTitle, setName, setJob, setContent, setMail, setPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_detail);

        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        tvTitle = (TextView)findViewById(R.id.textViewTitle);
        tvContent = (TextView)findViewById(R.id.textViewContent);
        tvName = (TextView)findViewById(R.id.textViewName);
        tvJob = (TextView)findViewById(R.id.textViewJob);
        tvMail = (TextView)findViewById(R.id.textViewMail) ;
        tvPhone = (TextView)findViewById(R.id.textViewPhone) ;

        Intent intent = getIntent();
        id = intent.getStringExtra("ID");
        flg = intent.getStringExtra("flg");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // サーバーにあるphpを実行
                    URL url = new URL("http://" + ipAdress + "/php/" + flg + "_detail.php?ID=" + id);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    //phpの結果を
                    String str = InputStreamToString(con.getInputStream());
                    System.out.println(str);
                    String[] value = str.split(",", 0);
                    setTitle = value[0];
                    setName = value[1];
                    setJob = value[2];
                    setContent = value[3];
                    setMail = value[4];
                    setPhone = value[5];

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvTitle.setHorizontallyScrolling(false);
                            tvTitle.setText(setTitle);
                            tvName.setText(setName);
                            tvJob.setText(setJob);
                            tvContent.setText(setContent);
                            tvMail.setText(setMail);
                            tvPhone.setText(setPhone);
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
