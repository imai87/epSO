package escalonador;

import java.util.List;

public class BCP {
    
    private String nomePrograma;            // Nome do programa
    private List<String> refCodigo;         // Referencia a reg. da memoria em que esta o codigo do programa
    
    // Estado do processo
    private boolean executando = false;
    private boolean pronto = true;
    private boolean bloqueado = false;
    
    private boolean concluido = false;      // Flag para definicao da conclusao do processo
    
    private int prioridade;                 // Prioridade do processo
    private int creditos;                   // Credito do processo
    private int tempoDeEspera;
    
    private int PC = 0;                     // Contador de Programa
    private int X = 0;                      // Estado do registrador atual X
    private int Y = 0;                      // Estado do registrador atual Y

    public String getNomePrograma() {
        return nomePrograma;
    }

    public void setNomePrograma(String nomePrograma) {
        this.nomePrograma = nomePrograma;
    }

    public List<String> getRefCodigo() {
        return refCodigo;
    }

    public void setRefCodigo(List<String> refCodigo) {
        this.refCodigo = refCodigo;
    }

    public boolean isExecutando() {
        return executando;
    }

    public void setExecutando(boolean executando) {
        this.executando = executando;
    }

    public boolean isPronto() {
        return pronto;
    }

    public void setPronto(boolean pronto) {
        this.pronto = pronto;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public void setConcluido(boolean concluido) {
        this.concluido = concluido;
    }
   
    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }

    public void setTempoDeEspera(int tempoDeEspera) {
        this.tempoDeEspera = tempoDeEspera;
    }
    
    public int getTempoDeEspera() {
        return tempoDeEspera;
    }
    
    public int getPC() {
        return PC;
    }

    public void setPC(int PC) {
        this.PC = PC;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

}