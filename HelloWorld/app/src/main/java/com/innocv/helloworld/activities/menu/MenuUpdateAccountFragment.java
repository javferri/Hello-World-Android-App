package com.innocv.helloworld.activities.menu;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innocv.helloworld.R;
import com.innocv.helloworld.activities.HelloWorldPreferenceActivity;
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

/**
 *
 * Update account fragment
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class MenuUpdateAccountFragment extends Fragment {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private EditText _updateValue, _birthdateEditText, _birthhourEditText;
    private Button _updateButton;

    private Calendar _calendar;
    private int _year, _month, _day, _hours, _minutes, _seconds;
    private String _birthDate, _birthHour;

    private HelloWorldAPI _restClient;

    public MenuUpdateAccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_update_account, container, false);

        /* DATE PICKER */
        _calendar = Calendar.getInstance();
        _year = _calendar.get(Calendar.YEAR);
        _month = _calendar.get(Calendar.MONTH);
        _day = _calendar.get(Calendar.DAY_OF_MONTH);
        _hours = _calendar.get(Calendar.HOUR_OF_DAY);
        _minutes = _calendar.get(Calendar.MINUTE);
        _seconds = _calendar.get(Calendar.SECOND);

        _birthdateEditText = (EditText) view.findViewById(R.id.update_birthdate);
        _birthhourEditText = (EditText) view.findViewById(R.id.update_birthhour);

        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.FRANCE);
        _birthdateEditText.setText(sdf.format(_calendar.getTime()));

        _birthDate = _year + "-" + (_month +1) + "-" + _day;
        _birthdateEditText.setText(_birthDate);

        _birthHour = _hours + ":" + _minutes + ":" + _seconds;
        _birthdateEditText.setText(_birthHour);

        final DatePickerDialog.OnDateSetListener date = new android.app.DatePickerDialog.OnDateSetListener() {

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
                new DatePickerDialog(getActivity(), date, _calendar
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
                new TimePickerDialog(getActivity(), timePickerDialog, _calendar
                        .get(Calendar.HOUR_OF_DAY), _calendar.get(Calendar.MINUTE),
                        true).show();
            }
        });

        _updateValue = (EditText) view.findViewById(R.id.update_value);

        _updateButton = (Button) view.findViewById(R.id.update_button);
        _updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((_updateValue != null && !_updateValue.equals("")) && (_birthdateEditText.getText().toString() != null && !_birthdateEditText.getText().toString().equals("")) && (_birthhourEditText.getText().toString() != null && !_birthhourEditText.getText().toString().equals(""))){
                    updateUI();
                } else {
                    Snackbar.make(getView(), getResources().getString(R.string.not_enough_data), Snackbar.LENGTH_LONG).show();
                }

            }

        });

        return view;

    }

    private void updateUI() {

        loadJSON();

        final String birthdate = _birthdateEditText.getText().toString() + "T" + _birthhourEditText.getText().toString();

        User usr = new User(Integer.parseInt(HelloWorldPreferenceActivity.getID(getContext())), _updateValue.getText().toString(), birthdate);

        Call<User> updatedUser = _restClient.update(usr);

        updatedUser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    Snackbar.make(getView(), getResources().getString(R.string.changes_applied), Snackbar.LENGTH_LONG).show();
                    HelloWorldPreferenceActivity.setBirthDate(getContext(), birthdate);
                    HelloWorldPreferenceActivity.setUserName(getContext(), _updateValue.getText().toString());

                    NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                    View headerLayout = navigationView.getHeaderView(0);

                    TextView nav_header_username = headerLayout.findViewById(R.id.nav_header_username);
                    nav_header_username.setText(HelloWorldPreferenceActivity.getUserName(getContext()));

                } else {
                    Snackbar.make(getView(), getResources().getString(R.string.error_receiving_data), Snackbar.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Snackbar.make(getView(), t.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

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

}
