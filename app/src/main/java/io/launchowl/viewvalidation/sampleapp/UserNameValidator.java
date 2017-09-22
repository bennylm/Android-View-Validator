package io.launchowl.viewvalidation.sampleapp;

import android.widget.EditText;

import io.launchowl.viewvalidationlibrary.Validator;

/**
 * Created by benja on 9/22/2017.
 */

public class UserNameValidator  {
    Validator<EditText> validator;
    public UserNameValidator() {
        this.validator = new Validator<>()
    }
}
