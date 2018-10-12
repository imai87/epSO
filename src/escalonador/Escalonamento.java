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
    
    public void imprimirProcesso(BCP processo) {
        System.out.println("Nome: " +processo.getNomePrograma());
        System.out.println("PC: " +processo.getPC());
        System.out.println("X: " +processo.getX());
        System.out.println("Y: " +processo.getY());
        if (processo.isConcluido()) System.out.println("Fim do processo " +processo.getNomePrograma());
        System.out.println("");
    }
    
    /*
    ** Algoritmo de escalonamento utilizado
    ** em cada classe de prioridades
    */
    
    public int roundRobin(BCP processo) {
        String instrucao;
        String[] registradorGeral;
        double aux;
        int creditos = 0;
        int n, PC, valorReg;
        
        PC = processo.getPC();
        n = processo.getPrioridade() - processo.getCreditos();
        aux = quantum * Math.pow(2, n);
        
        while (aux > 0) {
            instrucao = processo.getRefCodigo().get(PC);
            //System.out.println(instrucao);
            
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
                PC++;
                processo.setPC(PC);
                break;
            }
            
            if (instrucao.equalsIgnoreCase("SAIDA")) { 
                processo.setExecutando(false);
                processo.setConcluido(true);
                tabelaDeProcessos.remove(processo);
                //System.out.println("Terminou: " +processo.getNomePrograma());
                break;
            }
            
            PC++;
            processo.setPC(PC);
            
            aux--;
        }
        if (!processo.isConcluido()) {
            creditos = processo.getCreditos();
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
    ** Verifica se todos os processos
    ** presentes na tabela contem zero
    ** creditos, visando auxiliar na
    ** redistribuicao dos mesmos
    */
    
    public boolean verificaZeroCreditos() {
        boolean zeroCreditos = true;
        
        for (BCP processo : tabelaDeProcessos)
            if (processo.getCreditos() > 0) { zeroCreditos = false; }

        return zeroCreditos;
    }
    
    /*
    ** Metodo que gerencia o processo
    ** de escalonamento
    */
    
    public void escalonamento() {
        if (tabelaDeProcessos != null) {
            int qtdFilas = distribuicaoCreditos();
            int qtdProcessos = tabelaDeProcessos.size();
            int primeiroProcesso = 0;
            int creditos;
            
            Map<Integer, List<BCP>> filas = multiplasFilas(qtdFilas);
            List<BCP> filaProntos;
            List<BCP> filaBloqueados;
            
            Iterator<BCP> iterator;
            
            int nroFila = qtdFilas;
            imprimirFilas(filas);
            while (nroFila > 0) {
                filaProntos = filas.get(nroFila);
                iterator = filaProntos.iterator();
                
                while (iterator.hasNext()) {
                    BCP processo = iterator.next();
                    iterator.remove();
                    
                    processo.setPronto(false);
                    processo.setExecutando(true);
                    
                    // Round Robin
                    creditos = roundRobin(processo);
                    imprimirProcesso(processo);
                    
                    if (creditos > 0) {
                        if (processo.isPronto()) { filas.get(creditos).add(primeiroProcesso, processo); }
                        else if (processo.isBloqueado()) {  }
                        else if (processo.isConcluido()) { qtdProcessos--; }
                    }
                    
                    imprimirFilas(filas);
                }
                nroFila--;
                
                if ((verificaZeroCreditos()) && (qtdProcessos > 0)) {
                    redistribuicaoCreditos(filas);
                    nroFila = qtdFilas;
                }
            }
        }
    }
}
