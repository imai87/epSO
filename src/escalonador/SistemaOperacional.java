package escalonador;

import java.util.*;
import java.io.*;

public class SistemaOperacional {

    public static void main(String[] args) {
        
        // Acesso ao subdiretorio que contem os arquivos a serem lidos,
        // salvando-os adequadamente para tratamento posterior
        
        File subdiretorio = new File("processos");
        File[] arquivos = subdiretorio.listFiles();
        
        List<String> processos = new ArrayList<>();
        String arqPrioridade = null;
        String arqQuantum = null;
        
        for (File arquivo : arquivos) {
            if (arquivo.getName().equalsIgnoreCase("prioridades.txt")) { arqPrioridade = arquivo.getPath(); }
            else if (arquivo.getName().equalsIgnoreCase("quantum.txt")) { arqQuantum = arquivo.getPath(); }
            else { processos.add(arquivo.getPath()); }  
        }
        
        // Ordenacao pelo nome do arquivo
        Collections.sort(processos);
        
        // Inicializacao da Tabela de Processos para o armazenamento
        // de referencias aos BCPs respectivos
        
        List<BCP> tabelaDeProcessos = new ArrayList<>();
        
        String code;
        int prioridade;
        int quantum = -1;
            
        try {              
            // Leitura de arquivos relacionados aos processos
            FileReader arq;
            BufferedReader read;
            
            // Leitura de arquivo relacionado a prioridade
            FileReader p = new FileReader(arqPrioridade);
            BufferedReader pRead = new BufferedReader(p);
            
            // Leitura de arquivo relacionado ao quantum
            FileReader q = new FileReader(arqQuantum);
            BufferedReader qRead = new BufferedReader(q);
            
            // Estabelecimento do valor do quantum
            quantum = Integer.parseInt(qRead.readLine());
            
            // Atribuicao das caracteristicas de cada processo a partir
            // da leitura de seu arquivo correspondente
            
            for (String processo : processos) {
                List<String> refCod = new ArrayList<>();
                
                arq = new FileReader(processo);
                read = new BufferedReader(arq);
                
                BCP bloco = new BCP();
                
                // Obtendo-se o nome do programa
                code = read.readLine();
                bloco.setNomePrograma(code);
                
                // Armazenamento do codigo do programa executado,
                // funcionando como uma referencia ao mesmo
                
                while (true) {
                    code = read.readLine();
                    if (code == null) { break; }
                    refCod.add(code);
                }
                bloco.setRefCodigo(refCod);
                
                // Definicao da prioridade de cada processo a partir
                // da leitura do arquivo correspondente
                
                prioridade = Integer.parseInt(pRead.readLine());
                bloco.setPrioridade(prioridade);
                
                // Adicao da referencia ao BCP na Tabela De Processos
                tabelaDeProcessos.add(bloco);
                
                arq.close();
            }
            p.close();
            q.close();
        }
        catch(IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        
        // Construcao do logfile que corresponde a criacao de um
        // arquivo no formato "logXX.txt", em que XX representa o
        // valor do quantum escolhido
        
        String log = "log";
        String valorQuantum = Integer.toString(quantum); 
        if (valorQuantum.length() == 1) { valorQuantum = "0" + valorQuantum; }
        String pontoTXT = ".txt";
     
        String nomeArq = log + valorQuantum + pontoTXT;
        
        try {
            FileWriter arqLog = new FileWriter(nomeArq);
            PrintWriter gravarLog = new PrintWriter(arqLog);
            
            // Instancia de "Escalonamento" responsavel pelo procedimento
            // escalonador em si e pela gravacao das informacoes necessarias
            // ao logfile
            
            Escalonador esc = new Escalonador(tabelaDeProcessos, quantum);
            esc.escalonamento(gravarLog);
            
            arqLog.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }     
    }    
}
