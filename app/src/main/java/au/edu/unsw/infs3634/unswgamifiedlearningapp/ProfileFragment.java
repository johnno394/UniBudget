package au.edu.unsw.infs3634.unswgamifiedlearningapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

//This fragment allows the user to navigate to other options such as shop, about, edit profile and help
public class ProfileFragment extends Fragment {

    private static String fName, lName, userName;
    private static final String TAG = "Profile Fragment: ";
    private static TextView tvName, tvUsername, tvEditProfile, tvHelp, tvAbout, tvShop;
    private static Button logoutBtn;
    private static ImageButton iconEditProfile, iconHelp, iconAbout, iconShop;
    private static RelativeLayout editProfileBtn, helpBtn, aboutBtn, shopBtn;
    AlertDialog.Builder builder;
    DatabaseReference db = FirebaseDatabase.getInstance().getReferenceFromUrl("https://infs3634-group-69-default-rtdb.firebaseio.com/");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container,false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // instantiate ui elements
        tvName = view.findViewById(R.id.profileFragName);
        tvUsername = view.findViewById(R.id.profileFragUsername);
        logoutBtn = view.findViewById(R.id.btnLogout);

        //Relative layouts as buttons
        editProfileBtn = view.findViewById(R.id.btnEditProfile);
        helpBtn = view.findViewById(R.id.btnHelp);
        aboutBtn = view.findViewById(R.id.btnAbout);
        shopBtn = view.findViewById(R.id.btnShop);
        tvEditProfile = view.findViewById(R.id.tvEditProfile);
        tvHelp = view.findViewById(R.id.tvHelp);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvShop = view.findViewById(R.id.tvShop);
        iconEditProfile = view.findViewById(R.id.iconEditProfile);
        iconHelp = view.findViewById(R.id.iconHelp);
        iconAbout = view.findViewById(R.id.iconAbout);
        iconShop = view.findViewById(R.id.iconShop);

        // Get user
        userName = getActivity().getIntent().getStringExtra("FLAG");
        Log.d(TAG, "Username received: " + userName);

        // Call method to set ui elements
        prePopulateUserFields(userName);

        // Logout button on click listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(view.getContext()); //Home is name of the activity
                builder.setMessage("Do you want to log out?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        getActivity().finish();
                        Intent i=new Intent();
                        i.putExtra("finish", true);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().finish();
                    }
                });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert=builder.create();
                alert.show();
            }
        });

        //Go to edit profile clicks
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });
        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });
        iconEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();
            }
        });

        //Go to help screen
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHelp();
            }
        });
        tvHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHelp();
            }
        });
        iconHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHelp();
            }
        });

        //Go to about screen
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAbout();
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAbout();
            }
        });
        iconAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAbout();
            }
        });

        //Go to shop
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShop();
            }
        });
        tvShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShop();
            }
        });
        iconShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToShop();
            }
        });



    }

    @Override
    public void onResume() {
        super.onResume();
        //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        prePopulateUserFields(userName);
    }

    private void goToShop() {
        Intent i = new Intent(getActivity(), ShopActivity.class);
        i.putExtra("username", userName);
        Log.d(TAG, "Switching to shop screen");
        startActivity(i);
    }

    private void goToAbout() {
        Intent i = new Intent(getActivity(), AboutProfileActivity.class);
        i.putExtra("username", userName);
        Log.d(TAG, "Switching to about profile scree");
        startActivity(i);
    }

    private void goToHelp() {
        Intent i = new Intent(getActivity(), HelpProfileActivity.class);
        i.putExtra("username", userName);
        Log.d(TAG, "Switching to help screen");
        startActivity(i);
    }

    private void goToEditProfile() {
        Intent i = new Intent(getActivity(), EditProfileActivity.class);
        i.putExtra("username", userName);
        Log.d(TAG, "Switching to edit profile screen");
        startActivity(i);
    }

    //Loads user data using username and populates text fields
        private void prePopulateUserFields(String username) {
            //Retrieve user info and set it into profile
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // Set ui elements
                    fName = String.valueOf(snapshot.child("users").child(username).child("first name").getValue());
                    lName = String.valueOf(snapshot.child("users").child(username).child("last name").getValue());

                    tvName.setText(fName + " " + lName);
                    Log.d(TAG, "Full name is set as: " + fName + " " + lName);
                    tvUsername.setText("@" + username);
                    Log.d(TAG, "Full name is set as: " + username);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }