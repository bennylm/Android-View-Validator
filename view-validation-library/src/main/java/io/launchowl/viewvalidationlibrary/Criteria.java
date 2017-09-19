package io.launchowl.viewvalidationlibrary;

import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Criteria<T extends View> {
    private int asyncConditionsComplete = 0;
    private Validator.ValidationResult validationResult = Validator.ValidationResult.Valid;
    private EvalCompleteListener evalCompleteListener;
    final private T sourceView;
    final private Set<Condition<T>> conditions;
    final private Set<AsyncCondition<T>> asyncConditions;

    public interface Condition<T> {
        boolean evaluate(T view);
    }

    public interface AsyncCondition<T extends View> {
        void evaluate(final Criteria<T> criteria, T view);
    }

    public interface EvalCompleteListener {
        void onComplete(Validator.ValidationResult validationResult);
    }

    public Criteria(T sourceView) {
        this.sourceView = sourceView;
        this.conditions = new HashSet<>();
        this.asyncConditions = new HashSet<>();
    }

    public Criteria<T> asyncTest(AsyncCondition<T> asyncCondition) {
        this.asyncConditions.add(asyncCondition);
        return this;
    }

    public Criteria<T> test(Condition<T> condition) {
        this.conditions.add(condition);
        return this;
    }

    public void invalid() {
        this.validationResult = Validator.ValidationResult.Invalid;
    }

    public void valid() {
        this.validationResult = Validator.ValidationResult.Valid;
    }

    public void asyncConditionComplete(boolean result) {
        this.asyncConditionsComplete++;
        setValidationResult(result);

        complete();
    }

    public void evaluate(EvalCompleteListener evalCompleteListener) {
        this.evalCompleteListener = evalCompleteListener;

        for (Condition<T> condition : this.conditions) {
            setValidationResult(condition.evaluate(this.sourceView));
        }

        for (AsyncCondition<T> asyncCondition : this.asyncConditions) {
            asyncCondition.evaluate(this, this.sourceView);
        }

        // Only complete if there aren't any AsyncCondition objects
        // still running
        if (this.asyncConditions.size() == 0) {
            complete();
        }
    }

    Set<Condition<T>> getConditions() {
        return Collections.unmodifiableSet(this.conditions);
    }

    Set<AsyncCondition<T>> getAsyncConditions() {
        return Collections.unmodifiableSet(this.asyncConditions);
    }

    private void complete() {
        if (this.asyncConditions.size() == asyncConditionsComplete) {
            this.evalCompleteListener.onComplete(this.validationResult);
            reset();
        }
    }

    private void setValidationResult(boolean result) {
        if (!result) {
            this.validationResult = Validator.ValidationResult.Invalid;
        }
    }

    private void reset() {
        this.asyncConditionsComplete = 0;
        this.validationResult = Validator.ValidationResult.Valid;
    }


}
