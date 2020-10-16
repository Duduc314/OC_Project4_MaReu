package com.openclassrooms.mareu.ui.fragments.listmeetings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.openclassrooms.mareu.R;
import com.openclassrooms.mareu.model.Meeting;
import com.openclassrooms.mareu.utils.DateAndTimeConverter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Adapter for @{@link ListMeetingsFragment} fragment
 */
public class RecyclerViewAdapterListMeetings extends RecyclerView.Adapter<RecyclerViewAdapterListMeetings.ViewHolderItemMeeting> {

    // Contains all Meeting to display
    private ArrayList<Meeting> listMeetings;

    // For handling user actions
    private final ListMeetingActionListener listener;

    //TODO: Utilise une list au lieu d'un ArrayList
    public RecyclerViewAdapterListMeetings(ArrayList<Meeting> listMeetings, ListMeetingActionListener listener) {
        this.listMeetings = listMeetings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolderItemMeeting onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_meetings_item, parent, false);
        return new ViewHolderItemMeeting(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItemMeeting holder, int position) {

        Date currentDate = Calendar.getInstance().getTime();

        compareDateMeetingToCurrentDate(listMeetings.get(position).getDate(), listMeetings.get(position).getHour());
        // Icon Status Item
        // TODO() : Use system Date to compare to date from Meeting item

        // Title Item
        String title = listMeetings.get(position).getObjectMeeting();
        holder.titleItem.setText(title);

        // Text Item
        String text = listMeetings.get(position).getDate() + " - " + listMeetings.get(position).getHour() + " - " + listMeetings.get(position).getMeetingRoom();
        holder.textItem.setText(text);

        // Subtext Item
        String subText = "";
        if (listMeetings.get(position).getListParticipants().size() > 1) {
            subText = listMeetings.get(position).getListParticipants().get(0).getEmail() + "...";
        } else {
            subText = listMeetings.get(position).getListParticipants().get(0).getEmail();
        }

        holder.subTextItem.setText(subText);

        // Icon Delete Item
        holder.iconDeleteItem.setOnClickListener((View view) -> {
                    listener.onDeleteItem(listMeetings.get(position));
                }
        );
    }

    @Override
    public int getItemCount() {
        return listMeetings.size();
    }

    static class ViewHolderItemMeeting extends RecyclerView.ViewHolder {

        private ImageView iconStatusItem;
        private TextView titleItem;
        private TextView textItem;
        private TextView subTextItem;
        private ImageView iconDeleteItem;

        ViewHolderItemMeeting(View view) {
            super(view);

            iconStatusItem = view.findViewById(R.id.img_item_recycler_view);
            titleItem = view.findViewById(R.id.title_item_recycler_view);
            textItem = view.findViewById(R.id.text_item_recycler_view);
            subTextItem = view.findViewById(R.id.subtext_item_recycler_view);
            iconDeleteItem = view.findViewById(R.id.icon_delete_item_recycler_view);
        }
    }

    public boolean compareDateMeetingToCurrentDate(String date, String time) {
        final Calendar calendar = Calendar.getInstance();
        // Time
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        String currentTime = DateAndTimeConverter.timeConverter(currentHour, currentMinutes);

        // Date
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = DateAndTimeConverter.dateConverter(currentYear, currentMonth, currentDay);

        return true;
    }
}
