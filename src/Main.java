import javax.swing.*;
import java.net.MalformedURLException;

/***
 * Main class that starts the EDT that handles the gui of the program.
 *
 * @author Leo Juneblad (c19lsd)
 * @version 1.0
 */
public class Main {
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();

            TableController controller = new TableController(gui);

            gui.setVisible();
        });
    }
}
