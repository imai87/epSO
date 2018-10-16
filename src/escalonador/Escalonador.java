package escalonador;

import java.util.*;
import java.io.*;

public class Escalonador {

    public static void main(String[] args) {
        
        String[] processos = {"01.txt", "02.txt", "03.txt", "04.txt", "05.txt",
                              "06.txt", "07.txt", "08.txt", "09.txt", "10.txt"};
        
        String arqPrioridade = "prioridades.txt";
        String arqQuantum = "quantum.txt";
        
        List<BCP> tabelaDeProcessos = new ArrayList<>();
        
        String code = "";
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
            
            quantum = Integer.parseInt(qRead.readLine());
            
            for (String processo : processos) {
                List<String> refCod = new ArrayList<>();
                
                arq = new FileReader(processo);
                read = new BufferedReader(arq);
                
                BCP bloco = new BCP();
                code = read.readLine();
                bloco.setNomePrograma(code);
                
                while (true) {
                    code = read.readLine();
                    if (code == null) { break; }
                    refCod.add(code);
                }
                
                bloco.setRefCodigo(refCod);
                
                prioridade = Integer.parseInt(pRead.readLine());
                bloco.setPrioridade(prioridade);
                
                tabelaDeProcessos.add(bloco);
                
                arq.close();
            }
            p.close();
            q.close();
        }
        catch(IOException e) {
            System.err.printf("Erro na abertura do arquivo: %s.\n", e.getMessage());
        }
        
        Escalonamento esc = new Escalonamento(tabelaDeProcessos, quantum);
        String log = "log";
        String valorQuantum = Integer.toString(quantum); 
        if (valorQuantum.length() == 1) { valorQuantum = "0" + valorQuantum; }
        String pontoTXT = ".txt";
        
        String nomeArq = log + valorQuantum + pontoTXT;
        
        try {
            FileWriter arqLog = new FileWriter(nomeArq);
            PrintWriter gravarLog = new PrintWriter(arqLog);
            
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
