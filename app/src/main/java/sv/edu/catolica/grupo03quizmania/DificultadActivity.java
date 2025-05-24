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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dificultad);

        idCategoria = getIntent().getIntExtra("idCategoria", 1);
        idModoJuego = getIntent().getIntExtra("idModoJuego", 1);

        Button btnFacil = findViewById(R.id.btnFacil);
        Button btnMedio = findViewById(R.id.btnMedio);
        Button btnDificil = findViewById(R.id.btnDificil);

        btnFacil.setOnClickListener(v -> iniciarQuiz(1));
        btnMedio.setOnClickListener(v -> iniciarQuiz(2));
        btnDificil.setOnClickListener(v -> iniciarQuiz(3));
    }

    private void iniciarQuiz(int dificultad) {
        Intent intent = new Intent(this, QuizPregunta.class);
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idDificultad", dificultad);
        intent.putExtra("idModoJuego", idModoJuego);
        startActivity(intent);
    }

    public void volverACategorias(View view) {
        finish();
    }
}