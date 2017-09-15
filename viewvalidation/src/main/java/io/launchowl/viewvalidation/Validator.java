package io.launchowl.viewvalidation;

import android.view.View;
import android.widget.EditText;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Validator<T extends View> {
    int limit = -1;
    Set<Observer> observers;
    T sourceView;
    Criteria<T> criteria;
    public enum ValidationResult {
        Valid,
        Invalid
    }

    public Validator() { }

    public Validator(T sourceView, Criteria<T> criteria) {
        this.observers = new HashSet<>();
        this.sourceView = sourceView;
        this.criteria = criteria;
    }

    public void observe(Observer... observers) {
        Collections.addAll(this.observers, observers);
    }

    public Validator limit(int charLimit) {
        this.limit = charLimit;
        return this;
    }

    public void validate() {
        this.criteria.test(this.sourceView, new TestCompleteListener() {
            @Override
            public void onComplete(ValidationResult validationResult) {
                if (!(sourceView instanceof EditText) || (sourceView instanceof EditText && ((EditText) sourceView).getText().length() > limit)) {
                    Notifier.notify(observers, validationResult);
                }
            }
        });
    }

    Set<Observer> getObservers() {
        return this.observers;
    }

    View getView() {
        return this.sourceView;
    }

    static class Notifier {
         static void notify(Set<Observer> observers, ValidationResult validationResult) {
            for (Observer observer : observers) {
                observer.update(validationResult);
            }
        }
    }
}
