package upc.edu.prop;

// Classe creada per poder comunicar-se entre robotos de forma més fàcil.
public class Punt implements java.io.Serializable {
    
    //Dues variables, coordenades
    private double x;
    private double y;

    public Punt(double X, double Y){
        this.x=X;
        this.y=Y;
    }

    // Normalment enviarem coordenades, d'aqui ve el nom.
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
