// Ma_Sys.ma ScanImgRename 1.2, Copyright (c) 2019, 2020, 2021 Ma_Sys.ma.
// For further info send an e-mail to Ma_Sys.ma@web.de.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

public class ScanImgRename extends JFrame {

	private static class RenameOperation {

		final Path originalName;
		final Path newName;

		RenameOperation(Path originalName, Path newName) {
			this.originalName = originalName;
			this.newName      = newName;
		}

		boolean isPossible() {
			return !Files.exists(newName);
		}

		void execute() throws IOException {
			Files.move(originalName, newName);
		}

		@Override
		public String toString() {
			return originalName.toString() + " to " +
							newName.toString();
		}

	}

	private int idx;
	private String[] files;
	private String proposedForRename;

	private final LinkedList<RenameOperation> delayedRenames;

	private final JLabel top;
	private final JTextField curnam = new JTextField(30);

	public ScanImgRename(String[] files) {
		super("ScanImgRename");

		this.files = files;
		idx = -1;
		delayedRenames = new LinkedList<RenameOperation>();

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());
		JPanel bot = new JPanel(new FlowLayout(FlowLayout.CENTER));
		curnam.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ev) {
				if(ev.getKeyCode() == KeyEvent.VK_ENTER)
					handleEnter();
				else
					top.repaint();
			}
		});
		bot.add(curnam);
		cp.add("South", bot);
		top = new JLabel("...") {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.setColor(delayedRenames.isEmpty()?
						Color.BLUE: Color.RED);
				g.setFont(new Font(Font.MONOSPACED, Font.BOLD,
									36));
				g.drawString(curnam.getText(), 100, 100);
			}
		};
		cp.add("Center", new JScrollPane(top));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		nextImage();
	}

	private void handleEnter() {
		try {
			String newNameStr = String.format("madoc%06d.png",
					Integer.parseInt(curnam.getText()));
			RenameOperation rn = new RenameOperation(
					Paths.get(proposedForRename),
					Paths.get(newNameStr));
			if(rn.isPossible()) {
				rn.execute();
				executePendingDelayedRenames();
				curnam.setText("");
				nextImage();
			} else {
				delayedRenames.addFirst(rn);
				curnam.setText("");
				proposeForRename(newNameStr);
			}
		} catch(NumberFormatException|IOException ex) {
			ex.printStackTrace();
		}
	}

	private void executePendingDelayedRenames() throws IOException {
		Iterator<RenameOperation> iter = delayedRenames.iterator();
		while(iter.hasNext()) {
			RenameOperation rnP = iter.next();
			if(rnP.isPossible()) {
				rnP.execute();
				iter.remove();
			}
		}
	}

	private void nextImage() {
		idx++;
		if(idx == files.length) {
			for(RenameOperation rn: delayedRenames)
				System.out.println("WARNING: Unable to rename "
							+ rn.toString());
			// fin
			setVisible(false);
			System.exit(0);
		} else {
			proposeForRename(files[idx]);
		}
	}

	private void proposeForRename(String fileName) {
		ImageIcon ic = new ImageIcon(fileName);
		top.setIcon(ic);
		proposedForRename = fileName;
	}

	public static void main(String[] args) {
		new ScanImgRename(args);
	}

}
