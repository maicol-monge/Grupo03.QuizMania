package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ModoJuegoActivity extends AppCompatActivity {

    private int idCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_juego);

        idCategoria = getIntent().getIntExtra("idCategoria", 1);

        Button btnNormal = findViewById(R.id.btnModoNormal);
        Button btnHarryPotter = findViewById(R.id.btnModoHarryPotter);

        btnNormal.setOnClickListener(v -> seleccionarModo(1));
        btnHarryPotter.setOnClickListener(v -> seleccionarModo(2));
    }

    private void seleccionarModo(int modoJuego) {
        Intent intent = new Intent(this, DificultadActivity.class);
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idModoJuego", modoJuego);
        startActivity(intent);
    }

    public void volverACategorias(View view) {
        finish();
    }
}