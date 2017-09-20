package com.innocv.helloworld.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innocv.helloworld.R;
import com.innocv.helloworld.model.User;
import com.innocv.helloworld.server.HelloWorldAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Login activity
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private EditText _idEditText;
    private EditText _usernameEditText;

    private HelloWorldAPI _restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!HelloWorldPreferenceActivity.getUserName(this).
                equals(HelloWorldPreferenceActivity.USERNAME_DEFAULT)){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        _idEditText = (EditText) findViewById(R.id.login_id);
        _usernameEditText = (EditText) findViewById(R.id.login_username);

        Button loginButton = (Button) findViewById(R.id.login_button);
        Button registerButton = (Button) findViewById(R.id.register_button);
        Button exitButton = (Button) findViewById(R.id.exit_button);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

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

            final String id = _idEditText.getText().toString();
            final String username = _usernameEditText.getText().toString();

            switch (v.getId()) {
                case R.id.login_button:

                    if (isIDValid(id) && isValid(username)){
                        Call<List<User>> allusers = _restClient.getAll();
                        allusers.enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                switch (response.code()) {
                                    case 200:
                                        List<User> users= response.body();
                                        for (User u : users){
                                            if (u.getId() == Integer.parseInt(id) && u.getName().equals(username)){
                                                HelloWorldPreferenceActivity.setID(LoginActivity.this, id);
                                                HelloWorldPreferenceActivity.setUserName(LoginActivity.this, username);
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                                return;
                                            }
                                        }
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.error_receiving_data), Toast.LENGTH_LONG).show();
                                        break;
                                }

                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {
                                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    } else {
                        Snackbar.make(v, getResources().getString(R.string.invalid_credentials), Snackbar.LENGTH_SHORT).show();
                        break;
                    }

                case R.id.register_button:

                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    break;

                case R.id.exit_button:
                    finish();
                    break;
            }


    }

    private boolean isIDValid(String id) {

        try {

        if (id.equals("") || id == null){
            return false;
        }

        int aux = Integer.parseInt(id);


        } catch (NumberFormatException e) {
            return false;

        }

        return true;

    }

    private boolean isValid(String username) {
        if (username.equals("") || username == null){
            return false;
        } else {
            return true;
        }
    }

}
