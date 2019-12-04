package com.clipcat;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.ImageIcon;

/**
 * Class to manage copying stuff from and putting stuff in the clipboard.
 */

public class ClipboardManager {


    /**
     * Get the clipboard data.
     *
     * @return Object containing data that existed in the clipboard.
     */
    public static ClipboardObject getData()
    {
        Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        ClipboardObject clipboardObject = null;

        if(t.isDataFlavorSupported(DataFlavor.imageFlavor))
        {
            try(ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream os = new ObjectOutputStream(bo))
            {
                // getting the image and converting it to an ImageIcon
                Image o = (Image) t.getTransferData(DataFlavor.imageFlavor);
                ImageIcon image = new ImageIcon(o);

                // serialization
                os.writeObject(image);
                os.flush();
                byte[] bytes = bo.toByteArray();
                clipboardObject = new ClipboardObject(bytes, ObjectType.IMAGE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else if(t.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            try {
                String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                byte[] bytes = s.getBytes();
                clipboardObject = new ClipboardObject(bytes, ObjectType.TEXT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return clipboardObject;
    }

    /**
     * Set the clipboard data.
     *
     * @param clipboardObject Data to put in the clipboard.
     */
    public static void setData(ClipboardObject clipboardObject)
    {
        if(clipboardObject == null)
            throw new IllegalArgumentException("Clipboard object cannot be null");

        // the clipboard object is text
        if(clipboardObject.objectType == ObjectType.TEXT){
            String text = new String(clipboardObject.data);
            StringSelection t = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(t, null);

        // the clipboard object is an image
        }else if(clipboardObject.objectType == ObjectType.IMAGE){
            ByteArrayInputStream bis = new ByteArrayInputStream(clipboardObject.data);
            try(ObjectInput in = new ObjectInputStream(bis)) {
                ImageIcon img = (ImageIcon) in.readObject();
                Image original =  img.getImage();

                BufferedImage newImage = new BufferedImage(
                        original.getWidth(null), original.getHeight(null), BufferedImage.TYPE_INT_RGB);

                Graphics2D g = newImage.createGraphics();
                g.drawImage(original, 0, 0, null);
                g.dispose();
                ImageTransferable t = new ImageTransferable(newImage);
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(t, null);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Static class to handle copying images to the clipboard.
     */
    static class ImageTransferable implements Transferable
    {
        private Image image;

        public ImageTransferable (Image image)
        {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException
        {
            if (isDataFlavorSupported(flavor))
            {
                return image;
            }
            else
            {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        public boolean isDataFlavorSupported (DataFlavor flavor)
        {
            return flavor.equals(DataFlavor.imageFlavor);
        }

        public DataFlavor[] getTransferDataFlavors ()
        {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }
    }
}
