package com.whf.demolist;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by @author WangHaoFei on 2017/11/13.
 */
@RunWith(AndroidJUnit4.class)
public class UiUnitTest {

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testText(){
        onView(withId(R.id.btn_unit_test)).perform(click());
        onView(withId(R.id.tv_unit_test)).perform(replaceText("Hello Word!")).check(matches(isDisplayed()));
    }

    public ViewInteraction getView(){
        return onView(withId(R.id.tv_unit_test)).perform(replaceText("Hello Word!")).check(matches(isDisplayed()));
    }
}
