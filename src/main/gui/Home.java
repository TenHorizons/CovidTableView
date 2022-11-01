package main.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Home {
    private JFrame frame;
    private JPanel panel;
    private JTextField searchText;
    private JButton searchButton;
    private JLabel searchLabel;
    private JTable table;
    private final DefaultTableCellRenderer blueHighlight = new DefaultTableCellRenderer(){
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            //set text colour to blue.
            c.setForeground(Color.BLUE);
            //set text to be underlined.
            Map<TextAttribute, Integer> fontAttributes = new HashMap<>();
            fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            c.setFont(new Font(fontAttributes));

            return c;
        }
    };
    private final DefaultTableCellRenderer commaNumberFormat = new DefaultTableCellRenderer(){
        final DecimalFormat commaPerThousand = new DecimalFormat("#,###");
        @Override
        protected void setValue(Object value) {
            super.setValue(commaPerThousand.format(value));
        }
    };
    private JScrollPane scrollPane;

    public Home(){
        createSearchTextButtonAndLabel();
        createTable();
        fillPanel();

        //set up frame and display
        frame  = new JFrame("Covid Cases for Each Country");
        frame.setMinimumSize(new Dimension(800,500));
        frame.setLocationRelativeTo(null);//position window at centre of screen.
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void createTable() {
        //provide JTable to Model for formatting with CellRenderer.
        table = new JTable(new AllCountriesTableModel());
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //set text color of country column to blue underlined (hyperlink)
        table.getColumnModel().getColumn(0).setCellRenderer(blueHighlight);
        //set number format of confirmed, deaths and recovered columns to have comma per thousand.
        for(int i=1;i<=3;i++)table.getColumnModel().getColumn(i).setCellRenderer(commaNumberFormat);
        //for converting country name to hyperlink
        table.addMouseListener(new TableMouseListener());
    }

    private void createSearchTextButtonAndLabel() {
        searchText = new JTextField("Find country...");

        ActionListener al = e -> {
            String keyword = searchText.getText();
            //update table with 'keyword'
            ((AllCountriesTableModel)table.getModel()).updateTableForSearch(keyword);
            /*Seems like need to reapply cell renderers whenever search updates.
            * Attempted to fire updates and repaint but no results.*/
            table.getColumnModel().getColumn(0).setCellRenderer(blueHighlight);
            for(int i=1;i<=3;i++)table.getColumnModel().getColumn(i).setCellRenderer(commaNumberFormat);
        };

        searchText.addActionListener(al);
        searchText.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchText.selectAll();
            }

            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        searchButton = new JButton("Search");
        searchButton.addActionListener(al);

        searchLabel = new JLabel("Cases as of 26 June 2022.");
    }

    private void fillPanel() {
        //From https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/GridBagLayoutDemoProject/src/layout/GridBagLayoutDemo.java
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        //natural height, maximum width
        c.fill = GridBagConstraints.HORIZONTAL;//always fill horizontally

        c.gridwidth = 3;
        c.weightx = 0.8;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(searchText, c);

        c.gridwidth = 1;
        c.weightx = 0.2;
        c.gridx = 3;
        c.gridy = 0;
        c.insets = new Insets(0,5,0,0);  //left padding
        panel.add(searchButton, c);

        c.gridwidth = 4;
        c.weightx = 1; //request any extra horizontal space
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0,0,0,0);  //reset padding
        panel.add(searchLabel, c);

        c.weightx = 1;
        c.weighty = 1; //request any extra vertical space
        c.gridx = 0;
        c.gridy = 2;
        panel.add(scrollPane, c);
    }
}
