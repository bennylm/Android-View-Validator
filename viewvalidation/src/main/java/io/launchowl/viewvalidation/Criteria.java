package io.launchowl.viewvalidation;

import android.view.View;

public interface Criteria<T extends View> {
    void test(T t, TestCompleteListener testCompleteListener);
}
