package art.lookingup.namaskaram.util;

import heronarts.lx.LX;
import org.shredzone.commons.suncalc.MoonIllumination;
import java.time.ZonedDateTime;

public class MoonCalc {
    public static float getIllumination() {
        // Get current moon phase information
        ZonedDateTime now = ZonedDateTime.now();
        MoonIllumination moon = MoonIllumination.compute()
                .on(now)
                .execute();
        
        return (float) (moon.getFraction() * 100);
    }

    public static double getPhaseAngle() {
        ZonedDateTime now = ZonedDateTime.now();
        // Get current moon phase information
        MoonIllumination moon = MoonIllumination.compute()
                .on(now)
                .execute();

        return moon.getPhase();
    }

    public static void debug() {
        ZonedDateTime now = ZonedDateTime.now();
        // Get current moon phase information
        MoonIllumination moon = MoonIllumination.compute()
                .on(now)
                .execute();
        
        double illumination = moon.getFraction() * 100;
        double phaseAngle = moon.getPhase();

        LX.log("Moon is illuminated " + illumination);
        LX.log("Phase angle degrees:" + phaseAngle);

        phaseAngle += 180f; // Normalize to 0-360 degrees
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
        
        LX.log("Current phase: " + phaseName);
    }
}