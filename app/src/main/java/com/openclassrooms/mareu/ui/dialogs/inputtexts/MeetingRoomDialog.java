package com.openclassrooms.mareu.ui.dialogs.inputtexts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.ui.fragments.addmeeting.AddMeetingFragment;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This Dialog is displayed every time the user clicks on "Room meeting" TextInput of AddMeetingFragment
 * Used in @{@link AddMeetingFragment} fragment
 */

public class MeetingRoomDialog extends DialogFragment {

    // Interface
    private InputTextChangeCallback callback;

    public MeetingRoomDialog(){ /* Empty constructor */ }

    public MeetingRoomDialog(InputTextChangeCallback callback){

        this.callback = callback;
    }

    public void setCallback(InputTextChangeCallback callback){

        this.callback = callback;
    }

    /**
     * This method creates a new MeetingRoomDialog and show it
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Retain current DialogFragment instance
        setRetainInstance(true);

        // Create Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Set Builder
        builder.setView(inflater.inflate(R.layout.layout_dialog_meeting_rooms, null))
                .setTitle(R.string.title_dialog_meeting_room);

        return builder.create();
    }

    /**
     * Called when DialogFragment is visible
     */
    @Override
    public void onResume() {

        super.onResume();
        handleMeetingRoomSelection();
    }

    /**
     * Handles click on all "meeting room selection" buttons
     */
    public void handleMeetingRoomSelection() {

        // Initialize list
        List<Button> rooms = Arrays.asList(
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_1), // Feynman
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_2), // Bohr
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_3), // Heisenberg
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_4), // Newton
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_5), // Dirac
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_6), // Faraday
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_7), // Planck
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_8), // Schrodinger
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_9), // Einstein
                Objects.requireNonNull(getDialog()).findViewById(R.id.button_room_10) // Pauli
        );

        for (int i = 0; i < rooms.size(); i++) {
            rooms.get(i).setOnClickListener((View view) -> {
                        // Get name Meeting from button
                        String roomName = ((Button) view).getText().toString();
                        callback.onSetMeetingRoom(roomName);
                        // Close Dialog
                        getDialog().cancel();
                    }
            );
        }
    }
}
