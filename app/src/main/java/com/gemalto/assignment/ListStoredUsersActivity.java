package com.gemalto.assignment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.gemalto.gemaltoapi.api.GemaltoApi;
import com.gemalto.gemaltoapi.data.User;
import java.util.List;

public class ListStoredUsersActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GemaltoApi gemaltoApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_stored_users);
        initViews();
        gemaltoApi = ((MyApp)getApplicationContext()).gemaltoApi;
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setRecyclerView(gemaltoApi.getUserRepository().getLocalUsers().getValue());
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        gemaltoApi = null;
    }


    private void setRecyclerView(List<User> users){
        mAdapter = new UserRecyclerViewAdapter(users);
        recyclerView.setAdapter(mAdapter);
        recyclerView.invalidateItemDecorations();
    }

    private void initViews(){
        initBindings();
        initRecyclerView();
        initToolbar();
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initRecyclerView(){
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void initBindings(){
        recyclerView = findViewById(R.id.user_recycler_view);
        toolbar = findViewById(R.id.toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
