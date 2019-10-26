import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ScanImgRename extends JFrame {

	private int idx;
	private String[] files;

	private final JLabel top;

	public ScanImgRename(String[] files) {
		super("ScanImgRename");

		this.files = files;
		idx = -1;

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JTextField curnam = new JTextField(30);
		curnam.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ev) {
				if(ev.getKeyCode() == KeyEvent.VK_ENTER &&
						performRename(
						curnam.getText())) {
					curnam.setText("");
					nextImage();
				}
			}
		});
		bot.add(curnam);
		cp.add("South", bot);
		top = new JLabel("...");
		cp.add("Center", new JScrollPane(top));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		nextImage();
	}

	private boolean performRename(String newName) {
		try {
			Path newFile = Paths.get(String.format("madoc%06d.png",
						Integer.parseInt(newName)));
			if(Files.exists(newFile))
				return false;
			else
				Files.move(Paths.get(files[idx]), newFile);
			return true;
		} catch(NumberFormatException|IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private void nextImage() {
		idx++;
		if(idx == files.length) {
			// fin
			setVisible(false);
			System.exit(0);
		} else {
			ImageIcon ic = new ImageIcon(files[idx]);
			top.setIcon(ic);
		}
	}

	public static void main(String[] args) {
		new ScanImgRename(args);
	}

}
