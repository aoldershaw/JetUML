package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.geom.Point;
import javafx.scene.input.MouseEvent;

import static ca.mcgill.cs.jetuml.gui.GuiUtils.getMousePoint;

public class NoneDragMode implements DragMode
{
    @Override
    public Point drag(MouseEvent pEvent)
    {
        // Placeholder - never used
        return getMousePoint(pEvent);
    }

    @Override
    public void endDrag(MouseEvent pEvent)
    {
    }

    @Override
    public void beginDrag(MouseEvent pEvent)
    {
    }
}
