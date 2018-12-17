package com.gemalto.assignment;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.gemalto.assignment.auth.AuthUtils;
import com.gemalto.assignment.auth.State;

public class AuthActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1;
    private KeyguardManager mKeyguardManager;
    private AuthUtils authUtils = new AuthUtils();
    private boolean btnClicked = false;
    private Button btnAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        initViews();
        initKeyGuard();
        initListeners();
        initObserver();
    }

    private void initBindings(){
        btnAuth = findViewById(R.id.btn_auth);
    }

    private void initViews(){
        initBindings();
    }

    private void initListeners(){
        findViewById(R.id.btn_auth).setOnClickListener(v -> {
            authUtils.tryEncrypt();
            btnClicked = true;
        });
    }

    private void initKeyGuard(){
        mKeyguardManager = (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
        if (!mKeyguardManager.isKeyguardSecure()) {
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                    Toast.LENGTH_LONG).show();
            btnAuth.setEnabled(false);
            return;
        }
        authUtils.createKey();
    }

    private void initObserver(){
        authUtils.getAuthState().observe(AuthActivity.this, state -> {
            if(state!=null) {
                switch (state) {
                    case AUTHENTICATED:{
                        startActivity(new Intent(this,MainActivity.class));
                        finish();
                        break;
                    }
                    case UNAUTHENTICATED:{
                        if(btnClicked) {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case SHOULD_AUTHENTICATE:{
                        showAuthenticationScreen();
                        break;
                    }
                }
            }
        });
    }

    private void showAuthenticationScreen() {
        Intent intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            if (resultCode == RESULT_OK) {
                authUtils.getAuthState().postValue(State.AUTHENTICATED);
            } else {
                authUtils.getAuthState().postValue(State.UNAUTHENTICATED);
            }
        }
    }

}
