package com.openclassrooms.mareu;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import com.openclassrooms.mareu.di.DI;
import com.openclassrooms.mareu.testutils.DeleteViewAction;
import com.openclassrooms.mareu.ui.MainActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.openclassrooms.mareu.testutils.RecyclerViewItemCountAssertion.withItemCount;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Instrumented test file to test the list displayed of ListMeetingsFragment
 */
@RunWith(AndroidJUnit4.class)
public class MeetingListTest {

    private static final int NB_MEETINGS = DI.getListApiService().getListMeetings().size();

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp(){
        MainActivity mainActivity = mMainActivityRule.getActivity();
        assertThat(mainActivity, notNullValue());
    }

    /**
     * Test to check if ListMeetingsFragment is correctly displayed after launching application
     */
    @Test
    public void checkIf_ListMeetingsFragment_isDisplayed() {

        onView(withId(R.id.list_meetings_fragment))
                .check(matches(isDisplayed()));
    }

    /**
     * Test to check if recyclerview displays at least one element
     */
    @Test
    public void checkIf_listOfMeetings_isNotBeEmpty() {

        onView(withId(R.id.recycler_view_list_meetings)).check(matches(hasMinimumChildCount(1)));
    }

    /**
     * Test to check if "Confirm Suppression" Dialog is displayed when clicking in a "delete" icon of
     * an recyclerview item
     */
    @Test
    public void checkIf_deleteMeetingIcon_displays_confirmSuppressDialog() {

        // Perform a click on a delete icon
        onView(withId(R.id.recycler_view_list_meetings))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new DeleteViewAction()));

        // Check if Dialog is correctly displayed
        onView(ViewMatchers.withText(R.string.title_dialog_confirm_delete))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     * Test to check if list is correctly updated after suppress confirmation with Dialog
     */
    @Test
    public void checkIf_deleteAction_removeItemFromMeetingList() {

        onView(withId(R.id.recycler_view_list_meetings))
                .check(withItemCount(NB_MEETINGS));

        // Perform a click on a delete icon to remove associated Meeting
        onView(withId(R.id.recycler_view_list_meetings))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, new DeleteViewAction()));

        // Perform a click on positive Dialog button
        onView(withId(android.R.id.button1))
                .inRoot(isDialog())
                .perform(click());

        // Check if size of list of Meetings has changed
        onView(withId(R.id.recycler_view_list_meetings))
                .check(withItemCount(NB_MEETINGS-1));
    }

    @Test
    public void checkIf_AddMeetingFragment_isLaunched_onFabCLick(){

        // Click on Floating Button
        onView(withId(R.id.fab_list_meetings_fragment))
                .perform(click());

        // Check if fragment is displayed to user
        onView(withId(R.id.add_meeting_fragment))
                .check(matches(isDisplayed()));
    }
}
