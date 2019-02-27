import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import es.experis.racingdrones.RacingKeyListener;
import es.experis.racingdrones.RacingKeyListener.WriteListener;
import jssc.SerialPortList;

/**
 * Clase Ventana
 * Recibe el puerto en el que se ha conectado el arduino y muestra los comandos pulsados
 * @author Julia Gómez
 */
public class Ventana extends JFrame implements WriteListener {

	private final static Logger LOGGER = Logger.getLogger(Ventana.class.getName());
	
    private JTextArea textoComando;           // etiqueta o texto no editable
    private static RacingKeyListener app;
    
    public Ventana() {
        super();                    // usamos el contructor de la clase padre JFrame
        configurarVentana();        // configuramos la ventana
        inicializarComponentes();   // inicializamos los atributos o componentes
        
        app = new RacingKeyListener();
        app.addListener(this);
    }

    private void configurarVentana() {
    	this.setName("racingdrones");
        this.setTitle("racingdrones");  // colocamos titulo a la ventana
        this.setSize(200, 160);                                 // colocamos tamanio a la ventana (ancho, alto)
        this.setLocationRelativeTo(null);                       // centramos la ventana en la pantalla
        this.setLayout(null);                                   // no usamos ningun layout, solo asi podremos dar posiciones a los componentes
        this.setResizable(false);                               // hacemos que la ventana no sea redimiensionable
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    // hacemos que cuando se cierre la ventana termina todo proceso
    }

    private void inicializarComponentes() {
        // creamos los componentes
    	JLabel texto = new JLabel();
        textoComando = new JTextArea();
        // configuramos los componentes
        
        ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource("resources/W.png"));  
        texto.setIcon(icon);  
        texto.setBounds(65, 30, 30, 30);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        
        JLabel textoA = new JLabel();
        icon = new ImageIcon(getClass().getClassLoader().getResource("resources/A.png"));  
        textoA.setIcon(icon);  
        textoA.setBounds(50, 60, 30, 30);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        
        JLabel textoS = new JLabel();
        icon = new ImageIcon(getClass().getClassLoader().getResource("resources/S.png"));  
        textoS.setIcon(icon);  
        textoS.setBounds(82, 60, 30, 30);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        
        JLabel textoD = new JLabel();
        icon = new ImageIcon(getClass().getClassLoader().getResource("resources/D.png"));  
        textoD.setIcon(icon);  
        textoD.setBounds(114, 60, 30, 30);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        
        textoComando.setBounds(10, 80, 170, 45);   // colocamos posicion y tamanio al texto (x, y, ancho, alto)
        textoComando.setOpaque(false);
        // adicionamos los componentes a la ventana
        this.add(texto);
        this.add(textoS);
        this.add(textoA);
        this.add(textoD);
   //     this.add(textoComando);
        
    }

    public static void main(String[] args) {
        Ventana V = new Ventana();      // creamos una ventana
        V.setVisible(true);             // hacemos visible la ventana creada
        System.out.println("Ventana.java: Vamos a init() con id: "+args[0]);
        app.init(Integer.parseInt(args[0]));
        
    }

	@Override
	public void writeToPortListener(String msg) {
		String txtCommand = textoComando.getText();
		txtCommand = msg + "\n"+txtCommand;
		textoComando.setText(txtCommand);
		
	}
    
    
}