public class Atendente {
    private int id;
    private Cliente clienteAtual;
    private int tempoRestanteAtendimento;
    private int tempoTrabalhoConsecutivo;
    private boolean emIntervalo;
    private int clientesAtendidos;
    private int tempoTotalAtendimento;
    private int contadorVIPConsecutivos;
    
    public Atendente(int id) {
        this.id = id;
        this.tempoTrabalhoConsecutivo = 0;
        this.emIntervalo = false;
        this.clientesAtendidos = 0;
        this.tempoTotalAtendimento = 0;
        this.contadorVIPConsecutivos = 0;
    }
    
    public boolean isLivre() {
        return clienteAtual == null && !emIntervalo;
    }
    
    public boolean isOcupado() {
        return clienteAtual != null && !emIntervalo;
    }
    
    public boolean isEmIntervalo() {
        return emIntervalo;
    }
    
    public void atenderCliente(Cliente proximoCliente, int tempoAtendimento) {
        this.clienteAtual = proximoCliente;
        this.tempoRestanteAtendimento = tempoAtendimento;
        this.clientesAtendidos++;
        this.tempoTotalAtendimento += tempoAtendimento;
        
        if (proximoCliente.getPreferencial()) {
            contadorVIPConsecutivos++;
        } else {
            contadorVIPConsecutivos = 0;
        }
        
        System.out.println("Atendente " + id + " atendendo cliente " + proximoCliente.getId() + 
                         " (VIP: " + proximoCliente.getPreferencial() + "), tempo: " + 
                         tempoAtendimento + " minutos.");
    }
    
    public void trabalhar() {
        if (isOcupado()) {
            tempoRestanteAtendimento--;
            tempoTrabalhoConsecutivo++;
            
            if (tempoRestanteAtendimento == 0) {
                System.out.println("Atendente " + id + " terminou de atender cliente " + clienteAtual.getId());
                clienteAtual = null;
                
                // Verificar se precisa de intervalo
                if (tempoTrabalhoConsecutivo >= 180) { // 3 horas
                    iniciarIntervalo();
                }
            }
        } else if (isLivre()) {
            tempoTrabalhoConsecutivo++;
            
            if (tempoTrabalhoConsecutivo >= 180) {
                iniciarIntervalo();
            }
        } else if (isEmIntervalo()) {
            // Em intervalo - não faz nada
        }
    }
    
    private void iniciarIntervalo() {
        emIntervalo = true;
        tempoTrabalhoConsecutivo = 0;
        System.out.println("Atendente " + id + " iniciou intervalo de 60 minutos.");
    }
    
    public void finalizarIntervalo() {
        if (emIntervalo) {
            emIntervalo = false;
            System.out.println("Atendente " + id + " retornou do intervalo.");
        }
    }
    
    // Getters
    public int getContadorVIPConsecutivos() { return contadorVIPConsecutivos; }
    public void resetContadorVIP() { contadorVIPConsecutivos = 0; }
    public int getClientesAtendidos() { return clientesAtendidos; }
    public int getTempoTotalAtendimento() { return tempoTotalAtendimento; }
    public int getId() { return id; }

}