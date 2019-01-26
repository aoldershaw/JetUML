package ca.mcgill.cs.jetuml.gui;

import java.io.File;

public interface RecentFilesObserver
{
    void filesUpdated(Iterable<File> files);
}
