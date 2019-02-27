package es.experis.racingdrones;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class RacingKeyListener implements NativeKeyListener {
	
	private final static Logger LOGGER = Logger.getLogger(RacingKeyListener.class.getName());
	private final long TEMP_MILLIS = 1000;
	
	static SerialPort serialPort = null;

	static AtomicInteger acceleration = new AtomicInteger(1500);
	static AtomicInteger turn = new AtomicInteger(1500);

	private WriteListener listener;
	
	private long millis = 0;

	public void addListener(WriteListener toAdd) {
		listener = toAdd;
	}

	// An interface to be implemented by everyone interested in "Hello" events
	public interface WriteListener {
		void writeToPortListener(String msg);
	}

	public RacingKeyListener() {

	}

	public void init(int choice) {
		
		millis = System.currentTimeMillis();
		
		LOGGER.info("RacingKeyListener.java ENTERING init("+choice+")");
		String[] portNames = SerialPortList.getPortNames();
		
		LOGGER.info("RacingKeyListener.java Número de puertos encontrados: " + portNames.length);
				
		
		for (int i = 0; i < portNames.length; i++) {
			
			System.out.println((i + 1) + ". " + portNames[i]);
		}
		
		if (choice > 0 && choice <= portNames.length) {
			serialPort = new SerialPort(portNames[(choice - 1)]);

			try {
				serialPort.openPort();
				serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);// Set params. Also you can set params by this string:
												// serialPort.setParams(9600, 8, 1, 0);
				serialPort.addEventListener(new SerialPortReader());//Add SerialPortEventListener
				try {

					Logger l0 = Logger.getLogger("");
					l0.removeHandler(l0.getHandlers()[0]);

					GlobalScreen.registerNativeHook();

				} catch (NativeHookException ex) {

					System.exit(1);
				}

				GlobalScreen.addNativeKeyListener(this);

			} catch (SerialPortException e) {
				LOGGER.info(e.getMessage());
				
			} // Open serial port
		}
	}

	public void nativeKeyPressed(NativeKeyEvent e) {

		Integer oldValueAccel = acceleration.get();
		Integer oldValueTurn = turn.get();
		LOGGER.info("Key Pressed: " + Calendar.getInstance() + " ----- " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		
		// w
		if (e.getKeyCode() == 17) {
			acceleration.set(1550);
		} // s
		else if (e.getKeyCode() == 31) {

			if (acceleration.get() > 1500) {
				acceleration.set(1500);

				try {
					writeToPort(getPortValueToSend());
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}

			}
			acceleration.set(1400);

		} // a
		else if (e.getKeyCode() == 30) {
			turn.set(2000);
		} // d
		else if (e.getKeyCode() == 32) {
			turn.set(1000);
		}

		//if (oldValueAccel != acceleration.get() || oldValueTurn != turn.get() || (System.currentTimeMillis() - millis) > TEMP_MILLIS) {
		if (oldValueAccel != acceleration.get() || oldValueTurn != turn.get()) {
			//		millis = System.currentTimeMillis();
			try {
				writeToPort(getPortValueToSend());
			} catch (SerialPortException e1) {
				//e1.printStackTrace();
				LOGGER.info(e1.getMessage());
			}
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {

		LOGGER.info("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
		Integer oldValueAccel = acceleration.get();
		Integer oldValueTurn = turn.get();

		// w
		if (e.getKeyCode() == 17) {
			acceleration.set(1500);
		} // s
		else if (e.getKeyCode() == 31) {
			acceleration.set(1500);
		} // a
		else if (e.getKeyCode() == 30) {
			turn.set(1500);
		} // d
		else if (e.getKeyCode() == 32) {
			turn.set(1500);
		}

		if (oldValueAccel != acceleration.get() || oldValueTurn != turn.get()) {
		//if (oldValueAccel != acceleration.get() || oldValueTurn != turn.get() || (System.currentTimeMillis() - millis) > TEMP_MILLIS) {
			//millis = System.currentTimeMillis();
			try {
				writeToPort(getPortValueToSend());
			} catch (SerialPortException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		LOGGER.info("Key Typed: " + e.getKeyText(e.getKeyCode()));
		
	}

	private void writeToPort(String msg) throws SerialPortException {
		LOGGER.info("Send to port: " + msg);
		// Notify everybody that may be interested.
		listener.writeToPortListener(msg);
		serialPort.writeBytes(msg.getBytes());// Write data to port
		
	}

	
	private void closePort() throws SerialPortException {
		serialPort.closePort();// Close serial port
	}

	private String getPortValueToSend() {
		return acceleration.get() + "," + turn.get() + "\n";
	}
	
	static class SerialPortReader implements SerialPortEventListener {

        public void serialEvent(SerialPortEvent event) {
            if (event.isRXCHAR()) {//If data is available
                //System.out.println(event.getEventValue());
                if (event.getEventValue() > 16) {//Check bytes count in the input buffer

                    //Read data, if 10 bytes available
                    try {
                        byte buffer[] = serialPort.readBytes(16);

                        
                        System.out.println(new String( buffer, Charset.forName("UTF-8") ));
                        
                    } catch (SerialPortException ex) {
                        System.out.println(ex);
                    }
                }
            } else if (event.isCTS()) {//If CTS line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("CTS - ON");
                } else {
                    System.out.println("CTS - OFF");
                }
            } else if (event.isDSR()) {///If DSR line has changed state
                if (event.getEventValue() == 1) {//If line is ON
                    System.out.println("DSR - ON");
                } else {
                    System.out.println("DSR - OFF");
                }
            }
        }
    }
}
