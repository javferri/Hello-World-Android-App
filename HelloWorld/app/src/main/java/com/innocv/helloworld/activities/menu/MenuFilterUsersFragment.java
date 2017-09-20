package com.innocv.helloworld.activities.menu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.innocv.helloworld.R;
import com.innocv.helloworld.model.User;
import com.innocv.helloworld.server.HelloWorldAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * Filter users fragment
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class MenuFilterUsersFragment extends Fragment {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private RecyclerView _listFilteredUsersRecyclerView;
    private ListFilteredUsersAdapter _listFilteredUsersAdapter;
    private HelloWorldAPI _restClient;

    private EditText _filterValue;
    private Spinner _filterSpinner;
    private Button _filterButton;

    public MenuFilterUsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_filter_users, container, false);

        _listFilteredUsersRecyclerView = (RecyclerView) view.findViewById(R.id.list_filtered_users_recycler_view);
        RecyclerView.LayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity());
        _listFilteredUsersRecyclerView.setLayoutManager(linearLayoutManager);
        _listFilteredUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        _filterValue = (EditText) view.findViewById(R.id.filter_value);

        // Add listener on Spinner item selection
        _filterSpinner = (Spinner) view.findViewById(R.id.drop_down_filter);
        _filterSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        _filterButton = (Button) view.findViewById(R.id.filter_button);
        _filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Snackbar.make(getView(), R.string.filtering,
                        Snackbar.LENGTH_SHORT).show();

                updateUI();

            }

        });

        return view;
    }

    private void updateUI() {

        loadJSON();
        Call<List<User>> allusers = _restClient.getAll();
        String[] aux = getResources().getStringArray(R.array.spinner_arrays);

        if (String.valueOf(_filterSpinner.getSelectedItem()).equals(aux[0])){

            allusers.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    switch (response.code()) {
                        case 200:
                            List<User> users = response.body();
                            List<User> filteredUsers = new ArrayList<User>();

                            for (User u : users){
                                if (u.getId() >  Integer.parseInt(_filterValue.getText().toString())){
                                    filteredUsers.add(u);
                                }
                            }

                            if (_listFilteredUsersAdapter == null) {
                                _listFilteredUsersAdapter = new MenuFilterUsersFragment.ListFilteredUsersAdapter(filteredUsers);
                                _listFilteredUsersRecyclerView.setAdapter(_listFilteredUsersAdapter);
                            } else {
                                _listFilteredUsersAdapter.setUsers(filteredUsers);
                                _listFilteredUsersAdapter.notifyDataSetChanged();
                            }
                            break;
                        default:
                            Snackbar.make(getView(), getResources().getString(R.string.error_receiving_data), Snackbar.LENGTH_LONG).show();
                            break;
                    }

                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Snackbar.make(getView(), t.toString(), Snackbar.LENGTH_LONG).show();
                }
            });

        } else {

            allusers.enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    switch (response.code()) {
                        case 200:
                            List<User> users = response.body();
                            List<User> filteredUsers = new ArrayList<>();

                            for (User u : users){
                                if (u.getName().contains(_filterValue.getText().toString())){
                                    filteredUsers.add(u);
                                }
                            }

                            if (_listFilteredUsersAdapter == null) {
                                _listFilteredUsersAdapter = new MenuFilterUsersFragment.ListFilteredUsersAdapter(filteredUsers);
                                _listFilteredUsersRecyclerView.setAdapter(_listFilteredUsersAdapter);
                            } else {
                                _listFilteredUsersAdapter.setUsers(filteredUsers);
                                _listFilteredUsersAdapter.notifyDataSetChanged();
                            }
                            break;
                        default:
                            Snackbar.make(getView(), getResources().getString(R.string.error_receiving_data), Snackbar.LENGTH_LONG).show();
                            break;
                    }

                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    Snackbar.make(getView(), t.toString(), Snackbar.LENGTH_LONG).show();
                }
            });

        }



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

    /**
     * List filtered users Adapter
     *
     * @author Javier Fernandez Riolobos
     * @version 1.0
     */
    public class ListFilteredUsersAdapter extends RecyclerView.Adapter<ListFilteredUsersAdapter.ListFilteredUsersHolder> {

        private List<User> users;

        public ListFilteredUsersAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public ListFilteredUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item_user, parent, false);
            return new ListFilteredUsersHolder(view);
        }

        @Override
        public void onBindViewHolder(ListFilteredUsersHolder holder, int position) {
            User user = users.get(position);
            holder.bindUser(user);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        /**
         * Filtered User's holder
         *
         * @author Javier Fernandez Riolobos
         * @version 1.0
         */
        public class ListFilteredUsersHolder extends RecyclerView.ViewHolder {

            private TextView userNameTextView;
            private TextView userIDTextView;
            private TextView userBithDateTextView;
            private User user;

            public ListFilteredUsersHolder(View itemView) {
                super(itemView);
                userNameTextView = (TextView) itemView.findViewById(R.id.list_user_name);
                userIDTextView = (TextView) itemView.findViewById(R.id.list_user_id);
                userBithDateTextView = (TextView) itemView.findViewById(R.id.list_user_birthdate);
            }

            public void bindUser(User user) {
                this.user = user;
                userNameTextView.setText(user.getName());
                userIDTextView.setText(String.valueOf(user.getId()));
                userBithDateTextView.setText(user.getBirthdate());
            }
        }

    }

}

