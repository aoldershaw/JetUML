package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import javafx.scene.input.MouseEvent;

import static ca.mcgill.cs.jetuml.gui.GuiUtils.getMousePoint;

public class RubberbandDragMode extends AbstractDragMode
{
    public RubberbandDragMode(DiagramCanvasController pController) {
        super(pController);
    }

    @Override
    public Point drag(MouseEvent pEvent) {
        aLastMousePoint = getMousePoint(pEvent);
        aController.getSelectionModel().activateRubberband(computeRubberband());
        return getMousePoint(pEvent);
    }

    @Override
    public void endDrag(MouseEvent pEvent) {
        aController.releaseRubberband(aMouseDownPoint, getMousePoint(pEvent));
    }

    private Line computeRubberband()
    {
        return new Line(new Point(aMouseDownPoint.getX(), aMouseDownPoint.getY()),
              new Point(aLastMousePoint.getX(), aLastMousePoint.getY()));
    }
}
