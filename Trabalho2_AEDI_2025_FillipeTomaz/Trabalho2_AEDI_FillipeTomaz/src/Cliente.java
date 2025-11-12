
public class Cliente {
    private final int id;
    private final boolean preferencial; 
    private int tempoEspera;

    public Cliente(int id, boolean preferencial) {
        this.id = id;
        this.preferencial = preferencial;
        this.tempoEspera = 0;
    }

    public int getId() {
        return id;
    }

    public boolean getPreferencial() {
        return preferencial;
    }

    public int getTempoEspera() {
        return tempoEspera;
    }

    public void incrementarTempoEspera() {
        tempoEspera++;
    }
}