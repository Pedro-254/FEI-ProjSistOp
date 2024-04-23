// Array
import java.util.ArrayList;
import java.util.List;

//Dicionário
import java.util.HashMap;
import java.util.Map;

// Leitura de Arquivos
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Collections;

public class Main{
    

    public static ArrayList<Processo> LeitorArquivo(String CaminhoArquivo){
        ArrayList<Processo> Lista = new ArrayList<>();
        try {
            BufferedReader leitor = new BufferedReader(new FileReader(CaminhoArquivo));
            String linha;

            // Lê cada linha do arquivo e coloca na variavel Linha
            for (int i = 1; (linha = leitor.readLine()) != null; i++) {
                String[] partes = linha.split(" ");

                ArrayList<Integer> Lista_IO = new ArrayList<>();
                if (partes.length == 4) {
                    //Gerando Lista de IO
                    String[] lista_io_STRING = partes[3].split(",");
                    
                    for (String IO : lista_io_STRING) {
                        Lista_IO.add(Integer.parseInt(IO));
                    }
                }else{
                    Lista_IO.add(null);
                }
                
                
                // Gerando Pocesso
                Processo p = new Processo("P" + i, Integer.parseInt(partes[1]), Lista_IO, Integer.parseInt(partes[2]));
                Lista.add(p);
            }

            leitor.close();
            
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return Lista;
    
    }

    public static Map<Integer, List<Processo>> GerarListaChegadas(List<Processo> Lista_Processos) {
        Map<Integer, List<Processo>> ListaChegadas = new HashMap<>();
        
        for (Processo processo : Lista_Processos) {
            List<Processo> Lista = new ArrayList<>();
            if (ListaChegadas.containsKey(processo.chegada)) {
                Lista = ListaChegadas.get(processo.chegada);
                Lista.add(processo);
                ListaChegadas.put(processo.chegada, Lista);
            }else{
                Lista.add(processo);
                ListaChegadas.put(processo.chegada, Lista);
            }
        }
        return ListaChegadas;
    }

    public static List<Processo> AddFila(int tempo, Map<Integer, List<Processo>> Lista_Chegadas) {
        List<Processo> Chegou = new ArrayList<>();
        
        if (Lista_Chegadas.containsKey(tempo)) {
            Chegou = Lista_Chegadas.get(tempo);
        }else{
            Chegou = new ArrayList<>();
        }

        return Chegou;
    }

    public static void RoundRobin(int Quantum, int Quantum_Limite, Map<Integer, List<Processo> > DicProcess, List<Processo> Fila_CPU){
        boolean estadoCPU=true;
        boolean IOCPU=false;
        boolean rodando = true;
        int tempo_atual = 0;
        Processo CPU = Fila_CPU.get(0);
        Fila_CPU.remove(0);

        while(rodando){
            System.out.println("********** TEMPO "+tempo_atual+" **************");
            System.out.println("Quantum: "+Quantum);
            if(DicProcess.containsKey(tempo_atual)){
                //chegada de processo
                for (Processo p : DicProcess.get(tempo_atual)) {
                    Fila_CPU.add(p);
                    Evento("CHEGADA <"+p+">");
                }
                DicProcess.remove(tempo_atual);
            }
            if (Quantum == Quantum_Limite || !estadoCPU || IOCPU) {
                //troca de processo ( quando o quantum acabar ou processo finalizar ou IO)

                if(!estadoCPU){
                    //processo finalizado
                    Evento("ENCERRANDO <"+CPU+">");

                }
                if(IOCPU){
                    //processo com IO
                    Evento("IO <"+CPU+">");
                    Fila_CPU.add(CPU);
                }
                else{
                    //fim do quantum
                    if(estadoCPU){
                        Evento("FIM QUANTUM <"+CPU+">");
                        Fila_CPU.add(CPU);
                    }
                    Quantum = 0;
                }
                
                if(Fila_CPU.size() != 0){
                    //fila contem processos e troca de processo
                    CPU = Fila_CPU.get(0);
                    Fila_CPU.remove(0);
                }else{
                    //fila vazia
                    if(!estadoCPU){
                        //CPU com processo
                        CPU = null;
                    }
                }
            }else{
                //continua com o mesmo processo
                Quantum++;
            }
            
            PrintarFila(Fila_CPU);
            PrintarCPU(CPU);
            
            
            tempo_atual++;
            if (CPU != null) {
                estadoCPU = CPU.AtualizarProcesso();
                IOCPU = CPU.conferirIO();
            }

            if(Fila_CPU.size() == 0 && DicProcess.size() == 0 && CPU==null){
                //fila vazia + processos vazios + CPU finalizada
                System.err.println("-----------------------------------");
                System.err.println("------- Encerrando simulacao ------");
                System.err.println("-----------------------------------");
                rodando = false;
            }
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        //falta rodar pra testar muchoo medo
        
    }

    public static void PrintarCPU(Processo CPU){
        if(CPU == null){
            System.out.println("ACABARAM OS PROCESSOS!!!");
        }else{
            System.out.println("CPU: "+CPU+"("+CPU.getTempo_atual()+")");
        }        
    }

    public static void PrintarFila(List<Processo> Fila){
        System.out.print("FILA: ");
        if(Fila.size() == 0){
            System.out.print("Não há processos na fila");
        }else{
            for (Processo processo : Fila) {
                System.out.print(processo+"("+processo.getTempo_atual()+") ");
            }
        }
        System.out.println();
    }

    // a função só printa o evento
    // pensei em fazer um for para printar a lista de eventos
    public static void Evento(String evento){
        System.out.println("#[evento] "+ evento);
    }

    public static void main(String[] args) {
        //_____Variaveis_____
        int Quantum = 0;
        int Quantum_Limite = 4;
        int Tempo_Atual = 0;
        List<Processo> Fila_CPU = new ArrayList<>();
        List<Processo> Lista_Processos = new ArrayList<>();
        Map<Integer, List<Processo>> Lista_Chegadas = new HashMap<>();
        List<String> Lista_Eventos = new ArrayList<>();
        //___ Leitura de Arquivo e Geração de Processos
        Lista_Processos = LeitorArquivo("./arquivo1.txt");

        //___ Gerando Lista de Chegadas ____
        Lista_Chegadas = GerarListaChegadas(Lista_Processos);
        //___Lista para usar na conferencia de chegada de intens____
        List<Integer> Lista_Chegadas_Sorted = new ArrayList<>();

        for (Integer chave : Lista_Chegadas.keySet()) {
            Lista_Chegadas_Sorted.add(chave);
        }
        Collections.sort(Lista_Chegadas_Sorted);
        System.out.println(Fila_CPU.size());
        
        Fila_CPU = AddFila(Tempo_Atual, Lista_Chegadas);

        Lista_Chegadas.remove(Tempo_Atual);

        RoundRobin(Quantum, Quantum_Limite, Lista_Chegadas, Fila_CPU);

        // Evento("CHEGADA <" +Fila_CPU.get(0)+ ">" );
        // System.out.println("FILA: "+Fila_CPU);
        // System.out.println("CPU: "+cpu+"("+cpu.getTempo_atual()+")");

    }


}