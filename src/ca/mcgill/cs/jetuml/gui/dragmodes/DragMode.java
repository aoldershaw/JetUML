package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.geom.Point;
import javafx.scene.input.MouseEvent;

public interface DragMode
{
    void beginDrag(MouseEvent pEvent);
    Point drag(MouseEvent pEvent);
    void endDrag(MouseEvent pEvent);
}
