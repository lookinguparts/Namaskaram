import org.shredzone.commons.suncalc.MoonIllumination;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MoonCalc {
    public static float getIllumination() {
        // Get current moon phase information
        MoonIllumination moon = MoonIllumination.compute()
                .on(LocalDateTime.now())
                .timezone(ZoneId.systemDefault())
                .execute();
        
        return (float) (moon.getFraction() * 100);
    }

    public static double getPhaseAngle() {
        // Get current moon phase information
        MoonIllumination moon = MoonIllumination.compute()
                .on(LocalDateTime.now())
                .timezone(ZoneId.systemDefault())
                .execute();
        
        return Math.toDegrees(moon.getPhase());
    }

    public static void debug() {
        // Get current moon phase information
        MoonIllumination moon = MoonIllumination.compute()
                .on(LocalDateTime.now())
                .timezone(ZoneId.systemDefault())
                .execute();
        
        double illumination = moon.getFraction() * 100;
        double phaseAngle = Math.toDegrees(moon.getPhase());
        
        System.out.printf("Moon is %.1f%% illuminated\n", illumination);
        System.out.printf("Phase angle: %.1f degrees\n", phaseAngle);
        
        // Determine phase name
        String phaseName;
        if (phaseAngle < 22.5 || phaseAngle >= 337.5) {
            phaseName = "New Moon";
        } else if (phaseAngle < 67.5) {
            phaseName = "Waxing Crescent";
        } else if (phaseAngle < 112.5) {
            phaseName = "First Quarter";
        } else if (phaseAngle < 157.5) {
            phaseName = "Waxing Gibbous";
        } else if (phaseAngle < 202.5) {
            phaseName = "Full Moon";
        } else if (phaseAngle < 247.5) {
            phaseName = "Waning Gibbous";
        } else if (phaseAngle < 292.5) {
            phaseName = "Third Quarter";
        } else {
            phaseName = "Waning Crescent";
        }
        
        System.out.println("Current phase: " + phaseName);
    }
}