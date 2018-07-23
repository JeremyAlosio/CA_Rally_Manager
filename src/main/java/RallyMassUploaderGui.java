import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rallydev.rest.RallyRestApi;
import com.rallydev.rest.request.CreateRequest;
import com.rallydev.rest.request.GetRequest;
import com.rallydev.rest.request.QueryRequest;
import com.rallydev.rest.response.GetResponse;
import com.rallydev.rest.response.QueryResponse;
import com.rallydev.rest.util.Fetch;
import com.rallydev.rest.util.QueryFilter;

public class RallyMassUploaderGui extends JFrame {

	private static final long serialVersionUID = 3614876536079282828L;

	public static String API_KEY = "";
	public static RallyRestApi restApi = null;
	public static File file;

	private JPanel contentPane;
	private static JTextField userstoryTextBox;
	private static JTextField nameTextBox;
	private static JTextPane descriptionTextBox;
	private static JTextPane preconditionsTextBox;
	private static JTextPane scenarioTextBox;
	static JTextPane systemOutputTextBox;
	private static JButton uploadButton;
	private static JButton undoButton;
	private static JCheckBox mobileTabletCheckBox;
	private static JCheckBox mobilePhoneCheckBox;
	private static JCheckBox spanishCheckBox;
	private static JButton nextButton;
	private JLabel lblCurrentScenarioTc;
	private static JButton btnResetScenarioTc;
	private static JTextField scenarionumberTextBox;
	private JLabel lblScenario_1;
	private JLabel lblTc;
	private static JTextField testcasenumberTextBox;
	private static JButton setscenariotcButton;
	private JSeparator separator_3;
	private JSeparator separator_5;
	private JSeparator separator_4;
	private static JButton btnPrevious;
	private static JButton btnFirst;
	private static JButton btnLast;
	private static JButton addScenarioButton;
	private static JButton removeScenarioButton;
	public static TextArea statusTextArea;
	private JLabel lblScenarios;
	private JSeparator separator_6;
	private JLabel lblRally;
	private JSeparator separator_7;
	private static JButton btnSet;
	private static JButton setUSTestCasesButton;
	private static JScrollPane userstoryScrollPane;
	private static List<TestResult> TestResultHistory = new ArrayList<TestResult>();
	private static JList<String> historyList;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Error setting native LAF: " + e);
		}

		file = new File(System.getenv("AppData") + "/RallyTool/apikey.txt");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fw = null;
			BufferedWriter bw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				out = new PrintWriter(bw);
				out.println("_uGcA3XfeT5WZ2ESHiZMm62zvek3BUIMhxscRZbnJVw");
				out.close();
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
		}

		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) {
				API_KEY = sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		file = new File(System.getenv("AppData") + "/RallyTool/NotesHistory.txt");
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fw = null;
			BufferedWriter bw = null;
			PrintWriter out = null;
			try {
				fw = new FileWriter(file);
				bw = new BufferedWriter(fw);
				out = new PrintWriter(bw);
				out.close();
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
		}

		String seralizedObject = "";
		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file));
			while ((sCurrentLine = br.readLine()) != null) {
				seralizedObject += sCurrentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		TestResultHistory = LoadTestResultsHistory(seralizedObject);

		if (TestResultHistory == null) {
			TestResultHistory = new ArrayList<TestResult>();
		}

		try {
			restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), API_KEY);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RallyMassUploaderGui frame = new RallyMassUploaderGui();
					frame.setVisible(true);
					DynamiclyManageButtons();
					addScenarioButton.setEnabled(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public RallyMassUploaderGui() {

		setResizable(false);
		setTitle("Rally Manager");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1011, 506);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1008, 478);
		contentPane.add(tabbedPane);

		JPanel uploaderTab = new JPanel();
		tabbedPane.addTab("Uploader", null, uploaderTab, null);
		uploaderTab.setLayout(null);

		uploadButton = new JButton("Upload");
		uploadButton.setEnabled(false);
		uploadButton.setBounds(710, 389, 89, 23);
		uploaderTab.add(uploadButton);

		userstoryTextBox = new JTextField();
		userstoryTextBox.setBounds(75, 9, 97, 20);
		uploaderTab.add(userstoryTextBox);
		userstoryTextBox.setColumns(10);

		JLabel lblTypeOfTest = new JLabel("Type of Test (Generated at Add):");
		lblTypeOfTest.setBounds(32, 74, 198, 14);
		uploaderTab.add(lblTypeOfTest);

		JLabel userstoryLabel = new JLabel("Userstory:");
		userstoryLabel.setBounds(10, 12, 154, 14);
		uploaderTab.add(userstoryLabel);

		undoButton = new JButton("Undo Upload");
		undoButton.setEnabled(false);
		undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (Scenario scenario : scenarios) {
					scenario.DeleteTests();
				}
				nameTextBox.setEnabled(true);
				spanishCheckBox.setEnabled(true);
				mobileTabletCheckBox.setEnabled(true);
				mobilePhoneCheckBox.setEnabled(true);
				descriptionTextBox.setEnabled(true);
				preconditionsTextBox.setEnabled(true);
				scenarioTextBox.setEnabled(true);
				btnResetScenarioTc.setEnabled(true);
				setscenariotcButton.setEnabled(true);
				addScenarioButton.setEnabled(true);
				testcasenumberTextBox.setEnabled(true);
				scenarionumberTextBox.setEnabled(true);
				undoButton.setEnabled(false);
				uploadButton.setEnabled(true);
				DynamiclyManageButtons();
			}

		});
		undoButton.setBounds(809, 389, 116, 23);
		uploaderTab.add(undoButton);

		nameTextBox = new JTextField();
		nameTextBox.setEnabled(false);
		nameTextBox.setBounds(314, 39, 337, 23);
		uploaderTab.add(nameTextBox);
		nameTextBox.setColumns(10);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(314, 21, 46, 14);
		uploaderTab.add(lblName);

		spanishCheckBox = new JCheckBox("Spanish");
		spanishCheckBox.setEnabled(false);
		spanishCheckBox.setBounds(184, 105, 72, 23);
		uploaderTab.add(spanishCheckBox);

		mobileTabletCheckBox = new JCheckBox("Tablet");
		mobileTabletCheckBox.setEnabled(false);
		mobileTabletCheckBox.setBounds(32, 105, 63, 23);
		uploaderTab.add(mobileTabletCheckBox);

		JSeparator separator = new JSeparator();
		separator.setBounds(19, 92, 261, 8);
		uploaderTab.add(separator);

		JLabel lblScenario = new JLabel("Scenario:");
		lblScenario.setBounds(657, 110, 165, 12);
		uploaderTab.add(lblScenario);

		nextButton = new JButton("Next");
		nextButton.setBounds(528, 244, 89, 23);
		uploaderTab.add(nextButton);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (currentScenarioViewed < scenarios.size()) {
					EditScenario();
				}
				if ((currentScenarioViewed != scenarios.size() - 1) && (currentScenarioViewed < scenarios.size())) {
					currentScenarioViewed++;
					NavigateScenarios();
				} else if ((currentScenarioViewed + 1 > scenarios.size() - 1)
						&& (currentScenarioViewed != scenarios.size())) {
					currentScenarioViewed++;
					scenarioTextBox.setText("");
					nameTextBox.setText("");
					mobileTabletCheckBox.setSelected(false);
					mobilePhoneCheckBox.setSelected(false);
					spanishCheckBox.setSelected(false);

				} else {
					scenarioTextBox.setText("");
					nameTextBox.setText("");
				}
				DynamiclyManageButtons();
			}
		});

		JLabel systemOutputLabel = new JLabel("Console Output:");
		systemOutputLabel.setBounds(28, 283, 126, 14);
		uploaderTab.add(systemOutputLabel);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(290, 0, 14, 267);
		uploaderTab.add(separator_2);
		separator_2.setOrientation(SwingConstants.VERTICAL);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(314, 87, 333, 152);
		uploaderTab.add(scrollPane_1);

		descriptionTextBox = new JTextPane();
		scrollPane_1.setViewportView(descriptionTextBox);
		descriptionTextBox.setEnabled(false);

		scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(657, 39, 333, 62);
		uploaderTab.add(scrollPane_3);

		preconditionsTextBox = new JTextPane();
		scrollPane_3.setViewportView(preconditionsTextBox);
		preconditionsTextBox.setEnabled(false);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(657, 123, 333, 116);
		uploaderTab.add(scrollPane_2);

		scenarioTextBox = new JTextPane();
		scrollPane_2.setViewportView(scenarioTextBox);
		scenarioTextBox.setEnabled(false);
		scenarioTextBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if (autoPopulateCheckBox.isSelected()) {
					try {
						if (delimiterTextBox.getText().isEmpty()) {
							nameTextBox.setText(scenarioTextBox.getText().split("\n")[0].replace("\n", ""));
						} else {
							nameTextBox.setText(
									scenarioTextBox.getText().split(delimiterTextBox.getText())[1].split("\n")[0]
											.trim());
						}
					} catch (Exception e) {

					}
				}
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				if (autoPopulateCheckBox.isSelected()) {
					try {
						if (delimiterTextBox.getText().isEmpty()) {
							nameTextBox.setText(scenarioTextBox.getText().split("\n")[0].replace("\n", ""));
						} else {
							nameTextBox.setText(
									scenarioTextBox.getText().split(delimiterTextBox.getText())[1].split("\n")[0]
											.trim());
						}
					} catch (Exception e1) {

					}
				}
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (autoPopulateCheckBox.isSelected()) {
					try {
						if (delimiterTextBox.getText().isEmpty()) {
							nameTextBox.setText(scenarioTextBox.getText().split("\n")[0].replace("\n", ""));
						} else {
							nameTextBox.setText(
									scenarioTextBox.getText().split(delimiterTextBox.getText())[1].split("\n")[0]
											.trim());
						}
					} catch (Exception e2) {

					}
				}
			}
		});

		lblCurrentScenarioTc = new JLabel("Set Custom Start:");
		lblCurrentScenarioTc.setBounds(31, 148, 209, 14);
		uploaderTab.add(lblCurrentScenarioTc);

		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setBounds(314, 55, 79, 39);
		uploaderTab.add(lblDescription);

		btnResetScenarioTc = new JButton("Reset");
		btnResetScenarioTc.setEnabled(false);
		btnResetScenarioTc.setBounds(141, 201, 89, 23);
		uploaderTab.add(btnResetScenarioTc);
		btnResetScenarioTc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenarioNumber = "1";
				testcaseNumber = "1";
				systemOutputTextBox
						.setText(systemOutputTextBox.getText() + "Scenario and Test Case Number Changed to \"1\"\n");
			}
		});

		lblTc = new JLabel("Test Case #:");
		lblTc.setBounds(23, 178, 72, 14);
		uploaderTab.add(lblTc);

		testcasenumberTextBox = new JTextField();
		testcasenumberTextBox.setEnabled(false);
		testcasenumberTextBox.setText("1");
		testcasenumberTextBox.setHorizontalAlignment(JTextField.CENTER);
		testcasenumberTextBox.setBounds(92, 175, 38, 20);
		uploaderTab.add(testcasenumberTextBox);
		testcasenumberTextBox.setColumns(10);

		lblScenario_1 = new JLabel("Scenario #:");
		lblScenario_1.setBounds(141, 178, 63, 14);
		uploaderTab.add(lblScenario_1);

		JLabel lblPreconditions = new JLabel("PreConditions:");
		lblPreconditions.setBounds(657, 17, 148, 31);
		uploaderTab.add(lblPreconditions);

		scenarionumberTextBox = new JTextField();
		scenarionumberTextBox.setEnabled(false);
		scenarionumberTextBox.setText("1");
		scenarionumberTextBox.setHorizontalAlignment(JTextField.CENTER);
		scenarionumberTextBox.setBounds(205, 175, 38, 20);
		uploaderTab.add(scenarionumberTextBox);
		scenarionumberTextBox.setColumns(10);

		setscenariotcButton = new JButton("Set");
		setscenariotcButton.setEnabled(false);
		setscenariotcButton.setBounds(42, 201, 89, 23);
		uploaderTab.add(setscenariotcButton);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 273, 994, 14);
		uploaderTab.add(separator_1);

		separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setBounds(661, 283, 14, 134);
		uploaderTab.add(separator_3);

		separator_5 = new JSeparator();
		separator_5.setBounds(19, 164, 261, 8);
		uploaderTab.add(separator_5);

		separator_4 = new JSeparator();
		separator_4.setBounds(0, 35, 292, 8);
		uploaderTab.add(separator_4);

		btnPrevious = new JButton("Previous");
		btnPrevious.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentScenarioViewed < scenarios.size()) {
					EditScenario();
				}
				if (currentScenarioViewed != 0) {
					currentScenarioViewed--;
					NavigateScenarios();
				}
				DynamiclyManageButtons();
			}
		});
		btnPrevious.setBounds(436, 244, 89, 23);
		uploaderTab.add(btnPrevious);

		JLabel lblHistory = new JLabel("Generation Settings:");
		lblHistory.setBounds(10, 44, 209, 14);
		uploaderTab.add(lblHistory);

		btnFirst = new JButton("First");
		btnFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentScenarioViewed < scenarios.size()) {
					EditScenario();
				}
				if (!scenarios.isEmpty()) {
					currentScenarioViewed = 0;
					NavigateScenarios();
				}
				DynamiclyManageButtons();
			}
		});
		btnFirst.setBounds(314, 244, 89, 23);
		uploaderTab.add(btnFirst);

		btnLast = new JButton("Last");
		btnLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentScenarioViewed < scenarios.size()) {
					EditScenario();
				}
				if (!scenarios.isEmpty()) {
					currentScenarioViewed = scenarios.size() - 1;
					NavigateScenarios();
				}
				DynamiclyManageButtons();
			}
		});
		btnLast.setBounds(646, 244, 89, 23);
		uploaderTab.add(btnLast);

		addScenarioButton = new JButton("Add");
		addScenarioButton.setEnabled(false);
		addScenarioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (nameTextBox.getText().isEmpty() || scenarioTextBox.getText().isEmpty()) {
					systemOutputTextBox.setText(systemOutputTextBox.getText()
							+ "Please make sure that the \"Name\" field and the \"Scenario\" field aren't empty.\n");
				} else {
					AddScenario();
					nameTextBox.setText("");
					scenarioTextBox.setText("");
				}
			}
		});
		addScenarioButton.setBounds(710, 325, 89, 23);
		uploaderTab.add(addScenarioButton);

		removeScenarioButton = new JButton("Remove");
		removeScenarioButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentScenarioViewed < scenarios.size()) {
					RemoveScenario(currentScenarioViewed);
					if (scenarios.size() == 0) {
						nameTextBox.setText("");
						descriptionTextBox.setText("");
						scenarioTextBox.setText("");
						preconditionsTextBox.setText("");
					} else {
						if (currentScenarioViewed != 0) {
							currentScenarioViewed--;
						}

						NavigateScenarios();
					}
				}

			}
		});
		removeScenarioButton.setBounds(809, 325, 89, 23);
		uploaderTab.add(removeScenarioButton);

		lblScenarios = new JLabel("Current Scenario");
		lblScenarios.setBounds(710, 300, 209, 14);
		uploaderTab.add(lblScenarios);

		separator_6 = new JSeparator();
		separator_6.setBounds(699, 316, 250, 8);
		uploaderTab.add(separator_6);

		lblRally = new JLabel("Rally");
		lblRally.setBounds(710, 364, 209, 14);
		uploaderTab.add(lblRally);

		separator_7 = new JSeparator();
		separator_7.setBounds(699, 380, 261, 8);
		uploaderTab.add(separator_7);

		btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GetUserStory();
			}
		});
		btnSet.setBounds(191, 8, 89, 23);
		uploaderTab.add(btnSet);

		mobilePhoneCheckBox = new JCheckBox("Phone");
		mobilePhoneCheckBox.setEnabled(false);
		mobilePhoneCheckBox.setBounds(97, 105, 55, 23);
		uploaderTab.add(mobilePhoneCheckBox);

		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(24, 308, 627, 104);
		uploaderTab.add(scrollPane_4);

		systemOutputTextBox = new JTextPane();
		scrollPane_4.setViewportView(systemOutputTextBox);

		autoPopulateCheckBox = new JCheckBox("Auto Populate Name");
		autoPopulateCheckBox.setBounds(864, 250, 126, 14);
		uploaderTab.add(autoPopulateCheckBox);

		delimiterTextBox = new JTextField();
		delimiterTextBox.setHorizontalAlignment(SwingConstants.CENTER);
		delimiterTextBox.setFont(new Font("Tahoma", Font.BOLD, 11));
		delimiterTextBox.setText(":");
		delimiterTextBox.setBounds(838, 247, 20, 20);
		uploaderTab.add(delimiterTextBox);
		delimiterTextBox.setColumns(10);

		JLabel delimiterLabel = new JLabel("Delimiter:");
		delimiterLabel.setBounds(788, 250, 46, 14);
		uploaderTab.add(delimiterLabel);

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				BufferedReader br = null;
				FileReader fr = null;
				try {
					fr = new FileReader(file);
					br = new BufferedReader(fr);
					String sCurrentLine;
					br = new BufferedReader(new FileReader(file));
					while ((sCurrentLine = br.readLine()) != null) {
						apikeyField.setText(sCurrentLine);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (br != null)
							br.close();
						if (fr != null)
							fr.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		testResultsTab = new JPanel();
		tabbedPane.addTab("Test Results", null, testResultsTab, null);
		testResultsTab.setLayout(null);

		

		DefaultListModel<String> historyListModel = new DefaultListModel<>();

		if (TestResultHistory != null) {
			for (TestResult testResult : TestResultHistory) {
				
				String preview = testResult.getTestResult();
				
				int MaxLength = 28;
				if(testResult.getTestResult().length() > MaxLength)
				{
					MaxLength -= testResult.getName().length();
					preview = testResult.getTestResult().substring(0, MaxLength) + "...";
				}
				historyListModel.addElement(testResult.getName() + ":   " + preview);
			}
		}

		historyList = new JList<>(historyListModel);
		historyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		historyList.setSize(200, 190);
		historyList.setLocation(742, 103);
		historyList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				for (TestResult testResult : TestResultHistory) {
					if (testResult.getName().equals(historyList.getSelectedValue().toString().split(":")[0])) {
						notesForTestResults.setText(testResult.getTestResult());
						textField.setText(historyList.getSelectedValue().toString().split(":")[0]);
						whichToPass.setText(testResult.getWhichToPass());
						break;
					}
				}

			}

		});
		
				rdbtnError = new JRadioButton("Error");
				rdbtnError.setBounds(38, 274, 127, 23);
				groupPassFailBlock.add(rdbtnError);
				testResultsTab.add(rdbtnError);
		
				rdbtnPass = new JRadioButton("Pass");
				rdbtnPass.setSelected(true);
				rdbtnPass.setBounds(38, 143, 127, 23);
				groupPassFailBlock.add(rdbtnPass);
				testResultsTab.add(rdbtnPass);
		
				rdbtnNotComplete = new JRadioButton("Not Completed");
				rdbtnNotComplete.setBounds(38, 248, 141, 23);
				groupPassFailBlock.add(rdbtnNotComplete);
				testResultsTab.add(rdbtnNotComplete);
		
				rdbtnBlock = new JRadioButton("Blocked");
				rdbtnBlock.setBounds(38, 222, 127, 23);
				groupPassFailBlock.add(rdbtnBlock);
				testResultsTab.add(rdbtnBlock);
		
				rdbtnInconclusive = new JRadioButton("Inconclusive");
				rdbtnInconclusive.setBounds(38, 300, 141, 23);
				groupPassFailBlock.add(rdbtnInconclusive);
				testResultsTab.add(rdbtnInconclusive);
		
				rdbtnFail = new JRadioButton("Fail");
				rdbtnFail.setBounds(38, 169, 127, 23);
				groupPassFailBlock.add(rdbtnFail);
				testResultsTab.add(rdbtnFail);
		
				rdbtnDeferred = new JRadioButton("Deferred");
				rdbtnDeferred.setBounds(38, 195, 127, 23);
				groupPassFailBlock.add(rdbtnDeferred);
				testResultsTab.add(rdbtnDeferred);
		testResultsTab.add(historyList);

		textField = new JTextField();
		textField.setBounds(93, 31, 86, 20);
		textField.setColumns(10);
		testResultsTab.add(textField);

		label = new JLabel("Userstory:");
		label.setBounds(28, 34, 78, 14);
		testResultsTab.add(label);

		setUSTestCasesButton = new JButton("Generate Test Results");
		setUSTestCasesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setUserStoryForTestMods();
			}
		});
		setUSTestCasesButton.setBounds(407, 319, 183, 23);
		testResultsTab.add(setUSTestCasesButton);

		whichToPass = new JTextField();
		whichToPass.setBounds(63, 78, 167, 20);
		testResultsTab.add(whichToPass);
		whichToPass.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(302, 103, 409, 190);
		testResultsTab.add(scrollPane);

		notesForTestResults = new JTextPane();
		scrollPane.setViewportView(notesForTestResults);

		JLabel lblTestsEffected = new JLabel("Tests:");
		lblTestsEffected.setBounds(28, 81, 38, 14);
		testResultsTab.add(lblTestsEffected);

		JLabel lblNotes = new JLabel("Notes:");
		lblNotes.setBounds(314, 78, 66, 14);
		testResultsTab.add(lblNotes);

		lblExample = new JLabel("Example: 1, 2, 3-7, 9, 10-11");
		lblExample.setBounds(73, 104, 167, 14);
		testResultsTab.add(lblExample);

		JLabel lblVerdict = new JLabel("Verdict:");
		lblVerdict.setBounds(28, 122, 46, 14);
		testResultsTab.add(lblVerdict);

		labelHistory = new JLabel("History:");
		labelHistory.setBounds(749, 78, 78, 14);
		testResultsTab.add(labelHistory);
		
		lblWarningVerdictsArent = new JLabel("Verdicts aren't saved with History");
		lblWarningVerdictsArent.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblWarningVerdictsArent.setBounds(793, 300, 149, 14);
		testResultsTab.add(lblWarningVerdictsArent);

		taskCreationTab = new JPanel();
		tabbedPane.addTab("Task Creation", null, taskCreationTab, null);
		taskCreationTab.setLayout(null);

		ownerComboBox = new JComboBox();
		ownerComboBox.setModel(new DefaultComboBoxModel(
				new String[] { "Connor O'Brien", "Jeremy Alosio", "Raza Mahmood", "Scott Cichetti" }));
		ownerComboBox.setBounds(726, 105, 137, 20);
		taskCreationTab.add(ownerComboBox);

		userstoryTaskCreationTextArea = new TextArea();
		userstoryTaskCreationTextArea.setBounds(26, 50, 164, 312);
		taskCreationTab.add(userstoryTaskCreationTextArea);

		Label userstoryTaskCreation = new Label("Userstories to Add: ");
		userstoryTaskCreation.setBounds(26, 22, 102, 22);
		taskCreationTab.add(userstoryTaskCreation);

		Label userstoriesLabel = new Label("Userstories:");
		userstoriesLabel.setBounds(230, 22, 102, 22);
		taskCreationTab.add(userstoriesLabel);

		Label arrowsLabel = new Label("=>");
		arrowsLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
		arrowsLabel.setBounds(200, 193, 24, 22);
		taskCreationTab.add(arrowsLabel);

		addToUserstoryListButton = new JButton("Add to List");
		addToUserstoryListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddUserstoriesToList();
				ClearTasks();
			}
		});
		addToUserstoryListButton.setBounds(53, 395, 89, 23);
		taskCreationTab.add(addToUserstoryListButton);

		removeSelectedUserstoryButton = new JButton("Remove");
		removeSelectedUserstoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RemoveSelectedUserStory();
			}
		});
		removeSelectedUserstoryButton.setBounds(295, 373, 72, 23);
		taskCreationTab.add(removeSelectedUserstoryButton);

		JLabel ownerLabel = new JLabel("Owner:");
		ownerLabel.setBounds(670, 108, 46, 14);
		taskCreationTab.add(ownerLabel);

		taskLabel = new JLabel("Tasks:");
		taskLabel.setBounds(393, 22, 46, 14);
		taskCreationTab.add(taskLabel);

		editTaskButton = new JButton("Edit Task");
		editTaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FillCurrentTask();
			}
		});
		editTaskButton.setBounds(455, 105, 89, 23);
		taskCreationTab.add(editTaskButton);

		ownerForAddUserstoryComboBox = new JComboBox();
		ownerForAddUserstoryComboBox.setModel(new DefaultComboBoxModel(
				new String[] { "Connor O'Brien", "Jeremy Alosio", "Raza Mahmood", "Scott Cichetti" }));
		ownerForAddUserstoryComboBox.setSelectedIndex(0);
		ownerForAddUserstoryComboBox.setBounds(67, 368, 123, 20);
		taskCreationTab.add(ownerForAddUserstoryComboBox);

		ownerForAddUserstoryLabel = new JLabel("Owner:");
		ownerForAddUserstoryLabel.setBounds(17, 369, 46, 14);
		taskCreationTab.add(ownerForAddUserstoryLabel);

		taskNameTextBox = new JTextField();
		taskNameTextBox.setBounds(660, 70, 274, 20);
		taskCreationTab.add(taskNameTextBox);
		taskNameTextBox.setColumns(10);

		taskNameLabel = new JLabel("Task Name:");
		taskNameLabel.setBounds(660, 45, 72, 14);
		taskCreationTab.add(taskNameLabel);

		currentTaskLabel = new JLabel("Current Task:");
		currentTaskLabel.setBounds(636, 11, 89, 14);
		taskCreationTab.add(currentTaskLabel);

		descriptionTextArea = new TextArea();
		descriptionTextArea.setBounds(660, 156, 274, 60);
		taskCreationTab.add(descriptionTextArea);

		descriptionLabel = new JLabel("Description:");
		descriptionLabel.setBounds(660, 136, 72, 14);
		taskCreationTab.add(descriptionLabel);

		saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetTask();
			}
		});
		saveButton.setBounds(748, 260, 89, 23);
		taskCreationTab.add(saveButton);

		taskEstTextBox = new JTextField();
		taskEstTextBox.setBounds(701, 229, 46, 20);
		taskCreationTab.add(taskEstTextBox);
		taskEstTextBox.setColumns(10);

		taskEstLabel = new JLabel("Task Est:");
		taskEstLabel.setBounds(655, 232, 46, 14);
		taskCreationTab.add(taskEstLabel);

		toDoTextBox = new JTextField();
		toDoTextBox.setColumns(10);
		toDoTextBox.setBounds(791, 229, 46, 20);
		taskCreationTab.add(toDoTextBox);

		toDoLabel = new JLabel("To Do:");
		toDoLabel.setBounds(757, 232, 46, 14);
		taskCreationTab.add(toDoLabel);

		actualTextBox = new JTextField();
		actualTextBox.setColumns(10);
		actualTextBox.setBounds(888, 229, 46, 20);
		taskCreationTab.add(actualTextBox);

		actualLabel = new JLabel("Actual:");
		actualLabel.setBounds(850, 232, 46, 14);
		taskCreationTab.add(actualLabel);

		separator_10 = new JSeparator();
		separator_10.setOrientation(SwingConstants.VERTICAL);
		separator_10.setBounds(617, 11, 9, 281);
		taskCreationTab.add(separator_10);

		uploadTaskButton = new JButton("Upload");
		uploadTaskButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UploadTasks();
			}
		});
		uploadTaskButton.setBounds(443, 192, 111, 23);
		taskCreationTab.add(uploadTaskButton);

		undoUploadButton = new JButton("Undo Upload");
		undoUploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UndoUploadTasks();
			}
		});
		undoUploadButton.setBounds(443, 243, 111, 23);
		taskCreationTab.add(undoUploadButton);

		uploadLabel = new JLabel("Upload:");
		uploadLabel.setBounds(393, 168, 46, 14);
		taskCreationTab.add(uploadLabel);

		separator_12 = new JSeparator();
		separator_12.setOrientation(SwingConstants.VERTICAL);
		separator_12.setBounds(377, 11, 6, 407);
		taskCreationTab.add(separator_12);

		userstoryScrollPane = new JScrollPane();
		userstoryScrollPane.setBounds(230, 46, 137, 316);
		taskCreationTab.add(userstoryScrollPane);

		userstoryList = new JList();
		userstoryList.setBounds(230, 50, 137, 312);
		userstoryScrollPane.setViewportView(userstoryList);

		tasksScrollPane = new JScrollPane();
		tasksScrollPane.setBounds(393, 50, 215, 44);
		taskCreationTab.add(tasksScrollPane);

		taskList = new JList();
		tasksScrollPane.setViewportView(taskList);

		editSelectedUserstory = new JButton("Edit");
		editSelectedUserstory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DisplayTasks();
				ClearCurrentTask();
			}
		});
		editSelectedUserstory.setBounds(230, 373, 55, 23);
		taskCreationTab.add(editSelectedUserstory);

		JButton removeAllButton = new JButton("Remove All");
		removeAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				RemoveAllTasks();
			}
		});
		removeAllButton.setBounds(250, 400, 89, 23);
		taskCreationTab.add(removeAllButton);

		JSeparator separator_11 = new JSeparator();
		separator_11.setBounds(393, 148, 214, 2);
		taskCreationTab.add(separator_11);

		JSeparator separator_13 = new JSeparator();
		separator_13.setBounds(393, 298, 586, 8);
		taskCreationTab.add(separator_13);

		statusTextArea = new TextArea();
		statusTextArea.setBounds(389, 309, 604, 110);
		taskCreationTab.add(statusTextArea);

		JPanel apiKeyTab = new JPanel();
		apiKeyTab.setLayout(null);
		tabbedPane.addTab("API Key", null, apiKeyTab, null);

		ImageIcon loadIcon = new ImageIcon(RallyMassUploaderGui.class.getResource("/Icon/apiScreen.png"));
		System.out.println(loadIcon);

		JLabel pic = new JLabel();
		pic.setIcon(loadIcon);
		pic.setBounds(131, 71, 766, 250);
		apiKeyTab.add(pic);
		apikeyField = new JTextField();
		apikeyField.setColumns(10);
		apikeyField.setBounds(243, 379, 355, 20);
		apiKeyTab.add(apikeyField);

		JLabel lblApiKey = new JLabel("API Key:");
		lblApiKey.setBounds(194, 382, 51, 14);
		apiKeyTab.add(lblApiKey);

		JButton btnSetNewKey = new JButton("Set New Key");
		btnSetNewKey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileWriter fw = null;
				BufferedWriter bw = null;
				PrintWriter out = null;
				try {
					fw = new FileWriter(file);
					bw = new BufferedWriter(fw);
					out = new PrintWriter(bw);
					out.println(apikeyField.getText());
					out.close();
				} catch (IOException e1) {
					// exception handling left as an exercise for the reader
				}
				try {
					restApi = new RallyRestApi(new URI("https://rally1.rallydev.com"), API_KEY);
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}

		});
		btnSetNewKey.setBounds(624, 378, 150, 23);
		apiKeyTab.add(btnSetNewKey);

		txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys = new JTextField();
		txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys.setEditable(false);
		txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys
				.setText("Go to:   https://rally1.rallydev.com/login/accounts/index.html#/keys");
		txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys.setColumns(10);
		txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys.setBounds(311, 32, 355, 20);
		apiKeyTab.add(txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys);

		txtLoginToHttpsrallyrallydevcomlogin = new JTextField();
		txtLoginToHttpsrallyrallydevcomlogin.setText("Login to: https://rally1.rallydev.com/login/");
		txtLoginToHttpsrallyrallydevcomlogin.setEditable(false);
		txtLoginToHttpsrallyrallydevcomlogin.setColumns(10);
		txtLoginToHttpsrallyrallydevcomlogin.setBounds(379, 11, 213, 20);
		apiKeyTab.add(txtLoginToHttpsrallyrallydevcomlogin);

		setscenariotcButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scenarioNumber = scenarionumberTextBox.getText();
				testcaseNumber = testcasenumberTextBox.getText();
				systemOutputTextBox.setText(systemOutputTextBox.getText() + "Scenario number has been set to \""
						+ scenarionumberTextBox.getText() + "\"\nTest Case number has been set to \""
						+ testcasenumberTextBox.getText() + "\".\n");
			}
		});
		uploadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScenariosToTestCases();
				// GenerateTestFolder();
				nameTextBox.setEnabled(false);
				spanishCheckBox.setEnabled(false);
				mobileTabletCheckBox.setEnabled(false);
				mobilePhoneCheckBox.setEnabled(false);
				descriptionTextBox.setEnabled(false);
				preconditionsTextBox.setEnabled(false);
				scenarioTextBox.setEnabled(false);
				btnResetScenarioTc.setEnabled(false);
				setscenariotcButton.setEnabled(false);
				addScenarioButton.setEnabled(false);
				testcasenumberTextBox.setEnabled(false);
				scenarionumberTextBox.setEnabled(false);
				scenarioNumber = scenarionumberTextBox.getText();
				testcaseNumber = testcasenumberTextBox.getText();
				undoButton.setEnabled(true);
				uploadButton.setEnabled(false);
				DynamiclyManageButtons();
			}
		});
	}

	public static void SaveTestResultsHistory(List<TestResult> _listModel) {

		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);

			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(bo);
			so.writeObject(_listModel);
			so.flush();

			final byte[] byteArray = bo.toByteArray();
			out.println(Base64.getEncoder().encodeToString(byteArray));
			out.close();
		} catch (IOException e1) {
			// exception handling left as an exercise for the reader
		}

	}

	public static void UpdateHistoryList() {
		
		DefaultListModel<String> historyListModel = new DefaultListModel<>();

		for (TestResult testResult : TestResultHistory) {
			
			String preview = testResult.getTestResult();
			
			int MaxLength = 28;
			if(testResult.getTestResult().length() > MaxLength)
			{
				MaxLength -= testResult.getName().length();
				preview = testResult.getTestResult().substring(0, MaxLength) + "...";
			}
			historyListModel.addElement(testResult.getName() + ":   " + preview);
		}
		historyList.setModel(historyListModel);
	}

	public static List<TestResult> LoadTestResultsHistory(String serializedObject) {
		try {
			byte b[] = Base64.getDecoder().decode(serializedObject);
			ByteArrayInputStream bi = new ByteArrayInputStream(b);
			ObjectInputStream si = new ObjectInputStream(bi);
			return (List<TestResult>) si.readObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static String testcaseNumber = "1";
	public static String scenarioNumber = "1";
	public static int currentScenarioViewed = 0;

	private static List<Scenario> scenarios = new ArrayList<Scenario>();
	private static String testcase = null;
	private static String currentProject = null;
	private static String user = null;
	private JPanel testResultsTab;
	private static JTextField textField;
	private JLabel label;
	private JTextField apikeyField;
	JTextField txtLoginToHttpsrallyrallydevcomlogin;
	private JTextField txtGoToHttpsrallyrallydevcomloginaccountsindexhtmlkeys;
	private static String userStoryName;
	private static JTextField whichToPass;
	private static JRadioButton rdbtnBlock;
	private static JRadioButton rdbtnPass;
	private static JRadioButton rdbtnFail;
	private static JRadioButton rdbtnDeferred;
	private static JRadioButton rdbtnNotComplete;
	private static JRadioButton rdbtnInconclusive;
	private static JRadioButton rdbtnError;
	private static ButtonGroup groupPassFailBlock = new ButtonGroup();
	private static JTextPane notesForTestResults;
	private JLabel lblExample;
	private JPanel taskCreationTab;
	private JList userstoryList;
	private JList taskList;
	private JLabel taskLabel;
	private JButton editTaskButton;
	private JLabel ownerForAddUserstoryLabel;
	private JTextField taskNameTextBox;
	private JTextField taskEstTextBox;
	private JTextField toDoTextBox;
	private JTextField actualTextBox;
	private JSeparator separator_10;
	private JButton uploadTaskButton;
	private JButton undoUploadButton;
	private JLabel uploadLabel;
	private JSeparator separator_12;
	private static JLabel taskNameLabel;
	private static JLabel currentTaskLabel;
	private static TextArea descriptionTextArea;
	private static JLabel descriptionLabel;
	private static JButton saveButton;
	private static JLabel taskEstLabel;
	private static JLabel toDoLabel;
	private static JLabel actualLabel;
	private static JComboBox ownerComboBox;
	private static JComboBox ownerForAddUserstoryComboBox;
	private static JButton removeSelectedUserstoryButton;
	private static JButton addToUserstoryListButton;
	private static TextArea userstoryTaskCreationTextArea;

	private static void UpdateHistory(String Name, String Notes, String WhichToPass) {

		
		int maxSize = 11;

		for (TestResult testResult : TestResultHistory) {
			if (testResult.getName().equals(Name)) {
				TestResultHistory.remove(testResult);
				break;
			}
		}

		if (TestResultHistory.size() > maxSize) {
			TestResultHistory.remove(0);
		}

		TestResultHistory.add(new TestResult(Name, Notes, WhichToPass));

		SaveTestResultsHistory(TestResultHistory);
		UpdateHistoryList();
	}

	public static void setUserStoryForTestMods() {

		UpdateHistory(textField.getText(), notesForTestResults.getText(), whichToPass.getText());

		QueryRequest queryRequest = new QueryRequest("hierarchicalrequirement");
		queryRequest.setQueryFilter(new QueryFilter("FormattedID", "=", textField.getText()));

		QueryResponse queryResponse = null;

		try {
			queryResponse = restApi.query(queryRequest);
		} catch (IOException e1) {
			e1.printStackTrace();
			systemOutputTextBox.setText(systemOutputTextBox.getText() + "Couldn't Find Userstory\n");
		}

		String testcases = null;
		if (queryResponse.wasSuccessful() && queryResponse.getResults().size() != 0) {
			for (JsonElement result : queryResponse.getResults()) {
				JsonObject userstory = result.getAsJsonObject();
				testcases = userstory.get("TestCases").getAsJsonObject().get("_ref").getAsString();
			}
		}

		GetRequest getRequest = new GetRequest(testcases);
		getRequest.addParam("pagesize", "999");
		GetResponse getResponse = null;
		try {
			getResponse = restApi.get(getRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonObject queryResult = getResponse.getObject();

		JsonArray allResults = queryResult.get("Results").getAsJsonArray();

		String testRef = null;

		java.util.Date date = new java.util.Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		String timestamp = sdf.format(date);

		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date buildDate = new Date();

		String verdict = "";

		if (rdbtnBlock.isSelected()) {
			verdict = "Blocked";
		} else if (rdbtnPass.isSelected()) {
			verdict = "Pass";
		} else if (rdbtnFail.isSelected()) {
			verdict = "Fail";
		} else if (rdbtnDeferred.isSelected()) {
			verdict = "Deferred";
		} else if (rdbtnNotComplete.isSelected()) {
			verdict = "Not Completed";
		} else if (rdbtnError.isSelected()) {
			verdict = "Error";
		} else if (rdbtnInconclusive.isSelected()) {
			verdict = "Inconclusive";
		}

		List<Integer> tcToPass = new ArrayList<Integer>();

		Boolean setAll = false;

		for (String currentSet : whichToPass.getText().replaceAll(" ", "").split(",")) {

			if (currentSet.contains("-")) {
				Matcher input = Pattern.compile("([0-9]*)-([0-9]*)").matcher(currentSet);

				int startNumber = 0;
				int endNumber = 0;

				if (input.find()) {
					startNumber = Integer.parseInt(input.group(1));
					endNumber = Integer.parseInt(input.group(2));
				}

				for (; startNumber <= endNumber; startNumber++) {
					tcToPass.add(startNumber);
				}

			} else {
				tcToPass.add(Integer.parseInt(currentSet));
			}
		}
		if (tcToPass.isEmpty()) {
			if (whichToPass.getText().trim().isEmpty()) {
				setAll = true;
			} else {
				tcToPass.add(Integer.parseInt(whichToPass.getText().trim()));
			}
		}

		for (JsonElement result : allResults) {

			JsonObject newTestCaseResult = result.getAsJsonObject();
			testRef = newTestCaseResult.get("_ref").getAsString();

			Boolean setThisTC = false;

			if (setAll == false) {
				String testTitle = newTestCaseResult.get("_refObjectName").getAsString();

				Matcher input = Pattern.compile("US[0-9]* - TC([0-9]*)").matcher(testTitle);

				int testNumber = 0;

				if (input.find()) {
					testNumber = Integer.parseInt(input.group(1));
				}

				if (tcToPass.contains(testNumber)) {
					setThisTC = true;
				}

			}

			if (setThisTC || setAll) {
				newTestCaseResult = new JsonObject();
				user = getUserRef().replace("\"", "");
				newTestCaseResult.addProperty("Verdict", verdict);
				newTestCaseResult.addProperty("Notes", notesForTestResults.getText().replaceAll("\n", "<br/>"));
				newTestCaseResult.addProperty("Date", timestamp);
				newTestCaseResult.addProperty("Build", dateFormat.format(buildDate));
				newTestCaseResult.addProperty("Tester", user);
				newTestCaseResult.addProperty("TestCase", testRef);
				newTestCaseResult.addProperty("Workspace",
						"https://rally1.rallydev.com/slm/webservice/v2.0/workspace/14457696030");

				CreateRequest createRequest = new CreateRequest("testcaseresult", newTestCaseResult);

				try {
					restApi.create(createRequest);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public static void GetUserStory() {

		QueryRequest queryRequest = new QueryRequest("hierarchicalrequirement");
		queryRequest.setQueryFilter(new QueryFilter("FormattedID", "=", getUserstoryTextBox()));

		QueryResponse queryResponse = null;

		try {
			queryResponse = restApi.query(queryRequest);
		} catch (IOException e1) {
			e1.printStackTrace();
			systemOutputTextBox.setText(systemOutputTextBox.getText() + "Couldn't Find Userstory\n");
		}

		if (queryResponse.wasSuccessful() && queryResponse.getResults().size() != 0) {
			systemOutputTextBox
					.setText(systemOutputTextBox.getText() + "Ready to work with " + getUserstoryTextBox() + ":\n\t");
			for (JsonElement result : queryResponse.getResults()) {
				JsonObject userstory = result.getAsJsonObject();
				testcase = userstory.get("_ref").getAsString();
				currentProject = userstory.get("Project").getAsJsonObject().get("_ref").getAsString();
				descriptionTextBox.setText(
						userstory.get("Description").getAsString().replace("<p", "\n<p").replace("<br />", "<br/>\n"));
				systemOutputTextBox.setText(systemOutputTextBox.getText() + userstory.get("Name").getAsString() + "\n");
				userStoryName = getUserstoryTextBox() + ": " + userstory.get("Name").getAsString();
			}
			scenarios = new ArrayList<Scenario>();
			currentScenarioViewed = 0;
			nameTextBox.setText("");
			scenarioTextBox.setText("");
			preconditionsTextBox.setText("");

			uploadButton.setEnabled(true);
			undoButton.setEnabled(false);
			nameTextBox.setEnabled(true);
			spanishCheckBox.setEnabled(true);
			mobileTabletCheckBox.setEnabled(true);
			mobilePhoneCheckBox.setEnabled(true);
			descriptionTextBox.setEnabled(true);
			preconditionsTextBox.setEnabled(true);
			scenarioTextBox.setEnabled(true);
			btnResetScenarioTc.setEnabled(true);
			setscenariotcButton.setEnabled(true);
			addScenarioButton.setEnabled(true);
			testcasenumberTextBox.setEnabled(true);
			scenarionumberTextBox.setEnabled(true);
			DynamiclyManageButtons();
		} else {
			systemOutputTextBox.setText(systemOutputTextBox.getText() + "Couldn't Find Userstory\n");
		}

		user = getUserRef().replace("\"", "");
	}

	private static void DynamiclyManageButtons() {
		if (currentScenarioViewed > scenarios.size() - 1) {
			nextButton.setEnabled(false);
			btnLast.setEnabled(false);
		} else {
			nextButton.setEnabled(true);
			btnLast.setEnabled(true);
		}

		if (currentScenarioViewed == 0) {
			btnPrevious.setEnabled(false);
			btnFirst.setEnabled(false);
			uploadButton.setEnabled(false);
		} else {
			btnPrevious.setEnabled(true);
			btnFirst.setEnabled(true);
			if (!undoButton.isEnabled() && currentScenarioViewed == scenarios.size()) {
				uploadButton.setEnabled(true);
			} else {
				uploadButton.setEnabled(false);
			}
		}

		if (!undoButton.isEnabled()) {
			if (currentScenarioViewed < scenarios.size()) {
				addScenarioButton.setEnabled(false);
				if (addScenarioButton.isEnabled()) {
					removeScenarioButton.setEnabled(true);
				} else {
					removeScenarioButton.setEnabled(true);
				}
			} else {
				addScenarioButton.setEnabled(true);
				removeScenarioButton.setEnabled(false);
			}
		} else {
			addScenarioButton.setEnabled(false);
			removeScenarioButton.setEnabled(false);
		}

	}

	private static void NavigateScenarios() {
		userstoryTextBox.setText(scenarios.get(currentScenarioViewed).getUserstory());
		nameTextBox.setText(scenarios.get(currentScenarioViewed).getOriginalName());
		descriptionTextBox.setText(scenarios.get(currentScenarioViewed).getDescription());
		scenarioTextBox.setText(scenarios.get(currentScenarioViewed).getValidationInput());
		preconditionsTextBox.setText(scenarios.get(currentScenarioViewed).getPreConditions());
		mobileTabletCheckBox.setSelected(scenarios.get(currentScenarioViewed).isMobileTablet());
		mobilePhoneCheckBox.setSelected(scenarios.get(currentScenarioViewed).isMobilePhone());
		spanishCheckBox.setSelected(scenarios.get(currentScenarioViewed).isSpanish());
	}

	private static void RemoveScenario(int scenario) {
		String name = scenarios.get(scenario).getOriginalName();
		String userstory = scenarios.get(scenario).getUserstory();
		scenarios.remove(scenario);
		systemOutputTextBox.setText(systemOutputTextBox.getText() + "Scenario \"" + name + "\" has been removed from "
				+ userstory + "'s upload queue.\n");

		DynamiclyManageButtons();
	}

	private static void AddScenario() {
		Scenario currentScenario = new Scenario();

		currentScenario.setUserstory(getUserstoryTextBox());
		currentScenario.setOriginalName(getNameTextBox());
		currentScenario.setDescription(getDescriptionTextBox());
		currentScenario.setTestCase(testcase);
		currentScenario.setOwner(user);
		currentScenario.setProject(currentProject);
		currentScenario.setValidationInput(getScenarioTextBox());
		currentScenario.setPreConditions(getPreconditionsTextBox());
		currentScenario.setMobileTablet(mobileTabletCheckBox.isSelected());
		currentScenario.setMobilePhone(mobilePhoneCheckBox.isSelected());
		currentScenario.setSpanish(spanishCheckBox.isSelected());

		scenarios.add(currentScenario);

		systemOutputTextBox.setText(systemOutputTextBox.getText() + "Scenario \"" + currentScenario.getOriginalName()
				+ "\" has been added to " + currentScenario.getUserstory() + "'s upload queue.\n");

		if (currentScenarioViewed == scenarios.size() - 1) {
			currentScenarioViewed++;
		} else {
			currentScenarioViewed = scenarios.size();
		}
		DynamiclyManageButtons();

	}

	private static void EditScenario() {
		Scenario currentScenario = scenarios.get(currentScenarioViewed);

		currentScenario.setUserstory(getUserstoryTextBox());
		currentScenario.setOriginalName(getNameTextBox());
		currentScenario.setDescription(getDescriptionTextBox());
		currentScenario.setTestCase(testcase);
		currentScenario.setOwner(user);
		currentScenario.setProject(currentProject);
		currentScenario.setValidationInput(getScenarioTextBox());
		currentScenario.setPreConditions(getPreconditionsTextBox());
		currentScenario.setMobileTablet(mobileTabletCheckBox.isSelected());
		currentScenario.setMobilePhone(mobilePhoneCheckBox.isSelected());
		currentScenario.setSpanish(spanishCheckBox.isSelected());

		DynamiclyManageButtons();

	}

	private static void ScenariosToTestCases() {
		for (Scenario currentScenario : scenarios) {
			CreateTestCases(currentScenario);
			scenarioNumber = Integer.toString(Integer.parseInt(scenarioNumber) + 1);
			testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		}

	}

	private static void CreateTestCases(Scenario currentScenario) {

		systemOutputTextBox.setText(systemOutputTextBox.getText() + "Generating Testcases\n");

		// ------------------------------------------------------
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber));
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}

		currentScenario.setCurrentName(currentScenario.getUserstory() + " - TC" + testcaseNumber + " - Scenario "
				+ scenarioNumber + " - " + currentScenario.getOriginalName());
		// ------------------------------------------------------
		currentScenario.UploadTest();

		if (currentScenario.isMobileTablet()) {
			GenerateMobileTabletTests(currentScenario.getUserstory(), currentScenario);
		}
		if (currentScenario.isMobilePhone()) {
			GenerateMobilePhoneTests(currentScenario.getUserstory(), currentScenario);
		}
		if (currentScenario.isSpanish()) {
			GenerateSpanishTests(currentScenario.getUserstory(), currentScenario);
		}

	}

	private static void GenerateSpanishTests(String userstory, Scenario currentScenario) {
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}
		currentScenario.setCurrentName(userstory + " - TC" + testcaseNumber + " - Scenario " + scenarioNumber + " - "
				+ currentScenario.getOriginalName() + " - Spanish");
		currentScenario.UploadTest();
	}

	private static void GenerateMobileTabletTests(String userstory, Scenario currentScenario) {
		// Increase -------------------------------
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}
		currentScenario.setCurrentName(userstory + " - TC" + testcaseNumber + " - Scenario " + scenarioNumber + " - "
				+ currentScenario.getOriginalName() + " - Android - Tablet");
		currentScenario.UploadTest();
		// -------------------------------

		// Increase-------------------------------
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}
		currentScenario.setCurrentName(userstory + " - TC" + testcaseNumber + " - Scenario " + scenarioNumber + " - "
				+ currentScenario.getOriginalName() + " - Apple - Tablet");
		currentScenario.UploadTest();
	}

	private static void GenerateMobilePhoneTests(String userstory, Scenario currentScenario) {
		// Increase -------------------------------
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}
		currentScenario.setCurrentName(userstory + " - TC" + testcaseNumber + " - Scenario " + scenarioNumber + " - "
				+ currentScenario.getOriginalName() + " - Android - Phone");
		currentScenario.UploadTest();
		// -------------------------------

		// Increase -------------------------------
		testcaseNumber = Integer.toString(Integer.parseInt(testcaseNumber) + 1);
		if (testcaseNumber.length() < 2) {
			testcaseNumber = "0" + testcaseNumber;
		}
		currentScenario.setCurrentName(userstory + " - TC" + testcaseNumber + " - Scenario " + scenarioNumber + " - "
				+ currentScenario.getOriginalName() + " - Apple - Phone");
		currentScenario.UploadTest();
		// -------------------------------

	}

	public static String getUserRef() {
		GetResponse getResponse = null;
		try {
			getResponse = restApi.get(new GetRequest("user"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return getResponse.getObject().getAsJsonObject().get("_ref").toString();
	}

	@SuppressWarnings("null")
	void GetInformation() {

		// Read User
		QueryRequest userRequest = new QueryRequest("User");
		userRequest.setFetch(new Fetch("UserName", "Subscription", "DisplayName"));
		userRequest.setQueryFilter(new QueryFilter("UserName", "=", "jeremy.alosio@optum.com"));
		QueryResponse userQueryResponse = null;
		RallyRestApi restApi = null;
		try {
			userQueryResponse = restApi.query(userRequest);
		} catch (IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		JsonArray userQueryResults = userQueryResponse.getResults();
		JsonElement userQueryElement = userQueryResults.get(0);
		JsonObject userQueryObject = userQueryElement.getAsJsonObject();
		String userRef = userQueryObject.get("_ref").toString();

		// Get reference to Workspace of interest
		QueryRequest workspaceRequest = new QueryRequest("Workspace");
		workspaceRequest.setFetch(new Fetch("Name", "Owner", "Projects"));
		// workspaceRequest.setQueryFilter(new QueryFilter("Name", "=",
		// myWorkspace));
		QueryResponse workspaceQueryResponse = null;
		try {
			workspaceQueryResponse = restApi.query(workspaceRequest);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		String workspaceRef = workspaceQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").toString();

		// Get reference to Project of interest
		QueryRequest projectRequest = new QueryRequest("Project");
		projectRequest.setFetch(new Fetch("Name", "Owner", "Projects"));
		projectRequest.setQueryFilter(new QueryFilter("Name", "=", "Touchdown"));
		QueryResponse projectQueryResponse = null;
		try {
			projectQueryResponse = restApi.query(projectRequest);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String projectRef = projectQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").toString();

		// Query for existing Test Case
		QueryRequest existTestCaseRequest = new QueryRequest("TestCase");
		existTestCaseRequest.setFetch(new Fetch("FormattedID", "Name", "Tags"));
		existTestCaseRequest.setQueryFilter(new QueryFilter("FormattedID", "=", "TC206378"));
		QueryResponse testCaseQueryResponse = null;
		try {
			testCaseQueryResponse = restApi.query(existTestCaseRequest);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String existTestCaseRef = testCaseQueryResponse.getResults().get(0).getAsJsonObject().get("_ref").toString();

		System.out.println(userRef);// Me
		System.out.println(workspaceRef);// UHG
		System.out.println(projectRef);
		System.out.println(existTestCaseRef);
	}

	public static String getUserstoryTextBox() {
		if (userstoryTextBox == null) {
			return "";
		}
		return userstoryTextBox.getText();
	}

	public static String getNameTextBox() {
		if (nameTextBox == null) {
			return "";
		}
		return nameTextBox.getText();
	}

	public static String getDescriptionTextBox() {
		if (descriptionTextBox == null) {
			return "";
		}
		return descriptionTextBox.getText();
	}

	public static String getScenarioTextBox() {
		if (scenarioTextBox == null) {
			return "";
		}
		return scenarioTextBox.getText();
	}

	public static String getPreconditionsTextBox() {
		if (preconditionsTextBox == null) {
			return "";
		}
		return preconditionsTextBox.getText();
	}

	public static String getSystemOutputTextBox() {
		if (systemOutputTextBox == null) {
			return "";
		}
		return systemOutputTextBox.getText();
	}

	public static JButton getSetUSTestCasesButton() {
		return setUSTestCasesButton;
	}

	public static void setSetUSTestCasesButton(JButton setUSTestCasesButton) {
		RallyMassUploaderGui.setUSTestCasesButton = setUSTestCasesButton;
	}

	// Task Creation
	// ---------------------------------------------------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings({ "rawtypes" })
	static DefaultListModel listModel = new DefaultListModel();
	List<TaskUserstory> userstories = new ArrayList<TaskUserstory>();
	private JScrollPane tasksScrollPane;
	private JButton editSelectedUserstory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void AddUserstoriesToList() {

		List<String> userstoryStringList = new ArrayList<String>();

		Collections.addAll(userstoryStringList, getUserstoryTaskCreationTextArea().split("\r\n"));

		userstoryStringList.removeIf(x -> x.equals(""));

		for (String userstoryString : userstoryStringList) {
			TaskUserstory userstory = new TaskUserstory();

			String userstoryRef = findUserStory(userstoryString);

			if (!userstoryRef.equals("")) {
				userstory.setUserstory(userstoryString);
				userstory.setUserstoryAddress(userstoryRef);

				Task testPlanning = new Task();
				testPlanning.setUserstory(userstoryString);
				testPlanning.setTaskName("QA Test Planning");
				testPlanning.setDescription("QA member will create the test cases for this userstory.");
				testPlanning.setOwner(getOwnerForAddUserstoryComboBox());
				testPlanning.setProject(userstoryRef);
				testPlanning.setTimeEstimate("1");
				testPlanning.setTimeToDo("1");
				testPlanning.setTimeActuals("0");

				Task testExecution = new Task();
				testExecution.setUserstory(userstoryString);
				testExecution.setTaskName("QA Test Execution");
				testExecution.setDescription(
						"QA member tests the userstory and give test cases test results. Any defects found will be written during this time as well.");
				testExecution.setOwner(getOwnerForAddUserstoryComboBox());
				testExecution.setProject(userstoryRef);
				testExecution.setTimeEstimate("2");
				testExecution.setTimeToDo("2");
				testExecution.setTimeActuals("0");

				List<Task> tasks = new ArrayList<Task>();
				tasks.add(testPlanning);
				tasks.add(testExecution);

				userstory.setTasks(tasks);

				userstories.add(userstory);
			}
			ClearTasks();
		}

		for (TaskUserstory taskUserStory : userstories) {
			if (!listModel.contains(taskUserStory.getUserstory())) {
				listModel.addElement(taskUserStory.getUserstory());
			}
		}
		userstoryList = new JList(listModel);
		userstoryScrollPane.setViewportView(userstoryList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void RemoveSelectedUserStory() {
		userstories.removeIf(x -> x.getUserstory().equals(userstoryList.getSelectedValue()));
		listModel = new DefaultListModel();
		for (TaskUserstory taskUserStory : userstories) {
			listModel.addElement(taskUserStory.getUserstory());
		}
		userstoryList = new JList(listModel);
		userstoryScrollPane.setViewportView(userstoryList);
		ClearTasks();
		ClearCurrentTask();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void RemoveAllTasks() {
		listModel = new DefaultListModel();
		userstoryList = new JList(listModel);
		userstoryScrollPane.setViewportView(userstoryList);
		ClearTasks();
		ClearCurrentTask();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void DisplayTasks() {
		DefaultListModel listModelTasks = new DefaultListModel();

		for (Task task : userstories.get(userstoryList.getSelectedIndex()).getTasks()) {
			listModelTasks.addElement(task.getTaskName());
		}
		taskList = new JList(listModelTasks);
		tasksScrollPane.setViewportView(taskList);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void ClearTasks() {
		DefaultListModel listModelTasks = new DefaultListModel();
		taskList = new JList(listModelTasks);
		tasksScrollPane.setViewportView(taskList);
		ClearCurrentTask();
	}

	public void ClearCurrentTask() {
		taskNameTextBox.setText("");
		ownerComboBox.setSelectedIndex(0);
		descriptionTextArea.setText("");
		taskEstTextBox.setText("");
		toDoTextBox.setText("");
		actualTextBox.setText("");
	}

	public void FillCurrentTask() {
		Task currentTask = userstories.get(userstoryList.getSelectedIndex()).getTasks()
				.get(taskList.getSelectedIndex());

		taskNameTextBox.setText(currentTask.getTaskName());
		ownerComboBox.setSelectedIndex(getOwnerNumber(currentTask.getOwner()));
		descriptionTextArea.setText(currentTask.getDescription());
		taskEstTextBox.setText(currentTask.getTimeEstimate());
		toDoTextBox.setText(currentTask.getTimeToDo());
		actualTextBox.setText(currentTask.getTimeActuals());
	}

	public String findUserStory(String currentUserstory) {
		QueryRequest queryRequest = new QueryRequest("hierarchicalrequirement");
		queryRequest.setQueryFilter(new QueryFilter("FormattedID", "=", currentUserstory));

		QueryResponse queryResponse = null;

		try {
			queryResponse = restApi.query(queryRequest);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (queryResponse.wasSuccessful() && queryResponse.getResults().size() != 0) {
			for (JsonElement result : queryResponse.getResults()) {
				JsonObject userstoryJSON = result.getAsJsonObject();
				return userstoryJSON.get("_ref").getAsString();
			}
		} else {
			return null;
		}

		return null;
	}

	public List<Task> uploadedTasks = new ArrayList<Task>();
	private JScrollPane scrollPane_1;
	private JScrollPane scrollPane_2;
	private JScrollPane scrollPane_3;
	private JCheckBox autoPopulateCheckBox;
	private JTextField delimiterTextBox;
	private JLabel labelHistory;
	private JLabel lblWarningVerdictsArent;

	public void UploadTasks() {
		for (TaskUserstory userstory : userstories) {
			for (Task task : userstory.getTasks()) {
				uploadedTasks.add(task);
				task.UploadTask();
			}
		}
	}

	public void UndoUploadTasks() {
		for (Task task : uploadedTasks) {
			task.DeleteTask();
		}
	}

	public void SetTask() {
		Task currentTask = userstories.get(userstoryList.getSelectedIndex()).getTasks()
				.get(taskList.getSelectedIndex());

		currentTask.setTaskName(taskNameTextBox.getText());
		currentTask.setOwner(getURLofOwner(ownerComboBox.getSelectedIndex()));
		currentTask.setDescription(descriptionTextArea.getText());
		currentTask.setTimeEstimate(taskEstTextBox.getText());
		currentTask.setTimeToDo(toDoTextBox.getText());
		currentTask.setTimeActuals(actualTextBox.getText());

		userstories.get(userstoryList.getSelectedIndex()).getTasks().remove(taskList.getSelectedIndex());
		userstories.get(userstoryList.getSelectedIndex()).getTasks().add(currentTask);

		DisplayTasks();
	}

	public static String getOwnerForAddUserstoryComboBox() {

		// Squad's Urls
		if (ownerForAddUserstoryComboBox.getSelectedItem().toString().equals("Connor O'Brien")) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/77116148076";
		} else if (ownerForAddUserstoryComboBox.getSelectedItem().toString().equals("Jeremy Alosio")) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/52716332609";
		} else if (ownerForAddUserstoryComboBox.getSelectedItem().toString().equals("Raza Mahmood")) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/77116914960";
		} else if (ownerForAddUserstoryComboBox.getSelectedItem().toString().equals("Scott Cichetti")) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/55112165049";
		}

		return null;
	}

	public static int getOwnerNumber(String user) {

		// Squad's Urls
		if (user.equals("https://rally1.rallydev.com/slm/webservice/v2.0/user/77116148076")) {
			return 0;
		} else if (user.equals("https://rally1.rallydev.com/slm/webservice/v2.0/user/52716332609")) {
			return 1;
		} else if (user.equals("https://rally1.rallydev.com/slm/webservice/v2.0/user/77116914960")) {
			return 2;
		} else if (user.equals("https://rally1.rallydev.com/slm/webservice/v2.0/user/55112165049")) {
			return 3;
		}

		return -1;
	}

	public static String getURLofOwner(int user) {

		// Squad's Urls
		if (user == 0) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/77116148076";
		} else if (user == 1) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/52716332609";
		} else if (user == 2) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/77116914960";
		} else if (user == 3) {
			return "https://rally1.rallydev.com/slm/webservice/v2.0/user/55112165049";
		}

		return "";
	}

	public static String getUserstoryTaskCreationTextArea() {
		return userstoryTaskCreationTextArea.getText();
	}
}
