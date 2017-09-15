package io.launchowl.viewvalidation;

import android.view.View;
import android.widget.EditText;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Validator<T extends View> {
    Set<Observer> observers;
    T sourceView;
    Criteria<T> criteria;
    public enum ValidationResult {
        Valid,
        Invalid
    }

    public Validator(Criteria<T> criteria) {
        this.observers = new HashSet<>();
        this.criteria = criteria;
    }

    public Validator(T sourceView, Criteria<T> criteria) {
        this.observers = new HashSet<>();
        this.sourceView = sourceView;
        this.criteria = criteria;
    }

    public void observe(Observer... observers) {
        Collections.addAll(this.observers, observers);
    }

    public void validate() {
        this.criteria.evaluate(new Criteria.EvalCompleteListener() {
            @Override
            public void onComplete(ValidationResult validationResult) {
                Notifier.notify(observers, validationResult);
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
