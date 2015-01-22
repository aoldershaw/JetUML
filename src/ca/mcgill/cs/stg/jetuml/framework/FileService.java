/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
package ca.mcgill.cs.stg.jetuml.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/*
 * This class produces common file open and file save dialogs for normal operation.
 */
public abstract class FileService
{
	/**
    * Gets a service that is appropriate for the mode in which this program works.
    * @return a service for local dialogs
	 */
   public static synchronized FileService getInstance(File initialDirectory)
   {
      if (service != null) return service;
      try
      {
         service = new JFileChooserService(initialDirectory);
         return service;
      }
      catch (SecurityException exception)
      {
         // that happens when we are an applet
      }
      return null;
   }
   
   
   /**
    * Gets an Open object that encapsulates the stream and name of the file that the user selected
    * @param defaultDirectory the default directory for the file chooser
    * @param defaultFile the default file for the file chooser
    * @param extensions the extension filter
    * @param optionalExtensions is an array of diagram type specific extensions. It can be null.
    * @return the Open object for the selected file
    * @throws IOException
    */
   public abstract Open open(String defaultDirectory, String defaultFile, ExtensionFilter extensions, ExtensionFilter[] optionalExtensions) throws IOException;
   /**
    * Gets a Save object that encapsulates the stream and name of the file that the user selected (or will
    * select)
    * @param defaultDirectory the default directory for the file chooser
    * @param defaultFile the default file for the file chooser
    * @param extensions the extension filter
    * @param removeExtension the extension to remove from the default file name
    * @param addExtension the extension to add to the file name
    * @return the Save object for the selected file
    * @throws IOException
    */
   public abstract Save save(String defaultDirectory, String defaultFile, ExtensionFilter extensions,
      String removeExtension, String addExtension) throws IOException;
   
   private static FileService service;

   /**
    * An Open object encapsulates the stream and name of the file that the user selected for opening.
    */
   public interface Open
   {
      /**
       * Gets the input stream corresponding to the user selection.
       * @return the input stream     
       */
      InputStream getInputStream() throws IOException ;
      /**
       * Gets the name of the file that the user selected.
       * @return the file name      
       */
      String getName() throws IOException ;
      
   }

   /**
    * A Save object encapsulates the stream and name of the file that the user selected for saving.
    */
   public interface Save
   {
      /**
       * Gets the output stream corresponding to the user selection.
       * @return the output stream     
       */
      OutputStream getOutputStream() throws IOException ;
      /**
       * Gets the name of the file that the user selected.
       * @return the file name, or null if the file dialog is only displayed when the output
       * stream is closed.       
       */
      String getName() throws IOException ;
     
   }

   /**
    * This class implements a FileService with a JFileChooser
    */
   private static class JFileChooserService extends FileService
   {
      public JFileChooserService(File initialDirectory)
      {
         fileChooser = new JFileChooser();
         fileChooser.setCurrentDirectory(initialDirectory);
      }

      public FileService.Open open(String defaultDirectory, String defaultFile, 
         ExtensionFilter filter, ExtensionFilter[] optionalFilters) throws FileNotFoundException
      {
         fileChooser.resetChoosableFileFilters();
         fileChooser.setFileFilter(filter);
         //The following loop adds in FileExtensions for a user to choose based on Diagram type.
         //Done by JoelChev
         if(optionalFilters != null){
        	 for(ExtensionFilter aFilter: optionalFilters)
        	 {
        		 fileChooser.addChoosableFileFilter(aFilter);
        	 }
         }
         if (defaultDirectory != null)
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
         if (defaultFile == null)             
            fileChooser.setSelectedFile(null);
         else
            fileChooser.setSelectedFile(new File(defaultFile));         
         int response = fileChooser.showOpenDialog(null);         
         if (response == JFileChooser.APPROVE_OPTION)
            return new Open(fileChooser.getSelectedFile());
         else
            return new Open(null);
      }

      public FileService.Save save(String defaultDirectory, String defaultFile, 
         ExtensionFilter filter, String removeExtension, String addExtension) throws FileNotFoundException
      {
         fileChooser.resetChoosableFileFilters();
         fileChooser.setFileFilter(filter);
         if (defaultDirectory == null)
            fileChooser.setCurrentDirectory(new File("."));
         else
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
         if (defaultFile != null)
         {
            File f = new File(editExtension(defaultFile, removeExtension, addExtension));                  
            fileChooser.setSelectedFile(f);
         }
         else 
            fileChooser.setSelectedFile(new File(""));
         int response = fileChooser.showSaveDialog(null);         
         if (response == JFileChooser.APPROVE_OPTION)
         {
            File f = fileChooser.getSelectedFile();
            if (addExtension != null && f.getName().indexOf(".") < 0) // no extension supplied
               f = new File(f.getPath() + addExtension);
            if (!f.exists()) return new Save(f);
            
            ResourceBundle editorResources = 
               ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
            int result = JOptionPane.showConfirmDialog(
                  null,
                  editorResources.getString("dialog.overwrite"), 
                  null,
                  JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) 
               return new Save(f);                       
         }
         
         return new Save(null);
      }

      public class Open implements FileService.Open
      {
         public Open(File f) throws FileNotFoundException
         {
            if (f != null)
            {
               name = f.getPath();
               in = new FileInputStream(f);
            }
         }

         public String getName() { return name; }
         public InputStream getInputStream() { return in; }
        


         private String name;
         private InputStream in;
      }

      public class Save implements FileService.Save
      {
         public Save(File f) throws FileNotFoundException
         {
            if (f != null)
            {
               name = f.getPath();
               out = new FileOutputStream(f);
            }
         }

         public String getName() { return name; }
         public OutputStream getOutputStream() { return out; }

         private String name;
         private OutputStream out;
      }
      
      private JFileChooser fileChooser;
   }

   /**
   Edits the file path so that it ends in the desired 
   extension.
   @param original the file to use as a starting point
   @param toBeRemoved the extension that is to be
   removed before adding the desired extension. Use
   null if nothing needs to be removed. 
   @param desired the desired extension (e.g. ".png"),
   or a | separated list of extensions
   @return original if it already has the desired 
   extension, or a new file with the edited file path
   */
   public static String editExtension(String original,
   		String toBeRemoved, String desired)
   {
   	if (original == null) return null;
   	int n = desired.indexOf('|');
   	if (n >= 0) desired = desired.substring(0, n);
      String path = original;
   	if (!path.toLowerCase().endsWith(desired.toLowerCase()))
      {   		
   		if (toBeRemoved != null && path.toLowerCase().endsWith(
   				toBeRemoved.toLowerCase()))
   			path = path.substring(0, path.length() - toBeRemoved.length());
         path = path + desired;
      }
   	return path;      
   }
   
}