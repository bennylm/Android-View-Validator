package io.launchowl.view.validation;


import android.view.View;

public interface ValidationCompleteListener<T extends View> {
    void onValidationComplete(T view, Validator.ValidationResult validationResult);
}
