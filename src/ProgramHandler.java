import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/***
 * Handles the parsing of the xml file containing the programs for a given
 * channel for SverigesRadio.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class ProgramHandler extends DefaultHandler {

    private String elementValue;
    private Program currentProgram;
    private ArrayList<Program> programs;

    /***
     * Called when the start tag of the xml file is read.
     * Used here as a constructor for the class.
     */
    @Override
    public void startDocument() {
        programs = new ArrayList<>();
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
        if(qName.equals("scheduledepisode")) {
            currentProgram = new Program();
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
        switch (qName) {
            case "title" :
                currentProgram.setName(elementValue);
                break;
            case "description" :
                currentProgram.setDescription(elementValue);
                break;
            case "starttimeutc" :
                currentProgram.setStartTime(formatDateTime(elementValue));
                break;
            case "endtimeutc" :
                currentProgram.setEndTime(formatDateTime(elementValue));
                break;
            case "imageurl" :
                currentProgram.setImage(elementValue);
                break;
            case "scheduledepisode" :
                if(isWithinRange()) {
                    programs.add(currentProgram);
                }
                break;
        }
    }

    /***
     * Converts the given string of date and time from
     * Universal Standard Time to Swedish time.
     *
     * @param dateUTC the given date and time in UTC to be converted.
     * @return the new date and time in CET.
     */
    private String formatDateTime(String dateUTC) {
        SimpleDateFormat utcFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = new Date();

        try {
            date = utcFormat.parse(dateUTC);
        } catch (ParseException e) {
            return "-";
        }

        SimpleDateFormat localFormat
                = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        localFormat.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));

        return localFormat.format(date);
    }

    /***
     * Checks if the current program starts within the given
     * interval of 12 hours.
     *
     * @return True if the current program starts withing the
     * 12 hours of current time or false otherwise.
     */
    private boolean isWithinRange() {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date currentDate = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);


        //Get interval of time 12 hours before and after current time.
        cal.add(Calendar.HOUR, -12);
        Date startDate = cal.getTime();

        cal.add(Calendar.HOUR, 24);
        Date endDate = cal.getTime();

        Date pStart = new Date();

        //Parse start time
        try {
            pStart = format.parse(currentProgram.getStartTime());
        } catch (ParseException e) {
            return false;
        }

        if(!(pStart.before(startDate) || pStart.after(endDate))) {
            return true;
        }

        return false;
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
     * Get the current list of programs.
     *
     * @return An arraylist of current programs.
     */
    public ArrayList<Program> getPrograms() {
        return programs;
    }
}
