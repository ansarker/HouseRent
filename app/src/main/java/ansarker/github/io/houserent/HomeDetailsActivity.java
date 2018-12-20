package ansarker.github.io.houserent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.Rent;

public class HomeDetailsActivity extends AppCompatActivity {

    private TextView tvPostTitle;
    private TextView tvPostBy;
    private TextView tvPostDate;
    private TextView tvLocation;
    private ImageView ivPostImage;
    private TextView tvPostHousePrice;
    private TextView tvPostPeriod;
    private TextView tvAddress;
    private TextView tvPostDescription;
    private TextView tvNumOfBeds;
    private TextView tvNumOfBaths;
    private TextView tvContact;
    private TextView tvEmail;
    private Button btnContactPostOwner;
    private Button btnLocation;

    private DatabaseReference rentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_details);
        rentDatabase = new FirebaseHandler().getFirebaseConnection("Rent");

        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvPostBy = findViewById(R.id.tvPostBy);
        tvPostDate = findViewById(R.id.tvPostDate);
        tvLocation = findViewById(R.id.tvLocation);
        ivPostImage = findViewById(R.id.ivPostImage);
        tvPostHousePrice = findViewById(R.id.tvPostHousePrice);
        tvAddress = findViewById(R.id.tvAddress);
        tvPostPeriod = findViewById(R.id.tvPostPeriod);
        tvPostDescription = findViewById(R.id.tvPostDescription);
        tvNumOfBeds = findViewById(R.id.tvNumOfBeds);
        tvNumOfBaths = findViewById(R.id.tvNumOfBaths);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        btnLocation = findViewById(R.id.btnLocation);
        btnContactPostOwner = findViewById(R.id.btnContactPostOwner);

//        not working on v lower 21, they don't support png files
//        ivPostImage.setImageResource(R.drawable.signboard_for_rent);
        ivPostImage.setImageResource(R.drawable.ic_for_rent_signage);

        Bundle bundle = getIntent().getExtras();

        String title = bundle.getString("title");
        String postBy = bundle.getString("postBy");
        String date = bundle.getString("date");
        String location = bundle.getString("location");
        String fee = bundle.getString("fee");
        String period = bundle.getString("period");
        final String address = bundle.getString("address");
        String description = bundle.getString("description");
        int numBeds = bundle.getInt("beds");
        int numBaths = bundle.getInt("baths");
        final String contact = bundle.getString("contact");
        String email = bundle.getString("email");

        tvPostTitle.setText(title);
        tvPostBy.setText("Post by, " + postBy);
        tvPostDate.setText(" at " + date);
        tvLocation.setText("Location: " + location);
        tvPostHousePrice.setText(HomeDetailsActivity.this.getString(R.string.bdt_sign) + " " + fee);
        tvPostPeriod.setText(" /" + period);
        tvAddress.setText(address);
        tvPostDescription.setText(description);
        if (numBeds <= 1) {
            tvNumOfBeds.setText(numBeds + " Bed");
        } else {
            tvNumOfBeds.setText(numBeds + " Beds");
        }

        if (numBaths <= 1) {
            tvNumOfBaths.setText(numBaths + " Bath");
        } else {
            tvNumOfBaths.setText(numBaths + " Baths");
        }

        tvContact.setText(contact);
        tvEmail.setText(email);

        btnContactPostOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + contact)));
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)));
            }
        });

    }
}
