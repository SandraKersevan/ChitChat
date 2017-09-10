import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URISyntaxException;

import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class ChatFrame extends JFrame implements ActionListener, KeyListener {
	
	private JTextArea output;
	private JTextField input;
	public JTextField userName;
	private USER u;
	
	boolean prijava;
		
	public ChatFrame() {
		super();
		setTitle("ChitChat");
		Container pane = this.getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gridBagLayout.columnWeights = new double[]{0.0};
		pane.setLayout(gridBagLayout);

		this.output = new JTextArea(20, 40);
		this.output.setEditable(false);
		GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.insets = new Insets(0, 0, 5, 0);
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 1;
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weightx = 1;
		outputConstraint.weighty = 1;
		
		JScrollPane scrollpane = new JScrollPane(this.output,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.add(scrollpane, outputConstraint);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 2;
		getContentPane().add(panel_2, gbc_panel_2);
		
		JRadioButton rdbtnJavno = new JRadioButton("JAVNO");
		rdbtnJavno.setSelected(true);
		panel_2.add(rdbtnJavno);
		
		JRadioButton rdbtnPrivat = new JRadioButton("PRIVAT");
		panel_2.add(rdbtnPrivat);
		
		JTextField privatUporabnik = new JTextField();
		privatUporabnik.setEnabled(false);
		panel_2.add(privatUporabnik);
		privatUporabnik.setColumns(10);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.SOUTH;
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		getContentPane().add(panel_1, gbc_panel_1);
		
		JLabel lblSporocilo = new JLabel("Sporoèilo:");
		panel_1.add(lblSporocilo);
		
		this.input = new JTextField(40);
		panel_1.add(input);
		
		JButton btnPoslji = new JButton("Pošlji");
		btnPoslji.setEnabled(false);
		panel_1.add(btnPoslji);
		
		input.addKeyListener(this);		
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lblUporabniskoIme = new JLabel("Uporabniško ime:");
		
		this.userName = new JTextField(System.getProperty("user.name"), 20);
		this.u = new USER(this, userName.getText().trim(), prijava);
		this.u.activate();
		this.u.run();
		
		panel.add(lblUporabniskoIme);
		panel.add(userName);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.insets = new Insets(0, 0, 5, 0);
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		pane.add(panel, constraints);
		
		JButton btnPrijava = new JButton("Prijava");
		panel.add(btnPrijava);
		
		JButton btnOdjava = new JButton("Odjava");
		btnOdjava.setEnabled(false);
		panel.add(btnOdjava);
		
		JButton btnPrisotni = new JButton("Prisotni");
		btnPrisotni.setEnabled(false);
		panel.add(btnPrisotni);
		
		// gumb Prijava kljiknjen
		btnPrijava.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					u.user = userName.getText();
					u.logIn(userName.getText().trim());
				} catch (URISyntaxException e) {
					e.printStackTrace();
					System.out.println("Prišlo je do napake ob kliku na gumb Prijava.");
				}
				if (u.prijava){
					btnOdjava.setEnabled(true);
					btnPrisotni.setEnabled(true);
					btnPrijava.setEnabled(false);
					btnPoslji.setEnabled(true);
				}
			}
		});
		
		// gumb Odjava kliknjen
		btnOdjava.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					u.logOut(userName.getText().trim());
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
					System.out.println("Prišlo je do napake ob kliku na gumb Odjava.");
				}
				if (!u.prijava) {
					btnOdjava.setEnabled(false);
					btnPrisotni.setEnabled(false);
					btnPrijava.setEnabled(true);
					btnPoslji.setEnabled(false);
				}
			}
		});
		
		// gumb Prisotni kliknjen
		btnPrisotni.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				u.getUsers();
			}
		});
		
		// javno sporoèilo
		rdbtnJavno.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rdbtnPrivat.setSelected(false);
				rdbtnJavno.setSelected(true);
				privatUporabnik.setEnabled(false);
				privatUporabnik.setText("");
			}
		});
				
		// privat sporoèilo
		rdbtnPrivat.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				rdbtnJavno.setSelected(false);
				privatUporabnik.setEnabled(true);
				rdbtnPrivat.setSelected(true);
			}
		});
		
		// poslji sporoèilo
		btnPoslji.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(rdbtnJavno.isSelected()) {
					try {
						u.sendMessage(userName.getText().trim(), "true", escapeForJava(input.getText().trim(), false), "");
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
						System.out.println("Prišlo je do napake pri pošiljanju javnega sporoèila.");
					} 
				} else {
					try {
						u.sendMessage(userName.getText().trim(), "false", escapeForJava(input.getText().trim(), false), privatUporabnik.getText().trim());
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
						System.out.println("Prišlo je do napake pri pošiljanju privat sporoèila.");
					} 
				} 
				input.setText("");
			}
		});
	}

	/**
	 * @param person - the person sending the message
	 * @param message - the message content
	 */
	public void addMessage(String person, String message) {
		String chat = this.output.getText();
		if ((person == "") && (message.length() > 2)) {
			this.output.setText(chat + message + "\n");
		} if ((person != "") && (message.length() > 2)) {
			this.output.setText(chat + person + ": " + message + "\n");
		} 
	}
	
	// funkcija za popravo besedila, ce to vsebuje posebne znake kot so ", \n, ...
	public static String escapeForJava( String value, boolean quote ) {
	    StringBuilder builder = new StringBuilder();
	    if( quote )
	        builder.append( "\"" );
	    for( char c : value.toCharArray() ) {
	    	 if( c == '\'' ) {
	             builder.append( "\\'" );
	    	 } else if ( c == '\"' ) {
	             builder.append( "\\\"" );
	    	 } else if( c == '\r' ) {
	             builder.append( "\\r" );
	    	 } else if( c == '\n' ) {
	             builder.append( "\\n" );
	    	 } else if( c == '\t' ) {
	             builder.append( "\\t" );
	    	 } else {
	            builder.append( c );
	        }
	    }
	    if( quote )
	        builder.append( "\"" );
	    return builder.toString();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getSource() == this.input) {
			if (e.getKeyChar() == '\n') {
				this.addMessage(userName.getText(), this.input.getText());
				this.input.setText("");
			}
		}		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
