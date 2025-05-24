package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ModoJuegoActivity extends AppCompatActivity {

    private int idCategoria;
    private SQLiteDatabase db;
    private QuizManiaDB dbHelper;
    private LinearLayout buttonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_juego);

        idCategoria = getIntent().getIntExtra("idCategoria", 1);

        // Obtener referencia al contenedor de botones
        buttonsContainer = findViewById(R.id.buttons_container);

        // Conectar con la base de datos
        dbHelper = new QuizManiaDB(this);
        db = dbHelper.getReadableDatabase();

        // Cargar modos de juego desde la BD
        cargarModosDeJuego();
    }

    private void cargarModosDeJuego() {
        // Consultar todos los modos de juego disponibles
        Cursor cursor = db.rawQuery("SELECT idModoJuego, nombre FROM ModoJuego", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    int idModo = cursor.getInt(0);
                    String nombreModo = cursor.getString(1);

                    // Crear botón dinámicamente
                    Button btnModo = new Button(this);
                    btnModo.setText(nombreModo);
                    btnModo.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    // Estilo del botón (puedes personalizar según el modo)
                    if (idModo % 2 == 0) {
                        btnModo.setBackgroundResource(R.drawable.btn_style_b);
                        btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cuadrado, 0, 0, 0);
                    } else {
                        btnModo.setBackgroundResource(R.drawable.btn_style_a);
                        btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.signo_mas, 0, 0, 0);
                    }

                    btnModo.setTextColor(getResources().getColor(R.color.boton_texto));
                    btnModo.setTextSize(11);
                    btnModo.setPadding(16, 16, 16, 16);
                    btnModo.setCompoundDrawablePadding(16);
                    btnModo.setAllCaps(false);

                    // Margen inferior
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnModo.getLayoutParams();
                    params.setMargins(0, 0, 0, 16);
                    btnModo.setLayoutParams(params);

                    // Asignar listener
                    int finalIdModo = idModo;
                    btnModo.setOnClickListener(v -> seleccionarModo(finalIdModo));

                    // Agregar botón al contenedor
                    buttonsContainer.addView(btnModo);

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
    }

    private void seleccionarModo(int modoJuego) {
        Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", modoJuego);
        startActivity(intent);
    }

    public void volverAInicio(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}