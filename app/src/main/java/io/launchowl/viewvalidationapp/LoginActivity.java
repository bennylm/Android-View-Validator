package io.launchowl.viewvalidationapp;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

import io.launchowl.viewvalidation.Criteria;
import io.launchowl.viewvalidation.Observer;
import io.launchowl.viewvalidation.Validator;


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
        // Create an instance of the Validator for the username field
        EditText userNameEditText = (EditText) findViewById(R.id.user_name);
        final Validator<EditText> userNameValidator = new Validator<>(new Criteria<EditText>(userNameEditText)
            .asyncTest(new Criteria.AsyncCondition<EditText>() {
                @Override
                public void evaluate(final Criteria<EditText> criteria, EditText view) {
                    UserRepository userRepository = new UserRepository();
                    userRepository.getUser(view.getText().toString(), new UserRepository.OnuserRetrievedListener() {
                        @Override
                        public void onUserRetrieved(User user) {
                            criteria.asyncConditionComplete(user == null);
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
                if (validationResult == Validator.ValidationResult.Valid) {
                    view.setEnabled(true);
                }

                if (validationResult == Validator.ValidationResult.Invalid) {
                    view.setEnabled(false);
                }
            }
        };

        // Create an observer for the username status message that will display a
        // message stating whether the username is available
        Observer<TextView> userNameStatusObserver = new Observer<TextView>((TextView) findViewById(R.id.username_status)) {
            @Override
            protected void onValidationComplete(TextView view, Validator.ValidationResult validationResult) {
                if (validationResult == Validator.ValidationResult.Valid) {
                    view.setText(getString(R.string.success_available));
                    view.setTextColor(getColor(R.color.success_color));
                }

                if (validationResult == Validator.ValidationResult.Invalid) {
                    view.setText(getString(R.string.error_not_available));
                    view.setTextColor(getColor(R.color.error_color));
                }
            }
        };

        // Add the observers
        userNameValidator.observe(userNameStatusObserver, continueButtonObserver);

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
                    userNameValidator.validate();
                } else {
                    resetViews();
                }
            }
        });
    }

    void resetViews() {
        ((TextView) findViewById(R.id.username_status)).setText("");
        findViewById(R.id.continue_button).setEnabled(false);
    }
}

