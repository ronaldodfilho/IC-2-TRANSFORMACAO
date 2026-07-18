import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ValidadorService {

    public static List<RegistroDespesa> carregarCsv (Path caminhoArquivo) {

        List<RegistroDespesa> dadosValidados = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo.toFile()))){

            String linha;
            reader.readLine();

            while ((linha = reader.readLine()) != null) {

               String[] colunas = linha.split(";");

                if (colunas.length < 5) continue;

                String cnpj = colunas[0].trim();
                String razaoSocial = colunas[1].trim();
                String trimestre = colunas[2].trim();
                String ano = colunas[3].trim();
                Double valorDespesas = converterParaDouble(colunas[4].trim());

                if(!ValidadorUtils.isCnpjValido(cnpj)) {
                    System.out.println("CNPJ invalido: " + cnpj);
                    continue;
                }

                if (!ValidadorUtils.isRazaoSocialValida(razaoSocial)) {
                    System.out.println("Razão social inválida: " + razaoSocial);
                    continue;
                }

                if (!ValidadorUtils.isValorPositivo(valorDespesas)) {
                    System.out.println("Valor invalido: " + valorDespesas);
                    continue;
                }

                dadosValidados.add( new RegistroDespesa(cnpj,razaoSocial,trimestre,ano,valorDespesas) );
            }
        }
        catch (IOException e){
            throw new RuntimeException("Erro ao ler o aquivo" + caminhoArquivo, e);
        }
        return dadosValidados;
    }

    public static Map<String,OperadoraCadastro> carregarOperadoras(Path caminhoArquivo) {

        Map<String,OperadoraCadastro> operadoras = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(caminhoArquivo.toFile()))) {

            String linha;
            reader.readLine();

            while((linha = reader.readLine()) != null) {

                String[] colunas = linha.split(";");

                if (colunas.length < 11) continue;

                String regAns = colunas[0].replace("\"", "").trim();
                String cnpj = colunas[1].replace("\"", "").trim();
                String modalidade = colunas[4].replace("\"", "").trim();
                String uf = colunas[10].replace("\"", "").trim();

                operadoras.putIfAbsent(cnpj,new OperadoraCadastro(cnpj,regAns,modalidade,uf));

            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o aquivo da ANS" + caminhoArquivo, e);
        }
        return operadoras;
    }

    public static List<RegistroDespesaEnriquecido> enriquecerDados (List<RegistroDespesa> registroDespesas, Map<String,OperadoraCadastro> operadoras) {

        List<RegistroDespesaEnriquecido> dadosEnriquecidos = new ArrayList<>();

        for (RegistroDespesa registro : registroDespesas) {

            String cnpjLimpo = registro.getCnpj().replaceAll("\\D","");

            OperadoraCadastro cadastro = operadoras.get(cnpjLimpo);

            if (cadastro != null) {
                dadosEnriquecidos.add(new RegistroDespesaEnriquecido(
                        registro,
                        cadastro.getRegAns(),
                        cadastro.getModalidade(),
                        cadastro.getUf()));
            }
            else {
                dadosEnriquecidos.add(new RegistroDespesaEnriquecido(
                        registro,
                        "N/A",
                        "IGNORADO",
                        "IGNORADO"
                ));
            }
        }
        return dadosEnriquecidos;
    }

    public static void gerarDespesasAgregadas(List<RegistroDespesaEnriquecido> dados, Path caminhoSaida) {

        Map<String, Map<String,Double>> agrupamento = new HashMap<>();

        for (RegistroDespesaEnriquecido dado : dados){

            RegistroDespesa registro = dado.getRegistroDespesa();

            String chaveOperadora = registro.getRazaoSocial() + "|" + dado.getUf();
            String chaveTrimestre = registro.getAno() + "|" + registro.getTrimestre();

            Map<String,Double> valoresPorTrimestre = agrupamento.get(chaveOperadora);

            if (valoresPorTrimestre == null) {
                valoresPorTrimestre = new HashMap<>();
                agrupamento.put(chaveOperadora, valoresPorTrimestre);
            }

            double valorAtual = valoresPorTrimestre.getOrDefault(chaveTrimestre,0.0);

            valoresPorTrimestre.put(chaveTrimestre,valorAtual + registro.getValorDespesas());
        }

        List<DespesaAgregada> resultados = new ArrayList<>();

        for (Map.Entry<String, Map<String,Double>> entrada : agrupamento.entrySet()) {

            Map<String,Double> valoresPorTrimestre = entrada.getValue();
            double total = 0.0;

            for (double valor : valoresPorTrimestre.values()) {
                total += valor;
            }

            double media = total / valoresPorTrimestre.size();
            double somaQuadrados = 0.0;

            for (double valor : valoresPorTrimestre.values()) {
                double diferenca = valor - media;
                somaQuadrados += diferenca * diferenca;
            }

            double desvioPadrao = Math.sqrt(somaQuadrados / valoresPorTrimestre.size());

            String[] partes = entrada.getKey().split("\\|",2);

            String razaoSocial = partes[0];
            String uf = partes[1];

            String regAns = "N/A";
            String modalidade = "N/A";

            for (RegistroDespesaEnriquecido dado : dados){

                RegistroDespesa registro = dado.getRegistroDespesa();

                if (registro.getRazaoSocial().equals(razaoSocial) &&
                        (dado.getUf().equals(uf))) {

                    regAns = dado.getRegAns();
                    modalidade = dado.getModalidade();
                    break;
                    }
                }

            DespesaAgregada despesaAgregada = new DespesaAgregada(razaoSocial,regAns,modalidade,uf,total,media,desvioPadrao);
            resultados.add(despesaAgregada);

        }
        resultados.sort(Comparator.comparingDouble(DespesaAgregada::getTotalDespesas).reversed());

        salvarDespesasAgregadas(resultados, caminhoSaida);
    }

    private static void salvarDespesasAgregadas(List<DespesaAgregada> resultados, Path saida){

        try {

            Path pastaDestino = saida.getParent();

            if (pastaDestino != null) {
                Files.createDirectories(pastaDestino);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(saida)) {

                writer.write("RazaoSocial;RegistroANS;Modalidade;UF;TotalDespesas;MediaDespesasPorTrimestre;DesvioPadrao");
                writer.newLine();

                for (DespesaAgregada resultado : resultados){

                    String linha = String.format(
                            Locale.US,
                            "%s;%s;%s;%s;%.2f;%.2f;%.2f",
                            resultado.getRazaoSocial(),
                            resultado.getRegistroAns(),
                            resultado.getModalidade(),
                            resultado.getUf(),
                            resultado.getTotalDespesas(),
                            resultado.getMediaPorTrimestre(),
                            resultado.getDesvioPadrao()
                    );

                    writer.write(linha);
                    writer.newLine();
                }
                System.out.println("Arquivo salvo em: " + saida.toAbsolutePath());
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo", e);
        }
    }

    private static double converterParaDouble(String texto) {

        if (texto == null || texto.isEmpty()) return 0.0;
        String limpo = texto.trim().replace("\"", "");
        if (limpo.isEmpty() || limpo.equals("-")) return 0.0;

        limpo = limpo.replace(",",".");

        try {
            return Double.parseDouble(limpo);
        }
        catch (NumberFormatException e){
            return 0.0;
        }
    }
}
