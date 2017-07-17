package huan.luu.gov.gui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ResultDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private String strResult;
	private JCheckBox checkbox;
	private List<String> originalList;
	private List<String> editList;
	private JTextPane textPaneResult;
	private ResultDialog instance;

	public ResultDialog(JFrame frame, int total, List<String> list) {
		super(frame);
		this.instance = this;
		this.originalList = list;
		setTitle("Kết quả kiểm tra firewall");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, 1));
		add(mainPanel);

		float percent = this.originalList.size() / total * 100;
		System.out.println(percent);

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setMaximumSize(new Dimension(450, 30));
		JLabel hint1 = new JLabel("Đã kiểm tra tổng số: " + total + " trang");
		JLabel hint2 = new JLabel("Số trang lọt: " + list.size() + " trang");

		hint1.setBorder(BorderFactory.createEmptyBorder(10, 25, 5, 10));
		hint2.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 10));

		topPanel.add(hint1, "North");
		topPanel.add(hint2, "Center");

		mainPanel.add(topPanel);

		JPanel textPanel = new JPanel(new BorderLayout());
		textPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
		this.textPaneResult = new JTextPane();

		this.editList = new ArrayList();
		for (int i = 0; i < this.originalList.size(); i++) {
			String value = (String) this.originalList.get(i);
			if ((!value.contains("facebook")) && (!value.contains("twitter")) && (!value.contains("plus.google.com"))) {
				this.editList.add(new String(value));
			}
		}
		this.textPaneResult.setEditable(false);
		textPanel.add(new JScrollPane(this.textPaneResult));

		mainPanel.add(textPanel);

		JPanel boxPanel = new JPanel(new FlowLayout(0, 20, 0));

		this.checkbox = new JCheckBox("Loại bỏ Facebook/Twitter/Google+");
		this.checkbox.setMnemonic(83);
		this.checkbox.setSelected(false);
		this.checkbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!ResultDialog.this.checkbox.isSelected()) {
					if ((ResultDialog.this.originalList != null) && (!ResultDialog.this.originalList.isEmpty())) {
						ResultDialog.this.strResult = ResultDialog.this.originalList.toString().replace("[", "")
								.replace("]", "").replaceAll("\\, ", "\n");
						ResultDialog.this.textPaneResult
								.setText(ResultDialog.this.strResult == null ? "" : ResultDialog.this.strResult);
					}
				} else if ((ResultDialog.this.editList != null) && (!ResultDialog.this.editList.isEmpty())) {
					ResultDialog.this.strResult = ResultDialog.this.editList.toString().replace("[", "")
							.replace("]", "").replaceAll("\\, ", "\n");
					ResultDialog.this.textPaneResult
							.setText(ResultDialog.this.strResult == null ? "" : ResultDialog.this.strResult);
				}
			}
		});
		boxPanel.add(this.checkbox);
		mainPanel.add(boxPanel);

		JPanel bottom = new JPanel(new FlowLayout(2));

		JButton copyResult = new JButton("Copy kết quả");
		copyResult.setMnemonic(78);

		copyResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResultDialog.this.copyResult();
			}
		});
		JButton close = new JButton("Close");
		close.setMnemonic(67);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ResultDialog.this.instance.dispose();
			}
		});
		bottom.add(copyResult);
		bottom.add(close);
		mainPanel.add(bottom);

		bottom.setMaximumSize(new Dimension(450, 0));

		setSize(new Dimension(450, 350));
		setResizable(false);
		setDefaultCloseOperation(2);

		showResult();
	}

	private void showResult() {
		if (!this.checkbox.isSelected()) {
			if ((this.originalList != null) && (!this.originalList.isEmpty())) {
				this.strResult = this.originalList.toString().replace("[", "").replace("]", "").replaceAll("\\, ",
						"\n");
				this.textPaneResult.setText(this.strResult == null ? "" : this.strResult);
			}
		} else if ((this.editList != null) && (!this.editList.isEmpty())) {
			this.strResult = this.editList.toString().replace("[", "").replace("]", "").replaceAll("\\, ", "\n");
			this.textPaneResult.setText(this.strResult == null ? "" : this.strResult);
		}
	}

	private void copyResult() {
		if ((this.strResult != null) && (!this.strResult.isEmpty())) {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			clipboard.setContents(new StringSelection(this.strResult), null);
			JOptionPane.showMessageDialog(this, "Sẵn sàng để paste qua word/excel");
		} else {
			JOptionPane.showMessageDialog(this, "Không có gì để copy!!!");
		}
	}
}
