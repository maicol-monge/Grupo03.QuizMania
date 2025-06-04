package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizResultadoFinal extends AppCompatActivity {

    TextView tvEncabezado, tvPuntajeFinal, tvCategoria, tvDificultad, tvModoJuego, tvFecha, tvPreguntasCorrectas, tvPreguntasIncorrectas;
    private QuizManiaDB dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_resultado_final);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new QuizManiaDB(this);


        tvEncabezado = findViewById(R.id.tvEncabezado);
        tvPuntajeFinal = findViewById(R.id.tvPuntuacioFinal);
        tvCategoria = findViewById(R.id.tvCategoria);
        tvDificultad = findViewById(R.id.tvDificultad);
        tvModoJuego = findViewById(R.id.tvModoJuego);
        tvFecha = findViewById(R.id.tvFecha);
        tvPreguntasCorrectas = findViewById(R.id.tvPreguntasCorrectas);
        tvPreguntasIncorrectas = findViewById(R.id.tvPreguntasIncorrectas);


        // Obtener datos del intent
        Intent intent = getIntent();
        int puntajeTotal = intent.getIntExtra("puntajeTotal", 0);
        int preguntasCorrectas = intent.getIntExtra("preguntasCorrectas", 0);
        int preguntasIncorrectas = intent.getIntExtra("preguntasIncorrectas", 0);
        String categoria = obtenerNombreCategoria(intent.getIntExtra("idCategoria", 1));
        String dificultad = obtenerNombreDificultad(intent.getIntExtra("idDificultad", 1));
        String modoJuego = obtenerNombreModoJuego(intent.getIntExtra("idModoJuego", 1));
        int idCategoria = intent.getIntExtra("idCategoria", 1);
        int idDificultad = intent.getIntExtra("idDificultad", 1);
        int idModoJuego = intent.getIntExtra("idModoJuego", 1);
        String fechaActual = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        boolean esModoAleatorio = idModoJuego == 3; // O el ID que uses para el modo aleatorio




        if (esModoAleatorio) {
            // Si es modo aleatorio
            tvCategoria.setText("Mixta");
            insertarEnHistorial(idModoJuego, idDificultad, puntajeTotal, fechaActual);
        } else {
            tvCategoria.setText(categoria);
            insertarEnHistorial(idModoJuego, idCategoria, idDificultad, puntajeTotal, fechaActual);
        }


        // Mostrar datos en la UI
        if (preguntasCorrectas > 6 && puntajeTotal <= 8) {
            tvEncabezado.setText("¡Bien Jugado!");
        } else if (preguntasCorrectas > 3 && preguntasCorrectas <= 6) {
            tvEncabezado.setText("¡Buen intento!");
        } else if (preguntasCorrectas > 0 && preguntasCorrectas <= 3) {
            tvEncabezado.setText("¡No te preocupes! \n ¡Puedes intentarlo de nuevo!");
        } else {
            tvEncabezado.setText("Sin comentarios... \n ¡Suerte para la proxima!");
        }

        tvPuntajeFinal.setText(String.valueOf(puntajeTotal));
        tvPreguntasCorrectas.setText(String.valueOf(preguntasCorrectas));
        tvPreguntasIncorrectas.setText(String.valueOf(preguntasIncorrectas));
        tvDificultad.setText(dificultad);
        tvModoJuego.setText(modoJuego);
        tvFecha.setText(java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }


    private String obtenerNombreCategoria(int idCategoria) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM Categoria WHERE idCategoria = ?", new String[]{String.valueOf(idCategoria)});
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            cursor.close();
            return nombre;
        }
        cursor.close();
        return "Categoría desconocida";
    }

    private String obtenerNombreDificultad(int idDificultad) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nivel FROM Dificultad WHERE idDificultad = ?", new String[]{String.valueOf(idDificultad)});
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            cursor.close();
            return nombre;
        }
        cursor.close();
        return "Dificultad desconocida";
    }

    private String obtenerNombreModoJuego(int idModoJuego) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM ModoJuego WHERE idModoJuego = ?", new String[]{String.valueOf(idModoJuego)});
        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            cursor.close();
            return nombre;
        }
        cursor.close();
        return "Modo de juego desconocido";
    }

    private void insertarEnHistorial(int idModoJuego, int idCategoria, int idDificultad, int puntuacion, String fecha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO HistorialPartidas (idModoJuego, idCategoria, idDificultad, puntuacion, fecha) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{idModoJuego, idCategoria, idDificultad, puntuacion, fecha});
            Log.d("Historial", "Partida guardada correctamente");
        } catch (Exception e) {
            Log.e("Historial", "Error al guardar partida: " + e.getMessage());
        }
    }

    private void insertarEnHistorial(int idModoJuego,int idDificultad, int puntuacion, String fecha) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("INSERT INTO HistorialPartidas (idModoJuego, idCategoria, idDificultad, puntuacion, fecha) VALUES (?, ?, ?, ?, ?)",
                    new Object[]{idModoJuego, null, idDificultad, puntuacion, fecha});
            Log.d("Historial", "Partida guardada correctamente");
        } catch (Exception e) {
            Log.e("Historial", "Error al guardar partida: " + e.getMessage());
        }
    }

    public void btnFinalizar(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
