package ansarker.github.io.houserent;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.Rent;
import ansarker.github.io.houserent.model.User;

public class PostAdActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etLocation;
    private EditText etRentFee;
    private Spinner spinnerPeriod;
    private EditText etAddress;
    private EditText etDescription;
    private EditText etNumOfBeds;
    private EditText etNumOfBaths;
    private EditText etContactName;
    private EditText etContactNumber;
    private EditText etContactEmail;
    private Button btnPost;

    private String RENT_TABLE = "Rent";
    private String USER_TABLE = "User";

    DatabaseReference rentDatabase;
    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);

        rentDatabase = new FirebaseHandler().getFirebaseConnection(RENT_TABLE);
        userDatabase = new FirebaseHandler().getFirebaseConnection(USER_TABLE);

        etTitle = findViewById(R.id.etTitle);
        etLocation = findViewById(R.id.etLocation);
        etRentFee = findViewById(R.id.etRentFee);
        spinnerPeriod = findViewById(R.id.spinnerPeriod);
        etAddress = findViewById(R.id.etAddress);
        etDescription = findViewById(R.id.etDescription);
        etNumOfBeds = findViewById(R.id.etNumOfBeds);
        etNumOfBaths = findViewById(R.id.etNumOfBaths);
        etContactName = findViewById(R.id.etContactName);
        etContactNumber = findViewById(R.id.etContactNumber);
        etContactEmail = findViewById(R.id.etContactEmail);
        btnPost = findViewById(R.id.btnPost);

        if (Availablity.currentUser.getName().equals("")) {
            etContactName.setEnabled(true);
        } else {
            etContactName.setText(Availablity.currentUser.getName());
            etContactName.setEnabled(false);
        }
        etContactEmail.setText(Availablity.currentUser.getEmail());
        etContactEmail.setEnabled(false);

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String adTitle = etTitle.getText().toString().trim();
                final String adLocation = etLocation.getText().toString().trim();
                final String rentFee = etRentFee.getText().toString().trim();
                final String periodOfTime;
                if (spinnerPeriod.getSelectedItem().toString().equals("Monthly")) {
                    periodOfTime = "month";
                } else {
                    periodOfTime = "year";
                }
                final String address = etAddress.getText().toString().trim();
                final String description = etDescription.getText().toString().trim();
                final String beds = etNumOfBeds.getText().toString().trim();
                final String baths = etNumOfBaths.getText().toString().trim();
                final String userName = Availablity.currentUser.getUserName();
                final String contactName = etContactName.getText().toString().trim();
                String contactNumber = etContactNumber.getText().toString().trim();
                String contactEmail = etContactEmail.getText().toString().trim();

                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat mdformat = new SimpleDateFormat();
                final String strDate = mdformat.format(calendar.getTime());

                if (adTitle.equals("") || adLocation.equals("") || rentFee.equals("") || address.equals("") ||
                        description.equals("") || beds.equals("") || baths.equals("") || contactName.equals("") ||
                        contactNumber.equals("")) {
                    Toast.makeText(PostAdActivity.this, "Fill up all fields!", Toast.LENGTH_SHORT).show();
                } else {
//                    userDatabase.child(userName).updateChildren();
                    if (Availablity.currentUser.getName().equals("")) {
                        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                User user = new User(contactName);
//                                Map<String, Object> userValues = user.toMap();

                                Map<String, Object> userChildUpdates = new HashMap<>();
//                                childUpdates.put("/User/" + userName, userValues);
                                userChildUpdates.put("name", contactName);

                                userDatabase.child(userName).updateChildren(userChildUpdates);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    final String id = rentDatabase.push().getKey();
                    Rent rent = new Rent(adTitle, adLocation, address, rentFee, periodOfTime, description, Integer.parseInt(beds), Integer.parseInt(baths), userName, contactNumber, strDate, id);
                    rentDatabase.child(id).setValue(rent);
                    new AlertDialog.Builder(PostAdActivity.this).setMessage("Your ad has posted!").show();
//                    Toast.makeText(PostAdActivity.this, "Your ad has posted!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
