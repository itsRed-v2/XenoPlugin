package fr.xenocraft.virtualStorages;

public class VirtualStorageUtils {

	public static String inStacks(int amount, int maxStackSize) {

		int stack = (int) Math.floor(amount / maxStackSize);
		int reminder = amount % maxStackSize;
		if (reminder == 0) {
			return stack + "×" + maxStackSize;
		}

		return stack + "×" + maxStackSize + " + " + reminder;
	}

}
