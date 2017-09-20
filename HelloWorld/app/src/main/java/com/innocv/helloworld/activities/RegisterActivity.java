package com.innocv.helloworld.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innocv.helloworld.R;
import com.innocv.helloworld.model.User;
import com.innocv.helloworld.server.HelloWorldAPI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.innocv.helloworld.activities.LoginActivity.BASE_URL;

/**
 *
 * Register activity
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText _usernameEditText;
    private EditText _birthdateEditText;
    private EditText _birthhourEditText;
    private Calendar _calendar;
    private int _year, _month, _day, _hours, _minutes, _seconds;
    private String _birthDate, _birthHour;
    private HelloWorldAPI _restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _usernameEditText = (EditText) findViewById(R.id.register_username);
        _birthdateEditText = (EditText) findViewById(R.id.register_birthdate);
        _birthhourEditText = (EditText) findViewById(R.id.register_birthhour);

         /* DATE PICKER */
        _calendar = Calendar.getInstance();
        _year = _calendar.get(Calendar.YEAR);
        _month = _calendar.get(Calendar.MONTH);
        _day = _calendar.get(Calendar.DAY_OF_MONTH);
        _hours = _calendar.get(Calendar.HOUR_OF_DAY);
        _minutes = _calendar.get(Calendar.MINUTE);
        _seconds = _calendar.get(Calendar.SECOND);

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
        _birthdateEditText.setText(sdf.format(_calendar.getTime()));

        _birthDate = _year + "-" + (_month +1) + "-" + _day;
        _birthdateEditText.setText(_birthDate);

        _birthHour = _hours + ":" + _minutes + ":" + _seconds;
        _birthhourEditText.setText(_birthHour);

        Button signupButton = (Button) findViewById(R.id.signup_button);
        signupButton.setOnClickListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                _calendar.set(Calendar.YEAR, year);
                _calendar.set(Calendar.MONTH, monthOfYear);
                _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String format = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                _birthdateEditText.setText(sdf.format(_calendar.getTime()));

            }

        };

        _birthdateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, _calendar
                        .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
                        _calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        /* HOUR PICKER */
        final TimePickerDialog.OnTimeSetListener timePickerDialog = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {

                _calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _calendar.set(Calendar.MINUTE, minute);
                String format = "HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                _birthhourEditText.setText(sdf.format(_calendar.getTime()));
            }
        };

        _birthhourEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new TimePickerDialog(RegisterActivity.this, timePickerDialog, _calendar
                        .get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });



        /* REST API */
        loadJSON();

    }

    private void loadJSON() {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        _restClient = retrofit.create(HelloWorldAPI.class);
    }

    @Override
    public void onClick(View v) {
        final String username = _usernameEditText.getText().toString();
        final String birthdate = _birthdateEditText.getText().toString();
        final String birthhour = _birthhourEditText.getText().toString();

        switch (v.getId()) {
            case R.id.signup_button:

                if (isValid(username)){
                    User user = new User(0, username, birthdate + "T" + birthhour);
                    Call<User> usercall = _restClient.create(user);

                    usercall.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.successfully_registered)+ response.body().getId(), Toast.LENGTH_LONG).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        navigateToLogin();
                                    }
                                }, 5000);

                            } else {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.error_receiving_data), Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                } else {
                    Snackbar.make(v, getResources().getString(R.string.invalid_credentials), Snackbar.LENGTH_SHORT).show();
                    break;
                }

        }
    }

    private void navigateToLogin() {
        Intent login = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
        finish();
    }

    private boolean isValid(String username) {
        if (username.equals("") || username == null){
            return false;
        } else {
            return true;
        }
    }

}
