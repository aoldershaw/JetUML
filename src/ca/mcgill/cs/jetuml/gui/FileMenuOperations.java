package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.gui.ImageExporter.KEY_LAST_EXPORT_DIR;
import static ca.mcgill.cs.jetuml.gui.ImageExporter.KEY_LAST_IMAGE_FORMAT;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.prefs.Preferences;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.FileExtensions;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.persistence.DeserializationException;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import ca.mcgill.cs.jetuml.views.ImageCreator;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileMenuOperations
{
	
	private RecentFilesManager aRecentFilesManager;
	private Stage aMainStage;
	
	private static final String KEY_LAST_SAVEAS_DIR = "lastSaveAsDir";
	
	public FileMenuOperations(RecentFilesManager pRecentFilesManager, Stage pMainStage) 
	{
		aRecentFilesManager = pRecentFilesManager;
		aMainStage = pMainStage;
	}

	protected void save(DiagramTab pFrame) 
	{
		File file = pFrame.getFile();
		if(file == null) 
		{
			saveAs(pFrame);
			return;
		}
		try 
		{
			PersistenceService.save(pFrame.getDiagram(), file);
			pFrame.setModified(false);
		} 
		catch(IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	
	protected void saveAs(DiagramTab pFrame) 
	{
		Diagram diagram = pFrame.getDiagram();

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(FileExtensions.getAll());
		fileChooser.setSelectedExtensionFilter(FileExtensions.get(diagram.getDescription()));

		if (pFrame.getFile() != null) 
		{
			fileChooser.setInitialDirectory(pFrame.getFile().getParentFile());
			fileChooser.setInitialFileName(pFrame.getFile().getName());
		} 
		else 
		{
			fileChooser.setInitialDirectory(getLastDir(KEY_LAST_SAVEAS_DIR));
			fileChooser.setInitialFileName("");
		}

		try 
		{
			File result = fileChooser.showSaveDialog(aMainStage);
			if(fileChooser.getSelectedExtensionFilter() != FileExtensions.get(diagram.getDescription()))
			{
				result = new File(result.getPath() + diagram.getFileExtension() + RESOURCES.getString("application.file.extension"));
			}
			if(result != null) 
			{
				PersistenceService.save(diagram, result);
				aRecentFilesManager.addRecentFile(result.getAbsolutePath());
				pFrame.setFile(result);
				pFrame.setText(pFrame.getFile().getName());
				pFrame.setModified(false);
				File dir = result.getParentFile();
				if( dir != null )
				{
					setLastDir(KEY_LAST_SAVEAS_DIR, dir);
				}
			}
		} 
		catch (IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}
	
	/*
	 * Opens a file with the given name, or switches to the frame if it is already
	 * open.
	 * 
	 * @param pName the file name
	 */
	protected DiagramTab open(String pName, List<Tab> pTabs, TabPane pTabPane) 
	{
		for( Tab tab : pTabs )
		{
			if(tab instanceof DiagramTab)
			{
				if(((DiagramTab) tab).getFile() != null	&& 
						((DiagramTab) tab).getFile().getAbsoluteFile().equals(new File(pName).getAbsoluteFile())) 
				{
					System.out.println("In if");
					pTabPane.getSelectionModel().select(tab);
					aRecentFilesManager.addRecentFile(new File(pName).getPath());
					return null;
				}
			}
		}
		
		try 
		{
			Diagram diagram2 = PersistenceService.read(new File(pName));
			
			Rectangle bounds = DiagramType.newViewInstanceFor(diagram2).getBounds();
			int viewWidth = UserPreferences.instance().getInteger(IntegerPreference.diagramWidth);
			int viewHeight = UserPreferences.instance().getInteger(IntegerPreference.diagramHeight);
			if( bounds.getMaxX() > viewWidth || bounds.getMaxY() > viewHeight )
			{
				showDiagramViewTooSmallAlert(bounds, viewWidth, viewHeight);
				return null;
			}
			
			DiagramTab frame2 = new DiagramTab(diagram2);
			frame2.setFile(new File(pName).getAbsoluteFile());
			aRecentFilesManager.addRecentFile(new File(pName).getPath());
			System.out.println("Returning frame");
			return frame2;
		}
		catch (IOException | DeserializationException exception2) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.open_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
		return null;
	}
	
	/**
	 * Exports the current graph to an image file.
	 */
	protected void exportImage(DiagramTab pFrame)
	{
		FileChooser fileChooser = ImageExporter.getImageFileChooser(pFrame, getLastDir(KEY_LAST_EXPORT_DIR),
				Preferences.userNodeForPackage(UMLEditor.class).get(KEY_LAST_IMAGE_FORMAT, "png"));
		File file = fileChooser.showSaveDialog(aMainStage);
		if(file == null) 
		{
			return;
		}
		File dir = file.getParentFile();
		if( dir != null )
		{
			setLastDir(KEY_LAST_EXPORT_DIR, dir);
		}

		try
		{
			ImageExporter.exportDiagram(pFrame.getDiagram(), file);
		}
		catch(IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}
	
	protected boolean close(DiagramTab pOpenFrame) 
	{
		// we only want to check attempts to close a frame
		if (pOpenFrame.isModified()) 
		{
			// ask user if it is ok to close
			Alert alert = new Alert(AlertType.CONFIRMATION, RESOURCES.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(RESOURCES.getString("dialog.close.title"));
			alert.setHeaderText(RESOURCES.getString("dialog.close.title"));
			alert.showAndWait();

			if (alert.getResult() != ButtonType.YES) 
			{
				return false;
			}
			
		} 
		return true;
	}
	
	/**
	 * Exits the program if no graphs have been modified or if the user agrees to
	 * abandon modified graphs.
	 * @param pModcount - Integer representing the number of currently opened modified files.
	 */
	protected void exit(int pModcount) 
	{
		if (pModcount > 0) 
		{
			Alert alert = new Alert(AlertType.CONFIRMATION, 
					MessageFormat.format(RESOURCES.getString("dialog.exit.ok"), new Object[] { Integer.valueOf(pModcount) }),
					ButtonType.YES, 
					ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(RESOURCES.getString("dialog.exit.title"));
			alert.setHeaderText(RESOURCES.getString("dialog.exit.title"));
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) 
			{
				aRecentFilesManager.storeRecentFiles();
				System.exit(0);
			}
		}
		else 
		{
			aRecentFilesManager.storeRecentFiles();
			System.exit(0);
		}
	}
	
	/**
	 * Copies the current image to the clipboard.
	 * @param pFrame - The currently opened DiagramTab that contains the Diagram that will be copied into the clipboard.
	 */
	protected void copyToClipboard(DiagramTab pFrame) 
	{
		final Image image = ImageCreator.createImage(pFrame.getDiagram());
		final Clipboard clipboard = Clipboard.getSystemClipboard();
	    final ClipboardContent content = new ClipboardContent();
	    content.putImage(image);
	    clipboard.setContent(content);
		Alert alert = new Alert(AlertType.INFORMATION, RESOURCES.getString("dialog.to_clipboard.message"), ButtonType.OK);
		alert.initOwner(aMainStage);
		alert.setHeaderText(RESOURCES.getString("dialog.to_clipboard.title"));
		alert.showAndWait();
	}
	
	private File getLastDir(String pKey)
	{
		String dir = Preferences.userNodeForPackage(UMLEditor.class).get(pKey, ".");
		File result = new File(dir);
		if( !(result.exists() && result.isDirectory()))
		{
			result = new File(".");
		}
		return result;
	}
	
	private void setLastDir(String pKey, File pLastExportDir)
	{
		Preferences.userNodeForPackage(UMLEditor.class).put(pKey, pLastExportDir.getAbsolutePath().toString());
	}
	
	private void showDiagramViewTooSmallAlert(Rectangle pBounds, int pWidth, int pHeight)
	{
		String content = RESOURCES.getString("dialog.open.size_error_content");
		content = content.replace("#1", Integer.toString(pBounds.getMaxX()));
		content = content.replace("#2", Integer.toString(pBounds.getMaxY()));
		content = content.replace("#3", Integer.toString(pWidth));
		content = content.replace("#4", Integer.toString(pHeight));
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("dialog.open.size_error_header"));
		alert.initOwner(aMainStage);
		alert.showAndWait();
	}

}
