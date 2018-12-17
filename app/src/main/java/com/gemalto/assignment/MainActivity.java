package com.gemalto.assignment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.gemalto.gemaltoapi.api.GemaltoApi;
import com.jakewharton.rxbinding3.view.RxView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jacksondeng on 15/12/18.
 */

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Spinner genderSpinner;
    private Button btnQuery;
    private String gender;
    private EditText multipleUserEt,seedEt;
    private Dialog loadingDialog;
    private GemaltoApi gemaltoApi;
    private boolean btnClicked =false;

    @Override
    protected void onCreate(Bundle savedBundleInstance){
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.activity_main);
        initViews();
        gemaltoApi = ((MyApp)getApplicationContext()).gemaltoApi;
        gemaltoApi.getUserRepository().getLocalUsers().observe(this,
                users -> {
                    if(btnClicked) {
                        startActivity(new Intent(this, ListStoredUsersActivity.class));
                        btnClicked = false;
                    }
                });
        gemaltoApi.getUserRepository().getRemoteUsers().observe(this,
                users -> {
                    if(btnClicked) {
                        startActivity(new Intent(this, ListQueryUsersActivity.class));
                        btnClicked = false;
                    }
                });

        gemaltoApi.getApiSuccess().observe(this , apiSuccess->{
           if(apiSuccess != null && apiSuccess){
               dismissSpinnerDialog();
           }
        });
        gemaltoApi.getShowNetworkError().observe(this , showNetworkErr->{
            if(showNetworkErr != null && showNetworkErr){
                dismissSpinnerDialog();
                promptRetryDialog();
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    private void initViews(){
        initBindings();
        initSpinner();
        initToolbar();
        initListeners();
        initNavigationView();
        initDrawerLayout();
    }

    private void initBindings(){
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        genderSpinner = findViewById(R.id.gender_spinner);
        btnQuery = findViewById(R.id.btn_query);
        multipleUserEt = findViewById(R.id.multiple_user_et);
        seedEt = findViewById(R.id.seed_et);
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDrawerLayout(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initNavigationView(){
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            switch(id)
            {
                case R.id.query_user:
                    queryUser();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.view_stored_user:
                    listStoredUsers();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.exit:
                    finish();
                    drawerLayout.closeDrawers();
                    return true;
                default:
                    return true;
            }
        });
    }

    private void queryUser(){
        btnClicked = true;
        showLoadingDialog();
        gemaltoApi.getUsers(gender,getSeed(),getMultiple());
    }

    private void listStoredUsers(){
        btnClicked = true;
        gemaltoApi.listStoredUsers();
    }

    private void initListeners(){
        RxView.clicks(btnQuery)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(unit -> {
                    queryUser();
                });
    }

    private void initSpinner(){
        List<String> genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item, genderList);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(0);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                genderSpinner.setSelection(position);
                gender = genderList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String getMultiple(){
        String multiple = multipleUserEt.getText().toString();
        if(multiple.length()>0 && multiple.matches("[0-9]+")){
            int result = Integer.valueOf(multiple);
            if(result<=5000 && result>0){
                return multiple;
            }
        }
        return "";
    }

    private String getSeed(){
        if(seedEt.getText().length()>0){
            return seedEt.getText().toString();
        }
        return "";
    }

    private void showLoadingDialog() {
        if(loadingDialog == null) {
            loadingDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        }
        loadingDialog.setCancelable(false);
        loadingDialog.setContentView(R.layout.dialog_api);
        loadingDialog.show();
    }

    public void dismissSpinnerDialog(){
        if(loadingDialog!=null && loadingDialog.isShowing())
        {
            loadingDialog.dismiss();
        }
    }

    public void promptRetryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.network_timeout))
                .setMessage(getString(R.string.try_again))
                .setPositiveButton(getString(R.string.retry), (dialogInterface, i) -> queryUser())
                .setNeutralButton(getString(R.string.cancel), null);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

}
