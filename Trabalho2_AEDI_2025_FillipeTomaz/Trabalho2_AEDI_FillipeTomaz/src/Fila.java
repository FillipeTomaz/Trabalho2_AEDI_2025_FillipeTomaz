
public class Fila {
    private Node head;
    private Node tail;
    private int tamanho;
    
    private static class Node {
        Cliente cliente;
        Node next;

        Node(Cliente cliente) {
            this.cliente = cliente;
            this.next = null;
        }
    }
    
    public Fila() {
        head = null;
        tail = null;
        tamanho = 0;
    }
    
    public boolean vazia() {
        return head == null;
    }
    
    public boolean cheia() {
        // Fila dinâmica nunca está cheia (a menos que haja limite de memória)
        return false;
    }
    
    public void enqueue(Cliente cliente) {
        Node newNode = new Node(cliente);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        tamanho++;
    }
    
    public Cliente dequeue() {
        if (vazia()) {
            return null;
        }
        Cliente cliente = head.cliente;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        tamanho--;
        return cliente;
    }
    
    public Cliente peek() {
        if (vazia()) {
            return null;
        }
        return head.cliente;
    }
    
    public int getTamanho() {
        return tamanho;
    }

    
}