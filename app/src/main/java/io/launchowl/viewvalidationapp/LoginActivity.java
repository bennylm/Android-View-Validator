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
import io.launchowl.viewvalidation.TestCompleteListener;
import io.launchowl.viewvalidation.ValidationCompleteListener;
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
        // Create an instance of the Validator for the EditText view with an
        // id of "user_name".
        EditText userNameEditText = (EditText) findViewById(R.id.user_name);
        final Validator<EditText> userNameValidator = new Validator<>(userNameEditText, new Criteria<EditText>() {
            Validator.ValidationResult validationResult;

            @Override
            public void test(EditText editText, final TestCompleteListener testCompleteListener) {
                validationResult = Validator.ValidationResult.Valid;
                String userName = editText.getText().toString();

                // Make sure it's alphanumeric
                if (!Pattern.matches("\"^[a-zA-Z0-9]*$\"", userName)) {
                    testCompleteListener.onComplete(validationResult);
                }

                // Make sure it's available
                UserGetterAsyncTask.getUser(editText.getText().toString(), new UserGetterAsyncTask.OnUserRetrievedListener() {
                    @Override
                    public void onUserRetrieved(User user) {
                        validationResult = (user == null ? Validator.ValidationResult.Valid : Validator.ValidationResult.Invalid);
                        testCompleteListener.onComplete(validationResult);
                    }
                });
            }
        });
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

        // Set the observers that will listen for the results
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
                }
            }
        });
    }

    private static class UserGetterAsyncTask {
        static void getUser(String userName, OnUserRetrievedListener onUserRetrievedListener) {
            UserRepository userRepository = new UserRepository();
            onUserRetrievedListener.onUserRetrieved(userRepository.getUser(userName));
        }

        private interface OnUserRetrievedListener {
            void onUserRetrieved(User user);
        }
    }
}

