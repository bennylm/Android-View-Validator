package io.launchowl.viewvalidationapp;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.launchowl.viewvalidationlibrary.Criteria;
import io.launchowl.viewvalidationlibrary.Observer;
import io.launchowl.viewvalidationlibrary.Validator;
import io.launchowl.viewvalidationlibrary.ValidatorSet;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // The views will be available in the onCreate method.
        // Offload creating the Validator and Observer
        // objects in a separate method.
        initFormValidation();
    }

    /**
     * Setup the form validation
     */
    void initFormValidation() {
        // Create a Validator for the username field that
        // will be used to check if a username is available
        EditText userNameEditText = (EditText) findViewById(R.id.user_name);
        final Validator<EditText> userNameAvailableValidator = new Validator<>(new Criteria<EditText>(userNameEditText)
            .asyncTest(new Criteria.AsyncCondition<EditText>() {
                @Override
                public void evaluate(final Criteria.AsyncConditionCompletionListener asyncConditionCompletionListener, EditText view) {
                    UserRepository userRepository = new UserRepository();
                    userRepository.getUser(view.getText().toString(), new UserRepository.OnuserRetrievedListener() {
                        @Override
                        public void onUserRetrieved(User user) {
                            asyncConditionCompletionListener.complete(user == null);
                        }
                    });
                }
            })
        );

        // Create an observer for the continue button that will enable/disable it based
        // on the username field containing valid data
        Observer<Button> continueButtonObserver = new Observer<Button>((Button) findViewById(R.id.continue_button)) {
            @Override
            protected void onValidationComplete(Button view, Validator.ValidationResult validationResult) {
                view.setEnabled(validationResult == Validator.ValidationResult.Valid);
            }
        };

        // Create an observer for the username status message that will display a
        // message stating whether the username is available
        Observer<TextView> userNameStatusObserver = new Observer<TextView>((TextView) findViewById(R.id.username_status)) {
            @Override
            protected void onValidationComplete(TextView view, Validator.ValidationResult validationResult) {
                view.setText(
                        validationResult == Validator.ValidationResult.Valid
                                ? getString(R.string.success_available)
                                : getString(R.string.error_not_available)
                );


                view.setTextColor(
                        validationResult == Validator.ValidationResult.Valid
                                ? getColor(R.color.success_color)
                                : getColor(R.color.error_color)
                );

            }
        };

        // Add the observers
        userNameAvailableValidator.observe(userNameStatusObserver, continueButtonObserver);

        // Create a Validator for the username field that
        // will be used to check if it contains vlid characters
        final Validator<EditText> userNameCompliesValidator = new Validator<>(new Criteria<EditText>(userNameEditText)
                // Make sure it doesn't contain special characters
                .test(new Criteria.Condition<EditText>() {
                    @Override
                    public boolean evaluate(EditText view) {
                        return Pattern.matches("^[a-zA-Z0-9]*$", view.getText().toString());
                    }
                // Make sure it contains the name of a popular fruit
                }).test(new Criteria.Condition<EditText>() {
                    @Override
                    public boolean evaluate(EditText view) {
                        String[] fruit = {
                                "apple",
                                "banana",
                                "blueberry",
                                "kiwi",
                                "orange",
                                "strawberry"
                        };
                        Pattern pattern = Pattern.compile(TextUtils.join("|", fruit));
                        Matcher matcher = pattern.matcher(view.getText().toString().toLowerCase());
                        return matcher.find();
                    }
                })
        );

        userNameCompliesValidator.observe(
                continueButtonObserver, // Reuse existing behavior from the observer representing  the continue button
                new Observer<TextView>((TextView) findViewById(R.id.username_status)) {
                    // This is a new observer for the username status TextView. If the username doesn't meet
                    // the expected criteria then we want to hide the status completely, so we're not
                    // overcrowding the space below the EditText view.
                    @Override
                    protected void onValidationComplete(TextView view, Validator.ValidationResult validationResult) {
                        view.setVisibility(validationResult == Validator.ValidationResult.Valid
                                ? View.VISIBLE
                                : View.INVISIBLE);
                    }
                }, new Observer<TextInputLayout>((TextInputLayout) findViewById(R.id.user_name_layout)) {
                    // Yes...this is an observer for same view that's being validated! Remember, the goal was to
                    // separate the code that evaluates the input from the code responsible for providing information
                    // to the user.
                    @Override
                    protected void onValidationComplete(TextInputLayout view, Validator.ValidationResult validationResult) {
                        view.setError(validationResult == Validator.ValidationResult.Valid
                        ? null
                        : getString(R.string.error_invalid_username));
                    }
                });

        // Add the validators to a ValidatorSet so they can both be
        // validated via a single request
        final ValidatorSet validatorSet = new ValidatorSet(userNameAvailableValidator, userNameCompliesValidator);

        // Listen for text being modified in the user name view.
        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // If at least 3 characters have been entered actively
                // validate the input
                if (s.toString().length() > 3) {
                    validatorSet.validate();
                } else {
                    resetViews();
                }
            }
        });
    }

    void resetViews() {
        ((EditText) findViewById(R.id.user_name)).setError(null);
        ((TextView) findViewById(R.id.username_status)).setText("");
        ((TextView) findViewById(R.id.username_status)).setVisibility(View.VISIBLE);
        findViewById(R.id.continue_button).setEnabled(false);
    }
}

