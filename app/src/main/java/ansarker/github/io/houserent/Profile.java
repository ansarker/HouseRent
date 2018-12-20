package ansarker.github.io.houserent;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import ansarker.github.io.houserent.firebase.FirebaseHandler;

public class Profile extends AppCompatActivity {

    private TextView tvProfileUserRole;
    private TextView tvProfileUserName;
    private EditText tvProfileEmail;
    private EditText tvProfileFullName;
    private EditText tvProfileContact;
    private TextView tvProfileGender;
    private TextView tvProfileDateOfBirth;
    private EditText tvProfileAddress;

    private Button btnUpdate;
    private Button btnDeleteAccount;

    private String USER_TABLE = "User";
    private String RENT_TABLE = "Rent";
    private DatabaseReference userDatabase;
    private DatabaseReference rentDatabase;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userDatabase = new FirebaseHandler().getFirebaseConnection(USER_TABLE);
        rentDatabase = new FirebaseHandler().getFirebaseConnection(RENT_TABLE);

        tvProfileUserRole = findViewById(R.id.tvProfileUserRole);
        tvProfileUserName = findViewById(R.id.tvProfileUserName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfileFullName = findViewById(R.id.tvProfileFullName);
        tvProfileContact = findViewById(R.id.tvProfileContact);
        tvProfileGender = findViewById(R.id.tvProfileGender);
        tvProfileDateOfBirth = findViewById(R.id.tvProfileDateOfBirth);
        tvProfileAddress = findViewById(R.id.tvProfileAddress);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        tvProfileUserRole.setText("Logged in as: " + Availablity.currentUser.getRole());
        tvProfileUserName.setText(Availablity.currentUser.getUserName());
        tvProfileEmail.setText(Availablity.currentUser.getEmail());
        tvProfileFullName.setText(Availablity.currentUser.getName());
        tvProfileContact.setText(Availablity.currentUser.getPhone());
        tvProfileGender.setText(Availablity.currentUser.getGender());
        tvProfileDateOfBirth.setText(Availablity.currentUser.getDateOfBirth());
        tvProfileAddress.setText(Availablity.currentUser.getAddress());

        tvProfileGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] gender = {"Male","Female"};
                AlertDialog.Builder alert = new AlertDialog.Builder(Profile.this);
                alert.setTitle("Select Gender");
                alert.setSingleChoiceItems(gender,-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(gender[which]=="Male") {
                            tvProfileGender.setText("Male");
                        } else if (gender[which]=="Female") {
                            tvProfileGender.setText("Female");
                        }
                    }
                });
                alert.show();
            }
        });

        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                tvProfileDateOfBirth.setText(sdf.format(myCalendar.getTime()));
            }
        };

        tvProfileDateOfBirth.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
                new DatePickerDialog(Profile.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, Object> userChildUpdates = new HashMap<>();
                        userChildUpdates.put("address", tvProfileAddress.getText().toString().trim());
                        userChildUpdates.put("name", tvProfileFullName.getText().toString().trim());
                        userChildUpdates.put("email", tvProfileEmail.getText().toString().trim());
                        userChildUpdates.put("gender", tvProfileGender.getText().toString().trim());
                        userChildUpdates.put("phone", tvProfileContact.getText().toString().trim());
                        userChildUpdates.put("dateOfBirth", tvProfileDateOfBirth.getText().toString().trim());

                        userDatabase.child(Availablity.currentUser.getUserName()).updateChildren(userChildUpdates);
                        Toast.makeText(Profile.this, "Information update successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Profile.this)
                        .setTitle("Title")
                        .setMessage("Do you really want to Delete?\n" +
                                "Remember, Your ads will be also deleted.")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                Query rentsQuery = rentDatabase.orderByChild("userName").equalTo(Availablity.currentUser.getUserName());
                                Query usersQuery = userDatabase.child(Availablity.currentUser.getUserName());

                                rentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot rentSnapshot: dataSnapshot.getChildren()) {
                                            rentSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                usersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                                            userSnapshot.getRef().removeValue();
                                        }
                                        Availablity.currentUser.setUserName("");

                                        startActivity(new Intent(Profile.this, MainActivity.class));
                                        Toast.makeText(Profile.this, "Your account has deleted!", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("Tag", "onCancelled", databaseError.toException());
                                    }
                                });
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }
}
