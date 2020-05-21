package si.uni_lj.fe.tnuv.hpm;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class racuniAdapter extends RecyclerView.Adapter<racuniAdapter.racuniVH> {

    List<racuni> racuniList;

    public racuniAdapter(List<racuni> racuniList) {
        this.racuniList = racuniList;
    }

    @NonNull
    @Override
    public racuniVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_racuni, parent, false);
        return new racuniVH(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull racuniVH holder, int position) {

        racuni racuni = racuniList.get(position);
        holder.imeRacunaTxt.setText(racuni.getImeMeseca());
        holder.cenaRacunaTxt.setText(racuni.getVersion());
        holder.zapadlostTxt.setText(racuni.getApiLevel());
        holder.razclenjenRacunTxt.setText(racuni.getDescription());
        holder.ze_placanoTxt.setText(racuni.getZe_placano());

        if(racuni.getZe_placano().equals("(Že plačano)")){
            holder.zapadlostTxt.setTextColor(R.color.crna);
            holder.ze_placanoTxt.setTextColor(R.color.crna);
        }

        boolean isExpandable = racuniList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return racuniList.size();
    }

    public class racuniVH extends RecyclerView.ViewHolder {

        TextView imeRacunaTxt, cenaRacunaTxt, zapadlostTxt, razclenjenRacunTxt, ze_placanoTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;


        public racuniVH(@NonNull View itemView) {
            super(itemView);

            imeRacunaTxt = itemView.findViewById(R.id.mesec_row_racuni);
            cenaRacunaTxt = itemView.findViewById(R.id.cena_racuna);
            zapadlostTxt = itemView.findViewById(R.id.zapadlost);
            razclenjenRacunTxt = itemView.findViewById(R.id.descriptio_row_racuni);
            ze_placanoTxt = itemView.findViewById(R.id.ze_placano);

            linearLayout = itemView.findViewById(R.id.linear_layout_row_racuni);
            expandableLayout = itemView.findViewById(R.id.expandable_layout_row_racuni);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    racuni racuni = racuniList.get(getAdapterPosition());
                    racuni.setExpandable(!racuni.isExpandable());
                    notifyItemChanged(getAdapterPosition());

                }
            });

        }
    }
}
