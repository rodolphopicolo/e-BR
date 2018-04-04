package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class CNPJInvalido extends Exception {
    public CNPJInvalido(String numeroCNPJ){
        super("CNPJ inválido " + numeroCNPJ);
    }
}
