package sv.edu.catolica.grupo03quizmania;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.google.android.material.progressindicator.CircularProgressIndicator;

public class QuizPregunta extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvPregunta, tvPreguntaActual;
    private Button opcion1, opcion2, opcion3, opcion4, btnRendirse;
    private int totalPreguntas = 8;
    private int preguntaActual = 1;
    private int puntaje = 0;
    private List<Pregunta> listaPreguntas;
    private Pregunta preguntaActualObj;
    private QuizManiaDB dbHelper;
    private int idCategoria;
    private int idDificultad;
    private int idModoJuego;
    private String valorCronometrado = "";

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

        // Obtener parámetros del intent
//        Intent intent = getIntent();
//        idCategoria = intent.getIntExtra("idCategoria", 1);
//        idDificultad = intent.getIntExtra("idDificultad", 1);
//        idModoJuego = intent.getIntExtra("idModoJuego", 1);

        idCategoria = getIntent().getIntExtra("idCategoria", 1);
        idDificultad = getIntent().getIntExtra("idDificultad", 1);
        idModoJuego = getIntent().getIntExtra("idModoJuego", 1);




        ///
        valorCronometrado = getIntent().getStringExtra("ValorCronometrado");
        if (valorCronometrado == null) valorCronometrado = "";
        ///



        // Configurar título según modo de juego
        String titulo = (idModoJuego == 2) ? "Quiz de Harry Potter" : "Quiz Normal";
        setTitle(titulo);

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBarCircular);
        tvPregunta = findViewById(R.id.tvPregunta);
        tvPreguntaActual = findViewById(R.id.tvPreguntaActual);
        opcion1 = findViewById(R.id.opcion1);
        opcion2 = findViewById(R.id.opcion2);
        opcion3 = findViewById(R.id.opcion3);
        opcion4 = findViewById(R.id.opcion4);
        btnRendirse = findViewById(R.id.opcionRendirse);

        // Inicializar base de datos
        dbHelper = new QuizManiaDB(this);

        // Cargar preguntas
        cargarPreguntas();

        // Configurar listeners de botones
        opcion1.setOnClickListener(v -> verificarRespuesta(opcion1));
        opcion2.setOnClickListener(v -> verificarRespuesta(opcion2));
        opcion3.setOnClickListener(v -> verificarRespuesta(opcion3));
        opcion4.setOnClickListener(v -> verificarRespuesta(opcion4));

        btnRendirse.setOnClickListener(v -> rendirse());

        // Mostrar primera pregunta
        mostrarPregunta();

    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Debug: Mostrar parámetros recibidos
        Log.d("QUIZ_PARAMS", "Categoría: " + idCategoria + ", Dificultad: " + idDificultad + ", Modo: " + idModoJuego);

        String[] projection = {
                "idPregunta", "pregunta", "opcionA", "opcionB", "opcionC", "opcionD",
                "respuestaCorrecta", "explicacion", "puntaje"
        };

        String selection = "idCategoria = ? AND idDificultad = ? AND idModoJuego = ?";
        String[] selectionArgs = {String.valueOf(idCategoria), String.valueOf(idDificultad), String.valueOf(idModoJuego)};

        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Preguntas",
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            Log.d("QUIZ_DEBUG", "Número de preguntas encontradas: " + cursor.getCount());

            while (cursor.moveToNext()) {
                Pregunta pregunta = new Pregunta(
                        cursor.getInt(cursor.getColumnIndexOrThrow("idPregunta")),
                        cursor.getString(cursor.getColumnIndexOrThrow("pregunta")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opcionA")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opcionB")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opcionC")),
                        cursor.getString(cursor.getColumnIndexOrThrow("opcionD")),
                        cursor.getString(cursor.getColumnIndexOrThrow("respuestaCorrecta")),
                        cursor.getString(cursor.getColumnIndexOrThrow("explicacion")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("puntaje"))
                );
                listaPreguntas.add(pregunta);
            }
        } catch (Exception e) {
            Log.e("QUIZ_ERROR", "Error al cargar preguntas: " + e.getMessage());
            Toast.makeText(this, "Error al cargar preguntas", Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        // Si no hay preguntas, mostrar mensaje y terminar
        if (listaPreguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas disponibles para esta configuración", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Mezclar las preguntas
        Collections.shuffle(listaPreguntas);

        // Ajustar el total de preguntas
        totalPreguntas = Math.min(listaPreguntas.size(), totalPreguntas);
    }

    private void mostrarPregunta() {

        if (listaPreguntas == null || listaPreguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (preguntaActual <= totalPreguntas) {
            preguntaActualObj = listaPreguntas.get(preguntaActual - 1);

            // Actualizar UI
            tvPregunta.setText(preguntaActualObj.getPregunta());
            tvPreguntaActual.setText(String.valueOf(preguntaActual));

            // Mezclar las opciones para que no siempre estén en el mismo orden
            List<String> opciones = new ArrayList<>();
            opciones.add(preguntaActualObj.getOpcionA());
            opciones.add(preguntaActualObj.getOpcionB());
            opciones.add(preguntaActualObj.getOpcionC());
            opciones.add(preguntaActualObj.getOpcionD());
            Collections.shuffle(opciones);

            // Asignar opciones a los botones
            opcion1.setText(opciones.get(0));
            opcion2.setText(opciones.get(1));
            opcion3.setText(opciones.get(2));
            opcion4.setText(opciones.get(3));

            // Actualizar barra de progreso
            actualizarProgreso();
        } else {
            // Fin del quiz
            terminarQuiz();
        }
    }

    private void verificarRespuesta(Button botonSeleccionado) {
        String respuestaSeleccionada = botonSeleccionado.getText().toString();
        boolean esCorrecta = respuestaSeleccionada.equals(preguntaActualObj.getRespuestaCorrecta());

        // Deshabilitar todos los botones
        opcion1.setEnabled(false);
        opcion2.setEnabled(false);
        opcion3.setEnabled(false);
        opcion4.setEnabled(false);

        // Resaltar respuestas
        if (esCorrecta) {
            botonSeleccionado.setBackgroundColor(Color.GREEN);
            puntaje += preguntaActualObj.getPuntaje();
        } else {
            botonSeleccionado.setBackgroundColor(Color.RED);
            mostrarRespuestaCorrecta();
        }

        // Mostrar pantalla de resultado
        Intent intent = new Intent(this, ResultadoPreguntaActivity.class);
        intent.putExtra("esCorrecta", esCorrecta);
        intent.putExtra("respuestaCorrecta", preguntaActualObj.getRespuestaCorrecta());
        intent.putExtra("explicacion", preguntaActualObj.getExplicacion());
        intent.putExtra("puntajePregunta", esCorrecta ? preguntaActualObj.getPuntaje() : 0);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verificar si debemos pasar a la siguiente pregunta
        if (opcion1 != null && !opcion1.isEnabled()) {
            // Esperar un breve momento para que el usuario vea los colores
            new Handler().postDelayed(() -> {
                resetearBotones();
                preguntaActual++;
                mostrarPregunta();
            }, 500);
        }
    }

//    private void verificarRespuesta(Button botonSeleccionado) {
//        String respuestaSeleccionada = botonSeleccionado.getText().toString();
//
//        // Deshabilitar todos los botones
//        opcion1.setEnabled(false);
//        opcion2.setEnabled(false);
//        opcion3.setEnabled(false);
//        opcion4.setEnabled(false);
//
//        if (respuestaSeleccionada.equals(preguntaActualObj.getRespuestaCorrecta())) {
//            botonSeleccionado.setBackgroundColor(Color.GREEN);
//            puntaje += preguntaActualObj.getPuntaje();
//            Toast.makeText(this, "¡Correcto! " + preguntaActualObj.getExplicacion(), Toast.LENGTH_SHORT).show();
//        } else {
//            botonSeleccionado.setBackgroundColor(Color.RED);
//            mostrarRespuestaCorrecta();
//            Toast.makeText(this, "Incorrecto. La respuesta correcta es: " +
//                    preguntaActualObj.getRespuestaCorrecta() + ". " +
//                    preguntaActualObj.getExplicacion(), Toast.LENGTH_LONG).show();
//        }
//
//        // Retraso antes de pasar a la siguiente pregunta
//        new Handler().postDelayed(() -> {
//            resetearBotones();
//            preguntaActual++;
//            mostrarPregunta();
//        }, 1500);
//    }

    private void mostrarRespuestaCorrecta() {
        if (opcion1.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion1.setBackgroundColor(Color.GREEN);
        } else if (opcion2.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion2.setBackgroundColor(Color.GREEN);
        } else if (opcion3.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion3.setBackgroundColor(Color.GREEN);
        } else {
            opcion4.setBackgroundColor(Color.GREEN);
        }
    }

    private void resetearBotones() {
        opcion1.setBackgroundResource(R.drawable.btn_style_a);
        opcion2.setBackgroundResource(R.drawable.btn_style_b);
        opcion3.setBackgroundResource(R.drawable.btn_style_c);
        opcion4.setBackgroundResource(R.drawable.btn_style_d);
        opcion1.setEnabled(true);
        opcion2.setEnabled(true);
        opcion3.setEnabled(true);
        opcion4.setEnabled(true);
    }

    private void actualizarProgreso() {
        int progreso = (int) ((preguntaActual / (float) totalPreguntas) * 100);
        progressBar.setProgress(progreso);
    }

    private void terminarQuiz() {
        // Mostrar resultados y finalizar
        Toast.makeText(this, "Quiz completado! Puntaje: " + puntaje + "/" +
                (totalPreguntas * 10), Toast.LENGTH_LONG).show();

        // Guardar resultados en historial
        guardarResultado();

        // Regresar al menú principal o mostrar resultados
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    private void guardarResultado() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Aquí deberías implementar la lógica para guardar el resultado en la tabla HistorialPartidas
        // Necesitarías obtener la fecha actual y otros datos necesarios
    }

    private void rendirse() {
        // Mostrar mensaje de rendición
        Toast.makeText(this, "Has terminado el quiz. Puntaje obtenido: " + puntaje, Toast.LENGTH_LONG).show();

        // Guardar resultados
        guardarResultado();

        // Finalizar actividad
        finish();
    }

    // Clase interna para representar una pregunta
    private static class Pregunta {
        private int idPregunta;
        private String pregunta;
        private String opcionA;
        private String opcionB;
        private String opcionC;
        private String opcionD;
        private String respuestaCorrecta;
        private String explicacion;
        private int puntaje;

        public Pregunta(int idPregunta, String pregunta, String opcionA, String opcionB,
                        String opcionC, String opcionD, String respuestaCorrecta,
                        String explicacion, int puntaje) {
            this.idPregunta = idPregunta;
            this.pregunta = pregunta;
            this.opcionA = opcionA;
            this.opcionB = opcionB;
            this.opcionC = opcionC;
            this.opcionD = opcionD;
            this.respuestaCorrecta = respuestaCorrecta;
            this.explicacion = explicacion;
            this.puntaje = puntaje;
        }

        // Getters
        public int getIdPregunta() {
            return idPregunta;
        }

        public String getPregunta() {
            return pregunta;
        }

        public String getOpcionA() {
            return opcionA;
        }

        public String getOpcionB() {
            return opcionB;
        }

        public String getOpcionC() {
            return opcionC;
        }

        public String getOpcionD() {
            return opcionD;
        }

        public String getRespuestaCorrecta() {
            return respuestaCorrecta;
        }

        public String getExplicacion() {
            return explicacion;
        }

        public int getPuntaje() {
            return puntaje;
        }
    }


}
