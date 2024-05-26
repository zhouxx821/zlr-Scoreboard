package Table;

import javax.swing.*;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Multiple Tables Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(1, 2)); // Arrange tables side by side

        // Create data for the first table
        Object[][] data1 = {
                {"Alice", 25, "Female"},
                {"Bob", 30, "Male"},
                {"Charlie", 22, "Male"}
        };
        String[] columnNames1 = {"Name", "Age", "Gender"};
        JTable table1 = new JTable(data1, columnNames1);

        // Create data for the second table
        Object[][] data2 = {
                {"David", 28, "Male"},
                {"Eve", 24, "Female"},
                {"Frank", 35, "Male"}
        };
        String[] columnNames2 = {"Name", "Age", "Gender"};
        JTable table2 = new JTable(data2, columnNames2);

        // Add tables to the frame
        frame.add(new JScrollPane(table1));
        frame.add(new JScrollPane(table2));

        frame.pack();
        frame.setVisible(true);
    }
}
