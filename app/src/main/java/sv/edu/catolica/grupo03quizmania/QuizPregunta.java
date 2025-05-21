package sv.edu.catolica.grupo03quizmania;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;

public class QuizPregunta extends AppCompatActivity {

    ProgressBar progressBar;
    int totalPreguntas = 8;
    int preguntaActual = 6;

    TextView progresoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_pregunta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.progressBarCircular);
        actualizarProgreso();

    }

    void actualizarProgreso() {
        int progreso = (int) ((preguntaActual / (float) totalPreguntas) * 100);
        progressBar.setProgress(progreso);
    }

    void siguientePregunta(View view) {
        if (preguntaActual < totalPreguntas) {
            preguntaActual++;
            actualizarProgreso();
        }
    }
}
