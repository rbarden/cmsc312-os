import javax.swing.JFrame;

public class GUIFrame extends JFrame{
	
	public static void main(String[] args){
		GUIFrame panel = new GUIFrame();
		
	}
	
	public GUIFrame(){
		add(new GUIPanel());
		setSize(800,520);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
	}
}
