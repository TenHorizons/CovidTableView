package main.gui;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TableMouseListener implements MouseListener {
    JTable table;
    @Override
    public void mouseClicked(MouseEvent e) {
        //get JTable source.
        table = (JTable) e.getSource();
        if(table.getSelectedColumn() != 0) return;
        String country = table.getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
        //open new window with detailed country info.
        SingleCountryFrame.getInstance(country);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //can let country name be highlighted.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //can let country name be not highlighted.
    }

    /**No changes to methods below.*/
    @Override
    public void mousePressed(MouseEvent e) {

    }
    @Override
    public void mouseReleased(MouseEvent e) {

    }
}
