//----------------------------------------------------------------------------
// $Id$
//----------------------------------------------------------------------------

package net.sf.gogui.thumbnail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/** Thumbnail reader. */
public final class ThumbnailReader
{
    public static class MetaData
    {
        URI m_uri;

        long m_lastModified;

        String m_mimeType;

        String m_description;

        String m_software;
    }

    public static void main(String argv[]) throws IOException
    {
        for (int i = 0; i < argv.length; ++i)
        {
            System.out.println(argv[i]);
            MetaData metaData = read(new File(argv[i]));
            System.out.println("URI: " + metaData.m_uri);
            System.out.println("MTime: " + metaData.m_lastModified);
            System.out.println("MimeType: " + metaData.m_mimeType);
            System.out.println("Description: " + metaData.m_description);
            System.out.println("Software: " + metaData.m_software);
            System.out.println();
        }
    }

    public static MetaData read(File file) throws IOException
    {
        MetaData metaData = new MetaData();
        ImageInputStream stream = ImageIO.createImageInputStream(file);
        if (stream == null)
            return metaData;
        Iterator iter = ImageIO.getImageReaders(stream);
        ImageReader reader = (ImageReader)iter.next();
        reader.setInput(stream, true);
        IIOMetadata metadata = reader.getImageMetadata(0);
        String formatName = "javax_imageio_1.0";
        Node root = metadata.getAsTree(formatName);
        try
        {
            metaData.m_uri = new URI(getMeta(root, "Thumb::URI"));
        }
        catch (URISyntaxException e)
        {
            System.err.println("File " + file + " has invalid Thumb::URI "
                               + getMeta(root, "Thumb::URI"));
        }
        try
        {
            metaData.m_lastModified =
                Long.parseLong(getMeta(root, "Thumb::MTime"));
        }
        catch (NumberFormatException e)
        {
            System.err.println("File " + file + " has invalid Thumb::MTime "
                               + getMeta(root, "Thumb::MTime"));
        }
        metaData.m_mimeType = getMeta(root, "Thumb::Mimetype");
        metaData.m_description = getMeta(root, "Thumb::Description");
        metaData.m_software = getMeta(root, "Thumb::Software");
        return metaData;
    }

    /** Get meta data.
        @param node the (root) node of the meta data tree.
        @param key the key for the meta data.
        @return value or empty string if meta data does not exist.
    */
    private static String getMeta(Node node, String key)
    {
        String result = "";
        boolean found = false;
        for (Node child = node.getFirstChild(); child != null && ! found;
             child = child.getNextSibling())
        {
            if (! child.getNodeName().equals("Text"))
                continue;
            for (Node grandChild = child.getFirstChild();
                 grandChild != null && ! found;
                 grandChild = grandChild.getNextSibling())
            {
                if (! grandChild.getNodeName().equals("TextEntry"))
                    continue;
                NamedNodeMap attrs = grandChild.getAttributes();
                String keyword = "";
                String value = "";
                for (int i = 0; i < attrs.getLength(); i++)
                {
                    Node attr = attrs.item(i);
                    String name = attr.getNodeName();
                    if (name.equals("keyword"))
                        keyword = attr.getNodeValue();
                    else if (name.equals("value"))
                        value = attr.getNodeValue();
                }
                if (keyword.equals(key))
                {
                    result = value;
                    found = true;
                }
            }
        }
        return result;
    }
}
