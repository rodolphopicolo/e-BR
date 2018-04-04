package io.echosystem.ebr.constant;

/**
 *
 * @author Rodolpho Picolo <rodolphopicolo@gmail.com>
 */
public enum TipoDocumento {
    CTE(57), NFE(55), MDFE(58);

    private final int codigoFiscalModeloDocumento;
    private TipoDocumento(int codigoFiscalModeloDocumento){
        this.codigoFiscalModeloDocumento = codigoFiscalModeloDocumento;
    }
    public int getCodigoFiscalDocumento(){
        return this.codigoFiscalModeloDocumento;
    }
    
    public static TipoDocumento identificarModeloDocumentoFiscal(int codigoFiscalDocumento){
        for(TipoDocumento td: TipoDocumento.values()){
            if(td.codigoFiscalModeloDocumento == codigoFiscalDocumento){
                return td;
            }
        }
        return null;
    }
}
