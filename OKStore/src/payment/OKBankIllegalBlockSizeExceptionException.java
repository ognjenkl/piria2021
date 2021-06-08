
/**
 * OKBankIllegalBlockSizeExceptionException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.7.9  Built on : Nov 16, 2018 (12:05:37 GMT)
 */

package payment;

public class OKBankIllegalBlockSizeExceptionException extends java.lang.Exception{

    private static final long serialVersionUID = 1623195158421L;
    
    private payment.OKBankStub.OKBankIllegalBlockSizeException faultMessage;

    
        public OKBankIllegalBlockSizeExceptionException() {
            super("OKBankIllegalBlockSizeExceptionException");
        }

        public OKBankIllegalBlockSizeExceptionException(java.lang.String s) {
           super(s);
        }

        public OKBankIllegalBlockSizeExceptionException(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public OKBankIllegalBlockSizeExceptionException(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(payment.OKBankStub.OKBankIllegalBlockSizeException msg){
       faultMessage = msg;
    }
    
    public payment.OKBankStub.OKBankIllegalBlockSizeException getFaultMessage(){
       return faultMessage;
    }
}
    