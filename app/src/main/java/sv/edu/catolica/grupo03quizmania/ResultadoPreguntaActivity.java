package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class ResultadoPreguntaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_pregunta);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Obtener datos del intent
        Intent intent = getIntent();
        boolean esCorrecta = intent.getBooleanExtra("esCorrecta", false);
        String respuestaCorrecta = intent.getStringExtra("respuestaCorrecta");
        String explicacion = intent.getStringExtra("explicacion");
        int puntajePregunta = intent.getIntExtra("puntajePregunta", 0);
        int puntajeTotal = intent.getIntExtra("puntajeTotal", 0);

        // Configurar vistas
        TextView tvResultado = findViewById(R.id.tvResultado);
        TextView tvPuntuacion = findViewById(R.id.tvPuntuacion);
        TextView tvExplicacion = findViewById(R.id.tvExplicacion);
        Button btnContinuar = findViewById(R.id.btnContinuar);

        if (esCorrecta) {
            tvResultado.setText(getString(R.string.txt_respuesta_correcta));
            tvResultado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvResultado.setText(getString(R.string.txt_respuesta_incorrecta));
            tvResultado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvPuntuacion.setText(getString(R.string.txt_la_respuesta_correcta_era) + " " + respuestaCorrecta);
        }

        tvPuntuacion.append(getString(R.string.txt_puntos_obtenidos) + " " + puntajePregunta);
        tvPuntuacion.setTypeface(null, android.graphics.Typeface.BOLD);
        Typeface montserrat = ResourcesCompat.getFont(this, R.font.montserrat_bold);
        tvPuntuacion.setTypeface(montserrat);
        tvPuntuacion.setPadding(16, 16, 16, 16);
        tvExplicacion.setText(explicacion);

        // Configurar botón para continuar
        btnContinuar.setOnClickListener(v -> {
            int preguntaActual = getIntent().getIntExtra("preguntaActual", 0);
            int totalPreguntas = getIntent().getIntExtra("totalPreguntas", 8); // Total de preguntas
            Log.d("ResultadoPregunta", "Pregunta actual: " + preguntaActual);
            Log.d("ResultadoPregunta", "Total de preguntas: " + totalPreguntas);


            if (preguntaActual >= totalPreguntas) {
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
            } else {
                // Continuar con PuntajePregunta si no es la última pregunta
                Intent intentPuntaje = new Intent(ResultadoPreguntaActivity.this, PuntajePregunta.class);
                intentPuntaje.putExtra("puntajeTotal", puntajeTotal);
                intentPuntaje.putExtra("retomarQuiz", true);
                intentPuntaje.putExtra("preguntaActual", preguntaActual + 1);
                intentPuntaje.putExtra("idCategoria", getIntent().getIntExtra("idCategoria", 1));
                intentPuntaje.putExtra("idDificultad", getIntent().getIntExtra("idDificultad", 1));
                intentPuntaje.putExtra("idModoJuego", getIntent().getIntExtra("idModoJuego", 1));
                intentPuntaje.putExtra("ValorCronometrado", getIntent().getStringExtra("ValorCronometrado"));
                intentPuntaje.putExtra("totalPreguntas", totalPreguntas);
                intentPuntaje.putExtra("preguntasCorrectas", getIntent().getIntExtra("preguntasCorrectas", 0));
                intentPuntaje.putExtra("preguntasIncorrectas", getIntent().getIntExtra("preguntasIncorrectas", 0));

                Log.d("ResultadoPregunta", "puntajeTotal: " + puntajeTotal);
                Log.d("ResultadoPregunta", "preguntasCorrectas: " + getIntent().getIntExtra("preguntasCorrectas", -1));
                Log.d("ResultadoPregunta", "preguntasIncorrectasRR: " + getIntent().getIntExtra("preguntasIncorrectas", -1));

                startActivity(intentPuntaje);
                finish(); // Finalizar la actividad actual
            }

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

    }
}
