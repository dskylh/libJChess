import javax.swing.*;

public class libChess {

    public static void main(String[] args) {

        UI ui = new UI();

        JFrame frame = new JFrame("JChess");
        frame.add(ui.getGui());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);


    }

}
