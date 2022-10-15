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
    static double prevEnergy = 100.0;
    private int direccioGir = 1;
    private int dist = 200;
    private boolean firstScan = true;
    private boolean eliminant = false;
    private Map<String, Double> distancies = new HashMap<String, Double>();
    private Map<String, Double> energies = new HashMap<String, Double>();
    private Map<String, Punt> posicions = new HashMap<String, Punt>();
    private Map<String, Double> anglesEquip = new HashMap<String, Double>();

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
            try {
                comparteix_pos();
            } catch (IOException ignorada) {}
            execute(); 
        }
    }
    
    public void onScannedRobot(ScannedRobotEvent e) {
        if(!isTeammate(e.getName())){
            if(!firstScan){   
                if(e.getName() == getTarget()){
                    // Obtenim l'angle al que es preveu que anira el robot, per disparar-lo.
                    double theta = getAngleMoviment(e.getDistance(), e.getBearing());
                    // Hem observat que si parem el robot per disparar, es millora l'eficiencia contra MyFirstTeam per un 2%.
                    stop();
                    if(energies.get(e.getName())-e.getEnergy()>0){ //mirem si ha disparat el enemic escanejat mes proper amb la diferencia de energia
                        setAhead(36);               //avancem la mida del robot cap endavant
                    }
                    energies.put(e.getName(), e.getEnergy());

                    if(!friendlyFire(theta)){
                        turnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
                        disparControlat(e.getDistance());
                        //fire(3);
                    }
                    else{
                        //evita socar
                    }
                    resume();
                    scan();
                }
            }
            else{
                firstScan = false;
            }

            if(!eliminant){
                energies.put(e.getName(), e.getEnergy());
                distancies.put(e.getName(), e.getDistance());
            }
            else eliminant = false;
        }
        else{
            // Obtenim l'angle al que es preveu que anira el robot, per emmagatzemar-ho.
            double angle = getAngleMoviment(e.getDistance(), e.getBearing());
            anglesEquip.put(e.getName(), angle);
        }
    }

    public void onRobotDeath(RobotDeathEvent e){
        if(!isTeammate(e.getName())){
            distancies.remove(e.getName());
            eliminant = true;
        }
    }
    
    @Override 
    public void onMessageReceived(MessageEvent event){
        Punt p = (Punt) event.getMessage();     //Convertim el missatge en Missatge. 
        posicions.put(event.getSender(), p);    // Guardem la posicio del compa a la variable.
    }

    public void onHitByBullet(HitByBulletEvent e){
        setTurnRight(normalRelativeAngleDegrees(90 - (getHeading() - e.getHeading())));
		setAhead(dist);
		dist *= -1;
		scan();
    }

    public void inici() {
        // Inicialitzem colors.
        setBodyColor(Color.black);
        setGunColor(Color.red);
        setRadarColor(Color.red);
        setBulletColor(Color.red);
        setScanColor(Color.red);
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

    public void disparControlat(double distanciaRobot) {
        if (distanciaRobot > 800 || getEnergy() < 15) {
            fire(1);
        } else if (distanciaRobot > 400) {
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
    
    public boolean friendlyFire(double angle){
        // Funció per, donat un angle "a" i sabent els angles de nosaltres als companys, detectar si "a" es similar a algun angle i evitar friendly fire
        for (Map.Entry<String, Double> set : anglesEquip.entrySet()) {
            int margeAngles = 5; 
            int angleCompanyArrodonit = (int) Math.round(set.getValue());
            if(angleCompanyArrodonit - margeAngles < angle && angleCompanyArrodonit + margeAngles > angle) {
                return true; 
            } 
        }
        return false;
    }

    public void comparteix_pos() throws IOException{
        Punt p = new Punt(getX(), getY());
        broadcastMessage(p);
        posicions.put(getName(), p);
    }
    public double getAngleMoviment(double distancia, double bearing){
        // Calcular el bearing del enemic
        double enemyBearing = this.getHeading() + bearing;
        // Calcular posicio del enemic
        double enemyX = getX() + distancia * Math.sin(Math.toRadians(enemyBearing));
        double enemyY = getY() + distancia * Math.cos(Math.toRadians(enemyBearing));
        // Calcular x i y futures del target
        double dx = enemyX - this.getX();
        double dy = enemyY - this.getY();
        // Calcular angle
        return Math.toDegrees(Math.atan2(dx, dy));
    }
}

    

