package ansarker.github.io.houserent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.User;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Spinner spinnerRoles;
    private Button btnSignUp;
    private TextView tvSignIn;

    private Context context;

    // firebase
    private String USER_TABLE = "User";

    FirebaseHandler firebaseHandler;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = SignupActivity.this;

//        firebase database
        firebaseHandler = new FirebaseHandler();
        userDatabase = firebaseHandler.getFirebaseConnection(USER_TABLE);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRoles = findViewById(R.id.spinnerRoles);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = etUsername.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String password = etPassword.getText().toString();
                String confirmPassword = etConfirmPassword.getText().toString();
                final String role = spinnerRoles.getSelectedItem().toString();

                if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Fill up all the fields!");
                    alert.show();
//                    Toast.makeText(context, "Fill up all the fields!", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                    alert.setTitle("Failure");
                    alert.setMessage("Passwords don't matched!");
                    alert.show();
//                    Toast.makeText(context, "Passwords don't matched!", Toast.LENGTH_SHORT).show();
                } else {
                    userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userName).exists()) {

                                AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                                alert.setTitle("Failure");
                                alert.setMessage("Username already taken!");
                                alert.show();

//                                Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show();
                            } else {
                                User user = new User(email, password, role);
                                userDatabase.child(userName).setValue(user);
//                                Toast.makeText(context, "Sign up done successfully", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder alert = new AlertDialog.Builder(SignupActivity.this);
                                alert.setTitle("Success");
                                alert.setMessage("Sign up done successfully!");
                                alert.show();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(context, MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }


}
