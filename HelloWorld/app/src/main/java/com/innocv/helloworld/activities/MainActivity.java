package com.innocv.helloworld.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innocv.helloworld.R;
import com.innocv.helloworld.activities.menu.MenuFilterUsersFragment;
import com.innocv.helloworld.activities.menu.MenuListUsersFragment;
import com.innocv.helloworld.activities.menu.MenuSearchUserFragment;
import com.innocv.helloworld.activities.menu.MenuUpdateAccountFragment;
import com.innocv.helloworld.server.HelloWorldAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Main activity
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private Fragment _fragmentInit;
    private MenuItem _previousMenuItem;
    private HelloWorldAPI _restClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MainActivity.this);

                alertDialogBuilder.setTitle(getResources().getString(R.string.follow_me));

                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.link_to_linkedin))
                        .setCancelable(false)
                        .setNegativeButton(getResources().getString(R.string.quit),new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        TextView nav_header_id = headerLayout.findViewById(R.id.nav_header_id);
        nav_header_id.setText(HelloWorldPreferenceActivity.getID(getApplicationContext()));

        TextView nav_header_username = headerLayout.findViewById(R.id.nav_header_username);
        nav_header_username.setText(HelloWorldPreferenceActivity.getUserName(getApplicationContext()));

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_help) {
            Intent help = new Intent(MainActivity.this, HelpActivity.class);
            startActivity(help);
            return true;

        } else if (id == R.id.action_credits){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    MainActivity.this);

            alertDialogBuilder.setTitle(getResources().getString(R.string.action_credits));

            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.credits_message))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.quit),new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        /* Only one item checked at a time */
        item.setCheckable(true);
        item.setChecked(true);
        if (_previousMenuItem != null) {
            _previousMenuItem.setChecked(false);
        }
        _previousMenuItem = item;

        boolean fragmentTransaction = false;
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragmentTransaction = false;
        } else if (id == R.id.nav_list_users) {
            fragment = new MenuListUsersFragment();
            _fragmentInit = fragment;
            fragmentTransaction = true;
        } else if (id == R.id.nav_filter_users) {
            fragment = new MenuFilterUsersFragment();
            _fragmentInit = fragment;
            fragmentTransaction = true;
        } else if (id == R.id.nav_search_user) {
            fragment = new MenuSearchUserFragment();
            _fragmentInit = fragment;
            fragmentTransaction = true;
        } else if (id == R.id.nav_update_user) {
            fragment = new MenuUpdateAccountFragment();
            _fragmentInit = fragment;
            fragmentTransaction = true;
        } else if (id == R.id.nav_remove_account) {


            Call<Void> removeuser = _restClient.remove(Integer.parseInt(HelloWorldPreferenceActivity.getID(MainActivity.this)));

            removeuser.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    switch (response.code()) {
                        case 200:
                            HelloWorldPreferenceActivity.setID(MainActivity.this, null);
                            HelloWorldPreferenceActivity.setUserName(MainActivity.this, null);
                            HelloWorldPreferenceActivity.setBirthDate(MainActivity.this, null);
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            break;
                        default:
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.error_receiving_data), Toast.LENGTH_LONG).show();
                            break;
                    }

                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(MainActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (id == R.id.nav_exit_application) {
            HelloWorldPreferenceActivity.setID(MainActivity.this, null);
            HelloWorldPreferenceActivity.setUserName(MainActivity.this, null);
            HelloWorldPreferenceActivity.setBirthDate(MainActivity.this, null);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }

        setTitle(item.getTitle());

        if(fragmentTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_main, fragment)
                    .commit();


        } else {

            if (_fragmentInit != null) {
                getSupportFragmentManager().beginTransaction()
                        .detach(_fragmentInit)
                        .commit();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
