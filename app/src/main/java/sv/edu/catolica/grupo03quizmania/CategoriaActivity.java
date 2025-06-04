package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {

    private LinearLayout layoutBotones;
    private QuizManiaDB dbHelper;
    private List<Categoria> categorias;
    private int idModoJuego = 1; // Valor por defecto
    private String valorCronometrado = ""; // Nombre de variable en minúsculas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        // Obtener parámetros del intent
        Intent intent = getIntent();
        valorCronometrado = intent.getStringExtra("ValorCronometrado"); // Nombre exacto del extra



            idModoJuego = intent.getIntExtra("idModoJuego", 1);


        layoutBotones = findViewById(R.id.layoutBotonesCategorias);
        dbHelper = new QuizManiaDB(this);

        cargarCategorias();
        crearBotonesCategorias();
    }

    private void cargarCategorias() {
        categorias = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Si el idModoJuego es 2, solo cargar la categoria con id 9
        if (idModoJuego == 2) {
            Cursor cursor = db.query(
                    "Categoria",
                    new String[]{"idCategoria", "nombre"},
                    "idCategoria = ?",
                    new String[]{"9"},
                    null, null, null
            );

            while (cursor.moveToNext()) {
                Categoria categoria = new Categoria(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idCategoria")),
                        cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                );
                categorias.add(categoria);
            }
            cursor.close();
            db.close();
            return;
        }
        // Y si el modo de juego no es 2, cargar todas las categorias menos la 9
        Cursor cursor = db.query(
                "Categoria",
                new String[]{"idCategoria", "nombre"},
                "idCategoria != ?",
                new String[]{"9"},
                null, null, null
        );
        while (cursor.moveToNext()) {
            Categoria categoria = new Categoria(
                    cursor.getInt(cursor.getColumnIndexOrThrow("idCategoria")),
                    cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            );
            categorias.add(categoria);
        }
        cursor.close();
        db.close();


    }

    private void crearBotonesCategorias() {
        layoutBotones.removeAllViews();

        for (int i = 0; i < categorias.size(); i++) {
            Categoria categoria = categorias.get(i);


            Button boton = new Button(this);
            boton.setId(View.generateViewId());
            boton.setText(traducirNombreCategoria(categoria.getNombre()));

            if (idModoJuego == 2) {
                // Asignar estilo especial para el modo de juego 2
                boton.setBackgroundResource(R.drawable.btn_style_harry_potter);
                boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.harry_potter, 0, 0, 0);

            } else {
                // Asignar estilos según posición (manteniendo tu diseño actual)
                switch (i % 4) {
                    case 0:
                        boton.setBackgroundResource(R.drawable.btn_style_signo_mas);
                        boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.signo_mas_white, 0, 0, 0);
                        break;
                    case 1:
                        boton.setBackgroundResource(R.drawable.btn_style_cuadrado);
                        boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cuadrado_white, 0, 0, 0);
                        break;
                    case 2:
                        boton.setBackgroundResource(R.drawable.btn_style_circulo);
                        boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo_white, 0, 0, 0);
                        break;
                    case 3:
                        boton.setBackgroundResource(R.drawable.btn_style_triangulo);
                        boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.triangulo_white, 0, 0, 0);
                        break;
                }
            }



            // Estilos comunes (sin cambios)
            boton.setTextColor(getResources().getColor(R.color.boton_texto));
            boton.setTextSize(15);
            boton.setTypeface(null, android.graphics.Typeface.BOLD);
            Typeface montserrat = ResourcesCompat.getFont(this, R.font.montserrat_bold);
            boton.setTypeface(montserrat);
            boton.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            boton.setLayoutParams(params);

            // Listener mejorado para el botón
            boton.setOnClickListener(v -> {
                Intent intent = new Intent(CategoriaActivity.this, DificultadActivity.class);

                // Siempre enviar estos parámetros
                intent.putExtra("idCategoria", categoria.getIdCategoria());
                intent.putExtra("idModoJuego", idModoJuego);

                // Solo enviar ValorCronometrado si tiene contenido
                if (valorCronometrado != null && !valorCronometrado.isEmpty()) {
                    intent.putExtra("ValorCronometrado", valorCronometrado);
                }

                startActivity(intent);
            });

            layoutBotones.addView(boton);
        }
    }

    private String traducirNombreCategoria(String nombre) {
        switch (nombre) {
            case "Historia":
                return getString(R.string.cat_historia);
            case "Ciencia":
                return getString(R.string.cat_ciencia);
            case "Geografía":
                return getString(R.string.cat_geografia);
            case "Arte":
                return getString(R.string.cat_arte);
            case "Literatura":
                return getString(R.string.cat_literatura);
            case "Cine":
                return getString(R.string.cat_cine);
            case "Música":
                return getString(R.string.cat_musica);
            case "Actualidad":
                return getString(R.string.cat_actualidad);
            case "Harry Potter":
                return getString(R.string.cat_harry_potter);
            default:
                return nombre; // Si no encuentra traducción, muestra el original
        }
    }

    public void volverAModoJuego(View view){
        finish();
    }

    public void home(View view) {
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    // Clase interna para representar una categoría
    private static class Categoria {
        private int idCategoria;
        private String nombre;

        public Categoria(int idCategoria, String nombre) {
            this.idCategoria = idCategoria;
            this.nombre = nombre;
        }

        public int getIdCategoria() {
            return idCategoria;
        }

        public String getNombre() {
            return nombre;
        }
    }
}