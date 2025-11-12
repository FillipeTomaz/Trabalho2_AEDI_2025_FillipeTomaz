// App.java - Versão Adaptada
import java.util.Random;

public class App {
    // Constantes conforme o PDF
    private static final int TEMPO_SIMULACAO = 720; 
    private static final int TEMPO_MAX_CLIENTE = 15;
    private static final int TEMPO_CLIENTE_MIN = 3;
    private static final int TEMPO_CLIENTE_MAX = 10;
    private static final int QTD_BOX_MIN = 1;
    private static final int QTD_BOX_MAX = 5;
    private static final int QTD_ATD_MIN = 1;
    private static final int QTD_ATD_MAX = 5;
    
    public static void main(String[] args) {
        Fila filaVIP = new Fila();
        Fila filaNormal = new Fila();
        
        // Inicializar atendentes
        Atendente[] atendentes = new Atendente[QTD_ATD_MAX];
        for (int i = 0; i < QTD_ATD_MAX; i++) {
            atendentes[i] = new Atendente(i + 1);
        }
        // Inicialmente apenas um atendente ativo
        int atendentesAtivos = 1;
        
        Random random = new Random();
        int contadorClientes = 1;
        int clientesDesistiram = 0;
        
        System.out.println("=== INÍCIO DA SIMULAÇÃO DO CALL CENTER ===");
        
        for (int minuto = 0; minuto < TEMPO_SIMULACAO || (!filaVIP.vazia() && !filaNormal.vazia()); minuto++) {
            
            // Chegada de clientes (apenas durante horário comercial)
            if (minuto < TEMPO_SIMULACAO) {
                // 30% de chance de chegar um cliente por minuto
                if (random.nextDouble() < 0.3) {
                    boolean vip = random.nextDouble() < 0.3; 
                    Cliente cliente = new Cliente(contadorClientes++, vip);
                    
                    if (vip) {
                        filaVIP.enqueue(cliente);
                        System.out.println("Cliente VIP " + cliente.getId() + " chegou na fila.");
                    } else {
                        filaNormal.enqueue(cliente);
                        System.out.println("Cliente Normal " + cliente.getId() + " chegou na fila.");
                    }
                }
            }
            
            // Gerenciamento de atendentes (abrir/fechar boxes)
            int totalFila = filaVIP.getTamanho() + filaNormal.getTamanho();
            if (totalFila > 10 && atendentesAtivos < QTD_ATD_MAX) {
                atendentesAtivos++;
                System.out.println("Novo atendente ativado. Total: " + atendentesAtivos);
            } else if (totalFila < 3 && atendentesAtivos > QTD_ATD_MIN) {
                atendentesAtivos--;
                System.out.println("Atendente desativado. Total: " + atendentesAtivos);
            }
            
            // Processar atendentes
            for (int i = 0; i < atendentesAtivos; i++) {
                Atendente atendente = atendentes[i];
                
                // Finalizar intervalo se necessário
                if (atendente.isEmIntervalo() && minuto % 60 == 0) {
                    atendente.finalizarIntervalo();
                }
                
                // Atendimento
                if (atendente.isLivre()) {
                    Cliente proximoCliente = selecionarProximoCliente(filaVIP, filaNormal, atendente);
                    if (proximoCliente != null) {
                        int tempoAtendimento = random.nextInt(TEMPO_CLIENTE_MAX - TEMPO_CLIENTE_MIN + 1) + TEMPO_CLIENTE_MIN;
                        atendente.atenderCliente(proximoCliente, tempoAtendimento);
                    }
                }
                
                atendente.trabalhar();
            }
            
            // Incrementar tempo de espera e verificar desistências
            clientesDesistiram += verificarDesistencias(filaVIP, minuto);
            clientesDesistiram += verificarDesistencias(filaNormal, minuto);
            
            // Log a cada hora
            if (minuto % 60 == 0) {
                System.out.printf("[Minuto %d] Fila VIP: %d, Fila Normal: %d, Atendentes: %d\n",
                                minuto, filaVIP.getTamanho(), filaNormal.getTamanho(), atendentesAtivos);
            }
        }
        
        // Relatório final
        gerarRelatorioFinal(atendentes, atendentesAtivos, clientesDesistiram);
    }
    
    private static Cliente selecionarProximoCliente(Fila filaVIP, Fila filaNormal, Atendente atendente) {
        // Prioridade: VIP, mas após 2 VIPs consecutivos, atende normal se houver
        if (!filaVIP.vazia() && (atendente.getContadorVIPConsecutivos() < 2 || filaNormal.vazia())) {
            Cliente cliente = filaVIP.dequeue();
            System.out.println("Atendendo cliente VIP " + cliente.getId());
            return cliente;
        } else if (!filaNormal.vazia()) {
            Cliente cliente = filaNormal.dequeue();
            atendente.resetContadorVIP();
            System.out.println("Atendendo cliente Normal " + cliente.getId());
            return cliente;
        } else if (!filaVIP.vazia()) {
            Cliente cliente = filaVIP.dequeue();
            System.out.println("Atendendo cliente VIP " + cliente.getId());
            return cliente;
        }
        return null;
    }
    
    private static int verificarDesistencias(Fila fila, int minutoAtual) {
        int desistencias = 0;
        Fila temp = new Fila();
        
        while (!fila.vazia()) {
            Cliente cliente = fila.dequeue();
            cliente.incrementarTempoEspera();
            
            if (cliente.getTempoEspera() > TEMPO_MAX_CLIENTE) {
                System.out.println("Cliente " + cliente.getId() + " desistiu após " + 
                                 cliente.getTempoEspera() + " minutos de espera.");
                desistencias++;
            } else {
                temp.enqueue(cliente);
            }
        }
        
        // Reenfileirar clientes que não desistiram
        while (!temp.vazia()) {
            fila.enqueue(temp.dequeue());
        }
        
        return desistencias;
    }
    
    private static void gerarRelatorioFinal(Atendente[] atendentes, int atendentesAtivos, int clientesDesistiram) {
        System.out.println("\n=== RELATÓRIO FINAL ===");
        System.out.println("Clientes que desistiram: " + clientesDesistiram);
        
        int totalClientesAtendidos = 0;
        int totalTempoAtendimento = 0;
        
        for (int i = 0; i < atendentesAtivos; i++) {
            Atendente atd = atendentes[i];
            System.out.printf("Atendente %d: %d clientes atendidos, ", 
                            atd.getId(), atd.getClientesAtendidos());
            
            if (atd.getClientesAtendidos() > 0) {
                double tempoMedio = (double) atd.getTempoTotalAtendimento() / atd.getClientesAtendidos();
                System.out.printf("Tempo médio: %.2f minutos\n", tempoMedio);
                totalClientesAtendidos += atd.getClientesAtendidos();
                totalTempoAtendimento += atd.getTempoTotalAtendimento();
            } else {
                System.out.println("Nenhum cliente atendido");
            }
        }
        
        if (totalClientesAtendidos > 0) {
            System.out.printf("\nTOTAL: %d clientes atendidos\n", totalClientesAtendidos);
            System.out.printf("Tempo médio geral: %.2f minutos\n", 
                            (double) totalTempoAtendimento / totalClientesAtendidos);
        }
    }
}