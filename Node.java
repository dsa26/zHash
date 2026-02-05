public class Node<T> {
    public String key;
    public T val;
    public Node<T> next;

    public Node(String key, T val, Node<T> next) {
        this.key = key;
        this.val = val;
        this.next = next;
    }
}
