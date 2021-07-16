package thederpgamer.decor.utils;

import thederpgamer.decor.manager.LogManager;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * Utility functions for System Clipboard.
 *
 * @author TheDerpGamer
 * @since 07/16/2021
 */
public class ClipboardUtils implements ClipboardOwner {

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public String getClipboard() {
        try {
            return (String)  Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
        } catch(UnsupportedFlavorException | IOException exception) {
            LogManager.logException("Encountered an exception while trying to get clipboard data", exception);
        }
        return "";
    }

    public void setClipboard(String value) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(value), this);
    }
}
