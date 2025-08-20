package art.lookingup.namaskaram.patterns;


import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXComponentName;
import heronarts.lx.color.LXColor;
import heronarts.lx.parameter.DiscreteParameter;
import heronarts.lx.color.LXColor;
import heronarts.lx.model.LXPoint;
import heronarts.lx.pattern.LXPattern;

@LXCategory("Form")
@LXComponentName("Output")
public class Output extends LXPattern {
    DiscreteParameter pos = new DiscreteParameter("Pos", 0, -1, 500)
    .setDescription("pos");

	public Output(LX lx) {
		super(lx);
		addParameter("Pos", this.pos);
	}

	@Override
	protected void run(double deltaMs) {
      int i = 0;
	  for (LXPoint p : model.points) {
        if (pos.getValuei() == -1) {
            colors[p.index] = LXColor.rgb(255, 255, 255);
        } else {
            if (i < pos.getValuei())
                colors[p.index] = LXColor.rgb(255, 255, 255);
            else
                colors[p.index] = LXColor.rgb(0, 0, 0);
            i++;
        }
      }
	}
}
