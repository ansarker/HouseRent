package ansarker.github.io.houserent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ansarker.github.io.houserent.firebase.FirebaseHandler;
import ansarker.github.io.houserent.model.Rent;

class RentAdapter extends RecyclerView.Adapter<RentAdapter.ViewHolder> {

    private ArrayList<Rent> rent;
    private ItemClicked activity;

    public interface ItemClicked {
        void onItemClicked(int index);
    }

    public RentAdapter(Context context, ArrayList<Rent> rentList) {
        rent = rentList;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivHouse;
        private TextView tvTitle, tvFee, tvPeriod, tvLocationCardView;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            ivHouse = itemView.findViewById(R.id.ivHouse);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvFee = itemView.findViewById(R.id.tvFee);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            tvLocationCardView = itemView.findViewById(R.id.tvLocationCardView);

//            not working on api that lower than 21
//            ivHouse.setImageResource(R.drawable.signboard_for_rent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(rent.indexOf((Rent) v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public RentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_house, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RentAdapter.ViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(rent.get(i));
        if (rent.get(i).getTitle().length() > 24) {
            viewHolder.tvTitle.setText(rent.get(i).getTitle().substring(0, 20) + "...");
        } else {
            viewHolder.tvTitle.setText(rent.get(i).getTitle());
        }
        viewHolder.tvFee.setText(viewHolder.itemView.getContext()
                .getResources()
                .getString(R.string.bdt_sign) + " " + rent.get(i).getFee());
        viewHolder.tvPeriod.setText(" /" + rent.get(i).getPeriod());
        viewHolder.tvLocationCardView.setText(rent.get(i).getLocation());
    }

    @Override
    public int getItemCount() {
//        return rent.size();
        int arr = 0;
        try {
            if (rent.size() == 0) {
                arr = 0;
            } else {
                arr = rent.size();
            }
        } catch (Exception e){

        }
        return arr;
    }
}