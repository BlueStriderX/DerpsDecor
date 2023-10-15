package thederpgamer.decor.utils;

/**
 * Utility class for array operations.
 *
 * @author TheDerpGamer
 */
public class ArrayUtils {

	/**
	 * Removes the specified value from an array.
	 * @param array The array to remove the value from.
	 * @param value The value to remove.
	 * @return The modified array.
	 */
	public static Object[] remove(Object[] array, Object value) {
		assert array != null && value != null && array.length > 0 && array[0].getClass().equals(value.getClass());
		int index = -1;
		for(int i = 0; i < array.length; i ++) {
			if(array[i].equals(value)) {
				index = i;
				break;
			}
		}
		if(index != -1) {
			Object[] newArray = new Object[array.length - 1];
			for(int i = 0; i < newArray.length; i ++) {
				if(i < index) newArray[i] = array[i];
				else newArray[i] = array[i + 1];
			}
			array = newArray;
		}
		return array;
	}
}
