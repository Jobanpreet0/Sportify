package uk.ac.aston.cs3mdd.sportify000.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ac.aston.cs3mdd.sportify000.R;
import uk.ac.aston.cs3mdd.sportify000.activity.MainActivity;
import uk.ac.aston.cs3mdd.sportify000.fragments.MapFragment;
import uk.ac.aston.cs3mdd.sportify000.models.SportItem;


//class needed to handle the RecyclerView in FragmentGetRecommendations.java
public class SportAdapter extends RecyclerView.Adapter<SportAdapter.SportViewHolder> {
    private List<SportItem> sportsList; //store list of sports
    private final LayoutInflater mInflater; //used to inflate the xml file

    public SportAdapter(Context context, List<SportItem> sportsList) {
        mInflater = LayoutInflater.from(context); //store/inflate context data
        this.sportsList = sportsList;
    }

    //inflate one single item
    @NonNull
    @Override
    public SportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.sport_item, parent, false);
        return new SportViewHolder(mItemView, this);
    }

    //handle touch listener, set text and image for the item
    @Override
    public void onBindViewHolder(@NonNull SportViewHolder holder, int position) {

        //update variables with appropriate data
        SportItem sport = sportsList.get(position);
        holder.sportNameTextView.setText(sport.getName());
        holder.sportIconImageView.setImageResource(sport.getImageResourceId());

        // Set the sport object for this ViewHolder
        holder.setSport(sport);

        // Set touch listener for each row
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("AJB", "onTouch triggered"); // Log touch event
                    Log.i("AJB", "You touched " + holder.getSport().getName());
                    Log.i("AJB", "You touched " + holder.getSport().getLatitude());
                    if (holder.getSport() != null) {

                        // Handle item touch here
                        navigateToMapFragment(holder.getSport(), holder.getSport().getLatitude(), holder.getSport().getLongitude());
                    }
                    return true; // Consume the touch event
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.sportsList.size();
    }

    //update the data with new list from the parameter
    public void updateData(List<SportItem> list) {
        this.sportsList = list;
        notifyDataSetChanged();
    }

    //inner class to set and get each SportItem
    public static class SportViewHolder extends RecyclerView.ViewHolder {
        private TextView sportNameTextView;
        private ImageView sportIconImageView;
        private SportItem sport;

        public SportViewHolder(@NonNull View itemView, SportAdapter adapter) {
            super(itemView);
            sportNameTextView = itemView.findViewById(R.id.sportNameTextView);
            sportIconImageView = itemView.findViewById(R.id.sportIconImageView);
        }

        public SportItem getSport() {
            return sport;
        }

        public void setSport(SportItem sport) {
            this.sport = sport;
        }
    }

    //when user clicks on an sport, we move to the map fragment
    private void navigateToMapFragment(SportItem sportItem, double latitude, double longitude) {
        Log.d("SportAdapter", "Navigating to MapFragment");

        FragmentManager fragmentManager = ((FragmentActivity) mInflater.getContext()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MapFragment mapFragment = MapFragment.newInstance(sportItem, latitude, longitude);
        fragmentTransaction.replace(R.id.frame_layout, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Programmatically set the selected item in BottomNavigationView to the map without triggering a click event
        ((MainActivity) mInflater.getContext()).updateBottomNavigationView(R.id.map);
    }






}
