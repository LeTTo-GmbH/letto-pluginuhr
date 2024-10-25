package at.letto.tools.threads;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import at.letto.ServerConfiguration;
import at.letto.tools.ServerStatus;
import at.letto.tools.logging.EasyLeTToLogger;

public class TimerCall {

	private static EasyLeTToLogger logger = new EasyLeTToLogger();

	private static volatile int logCounter=0;

	public static synchronized int incrementLogCounter() {
		logCounter += 1;
		return logCounter;
	}

	public static void setLeTToLogger(EasyLeTToLogger easyLeTToLogger) {
		if (easyLeTToLogger != null)
			logger = easyLeTToLogger;
	}

	/**
	 * Status des Ergebnisses eines Calls<br>
	 * OK alles hat funktioniert<br>
	 * RUNTIMEEXCEPTION Es hat eine RuntimeException gegeben<br>
	 * EXCEPTION Es hat eine Exception gegeben<br>
	 * ERROR Es hat einen Error gegeben<br>
	 * TIMEOUT Es hat ein Timeout gegeben<br>
	 * TIMEOUTKILLED Es hat ein Timeout gegeben bei dem der Thread nicht korrekt beendet werden konnte, weshalb er interrupted wurde
	 * ZOMBIE   Es hat ein Timeout gegeben bei dem der Thread nicht korrekt beendet werden konnte. Da der Thread nicht interrupted werden konnte bleibt er als Zombie bestehen.
	 * @author Werner Damböck
	 *
	 */
	public static enum RESULT{OK,RUNTIMEEXCEPTION,EXCEPTION,ERROR,TIMEOUT,TIMEOUTKILLED, ZOMBIE;}

	public static boolean resetDebugMode = false;

	public static class CallResult{
		public final RESULT status;
		private final Object result;
		public CallResult(RESULT status) {
			this.status = status;
			result = null;
		}	
		public CallResult(RESULT status, Object result) {
			this.status = status;
			this.result = result;
		}	
		public RuntimeException getRuntimeException() { 
			if (status==RESULT.RUNTIMEEXCEPTION && result instanceof RuntimeException) 
				return (RuntimeException)result; 
			else return null;
		}
		public Exception getException() { 
			if (status==RESULT.EXCEPTION && result instanceof Exception) 
				return (Exception)result; 
			else return null;
		}		
		public Error getError() { 
			if (status==RESULT.ERROR && result instanceof Error) 
				return (Error)result; 
			else return null;
		}
		public Object getResult() { 
			if (status==RESULT.OK) return result; 
			else           return null;
		}
		public RESULT getStatus() { 
			return status;
		}
	}
	
	/**
	 * Variable welche für die Ergebnisrückgabe vom Testthread verwendet wird
	 */
	private volatile CallResult ret;
	/**
	 * Variable welche nach dem Testerthread true ist wenn ein Timeout aufgetreten ist
	 */
	private volatile boolean timeout;
	/**
	 * Timeout in ms
	 */
	private final int MAXTIME;
	/**
	 * Parameterlist für den Methodenaufruf
	 */
	private Object parameter[];
	
	private TimerCall(int timeout) {
		MAXTIME = timeout;
	}
	
	/**
	 * Methode gegen einen Timer ausführen<br>
	 * Im Debugging-Modus wird der Timeout auf einen extrem hohen Wert gesetzt, damit kein Timeout auftritt!!
	 * Ist timeoutms 0, so wird die Methode ohne Timer ausgeführt<br>
	 * @param methode   Methode über das CallInterface definiert
	 * @param timeoutms Timeout in ms
	 * @param objects   Parameter
	 * @return          Ergebnis des Methodenaufrufes 
	 */
	public static CallResult callMethode(CallInterface methode, int timeoutms, Object ... objects) {
		if (ServerStatus.isDebug && !resetDebugMode) if (LettoTimer.getDebugTimer()>0) timeoutms = LettoTimer.getDebugTimer();
		return callMethodeNoDebug(methode, timeoutms, objects);
	}

	/**
	 * Methode gegen einen Timer ausführen<br>
	 * Ist timeoutms 0, so wird die Methode ohne Timer ausgeführt<br>
	 * @param methode   Methode über das CallInterface definiert
	 * @param timeoutms Timeout in ms
	 * @param objects   Parameter
	 * @return          Ergebnis des Methodenaufrufes
	 */
	public static CallResult callMethodeNoDebug(CallInterface methode, int timeoutms, Object ... objects) {
		if (timeoutms < 1 ) {
			int ct = incrementLogCounter();
			logger.logMessage("START,CT="+ct+",ms=0,M="+methode.getMethodeName());
			CallResult ret;
			try {
				Object r = methode.callMethode(objects);
				ret = new CallResult(RESULT.OK,r);
				logger.logMessage("OK   ,CT="+ct+",M="+methode.getMethodeName());
			} catch (RuntimeException e) {
				logger.logMessage("RT-Ex,CT="+ct+",M="+methode.getMethodeName());
				ret = new CallResult(RESULT.RUNTIMEEXCEPTION,e);
			} catch (Exception e) {
				logger.logMessage("Excep,CT="+ct+",M="+methode.getMethodeName());
				ret = new CallResult(RESULT.EXCEPTION,e);
			} catch (LettoTimeoutException e) {
				logger.logMessage("TimeO,CT="+ct+",M="+methode.getMethodeName());
				throw e;
			} catch (Error e) {
				logger.logMessage("Error,CT="+ct+",M="+methode.getMethodeName());
				ret = new CallResult(RESULT.ERROR,e);
			}
			return ret;
		} else {
			TimerCall tc = new TimerCall(timeoutms);
			tc.parameter = objects;
			CallResult ret = tc.callmethod(methode);
			return ret;
		}
	}

	private CallResult callmethod(CallInterface methode) {
		int ct = incrementLogCounter();
		logger.logMessage("START,CT="+ct+",ms="+this.MAXTIME+",M="+methode.getMethodeName());
		CallResult result;
		Thread killthread=null;
		timeout=true;
		ret=null;
		// Thread für den Timer definieren
		final Thread timerthread = Thread.currentThread();	
		try {
			// Thread für die Berechnung definieren
			final Thread testerthread = new Thread() {
				@Override
				public void run() {
					try {
						Object r = methode.callMethode(parameter);
						ret = new CallResult(RESULT.OK,r);
					} catch (RuntimeException e) {
						ret = new CallResult(RESULT.RUNTIMEEXCEPTION,e);
					} catch (Exception e) {
						ret = new CallResult(RESULT.EXCEPTION,e);
					} catch (Error e) {
						ret = new CallResult(RESULT.ERROR,e);
					}
					timeout=false;
					// Wenn der Thread ordnungsgemäß terminiert hat dann beende auch den Timerthread. Dies funktioniert immer problemlos.
					timerthread.interrupt();
				}
			};	
			// Setze den Thread als Daemon, damit der Thread mit dem Haupttask beendet wird!
			testerthread.setDaemon(true);
			// Merke den Thread als killthread, um ihn dann auch beenden zu können
			killthread = testerthread;
			// Thread starten
			testerthread.start();
			// Timer starten in ms
			Thread.sleep(MAXTIME);
		} catch (InterruptedException e) { }
						
		// Wenn ein Timeout aufgetreten ist setze das Ergebnis auf ein Timeout Ergebnis
		if (timeout) {
			result = new CallResult(RESULT.TIMEOUT);
			logger.logMessage("TIMEOUT,CT="+ct+",M="+methode.getMethodeName());
		} else result=ret;
		// Bereinigen der Threads
		try {
			if (timeout && killthread!=null && killthread.isAlive()) {
				logger.logMessage("KILL ,CT="+ct+",M="+methode.getMethodeName());
				// Wenn der Timer abgelaufen ist, dann beende den gestarteten Thread(killerthread)  
				// killthread.stop();
				// versuche den Thread zu unterbrechen
				killthread.interrupt();
				// Warte maximal getInterruptTimer() Millisekunden bis der Thread durch interrupt() korrekt beendet wurde
				killthread.join(LettoTimer.getInterruptTimer()); 
				// Schieße den Thread brutal ab wenn er noch immer läuft
				if (killthread.isAlive()) {
					logger.logMessage("ZOMBIE ,CT="+ct+",M="+methode.getMethodeName());
					//Log.Msg1("Thread wurde nach einem Timeout brutal abgeschossen!");
					// killthread.stop();
					result = new CallResult(RESULT.ZOMBIE);
				} else {
					logger.logMessage("KILLED,CT="+ct+",M="+methode.getMethodeName());
					result = new CallResult(RESULT.TIMEOUTKILLED);
				}
			}
		} catch (Error e) {}	
		catch (Exception e) {}
		logger.logMessage(result.getStatus().toString()+",CT="+ct+",M="+methode.getMethodeName());
		return result;
	}

	/**
	 * Ausführen einer beliebigen Objektmethode innerhalb eines Timers
	 * @param handler	Objekt, von dem Methode ausgeführt werden soll
	 * @param timeoutms Timeout in ms. Ist timeoutms=0, so ist der Timer deaktiviert.
	 * @param methodName	Methode, die m Timer ausgeführt werden soll
	 * @param obj       Parameter
	 * @return			Rückgabe der Methode oder null, wenn Fehler aufgetreten
	 */
	public static CallResult exec(Object handler, int timeoutms, String methodName, Object ... obj) {
		CallResult ret = TimerCall.callMethode(new CallAdapter() {
			Object handler=null;
			String methodName="";
			Object[] params = new Object[0];
			@Override
			public Object callMethode(Object ... objects) {
				handler = objects[0];
				methodName = (String) objects[1];
				params = (Object[]) objects[2];
				
				if (handler == null) return null;
				
				// Aufruf der Methode für diesen View
				try {
					Method method= null;
					if (params != null && params.length > 0) {
						// Argumente für Methode erstellen
						@SuppressWarnings("rawtypes")
						Class[] cArg = new Class[params.length];
				        int i = 0;
				        for (Object p : params) 
				        	cArg[i++] = p.getClass();
						 
				         method = handler.getClass().getMethod(methodName, cArg);
					}
					else 
				         method = handler.getClass().getMethod(methodName);

					// Methode ausführen
					if (method == null) return null;
					Class<?> retType = method.getReturnType();
					if (retType == Void.TYPE) {
						method.invoke(handler, params);
						return null;
					}
					else
						return method.invoke(handler, params);
				} catch (Exception e) {
					if (e instanceof InvocationTargetException) {
						InvocationTargetException te = (InvocationTargetException)e;
						if (te.getTargetException() instanceof LettoTimeoutException) {
							LettoTimeoutException lte = (LettoTimeoutException)te.getTargetException();
							throw lte;
						}
					}
					if (e.getCause().getMessage().equals("Abbruch"))
				    	throw new RuntimeException("Abbruch");
					ServerConfiguration.service.err("Methode "+ methodName + " konnte nicht aufgerufen werden", e.getMessage());
					return null;
				}
			}
			@Override public String getMethodeName() { return "TimerCall.exec-"+(handler!=null?(handler.getClass()+"."+methodName):""); }
			@Override public String getMethodeInfo() { return  params.length+"-Parameter"; }
		},timeoutms, handler, methodName, obj);
		return ret;
	}
	
	/*
	 * --------------------------------------------------------------------------------------------------------------------
	 *  BEISPIEL
	 * --------------------------------------------------------------------------------------------------------------------
	 */
	private static int x=0;
	public static void main(String[] args) {	
		CallResult ret = TimerCall.callMethode(new CallAdapter() {
			@Override
			public Object callMethode(Object ... objects) {				
				int y;
				y = (Integer)objects[0]; 
				x=y+2;
				// while(true)x++;
				return y*3;
			}
		    @Override public String getMethodeName() { return "TEST"; }
		    @Override public String getMethodeInfo() { return "Info"; }
			},
			100, // Dies ist der Timeout in ms
			4    // Hier kommt die Parameterliste. Die 4 wird als Parameter objects[0] in der Method verwendet
			);		
		System.out.println("Ergebnis:"+ret.status+" x="+x+" Result="+ret.result);		
	}	

}
