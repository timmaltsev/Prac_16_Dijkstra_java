package ui;

import javax.swing.*;
import java.awt.*;

public class AlgorithmDialog extends JDialog {

    private AlgorithmMode selectedMode = null;

    public AlgorithmDialog(Frame owner) {

        super(owner, "Запуск алгоритма", true);

        setLayout(new BorderLayout(10, 10));


        JRadioButton instantButton =
                new JRadioButton("Мгновенный", true);

        JRadioButton stepButton =
                new JRadioButton("Пошаговый");

        ButtonGroup group = new ButtonGroup();
        group.add(instantButton);
        group.add(stepButton);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));

        centerPanel.setBorder(
                BorderFactory.createTitledBorder("Режим работы"));

        centerPanel.add(instantButton);
        centerPanel.add(stepButton);

        add(centerPanel, BorderLayout.CENTER);


        JButton okButton = new JButton("Запустить");
        JButton cancelButton = new JButton("Отмена");

        JPanel bottomPanel = new JPanel();

        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);

        add(bottomPanel, BorderLayout.SOUTH);


        okButton.addActionListener(e -> {

            if (instantButton.isSelected()) {
                selectedMode = AlgorithmMode.INSTANT;
            }
            else {
                selectedMode = AlgorithmMode.STEP_BY_STEP;
            }

            dispose();
        });

        cancelButton.addActionListener(e -> {

            selectedMode = null;

            dispose();
        });

        pack();

        setLocationRelativeTo(owner);

    }

    public AlgorithmMode getSelectedMode() {
        return selectedMode;
    }

}
