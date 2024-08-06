import java.util.ArrayList;

/***
 * Channel class that contains information about a channel for RadioInfo.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class Channel {

    private String name;
    private ArrayList<Program> programs;
    private int id;

    /***
     * Constructor to create new instance of channel.
     */
    public Channel() {
    }

    /***
     * Set the channel id.
     *
     * @param id the new channel id to be set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /***
     * Set the name of the channel.
     *
     * @param name the new channel name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /***
     * Set the list of programs in the channel.
     *
     * @param programs The list of programs to be set.
     */
    public void setPrograms(ArrayList<Program> programs) {
        this.programs = programs;
    }

    /***
     * Get the channel id.
     *
     * @return The channel id.
     */
    public int getId() {
        return id;
    }

    /***
     * Get the name of the channel.
     *
     * @return The name of the channel.
     */
    public String getName() {
        return name;
    }

    /***
     * Get the list of the programs in the channel.
     *
     * @return The list of programs.
     */
    public ArrayList<Program> getPrograms() {
        return programs;
    }

    /***
     * Represent the channel as a string.
     *
     * @return The name of the string.
     */
    @Override
    public String toString() {
        return name;
    }
}
