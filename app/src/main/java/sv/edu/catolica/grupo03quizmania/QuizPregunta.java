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

        // Mostrar primera pregunta
        mostrarPregunta();
    }

    private void cargarPreguntas() {
        listaPreguntas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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

        if (listaPreguntas.isEmpty()) {
            Toast.makeText(this, "No hay preguntas disponibles para esta configuración", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Collections.shuffle(listaPreguntas);
        totalPreguntas = Math.min(listaPreguntas.size(), totalPreguntas);
    }

    private void mostrarPregunta() {
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
        // Convertir valorCronometrado a segundos
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

    private void actualizarTemporizadorUI() {
        int minutos = tiempoRestante / 60;
        int segundos = tiempoRestante % 60;
        String tiempoFormateado = String.format("%02d:%02d", minutos, segundos);
        tvTemporizador.setText(tiempoFormateado);

        // Cambiar color cuando quedan 10 segundos o menos
        if (tiempoRestante <= 10) {
            tvTemporizador.setTextColor(Color.RED);
        } else {
            tvTemporizador.setTextColor(Color.BLACK);
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

        // Mostrar mensaje
        Toast.makeText(this, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show();

        // Preparar Intent para mostrar resultado (como respuesta incorrecta)
        Intent intent = new Intent(this, ResultadoPreguntaActivity.class);
        intent.putExtra("esCorrecta", false); // Marcar como incorrecta
        intent.putExtra("respuestaCorrecta", preguntaActualObj.getRespuestaCorrecta());
        intent.putExtra("explicacion", preguntaActualObj.getExplicacion());
        intent.putExtra("puntajePregunta", 0); // No se otorgan puntos

        // Iniciar la actividad de resultado
        startActivity(intent);

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
        detenerTemporizador();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerTemporizador();
    }

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
        Toast.makeText(this, "Has terminado el quiz. Puntaje obtenido: " + puntaje, Toast.LENGTH_LONG).show();
        guardarResultado();
        finish();
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