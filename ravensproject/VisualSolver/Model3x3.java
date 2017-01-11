package ravensproject.VisualSolver;

import ravensproject.RavensFigure;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Model3x3 {

    private final String name;
    private final VisualFigure a;
    private final VisualFigure b;
    private final VisualFigure c;
    private final VisualFigure d;
    private final VisualFigure e;
    private final VisualFigure f;
    private final VisualFigure g;
    private final VisualFigure h;
    private final List<VisualFigure> answerFigures;

    public Model3x3(String name, RavensFigure a, RavensFigure b, RavensFigure c, RavensFigure d, RavensFigure e, RavensFigure f, RavensFigure g, RavensFigure h, Map<String, RavensFigure> answerFigures) {

        this.name = name;
        this.a = BuildVisualFigure(a);
        this.b = BuildVisualFigure(b);
        this.c = BuildVisualFigure(c);
        this.d = BuildVisualFigure(d);
        this.e = BuildVisualFigure(e);
        this.f = BuildVisualFigure(f);
        this.g = BuildVisualFigure(g);
        this.h = BuildVisualFigure(h);

        this.answerFigures =
                answerFigures.entrySet().stream()
                        .map(v -> BuildVisualFigure(v.getValue()))
                        .collect(Collectors.toList());
    }

    public int Solve() {
        List<VisualFigure> darkRatioAnswers = SelectDarkRatioAnswers();

        if (darkRatioAnswers != null && darkRatioAnswers.size() != 0) {
            List<VisualFigure> intersectionRatioAnswers = SelectIntersectionRatioAnswers(darkRatioAnswers);
            if (intersectionRatioAnswers != null && intersectionRatioAnswers.size() != 0) {
                return Integer.parseInt(intersectionRatioAnswers.get(0).getName());
            } else {
                return Integer.parseInt(darkRatioAnswers.get(0).getName());
            }
        } else {
            List<VisualFigure> intersectionRatioAnswers = SelectIntersectionRatioAnswers(answerFigures);
            if(intersectionRatioAnswers != null && intersectionRatioAnswers.size() != 0) {
                return Integer.parseInt(intersectionRatioAnswers.get(0).getName());
            }
        }
        return -1;
    }

    private List<VisualFigure> SelectIntersectionRatioAnswers(List<VisualFigure> availableAnswers) {
        List<VisualFigure> matchedIntersectionRatioAnswers = new ArrayList<>();
        double maxVariance = 3;

        double AtoB = CalculateIntersectionPixelRatio(a, b);
        double BtoC = CalculateIntersectionPixelRatio(b, c);
        double DtoE = CalculateIntersectionPixelRatio(d, e);
        double EtoF = CalculateIntersectionPixelRatio(e, f);
        double GtoH = CalculateIntersectionPixelRatio(g, h);

        double AtoD = CalculateIntersectionPixelRatio(a, d);
        double DtoG = CalculateIntersectionPixelRatio(d, g);
        double BtoE = CalculateIntersectionPixelRatio(b, e);
        double EtoH = CalculateIntersectionPixelRatio(e, h);
        double CtoF = CalculateIntersectionPixelRatio(c, f);

        boolean increasesLeftToRight = AtoB < BtoC && DtoE < EtoF;
        boolean decreasesLeftToRight = AtoB > BtoC && DtoE > EtoF;

        boolean increasesUpToDown = AtoD < DtoG && BtoE < EtoH;
        boolean decreasesUpToDown = AtoD > DtoG && BtoE > EtoH;


        boolean hasCentralIntersection = variance(DtoE, EtoF, BtoE, EtoH) < maxVariance;
        double centralIntersectionWeight = mean(DtoE, EtoF, BtoE, EtoH);

        double outsideVariance = variance(AtoB, BtoC, GtoH, AtoD, DtoG, CtoF);
        double outsideMean = mean(AtoB, BtoC, GtoH, AtoD, DtoG, CtoF);
        boolean hasOutsideRelationship = outsideVariance < maxVariance;

        boolean hasOutsideIntersection = false;
        List<VisualFigure> outsideFigures = Arrays.asList(a, b, c, d, f, g, h);

        if (hasOutsideRelationship) {
            for (VisualFigure figure : outsideFigures) {
                for (VisualFigure compareFigure : outsideFigures) {
                    if (!figure.getName().equals(compareFigure.getName())) {
                        double intersectionPixelRatio = CalculateIntersectionPixelRatio(figure, compareFigure);
                        double percentDiff = (intersectionPixelRatio - outsideMean) / outsideMean;
                        hasOutsideIntersection |= percentDiff > 0.05;
                    }
                }
            }
        }


        for (VisualFigure answerFigure : availableAnswers) {
            double HtoAnswer = CalculateIntersectionPixelRatio(h, answerFigure);
            double FtoAnswer = CalculateIntersectionPixelRatio(f, answerFigure);


            boolean matchesRows = false, matchesCols = false;

            if (hasOutsideRelationship && !hasOutsideIntersection) {
                boolean hasIntersection = false;
                for(VisualFigure compareFigure : outsideFigures) {
                    double intersectionPixelRatio = CalculateIntersectionPixelRatio(answerFigure, compareFigure);
                    double percentDiff = (intersectionPixelRatio - outsideMean) / outsideMean;
                    hasIntersection |= percentDiff > 0.05;
                }
                if(!hasIntersection) {
                    matchesCols = matchesRows = true;
                }
            } else {
                if (increasesLeftToRight) {
                    matchesRows = GtoH < HtoAnswer;
                } else if (decreasesLeftToRight) {
                    matchesRows = GtoH > HtoAnswer;
                }

                if (increasesUpToDown) {
                    matchesCols = CtoF < FtoAnswer;
                } else if (decreasesUpToDown) {
                    matchesCols = CtoF > FtoAnswer;
                }
            }

            if (matchesRows && matchesCols) {
                matchedIntersectionRatioAnswers.add(answerFigure);
            }
        }

        return matchedIntersectionRatioAnswers;
    }

    private List<VisualFigure> SelectDarkRatioAnswers() {
        List<VisualFigure> matchedDarkRatioAnswers = new ArrayList<>();

        double maxVariance = 3;

        double AtoB = CalculateDarkPixelRatio(a, b);
        double BtoC = CalculateDarkPixelRatio(b, c);
        double DtoE = CalculateDarkPixelRatio(d, e);
        double EtoF = CalculateDarkPixelRatio(e, f);
        double GtoH = CalculateDarkPixelRatio(g, h);

        boolean decreasingHorizontally = AtoB > BtoC && DtoE > EtoF;
        boolean increasingHorizontally = AtoB < BtoC && DtoE < EtoF;
        double meanHorizontalChange = mean(Math.abs(AtoB - BtoC), Math.abs(DtoE - EtoF));

        boolean isRow1Inverse = areInverse(AtoB, BtoC);
        boolean isRow2Inverse = areInverse(DtoE, EtoF);
        boolean hasInverseHorizontalRelationship = isRow1Inverse && isRow2Inverse;

        double meanAllRows = mean(AtoB, BtoC, DtoE, EtoF, GtoH);
        double varianceAllRows = variance(AtoB, BtoC, DtoE, EtoF, GtoH);

        boolean areAllRowsSimilar = varianceAllRows < maxVariance;

        double meanRowsFirst = mean(AtoB, DtoE, GtoH);
        double varianceRowsFirst = variance(AtoB, DtoE, GtoH);

        boolean areRowsFirstSimilar = varianceRowsFirst < maxVariance;

        double meanRowsSecond = mean(BtoC, EtoF);
        double varianceRowsSecond = variance(BtoC, EtoF);

        boolean areRowsSecondSimilar = varianceRowsSecond < maxVariance;

        double AtoD = CalculateDarkPixelRatio(a, d);
        double DtoG = CalculateDarkPixelRatio(d, g);
        double BtoE = CalculateDarkPixelRatio(b, e);
        double EtoH = CalculateDarkPixelRatio(e, h);
        double CtoF = CalculateDarkPixelRatio(c, f);

        boolean decreasingVertically = AtoD > DtoG && BtoE > EtoH;
        boolean increasingVertically = AtoD < DtoG && BtoE < EtoH;
        double meanVerticalChange = mean(Math.abs(AtoD - DtoG), Math.abs(BtoE - EtoH));

        boolean isCol1Inverse = areInverse(AtoD, DtoG);
        boolean isCol2Inverse = areInverse(BtoE, EtoH);
        boolean hasInverseVerticalRelationship = isCol1Inverse && isCol2Inverse;

        double meanAllCols = mean(AtoD, DtoG, BtoE, EtoH, CtoF);
        double varianceAllCols = variance(AtoD, DtoG, BtoE, EtoH, CtoF);

        boolean areAllColsSimilar = varianceAllCols < maxVariance;

        double meanColsFirst = mean(AtoD, BtoE, CtoF);
        double varianceColsFirst = variance(AtoD, BtoE, CtoF);

        boolean areColsFirstSimilar = varianceColsFirst < maxVariance;

        double meanColsSecond = mean(DtoG, EtoH);
        double varianceColsSecond = variance(DtoG, EtoH);

        boolean areColsSecondSimilar = varianceColsSecond < maxVariance;

        double centralIntersection = variance(Math.abs(BtoE), Math.abs(EtoH), Math.abs(DtoE), Math.abs(EtoF));
        boolean hasCentralIntersection = centralIntersection < maxVariance;
        boolean hasLeftOutsideRelationship = variance(AtoB, GtoH) < maxVariance;
        boolean hasTopOutsideRelationship = variance(AtoD, CtoF) < maxVariance;

        for (VisualFigure visualAnswerFigure : answerFigures) {
            double HtoAnswer = CalculateDarkPixelRatio(h, visualAnswerFigure);
            double FtoAnswer = CalculateDarkPixelRatio(f, visualAnswerFigure);
            String name = visualAnswerFigure.getName();

            visualAnswerFigure.setDarkPixelRatioHorizontal(HtoAnswer);
            visualAnswerFigure.setDarkPixelRatioVertical(FtoAnswer);

            boolean matchesRows = false, matchesCols = false;
            if (areAllRowsSimilar) {
                if (withinTolerance(meanAllRows, varianceAllRows, HtoAnswer)) {
                    matchesRows = true;
                }
            } else if (areRowsFirstSimilar && areRowsSecondSimilar) {
                if (withinTolerance(meanRowsSecond, varianceRowsSecond, HtoAnswer)) {
                    matchesRows = true;
                }
            } else if (hasCentralIntersection && hasLeftOutsideRelationship) {
                double outsideRightVariance = variance(BtoC, HtoAnswer);
                matchesRows = outsideRightVariance < maxVariance;
            } else if (decreasingHorizontally) {
                if (GtoH > HtoAnswer) {
                    if (hasInverseHorizontalRelationship) {
                        double negGtoH = -1 * GtoH;
                        double var = variance(negGtoH, HtoAnswer);
                        matchesRows = var < maxVariance;
                        visualAnswerFigure.setUseVariance(true);
                        visualAnswerFigure.setVarianceHorizontal(var);
                    } else {
                        matchesRows = true;
                    }
                }
            } else if (increasingHorizontally) {
                if (GtoH < HtoAnswer) {
                    if (hasInverseHorizontalRelationship) {
                        double negGtoH = -1 * GtoH;
                        double var = variance(negGtoH, HtoAnswer);
                        matchesRows = var < maxVariance;
                        visualAnswerFigure.setUseVariance(true);
                        visualAnswerFigure.setVarianceHorizontal(var);
                    } else {
                        matchesRows = true;
                    }
                }
            }

            if (areAllColsSimilar) {
                if (withinTolerance(meanAllCols, varianceAllCols, FtoAnswer)) {
                    matchesCols = true;
                }
            } else if (areColsFirstSimilar && areColsSecondSimilar) {
                if (withinTolerance(meanColsSecond, varianceColsSecond, FtoAnswer)) {
                    matchesCols = true;
                }
            } else if (hasCentralIntersection && hasTopOutsideRelationship) {
                double bottomVariance = variance(DtoG, FtoAnswer);
                matchesCols = bottomVariance < maxVariance;
            } else if (decreasingVertically) {
                if (CtoF > FtoAnswer) {
                    if (hasInverseVerticalRelationship) {
                        double negCtoF = -1 * CtoF;
                        double var = variance(negCtoF, FtoAnswer);
                        matchesCols = var < maxVariance;
                        visualAnswerFigure.setUseVariance(true);
                        visualAnswerFigure.setVarianceVertical(var);
                    } else {
                        matchesCols = true;
                    }
                }
            } else if (increasingVertically) {
                if (CtoF < FtoAnswer) {
                    if (hasInverseVerticalRelationship) {
                        double negCtoF = -1 * CtoF;
                        double var = variance(negCtoF, FtoAnswer);
                        matchesCols = var < maxVariance;
                        visualAnswerFigure.setUseVariance(true);
                        visualAnswerFigure.setVarianceVertical(var);
                    } else {
                        matchesCols = true;
                    }
                }
            }

            if (matchesRows && matchesCols) {
                matchedDarkRatioAnswers.add(visualAnswerFigure);
            }
        }

        if (matchedDarkRatioAnswers.size() > 1) {
            if (matchedDarkRatioAnswers.stream().allMatch(VisualFigure::isUseVariance)) {
                matchedDarkRatioAnswers.sort((aAnswer, bAnswer) -> Double.compare(aAnswer.getVarianceHorizontal() + aAnswer.getVarianceVertical(),
                        bAnswer.getVarianceHorizontal() + bAnswer.getVarianceVertical()));
            } else {
                matchedDarkRatioAnswers.sort((aAnswer, bAnswer) -> Double.compare((GtoH - aAnswer.getDarkPixelRatioHorizontal()) + (CtoF - aAnswer.getDarkPixelRatioVertical()),
                        ((GtoH - bAnswer.getDarkPixelRatioHorizontal()) + (CtoF - bAnswer.getDarkPixelRatioVertical()))));
            }
        }

        return matchedDarkRatioAnswers;
    }

    private void noop() {
    }

    private boolean areInverse(double num1, double num2) {
        if (num1 < 0 ^ num2 < 0) {
            double negNum1 = -1 * num1;
            return variance(negNum1, num2) < 3;
        }
        return false;
    }

    private double rms(double... nums) {
        double ms = 0;
        for (int i = 0; i < nums.length; i++) {
            ms += Math.pow(nums[i], 2);
        }
        ms /= nums.length;
        return Math.sqrt(ms);
    }

    private boolean withinTolerance(double mean, double variance, double value) {
        //double stddev = stddev(variance);
        //return value > (mean - (2*stddev)) && value < (mean + (2*stddev));
        return Math.abs(Math.round(value) - mean) < 3;
    }

    private double stddev(double variance) {
        return Math.sqrt(variance);
    }

    private double variance(double... nums) {

        double mean = mean(nums);
        double sum = 0;
        for (double num : nums) {
            sum += Math.pow(num - mean, 2);
        }

        return sum / nums.length;
    }

    private double mean(double... nums) {
        double sum = 0;
        for (int i = 0; i < nums.length; i++)
            sum += nums[i];

        return sum / nums.length;
    }

    private BufferedImage GetFigureImage(RavensFigure figure) {
        try {
            return ImageIO.read(new File(figure.getVisual()));
        } catch (Exception ex) {
        }

        return null;
    }

    private VisualFigure BuildVisualFigure(RavensFigure figure) {

        Set<Pixel> pixelSet = new HashSet<>();

        BufferedImage figureImage = GetFigureImage(figure);
        int width = figureImage.getWidth();
        int height = figureImage.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Pixel.IsDark(figureImage.getRGB(x, y))) {
                    if (x + 1 < width) {
                        if (Pixel.IsDark(figureImage.getRGB(x + 1, y))) {
                            pixelSet.add(new Pixel(x, y));
                            pixelSet.add(new Pixel(x + 1, y));
                        }
                    }

                    if (y + 1 < height) {
                        if (Pixel.IsDark(figureImage.getRGB(x, y + 1))) {
                            pixelSet.add(new Pixel(x, y));
                            pixelSet.add(new Pixel(x, y + 1));
                        }
                    }
                }
            }
        }

        return new VisualFigure(figure.getName(), pixelSet);
    }

    private double CalculateDarkPixelRatio(VisualFigure figure1, VisualFigure figure2) {
        int dark1 = figure1.getPixelSet().size();
        int dark2 = figure2.getPixelSet().size();

        double darkPercentage1 = ((double) dark1 / (dark1 + dark2)) * 100;
        double darkPercentage2 = ((double) dark2 / (dark1 + dark2)) * 100;

        return darkPercentage2 - darkPercentage1;
    }

    private double CalculateIntersectionPixelRatio(VisualFigure figure1, VisualFigure figure2) {

        //https://en.wikipedia.org/wiki/Jaccard_index

        int dark1 = figure1.getPixelSet().size();
        int dark2 = figure2.getPixelSet().size();

        Set<Pixel> darkIntersection = Intersection(figure1.getPixelSet(), figure2.getPixelSet());
        double darkIntersectionSize = darkIntersection.size();

        if (dark1 + dark2 == 0) {
            return 0;
        }
        return (darkIntersectionSize / (dark1 + dark2)) * 100;
    }

    private static <T> Set<T> Intersection(Set<T> set1, Set<T> set2) {
        Set<T> set1Copy = new HashSet<T>(set1);
        set1Copy.retainAll(set2);
        return set1Copy;
    }

}
