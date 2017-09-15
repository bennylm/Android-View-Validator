package io.launchowl.viewvalidation;

import android.widget.TextView;

import org.hamcrest.Condition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class CriteriaTest {
    @Mock
    private TextView mockTextView;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
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
    }

    private static class SomeAsyncTask {
        public static void doAsync(SomeAsyncTaskCompleteListener someAsyncTaskCompleteListener) {
            someAsyncTaskCompleteListener.onAsyncTaskComplete();
        }

        private interface SomeAsyncTaskCompleteListener {
            void onAsyncTaskComplete();
        }
    }
}