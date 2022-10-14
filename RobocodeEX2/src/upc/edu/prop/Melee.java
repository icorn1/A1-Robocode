package upc.edu.prop;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 *
 * @author icorn1 & almogawer
 */
public class Melee extends TeamRobot {
    private int direccioGir = 1;                         // Direccio de gir del kamikaze
     
    public void run() {
        try {
            inici();                    // Inicialitzar colors, trobar la disatncia de cada robot a cada corner.
        } catch (IOException ignored) {}
        
        while(true) {                   // Dos casos: Si el robot es el lider, o si no ho és.
            setTurnRadarRight(10000);
            setTurnRight(5 * direccioGir);
            execute();
        }
    }
    
    @Override 
    public void onMessageReceived(MessageEvent event){
        
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Ignorem els casos on el robot escanejat es company d'equip.
        if(!isTeammate(e.getName())){
             // Si el lider escaneja un robot, ens fiquem en la seva trajectoria i anem a per ell!
                // Calcular el bearing del enemic
                double enemyBearing = this.getHeading() + e.getBearing();
                // Calcular posicio del enemic
                double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));

                // Ara anem a per ell modifican el gir per a igualar trajectories.
                if (e.getBearing() >= 0) {
                    direccioGir = 1;
                } else {
                    direccioGir = -1;
                }
        
                turnRight(e.getBearing());
                ahead(e.getDistance() + 5);
                scan(); // Reinicia l'event   
        }
    }
   
    public void onHitRobot(HitRobotEvent e) {
        // Dos casos, que sigui lider o que no ho sigui.
                         // Si es lider, dispara i continua xocant al robot.
            if(!isTeammate(e.getName())){
                if (e.getBearing() >= 0) {
                    direccioGir = 1;
                } else {
                    direccioGir = -1;
                }
                turnRight(e.getBearing());
                fire(3);
                ahead(40); // A per ell!!
            }
        
    }
		
    public void inici() throws IOException {
        // Inicialitzem colors.
        setBodyColor(Color.black);
        setGunColor(Color.blue);
        setRadarColor(Color.blue);
        setBulletColor(Color.black);
        setScanColor(Color.blue);
 
    }
}

   