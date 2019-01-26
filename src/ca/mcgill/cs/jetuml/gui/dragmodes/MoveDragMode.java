package ca.mcgill.cs.jetuml.gui.dragmodes;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.views.Grid;
import javafx.scene.input.MouseEvent;

import java.util.Iterator;

import static ca.mcgill.cs.jetuml.gui.GuiUtils.getMousePoint;

public class MoveDragMode extends AbstractDragMode
{
    public MoveDragMode(DiagramCanvasController pController)
    {
        super(pController);
    }

    @Override
    public Point drag(MouseEvent pEvent)
    {
        Point mousePoint = getMousePoint(pEvent);
        moveSelection(mousePoint);
        return computePointToReveal(mousePoint);

    }

    @Override
    public void endDrag(MouseEvent pEvent)
    {
        alignMoveToGrid(getMousePoint(pEvent));
        aController.releaseMove();
    }

    /*
     * Move by a delta that will align the result of the move gesture with the grid.
     */
    private void alignMoveToGrid(Point pMousePoint)
    {
        Iterator<Node> selectedNodes = aController.getSelectionModel().getSelectedNodes().iterator();
        if( selectedNodes.hasNext() )
        {
            // Pick one node in the selection model, arbitrarily
            Node firstSelected = selectedNodes.next();
            Point position = firstSelected.position();
            Point snappedPosition = Grid.snapped(position);
            final int dx = snappedPosition.getX() - position.getX();
            final int dy = snappedPosition.getY() - position.getY();
            for(Node selected : aController.getSelectionModel().getSelectedNodes())
            {
                selected.translate(dx, dy);
            }
            aController.repaint();
        }
    }

    // TODO, include edges between selected nodes in the bounds check.
    // This will be doable by collecting all edges connected to a transitively selected node.
    private void moveSelection(Point pMousePoint)
    {
        int dx = (int)(pMousePoint.getX() - aLastMousePoint.getX());
        int dy = (int)(pMousePoint.getY() - aLastMousePoint.getY());

        // Ensure the selection does not exceed the canvas bounds
        Rectangle bounds = aController.getSelectionModel().getSelectionBounds();
        dx = Math.max(dx, -bounds.getX());
        dy = Math.max(dy, -bounds.getY());
        dx = Math.min(dx, (int) aController.getCanvasWidth() - bounds.getMaxX());
        dy = Math.min(dy, (int) aController.getCanvasHeight() - bounds.getMaxY());

        for(Node selected : aController.getSelectionModel().getSelectedNodes())
        {
            selected.translate(dx, dy);
        }
        aLastMousePoint = pMousePoint;
        aController.repaint();
    }

    // finds the point to reveal based on the entire selection
    private Point computePointToReveal(Point pMousePoint)
    {
        Rectangle bounds = aController.getSelectionModel().getSelectionBounds();
        int x = bounds.getMaxX();
        int y = bounds.getMaxY();

        if( pMousePoint.getX() < aLastMousePoint.getX()) 	 // Going left, reverse coordinate
        {
            x = bounds.getX();
        }
        if( pMousePoint.getY() < aLastMousePoint.getY())	// Going up, reverse coordinate
        {
            y = bounds.getY();
        }
        return new Point(x, y);
    }
}