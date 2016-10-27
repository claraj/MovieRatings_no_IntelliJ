package com.clara;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;


public class MovieForm extends JFrame implements WindowListener{

    private JPanel rootPanel;

    private JTable movieDataTable;
    private JScrollPane tableScrollPane;


    private JLabel titleLabel, yearLabel, ratingLabel;
    private JTextField titleTextField;
    private JTextField yearTextField;
    private JSpinner ratingSpinner;

    private JButton addNewMovieButton;
    private JButton quitButton;
    private JButton deleteMovieButton;

    private MovieDataModel movieDataTableModel;

    MovieForm(final MovieDataModel model) {

        this.movieDataTableModel = model;

        createAndAddComponents();

        //Set up JTable
        movieDataTable.setGridColor(Color.BLACK);
        movieDataTable.setModel(movieDataTableModel);

        //Set up the rating spinner.
        //SpinnerNumberModel constructor arguments: spinner's initial value, min, max, step.
        ratingSpinner.setModel(new SpinnerNumberModel(1, MovieDatabase.MOVIE_MIN_RATING, MovieDatabase.MOVIE_MAX_RATING, 1));

        addListeners();

        setContentPane(rootPanel);
        pack();
        setTitle("Movie Database Application");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(this);
        setVisible(true);


    }

    private void createAndAddComponents() {

        rootPanel = new JPanel(new BorderLayout());

        movieDataTable = new JTable();
        tableScrollPane = new JScrollPane(movieDataTable);

        titleLabel = new JLabel("Title");
        yearLabel = new JLabel("Year");
        ratingLabel = new JLabel("Rating");

        titleTextField = new JTextField();
        yearTextField = new JTextField();
        ratingSpinner = new JSpinner();

        addNewMovieButton = new JButton("Add New Movie");
        deleteMovieButton = new JButton("Delete Movie");
        quitButton = new JButton("Quit");

        //Create a panel to organize the layout of the input fields and their labels in a 3 row, 2 col grid
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(titleLabel);
        inputPanel.add(titleTextField);
        inputPanel.add(yearLabel);
        inputPanel.add(yearTextField);
        inputPanel.add(ratingLabel);
        inputPanel.add(ratingSpinner);

        //Create a panel to organize the layout of the three buttons in a 3 row, 1 col grid
        JPanel buttonPanel = new JPanel(new GridLayout(3, 0));
        buttonPanel.add(addNewMovieButton);
        buttonPanel.add(deleteMovieButton);
        buttonPanel.add(quitButton);

        //Add these two JPanels and the JTable (in the scroll pane) to the root panel

        rootPanel.add(inputPanel, BorderLayout.NORTH);
        rootPanel.add(tableScrollPane, BorderLayout.CENTER);
        rootPanel.add(buttonPanel, BorderLayout.SOUTH);
    }


    private void addListeners() {

        //Event handlers for add, delete and quit buttons
        addNewMovieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Get Movie title, make sure it's not blank
                String titleData = titleTextField.getText();

                if (titleData == null || titleData.trim().equals("")) {
                    JOptionPane.showMessageDialog(rootPane, "Please enter a title for the new movie");
                    return;
                }

                //Get movie year. Check it's a number between 1900 and present year
                int yearData;

                try {
                    yearData = Integer.parseInt(yearTextField.getText());
                    if (yearData < 1900 || yearData > Calendar.getInstance().get(Calendar.YEAR)){
                        //Calendar.getInstance() returns a Calendar object representing right now.
                        //calenderObject.get(Calendar.MONTH) gets current month, calenderObject.get(Calendar.SECOND) gets current second
                        //Can get and set other time/date fields- check Java documentation for others
                        //http://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html
                        throw new NumberFormatException("Year needs to be between 1900 and present year");
                    }
                } catch (NumberFormatException ne) {
                    JOptionPane.showMessageDialog(rootPane,
                            "Year needs to be a number between 1900 and now");
                    return;
                }

                //Using a spinner means we are guaranteed to get a number in the range we set, so no validation needed.
                int ratingData = (Integer)(ratingSpinner.getValue());

                System.out.println("Adding " + titleData + " " + yearData + " " + ratingData);
                boolean insertedRow = movieDataTableModel.insertRow(titleData, yearData, ratingData);

                if (!insertedRow) {
                    JOptionPane.showMessageDialog(rootPane, "Error adding new movie");
                }
                // If insertedRow is true and the data was added, it should show up in the table, so no need for confirmation message.
            }

        });


        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MovieDatabase.shutdown();
                System.exit(0);   //Should probably be a call back to Main class so all the System.exit(0) calls are in one place.
            }
        });

        deleteMovieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentRow = movieDataTable.getSelectedRow();

                if (currentRow == -1) {      // -1 means no row is selected. Display error message.
                    JOptionPane.showMessageDialog(rootPane, "Please choose a movie to delete");
                }
                boolean deleted = movieDataTableModel.deleteRow(currentRow);
                if (deleted) {
                    MovieDatabase.loadAllMovies();
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Error deleting movie");
                }
            }
        });
    }

    //windowListener methods. Only need one of them, but are required to implement the others anyway
    //WindowClosing will call DB shutdown code, which is important, so the DB is in a consistent state however the application is closed.

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("closing");
        MovieDatabase.shutdown();
    }

    @Override
    public void windowClosed(WindowEvent e) {}
    @Override
    public void windowOpened(WindowEvent e) {}
    @Override
    public void windowIconified(WindowEvent e) {}
    @Override
    public void windowDeiconified(WindowEvent e) {}
    @Override
    public void windowActivated(WindowEvent e) {}
    @Override
    public void windowDeactivated(WindowEvent e) {}
}
