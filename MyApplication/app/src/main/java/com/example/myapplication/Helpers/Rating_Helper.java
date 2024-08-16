package com.example.myapplication.Helpers;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.example.myapplication.Activities.Activity_Barber;
import com.example.myapplication.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Rating_Helper {

    private static final String TAG = "Rating_Helper";

    private String userName;
    private String selectedBarberId;
    private String selectedBarberName;

    private Context context;

    private List<Activity_Barber> barberList;

    private DatabaseReference usersRef;

    public Rating_Helper(Context context, String userName) {
        this.context = context;
        this.userName = userName;
        this.usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        this.barberList = new ArrayList<>();
    }

    public void loadBarbersForRating() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                barberList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userType = snapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        String barberId = snapshot.getKey();
                        String name = snapshot.child("name").getValue(String.class);
                        if (barberId != null && name != null) {
                            barberList.add(new Activity_Barber(barberId, name));
                        }
                    }
                }
                showBarberSelectionDialogForRating();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadBarbers:onCancelled", databaseError.toException());
            }
        });
    }

    private void showBarberSelectionDialogForRating() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_select_barber);

        ListView listViewBarbers = dialog.findViewById(R.id.list_view_barbers);
        ArrayAdapter<Activity_Barber> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, barberList);
        listViewBarbers.setAdapter(adapter);

        listViewBarbers.setOnItemClickListener((parent, view, position, id) -> {
            Activity_Barber selectedBarber = barberList.get(position);
            selectedBarberId = selectedBarber.getBarberId();
            selectedBarberName = selectedBarber.getBarberName();
            Toast.makeText(context, "Selected: " + selectedBarber.getBarberName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();

            showRatingDialog();
        });

        dialog.show();
    }

    private void showRatingDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_rate_barber);

        RatingBar ratingBar = dialog.findViewById(R.id.rating_bar);
        Button submitRatingButton = dialog.findViewById(R.id.button_submit_rating);

        submitRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = ratingBar.getRating();
                Log.d(TAG, "Rating: " + rating);
                saveRating(rating);
                dialog.dismiss();
            }
        });

        Button closeButton = dialog.findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveRating(float rating) {
        Log.d(TAG, "Saving rating for barber: " + selectedBarberName);
        usersRef.child(selectedBarberId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Barber exists: " + selectedBarberName);
                    String userType = snapshot.child("userType").getValue(String.class);
                    if ("barber".equals(userType)) {
                        int numOfRatings = snapshot.child("numOfRatings")
                                            .getValue(Integer.class) != null ? snapshot.child("numOfRatings")
                                            .getValue(Integer.class) : 0;
                        float currentRating = snapshot.child("rating")
                                                .getValue(Float.class) != null ? snapshot.child("rating")
                                                .getValue(Float.class) : 0.0f;

                        float newRating = ((currentRating * numOfRatings) + rating) / (numOfRatings + 1);

                        snapshot.getRef().child("rating").setValue(newRating);
                        snapshot.getRef().child("numOfRatings").setValue(numOfRatings + 1);

                        Toast.makeText(context, "Rating saved successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Cannot rate non-barber user.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Barber not found: " + selectedBarberName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "saveRating:onCancelled", error.toException());
            }
        });
    }
}
