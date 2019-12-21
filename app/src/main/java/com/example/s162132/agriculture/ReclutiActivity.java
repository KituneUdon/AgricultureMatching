package com.example.s162132.agriculture;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static com.example.s162132.agriculture.MainActivity.InputStreamToString;

public class ReclutiActivity extends AppCompatActivity {

    Button buttonback,buttonsent, buttonStartTime, buttonFinishTime;
    EditText edTitle, edPlace, edContent, edBelongings, edClothes;
    TextView tvStartTime, tvFinishTime, tvDate;

    String title, date, time, startTime, finishTime,
            place, content, belongings, clothes, getID, str, value, ipAdress;
    int dayOfMonth, monthOfYear, year, hour, minute, flg,
            startHour, startMinute, finishHour, finishMinute;

    SharedPreferences pref;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    //日付設定時のリスナ登録
    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(android.widget.DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            //date = String.valueOf(monthOfYear + 1) + "月" + String.valueOf(dayOfMonth) + "日";
            date = String.valueOf(year) + "-" +  String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth);

            Calendar calendar   = Calendar.getInstance();
            int nowYear            = calendar.get(Calendar.YEAR);
            int nowMmonthOfYear     = calendar.get(Calendar.MONTH);
            int nowDayOfMonth      = calendar.get(Calendar.DAY_OF_MONTH);
            if (year <= nowYear && nowMmonthOfYear < monthOfYear && nowDayOfMonth < dayOfMonth) {
                Toast.makeText(ReclutiActivity.this, "過去の日付が設定されています。", Toast.LENGTH_SHORT).show();
                tvDate.clearComposingText();
            } else {
                tvDate.setText(date);
            }
        }
    };

    TimePickerDialog.OnTimeSetListener TimeSetListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time = String.valueOf(hourOfDay) + "時" + String.valueOf(minute) + "分";

            if (flg == 0) {
                tvStartTime.setText(time);
                startHour = hourOfDay;
                startMinute = minute;
                startTime = time;
            } else {
                tvFinishTime.setText(time);
                finishHour = hourOfDay;
                finishMinute = minute;
                finishTime = time;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recluti);

        Global IPAdress = (Global)this.getApplication();
        ipAdress = IPAdress.getIPAdress();

        pref = getSharedPreferences("ID", MODE_PRIVATE);

        buttonback = (Button)findViewById(R.id.buttonback);
        buttonsent = (Button)findViewById(R.id.buttonsent);
        buttonStartTime = (Button)findViewById(R.id.buttonStartTime);
        buttonFinishTime = (Button)findViewById(R.id.buttonFinishTime);

        edTitle = (EditText)findViewById(R.id.editTitle);
        edPlace = (EditText)findViewById(R.id.editplace);
        edContent = (EditText)findViewById(R.id.editNaiyou);
        edBelongings = (EditText)findViewById(R.id.editNaiyou);
        edClothes = (EditText)findViewById(R.id.editClothes);

        tvStartTime = (TextView)findViewById(R.id.textViewStartTime);
        tvFinishTime = (TextView)findViewById(R.id.textViewFinishTime);
        tvDate = (TextView)findViewById(R.id.textViewDate);

        BackListener bl = new BackListener();
        SentListener sl = new SentListener();
        SetTimeListener setTime = new SetTimeListener();

        buttonStartTime.setOnClickListener(setTime);
        buttonFinishTime.setOnClickListener(setTime);
        buttonback.setOnClickListener(bl);
        buttonsent.setOnClickListener(sl);

        findViewById(R.id.buttonCal).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //日付情報の初期設定(現在日時の取得)
                        Calendar calendar   = Calendar.getInstance();
                        year            = calendar.get(Calendar.YEAR);
                        monthOfYear     = calendar.get(Calendar.MONTH);
                        dayOfMonth      = calendar.get(Calendar.DAY_OF_MONTH);

                        //日付設定ダイアログの作成
                        datePickerDialog = new DatePickerDialog(ReclutiActivity.this,
                                DateSetListener, year, monthOfYear, dayOfMonth);

                        //日付設定ダイアログの表示
                        datePickerDialog.show();
                    }
                }
        );
    }

    class SetTimeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //時間情報の初期設定
            Calendar calendar   = Calendar.getInstance();
            hour            = calendar.get(Calendar.HOUR_OF_DAY);
            minute          = calendar.get(Calendar.MINUTE);

            //時間設定ダイアログの作成
            timePickerDialog = new TimePickerDialog(ReclutiActivity.this,
                    AlertDialog.THEME_HOLO_LIGHT, TimeSetListener, hour, minute, true);

            if (v == buttonStartTime) {
                flg = 0;
            } else {
                flg = 1;
            }

            //時間設定ダイアログの表示
            timePickerDialog.show();
        }
    }

    class BackListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish();
        }
    }

    class SentListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            SharedPreferences.Editor editor = pref.edit();
            getID = pref.getString("ID", "");

            title = edTitle.getText().toString();

            date = tvDate.getText().toString();
            place = edPlace.getText().toString();
            content = edContent.getText().toString();
            belongings = edBelongings.getText().toString();
            clothes = edClothes.getText().toString();

            str = "ID=" + getID + "&TITLE=" + title + "&DAY=" + date + "&STARTTIME=" + startTime + "&FINISHTIME=" + finishTime +
                    "&PLACE=" + place + "&CONTENT=" + content + "&BELONGINGS=" + belongings + "&CLOTHES=" + clothes;

            System.out.println(str);

            if (title.equals("") || date.equals("") || startTime.equals("") || finishTime.equals("") || place.equals("")
                    || content.equals("") || belongings.equals("") || clothes.equals("")) {
                Toast.makeText(ReclutiActivity.this, "入力していない項目があります。", Toast.LENGTH_SHORT).show();
            } else if (startHour > finishHour) {
                Toast.makeText(ReclutiActivity.this, "開始時刻が終了時刻より早い時間に設定されています。", Toast.LENGTH_SHORT).show();
                Toast.makeText(ReclutiActivity.this, String.valueOf(startHour) + ":" + String.valueOf(finishHour), Toast.LENGTH_SHORT).show();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        value = "";
                        try {
                            // サーバーにあるphpを実行
                            URL url = new URL("http://" + ipAdress + "/php/recru_insert3.php?" + str);
                            HttpURLConnection con = (HttpURLConnection) url.openConnection();

                            //phpの結果を
                            value = InputStreamToString(url.openStream());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if ((getID).equals(value)) {
                                        Toast.makeText(ReclutiActivity.this, "投稿に成功しました。", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(ReclutiActivity.this, "投稿に失敗しました。", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                    }
                }).start();
            }
        }
    }
}
