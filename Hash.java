public class Hash<T> {
    private Node[] hashmap; // Some type safety issues, but chose to ignore to simplify code

    public Hash() {
        this(2000);
    }

    public Hash(int size) {
        this.hashmap = new Node[size];
    }

    public void put(String key, T obj) {
        Node<T> current = hashmap[hash(key)];
        while (current != null) {
            if (current.key.equals(key)) {
                current.val = obj;
                return;
            }
            current = current.next;
        }
        hashmap[hash(key)] = new Node<T>(key, obj, hashmap[hash(key)]);
    }

    public T get(String key) {
        Node<T> current = hashmap[hash(key)];
        while (current != null) {
            if (current.key.equals(key))
                return current.val;
            current = current.next;
        }
        return null;
    }

    public Node<T> getIndex(int i) {
        return hashmap[i];
    }

    public int size() {
        return hashmap.length;
    }

    public int filledSize() {
        int count = 0;
        for (int i = 0; i < hashmap.length; i++) {
            count += llSize(hashmap[i]);
        }
        return count;
    }

    public int totalCollisions() {
        int count = 0;
        for (int i = 0; i < hashmap.length; i++) {
            count += Math.max(0, llSize(hashmap[i]) - 1); // Counting only >1 items as collisions
        }
        return count;
    }

    public int mostCollisions() {
        int count = 0;
        for (int i = 0; i < hashmap.length; i++) {
            count = Math.max(llSize(hashmap[i]), count);
        }
        return count;
    }

    public int hash(String str) {
        return Hash.hash(str, hashmap.length);
    }

    public static int hash(String str, int length) {
        char[] chars = new char[4];
        chars[0] = 'z'; // Least common English letter
        chars[1] = 'z';
        chars[2] = 'z';
        chars[3] = 'z';

        if (str.length() < 7) {
            for (int i = 0; i < Math.min(4, str.length()); i++) {
                chars[i] = str.charAt(i);
            }
            if (str.length() > 4)
                chars[2] = str.charAt(4); // Just spreading it out a bit
            // Also screws up the order, so hoping to improve collisions due to the
            // structure of the English language
        } else {
            for (int i = 0; i < 4; i++) {
                chars[i] = str.charAt(2 * i); // Using alternate letters to avoid collisions due to common clusters of
                                              // letters
            }
        }
        return (to10(to4(character(chars[0])) + to4(character(chars[1])) +
                to4(character(chars[2])) + to4(character(chars[3])))) % length; // Allows varying array size
    }

    public static int conventionalHash(String str) {
        return 0;
    }

    private static int character(char chr) {
        if ('a' <= chr && chr <= 'z')
            chr -= 32;
        chr -= 64; // 1-indexed
        return switch (chr) { // Mod will not work here specifically because of 15 and E/T
            case 5 -> 0;
            case 20 -> 1;
            case 1 -> 2;
            case 15 -> 3;
            case 9, 26 -> 4;
            case 14, 17 -> 5;
            case 19, 10 -> 6;
            case 18, 24 -> 7;
            case 8, 11 -> 8;
            case 12, 22 -> 9;
            case 4, 2 -> 10;
            case 3, 25 -> 11;
            case 21, 23 -> 12;
            case 13, 7 -> 13;
            case 6, 16 -> 14;
            default -> 15; // Requires separate chaining because causes unequal distribution, but hopefully
                           // dedicated container is enough
        };
    }

    private static String to4(int n) { // guaranteed to be 0-14
        String num = "";
        num += n / 4;
        num += n % 4;
        return num;
    }

    private static int to10(String n) { // guaranteed to be eight characters
        int num = 0;
        for (int i = 0; i < 8; i++) {
            num += parseChar(n, 7 - i) * Math.pow(4, i);
        }
        return num;
    }

    private static int parseChar(String str, int index) {
        return Integer.parseInt("" + str.charAt(index));
    }

    public static int llSize(Node node) {
        Node current = node;
        int length = 0;
        while (current != null) {
            length++;
            current = current.next;
        }
        return length;
    }
}