package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

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
                    btnModo.setText(traducirModoJuego(nombreModo));
                    btnModo.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));

                    switch (idModo % 4) {
                        case 1:
                            btnModo.setBackgroundResource(R.drawable.btn_style_signo_mas);
                            btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.signo_mas_white, 0, 0, 0);
                            break;
                        case 2:
                            btnModo.setBackgroundResource(R.drawable.btn_style_harry_potter);
                            btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.harry_potter, 0, 0, 0);
                            break;
                        case 3:
                            btnModo.setBackgroundResource(R.drawable.btn_style_circulo);
                            btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_white, 0, 0, 0);
                            break;
                        case 0:
                            btnModo.setBackgroundResource(R.drawable.btn_style_triangulo);
                            btnModo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.triangulo_white, 0, 0, 0);
                            break;
                    }

                    btnModo.setTextColor(getResources().getColor(R.color.white));
                    btnModo.setTextSize(20);
                    btnModo.setTypeface(null, android.graphics.Typeface.BOLD);
                    Typeface montserrat = ResourcesCompat.getFont(this, R.font.montserrat_bold);
                    btnModo.setTypeface(montserrat);
                    btnModo.setPadding(16, 16, 16, 16);
                    btnModo.setCompoundDrawablePadding(16);
                    btnModo.setAllCaps(false);

                    // Margen inferior
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btnModo.getLayoutParams();
                    params.setMargins(16, 16, 16, 16);
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
        /*Intent intent = new Intent(this, CategoriaActivity.class);
        intent.putExtra("idModoJuego", modoJuego);
        startActivity(intent);*/

        Intent intent;

        switch (modoJuego) {
            case 1:
                // Modo Normal
                intent = new Intent(this, CategoriaActivity.class);
                break;

            case 2:
                // Modo Harry Potter
                intent = new Intent(this, CategoriaActivity.class);
                break;

            case 3:
                // Modo Aleatorio
                intent = new Intent(this, DificultadActivity.class);
                break;

            case 4:
                // Modo Temporizador
                intent = new Intent(this, Modo_Cronometrado.class);
                break;

            default:
                // Modo por defecto si no se reconoce el id
                intent = new Intent(this, CategoriaActivity.class);
                break;
        }

        // Si también necesitas pasar el id del modo de juego:
        intent.putExtra("idModoJuego", modoJuego);
        intent.putExtra("idCategoria", idCategoria); // si lo necesitas
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private String traducirModoJuego(String nombreModoBD) {
        switch (nombreModoBD.toLowerCase()) {
            case "normal":
                return getString(R.string.modo_normal);
            case "harry potter":
                return getString(R.string.modo_harry_potter);
            case "aleatorio":
                return getString(R.string.modo_aleatorio);
            case "temporizador":
                return getString(R.string.modo_cronometrado);
            default:
                return nombreModoBD; // Si no se encuentra, devuelve el texto original
        }
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

    public void home(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}