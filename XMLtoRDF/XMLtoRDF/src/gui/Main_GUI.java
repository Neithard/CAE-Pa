package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import transformation.RDFforGood;

public class Main_GUI {

	private JFrame frame;
	private JPanel contentPane;
	private JTextField label_pathfile;
	
	private String source_file;
	private String destination_file;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		if(args.length>0){
			String source_temp=args[0];
			String destination_temp=args[1]; 
			if(source_temp.contains("-")&&source_temp.contains(".xml")&&destination_temp.contains("-")&&destination_temp.contains(".rdf")) {
				new RDFforGood(source_temp.substring(1, source_temp.length()), destination_temp.substring(1,destination_temp.length())).transformation();
				System.out.println("transformation successful");
			}	
			
		}
		else {		
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Main_GUI window = new Main_GUI();
						window.frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	/**
	 * Create the application.
	 */
	public Main_GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBackground(Color.WHITE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 500, 220);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(240, 248, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("XMLtoRDF converter tool");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		final JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(10)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(386, Short.MAX_VALUE))
		);
		panel.setLayout(null);
		
		final JLabel label_text = new JLabel("Step 1 of 3: Choose XML file");
		label_text.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label_text.setBounds(10, 11, 312, 14);
		panel.add(label_text);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.LIGHT_GRAY);
		separator.setBackground(Color.LIGHT_GRAY);
		separator.setBounds(10, 28, 393, 1);
		panel.add(separator);
		
		final JLabel label_file = new JLabel("     Source file:");
		label_file.setFont(new Font("Tahoma", Font.PLAIN, 11));
		label_file.setBounds(10, 57, 83, 17);
		panel.add(label_file);
		
		final JLabel lblSuccessful = new JLabel("transformation successful");
		lblSuccessful.setForeground(Color.GREEN);
		lblSuccessful.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblSuccessful.setBounds(37, 46, 299, 28);
		panel.add(lblSuccessful);
		lblSuccessful.setVisible(false);
		
		
		
		label_pathfile = new JTextField();
		label_pathfile.setBounds(93, 55, 243, 20);
		panel.add(label_pathfile);
		label_pathfile.setColumns(10);
		
		final JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				
				if(label_file.getText().contains("Source")){
					label_pathfile.setText(holePfad("xml"));
				}
				else {
					label_pathfile.setText(holePfad("rdf"));
				}
				
			}
		});
		btnBrowse.setBounds(346, 54, 89, 23);
		panel.add(btnBrowse);
		
		final JButton btnconvert = new JButton("convert");
		btnconvert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				RDFforGood trans = new RDFforGood(source_file, destination_file);
				
				if(!trans.transformation()){
					lblSuccessful.setText("transformation failed");
					lblSuccessful.setBackground(Color.RED);
				}
				
				btnconvert.setVisible(false);
				lblSuccessful.setVisible(true);
			}
		});
		btnconvert.setForeground(Color.DARK_GRAY);
		btnconvert.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnconvert.setBounds(253, 85, 191, 37);
		btnconvert.setVisible(false);
		panel.add(btnconvert);
		contentPane.setLayout(gl_contentPane);
		
		final JButton btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if(label_file.getText().contains("Source")){
					//Sicherungsabfrage
					if(true){//label_pathfile.getText().contains(".xml")) {
						source_file = label_pathfile.getText();
						label_file.setText("Destination file");
						label_text.setText("Step 2 of 3: Choose destination file");
						label_pathfile.setText("");
					}
					else {
						JOptionPane.showMessageDialog(null, "Keine Datei ausgewählt" , "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else if (label_file.getText().contains("Destination")) {
					//Sicherungsabfrage
					if(true) { //label_pathfile.getText().contains(".rdf")) {
						destination_file = label_pathfile.getText();
						label_pathfile.setVisible(false);
						label_file.setVisible(false);
						btnBrowse.setVisible(false);
						label_text.setText("Step 3 of 3: Start transformation");
						btnNext.setVisible(false);
						btnconvert.setVisible(true);
					}
					else {
						JOptionPane.showMessageDialog(null, "Keine Datei ausgewählt" , "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				
			}
		});

		btnNext.setBounds(369, 99, 75, 23);
		panel.add(btnNext);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		lblNewLabel_1.setBounds(47, 36, 46, 14);
		
	}
	
	public static String holePfad(final String extension) {
		 
        String pfad = "";
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory()
                        || f.getName().toLowerCase().endsWith("." + extension);
            }
 
            @Override
            public String getDescription() {
                return "." + extension;
            }
        });
 
        final String DIALOG_TITLE_CHANGED_PROPERTY = "XMLtoRDF - filechooser";
        fc.setDialogTitle(DIALOG_TITLE_CHANGED_PROPERTY);
 
        int state = fc.showOpenDialog(null);
 
        if (state == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            pfad = file.getAbsolutePath();
        }
 
        return pfad;
    }
	
}
