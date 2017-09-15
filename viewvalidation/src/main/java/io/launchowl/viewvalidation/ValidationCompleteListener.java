package io.launchowl.viewvalidation;


import android.view.View;

public interface ValidationCompleteListener<T extends View> {
    void onValidationComplete(T view, Validator.ValidationResult validationResult);
}
