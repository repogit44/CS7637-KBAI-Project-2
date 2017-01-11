package ravensproject.VisualSolver;

import ravensproject.RavensFigure;
import ravensproject.RavensProblem;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VisualSolver {

    private String name;
    private String problemType;
    private boolean hasVerbal = false;
    private boolean hasVisual = false;

    private Map<String, RavensFigure> ravensFigures;

    public VisualSolver(RavensProblem problem) {

        name = problem.getName();

        hasVerbal = problem.hasVerbal();
        hasVisual = problem.hasVisual();
        problemType = problem.getProblemType();

        ravensFigures = problem.getFigures();
    }

    public int Solve() {

        if(hasVisual && problemType.equals("2x2")) {
            return SolveVisual2x2();
        } else if(hasVisual && problemType.equals("3x3")) {
            return SolveVisual3x3();
        }

        return -1;
    }

    private int SolveVisual2x2() {
        return -1;
    }

    private int SolveVisual3x3() {

        RavensFigure a = ravensFigures.get("A");
        RavensFigure b = ravensFigures.get("B");
        RavensFigure c = ravensFigures.get("C");
        RavensFigure d = ravensFigures.get("D");
        RavensFigure e = ravensFigures.get("E");
        RavensFigure f = ravensFigures.get("F");
        RavensFigure g = ravensFigures.get("G");
        RavensFigure h = ravensFigures.get("H");

        Map<String, RavensFigure> answerFigures =
                ravensFigures.entrySet().stream()
                        .filter(s -> s.getKey().matches("^\\d+$"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Model3x3 model = new Model3x3(name, a, b, c, d, e, f, g, h, answerFigures);
        return model.Solve();
    }
}
