package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

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

        /// //
        valorCronometrado = getIntent().getStringExtra("ValorCronometrado");
        if (valorCronometrado == null) valorCronometrado = "";
        /// /

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

        intent.putExtra("ValorCronometrado", valorCronometrado);

        startActivity(intent);
    }

    public void volverACategorias(View view) {
        finish();
    }
}