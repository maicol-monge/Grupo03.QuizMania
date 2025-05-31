package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Modo_Cronometrado extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modo_cronometrado);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void volverAModoJuego(View view) {
        finish();
    }


    public void DosMinuto(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", "DosMinuto");
        startActivity(intent);
    }

    public void UnMinutos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", "UnMinuto");
        startActivity(intent);
    }

    public void TreintaSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", "TreintaSegundos"); //Indica que seran 30 segundos
        startActivity(intent);
    }

    public void VeinteSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", "VeinteSegundos"); //Indica que seran 20 segundos
        startActivity(intent);
    }
}