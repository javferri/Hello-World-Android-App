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
 * Search user fragment
 *
 * @author Javier Fernández Riolobos
 * @version 1.0
 * @date 20/09/2017
 */
public class MenuSearchUserFragment extends Fragment {

    static final String BASE_URL = "http://hello-world.innocv.com/api/user/";

    private RecyclerView _listSearchedUsersRecyclerView;
    private ListSearchedUsersAdapter _listSearchedUsersAdapter;
    private HelloWorldAPI _restClient;

    private EditText _searchValue;
    private Button _searchButton;

    public MenuSearchUserFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu_search_user, container, false);

        _listSearchedUsersRecyclerView = (RecyclerView) view.findViewById(R.id.list_searched_users_recycler_view);
        RecyclerView.LayoutManager linearLayoutManager = new
                LinearLayoutManager(getActivity());
        _listSearchedUsersRecyclerView.setLayoutManager(linearLayoutManager);
        _listSearchedUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());

        _searchValue = (EditText) view.findViewById(R.id.search_value);

        _searchButton = (Button) view.findViewById(R.id.search_button);
        _searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    int id = Integer.parseInt(_searchValue.getText().toString());
                    Snackbar.make(getView(), getResources().getString(R.string.searching),
                            Snackbar.LENGTH_SHORT).show();

                    updateUI();

                } catch (NumberFormatException e){
                    Snackbar.make(getView(), getResources().getString(R.string.invalid_ID), Snackbar.LENGTH_SHORT).show();
                }



            }

        });

        return view;
    }

    private void updateUI() {

        loadJSON();
        final Call<User> searcheduser = _restClient.get(Integer.parseInt(_searchValue.getText().toString()));

        searcheduser.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                switch (response.code()) {
                    case 200:
                        User user = response.body();
                        if (user != null) {
                            List<User> searchedUsers = new ArrayList<>();
                            searchedUsers.add(user);

                            if (_listSearchedUsersAdapter == null) {
                                _listSearchedUsersAdapter = new MenuSearchUserFragment.ListSearchedUsersAdapter(searchedUsers);
                                _listSearchedUsersRecyclerView.setAdapter(_listSearchedUsersAdapter);
                            } else {
                                _listSearchedUsersAdapter.setUsers(searchedUsers);
                                _listSearchedUsersAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Snackbar.make(getView(), getResources().getString(R.string.no_such_user), Snackbar.LENGTH_LONG).show();
                        }
                        break;
                    default:
                        Snackbar.make(getView(), getResources().getString(R.string.error_receiving_data), Snackbar.LENGTH_LONG).show();
                        break;
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

    /**
     * List filtered users Adapter
     *
     * @author Javier Fernandez Riolobos
     * @version 1.0
     */
    public class ListSearchedUsersAdapter extends RecyclerView.Adapter<ListSearchedUsersAdapter.ListSearchedUsersHolder> {

        private List<User> users;

        public ListSearchedUsersAdapter(List<User> users) {
            this.users = users;
        }

        @Override
        public ListSearchedUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.list_item_user, parent, false);
            return new ListSearchedUsersHolder(view);
        }

        @Override
        public void onBindViewHolder(ListSearchedUsersHolder holder, int position) {
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
        public class ListSearchedUsersHolder extends RecyclerView.ViewHolder {

            private TextView userNameTextView;
            private TextView userIDTextView;
            private TextView userBithDateTextView;
            private User user;

            public ListSearchedUsersHolder(View itemView) {
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
