package packetproxy.extensions.summary;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import packetproxy.model.Extension;
import packetproxy.model.Packets;

import packetproxy.extensions.summary.gui.GUISummary;

public class SummaryExtension extends Extension {
    private static JFrame owner;
    private static Packets packets;

    public SummaryExtension() throws Exception {
        super();
        this.setName("Summary");
        packets = Packets.getInstance();
    }

    public SummaryExtension(String name, String path) throws Exception {
        super(name, path);
        this.setName("Summary");
        packets = Packets.getInstance();
    }

    @Override
    public JComponent createPanel() throws Exception {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        GUISummary gui = GUISummary.getInstance();
        panel.add(gui.createPanel());

        return panel;
    }

    public static JFrame getOwner() {
        return owner;
    }

    public static void setOwner(JFrame owner) {
        SummaryExtension.owner = owner;
    }

    @Override
    public JMenuItem historyClickHandler() {
        return null;
    }
}
