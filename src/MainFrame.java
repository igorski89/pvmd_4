import info.clearthought.layout.TableLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;


public class MainFrame extends JFrame {
	private static final long serialVersionUID = 2269971701250845501L;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openFileMenuItem;
	private JMenuItem exportMenuItem;
	private JMenuItem exitMenuItem;
	
	private JTabbedPane tabs;
	
	private MDSample originalSample = null;
	private JTable originalSampleTable;
	private AbstractTableModel originalSampleTableModel;
	
	private JComboBox ciamDComboBox;
	private ComboBoxModel ciamDComboBoxModel;
	private ChoosingInformationalAttributeMethod ciam = null;
	private int ciamDComboBoxIndex = -1;
	private AbstractTableModel ciamDTableModel;
	private JTable ciamDTable;
	private JSlider ciamAttrsCountSlider;
	private int ciamAttrsCount = 1;
	private JTable ciamSampleTable;
	private AbstractTableModel ciamSampleTableModel;
	
	
	private JTabbedPane mcmTabs;
	
	private MainComponentsMethod mcm = null;
	
	private JTable mcmSampleTable;
	private AbstractTableModel mcmSampleTableModel;
	
	private JTable mcmSampleCorelationTable;
	private AbstractTableModel mcmSampleCorelationTableModel;
	
	private JTable mcmADeltaTable;
	private AbstractTableModel mcmADeltaTableModel;
	
	private JTable mcmLambdaTable;
	private AbstractTableModel mcmLambdaTableModel;
	
	private JTable mcmNewSampleCorelationTable;
	private AbstractTableModel mcmNewSampleCorelationTableModel;
	
	private JTable mcmNewComponentsTable;
	private AbstractTableModel mcmNewComponentsTableModel;
	
	private JButton mcmMinimizeAllButton;
	private JButton mcmMinimize75Button;
	private JButton mcmMinimizeByEignValueButton;
	private JTextField mcmMinimizeEignValueTextField;
	
	public MainFrame(){
		setTitle("Main Window");
		tabs = new JTabbedPane();
		
		//menu
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		openFileMenuItem = new JMenuItem("Open");
//		openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		openFileMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFileChooser = new JFileChooser();
				openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFileChooser.setMultiSelectionEnabled(false);
				
//				openFileChooser.setCurrentDirectory(new File("/Users/igorevsukov/Documents/DNU/PVMD/DATA/DATA_4/"));
				if (openFileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION){
					try {
						String fileName = openFileChooser.getSelectedFile().getAbsolutePath();
						BufferedReader input = new BufferedReader(new FileReader(fileName));
						try {
							String line = input.readLine();
							if (line == null) throw new Exception("Can't read data: file "+fileName+"is empty");
							MDObject first_obj = new MDObject(line);
							originalSample = new MDSample(first_obj.getParams().length);
							originalSample.add(first_obj);
							while((line = input.readLine()) != null){
								try {
									originalSample.add(new MDObject(line));
								}catch (Exception ex) {
									System.out.println("can't add object to studySample:" + ex.getMessage());
								}
							}
						}
						finally{
							input.close();
							originalSampleTable.tableChanged(null);
							ciam = new ChoosingInformationalAttributeMethod(originalSample);
							ciamDTable.tableChanged(null);
							ciamDComboBoxIndex = -1;
							
							ciamAttrsCount = 1;
							ciamAttrsCountSlider.setModel(new DefaultBoundedRangeModel(1,0,1,ciam.getSortedAttributes().length));
							ciamSampleTable.tableChanged(null);
							
							mcm = new MainComponentsMethod(originalSample);
							mcm.minimizeAll();
							mcmSampleTable.tableChanged(null);
							mcmSampleCorelationTable.tableChanged(null);
							mcmADeltaTable.tableChanged(null);
							mcmLambdaTable.tableChanged(null);
							mcmNewSampleCorelationTable.tableChanged(null);
							mcmNewComponentsTable.tableChanged(null);
							
							setTitle(fileName);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		});
		fileMenu.add(openFileMenuItem);
		exportMenuItem = new JMenuItem("Export");
		exportMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if (tabs.getSelectedIndex() == 1 && ciam != null) {
					JFileChooser saver = new JFileChooser();
					if (saver.showSaveDialog(rootPane) == JFileChooser.APPROVE_OPTION) {
						String fileName = saver.getSelectedFile().getAbsolutePath();
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
							for(int i = 0; i < ciam.getSample().size(); i++){
								for(int j=0; j < ciamAttrsCount; j++) {
									out.write(String.valueOf(ciam.getSample().get(i).getParams()[ciam.getSortedAttributes()[j]]));
									out.write("\t");
								}
								out.write("\n");
							}
							out.close();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				} else if (tabs.getSelectedIndex() == 2 && mcm != null) {
					JFileChooser saver = new JFileChooser();
					if (saver.showSaveDialog(rootPane) == JFileChooser.APPROVE_OPTION) {
						String fileName = saver.getSelectedFile().getAbsolutePath();
						try {
							BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
							for(int i = 0; i < mcm.getZ().size(); i++){
								for(int j=0; j < mcm.getZ().get(0).length; j++) {
									out.write(String.valueOf(mcm.getZ().get(i)[j]));
									out.write("\t");
								}
								out.write("\n");
							}
							out.close();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		fileMenu.add(exportMenuItem);
		fileMenu.addSeparator();
		
		exitMenuItem = new JMenuItem("Exit");
//		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}			
		});
		fileMenu.add(exitMenuItem);
		
		menuBar.add(fileMenu);
		
		setJMenuBar(menuBar);
		
		//original table
		originalSampleTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (originalSample == null) return 0;
				else return originalSample.getDimension();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (originalSample == null) return 0;
				else return originalSample.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Double(originalSample.get(rowIndex).getParams()[columnIndex]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		originalSampleTable = new JTable(originalSampleTableModel);
		tabs.add(new JScrollPane(originalSampleTable), "Original Sample", 0);
		
		//"Choosing Informational Attrubite Method" panel
//		JPanel ciamPanel = new JPanel(new GridBagLayout());
		JPanel ciamPanel = new JPanel(new TableLayout(new double[][] {{0.5,0.5},{TableLayout.PREFERRED,TableLayout.FILL}}));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		ciamDComboBoxModel = new  ComboBoxModel(){
			@Override
			public Object getSelectedItem() {
				if (ciamDComboBoxIndex == -1) return "D";
				else return "D"+ciamDComboBoxIndex;
			}
			@Override
			public void setSelectedItem(Object anItem) {
				if ((String)anItem == "D") ciamDComboBoxIndex = -1;
				else ciamDComboBoxIndex = Integer.parseInt(((String)anItem).substring(1));
			}
			@Override
			public void addListDataListener(ListDataListener l) {}
			@Override
			public Object getElementAt(int index) {
				if (index == 0) return "D";
				else return "D"+(index-1);
			}
			@Override
			public int getSize() {
				if (ciam == null) return 0;
				else return ciam.getSample().getDimension()+1;
			}
			@Override
			public void removeListDataListener(ListDataListener l) {}
		};
		ciamDComboBox = new JComboBox(ciamDComboBoxModel);
		ciamDComboBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				ciamDTable.tableChanged(null);
			}			
		});
//		ciamPanel.add(ciamDComboBox,c);
		ciamPanel.add(ciamDComboBox,"0, 0");
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		ciamDTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;
			
			@Override
			public int getColumnCount() {
				if (ciam == null) return 0;
				else return ciam.getSample().size();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (ciam == null) return 0;
				else return ciam.getSample().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (ciamDComboBoxIndex == -1)
					return new Double(ciam.getD()[rowIndex][columnIndex]);
				else
					return new Double(ciam.getDj().get(ciamDComboBoxIndex)[rowIndex][columnIndex]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		ciamDTable = new JTable(ciamDTableModel);
		JScrollPane ciamDTableScrollPane = new JScrollPane(ciamDTable);
		ciamDTableScrollPane.setMinimumSize(new Dimension(200,200));
//		ciamPanel.add(ciamDTableScrollPane,c);
		ciamPanel.add(ciamDTableScrollPane,"0, 1");
		
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.HORIZONTAL;
		ciamAttrsCountSlider = new JSlider(JSlider.HORIZONTAL);
		ciamAttrsCountSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				ciamAttrsCount = ciamAttrsCountSlider.getValue();
				ciamSampleTable.tableChanged(null);
			}			
		});
		ciamAttrsCountSlider.setPaintLabels(true);
		ciamAttrsCountSlider.setPaintTicks(true);
		ciamAttrsCountSlider.setPaintTrack(true);
//		ciamPanel.add(ciamAttrsCountSlider,c);
		ciamPanel.add(ciamAttrsCountSlider,"1, 0");
		
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		ciamSampleTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;
			
			@Override
			public int getColumnCount() {
				if (ciam == null) return 0;
				else return ciamAttrsCount;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return String.valueOf(ciam.getSortedAttributes()[columnIndex]);
			}

			@Override
			public int getRowCount() {
				if (ciam == null) return 0;
				else return ciam.getSample().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Double(ciam.getSample().get(rowIndex).getParams()[ciam.getSortedAttributes()[columnIndex]]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		ciamSampleTable = new JTable(ciamSampleTableModel);
		JScrollPane ciamSampleTableScrollPane = new JScrollPane(ciamSampleTable);
		ciamSampleTableScrollPane.setMinimumSize(new Dimension(200,200));
//		ciamPanel.add(ciamSampleTableScrollPane,c);
		ciamPanel.add(ciamSampleTableScrollPane,"1, 1");
		
		tabs.add(ciamPanel, "Choosing Informational Attrubite Method", 1);
		
		
		//"Main Components Method" panel
		mcmTabs = new JTabbedPane();
		
		mcmSampleTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Double(mcm.getSample().get(rowIndex).getParams()[columnIndex]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmSampleTable = new JTable(mcmSampleTableModel);
		mcmTabs.add(new JScrollPane(mcmSampleTable),"Sample",0);
		
		mcmSampleCorelationTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
//				return new Double(mcm.getSample().getCovariationMatrix()[rowIndex][columnIndex]);
				return new Double(mcm.getSample().getCorelationMatrix()[rowIndex][columnIndex]);
//				return new Double(mcm.getA().get(columnIndex)[rowIndex]*Math.sqrt(mcm.getEignValue().get(columnIndex))/mcm.getSample().getDisp()[rowIndex]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmSampleCorelationTable = new JTable(mcmSampleCorelationTableModel);
		mcmTabs.add(new JScrollPane(mcmSampleCorelationTable),"Sample Corelation",1);
		
		mcmADeltaTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
//				else return mcm.getCompCount()+1;
				else return mcm.getSample().getDimension()+1;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex < mcm.getSample().getDimension()) return "z"+String.valueOf(columnIndex);
				else return "delta";
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex < mcm.getSample().getDimension()) return mcm.getA().get(columnIndex)[rowIndex];
				else return mcm.getDelta().get(rowIndex);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmADeltaTable = new JTable(mcmADeltaTableModel);
		
		mcmLambdaTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension()+1;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				if (columnIndex < mcm.getSample().getDimension()) return "lambda"+String.valueOf(columnIndex);
				else return "sum";
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return 2;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				if (columnIndex < mcm.getSample().getDimension()) {
					if (rowIndex == 0) return mcm.getEignValue().get(columnIndex);
					else return new Double((mcm.getEignValue().get(columnIndex).doubleValue()/mcm.getSample().getDimension())*100);
				}
				else {
					double sum = 0;
					for(int j=0; j < mcm.getSample().getDimension(); j++) sum += mcm.getEignValue().get(j).doubleValue();
					if (rowIndex == 0) return new Double(sum);
					else return new Double((sum/mcm.getSample().getDimension())*100);
				}
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmLambdaTable = new JTable(mcmLambdaTableModel);
		
		JPanel mainCompPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL},{0.50,0.50}}));
		mainCompPanel.add(new JScrollPane(mcmADeltaTable),"0, 0");
		mainCompPanel.add(new JScrollPane(mcmLambdaTable),"0, 1");
		mcmTabs.add(mainCompPanel, "A and Lambda",2);
		
		mcmNewSampleCorelationTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return "z"+String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return mcm.getSample().getDimension();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Double(mcm.getA().get(columnIndex)[rowIndex]*Math.sqrt(mcm.getEignValue().get(rowIndex).doubleValue()));
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmNewSampleCorelationTable = new JTable(mcmNewSampleCorelationTableModel);
		mcmTabs.add(new JScrollPane(mcmNewSampleCorelationTable),"Corelation Old vs New",3);
		
		mcmNewComponentsTableModel = new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (mcm == null) return 0;
				else return mcm.getCompCount();
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				return "z"+String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
				if (mcm == null) return 0;
				else return mcm.getZ().size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				return new Double(mcm.getZ().get(rowIndex)[columnIndex]);
			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		mcmNewComponentsTable = new JTable(mcmNewComponentsTableModel);
		mcmTabs.add(new JScrollPane(mcmNewComponentsTable),"New Sample",4);
		
		JPanel mcmButtonsPanel = new JPanel(new FlowLayout());
		mcmMinimizeAllButton = new JButton(" All ");
		mcmMinimizeAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mcm.minimizeAll();
				mcmSampleTable.tableChanged(null);
				mcmSampleCorelationTable.tableChanged(null);
				mcmADeltaTable.tableChanged(null);
				mcmLambdaTable.tableChanged(null);
				mcmNewSampleCorelationTable.tableChanged(null);
				mcmNewComponentsTable.tableChanged(null);
			}
		});
		mcmMinimize75Button = new JButton(" >= 75% of dispersion ");
		mcmMinimize75Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mcm.minimize75();
				mcmSampleTable.tableChanged(null);
				mcmSampleCorelationTable.tableChanged(null);
				mcmADeltaTable.tableChanged(null);
				mcmLambdaTable.tableChanged(null);
				mcmNewSampleCorelationTable.tableChanged(null);
				mcmNewComponentsTable.tableChanged(null);
			}
		});
		mcmMinimizeByEignValueButton = new JButton(" By eign value ");
		mcmMinimizeByEignValueButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				double t = Double.parseDouble(mcmMinimizeEignValueTextField.getText());
				mcm.minimizeByThreshold(t);
				mcmSampleTable.tableChanged(null);
				mcmSampleCorelationTable.tableChanged(null);
				mcmADeltaTable.tableChanged(null);
				mcmLambdaTable.tableChanged(null);
				mcmNewSampleCorelationTable.tableChanged(null);
				mcmNewComponentsTable.tableChanged(null);
			}
		});
		mcmMinimizeEignValueTextField = new JTextField(" 1.0 ");
		mcmButtonsPanel.add(mcmMinimizeAllButton);
		mcmButtonsPanel.add(mcmMinimize75Button);
		mcmButtonsPanel.add(mcmMinimizeByEignValueButton);
		mcmButtonsPanel.add(mcmMinimizeEignValueTextField);
		
		JPanel mcmPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL},{TableLayout.PREFERRED,TableLayout.FILL}}));
		mcmPanel.add(mcmButtonsPanel, "0, 0");
		mcmPanel.add(mcmTabs, "0, 1");
		
		tabs.add(mcmPanel, "Main Components Method", 2);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);
	}

}
