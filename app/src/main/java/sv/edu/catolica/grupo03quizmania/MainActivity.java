package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configuración de animación tipo "pop"
        imageView = findViewById(R.id.imageView);
        Animation popAnimation = AnimationUtils.loadAnimation(this, R.anim.pop);
        imageView.startAnimation(popAnimation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Handler manejador = new Handler();

        manejador.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent objVentana = new Intent(MainActivity.this, Home.class);
                startActivity(objVentana);
            }
        }, 2000);
    }
}