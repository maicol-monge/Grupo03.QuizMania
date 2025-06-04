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


    public void treintaSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("ValorCronometrado", "TreintaSegundos"); // Nombre consistente
        intent.putExtra("idModoJuego", 4); // Aseguramos que se use el modo cronometrado
        startActivity(intent);
    }

    public void VeinteSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("ValorCronometrado", "VeinteSegundos"); // Nombre consistente
        intent.putExtra("idModoJuego", 4); // Aseguramos que se use el modo cronometrado
        startActivity(intent);
    }

    public void DiezSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("ValorCronometrado", "DiezSegundos");
        intent.putExtra("idModoJuego", 4); // Aseguramos que se use el modo cronometrado
        startActivity(intent);
    }

    public void CincoSegundos(View view) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("ValorCronometrado", "CincoSegundos");
        intent.putExtra("idModoJuego", 4); // Aseguramos que se use el modo cronometrado
        startActivity(intent);
    }

    public void home(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}