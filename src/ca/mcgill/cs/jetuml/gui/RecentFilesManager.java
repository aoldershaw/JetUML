package ca.mcgill.cs.jetuml.gui;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.RecentFilesQueue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

public class RecentFilesManager
{
    private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
    private RecentFilesObserver aObserver;

    public RecentFilesManager(RecentFilesObserver pObserver)
    {
        aObserver = pObserver;
        aRecentFiles.deserialize(Preferences.userNodeForPackage(UMLEditor.class).get("recent", "").trim());
    }

    /*
     * Adds a file name to the "recent files" list and rebuilds the "recent files"
     * menu.
     *
     * @param pNewFile the file name to add
     */
    public void addRecentFile(String pNewFile)
    {
        aRecentFiles.add(pNewFile);
        aObserver.filesUpdated(aRecentFiles);
    }

    public File getMostRecentDirectory()
    {
        return aRecentFiles.getMostRecentDirectory();
    }

    public boolean isEmpty()
    {
        return aRecentFiles.size() == 0;
    }

    public void storeRecentFiles()
    {
        Preferences.userNodeForPackage(UMLEditor.class).put("recent", aRecentFiles.serialize());
    }

    public void triggerObserver()
    {
        aObserver.filesUpdated(aRecentFiles);
    }

    public List<NamedHandler> getOpenFileHandlers(Consumer<File> pOpenFileHandler)
    {
        List<NamedHandler> result = new ArrayList<>();
        for( File file : aRecentFiles )
        {
            result.add(new NamedHandler(file.getName(), pEvent -> pOpenFileHandler.accept(file)));
        }
        return Collections.unmodifiableList(result);
    }
}
