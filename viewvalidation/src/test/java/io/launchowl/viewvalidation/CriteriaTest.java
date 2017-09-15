package io.launchowl.viewvalidation;

import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.Condition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class CriteriaTest {
    @Mock
    private TextView mockTextView;

    @Mock
    private EditText mockEditText;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /*@Test
    public void test_ValidResult_ValidState() throws Exception {
        Criteria<TextView> textViewAsyncCondition = new Criteria<TextView>() {
            @Override
            public void test(TextView textView, final TestCompleteListener testCompleteListener) {
                SomeAsyncTask.doAsync(new SomeAsyncTask.SomeAsyncTaskCompleteListener() {
                    @Override
                    public void onAsyncTaskComplete() {
                        testCompleteListener.onComplete(Validator.ValidationResult.Valid);

                    }
                });
            }
        };


        textViewAsyncCondition.test(mockTextView, new TestCompleteListener() {
           @Override
            public void onComplete(Validator.ValidationResult validationResult) {
               assertEquals(Validator.ValidationResult.Valid, validationResult);
           }
        });
    }

    @Test
    public void test_ViewsToBeEqual_TextView() throws Exception {
        Criteria<TextView> textViewCriteria = new Criteria<TextView>() {
            @Override
            public void test(TextView textView, final TestCompleteListener asyncConditionListener) {
                assertEquals(mockTextView, textView);
            }
        };

        textViewCriteria.test(mockTextView, null);
    }*/

    @Test
    public void test_Three_AddThreeConditions() {
        Criteria<EditText> criteria = new Criteria<EditText>(mockEditText);
        criteria.test(new Criteria.Condition<EditText>() {
            @Override
            public boolean evaluate(EditText view) {
                return true;
            }
        }).test(new Criteria.Condition<EditText>() {
            @Override
            public boolean evaluate(EditText view) {
                return true;
            }
        }).test(new Criteria.Condition<EditText>() {
            @Override
            public boolean evaluate(EditText view) {
                return true;
            }
        });

        assertEquals(3, criteria.getConditions().size());
    }

    @Test
    public void test_Two_AddThreeAsyncConditions() {
        Criteria<EditText> criteria = new Criteria<EditText>(mockEditText);
        criteria.asyncTest(new Criteria.AsyncCondition<EditText>() {
            @Override
            public void evaluate(Criteria<EditText> criteria, EditText view) {

            }
        }).asyncTest(new Criteria.AsyncCondition<EditText>() {
            @Override
            public void evaluate(Criteria<EditText> criteria, EditText view) {

            }
        });

        assertEquals(2, criteria.getAsyncConditions().size());
    }

    @Test
    public void test_MixedTestsCompletesAsInvalid_MixedConditions() {
        Criteria<EditText> criteria = new Criteria<EditText>(mockEditText);
        criteria.asyncTest(new Criteria.AsyncCondition<EditText>() {
            @Override
            public void evaluate(final Criteria<EditText> criteria, EditText view) {
                criteria.asyncConditionComplete(false);
            }
        }).test(new Criteria.Condition<EditText>() {
            @Override
            public boolean evaluate(EditText view) {
                return true;
            }
        });

        criteria.evaluate(new Criteria.EvalCompleteListener() {
            @Override
            public void onComplete(Validator.ValidationResult validationResult) {
                assertEquals(Validator.ValidationResult.Invalid, validationResult);
            }
        });
    }

    @Test
    public void test_AsyncTestsCompletesAsValid_TwoAsyncConditions() {
        Criteria<EditText> criteria = new Criteria<EditText>(mockEditText);
        criteria.asyncTest(new Criteria.AsyncCondition<EditText>() {
            @Override
            public void evaluate(final Criteria<EditText> criteria, EditText view) {
                criteria.asyncConditionComplete(true);
            }
        }).asyncTest(new Criteria.AsyncCondition<EditText>() {
            @Override
            public void evaluate(final Criteria<EditText> criteria, EditText view) {
                criteria.asyncConditionComplete(true);
            }
        });

        criteria.evaluate(new Criteria.EvalCompleteListener() {
            @Override
            public void onComplete(Validator.ValidationResult validationResult) {
                assertEquals(Validator.ValidationResult.Valid, validationResult);
            }
        });
    }
}