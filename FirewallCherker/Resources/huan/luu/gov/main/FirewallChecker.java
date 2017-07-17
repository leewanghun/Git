package huan.luu.gov.main;
import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import huan.luu.gov.common.Constants;
import huan.luu.gov.common.WebLink;
import huan.luu.gov.gui.CellChangeColorRenderer;
import huan.luu.gov.gui.ResultDialog;
import huan.luu.gov.gui.support.URLTableModel;

public class FirewallChecker extends JPanel {
	private static JTable table;
	JButton btnStart;
	JButton btnStop;
	JButton btnViewResult;
	
	JButton btnOpenFile = new JButton("...");
	JTextField jtxtFieldFilePath = new JTextField();
	private List<String> hostList = new ArrayList();
	private URLTableModel tableModel;
	private ArrayList<WebLink> weblinkData;
	private ResultDialog resultDialog;
	private FirewallChecker.CheckFirewallSwingworker swingWorker;
	JLabel textStatus = new JLabel("");
	JProgressBar progessBar = new JProgressBar(0, 100);
	public static int COUNT = 0;
	private static FirewallChecker mainInstance;
	private static JFrame frame;
	private static JMenuItem closeItemMenu;
	private ExecutorService threadPool;
	public boolean isTerminator = false;

	public FirewallChecker() {
		setLayout(new BorderLayout());
		InitButton();
		InitTable();
		InitStatusBar();
	}

	private void InitButton() {
		Icon startIcon = new ImageIcon(getClass().getResource("playicon.png"));
//		ImageIcon startIcon = new ImageIcon("/icons/playicon.png");
		this.btnStart = new JButton();
		this.btnStart.setToolTipText("Nhấn vào đây để quét");
		this.btnStart.setIcon(startIcon);
		this.btnStart.setPreferredSize(new Dimension(40, 30));
		this.btnStart.setEnabled(false);

		this.btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FirewallChecker.this.btnOpenFile.setEnabled(false);
				FirewallChecker.this.isTerminator = false;
				FirewallChecker.this.loadValueToTable();
				FirewallChecker.this.btnViewResult.setVisible(false);
				FirewallChecker.this.resultDialog = null;
				FirewallChecker.this.btnStart.setEnabled(false);
				FirewallChecker.this.btnStop.setEnabled(true);
				FirewallChecker.this.swingWorker = new CheckFirewallSwingworker();
				FirewallChecker.this.swingWorker.execute();
			}
		});
		Icon stopIcon = new ImageIcon(getClass().getResource("stopicon.png"));
//		Icon stopIcon = new ImageIcon("/icons/stopicon.png");
		this.btnStop = new JButton();
		this.btnStop.setIcon(stopIcon);
		this.btnStop.setPreferredSize(new Dimension(40, 30));
		this.btnStop.setEnabled(false);
		this.btnStop.setToolTipText("Nhấn vào đây để dừng quét");

		this.btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FirewallChecker.this.isTerminator = true;
				if (FirewallChecker.this.swingWorker != null) {
					FirewallChecker.this.swingWorker.cancel(true);
				}
				if (FirewallChecker.this.threadPool != null) {
					FirewallChecker.this.threadPool.shutdownNow();
				}
				FirewallChecker.this.btnOpenFile.setEnabled(true);
				FirewallChecker.this.btnStop.setEnabled(false);
				FirewallChecker.this.btnStart.setEnabled(true);
				FirewallChecker.this.btnViewResult.setVisible(false);
			}
		});
		this.btnOpenFile.setPreferredSize(new Dimension(40, 30));
		this.btnOpenFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FirewallChecker.this.readfile();
			}
		});
		Icon viewResultIcon = new ImageIcon(getClass().getResource("viewresult.png"));
//		Icon viewResultIcon = new ImageIcon("/icons/viewresult.png");
		this.btnViewResult = new JButton();
		this.btnViewResult.setIcon(viewResultIcon);
		this.btnViewResult.setPreferredSize(new Dimension(40, 30));
		this.btnViewResult.setVisible(false);
		this.btnViewResult.setToolTipText("Nhấn vào đây để xem kết quả trước");

		this.btnViewResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resultDialog.setVisible(true);
			}
		});
		
		// init info panel
		
		
		
		
		add(initControlPanel(), "North");
	}

	private JPanel initControlPanel() {
		//add info panel
		JLabel infomation = new JLabel("IP: 1.2.3.4 (FPT)");
				
		
		
		//add button panel
		JPanel jPanelButton = new JPanel();
		jPanelButton.setLayout(new FlowLayout());
		jPanelButton.setBorder(new EmptyBorder(0, 0, 0, 500));
		jPanelButton.setAlignmentX(0.0F);

		this.jtxtFieldFilePath.setPreferredSize(new Dimension(300, 26));
		this.jtxtFieldFilePath.setEditable(false);

		jPanelButton.add(this.jtxtFieldFilePath);
		jPanelButton.add(this.btnOpenFile);
		jPanelButton.add(this.btnStart);
		jPanelButton.add(this.btnStop);
		jPanelButton.add(this.btnViewResult);
		jPanelButton.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		JPanel panel1 = new JPanel();
		panel1.setLayout(new FlowLayout());
		panel1.setAlignmentY(0.0F);
		panel1.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Khung điều khiển", 1, 2));
		panel1.add(infomation);
		panel1.add(jPanelButton);
		return panel1;
	}

	private void InitTable() {
		this.tableModel = new URLTableModel(Constants.PRE_DATA, Constants.TABLE_HEADER);

		table = new JTable(this.tableModel);

		CellChangeColorRenderer colorRenderer = new CellChangeColorRenderer();
		table.setDefaultRenderer(Object.class, colorRenderer);

		formatTable();

		table.setCellSelectionEnabled(false);
		table.setRowSelectionAllowed(true);

		table.setAutoCreateRowSorter(true);
		JScrollPane scrollpane = new JScrollPane(table);

		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Khung kết quả", 1, 2));
		panel.add(scrollpane);

		scrollpane
				.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Khung kết quả", 1, 2));

		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evnt) {
				if (evnt.getClickCount() == 2) {
					try {
						Desktop.getDesktop().browse(new URI(FirewallChecker.table
								.getValueAt(FirewallChecker.table.getSelectedRow(), 1).toString().trim()));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		});
		add(scrollpane, "Center");
	}

	private void InitStatusBar() {
		JPanel progressPanel = new JPanel();
		progressPanel.setAlignmentX(0.0F);
		progressPanel.setLayout(new FlowLayout());
		progressPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		progressPanel.setBorder(new EmptyBorder(0, 0, 0, 800));
		progressPanel.add(this.progessBar);

		add(progressPanel, "South");
	}

	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					createAndShowGUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void readfile() {
		JFileChooser fileOpen = new JFileChooser();
		fileOpen.setFileFilter(new FileNameExtensionFilter(".txt", new String[] { "txt", "text" }));
		int returnVal = fileOpen.showSaveDialog(this);
		if (returnVal == 0) {
			try {
				Path path = fileOpen.getSelectedFile().toPath();
				this.hostList = Files.readAllLines(path, Charset.defaultCharset());
				this.jtxtFieldFilePath.setText(path.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.btnStart.setEnabled(true);

			loadValueToTable();
			closeItemMenu.setEnabled(true);
			this.btnViewResult.setVisible(false);
		}
	}

	private void loadValueToTable() {
		this.weblinkData = new ArrayList();
		Object[][] array = new Object[this.hostList.size()][5];
		for (int i = 0; i < this.hostList.size(); i++) {
			WebLink webLink = new WebLink((String) this.hostList.get(i), i + 1);
			this.weblinkData.add(webLink);
			array[i][0] = Integer.valueOf(i + 1);
			array[i][1] = webLink.link;
			array[i][2] = null;
			array[i][3] = "";
			array[i][4] = "";
		}
		this.tableModel.setDataVector(array, Constants.TABLE_HEADER);
		formatTable();
	}

	private void formatTable() {
		table.setRowHeight(20);
		table.getColumnModel().getColumn(0).setMaxWidth(40);
		table.getColumnModel().getColumn(1).setMaxWidth(300);
		table.getColumnModel().getColumn(1).setMinWidth(200);
		table.getColumnModel().getColumn(2).setMaxWidth(50);
		table.getColumnModel().getColumn(3).setMaxWidth(60);
		table.getColumnModel().getColumn(4).setMaxWidth(500);
		table.setAutoResizeMode(3);
	}

	public static void createAndShowGUI() throws Exception {
		frame = new JFrame("Chương trình kiểm tra firewall");
		frame.setDefaultCloseOperation(3);
		mainInstance = new FirewallChecker();
		frame.add(mainInstance);
		frame.setLocationByPlatform(true);
		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		JMenu fileMenu = new JMenu("Tệp");
		JMenuItem openItem = new JMenuItem("Mở");
		fileMenu.add(openItem);
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FirewallChecker.mainInstance.readfile();
			}
		});
		closeItemMenu = new JMenuItem("Đóng");

		closeItemMenu.setEnabled(false);
		closeItemMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FirewallChecker.mainInstance.tableModel.setDataVector(Constants.PRE_DATA, Constants.TABLE_HEADER);
				FirewallChecker.mainInstance.formatTable();
				FirewallChecker.mainInstance.jtxtFieldFilePath.setText("");
				FirewallChecker.closeItemMenu.setEnabled(false);
				FirewallChecker.mainInstance.btnStart.setEnabled(false);
				FirewallChecker.mainInstance.btnViewResult.setVisible(false);
			}
		});
		fileMenu.add(closeItemMenu);
		fileMenu.addSeparator();
		JMenuItem exitItem = new JMenuItem("Thoát");
		fileMenu.add(exitItem);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JMenuBar myMenuBar = new JMenuBar();
		myMenuBar.add(fileMenu);
		JMenuItem helpMenu = new JMenuItem("Hướng dẫn");
		myMenuBar.add(helpMenu);
		frame.setJMenuBar(myMenuBar);
	}

	class CheckFirewallSwingworker extends SwingWorker<List<String>, String> {
		List<String> byPassList = new LinkedList();

		CheckFirewallSwingworker() {
		}

		protected List<String> doInBackground() throws Exception {
			FirewallChecker.this.threadPool = Executors.newFixedThreadPool(10);

			FirewallChecker.this.textStatus.setText("Trạng thái: đang kết nối...");
			int progress = 0;
			setProgress(0);
			for (int i = 0; i < FirewallChecker.this.hostList.size(); i++) {
				((WebLink) FirewallChecker.this.weblinkData.get(i)).setTableModel(FirewallChecker.this.tableModel);
				FirewallChecker.this.threadPool.execute((Runnable) FirewallChecker.this.weblinkData.get(i));
			}
			FirewallChecker.this.threadPool.shutdown();
			FirewallChecker.this.progessBar.setIndeterminate(true);
			FirewallChecker.this.progessBar.setVisible(true);
			FirewallChecker.this.progessBar.setEnabled(true);
			while (!FirewallChecker.this.threadPool.isTerminated()) {
				FirewallChecker.this.textStatus.setText("Đang kiểm tra...");

				progress++;
				if (progress >= 100) {
					progress = 0;
				}
				setProgress(progress);
				FirewallChecker.this.textStatus
						.setText("Đã kiểm tra " + FirewallChecker.COUNT + "/" + FirewallChecker.this.hostList.size());
			}
			return this.byPassList;
		}

		protected void done() {
			setProgress(100);
			FirewallChecker.this.btnStart.setEnabled(true);
			FirewallChecker.this.btnStop.setEnabled(false);
			FirewallChecker.this.progessBar.setValue(100);
			FirewallChecker.this.progessBar.setVisible(false);
			FirewallChecker.this.progessBar.setIndeterminate(false);
			FirewallChecker.this.progessBar.setEnabled(false);
			FirewallChecker.COUNT = 0;
			FirewallChecker.this.textStatus.setText("");
			FirewallChecker.this.btnOpenFile.setEnabled(true);
			if (!FirewallChecker.this.isTerminator) {
				List<String> bypasslist = new ArrayList();
				for (int i = 0; i < FirewallChecker.this.tableModel.getRowCount(); i++) {
					if (FirewallChecker.this.tableModel.getValueAt(i, 2).toString().equalsIgnoreCase("ok")) {
						bypasslist.add(new String(FirewallChecker.this.tableModel.getValueAt(i, 1).toString()));
					}
				}
				resultDialog = new ResultDialog(FirewallChecker.frame, FirewallChecker.this.tableModel.getRowCount(),
						bypasslist);
				resultDialog.setLocationRelativeTo(FirewallChecker.frame);

				resultDialog.setVisible(true);
				btnViewResult.setVisible(true);
			}
		}
	}
}
