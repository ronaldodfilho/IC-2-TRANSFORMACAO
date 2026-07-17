public class ValidadorUtils {

    public static boolean isCnpjValido(String cnpj) {

        if (cnpj == null) return false;

        String cnpjLimpo = cnpj.replaceAll("\\D","");

        // Impede CNPJ com o mesmo número repetido (0000)
        if (cnpjLimpo.length() != 14 || cnpjLimpo.matches("(\\d)\\1{13}")) return false;

        try {
            int[] pesosDigito1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesosDigito2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

            // Soma dos 12 primeiros números
            int soma = 0;

            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesosDigito1[i];
            }

            int resto = soma % 11;
            int digito1Esperado = (resto < 2) ? 0 : 11 - resto;

            // Soma dos 13 primeiros números
            soma = 0;

            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesosDigito2[i];
            }

            resto = soma % 11;
            int digito2Esperado = (resto < 2) ? 0 : 11 - resto;

            // Verifica se o cálculo bate com os digitos do CNPJ
            return Character.getNumericValue(cnpjLimpo.charAt(12)) == digito1Esperado &&
                    Character.getNumericValue(cnpjLimpo.charAt(13)) == digito2Esperado;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isRazaoSocialValida(String razaoSocial) {
        return razaoSocial != null && !razaoSocial.trim().isEmpty();
    }

    public static boolean isValorPositivo(double valor) {
        return valor > 0.0;
    }
}