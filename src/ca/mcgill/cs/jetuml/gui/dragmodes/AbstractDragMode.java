package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import javafx.scene.input.MouseEvent;

import static ca.mcgill.cs.jetuml.gui.GuiUtils.getMousePoint;

public abstract class AbstractDragMode implements DragMode
{
    protected DiagramCanvasController aController;
    protected Point aLastMousePoint;
    protected Point aMouseDownPoint;

    AbstractDragMode(DiagramCanvasController pController)
    {
        this.aController = pController;
    }

    @Override
    public void beginDrag(MouseEvent pEvent)
    {
        aMouseDownPoint = aLastMousePoint = getMousePoint(pEvent);
    }
}
