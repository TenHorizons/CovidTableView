package main.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class SingleCountryFrame extends JFrame {
    /*Create as Singleton to allow less messy interface.
    * Done because it takes a while to load,
    * and user may click on link multiple times.*/
    private static SingleCountryFrame INSTANCE;
    private final String country;
    private JPanel panel;
    private JTable table;
    private final DefaultTableCellRenderer commaNumberFormat = new DefaultTableCellRenderer(){
        final DecimalFormat commaPerThousand = new DecimalFormat("#,###");
        @Override
        protected void setValue(Object value) {
            super.setValue(commaPerThousand.format(Integer.parseInt(value.toString())));
        }
    };
    private final DefaultTableCellRenderer weekFormat = new DefaultTableCellRenderer(){
        final DateTimeFormatter d_mmm_yyyy = DateTimeFormatter.ofPattern("EEE d MMM yyyy");
        @Override
        protected void setValue(Object value) {
            super.setValue(((LocalDate)value).format(d_mmm_yyyy));
        }
    };
    private final DefaultTableCellRenderer monthFormat = new DefaultTableCellRenderer(){
        final DateTimeFormatter mmmm_yyyy = DateTimeFormatter.ofPattern("MMMM yyyy");
        @Override
        protected void setValue(Object value) {
            LocalDate date = ((YearMonth)value).atDay(1);
            super.setValue(date.format(mmmm_yyyy));
        }
    };
    private JLabel starLabel;

    private SingleCountryFrame(String country){
        this.setMinimumSize(new Dimension(600,500));
        this.country = country;
        this.setTitle("Detail Stats of "+country);
        this.setLocationRelativeTo(null);
        fillPanel();
        this.add(panel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }

    public static SingleCountryFrame getInstance(String country){
        if(INSTANCE!=null && INSTANCE.country.equals(country)) return INSTANCE;
        if(INSTANCE!=null) INSTANCE.dispatchEvent(new WindowEvent(INSTANCE, WindowEvent.WINDOW_CLOSING));
        INSTANCE = new SingleCountryFrame(country);
        return INSTANCE;
    }

    private void fillPanel() {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        panel.add(prepareTimePanel(), c);

        c.weighty = 1;
        c.gridy = 2;
        panel.add(prepareTablePanel(), c);

        c.weighty = 0;
        c.gridy = 1;
        starLabel = new JLabel();
        updateStarLabel();
        panel.add(starLabel, c);

    }

    private JPanel prepareTablePanel() {
        table = new JTable(new SingleCountryTableModel(country));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setCellRenderer(monthFormat);
        table.getColumnModel().getColumn(1).setCellRenderer(commaNumberFormat);
        JPanel toR = new JPanel();
        toR.add(scrollPane);
        return toR;
    }

    private JPanel prepareTimePanel() {
        //add label and checkbox.
        JLabel timeText = new JLabel("Cases by: ");
        JComboBox<String> timeBox = new JComboBox<>(new String[]{"Week", "Month"});
        timeBox.setSelectedIndex(1);
        timeBox.addActionListener(e ->{
            String selectedTimeRange = ((String)
                    ((JComboBox<?>) e.getSource())
                            .getSelectedItem()
            );
            //default to display month.
            ((SingleCountryTableModel)table.getModel())
                    .updateTableForComboBox(
                            Objects.requireNonNullElse(selectedTimeRange, "Month")
                    );
            //refresh cell renderers.
            if(selectedTimeRange.equals("Week")) table.getColumnModel().getColumn(0).setCellRenderer(weekFormat);
            else table.getColumnModel().getColumn(0).setCellRenderer(monthFormat);
            table.getColumnModel().getColumn(1).setCellRenderer(commaNumberFormat);

            updateStarLabel();
        });
        JPanel toR = new JPanel();
        toR.add(timeText);
        toR.add(timeBox);
        return toR;
    }
    private void updateStarLabel(){
        int casesPerStar = ((SingleCountryTableModel)table.getModel()).getCasesPerStar();
        DecimalFormat commaPerThousand = new DecimalFormat("#,###");
        starLabel.setText(commaPerThousand.format(casesPerStar) + " cases per *.");
    }
}
