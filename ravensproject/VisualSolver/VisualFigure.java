package ravensproject.VisualSolver;

import java.util.List;
import java.util.Set;

public class VisualFigure {

    private final String name;
    private final Set<Pixel> pixelSet;

    private double darkPixelRatioHorizontal;
    private double darkPixelRatioVertical;

    private boolean useVariance;
    private double varianceHorizontal;
    private double varianceVertical;

    public VisualFigure(String name, Set<Pixel> pixelSet) {
        this.name = name;
        this.pixelSet = pixelSet;
    }

    public String getName() {
        return name;
    }

    public Set<Pixel> getPixelSet() {
        return pixelSet;
    }

    public double getDarkPixelRatioHorizontal() {
        return darkPixelRatioHorizontal;
    }

    public void setDarkPixelRatioHorizontal(double darkPixelRatioHorizontal) {
        this.darkPixelRatioHorizontal = darkPixelRatioHorizontal;
    }

    public double getDarkPixelRatioVertical() {
        return darkPixelRatioVertical;
    }

    public void setDarkPixelRatioVertical(double darkPixelRatioVertical) {
        this.darkPixelRatioVertical = darkPixelRatioVertical;
    }

    public boolean isUseVariance() {
        return useVariance;
    }

    public void setUseVariance(boolean useVariance) {
        this.useVariance = useVariance;
    }

    public double getVarianceHorizontal() {
        return varianceHorizontal;
    }

    public void setVarianceHorizontal(double varianceHorizontal) {
        this.varianceHorizontal = varianceHorizontal;
    }

    public double getVarianceVertical() {
        return varianceVertical;
    }

    public void setVarianceVertical(double varianceVertical) {
        this.varianceVertical = varianceVertical;
    }
}
