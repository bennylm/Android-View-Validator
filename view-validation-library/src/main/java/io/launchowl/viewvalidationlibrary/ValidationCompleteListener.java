package io.launchowl.viewvalidationlibrary;


import android.view.View;

public interface ValidationCompleteListener<T extends View> {
    void onValidationComplete(T view, Validator.ValidationResult validationResult);
}
