package packetproxy.extensions.summary.gui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import packetproxy.common.Utils;
import packetproxy.model.Packet;
import packetproxy.model.Packets;

public class GUISummary {
    private static GUISummary instance;

    public static GUISummary getInstance() throws Exception {
        if (instance == null) {
            instance = new GUISummary();
        }
        return instance;
    }

    private Packets packets;
    private TreePath selectedPath = null;
    private Hashtable<String, Object> treeHash;

    GUISummary() throws Exception {
        packets = Packets.getInstance();
    }

    public JComponent createPanel() throws Exception {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        treeHash = createNodeTree();
        JTree tree = new JTree(treeHash);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem copy = new JMenuItem("Copy to Clipboard");
        copy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    TreePath path = tree.getSelectionPath();
                    Object[] keys = path.getPath();
                    Hashtable<String, Object> currentNode = treeHash;
                    for (int i = 1; i < keys.length; i++) {
                        String key = keys[i].toString();
                        if (i + 1 == keys.length) {
                            String uri = (String) currentNode.get(key);
                            Toolkit kit = Toolkit.getDefaultToolkit();
                            Clipboard clip = kit.getSystemClipboard();
                            StringSelection ss = new StringSelection("request==" + uri);
                            clip.setContents(ss, ss);
                        } else {
                            if (currentNode.containsKey(key)) {
                                currentNode = (Hashtable<String, Object>) currentNode.get(key);
                            }
                        }
                    }
                } catch(Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        menu.add(copy);

        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.isPopupTrigger()) {
                    selectedPath = tree.getPathForLocation(e.getX(), e.getY());
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (Utils.isWindows() && e.isPopupTrigger()) {
                    selectedPath = tree.getPathForLocation(e.getX(), e.getY());
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tree);
        panel.add(scroll);

        return panel;
    }

    private Hashtable<String, Object> createNodeTree() throws Exception {
        List<Packet> packetList = packets.queryAll();

        Hashtable<String, Object> res = new Hashtable<>();
        for (Packet packet : packetList) {
            String summary = packet.getSummarizedRequest();
            if (summary.isEmpty())  continue;

            String[] tokens = summary.split(" ");
            String method = tokens[0];
            try {
                URL url = new URL(tokens[1]);
                String origin = url.getProtocol() + "://" + url.getHost();
                if (!res.containsKey(origin)) {
                    res.put(origin, new Hashtable<String, Object>());
                }

                String[] paths = url.getPath().split("/");
                Hashtable<String, Object> currentNode = (Hashtable<String, Object>) res.get(origin);
                for (int i = 1; i < paths.length; i++) {
                    if (i + 1 == paths.length) {
                        if (!currentNode.containsKey(method + " /" + paths[i])) {
                            currentNode.put(method + " /" + paths[i], method + " " + url.getProtocol() + "://" + url.getHost() + url.getPath());
                        }
                    } else {
                        if (!currentNode.containsKey("/" + paths[i])) {
                            currentNode.put("/" + paths[i], new Hashtable<String, Object>());
                        }
                        currentNode = (Hashtable<String, Object>) currentNode.get("/" + paths[i]);
                    }
                }
            } catch (Exception e) {
            }
        }
        return res;
    }
}