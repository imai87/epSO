/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Gabriel
 */
public class NewClass {
    public void escalonamento() {
        if (tabelaDeProcessos != null) {
            int qtdFilas = distribuicaoCreditos();
            int qtdProcessos = tabelaDeProcessos.size();
            Map< Integer, List<BCP> > filas = multiplasFilas(qtdFilas); 
            
            //imprimirFilas(filas);
            
            int nroFila = filas.size();
            while (nroFila > 0) {
                List<BCP> filaProntos = filas.get(nroFila);
                List<BCP> removeP = new LinkedList<>();
                
                for (BCP processo : filaProntos) {
                    processo.setPronto(false);
                    removeP.add(processo);
                    
                    // EXECUTANDO
                    processo.setExecutando(true);
            
                    double aux;
                    int n, PC, creditos;
            
                    PC = processo.getPC();
            
                    n = processo.getPrioridade() - processo.getCreditos();
                    aux = quantum * Math.pow(2, n);
                    
                    // ROUND ROBIN
                    String instrucao;
                    //System.out.println(processo.getNomePrograma());
                    while (aux > 0) {
                        // Executando...
                        instrucao = processo.getRefCodigo().get(PC);
                        //System.out.println(instrucao);
                        if (instrucao.equalsIgnoreCase("saida")) { 
                            tabelaDeProcessos.remove(processo);
                            removeP.remove(processo);
                            qtdProcessos--;
                            break; 
                        }
                        PC++;
                        processo.setPC(PC);
                        aux--;
                    }
                    System.out.println();
                    
                    // PRONTO
                    processo.setExecutando(false);
                    
                    processo.setPronto(true);
                    creditos = processo.getCreditos();
                    creditos--;
                    processo.setCreditos(creditos);
                   
                    if ((creditos > 0) && (tabelaDeProcessos.contains(processo))) { filas.get(creditos).add(processo); }
                    
                    imprimirFilas(filas);
                }
                filaProntos.removeAll(removeP);
                imprimirFilas(filas);
                nroFila--;
                
                System.out.println(removeP.size());
                System.out.println(qtdProcessos);
                if (removeP.size() == qtdProcessos) {
                    System.out.println("redistribuir");
                    redistribuicaoCreditos(filas, removeP);
                }
            }
            imprimirFilas(filas);
        }    
    }

}
