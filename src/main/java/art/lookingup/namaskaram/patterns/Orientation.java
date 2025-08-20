package art.lookingup.namaskaram.patterns;


import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXComponentName;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;

@LXCategory("Form")
@LXComponentName("Orientation")
public class Orientation extends LXPattern {
    CompoundParameter width = new CompoundParameter("width", 0.1, 0.0, 1.0);
	public Orientation(LX lx) {
		super(lx);
        addParameter("width", width);
	}

	@Override
	protected void run(double deltaMs) {
      int i = 0;
	  for (LXPoint p : model.points) {
        if (Math.abs(0.5 - p.xn) < width.getValuef()) {
            if (p.yn > 0.8)
                colors[p.index] = LXColor.rgb(255, 0, 0);
            else
                colors[p.index] = LXColor.rgb(255, 255, 255);
        } else {
            colors[p.index] = LXColor.rgb(0, 0, 0);
        }
      }
	}
}
