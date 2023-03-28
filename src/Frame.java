import javax.swing.*;

public class Frame extends JFrame{
    public Frame(String title)
    {
        //creating the frame and setting the title
        super(title);

        //setting the size
        setSize(700,430);

        //setting what happens when the window closes
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //puts the window into the middle of the screen
        setLocationRelativeTo(null);

        //stops from changing frame window
        setResizable(false);

        //allows text boxes and text areas to be moved
        setLayout(null);
    }
}