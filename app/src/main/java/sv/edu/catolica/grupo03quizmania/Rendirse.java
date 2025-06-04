package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Rendirse extends AppCompatActivity {
    int puntajeTotal;
    Button btnNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rendirse);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        puntajeTotal = intent.getIntExtra("puntajeTotal", 0);
        btnNo = findViewById(R.id.btnNo);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra la actividad actual (Rendirse) y regresa a QuizPregunta
            }
        });

    }


    public void btnSi(View view) {
        int preguntaActual = getIntent().getIntExtra("preguntaActual", 0);
        int totalPreguntas = getIntent().getIntExtra("totalPreguntas", 8); // Total de preguntas
        Log.d("ResultadoPregunta", "Pregunta actual: " + preguntaActual);
        Log.d("ResultadoPregunta", "Total de preguntas: " + totalPreguntas);



            Log.d("ResultadoPregunta", "Entrando a resultado final");
            // Redirigir a QuizResultadoFinal si es la última pregunta
            Intent intentResumen = new Intent(this, QuizResultadoFinal.class);
            intentResumen.putExtra("puntajeTotal", puntajeTotal);
            intentResumen.putExtra("idCategoria", getIntent().getIntExtra("idCategoria", 1));
            intentResumen.putExtra("idDificultad", getIntent().getIntExtra("idDificultad", 1));
            intentResumen.putExtra("idModoJuego", getIntent().getIntExtra("idModoJuego", 1));
            intentResumen.putExtra("preguntasCorrectas", getIntent().getIntExtra("preguntasCorrectas", 0));
            intentResumen.putExtra("preguntasIncorrectas", getIntent().getIntExtra("preguntasIncorrectas", 0));

            Log.d("ResultadoPregunta", "puntajeTotal: " + puntajeTotal);
            Log.d("ResultadoPregunta", "preguntasCorrectas: " + getIntent().getIntExtra("preguntasCorrectas", -1));
            Log.d("ResultadoPregunta", "preguntasIncorrectas: " + getIntent().getIntExtra("preguntasIncorrectas", -1));



            startActivity(intentResumen);
            finish(); // Finalizar la actividad actual


        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}