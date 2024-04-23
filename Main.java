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
        Processo CPU = null;
        Fila_CPU.remove(0);
        List<Processo> Chegadas = new ArrayList<>();
        List<String> Eventos = new ArrayList<>();


        while(rodando){
            Chegadas.clear();
            Eventos.clear();

            //________Rodando Processo_________
            if (CPU != null) {
                estadoCPU = CPU.AtualizarProcesso();
                IOCPU = CPU.conferirIO();
                Quantum++;
            }

            //__________Chegada de Processos____________
            if(DicProcess.containsKey(tempo_atual)){
                
                for (Processo p : DicProcess.get(tempo_atual)) {
                    Fila_CPU.add(p);
                    Eventos.add("CHEGADA <"+p+">");
                    
                }
                DicProcess.remove(tempo_atual);
            }


            //_________Troca de Processos_____________
            if (Quantum == Quantum_Limite || !estadoCPU || IOCPU || CPU == null) {
                
                if (CPU == null) {
                    
                }
                else if (Quantum == Quantum_Limite) {
                    //fim do quantum
                    //if(estadoCPU == true){
                        Eventos.add("FIM QUANTUM <"+CPU+">");
                        Fila_CPU.add(CPU);
                    //}
                    Quantum = 0;
                }
                else if(!estadoCPU){
                    //processo finalizado
                    Eventos.add("ENCERRANDO <"+CPU+">");
                    Quantum = 0;
                }
                else if(IOCPU){
                    //processo com IO
                    Eventos.add("IO <"+CPU+">");
                    Quantum = 0;
                    Fila_CPU.add(CPU);
                }
                
                if(Fila_CPU.size() != 0){
                    //fila contem processos e troca de processo
                    CPU = Fila_CPU.get(0);
                    Fila_CPU.remove(0);
                }else{
                    //fila vazia e CPU finalizado
                    if(!estadoCPU){
                        CPU = null;
                        Quantum = 0;
                    }
                }
            }
            

            //______ Prints______
            System.out.println("********** TEMPO "+tempo_atual+" **************");
            System.out.println("Quantum: "+Quantum);
            Evento(Eventos);

            PrintarFila(Fila_CPU);
            PrintarCPU(CPU);

            //______Delay______
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            tempo_atual++;

            //______Encerrando______
            if(Fila_CPU.size() == 0 && DicProcess.size() == 0 && CPU==null){
                //fila vazia + processos vazios + CPU finalizada
                System.err.println("-----------------------------------");
                System.err.println("------- Encerrando simulacao ------");
                System.err.println("-----------------------------------");
                rodando = false;
                break;
            }

            

            
        }
        
        
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

    
    public static void Evento(List<String> Eventos){
        for (String evento : Eventos) {
            System.out.println("#[evento] "+ evento);
        }
        
    }

    public static void main(String[] args) {
        //_____Variaveis_____
        int Quantum = 0;
        int Quantum_Limite = 4;
        int Tempo_Atual = 0;
        List<Processo> Fila_CPU = new ArrayList<>();
        List<Processo> Lista_Processos = new ArrayList<>();
        Map<Integer, List<Processo>> Lista_Chegadas = new HashMap<>();

        //___ Leitura de Arquivo e Geração de Processos
        Lista_Processos = LeitorArquivo("./teste.txt");

        //___ Gerando Lista de Chegadas ____
        Lista_Chegadas = GerarListaChegadas(Lista_Processos);

        
        Fila_CPU = AddFila(Tempo_Atual, Lista_Chegadas);

        Lista_Chegadas.remove(Tempo_Atual);

        RoundRobin(Quantum, Quantum_Limite, Lista_Chegadas, Fila_CPU);

        // Evento("CHEGADA <" +Fila_CPU.get(0)+ ">" );
        // System.out.println("FILA: "+Fila_CPU);
        // System.out.println("CPU: "+cpu+"("+cpu.getTempo_atual()+")");

    }


}