package sv.edu.catolica.grupo03quizmania;

public class PartidaHistorial {
    private String modo;
    private String categoria;
    private String dificultad;
    private String fecha;
    private int puntuacion;

    public PartidaHistorial(String modo, String categoria, String dificultad, String fecha, int puntuacion) {
        this.modo = modo;
        this.categoria = categoria;
        this.dificultad = dificultad;
        this.fecha = fecha;
        this.puntuacion = puntuacion;
    }

    public String getModo() {
        return modo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDificultad() {
        return dificultad;
    }

    public String getFecha() {
        return fecha;
    }

    public int getPuntuacion() {
        return puntuacion;
    }
}

