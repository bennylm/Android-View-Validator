package io.launchowl.viewvalidationlibrary;

import android.view.View;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class contains a collection of {@link Condition} and.or {@link AsyncCondition} objects.
 * <p>
 * A Criteria is intended to be associated with a single {@link View}.
 * One or more {@link Condition} or {@link AsyncCondition} objects can
 * be added to the Criteria instance.
 * <p>
 * All conditions can be evaluated by calling the {@link Criteria#evaluate(EvalCompleteListener)}
 * method. The method will deliver a single result ({@link io.launchowl.viewvalidationlibrary.Validator.ValidationResult})
 * to the supplied {@link EvalCompleteListener}.
 *
 * @param <T> the type of {@link View} being evaluated
 */
public class Criteria<T extends View> {
    private int asyncConditionsComplete = 0;
    private Validator.ValidationResult validationResult = Validator.ValidationResult.Valid;
    private EvalCompleteListener evalCompleteListener;
    final private T validatedView;
    final private Set<Condition<T>> conditions;
    final private Set<AsyncCondition<T>> asyncConditions;

    /**
     * A condition is a single test that will return true or false.
     * <p>
     * Conditions are intended to test a single event. Add additional
     * conditions to a {@link Criteria} instance to test each unique
     * event.
     * <p>
     * For example, one condition could test whether a username contains
     * valid characters. A separate condition could test whether the
     * username contains profanity.
     *
     * @param <T> the type of {@link View} being evaluated
     *
     * @see AsyncCondition
     */
    public interface Condition<T> {
        /**
         * Perform a test using information from the view being evaluated.
         * <p>
         * <pre>
         *  // Assumes <i>view</i> is a {@link android.widget.TextView}
         *  // Make sure the username contains a fruit
         * {@code String[] fruit = {
         *   "apple",
         *   "banana",
         *   "blueberry",
         *   "kiwi",
         *   "orange",
         *   "strawberry"
         *  };
         *  Pattern pattern = Pattern.compile(TextUtils.join("|", fruit));
         *  Matcher matcher = pattern.matcher(view.getText().toString().toLowerCase());
         *  return matcher.find();
         * }
         * </pre>
         * @param view the {@link View} being evaluated
         *
         * @return true or false depending on whether the test passed
         */
        boolean evaluate(T view);
    }

    /**
     * An asynchronous condition is a single test that performs an
     * asynchronous operation and then returns a true or false value to the supplied
     * {@link AsyncConditionCompletionListener}.
     * <p>
     * Conditions are intended to test a single event. Add additional
     * conditions to a {@link Criteria} instance to test each unique
     * event.
     * <p>
     * For example, one condition could test whether a username contains
     * valid characters. A separate condition could test whether the
     * username contains profanity.
     * @param <T> the type of {@link View} being evaluated
     */
    public interface AsyncCondition<T extends View> {
        /**
         * Perform an asynchronous test using information from the view being evaluated.
         *
         * @param view the {@link View} being evaluated
         */
        void evaluate(Criteria.AsyncConditionCompletionListener asyncConditionCompletionListener, T view);
    }

    /**
     * This interface is supplied to the {@link Criteria#evaluate(EvalCompleteListener)}
     * method and receives the final validation result
     * after all conditions in a criteria object have been individually
     * tested.
     */
    public interface EvalCompleteListener {
        /**
         * This method is called after all conditions of the criteria
         * have been tested.
         *
         * @param validationResult the result after all conditions have been tested
         */
        void onComplete(Validator.ValidationResult validationResult);
    }

    /**
     * Class constructor specifying the view being evaluated.
     *
     * @param validatedView the {@link View} being evaluated
     */
    public Criteria(T validatedView) {
        this.validatedView = validatedView;
        this.conditions = new HashSet<>();
        this.asyncConditions = new HashSet<>();
    }

    /**
     * Adds an {@link AsyncCondition} to be tested.
     * <p>
     * For example, the asynchronous operation could evaluate whether a
     * username is available.
     *
     * @param asyncCondition a condition that performs an asynchronous operation
     * @return this {@link Criteria} instance
     *
     * @see AsyncCondition
     * @see Condition
     */
    public Criteria<T> asyncTest(AsyncCondition<T> asyncCondition) {
        this.asyncConditions.add(asyncCondition);
        return this;
    }

    /**
     * Adds a {@link Condition} to be tested.
     * <p>
     * For example, the asynchronous operation could evaluate whether a
     * username contains valid characters.
     *
     * @param condition a condition that can be tested immediately
     * @return this {@link Criteria} instance
     *
     * @see AsyncCondition
     * @see Condition
     */
    public Criteria<T> test(Condition<T> condition) {
        this.conditions.add(condition);
        return this;
    }


    /**
     * Evaluates all {@link Condition} and {@link AsyncCondition} objects
     * associated with this instance.
     *
     * @param evalCompleteListener an {@link EvalCompleteListener} that will handle the final result
     */
    void evaluate(EvalCompleteListener evalCompleteListener) {
        this.evalCompleteListener = evalCompleteListener;

        // Perform all synchronous evaluations first
        for (Condition<T> condition : this.conditions) {
            setValidationResult(condition.evaluate(this.validatedView));
        }

        // Initiate all asynchronous evaluations
        for (AsyncCondition<T> asyncCondition : this.asyncConditions) {
            asyncCondition.evaluate(new AsyncConditionCompletionListener(this), this.validatedView);
        }

        // Only complete if there aren't any AsyncCondition objects
        // still running
        if (this.asyncConditions.size() == 0) {
            complete();
        }
    }

    /**
     * Returns all {@link Condition} objects added to this instance.
     * <p>
     * For testing.
     * @return all {@link Condition} objects added to this instance
     */
    Set<Condition<T>> getConditions() {
        return Collections.unmodifiableSet(this.conditions);
    }

    /**
     * Returns all {@link AsyncCondition} objects added to this instance.
     * <p>
     * For testing.
     * @return all {@link AsyncCondition} objects added to this instance
     */
    Set<AsyncCondition<T>> getAsyncConditions() {
        return Collections.unmodifiableSet(this.asyncConditions);
    }

    /**
     * This method is called when {@link AsyncConditionCompletionListener#complete(boolean)}
     * is invoked.
     *
     * @param result the result of testing the condition
     *
     * @see AsyncCondition
     */
    private void asyncConditionComplete(boolean result) {
        this.asyncConditionsComplete++;
        setValidationResult(result);

        complete();
    }

    /**
     * This method is called by {@link #evaluate(EvalCompleteListener)} and/or
     * {@link #asyncConditionComplete(boolean)} after all synchronous and/or
     * asynchronous conditions have been evaluated.
     */
    private void complete() {
        if (this.asyncConditions.size() == asyncConditionsComplete) {
            this.evalCompleteListener.onComplete(this.validationResult);
            reset();
        }
    }

    /**
     * This method is called by {@link #evaluate(EvalCompleteListener)} or
     * {@link #asyncConditionComplete(boolean)} to set the {@link io.launchowl.viewvalidationlibrary.Validator.ValidationResult}
     * value supplied to the {@link EvalCompleteListener}.
     *
     * @param result the result of testing the condition
     */
    private void setValidationResult(boolean result) {
        if (!result) {
            this.validationResult = Validator.ValidationResult.Invalid;
        }
    }

    /**
     * Resets default values.
     */
    private void reset() {
        this.asyncConditionsComplete = 0;
        this.validationResult = Validator.ValidationResult.Valid;
    }

    /**
     * This class is responsible for notifying the {@link Criteria} object
     * that an {@link AsyncCondition} has completed its evaluation.
     * <p>
     * An instance of this class is supplied to the {@link AsyncCondition#evaluate(AsyncConditionCompletionListener, View)}
     * method.
     *
     * @see AsyncCondition
     */
    public static class AsyncConditionCompletionListener {
        final Criteria criteria;

        /**
         * Class constructor that is supplied an instance of this
         * {@link Criteria}.
         *
         * @param criteria an instance of this {@link Criteria}
         */
        AsyncConditionCompletionListener(Criteria criteria) {
            this.criteria = criteria;
        }

        /**
         * Notifies the {@link Criteria} instance that an asynchronous
         * condition evaluation is complete.
         *
         * @param result the result of the asynchronous test
         *
         * @see AsyncCondition
         */
        public void complete(boolean result) {
            this.criteria.asyncConditionComplete(result);
        }
    }
}
