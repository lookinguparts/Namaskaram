package art.lookingup.namaskaram.patterns;


import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXComponentName;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.pattern.LXPattern;

@LXCategory("Form")
@LXComponentName("MoonPhase")
public class MoonPhase extends LXPattern {

  public final CompoundParameter knob =
    new CompoundParameter("Knob", 0)
    .setDescription("An example parameter");

	public MoonPhase(LX lx) {
		super(lx);
		addParameter("knob", this.knob);
	}

	@Override
	protected void run(double deltaMs) {
	  final float knob = this.knob.getValuef();
	  setColors(LXColor.gray(knob * 100));
	}

}
