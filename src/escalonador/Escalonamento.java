package escalonador;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Escalonamento {
    
    List<BCP> tabelaDeProcessos = null;
    private final int quantum;
    
    public Escalonamento(List<BCP> tabelaDeProcessos, int quantum) {
        this.tabelaDeProcessos = tabelaDeProcessos;
        this.quantum = quantum;
    }
    
    /* 
    ** Distribuicao de creditos de mesmo valor
    ** que a prioridade de cada processo e
    ** determinacao da quantidade de filas a
    ** serem criadas 
    */
    
    public int distribuicaoCreditos() {
        int creditos;
        int qtdFilas = 0;
            
        for (BCP processo : tabelaDeProcessos) {
            creditos = processo.getPrioridade();
            processo.setCreditos(creditos);
                
            if (creditos > qtdFilas) { qtdFilas = creditos; }
        }
        return qtdFilas;
    }
    
    /*
    ** Os processos encontram-se com
    ** zero creditos, entao ocorre a
    ** redistribuicao de acordo com
    ** suas prioridades
    */
    
    public void redistribuicaoCreditos(Map<Integer, List<BCP>> filas) {
        int creditos;
        
        for (BCP processo : tabelaDeProcessos) {
            creditos = processo.getPrioridade();
            processo.setCreditos(creditos);
            filas.get(creditos).add(processo);
        }
        imprimirFilas(filas);
    }
    
    /* 
    ** Criacao de multiplas filas, de acordo com
    ** o numero de creditos (do maior para o menor)
    */
    
    public Map<Integer, List<BCP>> multiplasFilas(int qtdFilas) {
        Map<Integer, List<BCP>> filas = new HashMap<>();
            
        while (qtdFilas > 0) {
            List<BCP> filaProntos = new LinkedList<>();
                
            for (BCP processo : tabelaDeProcessos) {
                if (processo.getCreditos() == qtdFilas) { filaProntos.add(processo); }
            }
                
            filas.put(qtdFilas, filaProntos);
            qtdFilas--;
        }
        return filas;
    }
    
    /*
    ** Funcoes utilizadas para testes:
    ** imprimir o estado dos processos
    ** prontos e informacoes gerais sobre
    ** um dado processo
    */
    
    public void imprimirFilas(Map < Integer, List<BCP> > filas) {
        int n = filas.size();
        System.out.println("Situacao Filas:");
        while (n > 0) {
            System.out.print("["+ n +"] ~ ");
            List<BCP> fila = filas.get(n);
            for (BCP f : fila) {
                System.out.print("(" + f.getNomePrograma() + " -> C = " + f.getCreditos() + ")");
            }
            System.out.println();
            n--;
        }
        System.out.println();
    }
    
    public void imprimirGeral(BCP processo, List<BCP> filaBloqueados) {
        System.out.println("Executando: " +processo.getNomePrograma());
        System.out.println("PC: " +processo.getPC());
        System.out.println("X: " +processo.getX());
        System.out.println("Y: " +processo.getY());
        System.out.println("Creditos:" +processo.getCreditos());
        if (processo.isBloqueado()) System.out.println("Processo bloqueado " +processo.getNomePrograma());
        if (processo.isConcluido()) System.out.println("Fim do processo " +processo.getNomePrograma());
        System.out.println("");
        System.out.print("Fila de Bloqueados: ");
        for (BCP p : filaBloqueados) System.out.print("(" +p.getNomePrograma()+ ")[" +p.getCreditos()+ "]");
        System.out.println("\n");
    }
    
    /*
    ** Algoritmo de escalonamento utilizado em cada classe de prioridades
    */
    
    public int roundRobin(BCP processo, int tempoDeEspera) {
        String instrucao;
        String[] registradorGeral;
        double aux;
        int prioridade, creditos;
        int n, PC, valorReg;
        
        prioridade = processo.getPrioridade();
        creditos = processo.getCreditos();
        
        PC = processo.getPC();
        n = prioridade - creditos;
        aux = quantum * Math.pow(2, n);
        
        while (aux > 0) {
            instrucao = processo.getRefCodigo().get(PC);

            if (instrucao.contains("X=")) {
                registradorGeral = instrucao.split("=");
                int valor = registradorGeral.length-1;
                valorReg = Integer.parseInt(registradorGeral[valor]);
                processo.setX(valorReg);
            }
            if (instrucao.contains("Y=")) {
                registradorGeral = instrucao.split("=");
                int valor = registradorGeral.length-1;
                valorReg = Integer.parseInt(registradorGeral[valor]);
                processo.setY(valorReg);
            }
            if (instrucao.equalsIgnoreCase("E/S")) {
                processo.setExecutando(false);
                processo.setBloqueado(true);
                processo.setTempoDeEspera(tempoDeEspera);
                PC++;
                processo.setPC(PC);
                break;
            }          
            if (instrucao.equalsIgnoreCase("SAIDA")) { 
                processo.setExecutando(false);
                processo.setConcluido(true);
                tabelaDeProcessos.remove(processo);
                break;
            }
            
            PC++;
            processo.setPC(PC);
            
            aux--;
        }
        if (!processo.isConcluido()) {
            creditos--;
            processo.setCreditos(creditos);
            
            if (!processo.isBloqueado()) {
                processo.setExecutando(false);
                processo.setPronto(true);
            }
        }
        return creditos;
    }
    
    /*
    ** Verifica se existem processos
    ** presentes na tabela. Existindo, verifica-se 
    ** se TODOS possuem 0 creditos. Alem disso,
    ** espera-se que nao existam processos bloqueados
    */
    
    public boolean verificaRedistribuicao(int qtdProcessos) {
        if (qtdProcessos > 0) {
            boolean existeProcesso = true;
            boolean zeroCreditos = true;
            boolean pronto = true;
        
            for (BCP processo : tabelaDeProcessos) {
                if (qtdProcessos == 0) { existeProcesso = false; } 
                if (processo.getCreditos() > 0) { zeroCreditos = false; }
                if (!processo.isPronto()) { pronto = false; }
            }
        
            return (existeProcesso && zeroCreditos && pronto);
        }
        return false;
    }
    
    /*
    ** Metodo que gerencia os processos setados
    ** como bloqueados, verificando o tempo de
    ** espera e procedendo adequadamente.
    */
    
    public void gerenciaBloqueados(List<BCP> filaBloqueados, Map<Integer, List<BCP>> filas) {
        if (!filaBloqueados.isEmpty()) {
            Iterator<BCP> it = filaBloqueados.iterator();
            
            BCP processo;
            int tempoDeEspera, creditos;
            
            while (it.hasNext()) {
                processo = it.next();
                
                tempoDeEspera = processo.getTempoDeEspera();
                tempoDeEspera--;
                processo.setTempoDeEspera(tempoDeEspera);
                
                if (tempoDeEspera == 0) {
                    it.remove();
                    
                    processo.setBloqueado(false);
                    processo.setPronto(true);
                    
                    creditos = processo.getCreditos();
                    if (creditos > 0) {
                        filas.get(creditos).add(processo);
                        System.out.println("Reposicionando " +processo.getNomePrograma() +" em prontos apos exec do processo abaixo\n");
                    }
                }
            }
        }
    }
    
    /*
    ** Verificacao de situacoes que envolvem
    ** as filas de Prontos e Bloqueados: 
    ** Se existirem processos bloqueados e a
    ** fila de prontos estiver vazia, devolve true.
    ** Caso contrario, false.
    */
    
    public boolean verificaProntosEBloqueados(List<BCP> filaBloqueados, Map<Integer, List<BCP>> filas) {
        if (!filaBloqueados.isEmpty()) {
            for (int i = 1; i <= filas.size(); i++)
                if (!filas.get(i).isEmpty()) return false;
        }
        return true;
    } 
    
    /*
    ** Atualizacao de fila de bloqueados no caso de
    ** existirem processos bloqueados e a fila 
    ** de prontos estiver vazia:
    ** Decremento do tempo de espera de cada processo
    ** ate que se esgote, permitindo, assim, sua execucao
    */
    
    public void atualizaBloqueados(List<BCP> filaBloqueados, Map<Integer, List<BCP>> filas) {
        int tempoRestante;
        int creditos;
        
        while (!filaBloqueados.isEmpty()) {
            BCP processo = filaBloqueados.get(0);
            creditos = processo.getCreditos();
            tempoRestante = processo.getTempoDeEspera();
            
            while (tempoRestante > 0) {
                tempoRestante--;
                processo.setTempoDeEspera(tempoRestante);
            }
            if (tempoRestante == 0) {
                processo.setBloqueado(false);
                processo.setPronto(true);
                filaBloqueados.remove(processo);
                if (creditos > 0) { filas.get(creditos).add(processo); }
            }
        }
    }
    
    /*
    ** Metodo que gerencia o processo de escalonamento
    */
    
    public void escalonamento() {
        if (tabelaDeProcessos != null) {
            int qtdFilas = distribuicaoCreditos();
            int qtdProcessos = tabelaDeProcessos.size();
            int tempoDeEspera = 2;
            int primeiroProcesso = 0;
            int creditos;
            
            Map<Integer, List<BCP>> filas = multiplasFilas(qtdFilas);
            List<BCP> filaBloqueados = new LinkedList<>();
            List<BCP> filaProntos;
            
            Iterator<BCP> iterator;
            
            int nroFila = qtdFilas;
            imprimirFilas(filas);
            while (qtdProcessos > 0) {
                filaProntos = filas.get(nroFila);
                iterator = filaProntos.iterator();
                
                while (iterator.hasNext()) {
                    BCP processo = iterator.next();
                    iterator.remove();
                    
                    processo.setPronto(false);
                    processo.setExecutando(true);
                    
                    creditos = roundRobin(processo, tempoDeEspera);
                    
                    gerenciaBloqueados(filaBloqueados, filas);
                    iterator = filaProntos.iterator();

                    if ((creditos > 0) && (processo.isPronto())) { filas.get(creditos).add(primeiroProcesso, processo); }
                    if (processo.isBloqueado()) { filaBloqueados.add(processo); }
                    if (processo.isConcluido()) { qtdProcessos--; }
                    
                    imprimirGeral(processo, filaBloqueados);
                    imprimirFilas(filas);
                }
                nroFila--;
                if (nroFila == 0) { nroFila = qtdFilas; }
                
                if (verificaProntosEBloqueados(filaBloqueados, filas)) {
                    atualizaBloqueados(filaBloqueados, filas);
                }
                if (verificaRedistribuicao(qtdProcessos)) {
                    System.out.println("Redistribuindo creditos...\n");
                    redistribuicaoCreditos(filas);
                }
            }
        }
    }
}
