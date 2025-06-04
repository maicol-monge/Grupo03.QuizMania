package sv.edu.catolica.grupo03quizmania;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
    private TextView tvPregunta, tvPreguntaActual, tvTemporizador;
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

    // Variables para el temporizador
    private Handler handler = new Handler();
    private Runnable temporizadorRunnable;
    private int tiempoRestante;
    private boolean temporizadorActivo = false;
    private int preguntasCorrectas = 0;
    private int preguntasIncorrectas = 0;

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

        idCategoria = getIntent().getIntExtra("idCategoria", 1);
        idDificultad = getIntent().getIntExtra("idDificultad", 1);
        idModoJuego = getIntent().getIntExtra("idModoJuego", 1);
        valorCronometrado = getIntent().getStringExtra("ValorCronometrado");

        if (valorCronometrado == null) valorCronometrado = "";

        // Configurar título según modo de juego
        String titulo = (idModoJuego == 2) ? "Quiz de Harry Potter" : "Quiz Normal";
        setTitle(titulo);

        // Inicializar vistas
        progressBar = findViewById(R.id.progressBarCircular);
        tvPregunta = findViewById(R.id.tvPregunta);
        tvPreguntaActual = findViewById(R.id.tvPreguntaActual);
        tvTemporizador = findViewById(R.id.tvTemporizador);
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

        preguntaActual = getIntent().getIntExtra("preguntaActual", 0); // Restaurar el número de la pregunta
        puntaje = getIntent().getIntExtra("puntajeTotal", 0); // Restaurar puntaje

        boolean retomarQuiz = getIntent().getBooleanExtra("retomarQuiz", false);

        if (preguntaActual == 0) {
            preguntaActual = 1; // Comienza desde la primera si no se está retomando
        }


        if (!retomarQuiz) {
            mostrarPregunta(); // Solo si no estás retomando el quiz
        } else {
            mostrarPregunta(); // También deberías mostrar la pregunta actual
        }




    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Log.d("QUIZ_PARAMS", "Categoría: " + idCategoria + ", Dificultad: " + idDificultad + ", Modo: " + idModoJuego);

        String[] projection = {
                "idPregunta", "pregunta", "opcionA", "opcionB", "opcionC", "opcionD",
                "respuestaCorrecta", "explicacion", "puntaje"
        };

        String selection;
        String[] selectionArgs;

        // Si el modo es ALEATORIO (por ejemplo modo con id 3), ignoramos categoría
        if (idModoJuego == 3) {
            selection = "idDificultad = ?";
            selectionArgs = new String[]{String.valueOf(idDificultad)};
        } else if (idModoJuego == 4) {
            selection = "idCategoria = ? AND idDificultad = ? AND idModoJuego = 1";
            selectionArgs = new String[]{
                    String.valueOf(idCategoria),
                    String.valueOf(idDificultad)
            };
        } else {
            selection = "idCategoria = ? AND idDificultad = ? AND idModoJuego = ?";
            selectionArgs = new String[]{
                    String.valueOf(idCategoria),
                    String.valueOf(idDificultad),
                    String.valueOf(idModoJuego)
            };
        }

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

        if (listaPreguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas disponibles para esta configuración", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Mezclar y limitar a 8 si es modo aleatorio
        Collections.shuffle(listaPreguntas);

        if (idModoJuego == 3) {
            totalPreguntas = Math.min(8, listaPreguntas.size()); // Solo 8 preguntas
        } else {
            totalPreguntas = Math.min(listaPreguntas.size(), totalPreguntas);
        }
    }


    private void mostrarPregunta() {
        limpiarEstadoTemporizador();
        if (listaPreguntas == null || listaPreguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas disponibles", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (preguntaActual <= totalPreguntas) {
            // Detener temporizador anterior si existe
            detenerTemporizador();

            preguntaActualObj = listaPreguntas.get(preguntaActual - 1);

            // Actualizar UI
            tvPregunta.setText(preguntaActualObj.getPregunta());
            tvPreguntaActual.setText(String.valueOf(preguntaActual));

            // Mezclar las opciones
            List<String> opciones = new ArrayList<>();
            opciones.add(preguntaActualObj.getOpcionA());
            opciones.add(preguntaActualObj.getOpcionB());
            opciones.add(preguntaActualObj.getOpcionC());
            opciones.add(preguntaActualObj.getOpcionD());
            Collections.shuffle(opciones);

            opcion1.setText(opciones.get(0));
            opcion2.setText(opciones.get(1));
            opcion3.setText(opciones.get(2));
            opcion4.setText(opciones.get(3));

            // Reiniciar botones
            resetearBotones();

            // Actualizar barra de progreso
            actualizarProgreso();

            // Iniciar temporizador si es modo cronometrado
            if (!valorCronometrado.isEmpty()) {
                iniciarTemporizador();
            }
        } else {
            terminarQuiz();
        }
    }

    private void iniciarTemporizador() {
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        boolean temporizadorGuardado = prefs.getBoolean("temporizadorActivo", false);

        if (temporizadorGuardado) {
            tiempoRestante = prefs.getInt("tiempoRestante", 0);
            prefs.edit().clear().apply(); // Limpiar el estado después de cargarlo
        } else {
            // Convertir valorCronometrado a segundos solo si no hay valor guardado
            switch (valorCronometrado) {
                case "TreintaSegundos":
                    tiempoRestante = 30;
                    break;
                case "VeinteSegundos":
                    tiempoRestante = 20;
                    break;
                case "DiezSegundos":
                    tiempoRestante = 10;
                    break;
                case "CincoSegundos":
                    tiempoRestante = 5;
                    break;
            }
        }

        tvTemporizador.setVisibility(View.VISIBLE);
        actualizarTemporizadorUI();

        temporizadorRunnable = new Runnable() {
            @Override
            public void run() {
                tiempoRestante--;
                actualizarTemporizadorUI();

                if (tiempoRestante <= 0) {
                    tiempoAgotado();
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        };

        temporizadorActivo = true;
        handler.postDelayed(temporizadorRunnable, 1000);
    }

    private void limpiarEstadoTemporizador() {
        getSharedPreferences("QuizPrefs", MODE_PRIVATE).edit().clear().apply();
    }


    private void actualizarTemporizadorUI() {
        int minutos = tiempoRestante / 60;
        int segundos = tiempoRestante % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
        tvTemporizador.setText(tiempoFormateado);

        // Cambiar color cuando quedan 10 segundos o menos
        if (tiempoRestante <= 10) {
            tvTemporizador.setTextColor(Color.RED);
        } else {
            tvTemporizador.setTextColor(ContextCompat.getColor(this, R.color.temporizador));
        }
    }

    private void detenerTemporizador() {
        if (temporizadorRunnable != null) {
            handler.removeCallbacks(temporizadorRunnable);
            temporizadorActivo = false;
        }
    }

    private void tiempoAgotado() {
        detenerTemporizador();

        // Deshabilitar todos los botones
        opcion1.setEnabled(false);
        opcion2.setEnabled(false);
        opcion3.setEnabled(false);
        opcion4.setEnabled(false);

        // Mostrar respuesta correcta (como respuesta incorrecta)
        mostrarRespuestaCorrecta();

        preguntasIncorrectas++;

        // Mostrar mensaje
        Toast.makeText(this, getString(R.string.noti_tiempo_agotado), Toast.LENGTH_SHORT).show();

        // Preparar Intent para mostrar resultado (como respuesta incorrecta)
        Intent intent = new Intent(this, ResultadoPreguntaActivity.class);
        intent.putExtra("esCorrecta", false); // Marcar como incorrecta
        intent.putExtra("respuestaCorrecta", preguntaActualObj.getRespuestaCorrecta());
        intent.putExtra("explicacion", preguntaActualObj.getExplicacion());
        intent.putExtra("puntajePregunta", 0); // No se otorgan puntos
        intent.putExtra("puntajeTotal", puntaje); // Pasar puntaje total acumulado
        intent.putExtra("preguntaActual", preguntaActual);
        intent.putExtra("totalPreguntas", totalPreguntas); // Asegúrate de pasar el total de preguntas
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idDificultad", idDificultad);
        intent.putExtra("idModoJuego", idModoJuego);
        intent.putExtra("ValorCronometrado", valorCronometrado);
        intent.putExtra("preguntasCorrectas", preguntasCorrectas);
        intent.putExtra("preguntasIncorrectas", preguntasIncorrectas);
        new Handler().postDelayed(() -> {
            // Asegurarse de que la actividad de resultado se inicie después de un breve retraso
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }, 1000);

        // No incrementar preguntaActual aquí, se hará en onResume()
    }

    private void verificarRespuesta(Button botonSeleccionado) {
        // Detener temporizador si está activo
        detenerTemporizador();

        String respuestaSeleccionada = botonSeleccionado.getText().toString();
        boolean esCorrecta = respuestaSeleccionada.equals(preguntaActualObj.getRespuestaCorrecta());

        // Deshabilitar todos los botones
        opcion1.setEnabled(false);
        opcion2.setEnabled(false);
        opcion3.setEnabled(false);
        opcion4.setEnabled(false);

        // Resaltar respuestas
        if (esCorrecta) {
            botonSeleccionado.setBackgroundColor(Color.rgb(30,162,45));
            puntaje += preguntaActualObj.getPuntaje();
            preguntasCorrectas++;
        } else {
            botonSeleccionado.setBackgroundColor(Color.rgb(255,87,87));;
            mostrarRespuestaCorrecta();
            preguntasIncorrectas++;
        }

        // Mostrar pantalla de resultado
        Intent intent = new Intent(this, ResultadoPreguntaActivity.class);
        intent.putExtra("esCorrecta", esCorrecta);
        intent.putExtra("respuestaCorrecta", preguntaActualObj.getRespuestaCorrecta());
        intent.putExtra("explicacion", preguntaActualObj.getExplicacion());
        intent.putExtra("puntajePregunta", esCorrecta ? preguntaActualObj.getPuntaje() : 0);
        intent.putExtra("puntajeTotal", puntaje); // Pasar puntaje total acumulado
        intent.putExtra("preguntaActual", preguntaActual);
        intent.putExtra("totalPreguntas", totalPreguntas); // Asegúrate de pasar el total de preguntas
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idDificultad", idDificultad);
        intent.putExtra("idModoJuego", idModoJuego);
        intent.putExtra("ValorCronometrado", valorCronometrado);
        intent.putExtra("preguntasCorrectas", preguntasCorrectas);
        intent.putExtra("preguntasIncorrectas", preguntasIncorrectas);
        new Handler().postDelayed(() -> {
                    // Asegurarse de que la actividad de resultado se inicie después de un breve retraso
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }, 1000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idModoJuego == 4) {
            SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
            boolean temporizadorGuardado = prefs.getBoolean("temporizadorActivo", false);

            if (temporizadorGuardado) {
                iniciarTemporizador(); // Esto usará el tiempoRestante guardado
            }
        }

        // Verificar si debemos pasar a la siguiente pregunta
        if (opcion1 != null && !opcion1.isEnabled()) {
            new Handler().postDelayed(() -> {
                resetearBotones();
                preguntaActual++;
                mostrarPregunta();
            }, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (temporizadorActivo) {
            detenerTemporizador();

            // Guardar estado en SharedPreferences
            getSharedPreferences("QuizPrefs", MODE_PRIVATE).edit()
                    .putInt("tiempoRestante", tiempoRestante)
                    .putBoolean("temporizadorActivo", true)
                    .apply();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerTemporizador();
        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply(); // Limpia si se destruye la actividad
    }

    private void mostrarRespuestaCorrecta() {
        if (opcion1.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion1.setBackgroundColor(Color.rgb(30,162,45));
        } else if (opcion2.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion2.setBackgroundColor(Color.rgb(30,162,45));
        } else if (opcion3.getText().toString().equals(preguntaActualObj.getRespuestaCorrecta())) {
            opcion3.setBackgroundColor(Color.rgb(30,162,45));
        } else {
            opcion4.setBackgroundColor(Color.rgb(30,162,45));
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
        detenerTemporizador();
        Toast.makeText(this, "Quiz completado! Puntaje: " + puntaje + "/" +
                (totalPreguntas * 10), Toast.LENGTH_LONG).show();

        guardarResultado();

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    private void guardarResultado() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Implementar lógica para guardar resultados
    }

    private void rendirse() {
        detenerTemporizador();

        SharedPreferences prefs = getSharedPreferences("QuizPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("tiempoRestante", tiempoRestante);
        editor.putBoolean("temporizadorActivo", true);
        editor.apply();
        // Mostrar pantalla de resultado
        Intent intent = new Intent(this, Rendirse.class);
        intent.putExtra("puntajeTotal", puntaje); // Pasar puntaje total acumulado
        intent.putExtra("preguntaActual", preguntaActual);
        intent.putExtra("totalPreguntas", totalPreguntas); // Asegúrate de pasar el total de preguntas
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idDificultad", idDificultad);
        intent.putExtra("idModoJuego", idModoJuego);
        intent.putExtra("ValorCronometrado", valorCronometrado);
        intent.putExtra("preguntasCorrectas", preguntasCorrectas);
        intent.putExtra("preguntasIncorrectas", preguntasIncorrectas);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

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

        public int getIdPregunta() { return idPregunta; }
        public String getPregunta() { return pregunta; }
        public String getOpcionA() { return opcionA; }
        public String getOpcionB() { return opcionB; }
        public String getOpcionC() { return opcionC; }
        public String getOpcionD() { return opcionD; }
        public String getRespuestaCorrecta() { return respuestaCorrecta; }
        public String getExplicacion() { return explicacion; }
        public int getPuntaje() { return puntaje; }
    }
}
