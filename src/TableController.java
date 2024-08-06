import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;


/***
 * Controller handles interaction between GUI and TableLoader for RadioInfo.
 *
 * @author Leo Juneblad
 * @version 1.0
 */
public class TableController {

    private GUI gui;
    private TableLoader parser;

    private BufferedImage defaultImage;
    private CopyOnWriteArrayList<Channel> channels;

    private int currentChannel;

    private boolean connected;
    private AtomicBoolean updating;


    /***
     * Constructor initializes objects.
     *
     * @param gui The gui class for the program.
     */
    public TableController(GUI gui) {
        connected = true;
        updating = new AtomicBoolean();

        this.gui = gui;

        channels = new CopyOnWriteArrayList<>();
        currentChannel = 0;

        try {
            defaultImage = ImageIO.read(getClass().getResource("black.jpg"));
        } catch (IOException e) {
            gui.displayError("Kunde ej ladda in default bild för programmen");
        }

        gui.addChannelSelectListener(new ListListener());
        gui.addProgramSelectListener(new TableListener());
        gui.addUpdateListener(new UpdateListener());

        scheduleUpdates();
    }

    /***
     * Initialize the TableLoader.
     */
    private void initializeParser() {
        try {
            this.parser = new TableLoader();
        } catch (MalformedURLException e) {
            connected = false;
            updating.set(false);
            gui.displayError("Kunde ej ansluta till SRs api");
        }

        try {
            parser.checkConnection();
            connected = true;
        } catch (IOException e) {
            connected = false;
            updating.set(false);
            gui.displayError("Ingen anslutning");
        }
    }

    /***
     * Sets a schedule to update the table of programs every hour.
     */
    public void scheduleUpdates() {
        //3600 seconds per hour * 1000 ms per second
        Timer timer = new Timer(1000 * 3600, new UpdateListener());
        timer.setInitialDelay(0);
        timer.setRepeats(true);
        timer.start();
    }

    /***
     * Clears the values displayed in the gui and the list of channels.
     */
    public void clearUI() {
        gui.clearList();
        gui.clearTable();
        channels.clear();
    }

    /***
     * Loads the channels and programs to be shown in the gui. Loading is
     * done using a swingworker so that the gui continues to be responsive
     * as loading is done.
     */
    private void loadChannels() {
        if(connected && updating.get()) {
            //Swing worker is created to load channels so that the gui does
            //not freeze.
            SwingWorker<CopyOnWriteArrayList<Channel>, Integer> thread
                    = new SwingWorker<>() {
                @Override
                protected CopyOnWriteArrayList<Channel> doInBackground()
                        throws Exception {

                    //Load all channels and programs.
                    parser.loadChannels();
                    for(Channel c : parser.getChannels()) {
                        parser.loadPrograms(c.getId());
                    }

                    return new CopyOnWriteArrayList<>(parser.getChannels());
                }

                @Override
                protected void done() {
                    try {
                        CopyOnWriteArrayList<Channel> channelsTemp = get();

                        displayValues(channelsTemp);

                        updating.set(false);
                    } catch (InterruptedException | ExecutionException e) {
                        gui.displayError("Kunde ej ladda in kanaler på "
                                + "grund av: " + e.getCause().getMessage());
                        updating.set(false);
                    }
                }
            };

            thread.execute();
        }
    }

    /***
     * Adds the given list of channels to the gui and
     * displays the programs of the current channel.
     *
     * @param channelsTemp The list of channels to be added to the gui.
     */
    private void displayValues(CopyOnWriteArrayList<Channel> channelsTemp) {
        //Add channels to list.
        for(Channel c : channelsTemp) {
            gui.addChannelToList(c);
        }

        //Add current channels programs to gui.
        for(Program p :
                channelsTemp.get(currentChannel).getPrograms()) {
            String[] data = { p.getName(), p.getStartTime(), p.getEndTime() };

            gui.addProgram(data);
        }

        channels = channelsTemp;
    }

    /***
     * A mouse listener for the list of channels in the gui.
     * Updates the table of programs to the programs from the
     * selected channel.
     */
    class ListListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                Channel c = gui.getSelectedChannel();

                //If programs could not be found for the channel.
                if(c.getId() == 0) {
                    gui.displayError("Kunde ej hitta programtablå för "
                            + "denna kanal");
                }

                gui.clearTable();

                //Add programs to table.
                for(Program p : c.getPrograms()) {
                    String[] data = { p.getName(), p.getStartTime(),
                            p.getEndTime() };

                    gui.addProgram(data);
                }

                //Set the new currently displayed channel.
                for(Channel channel : channels) {
                    if(c.getId() == channel.getId()) {
                        currentChannel = channels.indexOf(channel);
                    }
                }

                gui.setTitle(c.getName());
                gui.closeChannelSelect();
            }
        }
    }

    /***
     * The mouse listener for the table of programs. Opens
     * a new window with information about the selected program.
     */
    class TableListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                for(Program p : channels.get(currentChannel).getPrograms()) {
                    if(gui.getSelectedProgram().equals(p.getName())) {
                        try {
                            BufferedImage img =
                                    ImageIO.read(new URL(p.getImage()));
                            gui.setImage(img);
                        } catch (IOException ex) {
                            gui.setImage(defaultImage);
                        }

                        gui.setProgramInfo(p.getDescription(),
                                p.getStartTime(), p.getEndTime());
                        gui.setProgramName(p.getName());

                        gui.openSelectedProgram(
                                channels.get(currentChannel).getName());

                        break;
                    }
                }
            }
        }
    }

    /***
     * Action listener for the update button in the menu in the gui.
     * Loads in the table again.
     */
    class UpdateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(updating.compareAndSet(false, true)) {
                initializeParser();

                clearUI();
                loadChannels();
            }
        }
    }
}
