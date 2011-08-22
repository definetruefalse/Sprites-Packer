package packer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;
import plist.NSDictionary;
import plist.NSNumber;
import plist.PropertyListParser;

/**
 * A daft image packer
 * 
 * @author kevin
 */
public class Pack {

    /**
     * Pack the images provided
     * 
     * @param files The list of file objects pointing at the images to be packed
     * @param width The width of the sheet to be generated 
     * @param height The height of the sheet to be generated
     * @param border The border between sprites
     * @param out The file to write out to
     * @return The generated sprite sheet
     * @throws IOException Indicates a failure to write out files
     */
    public Sheet pack(ArrayList files, int width, int height, int border, File out) throws IOException, Exception {
        ArrayList images = new ArrayList();

        try {
            for (int i = 0; i < files.size(); i++) {
                File file = (File) files.get(i);
                Sprite sprite = new Sprite(file.getName(), ImageIO.read(file));

                images.add(sprite);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return packImages(images, width, height, border, out);
    }

    /**
     * Pack the images provided
     * 
     * @param images The list of sprite objects pointing at the images to be packed
     * @param width The width of the sheet to be generated 
     * @param height The height of the sheet to be generated
     * @param border The border between sprites
     * @param out The file to write out to
     * @return The generated sprite sheet
     * @throws IOException Indicates a failure to write out files
     */
    public Sheet packImages(ArrayList images, int width, int height, int border, File out) throws IOException, Exception {
        Collections.sort(images, new Comparator() {

            public int compare(Object o1, Object o2) {
                Sprite a = (Sprite) o1;
                Sprite b = (Sprite) o2;

                int asize = a.getHeight();
                int bsize = b.getHeight();
                return bsize - asize;
            }
        });

        int x = 0;
        int y = 0;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        int rowHeight = 0;
        
        //Creating the root object
        NSDictionary root = new NSDictionary();
        
        try {

            

            //Creation of an array of the length 2
            NSDictionary frames = new NSDictionary();

            NSDictionary texture = new NSDictionary();
            texture.put("width", new NSNumber(result.getWidth()));
            texture.put("height", new NSNumber(result.getHeight()));
            root.put("texture", texture);
            for (int i = 0; i < images.size(); i++) {
                Sprite current = (Sprite) images.get(i);
                if (x + current.getWidth() > width) {
                    x = 0;
                    y += rowHeight;
                    rowHeight = 0;
                }

                if (rowHeight == 0) {
                    rowHeight = current.getHeight() + border;
                }

                NSDictionary frame = new NSDictionary();
                frame.put("x", new NSNumber(x));
                frame.put("y", new NSNumber(y));
                frame.put("width", new NSNumber(current.getWidth()));
                frame.put("height", new NSNumber(current.getHeight()));
                frame.put("originalWidth", new NSNumber(current.getWidth()));
                frame.put("originalHeight", new NSNumber(current.getHeight()));
                frame.put("offsetX", new NSNumber(0));
                frame.put("offsetY", new NSNumber(0));
                frames.put(current.getName(), frame);

                current.setPosition(x, y);
                g.drawImage(current.getImage(), x, y, null);
                x += current.getWidth() + border;
            }
            g.dispose();

            //Put the array into the property list
            root.put("frames", frames);

            //Save the propery list
            //

        } catch (Exception e) {
            e.printStackTrace();
            IOException io = new IOException("Failed writing image XML");
            io.initCause(e);
            throw io;
        }

        

        if (out != null) {
            
        if(root.count() > 0) {
            try {
                PropertyListParser.saveAsXML(root, new File(out.getParentFile(), out.getName().split("\\.")[0] + ".plist"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            
            try {
                ImageIO.write(result, "PNG", out);
            } catch (IOException e) {
                e.printStackTrace();

                IOException io = new IOException("Failed writing image");
                io.initCause(e);

                throw io;
            }
        }

        return new Sheet(result, images);
    }

    /**
     * Entry point to the tool, just pack the current directory of images
     * 
     * @param argv The arguments to the program
     * @throws IOException Indicates a failure to write out files
     */
//    public static void main(String[] argv) throws IOException {
//        File dir = new File(".");
//        dir = new File("C:\\eclipse\\grobot-workspace\\anon\\res\\tiles\\indoor1");
//
//        ArrayList list = new ArrayList();
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            if (files[i].getName().endsWith(".png")) {
//                if (!files[i].getName().startsWith("output")) {
//                    list.add(files[i]);
//                }
//            }
//        }
//
//        Pack packer = new Pack();
//        //packer.pack(list, 512, 512, 1, new File(dir, "output.png"));
//        System.out.println("Output Generated.");
//    }
}
