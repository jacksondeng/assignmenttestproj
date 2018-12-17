package com.gemalto.assignment.auth;

import android.arch.lifecycle.MutableLiveData;

/**
 * Created by jacksondeng on 16/12/18.
 */

public class AuthState {
    private MutableLiveData<State> state = new MutableLiveData<>();
    public MutableLiveData<State> getState(){
        return state;
    }
}
