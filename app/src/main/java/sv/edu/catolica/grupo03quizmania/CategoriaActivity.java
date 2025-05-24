package sv.edu.catolica.grupo03quizmania;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {

    private LinearLayout layoutBotones;
    private QuizManiaDB dbHelper;
    private List<Categoria> categorias;
    private int idModoJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        layoutBotones = findViewById(R.id.layoutBotonesCategorias);
        dbHelper = new QuizManiaDB(this);

        cargarCategorias();
        crearBotonesCategorias();
    }

    private void cargarCategorias() {
        categorias = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "Categoria",
                new String[]{"idCategoria", "nombre"},
                null, null, null, null, null
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
        // Limpiar layout antes de agregar botones
        layoutBotones.removeAllViews();

        for (int i = 0; i < categorias.size(); i++) {
            Categoria categoria = categorias.get(i);

            Button boton = new Button(this);
            boton.setId(View.generateViewId());
            boton.setText(categoria.getNombre());

            // Asignar diferentes estilos según la posición
            switch (i % 4) {
                case 0:
                    boton.setBackgroundResource(R.drawable.btn_style_a);
                    boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.signo_mas, 0, 0, 0);
                    break;
                case 1:
                    boton.setBackgroundResource(R.drawable.btn_style_b);
                    boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cuadrado, 0, 0, 0);
                    break;
                case 2:
                    boton.setBackgroundResource(R.drawable.btn_style_c);
                    boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.circulo, 0, 0, 0);
                    break;
                case 3:
                    boton.setBackgroundResource(R.drawable.btn_style_d);
                    boton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.triangulo, 0, 0, 0);
                    break;
            }

            // Estilos comunes
            boton.setTextColor(getResources().getColor(R.color.boton_texto));
            boton.setTextSize(11);
            boton.setPadding(16, 16, 16, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            boton.setLayoutParams(params);

            // Listener para el botón
            int finalI = i;
            boton.setOnClickListener(v -> {
                Intent intent = new Intent(CategoriaActivity.this, DificultadActivity.class);
                intent.putExtra("idCategoria", categoria.getIdCategoria());
                intent.putExtra("idModoJuego", idModoJuego);
                startActivity(intent);
            });

            layoutBotones.addView(boton);
        }
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