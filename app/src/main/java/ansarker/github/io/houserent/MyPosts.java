package ansarker.github.io.houserent;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.Rent;
import ansarker.github.io.houserent.model.User;

public class MyPosts extends AppCompatActivity implements  RentAdapter.ItemClicked {

    private RecyclerView rvMyPosts;
    private RecyclerView.Adapter houseAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<Rent> rent;

    private String USER_TABLE = "User";
    private String RENT_TABLE = "Rent";

    FirebaseHandler firebaseHandler;
    DatabaseReference rentDatabase;
    DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        firebaseHandler = new FirebaseHandler();
        rentDatabase = firebaseHandler.getFirebaseConnection(RENT_TABLE);
        userDatabase = firebaseHandler.getFirebaseConnection(USER_TABLE);

        rvMyPosts = findViewById(R.id.rvMyPosts);
        rvMyPosts.setHasFixedSize(true);

        rent = new ArrayList<Rent>();
        rentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot rentSnapshot: dataSnapshot.getChildren()) {
                    Rent rents = rentSnapshot.getValue(Rent.class);
                    if (rents.getUserName().equals(Availablity.currentUser.getUserName())) {
                        rent.add(new Rent(rents.getTitle(), rents.getLocation(), rents.getAddress(),
                                rents.getFee(), rents.getPeriod(), rents.getDescription(), rents.getNumOfBeds(),
                                rents.getNumOfBaths(), rents.getUserName(), rents.getContact(), rents.getDate(), rents.getKey()));
                    }
                }
                layoutManager = new LinearLayoutManager(MyPosts.this);
                houseAdapter = new RentAdapter(MyPosts.this, rent);

                rvMyPosts.setLayoutManager(layoutManager);
                rvMyPosts.setItemAnimator(new DefaultItemAnimator());
                rvMyPosts.setAdapter(houseAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClicked(final int index) {
        userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            Intent myAdDetailsIntent = new Intent(MyPosts.this, MyAdDetails.class);
            Bundle bundle = new Bundle();
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bundle.putString("title", rent.get(index).getTitle());
                bundle.putString("date", rent.get(index).getDate());
                bundle.putString("location", rent.get(index).getLocation());
                bundle.putString("fee", rent.get(index).getFee());
                bundle.putString("period", rent.get(index).getPeriod());
                bundle.putString("address", rent.get(index).getAddress());
                bundle.putString("description", rent.get(index).getDescription());
                bundle.putInt("beds", rent.get(index).getNumOfBeds());
                bundle.putInt("baths", rent.get(index).getNumOfBaths());
                bundle.putString("key", rent.get(index).getKey());

                User user = dataSnapshot.child(rent.get(index).getUserName()).getValue(User.class);

                bundle.putString("postBy", user.getName());
                bundle.putString("contact", rent.get(index).getContact());
                bundle.putString("email", user.getEmail());

                myAdDetailsIntent.putExtras(bundle);
                startActivity(myAdDetailsIntent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (this.getApplicationContext() instanceof HomeDetailsActivity) {
            Toast.makeText(getApplicationContext(), "Nothing", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.myposts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuClearPost) {
            new AlertDialog.Builder(MyPosts.this)
                    .setTitle("Title")
                    .setMessage("Do you really want to Delete all posts?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {

                            Query rentsQuery = rentDatabase.orderByChild("userName").equalTo(Availablity.currentUser.getUserName());
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
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        }
        return true;
    }
}
