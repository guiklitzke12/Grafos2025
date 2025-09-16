package grafos; // Declara o pacote onde a classe está localizada

import java.util.*; // Importa classes utilitárias como ArrayList, Arrays, List, etc.

/*
 * Autores:
 * Gabriel Schroeder Alfarth
 * Guilherme Ian
 *
 * Trabalho: Teoria dos Grafos - Trabalho Parcial 1
 * Profa: Patrícia Kayser Vargas Mangan
 *
 * Implementação dos métodos:
 * - tipoDoGrafo(int[][] adj)
 * - arestasDoGrafo(int[][] adj)
 * - grausDoVertice(int[][] adj)
 * - buscaEmProfundidade(int[][] adj)
 *
 * Observação: vértices indexados 0..n-1
 */
public class GraphUtils { // Declaração da classe pública chamada GraphUtils

    // -------------------------------------------------------------
    // MÉTODO: tipoDoGrafo
    // -------------------------------------------------------------
    // Analisa a matriz de adjacência e retorna uma descrição textual
    // sobre as propriedades do grafo: dirigido/não dirigido, simples ou multigrafo,
    // regular ou não, completo/não completo e nulo/não nulo.
    public static String tipoDoGrafo(int[][] adj) { // Recebe uma matriz de adjacência e retorna String
        int n = adj.length; // Número de vértices (tamanho da matriz)
        boolean directed = false;   // Inicialmente assume que não é dirigido
        boolean hasLoop = false;    // Inicialmente assume que não tem laços
        boolean multigraph = false; // Inicialmente assume que não tem múltiplas arestas
        boolean anyEdge = false;    // Inicialmente assume que não há arestas

        // Verifica se a matriz é quadrada
        for (int i = 0; i < n; i++) {
            if (adj[i].length != n) { // Se alguma linha não tiver tamanho n
                throw new IllegalArgumentException("Matriz não é quadrada"); // Erro
            }
        }

        // Percorre a matriz e checa propriedades
        for (int i = 0; i < n; i++) {
            if (adj[i][i] != 0) hasLoop = true; // Laço detectado se diagonal != 0
            for (int j = 0; j < n; j++) {
                if (adj[i][j] != 0) anyEdge = true; // Existe ao menos uma aresta
                if (adj[i][j] > 1) multigraph = true; // Mais de uma aresta entre i e j
                if (adj[i][j] != adj[j][i]) directed = true; // Se matriz não simétrica, é dirigido
            }
        }

        StringBuilder sb = new StringBuilder(); // StringBuilder para montar o resultado

        // 1. Dirigido ou não
        sb.append(directed ? "Dirigido" : "Não dirigido").append("; ");

        // 2. Simples ou multigrafo
        if (multigraph || hasLoop) sb.append("Multigrafo"); // Se houver laço ou multiaresta
        else sb.append("Simples"); // Caso contrário, simples
        sb.append("; ");

        // 3. Regularidade
        if (!directed) { // Para grafos não dirigidos
            int[] deg = new int[n]; // Array para armazenar o grau de cada vértice
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int j = 0; j < n; j++) sum += adj[i][j]; // Soma das arestas da linha
                sum += adj[i][i]; // Laço conta +1 extra
                deg[i] = sum; // Guarda grau do vértice i
            }
            // Verifica se todos os vértices têm o mesmo grau
            boolean allEqual = true;
            for (int i = 1; i < n; i++) if (deg[i] != deg[0]) { allEqual = false; break; }
            if (allEqual) sb.append("Regular (k=" + deg[0] + ")");
            else sb.append("Não regular");
        } else { // Para grafos dirigidos
            int[] indeg = new int[n], outdeg = new int[n]; // Grau de entrada e saída
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    outdeg[i] += adj[i][j]; // Soma linhas -> grau de saída
                    indeg[j] += adj[i][j];  // Soma colunas -> grau de entrada
                }
            }
            boolean inEqual = true, outEqual = true;
            for (int i = 1; i < n; i++) {
                if (indeg[i] != indeg[0]) inEqual = false;
                if (outdeg[i] != outdeg[0]) outEqual = false;
            }
            if (inEqual && outEqual) sb.append("Regular (in=" + indeg[0] + ", out=" + outdeg[0] + ")");
            else sb.append("Não regular");
        }
        sb.append("; ");

        // 4. Completo
        boolean completo = true;
        if (!directed) { // Para grafos não dirigidos
            for (int i = 0; i < n && completo; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j && adj[i][j] != 0) completo = false; // Laço invalida completo
                    else if (i != j && adj[i][j] <= 0) completo = false; // Aresta ausente
                }
            }
        } else { // Para grafos dirigidos
            for (int i = 0; i < n && completo; i++) {
                for (int j = 0; j < n; j++) {
                    if (i != j && adj[i][j] <= 0) completo = false; // Toda aresta i->j deve existir
                }
            }
        }
        sb.append(completo ? "Completo" : "Não completo").append("; ");

        // 5. Nulo ou não (sem arestas)
        sb.append(anyEdge ? "Não nulo" : "Nulo");

        return sb.toString(); // Retorna a descrição final do grafo
    }

    // -------------------------------------------------------------
    // MÉTODO: arestasDoGrafo
    // -------------------------------------------------------------
    // Lista todas as arestas do grafo, contando laços e multiarestas.
    // Também retorna o número total de arestas (m).
    public static String arestasDoGrafo(int[][] adj) {
        int n = adj.length; // Número de vértices
        boolean directed = false; // Inicialmente assume não dirigido

        // Verifica se o grafo é dirigido
        for (int i = 0; i < n && !directed; i++) {
            for (int j = 0; j < n; j++) {
                if (adj[i][j] != adj[j][i]) { directed = true; break; } // Não simétrico -> dirigido
            }
        }

        StringBuilder sb = new StringBuilder(); // StringBuilder para resultado
        List<String> edges = new ArrayList<>(); // Lista para armazenar arestas
        int m = 0; // Contador de arestas

        if (!directed) { // Caso não dirigido
            for (int i = 0; i < n; i++) {
                // Laços
                for (int k = 0; k < adj[i][i]; k++) {
                    edges.add("(" + i + "," + i + ")"); // Adiciona laço
                    m++; // Incrementa contagem
                }
                // Arestas simples (conta apenas i<j)
                for (int j = i + 1; j < n; j++) {
                    for (int k = 0; k < adj[i][j]; k++) { // Multiarestas
                        edges.add("(" + i + "," + j + ")"); // Adiciona aresta
                        m++;
                    }
                }
            }
            sb.append("m = ").append(m).append("; Arestas: ").append(edges.toString());
        } else { // Caso dirigido
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < adj[i][j]; k++) { // Multiarestas
                        edges.add("(" + i + "->" + j + ")"); // Adiciona aresta dirigida
                        m++;
                    }
                }
            }
            sb.append("m = ").append(m).append("; Arestas dirigidas: ").append(edges.toString());
        }

        return sb.toString(); // Retorna descrição das arestas
    }

    // -------------------------------------------------------------
    // MÉTODO: grausDoVertice
    // -------------------------------------------------------------
    // Calcula o grau de cada vértice (ou grau de entrada/saída se for dirigido).
    // Também mostra o grau máximo do grafo e a sequência de graus.
    public static String grausDoVertice(int[][] adj) {
        int n = adj.length; // Número de vértices
        boolean directed = false; // Inicialmente assume não dirigido

        // Verifica se é dirigido
        for (int i = 0; i < n && !directed; i++) {
            for (int j = 0; j < n; j++) {
                if (adj[i][j] != adj[j][i]) { directed = true; break; }
            }
        }

        StringBuilder sb = new StringBuilder(); // StringBuilder para resultado

        if (!directed) { // Grau em grafo não dirigido
            int[] deg = new int[n]; // Array de grau
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int j = 0; j < n; j++) sum += adj[i][j]; // Soma das arestas da linha
                sum += adj[i][i]; // Laço conta +1
                deg[i] = sum; // Armazena grau
            }
            int max = Arrays.stream(deg).max().orElse(0); // Calcula grau máximo

            sb.append("Grau do grafo (máximo grau) = ").append(max).append("\n");
            sb.append("Grau de cada vértice:\n");
            for (int i = 0; i < n; i++) {
                sb.append("v").append(i).append(": ").append(deg[i]).append("\n"); // Mostra grau de cada vértice
            }
            sb.append("Sequência de graus: ").append(Arrays.toString(deg)); // Sequência de graus
        } else { // Grafo dirigido
            int[] indeg = new int[n], outdeg = new int[n], total = new int[n]; // Arrays para graus
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    outdeg[i] += adj[i][j]; // Soma linha -> grau saída
                    indeg[j] += adj[i][j];  // Soma coluna -> grau entrada
                }
            }
            for (int i = 0; i < n; i++) total[i] = indeg[i] + outdeg[i]; // Total = in + out

            int maxTotal = Arrays.stream(total).max().orElse(0); // Máximo grau total

            sb.append("Grau do grafo (máximo grau total) = ").append(maxTotal).append("\n");
            sb.append("Grau de cada vértice (in, out, total):\n");
            for (int i = 0; i < n; i++) {
                sb.append("v").append(i)
                  .append(": (in=").append(indeg[i])
                  .append(", out=").append(outdeg[i])
                  .append(", total=").append(total[i])
                  .append(")\n"); // Mostra grau de cada vértice
            }
            sb.append("Sequência de graus totais: ").append(Arrays.toString(total)); // Sequência total
        }

        return sb.toString(); // Retorna resultado final
    }

    // -------------------------------------------------------------
    // MÉTODO: buscaEmProfundidade (DFS)
    // -------------------------------------------------------------
    // Realiza uma busca em profundidade (DFS) e retorna a ordem
    // de exploração dos vértices (0-based).
    public static String buscaEmProfundidade(int[][] adj) {
        int n = adj.length; // Número de vértices
        boolean[] visited = new boolean[n]; // Marca os vértices visitados
        List<Integer> order = new ArrayList<>(); // Guarda a ordem da DFS

        // Executa DFS em cada componente conexo
        for (int v = 0; v < n; v++) {
            if (!visited[v]) dfsUtil(v, adj, visited, order); // Chama função recursiva
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Ordem de exploração (0-based): ");
        for (int i = 0; i < order.size(); i++) {
            sb.append(order.get(i)); // Adiciona vértice à saída
            if (i < order.size() - 1) sb.append(" -> "); // Formata com seta
        }
        return sb.toString(); // Retorna ordem de exploração
    }

    // Função auxiliar recursiva da DFS
    private static void dfsUtil(int u, int[][] adj, boolean[] visited, List<Integer> order) {
        visited[u] = true; // Marca vértice como visitado
        order.add(u); // Adiciona à ordem
        int n = adj.length;
        for (int v = 0; v < n; v++) {
            if (adj[u][v] > 0 && !visited[v]) { // Se há aresta e não visitado
                dfsUtil(v, adj, visited, order); // Chamada recursiva
            }
        }
    }

    // -------------------------------------------------------------
    // MAIN DEMONSTRATIVO
    // -------------------------------------------------------------
    public static void main(String[] args) {
        // Exemplo 1: Triângulo (K3)
        int[][] exemplo1 = {
            {0,1,1},
            {1,0,1},
            {1,1,0}
        };

        // Exemplo 2: Grafo dirigido com múltiplas arestas
        int[][] exemplo2 = {
            {0,2,0},
            {0,0,1},
            {1,0,0}
        };

        // Exemplo 3: Grafo nulo (sem arestas)
        int[][] exemplo3 = {
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0},
            {0,0,0,0}
        };
        
        // Exemplo 4: Grafo da lousa (com multiaresta e laço)
        int[][] exemplo4 = {
            {0,1,1,0},
            {1,1,0,2},
            {1,0,0,1},
            {0,2,1,0}
        };

        // Testes de saída
        System.out.println("=== Exemplo 1 ===");
        System.out.println(tipoDoGrafo(exemplo1)); // Mostra tipo do grafo
        System.out.println(arestasDoGrafo(exemplo1)); // Mostra arestas
        System.out.println(grausDoVertice(exemplo1)); // Mostra graus
        System.out.println(buscaEmProfundidade(exemplo1)); // Mostra DFS
        System.out.println();

        System.out.println("=== Exemplo 2 ===");
        System.out.println(tipoDoGrafo(exemplo2));
        System.out.println(arestasDoGrafo(exemplo2));
        System.out.println(grausDoVertice(exemplo2));
        System.out.println(buscaEmProfundidade(exemplo2));
        System.out.println();

        System.out.println("=== Exemplo 3 ===");
        System.out.println(tipoDoGrafo(exemplo3));
        System.out.println(arestasDoGrafo(exemplo3));
        System.out.println(grausDoVertice(exemplo3));
        System.out.println(buscaEmProfundidade(exemplo3));
        
        System.out.println("=== Exemplo 4 (Professora) ===");
        System.out.println(tipoDoGrafo(exemplo4));
        System.out.println(arestasDoGrafo(exemplo4));
        System.out.println(grausDoVertice(exemplo4));
        System.out.println(buscaEmProfundidade(exemplo4));
        System.out.println();
    }
}
