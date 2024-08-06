import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Time;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/***
 * Loads and parses the xml files containing the channels and
 * programs from SverigesRadio. Parsing is done with SAXParser using
 * ChannelHandler and ProgramHandler for the two different xml files.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class TableLoader {

    private URL source;
    private String channelId;

    private CopyOnWriteArrayList<Channel> channels;

    private ChannelHandler channelHandler;
    private ProgramHandler programHandler;

    private String day1;
    private String day2;


    /***
     * Constructor, initializes variables and objects.
     *
     * @throws MalformedURLException If there is a problem with the url.
     */
    public TableLoader() throws MalformedURLException {
        source = new URL("http://api.sr.se/api/v2/channels?pagination=false");

        channels = new CopyOnWriteArrayList<>();

        channelHandler = new ChannelHandler();
        programHandler = new ProgramHandler();
    }

    /***
     * Check if the system is connected to the internet.
     *
     * @throws IOException If a connection is not established.
     */
    public void checkConnection() throws IOException {
        URLConnection connection = source.openConnection();
        connection.connect();
    }

    /***
     * Parses the xml file of programs of the given channel id.
     *
     * @param id The channel to get programs from.
     * @throws ParserConfigurationException If a parser cannot be created.
     * @throws SAXException
     * @throws IOException
     */
    public synchronized void loadPrograms(int id)
            throws ParserConfigurationException, SAXException, IOException {
        boolean found = true;

        channelId = Integer.toString(id);
        findDays();

        source = new URL("http://api.sr.se/api/v2/"
                + "scheduledepisodes?pagination=false&channelid="
                + channelId + "&fromdate=" + day1 + "&todate=" + day2);

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        try {
            saxParser.parse(new InputSource(source.openStream()),
                    programHandler);
        } catch (FileNotFoundException e) {
            found = false;
        }

        //Add programs to correct channels.
        for(Channel channel : channels) {
            if(channel.getId() == id) {
                //Mark the channel if no programs were found.
                if(found == false) {
                    channel.setId(0);
                }
                else {
                    channel.setPrograms(programHandler.getPrograms());
                }
            }
        }
    }

    /***
     * Find the two days covered by the 12 hours before and after
     * the current hour, in utc time and puts the days in the
     * variables day1 and day2.
     */
    private void findDays() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        //Get time in UTC
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        //Check day 12 hours before and after current time.
        cal.add(Calendar.HOUR, -12);
        day1 = format.format(cal.getTime());

        cal.add(Calendar.HOUR, 24);
        day2 = format.format(cal.getTime());
    }

    /***
     * Parses the xml file containing all channels on SverigesRadio.
     *
     * @throws ParserConfigurationException If a parser cannot be created.
     * @throws SAXException
     * @throws IOException
     */
    public synchronized void loadChannels()
            throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        saxParser.parse(new InputSource(source.openStream()),
                channelHandler);

        channels = channelHandler.getChannels();
    }

    /***
     * Get the arraylist containing all parsed channels.
     * @return The list of channels.
     */
    public synchronized CopyOnWriteArrayList<Channel> getChannels() {
        return channels;
    }
}
