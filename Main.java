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
    
    static String cores = "\033[0;40m \033[0;41m \033[0;42m \033[0;43m \033[0;44m \033[0;45m \033[0;46m \033[0;47m \033[1;40m \033[1;41m \033[1;42m \033[1;43m \033[1;44m \033[1;45m \033[1;46m \033[1;47m";
    static List<String> listacor = List.of(cores.split(" "));
    static String cornull = "\033[0m";
    static String tempdiag = "| ";
    static String diagrama = "| ";
    static String txtdiagrama = "| ";

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
                Processo p = new Processo("P" + i, listacor.get(i-1), Integer.parseInt(partes[1]), Lista_IO, Integer.parseInt(partes[2]));
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

    public static void PrintarDiagrama(Processo CPU, int tempo_atual){
        if(CPU == null){
            diagrama +=  cornull+ "xx |";
            txtdiagrama += "xx |";
        }else{
            diagrama += CPU.cor + CPU + cornull+ " | ";
            txtdiagrama += CPU + " | ";
        }

        if(tempo_atual < 10){
            tempdiag += "0"+tempo_atual + " | ";
        }else{
            tempdiag += tempo_atual + " | ";
        }
        System.out.println(tempdiag);
        System.out.println(diagrama);
        
    }

    public static void PrintTempoEspera(List<Processo> Finalizados, List<String> textos){
        int tot_temp = 0;
        String frase = "\n";
        for (Processo processo : Finalizados) {
            tot_temp += processo.tempoEspera();
            frase += "Tempo de Espera de "+processo+": "+processo.tempoEspera() + "\n";
        }
        // System.out.println("tam: "+Finalizados.size());
        frase += "Tempo de Espera Médio: "+ (float)tot_temp/Finalizados.size();
        textos.add(frase);
        System.out.println(frase);
    }

    public static void RoundRobin(int Quantum, int Quantum_Limite, Map<Integer, List<Processo> > DicProcess, List<Processo> Fila_CPU){
        int tempo_atual = 0;
        boolean estadoCPU=true;
        boolean IOCPU=false;
        boolean rodando = true;
        Processo CPU = null;
        List<String> textos = new ArrayList<>();
        List<String> Eventos = new ArrayList<>();
        List<Processo> Chegadas = new ArrayList<>();
        List<Processo> Finalizados = new ArrayList<>();

        textos.add("***********************************\n" + //
                        "***** ESCALONADOR ROUND ROBIN *****\n" + //
                        "-----------------------------------\n" + //
                        "------- INICIANDO SIMULACAO -------\n" + //
                        "-----------------------------------");
        
        System.out.println(textos.get(0));

        while(rodando){
            Chegadas.clear();
            Eventos.clear();



            //________Rodando Processo_________
            if (CPU != null) {
                estadoCPU = CPU.AtualizarProcesso();
                IOCPU = CPU.conferirIO();
                Quantum++;
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
                    Finalizados.add(CPU);
                    Quantum = 0;
                }
                else if(IOCPU){
                    //processo com IO
                    Eventos.add("OPERAÇÃO I/O <"+CPU+">");
                    Quantum = 0;
                    Fila_CPU.add(CPU);
                }
                
                if(Fila_CPU.size() != 0){
                    //fila contem processos e troca de processo
                    if(tempo_atual != 0){
                        CPU.addFim(tempo_atual);
                    }
                    CPU = Fila_CPU.get(0);
                    Fila_CPU.remove(0);
                    CPU.addInicio(tempo_atual);
                }else{
                    //fila vazia e CPU finalizado
                    if(!estadoCPU){
                        CPU = null;
                        Quantum = 0;
                    }
                }
            }
            //__________Chegada de Processos____________
            if(DicProcess.containsKey(tempo_atual)){
                
                for (Processo p : DicProcess.get(tempo_atual)) {
                    Fila_CPU.add(p);
                    Eventos.add("CHEGADA <"+p+">");
                    
                }
                DicProcess.remove(tempo_atual);
            }

            //______ Prints______
            textos.add("********** TEMPO "+tempo_atual+" **************");
            System.out.println("********** TEMPO "+tempo_atual+" **************");
            System.out.println("Quantum: "+Quantum);
            Evento(Eventos, textos);
            PrintarFila(Fila_CPU,textos);
            PrintarCPU(CPU,textos);

            PrintarDiagrama(CPU, tempo_atual);

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
                String fim = "-----------------------------------\n" +
                             "------- Encerrando simulacao ------\n" + 
                             "-----------------------------------";
                textos.add(fim);
                System.out.println(fim);
                rodando = false;
                break;
            }          
        }
        
        PrintTempoEspera(Finalizados, textos);
        textos.add("\n***********************************\n"+
                   "******** DIAGRAMA DE GANTT ********\n"+
                   "***********************************\n");
        textos.add(tempdiag);

        textos.add(txtdiagrama);

        EscreveArq escritor = new EscreveArq(textos);
        escritor.escrever();
        // System.out.println(textos);
    }

    public static void PrintarCPU(Processo CPU, List<String> textos){
        String frase;
        if(CPU == null){
            frase = "ACABARAM OS PROCESSOS!!!";
        }else{
            frase = "CPU: "+CPU+"("+CPU.getTempo_atual()+")";
        }
        textos.add(frase);
        System.out.println(frase);        
    }

    public static void PrintarFila(List<Processo> Fila, List<String> textos){
        String frase = "FILA: ";
        if(Fila.size() == 0){
            frase += "Não há processos na fila";
        }else{
            for (Processo processo : Fila) {
                frase += processo+"("+processo.getTempo_atual()+") ";
            }
        }
        System.out.println(frase);
        textos.add(frase);
    }

    
    public static void Evento(List<String> Eventos, List<String> textos){
        for (String evento : Eventos) {
            textos.add("#[evento] "+ evento);
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
        Lista_Processos = LeitorArquivo("./prova.txt");

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