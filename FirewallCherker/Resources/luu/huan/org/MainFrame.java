package luu.huan.org;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.Component;

public class MainFrame {

	private JFrame frame;
	private JTable jTable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		initMenu();
		frame.getContentPane().setLayout(new BorderLayout(10, 10));
		
		JPanel jToolBarPanel = new JPanel();
		frame.getContentPane().add(jToolBarPanel, BorderLayout.NORTH);
		
		JPanel jStatusPanel = new JPanel();
		frame.getContentPane().add(jStatusPanel, BorderLayout.SOUTH);
		jStatusPanel.setLayout(new BorderLayout(10, 10));
		
		JProgressBar progressBar = new JProgressBar();
		jStatusPanel.add(progressBar,BorderLayout.LINE_START);
		
		
		JLabel jLabelISPInfo = new JLabel("10.10.10.2 - FPT");
		jLabelISPInfo.setHorizontalAlignment(SwingConstants.CENTER);
		
		jStatusPanel.add(jLabelISPInfo, BorderLayout.CENTER);
		
		
		JLabel jLabelStatus = new JLabel("Đang kiểm tra kết nối Internet");
		jLabelStatus.setHorizontalAlignment(SwingConstants.LEFT);
		jStatusPanel.add(jLabelStatus, BorderLayout.LINE_END);
		
		
		
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		
		
		
		JTree jtree = new JTree();
		splitPane.setLeftComponent(jtree);
		
		jTable = new JTable();
		splitPane.setRightComponent(jTable);
	}

	private void initMenu() {
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenu mnSource = new JMenu("Source");
		menuBar.add(mnSource);
		
		JMenu mnSearch = new JMenu("Search");
		menuBar.add(mnSearch);
		
		JMenu mnRun = new JMenu("Run");
		menuBar.add(mnRun);
		
		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
	}

}
