package com.example.s162132.agriculture;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.s162132.agriculture.MainActivity.InputStreamToString;

public class LogLogin extends AppCompatActivity {

    String value, ipAdress;
    Button back;
    EditText ID,pass;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_login);

        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        pref = getSharedPreferences("ID", MODE_PRIVATE);
        editor = pref.edit();

        back = (Button)findViewById(R.id.buttonback);
        BackListener bl = new BackListener();
        back.setOnClickListener(bl);

        Button login = (Button)findViewById(R.id.buttonLogin);
        LoginListener log = new LoginListener();
        login.setOnClickListener(log);

        ID = (EditText) findViewById(R.id.editTextID);
        pass = (EditText)findViewById(R.id.editTextPass);
    }

    class LoginListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (ID.getText().toString().equals("") || pass.getText().toString().equals("")) {
                Toast.makeText(LogLogin.this, "IDまたはパスワードが未入力です。", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            value = "0";

                            String str = "ID=" + ID.getText().toString();
                            str += "&pass=" + pass.getText().toString();

                            // サーバーにあるphpを実行
                            URL url = new URL("http://" + ipAdress + "/php/login.php?" + str);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            //phpの結果を
                            //value = InputStreamToString(con.getInputStream());
                            value = InputStreamToString(url.openStream());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (value == "") {
                                        Toast.makeText(LogLogin.this, "IDまたはパスワードが間違っています。",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LogLogin.this, "ログイン成功", Toast.LENGTH_SHORT).show();
                                        editor.putString("ID", value);
                                        editor.commit();
                                        finish();
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            System.out.println(ex);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LogLogin.this, "エラーが発生しました。", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish();
        }
    }
}
