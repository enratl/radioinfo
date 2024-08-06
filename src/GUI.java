import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/***
 * Gui class that handles gui components for RadioInfo.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class GUI {

    private JFrame tableFrame;
    private JFrame channelFrame;
    private JFrame programFrame;

    private JMenuItem uppdatera;

    private JTable table;
    private JScrollPane scrollPane;

    private JList<Channel> list;
    private JScrollPane listPane;

    private JPanel panel;
    private JPanel programPanel;

    private JTextArea programInfo;

    private JLabel labelImage;
    private JLabel programName;

    private String infoMessage = "Skapat av:\n"
            + "Leo Juneblad\n"
            + "För: Applikationsutveckling i Java - Umeå Universitet";

    private String helpMessage = "Hur programmet används:\n\n"
            + "I tabellen på startsidan visas namnen, start tider\n"
            + "och slut tider för program i den aktuella kanalen.\n"
            + "Programmen som visas är de som spelats 12 timmar innan\n"
            + "nu och 12 timmar efter nu. Tabellen uppdateras varje timme.\n"
            + "Namnet på den aktuella kanalen visas ovanför tabellen.\n\n"
            + "För att välja en ny kanal klickar du Kanaler->Välj Kanal.\n\n"
            + "För att manuelt uppdatera tabellen innan en timme har gått\n"
            + "klickar du på Alternativ -> Uppdatera.";

    /***
     * Constructor, build the needed JFrames.
     */
    public GUI() {
        buildTableFrame();
        buildChannelFrame();
        buildProgramFrame();
    }

    /***
     * Makes the main JFrame visible.
     */
    public void setVisible() {
        tableFrame.setVisible(true);
    }

    /***
     * Builds the main JFrame containing the table and the menu.
     */
    private void buildTableFrame() {
        tableFrame = new JFrame("RadioInfo");
        tableFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        tableFrame.setMinimumSize(new Dimension(380, 300));
        tableFrame.setLayout(new BorderLayout());

        buildMenu();
        buildTable();

        tableFrame.add(panel, BorderLayout.CENTER);
        tableFrame.setJMenuBar(buildMenu());
        tableFrame.pack();
    }

    /***
     * Builds the JFrame containing the list of channels.
     */
    private void buildChannelFrame() {
        channelFrame = new JFrame("Välj Kanal");
        channelFrame.setMinimumSize(new Dimension(380, 250));

        buildList();

        channelFrame.add(listPane);
    }

    /***
     * Builds the JFrame that contains information about the selected program.
     */
    private void buildProgramFrame() {
        programFrame = new JFrame("Program");
        programFrame.setMinimumSize(new Dimension(380, 250));
        programFrame.setLayout(new BorderLayout());

        programFrame.add(buildProgramPanel());
    }

    /***
     * Initializes the list of channels and adds it to a scrollpane.
     */
    private void buildList() {
        list = new JList<>();
        DefaultListModel<Channel> listModel = new DefaultListModel<>();

        list.setModel(listModel);
        listPane = new JScrollPane(list);
    }

    /***
     * Adds the given channel to the jlist of channels.
     *
     * @param channel The channel to be added to the list.
     */
    public void addChannelToList(Channel channel) {
        DefaultListModel<Channel> model =
                (DefaultListModel<Channel>) list.getModel();

        model.addElement(channel);
    }

    /***
     * Gets the highlighted channel in the jlist of channels.
     *
     * @return The selected channel.
     */
    public Channel getSelectedChannel() {
        return list.getSelectedValue();
    }

    /***
     * Gets the name of the selected program in the table of programs.
     *
     * @return The name of the selected program in the table.
     */
    public String getSelectedProgram() {
        return (String) table.getValueAt(table.getSelectedRow(), 0);
    }

    /***
     * Removes all entries from the table of programs.
     */
    public void clearTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        tableModel.setRowCount(0);
    }

    /***
     * Removes all elements from the list of channels.
     */
    public void clearList() {
        DefaultListModel listModel = (DefaultListModel) list.getModel();

        listModel.removeAllElements();
    }

    /***
     * Adds a program to the table of programs.
     *
     * @param data An array of values to be entered on a row in the list.
     */
    public void addProgram(String[] data) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        tableModel.fireTableDataChanged();

        tableModel.addRow(data);
    }

    /***
     * Creates the jtable to hold the programs and adds it to a scrollpane.
     */
    private void buildTable () {

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        String[][] data = {};
        String[] names = { "Program", "Starttid", "Sluttid" };

        DefaultTableModel model = new DefaultTableModel(data, names) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable();
        table.setModel(model);

        scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "P1",
                TitledBorder.CENTER, TitledBorder.TOP));

    }

    /***
     * Initializes the label to hold the program icon.
     *
     * @return The label to hold an image.
     */
    public JLabel buildImage() {
        labelImage = new JLabel();

        return labelImage;
    }

    /***
     * Adds menus to the menubar.
     *
     * @return The jmenubar that contains the menus that have been added.
     */
    private JMenuBar buildMenu() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(buildAboutMenu());
        menuBar.add(buildChannelMenu());
        menuBar.add(buildOptionsMenu());

        return menuBar;
    }

    /***
     * Create the menu that contains the "select" menu button.
     *
     * @return The menu containing the relevant buttons.
     */
    private JMenu buildChannelMenu() {
        JMenu kanaler = new JMenu("Kanaler");

        JMenuItem pickChannels = new JMenuItem("Välj Kanal");

        pickChannels.addActionListener(e -> {
            openChannelSelect();
        });

        kanaler.add(pickChannels);

        return kanaler;
    }

    /***
     * Puts together the options menu.
     *
     * @return the options menu.
     */
    private JMenu buildOptionsMenu() {
        JMenu alternativ = new JMenu("Alternativ");

        uppdatera = new JMenuItem("Uppdatera");

        alternativ.add(uppdatera);

        return alternativ;
    }

    /***
     * Puts together the about menu.
     *
     * @return the about menu.
     */
    private JMenu buildAboutMenu() {
        JMenu om = new JMenu("Om");

        JMenuItem info = new JMenuItem("Info");

        info.addActionListener(e -> {
            JOptionPane.showMessageDialog(tableFrame, infoMessage, "Om",
                    JOptionPane.PLAIN_MESSAGE);
        });

        JMenuItem help = new JMenuItem("Hjälp");

        help.addActionListener(e -> {
            JOptionPane.showMessageDialog(tableFrame, helpMessage, "Hjälp",
                    JOptionPane.PLAIN_MESSAGE);
        });

        om.add(info);
        om.add(help);

        return om;
    }

    /***
     * Puts together the panel that contains information about a
     * selected program.
     *
     * @return The jpanel with program information.
     */
    private JPanel buildProgramPanel() {
        programPanel = new JPanel();

        programInfo = new JTextArea();
        programName = new JLabel();

        programInfo.setLineWrap(true);
        programInfo.setWrapStyleWord(true);
        programInfo.setEditable(false);

        programPanel.setLayout(new BorderLayout());

        programPanel.add(programName, BorderLayout.NORTH);
        programPanel.add(buildImage(), BorderLayout.CENTER);
        programPanel.add(programInfo, BorderLayout.SOUTH);

        programPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Program",
                TitledBorder.CENTER, TitledBorder.TOP));

        return programPanel;
    }

    /***
     * Displays the information about the program in the appropriate
     * swing elements.
     *
     * @param info The description of the program displayed.
     * @param startTime The start time of the program to be displayed.
     * @param endTime The end time of the program to be displayed.
     */
    public void setProgramInfo(String info, String startTime,
                               String endTime) {
        programName.setText(startTime + " -- " + endTime);
        programInfo.setText(info);
    }

    /***
     * Sets the the title of the jpanel that contains information about
     * the program.
     *
     * @param name The string to set the title to.
     */
    public void setProgramName(String name) {
        programPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), name,
                TitledBorder.CENTER, TitledBorder.TOP));
    }

    /***
     * Displays the channel select frame.
     */
    public void openChannelSelect() {
        channelFrame.setLocation(tableFrame.getLocation());
        channelFrame.setVisible(true);
    }

    /***
     * Removes the channel select frame.
     */
    public void closeChannelSelect() {
        channelFrame.setVisible(false);
    }

    /***
     * Opens the program frame with information about the selected program.
     * @param channel The channel containing the selected program.
     */
    public void openSelectedProgram(String channel) {
        programFrame.setTitle(channel);
        programFrame.setLocation(tableFrame.getLocation());
        programFrame.setVisible(true);
    }

    /***
     * Sets the image icon to be displayed in the program frame.
     *
     * @param bufImage The image to display.
     */
    public void setImage(BufferedImage bufImage) {
        Image image = bufImage.getScaledInstance(100, 100,
                Image.SCALE_SMOOTH);

        labelImage.setIcon(new ImageIcon(image));
    }

    /***
     * Sets the title of the jpanel containing the table of programs.
     *
     * @param channel The string to be set as a title preferably the
     *                name of the current channel.
     */
    public void setTitle(String channel) {
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), channel,
                TitledBorder.CENTER, TitledBorder.TOP));
    }


    /***
     * Displays an error message.
     *
     * @param error The message to be displayed.
     */
    public void displayError(String error) {
        JOptionPane.showMessageDialog(tableFrame, error);
    }

    /***
     * Adds an action listener to the update menu item.
     *
     * @param actionListener The action listener to be added.
     */
    public void addUpdateListener(ActionListener actionListener) {
        uppdatera.addActionListener(actionListener);
    }

    /***
     * Adds a mouse listener to the list of channels.
     *
     * @param mouseListener the mouse listener to be added.
     */
    public void addChannelSelectListener(MouseListener mouseListener) {
        list.addMouseListener(mouseListener);
    }

    /***
     * Adds a mouse listener to the table of programs.
     *
     * @param mouseListener The mouse listener to be added.
     */
    public void addProgramSelectListener(MouseListener mouseListener) {
        table.addMouseListener(mouseListener);
    }
}
