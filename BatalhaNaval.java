import java.util.Random;
import java.util.Scanner;

public class BatalhaNaval {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        char[][] tabuleiroVisivel = criarTabuleiro();
        char[][] tabuleiroNavios = criarTabuleiro();
        int[][] idsNavios = criarMatrizIds();

        char[] tiposNavio = new char[10];
        int[] tamanhosNavio = new int[10];
        preencherTiposETamanhos(tiposNavio, tamanhosNavio);

        int[] celulasRestantes = new int[tamanhosNavio.length];
        for (int i = 0; i < tamanhosNavio.length; i++) {
            celulasRestantes[i] = tamanhosNavio[i];
        }

        posicionarTodosNavios(tabuleiroNavios, idsNavios, tiposNavio, tamanhosNavio, random);

        int tentativas = 0;
        int acertos = 0;
        int erros = 0;
        int naviosAfundados = 0;
        int totalNavios = tamanhosNavio.length;

        while (tentativas < 30 && naviosAfundados < totalNavios) {
            mostrarEstatisticasRodada(tentativas, acertos, naviosAfundados);
            exibirTabuleiro(tabuleiroVisivel);

            int linha = lerCoordenada(scanner, "Digite a linha (0-7): ");
            int coluna = lerCoordenada(scanner, "Digite a coluna (0-7): ");

            if (!coordenadaValida(linha, coluna)) {
                System.out.println("POSIÇÃO INVÁLIDA! Digite valores entre 0 e 7.\n");
                continue;
            }

            if (tabuleiroVisivel[linha][coluna] == 'A' || tabuleiroVisivel[linha][coluna] == 'X') {
                System.out.println("Você já atacou essa posição! Tente outra.\n");
                continue;
            }

            tentativas++;

            if (tabuleiroNavios[linha][coluna] != '~') {
                tabuleiroVisivel[linha][coluna] = 'A';
                acertos++;

                int idNavio = idsNavios[linha][coluna];
                celulasRestantes[idNavio]--;
                System.out.println("ACERTOU! Um navio foi atingido!");

                if (celulasRestantes[idNavio] == 0) {
                    naviosAfundados++;
                    String nomeNavio = nomeDoNavio(tiposNavio[idNavio]);
                    System.out.println("AFUNDOU! Você destruiu um " + nomeNavio + "!");
                }
            } else {
                tabuleiroVisivel[linha][coluna] = 'X';
                erros++;
                System.out.println("ERROU! Nenhum navio nessa posição.");
            }

            System.out.println();
        }

        boolean vitoria = naviosAfundados == totalNavios;
        exibirResumoFinal(tentativas, acertos, erros, naviosAfundados, totalNavios, tiposNavio, celulasRestantes, vitoria, tabuleiroVisivel, tabuleiroNavios);

        scanner.close();
    }

    private static char[][] criarTabuleiro() {
        char[][] tabuleiro = new char[8][8];
        for (int i = 0; i < tabuleiro.length; i++) {
            for (int j = 0; j < tabuleiro[i].length; j++) {
                tabuleiro[i][j] = '~';
            }
        }
        return tabuleiro;
    }

    private static int[][] criarMatrizIds() {
        int[][] ids = new int[8][8];
        for (int i = 0; i < ids.length; i++) {
            for (int j = 0; j < ids[i].length; j++) {
                ids[i][j] = -1;
            }
        }
        return ids;
    }

    private static void preencherTiposETamanhos(char[] tipos, int[] tamanhos) {
        int indice = 0;

        tipos[indice] = 'P';
        tamanhos[indice] = 4;
        indice++;

        for (int i = 0; i < 2; i++) {
            tipos[indice] = 'C';
            tamanhos[indice] = 3;
            indice++;
        }

        for (int i = 0; i < 3; i++) {
            tipos[indice] = 'D';
            tamanhos[indice] = 2;
            indice++;
        }

        for (int i = 0; i < 4; i++) {
            tipos[indice] = 'S';
            tamanhos[indice] = 1;
            indice++;
        }
    }

    private static void posicionarTodosNavios(char[][] tabuleiro, int[][] ids, char[] tipos, int[] tamanhos, Random random) {
        for (int i = 0; i < tipos.length; i++) {
            posicionarNavio(tabuleiro, ids, i, tipos[i], tamanhos[i], random);
        }
    }

    private static void posicionarNavio(char[][] tabuleiro, int[][] ids, int id, char tipo, int tamanho, Random random) {
        boolean colocado = false;

        while (!colocado) {
            boolean horizontal = random.nextBoolean();
            int linha = random.nextInt(8);
            int coluna = random.nextInt(8);

            if (horizontal && coluna + tamanho > 8) {
                continue;
            }
            if (!horizontal && linha + tamanho > 8) {
                continue;
            }

            if (posicaoLivre(tabuleiro, linha, coluna, tamanho, horizontal)) {
                for (int i = 0; i < tamanho; i++) {
                    int linhaDestino = horizontal ? linha : linha + i;
                    int colunaDestino = horizontal ? coluna + i : coluna;
                    tabuleiro[linhaDestino][colunaDestino] = tipo;
                    ids[linhaDestino][colunaDestino] = id;
                }
                colocado = true;
            }
        }
    }

    private static boolean posicaoLivre(char[][] tabuleiro, int linha, int coluna, int tamanho, boolean horizontal) {
        for (int i = 0; i < tamanho; i++) {
            int linhaDestino = horizontal ? linha : linha + i;
            int colunaDestino = horizontal ? coluna + i : coluna;
            if (tabuleiro[linhaDestino][colunaDestino] != '~') {
                return false;
            }
        }
        return true;
    }

    private static void mostrarEstatisticasRodada(int tentativas, int acertos, int naviosAfundados) {
        double taxa = tentativas == 0 ? 0.0 : ((double) acertos / tentativas) * 100.0;
        int numeroRodada = tentativas + 1;
        System.out.printf("Tentativa %d/30 | Acertos: %d | Taxa: %.2f%% | Navios afundados: %d%n", numeroRodada, acertos, taxa, naviosAfundados);
    }

    private static void exibirTabuleiro(char[][] tabuleiro) {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < tabuleiro.length; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < tabuleiro[i].length; j++) {
                System.out.print(tabuleiro[i][j]);
                if (j < tabuleiro[i].length - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    private static int lerCoordenada(Scanner scanner, String mensagem) {
        System.out.print(mensagem);
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Digite um número entre 0 e 7: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static boolean coordenadaValida(int linha, int coluna) {
        return linha >= 0 && linha < 8 && coluna >= 0 && coluna < 8;
    }

    private static String nomeDoNavio(char tipo) {
        switch (tipo) {
            case 'P':
                return "Porta-aviões";
            case 'C':
                return "Cruzador";
            case 'D':
                return "Destroyer";
            default:
                return "Submarino";
        }
    }

    private static void exibirResumoFinal(int tentativas, int acertos, int erros, int naviosAfundados, int totalNavios, char[] tiposNavio, int[] celulasRestantes, boolean vitoria, char[][] tabuleiroVisivel, char[][] tabuleiroNavios) {
        int pontosAcertos = acertos * 10;
        int pontosNavios = naviosAfundados * 50;
        int penalidadeErros = erros * -2;
        int bonusRapido = (vitoria && tentativas < 25) ? 100 : 0;
        int pontuacaoFinal = pontosAcertos + pontosNavios + penalidadeErros + bonusRapido;

        String classificacao;
        if (pontuacaoFinal > 400) {
            classificacao = "EXCELENTE";
        } else if (pontuacaoFinal >= 300) {
            classificacao = "BOM";
        } else if (pontuacaoFinal >= 200) {
            classificacao = "REGULAR";
        } else {
            classificacao = "PRECISA MELHORAR";
        }

        System.out.println("========================================");
        System.out.println("ESTATÍSTICAS FINAIS");
        System.out.println("========================================");
        System.out.println("Status: " + (vitoria ? "VITÓRIA!" : "DERROTA."));
        System.out.printf("Tentativas usadas: %d/30%n", tentativas);
        System.out.printf("Total de acertos: %d%n", acertos);
        System.out.printf("Total de erros: %d%n", erros);
        double taxa = tentativas == 0 ? 0.0 : ((double) acertos / tentativas) * 100.0;
        System.out.printf("Taxa de acerto: %.2f%%%n", taxa);
        System.out.printf("Navios afundados: %d/%d%n", naviosAfundados, totalNavios);

        int portaAvioesAfundados = contarNaviosAfundadosPorTipo('P', tiposNavio, celulasRestantes);
        int cruzadoresAfundados = contarNaviosAfundadosPorTipo('C', tiposNavio, celulasRestantes);
        int destroyersAfundados = contarNaviosAfundadosPorTipo('D', tiposNavio, celulasRestantes);
        int submarinosAfundados = contarNaviosAfundadosPorTipo('S', tiposNavio, celulasRestantes);

        System.out.println("- Porta-aviões: " + portaAvioesAfundados + "/1");
        System.out.println("- Cruzadores: " + cruzadoresAfundados + "/2");
        System.out.println("- Destroyers: " + destroyersAfundados + "/3");
        System.out.println("- Submarinos: " + submarinosAfundados + "/4");

        System.out.println("PONTUAÇÃO FINAL: " + pontuacaoFinal + " pontos");
        System.out.println("- Acertos: " + acertos + " × 10 = " + pontosAcertos);
        System.out.println("- Navios afundados: " + naviosAfundados + " × 50 = " + pontosNavios);
        System.out.println("- Penalidade erros: " + erros + " × -2 = " + penalidadeErros);
        if (bonusRapido > 0) {
            System.out.println("- Bônus vitória rápida: +" + bonusRapido);
        }
        System.out.println("Classificação: " + classificacao + "!");
        System.out.println("========================================");

        System.out.println("\nTabuleiro final (navios revelados):");
        exibirTabuleiroFinal(tabuleiroVisivel, tabuleiroNavios);
    }

    private static int contarNaviosAfundadosPorTipo(char tipo, char[] tiposNavio, int[] celulasRestantes) {
        int total = 0;
        for (int i = 0; i < tiposNavio.length; i++) {
            if (tiposNavio[i] == tipo && celulasRestantes[i] == 0) {
                total++;
            }
        }
        return total;
    }

    private static void exibirTabuleiroFinal(char[][] tabuleiroVisivel, char[][] tabuleiroNavios) {
        System.out.println("  0 1 2 3 4 5 6 7");
        for (int i = 0; i < tabuleiroVisivel.length; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < tabuleiroVisivel[i].length; j++) {
                char marcaJogador = tabuleiroVisivel[i][j];
                if (marcaJogador == 'A' || marcaJogador == 'X') {
                    System.out.print(marcaJogador);
                } else if (tabuleiroNavios[i][j] != '~') {
                    System.out.print(tabuleiroNavios[i][j]);
                } else {
                    System.out.print('~');
                }

                if (j < tabuleiroVisivel[i].length - 1) {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
