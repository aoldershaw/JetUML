package ca.mcgill.cs.jetuml.gui;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.views.ImageCreator;
import javafx.embed.swing.SwingFXUtils;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

public class ImageExporter
{
    private ImageExporter()
    {
    }

    static final String KEY_LAST_EXPORT_DIR = "lastExportDir";
    static final String KEY_LAST_IMAGE_FORMAT = "lastImageFormat";

    static final String[] IMAGE_FORMATS = validFormats("png", "jpg", "gif", "bmp");

    /* Returns the subset of pDesiredFormats for which a registered image writer
     * claims to recognized the format */
    private static String[] validFormats(String... pDesiredFormats)
    {
        List<String> recognizedWriters = Arrays.asList(ImageIO.getWriterFormatNames());
        List<String> validFormats = new ArrayList<>();
        for( String format : pDesiredFormats )
        {
            if( recognizedWriters.contains(format))
            {
                validFormats.add(format);
            }
        }
        return validFormats.toArray(new String[validFormats.size()]);
    }

    /*
     * Return the image corresponding to the graph.
     *
     * @param pDiagram The graph to convert to an image.
     *
     * @return bufferedImage. To convert it into an image, use the syntax :
     * Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
     */
    private static BufferedImage getBufferedImage(Diagram pDiagram)
    {
        return SwingFXUtils.fromFXImage(ImageCreator.createImage(pDiagram), null);
    }

    public static void exportDiagram(Diagram diagram, File file) throws IOException
    {
        String fileName = file.getPath();
        String format = fileName.substring(fileName.lastIndexOf(".") + 1);
        Preferences.userNodeForPackage(UMLEditor.class).put(KEY_LAST_IMAGE_FORMAT, format);

        try (OutputStream out = new FileOutputStream(file))
        {
            BufferedImage image = getBufferedImage(diagram);
            if(format.equals("jpg"))	// to correct the display of JPEG/JPG images (removes red hue)
            {
                BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);
                Graphics2D graphics = imageRGB.createGraphics();
                graphics.drawImage(image, 0,  0, null);
                ImageIO.write(imageRGB, format, out);
                graphics.dispose();
            }
            else if(format.equals("bmp"))	// to correct the BufferedImage type
            {
                BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = imageRGB.createGraphics();
                graphics.drawImage(image, 0, 0, Color.WHITE, null);
                ImageIO.write(imageRGB, format, out);
                graphics.dispose();
            }
            else
            {
                ImageIO.write(image, format, out);
            }
        }
    }

    public static FileChooser getImageFileChooser(DiagramTab pFrame, File pInitialDirectory, String pInitialFormat)
    {
        assert pInitialDirectory.exists() && pInitialDirectory.isDirectory();
        DiagramTab frame = pFrame;

        FileChooser fileChooser = new FileChooser();
        for(String format : IMAGE_FORMATS )
        {
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(format.toUpperCase() + " " + RESOURCES.getString("files.image.name"), "*." +format);
            fileChooser.getExtensionFilters().add(filter);
            if( format.equals(pInitialFormat ))
            {
                fileChooser.setSelectedExtensionFilter(filter);
            }
        }
        fileChooser.setInitialDirectory(pInitialDirectory);

        // If the file was previously saved, use that to suggest a file name root.
        if(frame.getFile() != null)
        {
            File file = new File(replaceExtension(frame.getFile().getAbsolutePath(), RESOURCES.getString("application.file.extension"), ""));
            fileChooser.setInitialDirectory(file.getParentFile());
            fileChooser.setInitialFileName(file.getName());
        }
        return fileChooser;
    }

    /**
     * Edits the file path so that the pToBeRemoved extension, if found, is replaced
     * with pDesired.
     *
     * @param pOriginal
     *            the file to use as a starting point
     * @param pToBeRemoved
     *            the extension that is to be removed before adding the desired
     *            extension.
     * @param pDesired
     *            the desired extension (e.g. ".png")
     * @return original if it already has the desired extension, or a new file with
     *         the edited file path
     */
    static String replaceExtension(String pOriginal, String pToBeRemoved, String pDesired)
    {
        assert pOriginal != null && pToBeRemoved != null && pDesired != null;

        if (pOriginal.endsWith(pToBeRemoved))
        {
            return pOriginal.substring(0, pOriginal.length() - pToBeRemoved.length()) + pDesired;
        }
        else
        {
            return pOriginal;
        }
    }
}
