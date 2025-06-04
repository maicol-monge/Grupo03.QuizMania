package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PuntajePregunta extends AppCompatActivity {

    boolean retomarQuiz = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_puntaje_pregunta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        retomarQuiz = getIntent().getBooleanExtra("retomarQuiz", false);

        // Validar puntaje recibido
        int puntajeTotal = getIntent().getIntExtra("puntajeTotal", -1);
        if (puntajeTotal == -1) {
            puntajeTotal = 0;
        }

        TextView tvPuntaje = findViewById(R.id.tvPuntaje);
        tvPuntaje.setText(String.valueOf(puntajeTotal));
    }

    public void btnRendirse(View view) {
        // Mostrar pantalla de resultado
        Intent intent = new Intent(this, Rendirse.class);
        intent.putExtra("puntajeTotal", getIntent().getIntExtra("puntajeTotal", 0)); // Pasar puntaje total acumulado
        intent.putExtra("preguntaActual", getIntent().getIntExtra("preguntaActual", 0));
        intent.putExtra("totalPreguntas", getIntent().getIntExtra("totalPreguntas", 8)); // Asegúrate de pasar el total de preguntas
        intent.putExtra("idCategoria", getIntent().getIntExtra("idCategoria", 1));
        intent.putExtra("idDificultad", getIntent().getIntExtra("idDificultad", 1));
        intent.putExtra("idModoJuego", getIntent().getIntExtra("idModoJuego", 1));
        intent.putExtra("ValorCronometrado", getIntent().getStringExtra("ValorCronometrado"));
        intent.putExtra("preguntasCorrectas", getIntent().getIntExtra("preguntasCorrectas", -1));
        intent.putExtra("preguntasIncorrectas", getIntent().getIntExtra("preguntasIncorrectas", -1));


        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void btnContinuar(View view) {
        if (retomarQuiz) {
            Intent intentQuiz = new Intent(this, QuizPregunta.class);
            intentQuiz.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            intentQuiz.putExtra("preguntaActual", getIntent().getIntExtra("preguntaActual", 0));
            intentQuiz.putExtra("idCategoria", getIntent().getIntExtra("idCategoria", 1));
            intentQuiz.putExtra("idDificultad", getIntent().getIntExtra("idDificultad", 1));
            intentQuiz.putExtra("idModoJuego", getIntent().getIntExtra("idModoJuego", 1));
            intentQuiz.putExtra("ValorCronometrado", getIntent().getStringExtra("ValorCronometrado"));
            intentQuiz.putExtra("puntajeTotal", getIntent().getIntExtra("puntajeTotal", 0));
            intentQuiz.putExtra("retomarQuiz", true);
            intentQuiz.putExtra("totalPreguntas", getIntent().getIntExtra("totalPreguntas", 8));


            startActivity(intentQuiz);
        } else {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
