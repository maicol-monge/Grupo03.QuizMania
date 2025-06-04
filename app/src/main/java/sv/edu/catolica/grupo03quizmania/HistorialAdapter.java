package sv.edu.catolica.grupo03quizmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private List<PartidaHistorial> historialList;

    public HistorialAdapter(List<PartidaHistorial> historialList) {
        this.historialList = historialList;
    }

    @NonNull
    @Override
    public HistorialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historial, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialAdapter.ViewHolder holder, int position) {
        PartidaHistorial item = historialList.get(position);

        holder.txtModo.setText(holder.itemView.getContext().getString(R.string.txt_modo_de_juego) + " " + item.getModo());

        String categoria = item.getCategoria();
        if (categoria == null || categoria.trim().isEmpty()) {
            categoria = "Mixta";
        }
        holder.txtCategoria.setText(holder.itemView.getContext().getString(R.string.txt_categor_a) + " " + categoria);

        holder.txtDificultad.setText(holder.itemView.getContext().getString(R.string.txt_dificultad) + " " + item.getDificultad());
        holder.txtFecha.setText(holder.itemView.getContext().getString(R.string.txt_fecha) + " " + item.getFecha());
        holder.txtPuntuacion.setText(holder.itemView.getContext().getString(R.string.txt_puntuaci_n) + " " + item.getPuntuacion());
    }

    @Override
    public int getItemCount() {
        return historialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtModo, txtCategoria, txtDificultad, txtFecha, txtPuntuacion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtModo = itemView.findViewById(R.id.txtModo);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtDificultad = itemView.findViewById(R.id.txtDificultad);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtPuntuacion = itemView.findViewById(R.id.txtPuntuacion);
        }
    }
}
