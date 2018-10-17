package escalonador;

import java.util.*;
import java.io.*;

public class Escalonador {
    
    List<BCP> tabelaDeProcessos = null;
    private final int quantum;
    
    public Escalonador(List<BCP> tabelaDeProcessos, int quantum) {
        this.tabelaDeProcessos = tabelaDeProcessos;
        this.quantum = quantum;
    }
    
    /*
    ** Atualizacao do logfile: inicializacao (carregamento) de um processo
    */
   
    public void carregamentoDeProcessos(PrintWriter logFile, Map<Integer, List<BCP>> filas) {
        int nroFila = filas.size();
        List<BCP> filaProntos;
            
        while (nroFila > 0) {
            filaProntos = filas.get(nroFila);
            for (BCP processo : filaProntos) { 
                String carregamento = "Carregando " + processo.getNomePrograma() + "%n";
                logFile.printf(carregamento);
            }
            nroFila--;
        }
    }
    
    /*
    ** Atualizacao do logfile: execucao de um processo
    */
    
    public void executandoProcesso(PrintWriter logFile, BCP processo) {
        String executando = "Executando " + processo.getNomePrograma() + "%n";
        logFile.printf(executando);
    }
    
    /*
    ** Atualizacao do logfile: processo interrompido (bloqueio de E/S, fim do quantum,
    ** ou termino natural do processo), incluindo-se o numero de instrucoes realizadas
    ** ate seu interrompimento
    */
    
    public void interrompendoProcesso(PrintWriter logFile, BCP processo, int nInstrucoes) {
        String interrompendo = "Interrompendo " + processo.getNomePrograma();
        String numInstrucoes = " apos " + nInstrucoes + " instrucao(oes)%n";   
        logFile.printf(interrompendo+numInstrucoes);
    }
     
    /*
    ** Atualizacao do logfile: processo interrompido devido a uma E/S
    */
    
    public void iniciandoEntradaSaida(PrintWriter logFile, BCP processo) {
        String entradaSaida = "E/S iniciada em " + processo.getNomePrograma() + "%n";
        logFile.printf(entradaSaida);
    }
    
    /*
    ** Atualizacao do logfile: processo interrompido devido ao seu termino natural
    */
   
    public void terminandoProcesso(PrintWriter logFile, BCP processo) {
        String nomeProcesso = processo.getNomePrograma();
        String terminado = " terminado.";
        String regX = " [X=" + processo.getX() + "]";
        String regY = " [Y=" + processo.getY() + "]%n";
        logFile.printf(nomeProcesso+terminado+regX+regY);
    }
    
    /*
    ** Atualizacao do logfile: inclusao do valor de quantum definido
    */
    
    public void quantumUtilizado(PrintWriter logFile) {
        String sQuantum = "QUANTUM: ";
        String valorQuantum = Integer.toString(quantum);
        logFile.printf(sQuantum+valorQuantum);
    }
    
    /*
    ** Atualizacao do logfile: Insercao do numero medio de trocas de processo,
    ** por processo
    */
    
    public void mediaTrocas(PrintWriter logFile, int nTrocas, int numProcessos) {
        String mediaTrocas = "MEDIA DE TROCAS: ";
        String valorMedia = Double.toString(nTrocas/numProcessos) + "%n";
        logFile.printf(mediaTrocas+valorMedia);
    }
    
    /*
    ** Atualizacao do logfile: Insercao do numero medio de instrucoes executadas
    ** por grupo de n_com (quantum)
    */
    
    public void mediaInstrucoes(PrintWriter logFile, int numInstrucoes) {
        String mediaInstrucoes = "MEDIA DE INSTRUCOES: ";
        String valorMedia = Double.toString(numInstrucoes/quantum) + "%n";
        logFile.printf(mediaInstrucoes+valorMedia);
    }
    
    /* 
    ** Distribuicao de creditos de mesmo valor que a prioridade de cada processo e
    ** determinacao da quantidade de filas a serem criadas 
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
    ** Criacao de multiplas filas, de acordo com o numero de creditos (do maior para o menor)
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
    ** Imprimir o estado dos processos prontos e informacoes gerais sobre um dado processo
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
    
    public void imprimirGeral(BCP processo, List<BCP> filaBloqueados, List<BCP> filaProntosComum) {
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Executando: " +processo.getNomePrograma());
        System.out.println("PC: " +processo.getPC());
        System.out.println("X: " +processo.getX());
        System.out.println("Y: " +processo.getY());
        System.out.println("Creditos:" +processo.getCreditos());
        if (processo.isBloqueado()) System.out.println("Processo bloqueado " +processo.getNomePrograma());
        if (processo.isConcluido()) System.out.println("Fim do processo " +processo.getNomePrograma());
        System.out.println("");
        System.out.print("Fila de Bloqueados: ");
        for (BCP p : filaBloqueados) System.out.print("(" +p.getNomePrograma()+ ")[" +p.getCreditos()+ "][t: " +p.getTempoDeEspera()+ "]");
        System.out.println();
        System.out.print("Fila de Prontos Comum: ");
        for (BCP p : filaProntosComum) System.out.print("(" +p.getNomePrograma()+ ")[" +p.getCreditos()+ "]");
        System.out.println("\n");
        System.out.println("-------------------------------------------------------------------");
    }
        
    /*
    ** Verifica se existem processos presentes na tabela. Existindo, verifica-se 
    ** se TODOS possuem 0 creditos. Alem disso, espera-se que nao existam processos bloqueados
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
    ** Os processos encontram-se com zero creditos, entao ocorre a redistribuicao 
    ** de acordo com suas prioridades
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
    ** Verificacao de situacoes que envolvem as filas de Prontos e Bloqueados: 
    ** Se existirem processos bloqueados e a fila de prontos estiver vazia, devolve true.
    ** Caso contrario, false.
    */
    
    public boolean verificaProntosBloqueados(List<BCP> filaBloqueados, Map<Integer, List<BCP>> filas) {
        if (!filaBloqueados.isEmpty()) {
            for (int i = 1; i <= filas.size(); i++)
                if (!filas.get(i).isEmpty()) return false;
        }
        return true;
    } 
    
    /*
    ** Verificacao para a situacao em que se deve decrementar os tempos de espera de todos os
    ** processos na fila de bloqueados, ate que um possa ser rodado
    */
    
    public boolean decrementaBloqueados(List<BCP> filaBloqueados, Map<Integer, List<BCP>> filas, int qtdProcessos) {
        boolean verificaSituacao = verificaProntosBloqueados(filaBloqueados, filas);
        boolean verificaTamanho = (filaBloqueados.size() == qtdProcessos); 
        return (verificaSituacao && verificaTamanho);
    }
    
    /*
    ** Atualizacao de fila de bloqueados no caso de existirem processos bloqueados e a fila de prontos estiver vazia:
    ** Decremento do tempo de espera de cada processo ate que se esgote, permitindo, assim, sua execucao
    */
    
    public void atualizaBloqueados(List<BCP> filaBloqueados, List<BCP> filaProntosComum, Map<Integer, List<BCP>> filas) {
        int tempoRestante;
        int creditos;
        int primeiroProcesso = 0;
        
        boolean liberouProcesso = false;
        
        while ((!filaBloqueados.isEmpty()) && (!liberouProcesso)) {
            BCP processo = filaBloqueados.get(primeiroProcesso);
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
                else { filaProntosComum.add(processo); }
                
                liberouProcesso = true;
            }
        }
    }
    
    /*
    ** Metodo que gerencia os processos setados como bloqueados, verificando o tempo de espera e procedendo adequadamente
    */
    
    public void gerenciaBloqueados(List<BCP> filaBloqueados, List<BCP> filaProntosComum, Map<Integer, List<BCP>> filas) {
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
                    else { filaProntosComum.add(processo); }
                }
            }
        }
    }
    
    /*
    ** Verifica situacao em que se utiliza a fila de prontos comum, ou seja, momento em que existem processos bloqueados 
    ** e todos os demais processos encontram-se com zero credito
    */
    
    public boolean utilizaProntosComum(List<BCP> filaBloqueados, List<BCP> filaProntosComum, Map<Integer, List<BCP>> filas) {
        boolean utiliza = (verificaProntosBloqueados(filaBloqueados, filas) && (!filaProntosComum.isEmpty()));
        return utiliza;
    }
    
    /*
    ** Execucao do processo: leitura das instrucoes do programa, atualizando seu estado,
    ** numero de creditos e registradores (PC, X e Y)
    */
    
    public int execucaoDoProcesso(BCP processo, boolean prontosComum, int tempoDeEspera) {
        String instrucao;
        String[] registradorGeral;
        double aux;
        int prioridade, creditos;
        int n, PC, valorReg, numInstrucoes;
        
        numInstrucoes = 0;
        prioridade = processo.getPrioridade();
        creditos = processo.getCreditos();
        
        PC = processo.getPC();
        n = prioridade - creditos;
        aux = quantum * Math.pow(2, n);
        
        while (aux > 0) {
            instrucao = processo.getRefCodigo().get(PC);
            numInstrucoes++;
            
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
            if (!prontosComum) {
                creditos--;
                processo.setCreditos(creditos);
            }
            
            if (!processo.isBloqueado()) {
                processo.setExecutando(false);
                processo.setPronto(true);
            }
        }
        return numInstrucoes;
    }
    
    /*
    ** Implementacao do algoritmo de prioridades especificado
    */
    
    public void escalonamento(PrintWriter logFile) {
        if (tabelaDeProcessos != null) {
            
            // Distribuicao de creditos, a cada processo,
            // igual a sua prioridade e definicao do nro
            // de filas a serem criadas
            int qtdFilas = distribuicaoCreditos();
            
            // Criacao de multiplas filas, de acordo com o numero de
            // creditos (do maior para o menor)
            Map<Integer, List<BCP>> filas = multiplasFilas(qtdFilas);
            
            // Carregamento dos processos no logfile
            carregamentoDeProcessos(logFile, filas);
            
            // Quantidade inicial de processos
            int qtdProcessos = tabelaDeProcessos.size();
            
            // Variaveis auxiliares para o escalonamento
            int creditos, tempoDeEspera, primeiroProcesso, numInstrucoes, trocas, numProcessos;
            
            tempoDeEspera = 2;
            primeiroProcesso = 0;
            trocas = 0;
            numProcessos = qtdProcessos;
            
            boolean prontosComum = false;
            
            // Lista de processos bloqueados
            List<BCP> filaBloqueados = new LinkedList<>();
            
            // Listas de processos prontos:
            // Comum e ordenada por prioridades, respectivamente
            List<BCP> filaProntosComum = new LinkedList<>();
            List<BCP> filaProntos;
            
            Iterator<BCP> iterator;
            
            int nroFila = qtdFilas;
            imprimirFilas(filas);
            
            // Aplicacao do algoritmo 
            // Round-Robin em cada fila
            // definida (processos prontos)
            while (qtdProcessos > 0) {
                
                // Definicao da fila de prontos a ser utilizada
                if (utilizaProntosComum(filaBloqueados, filaProntosComum, filas)) { 
                    filaProntos = filaProntosComum;
                    prontosComum = true;
                }
                else { filaProntos = filas.get(nroFila); }
                
                iterator = filaProntos.iterator();
                
                // Tratamento dos processos
                // a partir da fila de prontos
                // definida anteriormente
                while (iterator.hasNext()) {
                    BCP processo = iterator.next();
                    iterator.remove();
                    
                    // Execucao do processo
                    processo.setPronto(false);
                    processo.setExecutando(true);
                    
                    // Leitura das instrucoes do programa em execucao e tratamento adequado
                    numInstrucoes = execucaoDoProcesso(processo, prontosComum, tempoDeEspera);
                    
                    // Registro da execucao no logfile
                    executandoProcesso(logFile, processo);
                    
                    gerenciaBloqueados(filaBloqueados, filaProntosComum, filas);
                    iterator = filaProntos.iterator();
                    
                    creditos = processo.getCreditos();
                    
                    if (processo.isPronto()) {
                        if (creditos > 0) { filas.get(creditos).add(primeiroProcesso, processo); }
                        else { filaProntosComum.add(processo); }
                    }
                    
                    if (processo.isBloqueado()) { 
                        filaBloqueados.add(processo); 
                        iniciandoEntradaSaida(logFile, processo);
                    }
                    
                    interrompendoProcesso(logFile, processo, numInstrucoes);
                    
                    if (processo.isConcluido()) { 
                        qtdProcessos--; 
                        terminandoProcesso(logFile, processo);
                    }
                    
                    imprimirGeral(processo, filaBloqueados, filaProntosComum);
                    imprimirFilas(filas);
                    
                    trocas++;
                    
                    if ((filaBloqueados.isEmpty()) && (prontosComum)) { break; }
                }
                nroFila--;
                if ((nroFila == 0) || (prontosComum)) { nroFila = qtdFilas; prontosComum = false; }
                
                if (decrementaBloqueados(filaBloqueados, filas, qtdProcessos)) {
                    atualizaBloqueados(filaBloqueados, filaProntosComum, filas);
                }
                if (verificaRedistribuicao(qtdProcessos)) {
                    System.out.println("Redistribuindo creditos...\n");
                    redistribuicaoCreditos(filas);
                    filaProntosComum.clear();
                }
            }
            mediaTrocas(logFile, trocas, numProcessos);
            //mediaInstrucoes(logFile, numInstrucoesTotal);
            quantumUtilizado(logFile);
        }
    }
}
