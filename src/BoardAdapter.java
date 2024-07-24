import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardAdapter extends MouseAdapter {
    public boolean enabled = true;

    @Override
    public void mousePressed(MouseEvent e) {
        if(!enabled) return;

        int mouseButton = e.getButton();

        if(mouseButton == 1 || mouseButton == 3) {
            Game.updateClick(e);
        }
    }
}
