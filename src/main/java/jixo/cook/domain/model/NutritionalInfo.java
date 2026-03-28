package jixo.cook.domain.model;

public class NutritionalInfo {

    public static double getKcal(double kj) {
        return Math.round(kj / 4.184 * 100.0) / 100.0;
    }
}
