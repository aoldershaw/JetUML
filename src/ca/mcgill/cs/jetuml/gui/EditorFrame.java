/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
 *
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ca.mcgill.cs.jetuml.application.FileExtensions;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * The main frame that contains panes that contain diagrams.
 */
public class EditorFrame extends BorderPane
{
	private Stage aMainStage;
	private RecentFilesManager aRecentFilesManager = new RecentFilesManager(this::buildRecentFilesMenu);
	private FileMenuOperations aFileMenuOperations;
	private Menu aRecentFilesMenu;
	private WelcomeTab aWelcomeTab;
	
	/**
	 * Constructs a blank frame with a desktop pane but no diagram window.
	 * 
	 * @param pMainStage The main stage used by the UMLEditor
	 */
	public EditorFrame(Stage pMainStage) 
	{
		aMainStage = pMainStage;

		MenuBar menuBar = new MenuBar();
		setTop(menuBar);
		
		TabPane tabPane = new TabPane();
		tabPane.getSelectionModel().selectedItemProperty().addListener((pValue, pOld, pNew) -> setMenuVisibility());
		setCenter( tabPane );
	
		List<NewDiagramHandler> newDiagramHandlers = createNewDiagramHandlers();
		createFileMenu(menuBar, newDiagramHandlers);
		createEditMenu(menuBar);
		createViewMenu(menuBar);
		createHelpMenu(menuBar);
		setMenuVisibility();
		
		aWelcomeTab = new WelcomeTab(newDiagramHandlers);
		aFileMenuOperations = new FileMenuOperations(aRecentFilesManager, aMainStage);
		
		showWelcomeTabIfNecessary();
	}
	
	/*
	 * Traverses all menu items up to the second level (top level
	 * menus and their immediate sub-menus), that have "true" in their user data,
	 * indicating that they should only be enabled if there is a diagram 
	 * present. Then, sets their visibility to the boolean value that
	 * indicates whether there is a diagram present.
	 * 
	 * This method assumes that any sub-menu beyond the second level (sub-menus of
	 * top menus) will NOT be diagram-specific.
	 */
	private void setMenuVisibility()
	{
			((MenuBar)getTop()).getMenus().stream() // All top level menus
				.flatMap(menu -> Stream.concat(Stream.of(menu), menu.getItems().stream())) // All menus and immediate sub-menus
				.filter( item -> Boolean.TRUE.equals(item.getUserData())) // Retain only diagram-relevant menu items
				.forEach( item -> item.setDisable(isWelcomeTabShowing()));
	}
	
	// Returns the new menu
	private void createFileMenu(MenuBar pMenuBar, List<NewDiagramHandler> pNewDiagramHandlers) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		
		// Special menu items whose creation can't be inlined in the factory call.
		Menu newMenu = factory.createMenu("file.new", false);
		for( NewDiagramHandler handler : pNewDiagramHandlers )
		{
			newMenu.getItems().add(factory.createMenuItem(handler.getDiagramType().getName(), false, handler));
		}
		
		aRecentFilesMenu = factory.createMenu("file.recent", false);
		aRecentFilesManager.triggerObserver();

		// Standard factory invocation
		pMenuBar.getMenus().add(factory.createMenu("file", false, 
				newMenu,
				factory.createMenuItem("file.open", false, pEvent -> openWithSelector()),
				aRecentFilesMenu,
				factory.createMenuItem("file.close", true, pEvent -> close()),
				factory.createMenuItem("file.save", true, pEvent -> save()),
				factory.createMenuItem("file.save_as", true, pEvent -> saveAs()),
				factory.createMenuItem("file.export_image", true, pEvent -> exportImage()),
				factory.createMenuItem("file.copy_to_clipboard", true, pEvent -> copyToClipboard()),
				new SeparatorMenuItem(),
				factory.createMenuItem("file.exit", false, pEvent -> exit())));
	}
	
	public void openWithSelector() 
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(aRecentFilesManager.getMostRecentDirectory());
		fileChooser.getExtensionFilters().addAll(FileExtensions.getAll());

		File selectedFile = fileChooser.showOpenDialog(aMainStage);
		if (selectedFile != null) 
		{
			open(selectedFile.getAbsolutePath());
		}
		
	}
	
	public void open(String pFileName) 
	{
		List<Tab> tabs = tabs();
		TabPane pane = tabPane();
		DiagramTab tabToOpen = aFileMenuOperations.open(pFileName, tabs, pane);
		if (tabToOpen != null) 
		{
			insertGraphFrameIntoTabbedPane(tabToOpen);
		}
	}
	
	public void close() 
	{
		DiagramTab currentTab = getSelectedDiagramTab();
		boolean shouldClose = aFileMenuOperations.close(currentTab);
		if (shouldClose) 
		{
			removeGraphFrameFromTabbedPane(currentTab);
		}
	}
	
	public void save() 
	{
		DiagramTab currentTab = getSelectedDiagramTab();
		aFileMenuOperations.save(currentTab);
	}
	
	public void saveAs() 
	{
		DiagramTab currentTab = getSelectedDiagramTab();
		aFileMenuOperations.saveAs(currentTab);
	}
	
	public void exportImage() 
	{
		DiagramTab currentTab = getSelectedDiagramTab();
		aFileMenuOperations.exportImage(currentTab);
	}
	
	public void copyToClipboard() 
	{
		DiagramTab currentTab = getSelectedDiagramTab();
		aFileMenuOperations.copyToClipboard(currentTab);
	}
	
	public void exit() 
	{
		int modified = getNumberOfDirtyDiagrams();
		aFileMenuOperations.exit(modified);
	}
	
	private void createEditMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("edit", true, 
				factory.createMenuItem("edit.undo", true, pEvent -> getSelectedDiagramTab().undo()),
				factory.createMenuItem("edit.redo", true, pEvent -> getSelectedDiagramTab().redo()),
				factory.createMenuItem("edit.selectall", true, pEvent -> getSelectedDiagramTab().selectAll()),
				factory.createMenuItem("edit.properties", true, pEvent -> getSelectedDiagramTab().editSelected()),
				factory.createMenuItem("edit.cut", true, pEvent -> getSelectedDiagramTab().cut()),
				factory.createMenuItem("edit.paste", true, pEvent -> getSelectedDiagramTab().paste()),
				factory.createMenuItem("edit.copy", true, pEvent -> getSelectedDiagramTab().copy()),
				factory.createMenuItem("edit.delete", true, pEvent -> getSelectedDiagramTab().removeSelected() )));
	}
	
	private void createViewMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("view", false, 
				
				factory.createCheckMenuItem("view.show_grid", false, 
				UserPreferences.instance().getBoolean(BooleanPreference.showGrid), 
				pEvent -> UserPreferences.instance().setBoolean(BooleanPreference.showGrid, ((CheckMenuItem) pEvent.getSource()).isSelected())),
			
				factory.createCheckMenuItem("view.show_hints", false, 
				UserPreferences.instance().getBoolean(BooleanPreference.showToolHints),
				pEvent -> UserPreferences.instance().setBoolean(BooleanPreference.showToolHints, 
						((CheckMenuItem) pEvent.getSource()).isSelected())),
		
				factory.createMenuItem("view.diagram_size", false, Event -> new DiagramSizeDialog(aMainStage).show())));
	}
	
	private void createHelpMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("help", false,
				factory.createMenuItem("help.about", false, pEvent -> new AboutDialog(aMainStage).show())));
	}
	
	private List<NewDiagramHandler> createNewDiagramHandlers()
	{
		List<NewDiagramHandler> result = new ArrayList<>();
		for( DiagramType diagramType : DiagramType.values() )
		{
			result.add(new NewDiagramHandler(diagramType, pEvent ->
			{
				insertGraphFrameIntoTabbedPane(new DiagramTab(diagramType.newInstance()));
			}));
		}
		return Collections.unmodifiableList(result);
	}

	
	/* @pre there is a selected diagram tab, not just the welcome tab */
	private DiagramTab getSelectedDiagramTab()
	{
		Tab tab = ((TabPane) getCenter()).getSelectionModel().getSelectedItem();
		assert tab instanceof DiagramTab; // implies a null check.
		return (DiagramTab) tab;
	}

	/*
	 * Rebuilds the "recent files" menu. Only works if the number of
	 * recent files is less than 10. Otherwise, additional logic will need
	 * to be added to 0-index the mnemonics for files 1-9.
	 */
	private void buildRecentFilesMenu(Iterable<File> pFiles)
	{
		aRecentFilesMenu.getItems().clear();
		aRecentFilesMenu.setDisable(!pFiles.iterator().hasNext());
		int i = 1;
		for( File file : pFiles )
		{
			String name = "_" + i + " " + file.getName();
			final String fileName = file.getAbsolutePath();
			MenuItem item = new MenuItem(name);
			aRecentFilesMenu.getItems().add(item);
			item.setOnAction(pEvent -> open(fileName));
			i++;
		}
	}

	private int getNumberOfDirtyDiagrams()
	{
		return (int) tabs().stream()
			.filter( tab -> tab instanceof DiagramTab ) 
			.filter( frame -> ((DiagramTab) frame).isModified())
			.count();
	}	
	
	private List<Tab> tabs()
	{
		return ((TabPane) getCenter()).getTabs();
	}
	
	private TabPane tabPane()
	{
		return (TabPane) getCenter();
	}
	
	private boolean isWelcomeTabShowing()
	{
		return aWelcomeTab != null && 
				tabs().size() == 1 && 
				tabs().get(0) instanceof WelcomeTab;
	}
	
	/* Insert a graph frame into the tabbedpane */ 
	private void insertGraphFrameIntoTabbedPane(DiagramTab pGraphFrame) 
	{
		if( isWelcomeTabShowing() )
		{
			tabs().remove(0);
		}
		tabs().add(pGraphFrame);
		tabPane().getSelectionModel().selectLast();
	}
	
	/*
	 * Shows the welcome tab if there are no other tabs.
	 */
	private void showWelcomeTabIfNecessary() 
	{
		if( tabs().size() == 0)
		{
			aWelcomeTab.loadRecentFileLinks(aRecentFilesManager.getOpenFileHandlers(file -> 
			aFileMenuOperations.open(file.getAbsolutePath(), tabs(), tabPane())));
			tabs().add(aWelcomeTab);
		}
	}
	
	/*
	 * Removes the graph frame from the tabbed pane
	 */
	private void removeGraphFrameFromTabbedPane(DiagramTab pTab) 
	{
		pTab.close();
		tabs().remove(pTab);
		showWelcomeTabIfNecessary();
	}
}
