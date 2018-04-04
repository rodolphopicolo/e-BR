package io.echosystem.ebr.pessoa;

import io.echosystem.ebr.exception.CNPJInvalido;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class CNPJ {
    private final String numeroCNPJ;
    
    public CNPJ(String _numeroCNPJ) throws CNPJInvalido{
        _numeroCNPJ = _numeroCNPJ.replace("\\D", "");
        while(_numeroCNPJ.length() < 14){
            _numeroCNPJ = "0" + _numeroCNPJ;
        }
        String numeroCNPJGerado = gerarNumeroCNPJ(_numeroCNPJ.substring(0, 12));
        if(numeroCNPJGerado.compareTo(_numeroCNPJ) != 0){
            throw new CNPJInvalido(_numeroCNPJ);
        }
        this.numeroCNPJ = _numeroCNPJ;
    }
    
    public String getNumeroCNPJ(){
        return this.numeroCNPJ;
    }
    
    public static String gerarNumeroCNPJ(String primeiros12Digitos) throws CNPJInvalido{
        if(primeiros12Digitos.matches("^\\d{12}$") == false){
            throw new CNPJInvalido(primeiros12Digitos);
        }
        {
            char[] chars = primeiros12Digitos.toCharArray();
            Set<Character> digitosDistintos = new HashSet();
            int length = chars.length;
            for(int i = 0; i < length; i++){
                digitosDistintos.add(chars[i]);
                if(digitosDistintos.size() > 1){
                    break;
                }
            }
            if(digitosDistintos.size() == 1){
                throw new CNPJInvalido(primeiros12Digitos);
            }
        }
        
        String numeroCNPJ = primeiros12Digitos;
        while(numeroCNPJ.length() < 14){
            int soma = 0;
            int peso = 2;
            int length = numeroCNPJ.length();
            for(int i = length - 1; i >= 0; i--){
                int digitoCNPJ = Character.getNumericValue(numeroCNPJ.charAt(i));
                soma += digitoCNPJ * peso;
                peso++;
                if(peso > 9){
                    peso = 2;
                }
            }
            int resto = soma % 11;
            int digito;
            if(resto < 2){
                digito = 0;
            } else {
                digito = 11 - resto;
            }
            numeroCNPJ += digito;
        }
        return numeroCNPJ;
    }
    
    
    public static boolean validarNumeroCNPJ(String numeroCNPJ){
        try {
            CNPJ cnpj = new CNPJ(numeroCNPJ);
        } catch (CNPJInvalido ex) {
            return false;
        }
        return true;
    }   
}
