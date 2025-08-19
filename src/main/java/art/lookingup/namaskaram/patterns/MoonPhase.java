package art.lookingup.namaskaram.patterns;


import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXComponentName;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.BooleanParameter;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.pattern.LXPattern;
import art.lookingup.namaskaram.util.MoonCalc;

@LXCategory("Form")
@LXComponentName("MoonPhase")
public class MoonPhase extends LXPattern {

  public final CompoundParameter phase =
    new CompoundParameter("Phase", 0, 0, 1)
    .setDescription("Current moon phase (0=new moon, 0.5=full moon, 1=new moon)");

  public final BooleanParameter animate =
    new BooleanParameter("Animate", false)
    .setDescription("Enable animation towards target phase");

  public final CompoundParameter speed =
    new CompoundParameter("Speed", 0.5, 0.1, 2.0)
    .setDescription("Animation speed multiplier");

  public final CompoundParameter holdTime =
    new CompoundParameter("HoldTime", 10, 0, 300)
    .setDescription("Hold time in seconds before resetting animation");

  private float animatedPhase = 0.0f;
  private float targetPhase = 0.0f;
  private double holdTimer = 0.0;
  private boolean isHolding = false;
  private boolean releaseHolding = false;

	public MoonPhase(LX lx) {
		super(lx);
		addParameter("phase", this.phase);
		addParameter("animate", this.animate);
		addParameter("speed", this.speed);
		addParameter("holdTime", this.holdTime);
	}

	@Override
	public void onActive() {
		super.onActive();
		MoonCalc.debug();
		animatedPhase = 0.0f;
		holdTimer = 0.0;
		isHolding = false;
	}

	@Override
	protected void run(double deltaMs) {
	  // Get current moon phase angle and normalize to 0-1
	  double phaseAngle = MoonCalc.getPhaseAngle();
	  targetPhase = (float) ((phaseAngle + 180.0) / 360.0);
	  
	  float displayPhase;

	  if (animate.isOn()) {
	    // Calculate animation speed based on deltaMs and speed parameter
	    float animationStep = (float) (deltaMs / 1000.0) * speed.getValuef();
	    
	    if (isHolding) {
	      // In hold phase - count down timer
	      holdTimer += deltaMs / 1000.0;
	      if (holdTimer >= holdTime.getValue()) {
	        // Hold time expired, reset animation to start from 0
	        isHolding = false;
	        //animatedPhase -= 1.0f;
	        holdTimer = 0.0;
			releaseHolding = true;
	      }
	      displayPhase = targetPhase; // Stay at target during hold
	    } else {
	      // Animation phase - always go to 1.0 first, then wrap to target
	      animatedPhase += animationStep;
	      
	      if (animatedPhase >= 1.0f + targetPhase &&!releaseHolding) {
	        // Completed full cycle plus reached target, start holding
	        animatedPhase = targetPhase;
	        isHolding = true;
	        holdTimer = 0.0;
	        displayPhase = targetPhase;
		  } else if (animatedPhase >= 1.0f + targetPhase && releaseHolding) {
			animatedPhase -= 1f;
			releaseHolding = false;
			displayPhase = animatedPhase; // Show wrapped value
	      } else if (animatedPhase >= 1.0f) {
	        // Past 1.0, now in the second phase going to target
	        displayPhase = animatedPhase - 1.0f; // Show wrapped value
	      } else {
	        // Still in first phase going to 1.0
	        displayPhase = animatedPhase;
	      }
	    }
	  } else {
	    // Direct mode - use target phase immediately
	    displayPhase = targetPhase;
	    animatedPhase = targetPhase; // Keep animated phase in sync
	    isHolding = false;
	    holdTimer = 0.0;
	  }
	  
	  // Update the phase parameter with current phase
	  this.phase.setValue(displayPhase);
	  
	  setColors(LXColor.gray(displayPhase * 100));
	}

}
