package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MisEstadisticas extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private TextView top1, top2, top3;
    private HistorialAdapter adapter;
    private QuizManiaDB dbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_estadisticas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        top1 = findViewById(R.id.top1);
        top2 = findViewById(R.id.top2);
        top3 = findViewById(R.id.top3);

        dbHelper = new QuizManiaDB(this);
        db = dbHelper.getReadableDatabase();

        cargarTop3();
        cargarHistorial();
    }


    private void cargarTop3() {
        String query = "SELECT h.puntuacion, h.fecha, " +
                "mj.nombre, c.nombre, d.nivel " +
                "FROM HistorialPartidas h " +
                "JOIN ModoJuego mj ON h.idModoJuego = mj.idModoJuego " +
                "LEFT JOIN Categoria c ON h.idCategoria = c.idCategoria " +
                "JOIN Dificultad d ON h.idDificultad = d.idDificultad " +
                "ORDER BY h.puntuacion DESC LIMIT 3";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            int i = 1;
            do {
                int puntuacion = cursor.getInt(0);
                String fecha = cursor.getString(1);
                String modo = cursor.getString(2);
                String categoria = cursor.isNull(3) ? getString(R.string.txt_mixto) : cursor.getString(3);
                String dificultad = cursor.getString(4);

                String texto = i + ". " +
                        getString(R.string.txt_puntuaci_n) + " " + puntuacion + " - " +
                        getString(R.string.txt_fecha) + " " + fecha + "\n" +
                        getString(R.string.txt_modo_de_juego) + " " + modo + ", " +
                        getString(R.string.txt_categor_a) + " " + categoria + ", " +
                        getString(R.string.txt_dificultad) + " " + dificultad;

                if (i == 1) top1.setText(texto);
                else if (i == 2) top2.setText(texto);
                else if (i == 3) top3.setText(texto);
                i++;
            } while (cursor.moveToNext());
        }

        cursor.close();
    }

    private void cargarHistorial() {
        List<PartidaHistorial> historialList = new ArrayList<>();

        String query = "SELECT h.puntuacion, h.fecha, " +
                "mj.nombre, c.nombre, d.nivel " +
                "FROM HistorialPartidas h " +
                "JOIN ModoJuego mj ON h.idModoJuego = mj.idModoJuego " +
                "LEFT JOIN Categoria c ON h.idCategoria = c.idCategoria " +
                "JOIN Dificultad d ON h.idDificultad = d.idDificultad " +
                "ORDER BY h.fecha DESC";

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int puntuacion = cursor.getInt(0);
            String fecha = cursor.getString(1);
            String modo = cursor.getString(2);
            String categoria = cursor.isNull(3) ? getString(R.string.txt_mixto) : cursor.getString(3);
            String dificultad = cursor.getString(4);

            historialList.add(new PartidaHistorial(modo, categoria, dificultad, fecha, puntuacion));
        }

        cursor.close();

        adapter = new HistorialAdapter(historialList);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistorial.setAdapter(adapter);
    }

    public void btnMenuPrincipal(View view) {
        finish();
    }
}