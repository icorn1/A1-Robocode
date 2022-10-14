package upc.edu.prop;

import java.awt.Color;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 *
 * @author icorn & almogawer
 */
public class Robot extends TeamRobot{

    private int direccioGir = 1;
    private int dist = 200;
    private boolean firstScan = true;
    private boolean eliminant = false;
    private Map<String, Double> distancies = new HashMap<String, Double>();
    /*
     * Cada Robot comença a escanejar d'immediat. Han de fer un escaneig complet i el que tinguin més aprop, disparar-li.
     * Quan li disparen o es xoca amb un robot:
     *      Es mou en direccio aleatoria.
     *      
     * 
     */
    public void run() {
        inici();
        while (true){
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            back(150);
            ahead(150);
            execute(); 
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        if(!isTeammate(e.getName())){
            if(!firstScan){   
                if(e.getName() == getTarget()){
                    // Calcular el bearing del enemic
                    double enemyBearing = this.getHeading() + e.getBearing();
                    // Calcular posicio del enemic
                    double enemyX = getX() + e.getDistance() * Math.sin(Math.toRadians(enemyBearing));
                    double enemyY = getY() + e.getDistance() * Math.cos(Math.toRadians(enemyBearing));
                    // Calcular x i y futures del target
                    double dx = enemyX - this.getX();
                    double dy = enemyY - this.getY();
                    // Calcular angle
                    double theta = Math.toDegrees(Math.atan2(dx, dy));
                    // Apuntar i disparar.
                    // Hem observat que si parem el robot per disparar, es millora l'eficiencia contra MyFirstTeam per un 2%.
                    stop();
                    turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                    fire(3);
                    resume();
                    scan();
                }
            }
            else{
                firstScan = false;
            }
            if(!eliminant)
                distancies.put(e.getName(), e.getDistance());
            else eliminant = false;
        }
    }
    public void onRobotDeath(RobotDeathEvent e){
        if(!isTeammate(e.getName())){
            distancies.remove(e.getName());
            eliminant = true;
        }
    }
    
    public String getTarget(){
        double min = 10000;
        String robotMesProper = "";
        for (Map.Entry<String, Double> set : distancies.entrySet()) {
            if(set.getValue() < min) {
                min = set.getValue();
                robotMesProper = set.getKey();
            } 
        }
        return robotMesProper;
    }

    public void inici() {
        // Inicialitzem colors.
        setBodyColor(Color.black);
        setGunColor(Color.red);
        setRadarColor(Color.red);
        setBulletColor(Color.red);
        setScanColor(Color.red);
   }

    public void disparControlat(double distanciaRobot) {
        if (distanciaRobot > 200 || getEnergy() < 15) {
                fire(1);
        } else if (distanciaRobot > 50) {
                fire(2);
        } else {
                fire(3);
        }
    }
    public double calcDist(double xOrg, double yOrg, double xDest, double yDest){
        //Funció usada per calcular la distància entre dos punts de coordenades
        //return Math.sqrt(Math.pow(Math.abs(xOrg-xDest),2) + Math.pow(Math.abs(yOrg-yDest),2));
        return Math.hypot((xDest-xOrg), (yDest- yOrg));
    }
    
    public double getAngleTo(double xOrg, double yOrg, double xDest, double yDest){
        return Math.toDegrees(Math.atan2(xDest-xOrg, yDest-yOrg));
    
    }
    
    public void onHitByBullet(HitByBulletEvent e){

        setTurnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
		setAhead(dist);
		dist *= -1;
		scan();
            
    }
}


