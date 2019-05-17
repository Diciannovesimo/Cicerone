package com.nullpointerexception.cicerone.activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.nullpointerexception.cicerone.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    @Rule
    public ActivityTestRule<RegistrationActivity> mActivityTestRule = new ActivityTestRule<>(RegistrationActivity.class);

    @Test
    public void existTestEmail() {
        ViewInteraction editText = onView(
                allOf(withId(R.id.emailTextField),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        Assert.assertNotNull(editText.check(matches(isDisplayed())));
    }

    @Test
    public void existTestPassword() {
        ViewInteraction editText2 = onView(
                allOf(withId(R.id.passwordTextField),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        Assert.assertNotNull(editText2.check(matches(isDisplayed())));
    }

    @Test
    public void existTestConfirmTextField() {
        ViewInteraction editText3 = onView(
                allOf(withId(R.id.confirmTextField),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                2),
                        isDisplayed()));
        Assert.assertNotNull(editText3.check(matches(isDisplayed())));
    }

    @Test
    public void existTestFrameView() {
        ViewInteraction viewGroup2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.frameview),
                                0),
                        0),
                        isDisplayed()));
        Assert.assertNotNull(viewGroup2.check(matches(isDisplayed())));
    }

    @Test
    public void ButtonTest() {
        ViewInteraction imageView = onView(
                allOf(withId(R.id.nextImageView),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                3),
                        isDisplayed()));
        Assert.assertNotNull(imageView.check(matches(isDisplayed())));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
