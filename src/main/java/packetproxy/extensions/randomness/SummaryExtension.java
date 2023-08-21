package packetproxy.extensions.randomness;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import packetproxy.model.Extension;

public class SummaryExtension extends Extension {
    private static JFrame owner;

    public SummaryExtension() {
        super();
        this.setName("Summary");
    }

    public SummaryExtension(String name, String path) throws Exception {
        super(name, path);
        this.setName("Summary");
    }

    @Override
    public JComponent createPanel() throws Exception {
        JSplitPane vsplit_panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        return vsplit_panel;
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
