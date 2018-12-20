package ansarker.github.io.houserent;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.User;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignIn;
    private TextView tvSignUp;

    // firebase
    private String USER_TABLE = "User";

    private FirebaseHandler firebaseHandler;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHandler = new FirebaseHandler();
        userDatabase = firebaseHandler.getFirebaseConnection(USER_TABLE);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvSignUp = findViewById(R.id.tvSignUp);

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = etUsername.getText().toString().trim();
                final String password = etPassword.getText().toString().trim();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter your Username and Password", Toast.LENGTH_SHORT).show();
                } else {
                    userDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child(userName).exists()) {
                                User user = new User();
                                user = dataSnapshot.child(userName).getValue(User.class);
                                user.setUserName(dataSnapshot.child(userName).getKey());
                                Availablity.isLoggedIn = true;
                                if (user.getPassword().equals(password)) {
                                    Toast.makeText(MainActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                    Intent dashboardIntent = new Intent(MainActivity.this, Dashboard.class);
                                    Availablity.currentUser = user;
                                    startActivity(dashboardIntent);
                                    finish();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "User not exists!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
