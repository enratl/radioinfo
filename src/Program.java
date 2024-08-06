import java.net.MalformedURLException;
import java.net.URL;

/***
 * Program class that contains information of a program for RadioInfo.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class Program {

    private String name;
    private String startTime;
    private String endTime;
    private String description;
    private String image;

    /***
     * Constructor to create new instance of program.
     */
    public Program() {
    }

    /***
     * Set the name of the program.
     *
     * @param name the new name of the program.
     */
    public void setName(String name) {
        this.name = name;
    }

    /***
     * Set the start time of the program.
     *
     * @param startTime the new start time.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /***
     * set the end time of the program.
     *
     * @param endTime the new end time.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /***
     * Set the url to the image of the program.
     *
     * @param image the string containing the url to the new image.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /***
     * Set the description of the program.
     *
     * @param description the new description of the program.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /***
     * Get the name of the program.
     *
     * @return the name of the program.
     */
    public String getName() {
        return name;
    }

    /***
     * Get the start time of the program.
     *
     * @return The start time of the program as a string.
     */
    public String getStartTime() {
        return startTime;
    }

    /***
     * Get the end time of the program.
     *
     * @return The end time of the program as a string.
     */
    public String getEndTime() {
        return endTime;
    }

    /***
     * Get the image of the program.
     *
     * @return The url to the programs image as a string.
     */
    public String getImage() {
        return image;
    }

    /***
     * Get the description of the program.
     *
     * @return The description of the program.
     */
    public String getDescription() {
        return description;
    }

    /***
     * Represent the program as a string.
     *
     * @return The name of the program.
     */
    @Override
    public String toString() {
        return name;
    }
}
