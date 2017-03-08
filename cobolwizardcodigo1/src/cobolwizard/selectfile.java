package cobolwizard;

import java.io.File;

import  javax.swing.*;

import com.jtechlabs.ui.widget.directorychooser.*;
public class selectfile extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public selectfile() {
        initComponents(); }

   private void initComponents() {
		setTitle("Fortify Cobol Wizard");
		setResizable(false);
		File f = new File("C:\\");
		jdirectorioFuente = new JDirectoryChooser();
		jdirectorioFuente.setSelectedDirectory(f);
		jdirectorioCopies = new JDirectoryChooser();
		jPanelroot = new  JPanel();
		jPanelcopies = new  JPanel();
		jPanelproyecto = new  JPanel();
		jPanelparametros = new  JPanel();
        jtfRutaEntrada = new  JTextField();
        jtfRutaEntradaC = new  JTextField();
        jtfproyecto = new  JTextField();
        jbFuente = new  JButton();
        jbCopies = new  JButton();
        jbNext = new  JButton();
        jbCancel = new  JButton();
        jchknoextension = new JCheckBox();
        jchkdebug = new JCheckBox();
        jchkmemoria = new JCheckBox();
        jchkfixmode = new JCheckBox();
        jcbmemoria=new JComboBox<String>();
        
        /*Declaramos el proyecto*/ 
        jPanelproyecto.setBorder( BorderFactory.createTitledBorder("Escribe el nombre del proyecto"));
        jPanelproyecto.setLayout(null);
        jPanelproyecto.add(jtfproyecto);
        jtfproyecto.setBounds(20, 20,300, 20);
        getContentPane().add(jPanelproyecto);
        jPanelproyecto.setBounds(30, 30, 340, 60);
        
        /*Declaramos los parametros*/ 
        jPanelparametros.setBorder( BorderFactory.createTitledBorder("Especifica los parametros"));
        jPanelparametros.setLayout(null);
        jchknoextension.setText("noextension-type");
        this.jPanelparametros.add(this.jchknoextension);
        jchknoextension.setBounds(20,20, 200, 20);
        jchkdebug.setText("Debug Mode");
        this.jPanelparametros.add(this.jchkdebug);
        jchkdebug.setBounds(20,50, 200, 20);
        jchkfixmode.setText("fixmode");
        this.jPanelparametros.add(this.jchkfixmode);
        jchkfixmode.setBounds(20,80, 200, 20);
        jchkmemoria.setText("Memoria RAM");
        this.jPanelparametros.add(this.jchkmemoria);
        jchkmemoria.setBounds(20,110, 200, 20);
        jcbmemoria.addItem("512M");
        jcbmemoria.addItem("1024M");
        jcbmemoria.addItem("2048M");
        jcbmemoria.addItem("4096M");
        this.jPanelparametros.add(this.jcbmemoria);
        jcbmemoria.setBounds(240,110,80,20);
        jcbmemoria.setEnabled(false);
        getContentPane().add(jPanelparametros);
        jPanelparametros.setBounds(30, 100, 340, 150);
        
        /*DEfinimos el directorio root*/
        jPanelroot.setBorder( BorderFactory.createTitledBorder("Ubica el directoio del Codigo Fuente"));
        jPanelroot.setLayout(null);
        jPanelroot.add(jtfRutaEntrada);
        jtfRutaEntrada.setBounds(20, 30, 240, 20);
        jtfRutaEntrada.setEditable(false);
        jbFuente.setText("...");
        jPanelroot.add(jbFuente);
        jbFuente.setBounds(270, 30, 50, 20);
        getContentPane().add(jPanelroot);
        jPanelroot.setBounds(30, 260, 340, 70);
        
        /*Definimos el directorio de copybooks*/
        jPanelcopies.setBorder( BorderFactory.createTitledBorder("Ubica el directoio de los copybooks"));
        jPanelcopies.setLayout(null);
        jPanelcopies.add(jtfRutaEntradaC);
        jtfRutaEntradaC.setBounds(20, 30, 240, 20);
        jtfRutaEntradaC.setEditable(false);
        jbCopies.setText("...");
        jPanelcopies.add(jbCopies);
        jbCopies.setBounds(270, 30, 50, 20);
        getContentPane().add(jPanelcopies);
        jPanelcopies.setBounds(30, 340, 340, 70);
    
        /*Agregamos bton de crear y boton de cancelar*/
        getContentPane().add(jbNext);
        jbNext.setText("Crear");
        jbNext.setBounds(280,430,90,20);
        getContentPane().add(jbCancel);
        jbCancel.setText("Cancelar");
        jbCancel.setBounds(180,430,90,20);
        
        /*Definimos la ubiación de nuestra ventana */
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-400)/2, (screenSize.height-500)/2, 400, 500);
        
        /* Declaramos que nuestra ventana se cierre al dar clic en X*/
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(null);
        

		/*declaramos una referencia a nuestra clase control de eventos*/
		ControlaEventos controlaEventos =new ControlaEventos (this);
		jchknoextension.addActionListener(controlaEventos);
		jchkdebug.addActionListener(controlaEventos);
		jchkfixmode.addActionListener(controlaEventos);
		jchkmemoria.addActionListener(controlaEventos);
		jcbmemoria.addActionListener(controlaEventos);
		jbFuente.addActionListener(controlaEventos);
		jbCancel.addActionListener(controlaEventos);
		jbNext.addActionListener(controlaEventos);
		jbCopies.addActionListener(controlaEventos);
    }


	//public JFileChooser jfcExaminarEntrada;
	public JDirectoryChooser jdirectorioFuente;
	public JDirectoryChooser jdirectorioCopies;
    public JButton jbFuente;
    public JButton jbCopies;
    public JButton jbNext;
    public JButton jbCancel;
    public JPanel jPanelroot;
    public JPanel jPanelparametros;
    public JPanel jPanelcopies;
    public JPanel jPanelproyecto;
    public JTextField jtfRutaEntrada;
    public JTextField jtfRutaEntradaC;
    public JTextField jtfproyecto;
    public JCheckBox jchkmemoria;
    public JCheckBox jchknoextension;
    public JCheckBox jchkdebug;
    public JCheckBox jchkfixmode;
    public JComboBox<String> jcbmemoria;
    public Boolean noextension=false;
    public Boolean fixmode=false;
    public Boolean debug=false;
    public String memory="";
    
}
