package io.echosystem.ebr.constant;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public enum ModoEmissao {

    NORMAL(1),
    EPEC(4),
    CONTINGENCIA_FSDA(5),
    AUTORIZACAO_SVC_RS(7),
    AUTORIZACAO_SVC_SP(8);

    private final int codigoModoEmissao;

    private ModoEmissao(int codigoModoEmissao) {
        this.codigoModoEmissao = codigoModoEmissao;
    }

    public int getCodigo() {
        return this.codigoModoEmissao;
    }
    
    public static ModoEmissao identificarCodigoModoEmissao(int codigoModoEmissao){
        for(ModoEmissao m: ModoEmissao.values()){
            if(m.codigoModoEmissao == codigoModoEmissao){
                return m;
            }
        }
        return null;
    }

}
