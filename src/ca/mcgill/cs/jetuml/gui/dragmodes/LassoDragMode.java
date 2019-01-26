package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import javafx.scene.input.MouseEvent;

import static ca.mcgill.cs.jetuml.gui.GuiUtils.getMousePoint;

public class LassoDragMode extends AbstractDragMode
{
    public LassoDragMode(DiagramCanvasController pController) {
        super(pController);
    }

    @Override
    public Point drag(MouseEvent pEvent) {
        aLastMousePoint = getMousePoint(pEvent);
        if( !pEvent.isControlDown() )
        {
            aController.getSelectionModel().clearSelection();
        }
        aController.getSelectionModel().activateLasso(computeLasso(), aController.getDiagram());
        return aLastMousePoint;
    }

    private Rectangle computeLasso()
    {
        return new Rectangle((int) Math.min(aMouseDownPoint.getX(), aLastMousePoint.getX()),
              (int) Math.min(aMouseDownPoint.getY(), aLastMousePoint.getY()),
              (int) Math.abs(aMouseDownPoint.getX() - aLastMousePoint.getX()) ,
              (int) Math.abs(aMouseDownPoint.getY() - aLastMousePoint.getY()));
    }

    @Override
    public void endDrag(MouseEvent pEvent) {
        aController.getSelectionModel().deactivateLasso();
    }
}
