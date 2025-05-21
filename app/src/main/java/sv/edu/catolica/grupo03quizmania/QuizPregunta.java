package sv.edu.catolica.grupo03quizmania;

import android.os.Bundle;
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
    CircularProgressIndicator progressBarMaterial;
    TextView tvPreguntaActual;

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


        progressBarMaterial = findViewById(R.id.progressBarMaterial); // Cuidado con el ID si cambias
        tvPreguntaActual = findViewById(R.id.tvPreguntaActual);

        int preguntaActual = 3;
        int totalPreguntas = 8;

        progressBarMaterial.setMax(totalPreguntas);
        progressBarMaterial.setProgress(preguntaActual);
        tvPreguntaActual.setText(String.valueOf(preguntaActual));
    }
}
