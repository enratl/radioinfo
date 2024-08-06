import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/***
 * Handles the parsing of the xml file containing the channels for
 * SverigesRadio.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class ChannelHandler extends DefaultHandler {

    private String elementValue;
    private CopyOnWriteArrayList<Channel> channels;
    private Channel currentChannel;

    /***
     * Called when the start tag of the xml file is read.
     * Used here as a constructor for the class.
     */
    @Override
    public void startDocument() {
        channels = new CopyOnWriteArrayList<>();
        currentChannel = new Channel();
    }

    /***
     * Gets called when the parser reads the start tag of an element
     * in the xml file being parsed.
     *
     * @param uri the namespace uri or an empty string if the element has no
     *            namespace uri.
     * @param localName The local name of the element or an empty string if
     *                  no namespace processing is being done.
     * @param qName The name of the element.
     * @param attributes The value of the attributes of the element.
     */
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) {
        if(qName.equals("channel")) {
            currentChannel = new Channel();
            currentChannel.setId(Integer.parseInt(attributes.getValue(0)));
            currentChannel.setName(attributes.getValue(1));
        }
    }

    /***
     * Gets called when the parser reads the end tag of an element
     * in the xml file being parsed.
     *
     * @param uri The namespace uri or an empty string if the element has
     *            no namespace uri.
     * @param localName The local name of the element or an empty
     *                  string if no namespace processing is being done
     * @param qName the name of the element.
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        if(qName.equals("channel")) {
            channels.add(currentChannel);
        }
    }

    /***
     * Gets the string stored between the current element tags.
     *
     * @param ch The array of characters (string) containing the data.
     * @param start The start index of the data in the array of characters ch.
     * @param length The length of the data in the array of characters ch
     *               beginning at start.
     */
    public void characters(char[] ch, int start, int length) {
        elementValue = new String(ch, start, length);
    }

    /***
     * Get the list of channels read from the xml file.
     *
     * @return The current list of channels.
     */
    public CopyOnWriteArrayList<Channel> getChannels() {
        return channels;
    }
}
