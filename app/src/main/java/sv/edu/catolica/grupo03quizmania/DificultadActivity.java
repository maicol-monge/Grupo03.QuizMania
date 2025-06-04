package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;



public class DificultadActivity extends AppCompatActivity {
    private int idModoJuego;
    private int idCategoria;
    private String valorCronometrado = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dificultad);

        idCategoria = getIntent().getIntExtra("idCategoria", 1);
        idModoJuego = getIntent().getIntExtra("idModoJuego", 1);

        valorCronometrado = getIntent().getStringExtra("ValorCronometrado");
        if (valorCronometrado == null) valorCronometrado = "";

        Button btnFacil = findViewById(R.id.btnFacil);
        Button btnMedio = findViewById(R.id.btnMedio);
        Button btnDificil = findViewById(R.id.btnDificil);
        Button btnIniciar = findViewById(R.id.btnIniciar);
        LinearLayout layoutSeleccion = findViewById(R.id.linearLayoutSeleccion);
        TextView txtModoJuego = findViewById(R.id.txtModoJuego);
        TextView txtCategoria = findViewById(R.id.txtCategoria);
        TextView txtDificultad = findViewById(R.id.txtDificultad);

        btnFacil.setOnClickListener(v -> {
            layoutSeleccion.setVisibility(View.VISIBLE);
            txtModoJuego.setText(getModoJuegoText(idModoJuego));
            if (idModoJuego == 3) {
                txtCategoria.setText(R.string.txt_mixto);
            }
            else {
                txtCategoria.setText(getCategoriaText(idCategoria));
            }
            txtDificultad.setText(R.string.txt_facil);
            btnIniciar.setTag(1); // Set difficulty level as tag
        });

        btnMedio.setOnClickListener(v -> {
            layoutSeleccion.setVisibility(View.VISIBLE);
            txtModoJuego.setText(getModoJuegoText(idModoJuego));
            if (idModoJuego == 3) {
                txtCategoria.setText(R.string.txt_mixto);
            }
            else {
                txtCategoria.setText(getCategoriaText(idCategoria));
            }

            txtDificultad.setText(R.string.txt_medio);
            btnIniciar.setTag(2); // Set difficulty level as tag
        });

        btnDificil.setOnClickListener(v -> {
            layoutSeleccion.setVisibility(View.VISIBLE);
            txtModoJuego.setText(getModoJuegoText(idModoJuego));
            if (idModoJuego == 3) {
                txtCategoria.setText(R.string.txt_mixto);
            }
            else {
                txtCategoria.setText(getCategoriaText(idCategoria));
            }
            txtDificultad.setText(R.string.txt_dificil);
            btnIniciar.setTag(3); // Set difficulty level as tag
        });

        btnIniciar.setOnClickListener(v -> {
            int dificultad = (int) btnIniciar.getTag(); // Retrieve difficulty level from tag
            iniciarQuiz(dificultad);
        });
    }

    private String getModoJuegoText(int idModoJuego) {
        // Replace with actual logic to get mode name based on idModoJuego
        switch (idModoJuego) {
            case 1: return getString(R.string.modo_normal);
            case 2: return getString(R.string.modo_harry_potter);
            case 3: return getString(R.string.modo_aleatorio);
            case 4: return getString(R.string.modo_cronometrado);
            default: return "Modo Desconocido";
        }
    }

    private String getCategoriaText(int idCategoria) {
        // Replace with actual logic to get category name based on idCategoria
        switch (idCategoria) {
            case 1: return getString(R.string.cat_historia);
            case 2: return getString(R.string.cat_ciencia);
            case 3: return getString(R.string.cat_geografia);
            case 4: return getString(R.string.cat_arte);
            case 5: return getString(R.string.cat_literatura);
            case 6: return getString(R.string.cat_cine);
            case 7: return getString(R.string.cat_musica);
            case 8: return getString(R.string.cat_actualidad);
            case 9: return getString(R.string.cat_harry_potter);
            default: return "Categoría Desconocida";
        }
    }

    private void iniciarQuiz(int dificultad) {
        Intent intent = new Intent(this, QuizPregunta.class);
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idDificultad", dificultad);
        intent.putExtra("idModoJuego", idModoJuego);

        intent.putExtra("ValorCronometrado", valorCronometrado);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void volverACategorias(View view) {
        finish();
    }


    public void home(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}
