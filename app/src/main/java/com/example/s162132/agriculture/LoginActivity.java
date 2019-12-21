package com.example.s162132.agriculture;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.s162132.agriculture.MainActivity.InputStreamToString;
import static com.example.s162132.agriculture.R.id.editTextAGe;
import static com.example.s162132.agriculture.R.id.editTextID;
import static com.example.s162132.agriculture.R.id.editTextMail;
import static com.example.s162132.agriculture.R.id.editTextPass;
import static com.example.s162132.agriculture.R.id.editTextPass2;
import static com.example.s162132.agriculture.R.id.editTextPhone;
import static com.example.s162132.agriculture.R.id.editTextUser;

public class LoginActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    String id, name, pass, age, sex, mail, value, knownID, str, pass2, ipAdress, occu, phone;
    EditText edId, edName, edPass, edPass2, edAge, edMail, edPhone;
    int flg;

    String mailFormat = "^[a-zA-Z0-9!#$%&'_`/=~\\*\\+\\-\\?\\^\\{\\|\\}]+(\\.[a-zA-Z0-9!#$%&'_`/" +
            "=~\\*\\+\\-\\?\\^\\{\\|\\}]+)*+(.*)@[a-zA-Z0-9][a-zA-Z0-9\\-]*(\\.[a-zA-Z0-9\\-]+)+$";

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        sex = "";
        pref = getSharedPreferences("ID", MODE_PRIVATE);

        RegistrationListener rl = new RegistrationListener();
        findViewById(R.id.buttonRegi).setOnClickListener(rl);

        ((RadioGroup)findViewById(R.id.radioGroup)).setOnCheckedChangeListener(this);
        ((RadioGroup)findViewById(R.id.radioGroupOccupation)).setOnCheckedChangeListener(this);

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
        if (checkedId == R.id.radioButtonMale) {
            sex = "男";
        } else if (checkedId == R.id.radioButtonFemale){
            sex = "女";
        }
        if (checkedId == R.id.radioButtonAgri) {
            occu = "農家";
        } else if (checkedId == R.id.radioButtonOther){
            occu = "農家以外";
        }
    }

    class RegistrationListener implements View.OnClickListener{
        final Handler handler=new Handler() {
            public void handleMessage(Message msg) {
                if (flg == 0) {
                    if (knownID == "") {
                        //同じIDが存在しない
                        flg = 1;
                    } else {
                        Toast.makeText(LoginActivity.this, "既に同じIDが存在します。", Toast.LENGTH_SHORT).show();
                        flg = -1;
                    }
                } else if (flg == 1){
                    if (value.equals(id)) {
                        Toast.makeText(LoginActivity.this, "登録に成功しました。", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("ID", value);
                        editor.commit();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "エラーが発生しました。", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        @Override
        public void onClick(View v) {
            edId = (EditText)findViewById(editTextID);
            edName = (EditText)findViewById(editTextUser);
            edPass = (EditText)findViewById(editTextPass);
            edPass2 = (EditText)findViewById(editTextPass2);
            edAge = (EditText)findViewById(editTextAGe);
            edMail = (EditText)findViewById(editTextMail);
            edPhone = (EditText)findViewById(editTextPhone);

            id = edId.getText().toString();
            name = edName.getText().toString();
            pass = edPass.getText().toString();
            pass2 = edPass2.getText().toString();
            age = edAge.getText().toString();
            mail = edMail.getText().toString();
            phone = edPhone.getText().toString();

            if (id.equals("") || name.equals("") || pass.equals("") || age.equals("") || occu.equals("")
                    || mail.equals("") || sex.equals("") || phone.equals("")){
                Toast.makeText(LoginActivity.this, "入力していない項目があります。", Toast.LENGTH_SHORT).show();
            } else if (!(pass.equals(pass2))) {
                Toast.makeText(LoginActivity.this, "パスワードが一致しません。", Toast.LENGTH_SHORT).show();
            } else if (!mail.matches(mailFormat)) {
                Toast.makeText(LoginActivity.this, "メールアドレスの形式が間違っています。", Toast.LENGTH_SHORT).show();
            } else {
                str = "ID=" + id + "&name=" + name + "&pass="
                        + pass + "&sex=" + sex + "&age=" + age + "&occupation=" +
                        occu + "&mail=" + mail + "&phone=" + phone;
                System.out.println(str);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        flg = 0;
                        value = "";

                        try {
                            // サーバーにあるphpを実行
                            URL url = new URL("http://" + ipAdress + "/php/user_list_check.php?ID=" + id);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            //phpの結果を
                            //value = InputStreamToString(con.getInputStream());
                            knownID = InputStreamToString(url.openStream());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (knownID == "") {
                                        //同じIDが存在しない
                                        flg = 1;
                                    } else {
                                        Toast.makeText(LoginActivity.this, "既に同じIDが存在します。", Toast.LENGTH_SHORT).show();
                                        flg = -1;
                                    }
                                }
                            });
                            } catch (Exception ex) {
                            System.out.println(ex);
                        }

                        if (flg != -1) {
                            try {
                                // サーバーにあるphpを実行
                                URL url = new URL("http://" + ipAdress + "/php/user_insert.php?" + str);
                                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                                //phpの結果を
                                //value = InputStreamToString(con.getInputStream());
                                value = InputStreamToString(url.openStream());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (value.equals(id)) {
                                            Toast.makeText(LoginActivity.this, "登録に成功しました。", Toast.LENGTH_SHORT).show();
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("ID", value);
                                            editor.commit();
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "エラーが発生しました。", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception ex) {
                                System.out.println(ex);
                            }
                        }
                    }
                }).start();
            }
        }
    }
}
