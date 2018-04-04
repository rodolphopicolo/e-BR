package io.echosystem.ebr.exception;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public class CPFInvalido extends Exception {
    public CPFInvalido(String numeroCPF){
        super("CPF inválido " + numeroCPF);
    }
}
