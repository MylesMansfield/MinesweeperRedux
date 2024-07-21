import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardAdapter extends MouseAdapter {
    @Override
    public void mousePressed(MouseEvent e) {
        int mouseButton = e.getButton();

        if(mouseButton == 1 || mouseButton == 3) {
            Game.updateClick(e);
        }
    }
}
