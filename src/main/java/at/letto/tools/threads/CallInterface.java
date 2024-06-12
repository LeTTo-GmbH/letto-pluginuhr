package at.letto.tools.threads;

/**
 * Ein Interface, um Methoden gezielt aufrufen zu können
 * @author Werner Damböck
 *
 */
public interface CallInterface {

	Object callMethode(Object ... objects);

	String getMethodeName();

	String getMethodeInfo();

}
