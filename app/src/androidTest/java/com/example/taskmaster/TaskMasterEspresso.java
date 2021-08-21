package com.example.taskmaster;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import androidx.test.espresso.contrib.RecyclerViewActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.taskmaster.Activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TaskMasterEspresso {
//    @Rule
//    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testDisplayTheName() {
        // go to setting change the name the go back to home
        onView(withId(R.id.imageViewSettings)).perform(click());

        onView(withId(R.id.editTextUsername))
                .perform(clearText())
                .perform(typeText("Aseel"), closeSoftKeyboard());

        onView(withId(R.id.imageViewSave))
                .perform(click());

        onView(withId(R.id.fromSettingsActivityToHome))
                .perform(click());

        onView(withId(R.id.textViewTasks))
                .check(matches(withText("Aseel's Tasks")));

    }

    @Test
    public void testGoToTaskDetails() {
        // create task then tap on it to display its details
        onView(withId(R.id.buttonAddTaskActivity))
                .perform(click());

        onView(withId(R.id.editTextTaskTitle))
                .perform(clearText())
                .perform(typeText("coding"), closeSoftKeyboard());

        onView(withId(R.id.buttonAddTask))
                .perform(click());

        onView(withId(R.id.fromAddTaskActivityToHome))
                .perform(click());

        onView(ViewMatchers.withId(R.id.list)).check(matches(isDisplayed()));

        onView(withId(R.id.list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.textViewDetailsTaskTitle))
                .check(matches(withText("coding")));
    }


    @Test
    public void testTheMainActivity() {
        onView(withId(R.id.textViewMyTasks))
                .check(matches(withText("My Tasks")));
    }

    @Test
    public void testTheSettingActivity() {
        onView(withId(R.id.imageViewSettings))
                .perform(click());
        onView(withId(R.id.editTextUsername))
                .check(matches(withText("Please Enter Your Username")));
    }

}
