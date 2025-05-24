package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        // Configurar vistas
        TextView tvResultado = findViewById(R.id.tvResultado);
        TextView tvPuntuacion = findViewById(R.id.tvPuntuacion);
        TextView tvExplicacion = findViewById(R.id.tvExplicacion);
        Button btnContinuar = findViewById(R.id.btnContinuar);

        if (esCorrecta) {
            tvResultado.setText("¡Respuesta Correcta!");
            tvResultado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            tvResultado.setText("Respuesta Incorrecta");
            tvResultado.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvPuntuacion.setText("La respuesta correcta era: " + respuestaCorrecta);
        }

        tvPuntuacion.append("\nPuntos obtenidos: " + puntajePregunta);
        tvExplicacion.setText(explicacion);

        // Configurar botón para continuar
        btnContinuar.setOnClickListener(v -> {
            finish(); // Cierra esta actividad y regresa a QuizPregunta
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }
}