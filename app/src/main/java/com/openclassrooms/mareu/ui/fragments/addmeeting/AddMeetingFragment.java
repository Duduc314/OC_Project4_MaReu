package com.openclassrooms.mareu.ui.fragments.addmeeting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.di.DI;
import com.openclassrooms.mareu.model.Employee;
import com.openclassrooms.mareu.model.Meeting;
import com.openclassrooms.mareu.service.ListEmployeesGenerator;
import com.openclassrooms.mareu.ui.MainActivityCallback;
import com.openclassrooms.mareu.ui.dialogs.inputtexts.DatePickerMeetingDialog;
import com.openclassrooms.mareu.ui.dialogs.inputtexts.InputTextChangeCallback;
import com.openclassrooms.mareu.ui.dialogs.inputtexts.MeetingRoomDialog;
import com.openclassrooms.mareu.ui.dialogs.inputtexts.TimePickerMeetingDialog;
import com.openclassrooms.mareu.ui.MainActivity;
import com.openclassrooms.mareu.ui.dialogs.inputtexts.TimeType;
import com.openclassrooms.mareu.ui.fragments.listemployees.ListEmployeesFragment;
import com.openclassrooms.mareu.utils.TextWatcherTextInput;
import com.openclassrooms.mareu.utils.TimeComparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The AddMeetingFragment fragment is used to create a new Meeting, allowing user
 * to edit meeting information by editing TextInputEditText fields.
 */
public class AddMeetingFragment extends Fragment implements InputTextChangeCallback  {

    public static final String TAG = "TAG_ADD_MEETING_FRAGMENT";

    private MaterialButton cancelButton;
    private MaterialButton okButton;

    // Edit fields
    private TextInputEditText objectMeetingInput;
    private TextInputEditText roomMeetingInput;
    private TextInputEditText dateInput;
    private TextInputEditText hourStartInput;
    private TextInputEditText hourEndInput;
    private TextInputEditText participantsInput;
    private TextInputEditText informationInput;

    // Edit Layout
    private TextInputLayout objectMeetingLayout;
    private TextInputLayout roomMeetingLayout;
    private TextInputLayout dateLayout;
    private TextInputLayout hourStartLayout;
    private TextInputLayout hourEndLayout;
    private TextInputLayout participantsLayout; // Updated with number of participants

    // For meeting participants selection
    private String selection = "";
    private int nbMeetingEmployees = 0;
    private final List<Employee> meetingEmployees = new ArrayList<>();
    private final String TAG_SELECTION = "TAG_SELECTION";

    // Dialogs
    private MeetingRoomDialog meetingRoomDialog;

    // Dialog TAG
    private final String MEETING_ROOM_DIALOG_TAG = "MEETING_ROOM_DIALOG_TAG";
    private final String DATE_DIALOG_TAG = "DATE_DIALOG_TAG";
    private final String HOUR_BEGIN_DIALOG_TAG = "HOUR_BEGIN_DIALOG_TAG";
    private final String HOUR_END_DIALOG_TAG = "HOUR_END_DIALOG_TAG";

    public AddMeetingFragment() { /* Empty constructor */ }

    public static AddMeetingFragment newInstance() { return new AddMeetingFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {

        super.onResume();
        getNewSelectionFromListEmployeesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_meeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        initializeIds(view);
        initializeToolbar();

        // Listeners for "CANCEL" and "OK" buttons
        handleButtonsListeners();

        // Listeners for all TextInputEditText fields
        onTextInputsEditTextListeners();

        // Restore listEmployee
        if (savedInstanceState != null) {
            // Restore "Selection" value
            selection = savedInstanceState.getString(TAG_SELECTION);
            if(selection != null){
                createListEmployeesForMeeting();
                nbMeetingEmployees = meetingEmployees.size();
                updateParticipantsTextInput();
            }
            else {
                selection = "";
            }

            // Restore msg Error boolean value
            hourEndLayout.setErrorEnabled(savedInstanceState.getBoolean("errorMsg", false));
        }

        // Listener for "Object Meeting" TextInputLayout
        handleOkBtnClickableStatus();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_add_meeting_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            // Remove AddMeetingFragment from stack
            ((MainActivityCallback) getActivity()).popBack();
        }
        else {
            // Update participants input layout hint text
            participantsLayout.setHint(getActivity().getResources().getString(R.string.edit_input_participants_meeting));
            // Reset Error display
            if (hourEndLayout.isErrorEnabled()) {
                hourEndLayout.setErrorEnabled(false);
            }
            // Reset nb Employee
            nbMeetingEmployees = 0;
        }
        // Reset text Inputs
        clearTextInputsFields();
        // Reset error enabled display
        resetErrorEnabled();

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initializeToolbar() {

        // Add title
        ((MainActivityCallback) getActivity()).setToolbarTitle(R.string.toolbar_name_frag_add_meeting);
        ((MainActivityCallback) getActivity()).setHomeAsUpIndicator(true);
    }

    private void initializeIds(View view) {

        // Buttons
        cancelButton = view.findViewById(R.id.cancel_button);
        okButton = view.findViewById(R.id.ok_button);

        // TextInputEditText fields
        objectMeetingInput = view.findViewById(R.id.text_input_object_meeting);
        roomMeetingInput = view.findViewById(R.id.text_input_room_meeting);
        dateInput = view.findViewById(R.id.text_input_date);
        hourStartInput = view.findViewById(R.id.text_input_hour_start);
        hourEndInput = view.findViewById(R.id.text_input_hour_end);
        participantsInput = view.findViewById(R.id.text_input_participants);
        informationInput = view.findViewById(R.id.text_input_information);

        // TextInputLayout fields
        objectMeetingLayout = view.findViewById(R.id.text_layout_object_meeting);
        roomMeetingLayout = view.findViewById(R.id.text_layout_room_meeting);
        dateLayout = view.findViewById(R.id.text_layout_date);
        hourStartLayout = view.findViewById(R.id.text_layout_hour_start);
        hourEndLayout = view.findViewById(R.id.text_layout_hour_end);
        participantsLayout = view.findViewById(R.id.text_layout_participants);

        updateDialogs();
    }

    /**
     * Handles click interactions on both bottom fragment buttons :
     * - Click on "CANCEL" cancels new meeting creation and close fragment
     * - Click on "OK" add new meeting to the meetings list and close fragment
     */
    private void handleButtonsListeners() {
        // Set listeners
        cancelButton.setOnClickListener((View view) -> {
                    MainActivity.getListEmployeesFragment().resetSelectionParameter();
                    // Reset text inputs
                    clearTextInputsFields();
                    // Reset error enabled
                    resetErrorEnabled();
                    // Remove AddMeetingFragment from stack
                    ((MainActivityCallback) getActivity()).popBack();
                }
        );

        okButton.setOnClickListener((View view) -> {
                    // Create New Meeting
                    Meeting meeting = createNewMeeting();
                    DI.getListApiService().addMeeting(meeting);
                    // Reset text inputs for next Meeting creation
                    clearTextInputsFields();
                    // Reset error enabled
                    resetErrorEnabled();
                    // Remove AddMeetingFragment from stack
                    ((MainActivityCallback) getActivity()).popBack();
                }
        );
    }

    /**
     * This method resets all textInput fields
     */
    public void clearTextInputsFields() {

        Objects.requireNonNull(objectMeetingInput.getText()).clear();
        Objects.requireNonNull(roomMeetingInput.getText()).clear();
        Objects.requireNonNull(dateInput.getText()).clear();
        Objects.requireNonNull(hourStartInput.getText()).clear();
        Objects.requireNonNull(hourEndInput.getText()).clear();
        Objects.requireNonNull(participantsInput.getText()).clear();
        Objects.requireNonNull(informationInput.getText()).clear();

        // Reset selection parameter
        resetSelectionParameter();
    }

    /**
     * Retrieve instance of dialogs in the fragment stack and update references to input texts
     */
    private void updateDialogs() {

        // Date Fragment
        Fragment dateFragment = getParentFragmentManager().findFragmentByTag(DATE_DIALOG_TAG);
        if (dateFragment instanceof DatePickerMeetingDialog) { ((DatePickerMeetingDialog) dateFragment).setCallback(this); }

        // Hour Start Fragment
        Fragment hourStartFragment = getParentFragmentManager().findFragmentByTag(HOUR_BEGIN_DIALOG_TAG);
        if(hourStartFragment instanceof TimePickerMeetingDialog){ ((TimePickerMeetingDialog) hourStartFragment).setCallback(this); }

        // Hour End Fragment
        Fragment hourEndFragment = getParentFragmentManager().findFragmentByTag(HOUR_END_DIALOG_TAG);
        if(hourEndFragment instanceof TimePickerMeetingDialog){ ((TimePickerMeetingDialog) hourEndFragment).setCallback(this); }

        // Room Fragment
        Fragment meetingRoomFragment = getParentFragmentManager().findFragmentByTag(MEETING_ROOM_DIALOG_TAG);
        if(meetingRoomFragment instanceof  MeetingRoomDialog){ ((MeetingRoomDialog) meetingRoomFragment).setCallback(this); }
    }

    /**
     * This methods handles clicks in TextInputEditText fields
     */
    private void onTextInputsEditTextListeners() {

        roomMeetingInput.setOnClickListener((View view) -> {
                    InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    meetingRoomDialog = new MeetingRoomDialog(this);
                    meetingRoomDialog.show(getParentFragmentManager(), MEETING_ROOM_DIALOG_TAG);
                }
        );

        dateInput.setOnClickListener((View view) -> {
                    String date = dateInput.getText() != null ? dateInput.getText().toString() : "//";
                    DatePickerMeetingDialog datePickerMeetingDialog = new DatePickerMeetingDialog(getContext(), date, this);
                    datePickerMeetingDialog.show(getParentFragmentManager(), DATE_DIALOG_TAG);
                }
        );

        hourStartInput.setOnClickListener((View view) -> {
                    String startHour = hourStartInput.getText() != null ? hourStartInput.getText().toString() : ":";
                    TimePickerMeetingDialog timePickerMeetingDialog = new TimePickerMeetingDialog(getContext(), startHour, TimeType.START, this);
                    timePickerMeetingDialog.show(getParentFragmentManager(), HOUR_BEGIN_DIALOG_TAG);
                }
        );

        hourEndInput.setOnClickListener((View view) -> {
                    String endHour = hourEndInput.getText() != null ? hourEndInput.getText().toString() : "";
                    TimePickerMeetingDialog timePickerMeetingDialog = new TimePickerMeetingDialog(getContext(), endHour, TimeType.END, this);
                    timePickerMeetingDialog.show(getParentFragmentManager(), HOUR_END_DIALOG_TAG);
                }
        );

        participantsInput.setOnClickListener((View view) -> {
                    // Store selection value in Bundle
                    Bundle previousResults = new Bundle();
                    previousResults.putString("oldSelectionString", selection);
                    getActivity().getSupportFragmentManager().setFragmentResult("oldSelection", previousResults);
                    ((MainActivity) getActivity()).changeFragment(MainActivity.getListEmployeesFragment(), ListEmployeesFragment.TAG);
                }
        );
    }

    /**
     * This method implements @{@link TextWatcherTextInput} objects as TextChangedListener for each TextInputLayout (except "Information" section)
     * If all these TextInputLayout contains a text specified by user, then the "Ok" button can be enabled
     */
    public void handleOkBtnClickableStatus() {

        Objects.requireNonNull(objectMeetingLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
        Objects.requireNonNull(roomMeetingLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
        Objects.requireNonNull(dateLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
        Objects.requireNonNull(hourStartLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
        Objects.requireNonNull(hourEndLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
        Objects.requireNonNull(participantsLayout.getEditText()).addTextChangedListener(new TextWatcherTextInput(this));
    }

    /**
     * Create a new Meeting object to send to ListMeetingsFragment
     * @return : Meeting
     */
    private Meeting createNewMeeting() {

        String objectMeeting = Objects.requireNonNull(objectMeetingInput.getText()).toString();
        String meetingRoom = Objects.requireNonNull(roomMeetingInput.getText()).toString();
        String date = Objects.requireNonNull(dateInput.getText()).toString();
        String hourBegin = Objects.requireNonNull(hourStartInput.getText()).toString();
        String hourEnd = Objects.requireNonNull(hourEndInput.getText()).toString();
        String information = Objects.requireNonNull(informationInput.getText()).toString();

        return new Meeting(objectMeeting, meetingRoom, date, hourBegin, hourEnd, information, meetingEmployees);
    }

    /**
     * Get selection from ListEmployeesFragment for TextInput participants display update
     */
    private void getNewSelectionFromListEmployeesFragment() {

        getActivity().getSupportFragmentManager().setFragmentResultListener("newSelection", this,
                (@NonNull String requestKey, @NonNull Bundle result) -> {
                // Get "selection" from ListMeetingsFragment
                selection = result.getString("newSelectionString");

                // Use "selection" to create new list of Employee
                createListEmployeesForMeeting();

                // Update text input participants display
                updateParticipantsTextInput();
            }
        );
    }

    /**
     * Generates list of Employee for current Meeting, according to
     * the "selection" String value sent by ListEmployeesFragment
     */
    public void createListEmployeesForMeeting() {

        // Prepare lists
        List<Employee> listEmployees = ListEmployeesGenerator.generateListEmployee();
        meetingEmployees.clear();

        // Reset nb Employee selected
        nbMeetingEmployees = 0;

        // Initialize create list meeting employees
        for (int i = 0; i < listEmployees.size(); i++) {
            if (Character.toString(selection.charAt(i)).equals("1")) {
                meetingEmployees.add(listEmployees.get(i));
                nbMeetingEmployees++;
            }
        }
    }

    /**
     * This method updates "Participants" TextInputEditText and TextInputLayout, according to the list meetingEmployees.
     */
    public void updateParticipantsTextInput() {

        String infoToDisplay = "";
        if (meetingEmployees.size() > 0) {
            // Update infoToDisplay String
            if (meetingEmployees.size() == 1) { infoToDisplay = meetingEmployees.get(0).getEmail(); }
            else { infoToDisplay = meetingEmployees.get(0).getEmail() + "..."; }

            // Update Layout hint
            participantsLayout.setHint(requireActivity().getResources().getString(R.string.edit_input_participants_meeting) + "(" + nbMeetingEmployees + ")");
        }
        else {
            // Reset Layout hint
            participantsLayout.setHint(requireActivity().getResources().getString(R.string.edit_input_participants_meeting));

            // reset Edit field
            participantsInput.getText().clear();
        }
        // Update Edit field
        participantsInput.setText(infoToDisplay);
    }

    /**
     * Method used to save data to prevent configuration change :
     *      - "selection" : to retrieve list of selected Employee after configuration change
     *      - error msg : to retrieve error msg status for hourEndLayout
     * @param outState : Bundle
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);

        // Save "selection" to retrieve list of Employee after configuration change
        if (selection != null) {
            if (!selection.equals("")) {
                outState.putString(TAG_SELECTION, selection);
            }
        }

        // Save error Msg boolean value
        if (hourEndLayout != null) {
            outState.putBoolean("errorMsg", hourEndLayout.isErrorEnabled());
        }
    }

    /**
     * To reset "selection" parameter value. Used when TextInputEditText are cleaned
     */
    public void resetSelectionParameter(){ selection = ""; }

    public void resetErrorEnabled() {

        if(hourEndLayout.isErrorEnabled()){
            hourEndLayout.setErrorEnabled(false);
        }
    }

    /**
     * TimeChangeCallback interface implementation :
     *      - onSetTime(String time, TimeType type) : updates hourStartInput or hourEndInput TextInputEditText field
     *      - onSetDate(String date) : updates dateInput TextInputEditText field
     *      - onSetMeetingRoom(String nameMeetingRoom) : updates roomMeetingInput TextInputEditText field
     *      - onCheckInputsTextStatus() : updates okButton status according to TextInputEditText fields and hourEndLayout error status
     */
    @Override
    public void onSetTime(String time, TimeType type) {

        if(type == TimeType.START){ hourStartInput.setText(time); }
        else{ hourEndInput.setText(time);}
        compareHourInputsFields();
    }

    @Override
    public void onSetDate(String date) {

        dateInput.setText(date);
    }

    @Override
    public void onSetMeetingRoom(String nameMeetingRoom) {

        roomMeetingInput.setText(nameMeetingRoom);
    }

    @Override
    public void onCheckInputsTextStatus() {

        if (Objects.requireNonNull(objectMeetingInput.getText()).length() > 0
                && Objects.requireNonNull(roomMeetingInput.getText()).length() > 0
                && Objects.requireNonNull(dateInput.getText()).length() > 0
                && Objects.requireNonNull(hourStartInput.getText()).length() > 0
                && Objects.requireNonNull(hourEndInput.getText()).length() > 0
                && Objects.requireNonNull(participantsInput.getText()).length() > 0) {
            okButton.setEnabled(!compareHourInputsFields());
        }
    }

    /**
     * Enables error message status displayed for hourEndLayout, if end hour detected is
     * not correct compareHourInputsFields
     */
    private boolean compareHourInputsFields() {

        if (Objects.requireNonNull(hourStartInput.getText()).length() > 0
                && Objects.requireNonNull(hourEndInput.getText()).length() > 0) {
            TimeComparator timeComparator = new TimeComparator();
            int resultTimeComparator = timeComparator.compare(hourStartInput.getText().toString(), hourEndInput.getText().toString());

            if (resultTimeComparator < 0) {
                hourEndLayout.setErrorEnabled(true);
                hourEndLayout.setError(getResources().getString(R.string.error_msg));
                return true;
            }
            else {
                hourEndLayout.setErrorEnabled(false);
                return false;
            }
        }
        return false;
    }
}