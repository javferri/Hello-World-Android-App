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
import android.widget.TextView;

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
 * List users fragment
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class MenuListUsersFragment extends Fragment {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private RecyclerView _listUsersRecyclerView;
    private ListUsersAdapter _listUsersAdapter;
    private HelloWorldAPI _restClient;

    public MenuListUsersFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_list_users, container, false);

        _listUsersRecyclerView = (RecyclerView) view.findViewById(R.id.list_users_recycler_view);
        RecyclerView.LayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity());
        _listUsersRecyclerView.setLayoutManager(linearLayoutManager);
        _listUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        loadJSON();

        Call<List<User>> allusers = _restClient.getAll();
        allusers.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                switch (response.code()) {
                    case 200:
                        List<User> users = response.body();
                        if (_listUsersAdapter == null) {
                            _listUsersAdapter = new ListUsersAdapter(users);
                            _listUsersRecyclerView.setAdapter(_listUsersAdapter);
                        } else {
                            _listUsersAdapter.setUsers(users);
                            _listUsersAdapter.notifyDataSetChanged();
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

        return view;
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
     * List users Adapter
     *
     * @author Javier Fernandez Riolobos
     * @version 1.0
     */
    public class ListUsersAdapter extends RecyclerView.Adapter<ListUsersAdapter.ListUsersHolder> {

        private List<User> users;

        public ListUsersAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public ListUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item_user, parent, false);
            return new ListUsersHolder(view);
        }

        @Override
        public void onBindViewHolder(ListUsersHolder holder, int position) {
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
         * User's holder
         *
         * @author Javier Fernandez Riolobos
         * @version 1.0
         */
        public class ListUsersHolder extends RecyclerView.ViewHolder {

            private TextView userNameTextView;
            private TextView userIDTextView;
            private TextView userBithDateTextView;
            private User user;

            public ListUsersHolder(View itemView) {
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
