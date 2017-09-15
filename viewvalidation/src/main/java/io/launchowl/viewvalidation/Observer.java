package io.launchowl.viewvalidation;

import android.view.View;

public abstract class Observer<T extends View> {
    private final T view;

    public Observer(T view) {
        this.view = view;
    }

    void update(Validator.ValidationResult validationResult) {
        onValidationComplete(getView(), validationResult);
    }

    T getView() {
        return this.view;
    }

    protected abstract void onValidationComplete(T view, Validator.ValidationResult validationResult);
}
