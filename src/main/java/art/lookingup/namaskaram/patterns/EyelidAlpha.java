package art.lookingup.namaskaram.patterns;

import heronarts.lx.LX;
import heronarts.lx.LXCategory;
import heronarts.lx.LXComponentName;
import heronarts.lx.parameter.CompoundParameter;
import heronarts.lx.parameter.BooleanParameter;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

@LXCategory("Animation")
@LXComponentName("EyelidAlpha")
public class EyelidAlpha extends Render2DBase {
    
    // Parameters
    protected CompoundParameter eyeRadius = new CompoundParameter("eyeRadius", 80, 10, 150);
    protected CompoundParameter blinkRate = new CompoundParameter("blinkRate", 3.0, 0.1, 10.0);
    protected CompoundParameter blinkDuration = new CompoundParameter("blinkDuration", 400, 100, 1000);
    protected BooleanParameter autoBlinkEnabled = new BooleanParameter("autoBlink", true);
    protected BooleanParameter triggerBlink = new BooleanParameter("triggerBlink", false);
    
    // Animation state
    private double blinkPhase = 0.0; // 0.0 = fully open, 1.0 = fully closed
    private boolean blinking = false;
    private double blinkStartTime = 0;
    private double lastBlinkTime = 0;
    
    public EyelidAlpha(LX lx) {
        super(lx);
        initialize(200, 200);
        addParameter("eyeRadius", eyeRadius);
        addParameter("blinkRate", blinkRate);
        addParameter("blinkDuration", blinkDuration);
        addParameter("autoBlink", autoBlinkEnabled);
        addParameter("triggerBlink", triggerBlink);
    }
    
    @Override
    protected void initialize(int width, int height) {
        this.width = width;
        this.height = height;
        // Use ARGB for alpha transparency support
        renderImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics = renderImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }
    
    @Override
    protected void renderFrame(double deltaMs) {
        updateBlinkAnimation(deltaMs);
        
        // Clear to fully transparent
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        
        // Only draw eyelids if blinking
        if (blinkPhase > 0.0) {
            int centerX = width / 2;
            int centerY = height / 2;
            int eyeRad = (int) eyeRadius.getValuef();
            drawEyelids(centerX, centerY, eyeRad);
        }
    }
    
    private void updateBlinkAnimation(double deltaMs) {
        double currentTime = System.currentTimeMillis();
        
        // Check for manual blink trigger
        if (triggerBlink.getValueb()) {
            startBlink(currentTime);
            triggerBlink.setValue(false);
        }
        
        // Auto blink timing
        if (autoBlinkEnabled.getValueb() && !blinking) {
            double blinkInterval = 1000.0 / blinkRate.getValuef(); // Convert rate to interval in ms
            if (currentTime - lastBlinkTime > blinkInterval) {
                startBlink(currentTime);
            }
        }
        
        // Update blink animation
        if (blinking) {
            double elapsed = currentTime - blinkStartTime;
            double duration = blinkDuration.getValuef();
            double progress = elapsed / duration;
            
            if (progress >= 1.0) {
                blinking = false;
                blinkPhase = 0.0;
                lastBlinkTime = currentTime;
            } else {
                // Smooth sine easing function for natural blink
                blinkPhase = Math.sin(progress * Math.PI);
            }
        }
    }
    
    private void startBlink(double currentTime) {
        if (!blinking) {
            blinking = true;
            blinkStartTime = currentTime;
        }
    }
    
    private void drawEyelids(int centerX, int centerY, int eyeRadius) {
        // Upper eyelid
        drawEyelid(centerX, centerY, eyeRadius, true, blinkPhase);
        
        // Lower eyelid
        drawEyelid(centerX, centerY, eyeRadius, false, blinkPhase);
    }
    
    private void drawEyelid(int centerX, int centerY, int eyeRadius, boolean isUpper, double closureAmount) {
        Graphics2D g2d = (Graphics2D) graphics;
        
        // Create eyelid shape
        GeneralPath eyelidPath = new GeneralPath();
        
        // Eye bounds
        int eyeLeft = centerX - eyeRadius;
        int eyeRight = centerX + eyeRadius;
        int eyeTop = centerY - eyeRadius;
        int eyeBottom = centerY + eyeRadius;
        
        if (isUpper) {
            // Upper eyelid - starts as arc, flattens as it closes
            int eyelidBottom = (int) (eyeTop + (2 * eyeRadius * closureAmount));
            
            if (closureAmount < 0.5) {
                // Early phase: curved eyelid following eye contour
                double arcHeight = eyeRadius * (1 - closureAmount * 2);
                
                eyelidPath.moveTo(eyeLeft - 20, eyeTop - 20);
                eyelidPath.lineTo(eyeRight + 20, eyeTop - 20);
                eyelidPath.lineTo(eyeRight + 20, eyelidBottom);
                
                // Curved bottom edge following eye contour
                eyelidPath.curveTo(
                    eyeRight, eyelidBottom - arcHeight/3,
                    centerX, eyelidBottom - arcHeight,
                    eyeLeft, eyelidBottom - arcHeight/3
                );
                
                eyelidPath.lineTo(eyeLeft - 20, eyelidBottom);
                eyelidPath.closePath();
            } else {
                // Later phase: flattening to straight line
                double flatness = (closureAmount - 0.5) * 2; // 0 to 1
                double arcHeight = eyeRadius * 0.5 * (1 - flatness);
                
                eyelidPath.moveTo(eyeLeft - 20, eyeTop - 20);
                eyelidPath.lineTo(eyeRight + 20, eyeTop - 20);
                eyelidPath.lineTo(eyeRight + 20, eyelidBottom);
                
                if (arcHeight > 1) {
                    eyelidPath.curveTo(
                        eyeRight, eyelidBottom - arcHeight/3,
                        centerX, eyelidBottom - arcHeight,
                        eyeLeft, eyelidBottom - arcHeight/3
                    );
                } else {
                    eyelidPath.lineTo(eyeLeft, eyelidBottom);
                }
                
                eyelidPath.lineTo(eyeLeft - 20, eyelidBottom);
                eyelidPath.closePath();
            }
        } else {
            // Lower eyelid - mirrors upper eyelid behavior
            int eyelidTop = (int) (eyeBottom - (2 * eyeRadius * closureAmount));
            
            if (closureAmount < 0.5) {
                double arcHeight = eyeRadius * (1 - closureAmount * 2);
                
                eyelidPath.moveTo(eyeLeft - 20, eyeBottom + 20);
                eyelidPath.lineTo(eyeLeft - 20, eyelidTop);
                
                // Curved top edge
                eyelidPath.curveTo(
                    eyeLeft, eyelidTop + arcHeight/3,
                    centerX, eyelidTop + arcHeight,
                    eyeRight, eyelidTop + arcHeight/3
                );
                
                eyelidPath.lineTo(eyeRight + 20, eyelidTop);
                eyelidPath.lineTo(eyeRight + 20, eyeBottom + 20);
                eyelidPath.closePath();
            } else {
                double flatness = (closureAmount - 0.5) * 2;
                double arcHeight = eyeRadius * 0.5 * (1 - flatness);
                
                eyelidPath.moveTo(eyeLeft - 20, eyeBottom + 20);
                eyelidPath.lineTo(eyeLeft - 20, eyelidTop);
                
                if (arcHeight > 1) {
                    eyelidPath.curveTo(
                        eyeLeft, eyelidTop + arcHeight/3,
                        centerX, eyelidTop + arcHeight,
                        eyeRight, eyelidTop + arcHeight/3
                    );
                } else {
                    eyelidPath.lineTo(eyeRight, eyelidTop);
                }
                
                eyelidPath.lineTo(eyeRight + 20, eyelidTop);
                eyelidPath.lineTo(eyeRight + 20, eyeBottom + 20);
                eyelidPath.closePath();
            }
        }
        
        // Draw eyelid with gradient for depth - fully opaque
        GradientPaint gradient = new GradientPaint(
            centerX - eyeRadius, centerY,
            new Color(80, 60, 50, 255),    // Fully opaque
            centerX + eyeRadius, centerY,
            new Color(120, 100, 90, 255)   // Fully opaque
        );
        g2d.setPaint(gradient);
        g2d.fill(eyelidPath);
        
        // Add eyelid outline
        g2d.setColor(new Color(60, 40, 30, 255)); // Fully opaque
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.draw(eyelidPath);
    }
}