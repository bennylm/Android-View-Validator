package io.launchowl.viewvalidation;

import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class ValidatorTest {
    @Mock
    private TextView mockTextView;

    @Mock
    private Button mockButton1;

    @Mock
    private Button mockButton2;

    @Mock
    private Button mockButton3;

    @Mock
    private EditText mockEditText;

    @Mock
    private Editable mockEditable;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addValidationObserver_ThreeObservers_AddThreeObservers() throws Exception {
        Validator validator = new Validator<TextView>(mockTextView, new Criteria<TextView>() {
            @Override
            public void test(TextView TextView, TestCompleteListener testCompleteListener) {

            }
        }).limit(3);

        validator.observe(mockButton1Observer, mockButton2Observer, mockButton3Observer);

        assertEquals(3, validator.getObservers().size());
    }

    @Test
    public void getView_ViewsToBeEqual_TextView() throws Exception {
        Validator validator = new Validator<TextView>(mockTextView, null);

        assertEquals(mockTextView, validator.getView());
    }


    @Test
    public void validate_EditTextHasValidText_MockEditText() {
        final String validText = "Hello";

        when(mockEditText.getText()).thenReturn(mockEditable);
        when(mockEditable.toString()).thenReturn(validText);

        Validator validator = new Validator<EditText>(mockEditText, new Criteria<EditText>() {
            @Override
            public void test(EditText editText, TestCompleteListener testCompleteListener) {
                assertEquals(validText, editText.getText().toString());
            }
        });

        validator.validate();
    }

    Observer mockButton1Observer = new Observer<Button>(mockButton1) {
        @Override
        public void onValidationComplete(Button button, Validator.ValidationResult validationResult) {

        }
    };

    Observer mockButton2Observer = new Observer<Button>(mockButton2) {
        @Override
        public void onValidationComplete(Button button, Validator.ValidationResult validationResult) {
        }
    };

    Observer mockButton3Observer = new Observer<Button>(mockButton3) {
        @Override
        public void onValidationComplete(Button button, Validator.ValidationResult validationResult) {
        }
    };
}